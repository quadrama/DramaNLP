package de.unistuttgart.ims.drama.core.cr;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.File;

import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.cleartk.ml.CleartkSequenceAnnotator;
import org.cleartk.ml.jar.DefaultSequenceDataWriterFactory;
import org.cleartk.ml.jar.DirectoryDataWriterFactory;
import org.cleartk.ml.jar.Train;
import org.cleartk.ml.mallet.MalletCrfStringOutcomeDataWriter;

import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.Option;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import de.unistuttgart.ims.drama.api.FigureMention;

public class MentionDetectionTraining {

	public static void main(String[] args) throws Exception {
		Options options = CliFactory.parseArguments(Options.class, args);

		// a reader that loads the URIs of the training files
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(XmiReader.class,
				XmiReader.PARAM_SOURCE_LOCATION, options.getTrainDirectory() + "/*.xmi", XmiReader.PARAM_LENIENT, true);

		// run the pipeline over the training corpus
		SimplePipeline.runPipeline(reader,
				createEngineDescription(TrainingAreaAnnotator.class, TrainingAreaAnnotator.PARAM_INSTANCE_CLASS,
						FigureMention.class),
				createEngineDescription(ClearTkMentionAnnotator.class, CleartkSequenceAnnotator.PARAM_IS_TRAINING, true,
						DirectoryDataWriterFactory.PARAM_OUTPUT_DIRECTORY, options.getModelDirectory(),
						DefaultSequenceDataWriterFactory.PARAM_DATA_WRITER_CLASS_NAME,
						MalletCrfStringOutcomeDataWriter.class));

		// train a Mallet CRF model on the training data
		Train.main(options.getModelDirectory());
	}

	public interface Options {

		@Option(longName = "train-dir", description = "The directory containing MASC-annotated files", defaultValue = "target/preprocessed/*.xmi")
		public File getTrainDirectory();

		@Option(longName = "model-dir", description = "The directory where the model should be written", defaultValue = "target/")
		public File getModelDirectory();
	}
}
