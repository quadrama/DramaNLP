package de.unistuttgart.ims.drama.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.unistuttgart.ims.drama.api.Act;
import de.unistuttgart.ims.drama.api.ActHeading;
import de.unistuttgart.ims.drama.api.Author;
import de.unistuttgart.ims.drama.api.CastFigure;
import de.unistuttgart.ims.drama.api.Drama;
import de.unistuttgart.ims.drama.api.DramatisPersonae;
import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.ims.drama.api.Person;
import de.unistuttgart.ims.drama.api.Scene;
import de.unistuttgart.ims.drama.api.Speaker;
import de.unistuttgart.ims.drama.api.Speech;
import de.unistuttgart.ims.drama.api.Utterance;

public class TestGenerics {
	public static boolean debug = true;

	public static void checkMinimalStructure(JCas jcas) {
		assertTrue(JCasUtil.exists(jcas, Speaker.class));
		assertTrue(JCasUtil.exists(jcas, Utterance.class));
		assertTrue(JCasUtil.exists(jcas, Speech.class));

		for (Speaker speaker : JCasUtil.select(jcas, Speaker.class)) {
			assertNotNull(speaker.getCastFigure());
			assertFalse(speaker.getCastFigure().size() == 0);
			assertNotNull(speaker.getCoveredText(), speaker.getCastFigure(0));
		}

		for (CastFigure cf : JCasUtil.select(jcas, CastFigure.class)) {
			assertNotNull(cf.getXmlId());
			assertNotNull(cf.getNames());
			assertNotNull(cf.getDisplayName());
		}

	}

	public static void checkMetadata(JCas jcas) {
		assertTrue(JCasUtil.exists(jcas, Drama.class));
		Drama d = JCasUtil.selectSingle(jcas, Drama.class);
		assertNotNull(d.getDocumentId());
		assertNotNull(d.getDocumentTitle());

		assertTrue(JCasUtil.exists(jcas, Author.class));
		for (Person a : JCasUtil.select(jcas, Person.class)) {
			assertNotNull(a.getName());
		}
	}

	public static void checkSanity(JCas jcas) {
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

		// check that figure annotations are not empty
		for (Figure figure : JCasUtil.select(jcas, Figure.class)) {
			assertNotEquals(figure.getBegin(), figure.getEnd());
		}
	}
}
