package de.unistuttgart.ims.drama.core.ml.spred;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.ml.CleartkSequenceAnnotator;
import org.cleartk.ml.Feature;
import org.cleartk.ml.Instances;
import org.cleartk.ml.chunking.BiesoChunking;
import org.cleartk.ml.feature.extractor.CleartkExtractor;
import org.cleartk.ml.feature.extractor.CleartkExtractor.Following;
import org.cleartk.ml.feature.extractor.CleartkExtractor.Preceding;
import org.cleartk.ml.feature.extractor.CombinedExtractor1;
import org.cleartk.ml.feature.extractor.CoveredTextExtractor;
import org.cleartk.ml.feature.extractor.FeatureExtractor1;
import org.cleartk.ml.feature.function.CapitalTypeFeatureFunction;
import org.cleartk.ml.feature.function.CharacterCategoryPatternFunction;
import org.cleartk.ml.feature.function.CharacterCategoryPatternFunction.PatternType;
import org.cleartk.ml.feature.function.FeatureFunctionExtractor;
import org.cleartk.ml.feature.function.FeatureFunctionExtractor.BaseFeatures;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unistuttgart.ims.drama.core.ml.RelativeAnnotationPositionExtractor;
import de.unistuttgart.ims.drama.core.ml.api.TextLayer;

public class ClearTkStructureAnnotator extends CleartkSequenceAnnotator<String> {

	FeatureExtractor1<Token> extractor;

	CleartkExtractor<Token, Token> contextExtractor;

	BiesoChunking<Token, TextLayer> chunking;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);

		// the token feature extractor: text, char pattern (uppercase, digits,
		// etc.), and part-of-speech
		this.extractor = new CombinedExtractor1<Token>(
				new FeatureFunctionExtractor<Token>(new CoveredTextExtractor<Token>(), BaseFeatures.INCLUDE,
						new CharacterCategoryPatternFunction<Token>(PatternType.REPEATS_MERGED),
						new CapitalTypeFeatureFunction()),
				new RelativeAnnotationPositionExtractor<Token>());
		// the context feature extractor: the features above for the 3 preceding
		// and 3 following tokens
		this.contextExtractor = new CleartkExtractor<Token, Token>(Token.class, this.extractor, new Preceding(5),
				new Following(3));

		// the chunking definition: Tokens will be combined to form
		// NamedEntityMentions, with labels
		// from the "mentionType" attribute so that we get B-location, I-person,
		// etc.
		this.chunking = new BiesoChunking<Token, TextLayer>(Token.class, TextLayer.class, "Name");
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {

		// extract features for each token in the sentence
		List<Token> tokens = new LinkedList<Token>(JCasUtil.select(jCas, Token.class));
		List<List<Feature>> featureLists = new ArrayList<List<Feature>>();
		for (Token token : tokens) {
			List<Feature> features = new ArrayList<Feature>();
			features.addAll(this.extractor.extract(jCas, token));
			features.addAll(this.contextExtractor.extract(jCas, token));
			featureLists.add(features);
		}

		// during training, convert NamedEntityMentions in the CAS into
		// expected classifier outcomes
		if (this.isTraining()) {

			// extract the gold (human annotated) NamedEntityMention
			// annotations
			List<TextLayer> namedEntityMentions = new LinkedList<TextLayer>(JCasUtil.select(jCas, TextLayer.class));

			// convert the NamedEntityMention annotations into token-level
			// BIO outcome labels
			List<String> outcomes = this.chunking.createOutcomes(jCas, tokens, namedEntityMentions);

			// write the features and outcomes as training instances
			this.dataWriter.write(Instances.toInstances(outcomes, featureLists));
		}

		// during classification, convert classifier outcomes into
		// NamedEntityMentions in the CAS
		else {

			// get the predicted BIO outcome labels from the classifier
			List<String> outcomes = this.classifier.classify(featureLists);

			// create the NamedEntityMention annotations in the CAS
			this.chunking.createChunks(jCas, tokens, outcomes);
		}

	}

}
