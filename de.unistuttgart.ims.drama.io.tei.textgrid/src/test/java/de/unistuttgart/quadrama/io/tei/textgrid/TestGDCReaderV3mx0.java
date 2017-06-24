package de.unistuttgart.quadrama.io.tei.textgrid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Before;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.unistuttgart.ims.drama.api.Act;
import de.unistuttgart.ims.drama.api.ActHeading;
import de.unistuttgart.ims.drama.api.Author;
import de.unistuttgart.ims.drama.api.Drama;
import de.unistuttgart.ims.drama.api.DramatisPersonae;
import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.ims.drama.api.FrontMatter;
import de.unistuttgart.ims.drama.api.MainMatter;
import de.unistuttgart.ims.drama.api.Scene;
import de.unistuttgart.ims.drama.api.SceneHeading;
import de.unistuttgart.ims.drama.api.Speaker;

public class TestGDCReaderV3mx0 {
	CollectionReaderDescription description;
	JCas jcas;

	@Before
	public void setUp() throws ResourceInitializationException {
		description = CollectionReaderFactory.createReaderDescription(GerDraCorUrlReader.class,
				GerDraCorUrlReader.PARAM_INPUT, "src/test/resources/gerdracor/v3mx.0.xml");
		AggregateBuilder b = new AggregateBuilder();
		if (TestGenerics.debug)
			b.add(AnalysisEngineFactory.createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION,
					"target/doc"));
		jcas = SimplePipeline.iteratePipeline(description, b.createAggregateDescription()).iterator().next();
	}

	@Test
	public void testGeneral() {
		assertEquals("v3mx.0", JCasUtil.selectSingle(jcas, Drama.class).getDocumentId());

		assertTrue(JCasUtil.exists(jcas, Drama.class));
		assertTrue(JCasUtil.exists(jcas, Figure.class));
		assertTrue(JCasUtil.exists(jcas, Act.class));
		assertTrue(JCasUtil.exists(jcas, Scene.class));
		assertTrue(JCasUtil.exists(jcas, Speaker.class));
		assertTrue(JCasUtil.exists(jcas, DramatisPersonae.class));
		assertTrue(JCasUtil.exists(jcas, Author.class));

		if (JCasUtil.exists(jcas, ActHeading.class)) {
			for (Act act : JCasUtil.select(jcas, Act.class)) {
				assertEquals(1, JCasUtil.selectCovered(ActHeading.class, act).size());
			}
		}
		// check that speaker annotations are not empty
		for (Speaker speaker : JCasUtil.select(jcas, Speaker.class)) {
			assertNotEquals(speaker.getBegin(), speaker.getEnd());
		}

		assertNotNull(JCasUtil.selectSingle(jcas, FrontMatter.class));
		assertNotNull(JCasUtil.selectSingle(jcas, MainMatter.class));

	}

	@Test
	public void testActsAndScenes() {
		assertEquals(5, JCasUtil.select(jcas, Act.class).size());
		assertEquals(44, JCasUtil.select(jcas, Scene.class).size());
		assertEquals(5, JCasUtil.select(jcas, ActHeading.class).size());
		assertEquals(44, JCasUtil.select(jcas, SceneHeading.class).size());
	}

	@Test
	public void testFigures() {
		assertTrue(JCasUtil.exists(jcas, DramatisPersonae.class));
		assertTrue(JCasUtil.exists(jcas, Figure.class));
		assertTrue(JCasUtil.exists(jcas, Speaker.class));
		assertEquals(15, JCasUtil.select(jcas, Figure.class).size());
	}

}
