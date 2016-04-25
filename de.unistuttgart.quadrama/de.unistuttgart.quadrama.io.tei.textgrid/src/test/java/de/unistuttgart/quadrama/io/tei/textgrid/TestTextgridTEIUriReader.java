package de.unistuttgart.quadrama.io.tei.textgrid;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterator;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Before;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.unistuttgart.quadrama.api.Drama;
import de.unistuttgart.quadrama.api.Figure;
import de.unistuttgart.quadrama.api.Speaker;

public class TestTextgridTEIUriReader {

	CollectionReaderDescription description;

	@Before
	public void setUp() throws ResourceInitializationException {
		description =
				CollectionReaderFactory.createReaderDescription(
						TextgridTEIUrlReader.class,
						TextgridTEIUrlReader.PARAM_URL_LIST,
						"src/test/resources/urls.txt",
						TextgridTEIUrlReader.PARAM_LANGUAGE, "de");
	}

	@Test
	public void testReader() throws UIMAException, IOException {
		JCasIterator iter =
				SimplePipeline.iteratePipeline(
						description,
						AnalysisEngineFactory.createEngineDescription(
								XmiWriter.class,
								XmiWriter.PARAM_TARGET_LOCATION, "target/doc"))
								.iterator();

		JCas jcas;
		Speaker speaker;
		Figure figure;

		jcas = iter.next();
		// 1.xml
		// general sanity checking
		assertTrue(JCasUtil.exists(jcas, Drama.class));

		figure = JCasUtil.selectByIndex(jcas, Figure.class, 10);

		// speakers
		speaker = JCasUtil.selectByIndex(jcas, Speaker.class, 0);

		jcas = iter.next();
		// 2.xml
		// general sanity checking
		assertTrue(JCasUtil.exists(jcas, Drama.class));

		// figures

		figure = JCasUtil.selectByIndex(jcas, Figure.class, 0);

		figure = JCasUtil.selectByIndex(jcas, Figure.class, 10);

		jcas = iter.next();

	}
}
