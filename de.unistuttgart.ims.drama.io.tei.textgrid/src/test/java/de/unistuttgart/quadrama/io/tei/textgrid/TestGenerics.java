package de.unistuttgart.quadrama.io.tei.textgrid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.unistuttgart.ims.drama.api.Act;
import de.unistuttgart.ims.drama.api.ActHeading;
import de.unistuttgart.ims.drama.api.Author;
import de.unistuttgart.ims.drama.api.Drama;
import de.unistuttgart.ims.drama.api.DramatisPersonae;
import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.ims.drama.api.Scene;
import de.unistuttgart.ims.drama.api.Speaker;

public class TestGenerics {
	public static boolean debug = false;

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
