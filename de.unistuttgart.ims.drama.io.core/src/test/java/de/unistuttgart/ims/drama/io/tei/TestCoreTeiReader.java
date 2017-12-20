package de.unistuttgart.ims.drama.io.tei;

import static org.junit.Assert.assertTrue;

import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.unistuttgart.ims.drama.api.Drama;
import de.unistuttgart.ims.drama.io.TestGenerics;
import de.unistuttgart.quadrama.io.tei.CoreTeiReader;

public class TestCoreTeiReader {
	static CollectionReaderDescription description;
	static JCas jcas;

	@BeforeClass
	public static void setUp() throws ResourceInitializationException {
		description = CollectionReaderFactory.createReaderDescription(CoreTeiReader.class,
				CoreTeiReader.PARAM_INPUT, "src/test/resources/es/Calderon.xml", CoreTeiReader.PARAM_LANGUAGE,
				"es");
		AggregateBuilder b = new AggregateBuilder();
		if (TestGenerics.debug)
			b.add(AnalysisEngineFactory.createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION,
					"target/doc"));
		jcas = SimplePipeline.iteratePipeline(description, b.createAggregateDescription()).iterator().next();
	}

	@Test
	public void testGeneral() {
		assertTrue(JCasUtil.exists(jcas, Drama.class));
	}
}
