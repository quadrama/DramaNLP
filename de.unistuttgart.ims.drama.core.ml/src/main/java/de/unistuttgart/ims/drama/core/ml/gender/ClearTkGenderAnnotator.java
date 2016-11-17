package de.unistuttgart.ims.drama.core.ml.gender;

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
import org.cleartk.ml.feature.extractor.CombinedExtractor1;
import org.cleartk.ml.feature.extractor.CoveredTextExtractor;
import org.cleartk.ml.feature.extractor.FeatureExtractor1;
import org.cleartk.ml.feature.extractor.TypePathExtractor;
import org.cleartk.ml.feature.function.CharacterCategoryPatternFunction;
import org.cleartk.ml.feature.function.CharacterCategoryPatternFunction.PatternType;
import org.cleartk.ml.feature.function.FeatureFunctionExtractor;

import de.unistuttgart.ims.drama.api.Figure;

public class ClearTkGenderAnnotator extends CleartkAnnotator<String> {
	FeatureExtractor1<Figure> extractor;
	CleartkExtractor<Figure, Figure> contextExtractor;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		this.extractor = new CombinedExtractor1<Figure>(
				new FeatureFunctionExtractor<Figure>(new CoveredTextExtractor<Figure>(),
						new CharacterCategoryPatternFunction<Figure>(PatternType.REPEATS_MERGED)),
				new TypePathExtractor<Figure>(Figure.class, "pos/PosValue"));
		this.contextExtractor = new CleartkExtractor<Figure, Figure>(Figure.class, this.extractor, new Preceding(2),
				new Following(1));
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		for (Figure figure : JCasUtil.select(jcas, Figure.class)) {
			List<Feature> features = extractor.extract(jcas, figure);
			if (this.isTraining()) {
				this.dataWriter.write(new Instance<String>("", features));
			} else {
				String category = this.classifier.classify(features);

			}

		}

	}

}
