package de.unistuttgart.quadrama.core;

import java.io.File;
import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.unistuttgart.quadrama.io.tei.GerDraCorReader;

public class GenerateTestFiles {

	public static void main(String[] args) throws ResourceInitializationException, UIMAException, IOException {
		SimplePipeline.runPipeline(
				CollectionReaderFactory.createReaderDescription(GerDraCorReader.class, GerDraCorReader.PARAM_INPUT,
						"src/test/resources/tei/", GerDraCorReader.PARAM_REMOVE_XML_ANNOTATIONS, true),
				AnalysisEngineFactory.createEngineDescription(XmiWriter.class, XmiWriter.PARAM_USE_DOCUMENT_ID, true,
						XmiWriter.PARAM_TARGET_LOCATION, "src/test/resources/level-1/"));

		new File("src/test/resources/level-1/typesystem.xml").delete();
	}

}
