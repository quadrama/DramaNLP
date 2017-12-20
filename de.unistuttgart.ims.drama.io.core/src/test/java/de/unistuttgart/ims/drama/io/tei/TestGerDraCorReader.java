package de.unistuttgart.ims.drama.io.tei;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.unistuttgart.ims.drama.api.Act;
import de.unistuttgart.ims.drama.api.ActHeading;
import de.unistuttgart.ims.drama.api.Author;
import de.unistuttgart.ims.drama.api.CastFigure;
import de.unistuttgart.ims.drama.api.Drama;
import de.unistuttgart.ims.drama.api.DramatisPersonae;
import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.ims.drama.api.FrontMatter;
import de.unistuttgart.ims.drama.api.MainMatter;
import de.unistuttgart.ims.drama.api.Scene;
import de.unistuttgart.ims.drama.api.SceneHeading;
import de.unistuttgart.ims.drama.api.Speaker;
import de.unistuttgart.ims.drama.io.TestGenerics;
import de.unistuttgart.ims.drama.util.DramaUtil;
import de.unistuttgart.quadrama.io.tei.GerDraCorUrlReader;

public class TestGerDraCorReader {
	CollectionReaderDescription description;
	JCas jcas;

	@Test
	public void testNdtw0() throws ResourceInitializationException {
		description = CollectionReaderFactory.createReaderDescription(GerDraCorUrlReader.class,
				GerDraCorUrlReader.PARAM_INPUT, "src/test/resources/gerdracor/ndtw.0.xml");
		AggregateBuilder b = new AggregateBuilder();
		if (TestGenerics.debug)
			b.add(AnalysisEngineFactory.createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION,
					"target/doc"));
		jcas = SimplePipeline.iteratePipeline(description, b.createAggregateDescription()).iterator().next();

		TestGenerics.checkMinimalStructure(jcas);
		TestGenerics.checkMetadata(jcas);

		assertEquals("ndtw.0", JCasUtil.selectSingle(jcas, Drama.class).getDocumentId());
		assertEquals(54991, jcas.getDocumentText().length());
		assertEquals("de", jcas.getDocumentLanguage());

		assertEquals(1, JCasUtil.select(jcas, Author.class).size());
		Author a = JCasUtil.select(jcas, Author.class).iterator().next();
		assertEquals("Gessner, Salomon", a.getName());
		assertEquals("118538969", a.getPnd());

		assertTrue(JCasUtil.exists(jcas, Drama.class));
		assertFalse(JCasUtil.exists(jcas, Figure.class));
		assertTrue(JCasUtil.exists(jcas, Act.class));
		assertTrue(JCasUtil.exists(jcas, Scene.class));
		// no dramatis personae in ndtw.0
		assertFalse(JCasUtil.exists(jcas, DramatisPersonae.class));
		assertTrue(JCasUtil.exists(jcas, Author.class));

		if (JCasUtil.exists(jcas, ActHeading.class)) {
			for (Act act : JCasUtil.select(jcas, Act.class)) {
				assertEquals(1, JCasUtil.selectCovered(ActHeading.class, act).size());
			}
		}

		for (Scene segment : JCasUtil.select(jcas, Scene.class)) {
			assertEquals(1, JCasUtil.selectCovered(SceneHeading.class, segment).size());
		}

		// check that speaker annotations are not empty
		for (Speaker speaker : JCasUtil.select(jcas, Speaker.class)) {
			assertNotEquals(speaker.getBegin(), speaker.getEnd());
		}

		assertNotNull(JCasUtil.selectSingle(jcas, FrontMatter.class));
		assertNotNull(JCasUtil.selectSingle(jcas, MainMatter.class));

		assertEquals(1762, DramaUtil.getDrama(jcas).getDatePrinted());
		assertEquals(1762, DramaUtil.getDrama(jcas).getDateWritten());

		assertEquals(3, JCasUtil.select(jcas, Act.class).size());
		assertEquals(19, JCasUtil.select(jcas, Scene.class).size());
		assertEquals(3, JCasUtil.select(jcas, ActHeading.class).size());
		assertEquals(19, JCasUtil.select(jcas, SceneHeading.class).size());

