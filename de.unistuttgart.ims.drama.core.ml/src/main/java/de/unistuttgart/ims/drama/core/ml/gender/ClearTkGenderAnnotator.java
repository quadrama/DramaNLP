package de.unistuttgart.ims.drama.core.ml.gender;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.ml.CleartkAnnotator;
import org.cleartk.ml.Feature;
import org.cleartk.ml.Instance;
import org.cleartk.ml.feature.extractor.CleartkExtractor;
import org.cleartk.ml.feature.extractor.CleartkExtractor.Following;
import org.cleartk.ml.feature.extractor.CleartkExtractor.Preceding;
import org.cleartk.ml.feature.extractor.CleartkExtractorException;
import org.cleartk.ml.feature.extractor.FeatureExtractor1;

import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.ims.drama.util.DramaUtil;

public class ClearTkGenderAnnotator extends CleartkAnnotator<String> {
	FeatureExtractor1<Figure> extractor;
	CleartkExtractor<Figure, Figure> contextExtractor;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		this.extractor = new FeatureExtractor1<Figure>() {
			@Override
			public List<Feature> extract(JCas view, Figure focusAnnotation) throws CleartkExtractorException {
				String text = focusAnnotation.getCoveredText();
				String[] tokens = text.split("[[:space:][:punct:]]");

				List<Feature> features = new ArrayList<Feature>();
				features.add(new Feature("numberOfTokens", tokens.length));
				int i = 0;
				for (String t : tokens) {
					features.add(new Feature("Token " + i++, t));
				}
				return features;
			}
		};
		this.contextExtractor = new CleartkExtractor<Figure, Figure>(Figure.class, this.extractor, new Preceding(2),
				new Following(1));
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		for (Figure figure : JCasUtil.select(jcas, Figure.class)) {
			List<Feature> features = extractor.extract(jcas, figure);
			if (this.isTraining()) {
				String outcome = DramaUtil.getTypeValue(jcas, figure, "Gender");
				if (outcome != null)
					this.dataWriter.write(new Instance<String>(outcome, features));
			} else {
				String category = this.classifier.classify(features);
				DramaUtil.assignFigureType(jcas, figure, "Gender", category);
			}

		}

	}

}
