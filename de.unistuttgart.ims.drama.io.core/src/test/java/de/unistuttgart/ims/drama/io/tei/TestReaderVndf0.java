package de.unistuttgart.ims.drama.io.tei;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
import de.unistuttgart.ims.drama.api.Drama;
import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.ims.drama.api.FrontMatter;
import de.unistuttgart.ims.drama.api.MainMatter;
import de.unistuttgart.ims.drama.api.Scene;
import de.unistuttgart.ims.drama.api.SceneHeading;
import de.unistuttgart.ims.drama.api.Speaker;
import de.unistuttgart.ims.drama.io.TestGenerics;
import de.unistuttgart.quadrama.io.tei.TextgridTEIUrlReader;

public class TestReaderVndf0 {
	static CollectionReaderDescription description;
	static JCas jcas;

	@BeforeClass
	public static void setUp() throws ResourceInitializationException {
		description = CollectionReaderFactory.createReaderDescription(TextgridTEIUrlReader.class,
				TextgridTEIUrlReader.PARAM_INPUT, "src/test/resources/textgridFiles/vndf.0.xml");
		AggregateBuilder b = new AggregateBuilder();
		if (TestGenerics.debug)
			b.add(AnalysisEngineFactory.createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION,
					"target/doc"));
		jcas = SimplePipeline.iteratePipeline(description, b.createAggregateDescription()).iterator().next();
	}

	@Test
	public void testGeneral() {
		assertEquals("vndf.0", JCasUtil.selectSingle(jcas, Drama.class).getDocumentId());

		TestGenerics.checkSanity(jcas);

		assertNotNull(JCasUtil.selectSingle(jcas, FrontMatter.class));
		assertNotNull(JCasUtil.selectSingle(jcas, MainMatter.class));

	}

	@Test
	public void testActsAndScenes() {
		assertEquals(5, JCasUtil.select(jcas, Act.class).size());
		assertEquals(24, JCasUtil.select(jcas, Scene.class).size());

		assertEquals(5, JCasUtil.select(jcas, ActHeading.class).size());
		assertEquals(24, JCasUtil.select(jcas, SceneHeading.class).size());

		Act act;
		act = JCasUtil.selectByIndex(jcas, Act.class, 0);
		assertEquals(5, JCasUtil.selectCovered(Scene.class, act).size());
		act = JCasUtil.selectByIndex(jcas, Act.class, 1);
		assertEquals(6, JCasUtil.selectCovered(Scene.class, act).size());
		act = JCasUtil.selectByIndex(jcas, Act.class, 2);
		assertEquals(5, JCasUtil.selectCovered(Scene.class, act).size());
		act = JCasUtil.selectByIndex(jcas, Act.class, 3);
		assertEquals(5, JCasUtil.selectCovered(Scene.class, act).size());
		act = JCasUtil.selectByIndex(jcas, Act.class, 4);
		assertEquals(3, JCasUtil.selectCovered(Scene.class, act).size());

	}

	@Test
	public void testFigures() {
		Figure figure;
		Speaker speaker;

		// figures
		// should be 24, but we can't identify the last lines
		assertEquals(26, JCasUtil.select(jcas, Figure.class).size());
		figure = JCasUtil.selectByIndex(jcas, Figure.class, 0);
		assertEquals("Escalus, Prinz von Verona", figure.getCoveredText());
		assertNull(figure.getDescription());

		figure = JCasUtil.selectByIndex(jcas, Figure.class, 10);
		assertEquals("Bruder Marcus, von demselben Orden", figure.getCoveredText());
		assertNull(figure.getDescription());

		// speakers
		speaker = JCasUtil.selectByIndex(jcas, Speaker.class, 0);
		assertEquals("SIMSON", speaker.getCoveredText());
	}

}