		assertFalse(JCasUtil.exists(jcas, DramatisPersonae.class));
		assertFalse(JCasUtil.exists(jcas, Figure.class));
		assertTrue(JCasUtil.exists(jcas, Speaker.class));
		assertTrue(JCasUtil.exists(jcas, CastFigure.class));

		assertEquals(14, JCasUtil.select(jcas, CastFigure.class).size());
	}

	@Test
	public void testQfxf0() throws ResourceInitializationException {
		description = CollectionReaderFactory.createReaderDescription(GerDraCorUrlReader.class,
				GerDraCorUrlReader.PARAM_INPUT, "src/test/resources/gerdracor/qfxf.0.xml");
		AggregateBuilder b = new AggregateBuilder();
		if (TestGenerics.debug)
			b.add(AnalysisEngineFactory.createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION,
					"target/doc"));
		jcas = SimplePipeline.iteratePipeline(description, b.createAggregateDescription()).iterator().next();

		TestGenerics.checkMinimalStructure(jcas);
		TestGenerics.checkMetadata(jcas);
		TestGenerics.checkSanity(jcas);

		assertEquals("qfxf.0", JCasUtil.selectSingle(jcas, Drama.class).getDocumentId());
		assertEquals(122999, jcas.getDocumentText().length());
		assertEquals("de", jcas.getDocumentLanguage());

		assertEquals(1, JCasUtil.select(jcas, Author.class).size());
		Author a = JCasUtil.select(jcas, Author.class).iterator().next();
		assertEquals("Holtei, Karl von", a.getName());
		assertEquals("118706640", a.getPnd());

		if (JCasUtil.exists(jcas, ActHeading.class)) {
			for (Act act : JCasUtil.select(jcas, Act.class)) {
				assertEquals(1, JCasUtil.selectCovered(ActHeading.class, act).size());
			}
		}

		for (Scene segment : JCasUtil.select(jcas, Scene.class)) {
			assertEquals(1, JCasUtil.selectCovered(SceneHeading.class, segment).size());
		}

		// check that speaker annotations are not empty
		for (Speaker speaker : JCasUtil.select(jcas, Speaker.class)) {
			assertNotEquals(speaker.getBegin(), speaker.getEnd());
		}

		assertNotNull(JCasUtil.selectSingle(jcas, FrontMatter.class));
		assertNotNull(JCasUtil.selectSingle(jcas, MainMatter.class));

		assertEquals(1838, DramaUtil.getDrama(jcas).getDatePrinted());
		assertEquals(1832, DramaUtil.getDrama(jcas).getDatePremiere());

		assertEquals(3, JCasUtil.select(jcas, Act.class).size());
		assertEquals(40, JCasUtil.select(jcas, Scene.class).size());
		assertEquals(3, JCasUtil.select(jcas, ActHeading.class).size());
		assertEquals(40, JCasUtil.select(jcas, SceneHeading.class).size());

		assertEquals(19, JCasUtil.select(jcas, CastFigure.class).size());
	}

	@Test
	public void testRjmw0() throws ResourceInitializationException {
		description = CollectionReaderFactory.createReaderDescription(GerDraCorUrlReader.class,
				GerDraCorUrlReader.PARAM_INPUT, "src/test/resources/gerdracor/rjmw.0.xml");
		AggregateBuilder b = new AggregateBuilder();
		if (TestGenerics.debug)
			b.add(AnalysisEngineFactory.createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION,
					"target/doc"));
		jcas = SimplePipeline.iteratePipeline(description, b.createAggregateDescription()).iterator().next();

		TestGenerics.checkMinimalStructure(jcas);
		TestGenerics.checkMetadata(jcas);
		TestGenerics.checkSanity(jcas);

		assertEquals("rjmw.0", JCasUtil.selectSingle(jcas, Drama.class).getDocumentId());
		assertEquals(170841, jcas.getDocumentText().length());
		assertEquals("de", jcas.getDocumentLanguage());

		assertEquals(1, JCasUtil.select(jcas, Author.class).size());
		Author a = JCasUtil.select(jcas, Author.class).iterator().next();
		assertEquals("Lessing, Gotthold Ephraim", a.getName());
		assertEquals("118572121", a.getPnd());

		if (JCasUtil.exists(jcas, ActHeading.class)) {
			for (Act act : JCasUtil.select(jcas, Act.class)) {
				assertEquals(1, JCasUtil.selectCovered(ActHeading.class, act).size());
			}
		}

		for (Scene segment : JCasUtil.select(jcas, Scene.class)) {
			assertEquals(1, JCasUtil.selectCovered(SceneHeading.class, segment).size());
		}

		// check that speaker annotations are not empty
		for (Speaker speaker : JCasUtil.select(jcas, Speaker.class)) {
			assertNotEquals(speaker.getBegin(), speaker.getEnd());
		}

		assertNotNull(JCasUtil.selectSingle(jcas, FrontMatter.class));
		assertNotNull(JCasUtil.selectSingle(jcas, MainMatter.class));

		assertEquals(1755, DramaUtil.getDrama(jcas).getDatePrinted());
		assertEquals(1755, DramaUtil.getDrama(jcas).getDatePremiere());

		assertEquals(5, JCasUtil.select(jcas, Act.class).size());
		assertEquals(44, JCasUtil.select(jcas, Scene.class).size());
		assertEquals(5, JCasUtil.select(jcas, ActHeading.class).size());
		assertEquals(44, JCasUtil.select(jcas, SceneHeading.class).size());

		assertEquals(11, JCasUtil.select(jcas, CastFigure.class).size());
		for (CastFigure cf : JCasUtil.select(jcas, CastFigure.class)) {
			assertNotNull(cf.getNames());
			assertFalse(cf.getNames().size() == 0);
		}
	}

	@Test
	public void testR0n20() throws ResourceInitializationException {
		description = CollectionReaderFactory.createReaderDescription(GerDraCorUrlReader.class,
				GerDraCorUrlReader.PARAM_INPUT, "src/test/resources/gerdracor/r0n2.0.xml");
		AggregateBuilder b = new AggregateBuilder();
		if (TestGenerics.debug)
			b.add(AnalysisEngineFactory.createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION,
					"target/doc"));
		jcas = SimplePipeline.iteratePipeline(description, b.createAggregateDescription()).iterator().next();

		TestGenerics.checkMetadata(jcas);
		TestGenerics.checkMinimalStructure(jcas);
		assertTrue(JCasUtil.exists(jcas, Scene.class));

		assertFalse(JCasUtil.exists(jcas, Act.class));
		assertEquals(0, JCasUtil.select(jcas, Act.class).size());
		assertEquals(24, JCasUtil.select(jcas, Scene.class).size());
		assertFalse(JCasUtil.exists(jcas, ActHeading.class));
		assertEquals(24, JCasUtil.select(jcas, SceneHeading.class).size());
	}

	@Test
	public void testV3mx0() throws ResourceInitializationException {
		description = CollectionReaderFactory.createReaderDescription(GerDraCorUrlReader.class,
				GerDraCorUrlReader.PARAM_INPUT, "src/test/resources/gerdracor/v3mx.0.xml");
		AggregateBuilder b = new AggregateBuilder();
		if (TestGenerics.debug)
			b.add(AnalysisEngineFactory.createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION,
					"target/doc"));
		jcas = SimplePipeline.iteratePipeline(description, b.createAggregateDescription()).iterator().next();
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
		assertEquals(1743, JCasUtil.selectSingle(jcas, Drama.class).getDatePrinted());

		assertEquals(5, JCasUtil.select(jcas, Act.class).size());
		assertEquals(44, JCasUtil.select(jcas, Scene.class).size());
		assertEquals(5, JCasUtil.select(jcas, ActHeading.class).size());
		assertEquals(44, JCasUtil.select(jcas, SceneHeading.class).size());
		assertTrue(JCasUtil.exists(jcas, DramatisPersonae.class));
		assertTrue(JCasUtil.exists(jcas, Figure.class));
		assertTrue(JCasUtil.exists(jcas, Speaker.class));
		assertEquals(15, JCasUtil.select(jcas, Figure.class).size());

		for (CastFigure f : JCasUtil.select(jcas, CastFigure.class)) {
			assertNotNull(f);
			for (int j = 0; j < f.getNames().size(); j++) {
				assertNotEquals(" ", f.getNames(j));
			}
		}

	}

}
