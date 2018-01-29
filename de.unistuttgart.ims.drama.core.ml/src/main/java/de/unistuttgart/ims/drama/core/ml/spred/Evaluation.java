package de.unistuttgart.ims.drama.core.ml.spred;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterator;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.cleartk.eval.AnnotationStatistics;
import org.cleartk.eval.Evaluation_ImplBase;
import org.cleartk.ml.CleartkSequenceAnnotator;
import org.cleartk.ml.jar.DefaultSequenceDataWriterFactory;
import org.cleartk.ml.jar.DirectoryDataWriterFactory;
import org.cleartk.ml.jar.GenericJarClassifierFactory;
import org.cleartk.ml.jar.JarClassifierBuilder;
import org.cleartk.ml.jar.Train;
import org.cleartk.ml.mallet.MalletCrfStringOutcomeDataWriter;
import org.cleartk.util.ae.UriToXmiCasAnnotator;
import org.cleartk.util.cr.UriCollectionReader;

import com.google.common.base.Function;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.unistuttgart.ims.drama.core.ml.CopyView;
import de.unistuttgart.ims.drama.core.ml.api.TextLayer;
import de.unistuttgart.ims.uimautil.ClearAnnotation;
import de.unistuttgart.quadrama.core.MergeSpeechAnnotations;

public class Evaluation extends Evaluation_ImplBase<File, AnnotationStatistics<String>> {

	public Evaluation(File baseDirectory) {
		super(baseDirectory);
	}

	public static void main(String[] args) throws Exception {

		// find training files
		List<File> trainFiles = Arrays
				.asList(new File("src/main/resources/spred/devel/xmi").listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return name.endsWith(".xmi");
					}
				}));

		System.err.println(trainFiles);

		// run cross validation
		Evaluation evaluator = new Evaluation(new File("target"));
		List<AnnotationStatistics<String>> foldStats = evaluator.crossValidation(trainFiles, 5);
		AnnotationStatistics<String> crossValidationStats = AnnotationStatistics.addAll(foldStats);

		System.err.println("Cross Validation Results:");
		System.err.print(crossValidationStats);
		System.err.println();
		System.err.println(crossValidationStats.confusions());
		System.err.println();

		// train and save a model using all the data
		// evaluator.trainAndTest(trainFiles, Collections.<File>emptyList());
	}

	@Override
	protected CollectionReader getCollectionReader(List<File> files) throws Exception {

		return CollectionReaderFactory.createReader(UriCollectionReader.getDescriptionFromFiles(files));
	}

	@Override
	public void train(CollectionReader collectionReader, File outputDirectory) throws Exception {
		// assemble the training pipeline
		AggregateBuilder aggregate = new AggregateBuilder();

		aggregate.add(UriToXmiCasAnnotator.getDescription());

		aggregate.add(AnalysisEngineFactory.createEngineDescription(MergeSpeechAnnotations.class));

		// our NamedEntityChunker annotator, configured to write Mallet CRF
		// training data
		aggregate.add(AnalysisEngineFactory.createEngineDescription(ClearTkStructureAnnotator.class,
				CleartkSequenceAnnotator.PARAM_IS_TRAINING, true, DirectoryDataWriterFactory.PARAM_OUTPUT_DIRECTORY,
				outputDirectory, DefaultSequenceDataWriterFactory.PARAM_DATA_WRITER_CLASS_NAME,
				MalletCrfStringOutcomeDataWriter.class));

		// run the pipeline over the training corpus
		SimplePipeline.runPipeline(collectionReader, aggregate.createAggregateDescription());

		// quiet Mallet down a bit (but still leave likelihoods so you can see
		// progress)
		Logger malletLogger = Logger.getLogger("cc.mallet");
		malletLogger.setLevel(Level.WARNING);
		Logger likelihoodLogger = Logger.getLogger("cc.mallet.fst.CRFOptimizableByLabelLikelihood");
		likelihoodLogger.setLevel(Level.INFO);

		// train a Mallet CRF model on the training data
		Train.main(outputDirectory);

	}

	@Override
	protected AnnotationStatistics<String> test(CollectionReader collectionReader, File modelDirectory)
			throws Exception {

		final String defaultViewName = CAS.NAME_DEFAULT_SOFA;
		final String goldViewName = "GoldView";

		// define the pipeline
		AggregateBuilder aggregate = new AggregateBuilder();

		aggregate.add(UriToXmiCasAnnotator.getDescription());
		aggregate.add(AnalysisEngineFactory.createEngineDescription(MergeSpeechAnnotations.class));

		// Annotators processing the gold view:
		// * create the gold view
		// * load the text
		// * load the MASC annotations
		aggregate.add(AnalysisEngineFactory.createEngineDescription(CopyView.class,
				CopyView.PARAM_DESTINATION_VIEW_NAME, goldViewName, CopyView.PARAM_SOURCE_VIEW_NAME, defaultViewName));

		// Annotators processing the default (system) view:
		// * load the text
		// * parse sentences, tokens, part-of-speech tags
		// * run the named entity chunker
		aggregate.add(AnalysisEngineFactory.createEngineDescription(ClearAnnotation.class, ClearAnnotation.PARAM_TYPE,
				TextLayer.class));
		aggregate.add(AnalysisEngineFactory.createEngineDescription(ClearTkStructureAnnotator.class,
				CleartkSequenceAnnotator.PARAM_IS_TRAINING, false,
				GenericJarClassifierFactory.PARAM_CLASSIFIER_JAR_PATH,
				JarClassifierBuilder.getModelJarFile(modelDirectory)));
		aggregate.add(AnalysisEngineFactory.createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION,
				"target/"));

		// prepare the evaluation statistics
		AnnotationStatistics<String> stats = new AnnotationStatistics<String>();
		Function<TextLayer, ?> getSpan = AnnotationStatistics.annotationToSpan();
		Function<TextLayer, String> getCategory = AnnotationStatistics.annotationToFeatureValue("Name");

		// iterate over each JCas to be evaluated
		JCasIterator iter = new JCasIterator(collectionReader, aggregate.createAggregate());
		while (iter.hasNext()) {
			JCas jCas = iter.next();
			JCas goldView = jCas.getView(goldViewName);
			JCas systemView = jCas.getView(defaultViewName);

			// extract the named entity mentions from both gold and system views
			Collection<TextLayer> goldMentions, systemMentions;
			goldMentions = JCasUtil.select(goldView, TextLayer.class);
			systemMentions = JCasUtil.select(systemView, TextLayer.class);

			// compare the system mentions to the gold mentions
			stats.add(goldMentions, systemMentions, getSpan, getCategory);
		}

		return stats;
	}
}
