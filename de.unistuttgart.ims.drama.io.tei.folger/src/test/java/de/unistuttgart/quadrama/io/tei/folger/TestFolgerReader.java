package de.unistuttgart.quadrama.io.tei.folger;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;

public class TestFolgerReader {

	CollectionReaderDescription description;

	public void setUp() throws ResourceInitializationException {
		description = CollectionReaderFactory.createReaderDescription(FolgerReader.class, FolgerReader.PARAM_INPUT,
				"src/test/resources/");
	}

	public void testReader() throws UIMAException, IOException {
		JCas jcas = SimplePipeline.iteratePipeline(description,

				AnalysisEngineFactory.createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION,
						"target/"))
				.iterator().next();

		// assertNotNull(JCasUtil.selectSingle(jcas, Drama.class));
	}
}
