package de.unistuttgart.quadrama.io.gutenbergde;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Before;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.unistuttgart.quadrama.api.Act;

public class TestGutenbergDEReader {

	CollectionReaderDescription description;

	@Before
	public void setUp() throws ResourceInitializationException {
		description =
				CollectionReaderFactory.createReaderDescription(
						GutenbergDEReader.class,
						GutenbergDEReader.PARAM_INPUT_DIRECTORY,
						"src/test/resources/test1");
	}

	@Test
	public void testReader() throws UIMAException, IOException {
		JCas jcas =
				SimplePipeline
				.iteratePipeline(
						description,
						AnalysisEngineFactory.createEngineDescription(
								XmiWriter.class,
								XmiWriter.PARAM_TARGET_LOCATION,
								"target/doc")).iterator().next();

		assertTrue(JCasUtil.exists(jcas, Act.class));
	}
}
