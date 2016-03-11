package de.unistuttgart.quadrama.experiments;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReaderDescription;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.pipeline.SimplePipeline;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.unistuttgart.quadrama.core.SetDramaMetaData;
import de.unistuttgart.quadrama.io.tei.textgrid.TextgridTEIReader;

public class Freischuetz {

	public static void main(String[] args) throws UIMAException, IOException {
		System.setProperty("java.util.logging.config.file",
				"src/main/resources/logging.properties");

		SimplePipeline.runPipeline(
				createReaderDescription(TextgridTEIReader.class,
						TextgridTEIReader.PARAM_INPUT_DIRECTORY,
						"src/main/resources/raw/freischuetz/",
						TextgridTEIReader.PARAM_LANGUAGE, "de"),
				createEngineDescription(SetDramaMetaData.class,
						SetDramaMetaData.PARAM_DRAMAID, "Freischuetz"),
				createEngineDescription(XmiWriter.class,
						XmiWriter.PARAM_TARGET_LOCATION,
										"src/main/resources/freischuetz/"));
	}

}
