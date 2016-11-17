package de.unistuttgart.ims.drama.core.ml.gender;

import java.io.File;

import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.cleartk.ml.jar.DefaultDataWriterFactory;
import org.cleartk.ml.jar.DirectoryDataWriterFactory;
import org.cleartk.ml.jar.Train;
import org.cleartk.ml.libsvm.LibSvmStringOutcomeDataWriter;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;

public class Training {

	public static void main(String[] args) throws Exception {
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(XmiReader.class,
				XmiReader.PARAM_SOURCE_LOCATION, "src/main/resources/gender/training/*.xmi", XmiReader.PARAM_LENIENT,
				true);

		AggregateBuilder b = new AggregateBuilder();

		b.add(AnalysisEngineFactory.createEngineDescription(ClearTkGenderAnnotator.class,
				DefaultDataWriterFactory.PARAM_DATA_WRITER_CLASS_NAME, LibSvmStringOutcomeDataWriter.class,
				DirectoryDataWriterFactory.PARAM_OUTPUT_DIRECTORY, "target/models"));

		SimplePipeline.runPipeline(reader, b.createAggregateDescription());

		Train.main(new File("target/models"), new String[] { "-t", "0" });
	}

}
