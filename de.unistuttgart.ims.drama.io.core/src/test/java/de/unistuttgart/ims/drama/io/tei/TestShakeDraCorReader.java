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
import org.apache.uima.fit.pipeline.JCasIterator;
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
import de.unistuttgart.quadrama.io.tei.ShakeDraCorReader;

public class TestShakeDraCorReader {

	@Test
	public void testRomeoAndJuliet() throws ResourceInitializationException {

		CollectionReaderDescription description = CollectionReaderFactory.createReaderDescription(
				ShakeDraCorReader.class, ShakeDraCorReader.PARAM_INPUT, "src/test/resources/shakedracor/",
				ShakeDraCorReader.PARAM_LANGUAGE, "en");
		AggregateBuilder b = new AggregateBuilder();
		if (TestGenerics.debug)
			b.add(AnalysisEngineFactory.createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION,
					"target/doc"));
		JCasIterator iterator = SimplePipeline.iteratePipeline(description, b.createAggregateDescription()).iterator();
		JCas jcas = iterator.next();

		TestGenerics.checkMinimalStructure(jcas);
		TestGenerics.checkMetadata(jcas);

		assertEquals("Rom", JCasUtil.selectSingle(jcas, Drama.class).getDocumentId());
		assertEquals(202029, jcas.getDocumentText().length());
		assertEquals("en", jcas.getDocumentLanguage());

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

}
