package de.unistuttgart.quadrama.experiments;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReaderDescription;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.pipeline.SimplePipeline;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.unistuttgart.quadrama.core.SetDramaMetaData;
import de.unistuttgart.quadrama.io.gutenbergde.GutenbergDEReader;
import de.unistuttgart.quadrama.io.tei.textgrid.TextgridTEIReader;

public class CollectDocuments {

	public static void main(String[] args) throws UIMAException, IOException {
		System.setProperty("java.util.logging.config.file",
				"src/main/resources/logging.properties");
		System.err.println("Collecting Wieland ...");
		SimplePipeline.runPipeline(
				createReaderDescription(GutenbergDEReader.class,
						GutenbergDEReader.PARAM_INPUT_DIRECTORY,
						"src/main/resources/raw/Wieland/"),
				createEngineDescription(SetDramaMetaData.class,
						SetDramaMetaData.PARAM_DRAMAID, "RuJ-Wieland"),
				createEngineDescription(XmiWriter.class,
						XmiWriter.PARAM_TARGET_LOCATION,
						"src/main/resources/romeo-and-juliet/"));

		System.err.println("Collecting Schlegel ...");
		SimplePipeline.runPipeline(
				createReaderDescription(TextgridTEIReader.class,
						TextgridTEIReader.PARAM_INPUT_DIRECTORY,
						"src/main/resources/raw/Schlegel/"),
				createEngineDescription(SetDramaMetaData.class,
						SetDramaMetaData.PARAM_DRAMAID, "RuJ-Schlegel"),
				createEngineDescription(XmiWriter.class,
						XmiWriter.PARAM_TARGET_LOCATION,
										"src/main/resources/romeo-and-juliet/"));

		System.err.println("Collecting Shakespeare ...");
		SimplePipeline.runPipeline(
				createReaderDescription(TextgridTEIReader.class,
						TextgridTEIReader.PARAM_INPUT_DIRECTORY,
						"src/main/resources/raw/Shakespeare/",
						TextgridTEIReader.PARAM_LANGUAGE, "en"),
				createEngineDescription(SetDramaMetaData.class,
						SetDramaMetaData.PARAM_DRAMAID, "RuJ-Shakespeare"),
				createEngineDescription(XmiWriter.class,
						XmiWriter.PARAM_TARGET_LOCATION,
										"src/main/resources/romeo-and-juliet/"));
	}

}
