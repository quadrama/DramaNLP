package de.unistuttgart.ims.drama.core.cr;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.File;
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

import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.Option;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.unistuttgart.ims.drama.api.FigureMention;
import de.unistuttgart.ims.drama.api.Speech;
import de.unistuttgart.ims.entitydetection.api.TrainingArea;
import de.unistuttgart.ims.uimautil.ClearAnnotation;
import de.unistuttgart.ims.uimautil.ContextWindowAnnotator;

public class MentionDetectionEvaluation extends Evaluation_ImplBase<File, AnnotationStatistics<String>> {

	public interface Options {
		@Option(longName = "train-dir", description = "Specify the directory containing the training documents.  This is used for cross-validation and for training in a holdout set evaluator. "
				+ "When we run this example we point to a directory containing training data from the MASC-1.0.3 corpus - i.e. a directory called 'MASC-1.0.3/data/written'", defaultValue = "src/main/resources/training")
		public File getTrainDirectory();

		@Option(longName = "models-dir", description = "specify the directory in which to write out the trained model files", defaultValue = "target/models")
		public File getModelsDirectory();
	}

	public static void main(String[] args) throws Exception {
		Options options = CliFactory.parseArguments(Options.class, args);

		// find training files
		List<File> trainFiles = Arrays.asList(options.getTrainDirectory().listFiles());

		System.err.println(trainFiles.toString());
		MentionDetectionEvaluation evaluator = new MentionDetectionEvaluation(options.getModelsDirectory());
		AnnotationStatistics<String> crossValidationStats = evaluator.trainAndTest(trainFiles, trainFiles);// AnnotationStatistics.addAll(foldStats);

		System.err.println("Cross Validation Results:");
		System.err.print(crossValidationStats);
		System.err.println();
		System.err.println(crossValidationStats.confusions());
		System.err.println();

	}

	public MentionDetectionEvaluation(File baseDirectory) {
		super(baseDirectory);
	}

	@Override
	protected CollectionReader getCollectionReader(List<File> files) throws Exception {
		return CollectionReaderFactory.createReader(XmiReader.class, XmiReader.PARAM_SOURCE_LOCATION,
				files.get(0).getParent() + "/*.xmi");
	}

	@Override
	public void train(CollectionReader collectionReader, File outputDirectory) throws Exception {
		// assemble the training pipeline
		AggregateBuilder aggregate = new AggregateBuilder();

		aggregate
				.add(createEngineDescription(ContextWindowAnnotator.class, ContextWindowAnnotator.PARAM_BASE_ANNOTATION,
						FigureMention.class, ContextWindowAnnotator.PARAM_CONTEXT_CLASS, Speech.class,
						ContextWindowAnnotator.PARAM_TARGET_ANNOTATION, TrainingArea.class));
		// our NamedEntityChunker annotator, configured to write Mallet CRF
		// training data
		aggregate.add(AnalysisEngineFactory.createEngineDescription(ClearTkMentionAnnotator.class,
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

		// Annotators processing the gold view:
		// * create the gold view
		// * load the text
		// * load the MASC annotations
		aggregate.add(AnalysisEngineFactory.createEngineDescription(PrepareEvaluation.class,
				PrepareEvaluation.PARAM_GOLD_VIEW_NAME, goldViewName));

		// Annotators processing the default (system) view:
		// * load the text
		// * parse sentences, tokens, part-of-speech tags
		// * run the named entity chunker
		aggregate.add(AnalysisEngineFactory.createEngineDescription(ClearTkMentionAnnotator.class,
				CleartkSequenceAnnotator.PARAM_IS_TRAINING, false,
				GenericJarClassifierFactory.PARAM_CLASSIFIER_JAR_PATH,
				JarClassifierBuilder.getModelJarFile(modelDirectory)));
		aggregate.add(AnalysisEngineFactory.createEngineDescription(ClearAnnotation.class, ClearAnnotation.PARAM_TYPE,
				TrainingArea.class));
		aggregate.add(AnalysisEngineFactory.createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION,
				"target/debug/", XmiWriter.PARAM_USE_DOCUMENT_ID, true));

		// prepare the evaluation statistics
		AnnotationStatistics<String> stats = new AnnotationStatistics<String>();
		// Function<FigureMention, ?> getSpan =
		// AnnotationStatistics.annotationToSpan();

		// iterate over each JCas to be evaluated
		JCasIterator iter = new JCasIterator(collectionReader, aggregate.createAggregate());
		while (iter.hasNext()) {
			JCas jCas = iter.next();
			JCas goldView = jCas.getView(goldViewName);
			JCas systemView = jCas.getView(defaultViewName);

			// extract the named entity mentions from both gold and system views
			Collection<FigureMention> goldMentions, systemMentions;
			goldMentions = JCasUtil.select(goldView, FigureMention.class);
			systemMentions = JCasUtil.select(systemView, FigureMention.class);

			// compare the system mentions to the gold mentions
			stats.add(goldMentions, systemMentions);
		}

		return stats;
	}
}
