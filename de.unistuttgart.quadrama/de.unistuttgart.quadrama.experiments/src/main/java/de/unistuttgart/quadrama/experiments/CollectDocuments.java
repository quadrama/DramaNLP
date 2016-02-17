package de.unistuttgart.quadrama.experiments;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReaderDescription;

import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.unistuttgart.ims.uimautil.SetDocumentId;
import de.unistuttgart.quadrama.io.gutenbergde.GutenbergDEReader;
import de.unistuttgart.quadrama.io.textgridtei.TextgridTEIReader;

public class CollectDocuments {

	public static void main(String[] args)
			throws ResourceInitializationException {

		System.err.println("Collecting Wieland ...");
		CollectionReaderDescription description =
				createReaderDescription(GutenbergDEReader.class,
						GutenbergDEReader.PARAM_INPUT_DIRECTORY,
						"src/main/resources/raw/Wieland");
		SimplePipeline.iteratePipeline(
				description,
				createEngineDescription(SetDocumentId.class,
						SetDocumentId.PARAM_DOCUMENT_ID, "Wieland"),
				createEngineDescription(XmiWriter.class,
						XmiWriter.PARAM_TARGET_LOCATION,
						"src/main/resources/romeo-and-juliet/de"));

		System.err.println("Collecting Schlegel ...");
		SimplePipeline.iteratePipeline(
				createReaderDescription(TextgridTEIReader.class,
						TextgridTEIReader.PARAM_INPUT_DIRECTORY,
						"src/main/resources/raw/Schlegel"),
				createEngineDescription(SetDocumentId.class,
						SetDocumentId.PARAM_DOCUMENT_ID, "Schlegel"),
				createEngineDescription(XmiWriter.class,
						XmiWriter.PARAM_TARGET_LOCATION,
										"src/main/resources/romeo-and-juliet/de"));
	}

}
