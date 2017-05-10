package de.unistuttgart.ims.drama.core.ml.gender;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.pipeline.JCasIterator;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.cleartk.eval.AnnotationStatistics;
import org.cleartk.ml.CleartkSequenceAnnotator;
import org.cleartk.ml.jar.DefaultDataWriterFactory;
import org.cleartk.ml.jar.DirectoryDataWriterFactory;
import org.cleartk.ml.jar.GenericJarClassifierFactory;
import org.cleartk.ml.jar.JarClassifierBuilder;
import org.cleartk.ml.jar.Train;
import org.cleartk.ml.libsvm.LibSvmStringOutcomeDataWriter;

import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.Option;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.unistuttgart.ims.drama.api.DramatisPersonae;
import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.ims.drama.core.ml.AbstractEvaluation;
import de.unistuttgart.ims.drama.core.ml.ClearTkUtil;
import de.unistuttgart.ims.drama.core.ml.PrepareClearTk;

public class Evaluation extends AbstractEvaluation {

	public Evaluation(File baseDirectory) {
		super(ClearTkGenderAnnotator.class, baseDirectory);
	}

	public interface Options {
		@Option(longName = "train-dir", description = "Specify the directory containing the training documents.  This is used for cross-validation and for training in a holdout set evaluator. "
				+ "When we run this example we point to a directory containing training data from the MASC-1.0.3 corpus - i.e. a directory called 'MASC-1.0.3/data/written'", defaultValue = "src/main/resources/gender/training")
		public File getTrainDirectory();

		@Option(longName = "models-dir", description = "specify the directory in which to write out the trained model files", defaultValue = "target/models")
		public File getModelsDirectory();

	}

	public static void main(String[] args) throws Exception {
		Options options = CliFactory.parseArguments(Options.class, args);

		// find training files
		List<File> allFiles = Arrays.asList(options.getTrainDirectory().listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith("xmi");
			}
		}));

		AbstractEvaluation evaluator = new Evaluation(options.getModelsDirectory());
		List<AnnotationStatistics<String>> crossValidationStatsList = evaluator.crossValidation(allFiles, 5); // .trainAndTest(trainFiles,
		AnnotationStatistics<String> crossValidationStats = AnnotationStatistics.addAll(crossValidationStatsList);

		System.out.println(crossValidationStats);
		System.out.println(ClearTkUtil.toCmdLine(crossValidationStats.confusions()));
	}

	@Override
	protected void train(CollectionReader collectionReader, File directory) throws Exception {
		String tmpView = "DP";

		AggregateBuilder b = new AggregateBuilder();

		b.add(AnalysisEngineFactory.createEngineDescription(PrepareClearTk.class, PrepareClearTk.PARAM_VIEW_NAME,
				tmpView, PrepareClearTk.PARAM_ANNOTATION_TYPE, DramatisPersonae.class,
				PrepareClearTk.PARAM_SUBANNOTATIONS, Arrays.asList(Figure.class)));
		b.add(AnalysisEngineFactory.createEngineDescription(BreakIteratorSegmenter.class), CAS.NAME_DEFAULT_SOFA,
				tmpView);
		b.add(AnalysisEngineFactory.createEngineDescription(ClearTkGenderAnnotator.class,
				DefaultDataWriterFactory.PARAM_DATA_WRITER_CLASS_NAME, LibSvmStringOutcomeDataWriter.class,
				DirectoryDataWriterFactory.PARAM_OUTPUT_DIRECTORY, directory), CAS.NAME_DEFAULT_SOFA, tmpView);
		b.add(AnalysisEngineFactory.createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION,
				"target/xmi"));

		SimplePipeline.runPipeline(collectionReader, b.createAggregate());

		Train.main(directory, new String[] { "-t", "0" });
	}

	@Override
	protected AnnotationStatistics<String> test(CollectionReader collectionReader, File modelDirectory)
			throws Exception {

		final String silverViewName = "DP";
		final String goldViewName = "GoldView";

		// define the pipeline
		AggregateBuilder aggregate = new AggregateBuilder();

		// Annotators processing the gold view:
		// * create the gold view
		// * load the text
		// * load the MASC annotations
		aggregate.add(AnalysisEngineFactory.createEngineDescription(PrepareClearTk.class,
				PrepareClearTk.PARAM_VIEW_NAME, goldViewName, PrepareClearTk.PARAM_ANNOTATION_TYPE,
				DramatisPersonae.class, PrepareClearTk.PARAM_SUBANNOTATIONS, Arrays.asList(Figure.class)));

		// Annotators processing the default (system) view:
		// * load the text
		// * parse sentences, tokens, part-of-speech tags
		// * run the named entity chunker
		aggregate.add(AnalysisEngineFactory.createEngineDescription(PrepareClearTk.class,
				PrepareClearTk.PARAM_VIEW_NAME, silverViewName, PrepareClearTk.PARAM_ANNOTATION_TYPE,
				DramatisPersonae.class, PrepareClearTk.PARAM_SUBANNOTATIONS, Arrays.asList(Figure.class)));
		aggregate.add(AnalysisEngineFactory.createEngineDescription(BreakIteratorSegmenter.class,
				BreakIteratorSegmenter.PARAM_WRITE_SENTENCE, false), CAS.NAME_DEFAULT_SOFA, silverViewName);
		aggregate.add(
				AnalysisEngineFactory.createEngineDescription(tagger, CleartkSequenceAnnotator.PARAM_IS_TRAINING, false,
						GenericJarClassifierFactory.PARAM_CLASSIFIER_JAR_PATH,
						JarClassifierBuilder.getModelJarFile(modelDirectory)),
				CAS.NAME_DEFAULT_SOFA, silverViewName);
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
			JCas systemView = jCas.getView(silverViewName);

			// extract the named entity mentions from both gold and system views
			Collection<Figure> goldMentions, systemMentions;
			goldMentions = JCasUtil.select(goldView, Figure.class);
			systemMentions = JCasUtil.select(systemView, Figure.class);

			// compare the system mentions to the gold mentions
			stats.add(goldMentions, systemMentions, AnnotationStatistics.annotationToSpan(),
					AnnotationStatistics.annotationToFeatureValue("Gender"));
		}

		return stats;
	}

}
