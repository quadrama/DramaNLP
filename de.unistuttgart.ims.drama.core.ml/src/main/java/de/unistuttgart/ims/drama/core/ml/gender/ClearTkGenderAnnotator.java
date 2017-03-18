package de.unistuttgart.ims.drama.core.ml.gender;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
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
import org.cleartk.ml.jar.GenericJarClassifierFactory;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.unistuttgart.ims.cleartkutil.ListFeatureExtractor;
import de.unistuttgart.ims.cleartkutil.SuffixFeatureExtractor;
import de.unistuttgart.ims.drama.api.DramatisPersonae;
import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.ims.drama.api.FigureType;
import de.unistuttgart.ims.drama.core.ml.MapBackFeature;
import de.unistuttgart.ims.drama.core.ml.PrepareClearTk;

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
				return Arrays.asList(new Feature("Length", focusAnnotation.getEnd() - focusAnnotation.getBegin()));
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

		try {
			this.tokenExtractor = new CombinedExtractor1<Token>(new CoveredTextExtractor<Token>(),
					new ListFeatureExtractor<Token>("male_first_names", maleFirstNames),
					new ListFeatureExtractor<Token>("female_first_name", femaleFirstNames),
					new ListFeatureExtractor<Token>("male_titles", maleTitles),
					new ListFeatureExtractor<Token>("female_titles", femaleTitles),
					new ListFeatureExtractor<Token>("numerals",
							IOUtils.readLines(this.getClass().getResourceAsStream("/gender/numbers.csv"), "UTF-8")),
					new SuffixFeatureExtractor<Token>("in"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		for (Figure figure : JCasUtil.select(jcas, Figure.class)) {
			List<Feature> features = extractor.extract(jcas, figure);
			features.addAll(contextExtractor.extract(jcas, figure, null, Token.class, tokenExtractor));
			if (this.isTraining()) {
				String outcome = figure.getGender();
				if (outcome != null)
					this.dataWriter.write(new Instance<String>(outcome, features));
			} else {
				String category = this.classifier.classify(features);
				figure.setGender(category);
			}

		}

	}

	public static AnalysisEngineDescription getEngineDescription(String genderModelUrl)
			throws ResourceInitializationException {
		String tmpView = "tmp:Dramatis Personae";

		AggregateBuilder b = new AggregateBuilder();

		b.add(AnalysisEngineFactory.createEngineDescription(PrepareClearTk.class, PrepareClearTk.PARAM_VIEW_NAME,
				tmpView, PrepareClearTk.PARAM_ANNOTATION_TYPE, DramatisPersonae.class,
				PrepareClearTk.PARAM_SUBANNOTATIONS, Arrays.asList(Figure.class, FigureType.class)));
		b.add(AnalysisEngineFactory.createEngineDescription(BreakIteratorSegmenter.class,
				BreakIteratorSegmenter.PARAM_WRITE_SENTENCE, false), CAS.NAME_DEFAULT_SOFA, tmpView);
		b.add(AnalysisEngineFactory.createEngineDescription(ClearTkGenderAnnotator.class,
				GenericJarClassifierFactory.PARAM_CLASSIFIER_JAR_PATH, genderModelUrl), CAS.NAME_DEFAULT_SOFA, tmpView);
		b.add(AnalysisEngineFactory.createEngineDescription(MapBackFeature.class, MapBackFeature.PARAM_ANNOTATION_TYPE,
				Figure.class, MapBackFeature.PARAM_VIEW_NAME, tmpView, MapBackFeature.PARAM_FEATURE_NAME, "Gender"));

		return b.createAggregateDescription();
	}

}
