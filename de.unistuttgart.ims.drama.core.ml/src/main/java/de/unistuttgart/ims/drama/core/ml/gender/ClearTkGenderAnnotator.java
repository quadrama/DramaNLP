package de.unistuttgart.ims.drama.core.ml.gender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.ml.CleartkAnnotator;
import org.cleartk.ml.Feature;
import org.cleartk.ml.Instance;
import org.cleartk.ml.feature.extractor.CleartkExtractor;
import org.cleartk.ml.feature.extractor.CleartkExtractorException;
import org.cleartk.ml.feature.extractor.CombinedExtractor1;
import org.cleartk.ml.feature.extractor.CoveredTextExtractor;
import org.cleartk.ml.feature.extractor.FeatureExtractor1;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.ims.drama.util.DramaUtil;

public class ClearTkGenderAnnotator extends CleartkAnnotator<String> {

	FeatureExtractor1<Figure> extractor;
	CleartkExtractor.Context contextExtractor;
	FeatureExtractor1<Token> tokenExtractor;

	List<String> maleFirstNames;
	List<String> femaleFirstNames;
	List<String> maleTitles;
	List<String> femaleTitles;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		extractor = new CombinedExtractor1<Figure>(new CoveredTextExtractor<Figure>(), new FeatureExtractor1<Figure>() {
			@Override
			public List<Feature> extract(JCas view, Figure focusAnnotation) throws CleartkExtractorException {
				return new ArrayList<Feature>();
			}
		});
		try {
			maleFirstNames = IOUtils.readLines(this.getClass().getResourceAsStream("/gender/m.csv"), "UTF-8");
			femaleFirstNames = IOUtils.readLines(this.getClass().getResourceAsStream("/gender/f.csv"), "UTF-8");
			maleTitles = IOUtils.readLines(this.getClass().getResourceAsStream("/gender/m.titles.csv"), "UTF-8");
			femaleTitles = IOUtils.readLines(this.getClass().getResourceAsStream("/gender/f.titles.csv"), "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.contextExtractor = new CleartkExtractor.Covered();

		this.tokenExtractor = new CombinedExtractor1<Token>(new CoveredTextExtractor<Token>(),
				new ListFeatureExtractor("male first names", maleFirstNames),
				new ListFeatureExtractor("female first name", femaleFirstNames),
				new ListFeatureExtractor("male titles", maleTitles),
				new ListFeatureExtractor("female titles", femaleTitles));

	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		for (Figure figure : JCasUtil.select(jcas, Figure.class)) {
			List<Feature> features = extractor.extract(jcas, figure);
			features.addAll(contextExtractor.extract(jcas, figure, null, Token.class, tokenExtractor));
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

	class ListFeatureExtractor implements FeatureExtractor1<Token> {

		List<String> strList;
		String fName;

		public ListFeatureExtractor(String featureName, List<String> list) {
			strList = list;
			fName = featureName;
		}

		@Override
		public List<Feature> extract(JCas view, Token focusAnnotation) throws CleartkExtractorException {
			return Arrays.asList(new Feature(fName, strList.contains(focusAnnotation.getCoveredText().toLowerCase())));
		}

	}

}
