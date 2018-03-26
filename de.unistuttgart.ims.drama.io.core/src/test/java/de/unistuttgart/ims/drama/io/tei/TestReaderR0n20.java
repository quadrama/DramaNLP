package de.unistuttgart.ims.drama.io.tei;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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
import de.unistuttgart.ims.drama.api.Act;
import de.unistuttgart.ims.drama.api.ActHeading;
import de.unistuttgart.ims.drama.api.Scene;
import de.unistuttgart.ims.drama.api.SceneHeading;
import de.unistuttgart.ims.drama.io.TestGenerics;
import de.unistuttgart.quadrama.io.tei.TextgridTEIUrlReader;

public class TestReaderR0n20 {
	static CollectionReaderDescription description;
	static JCas jcas;

	@BeforeClass
	public static void setUp() throws ResourceInitializationException {
		description = CollectionReaderFactory.createReaderDescription(TextgridTEIUrlReader.class,
				TextgridTEIUrlReader.PARAM_INPUT, "src/test/resources/textgridFiles/r0n2.0.xml",
				TextgridTEIUrlReader.PARAM_STRICT, true);
		AggregateBuilder b = new AggregateBuilder();
		if (TestGenerics.debug)
			b.add(AnalysisEngineFactory.createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION,
					"target/doc"));
		jcas = SimplePipeline.iteratePipeline(description, b.createAggregateDescription()).iterator().next();
	}

	@Test
	public void testActsAndScenes() {
		assertFalse(JCasUtil.exists(jcas, Act.class));
		assertEquals(0, JCasUtil.select(jcas, Act.class).size());
		assertEquals(24, JCasUtil.select(jcas, Scene.class).size());
		assertFalse(JCasUtil.exists(jcas, ActHeading.class));
		assertEquals(24, JCasUtil.select(jcas, SceneHeading.class).size());
	}

}
