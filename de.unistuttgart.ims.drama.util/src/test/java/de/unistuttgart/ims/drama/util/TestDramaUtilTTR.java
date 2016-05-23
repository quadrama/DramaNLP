package de.unistuttgart.ims.drama.util;

import static org.junit.Assert.assertEquals;

import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.factory.JCasBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class TestDramaUtilTTR {
	@Test
	public void testTTR1() throws Exception {
		JCas jcas = JCasFactory.createJCas();
		jcas.setDocumentText("b b c");
		AnnotationFactory.createAnnotation(jcas, 0, 1, Token.class);
		AnnotationFactory.createAnnotation(jcas, 2, 3, Token.class);
		AnnotationFactory.createAnnotation(jcas, 4, 5, Token.class);

		assertEquals(2.0 / 3.0, DramaUtil.ttr(JCasUtil.select(jcas, Token.class), 3, true), 1e-4);
		assertEquals(0.0, DramaUtil.ttr(JCasUtil.select(jcas, Token.class), 100, false), 1e-4);
		assertEquals(0.0, DramaUtil.ttr(JCasUtil.select(jcas, Token.class), 5, false), 1e-4);
		assertEquals(0.0, DramaUtil.ttr(JCasUtil.select(jcas, Token.class), 4, false), 1e-4);
	}

	@Test
	public void testTTR2() throws Exception {
		int tokens = 10000;
		JCasBuilder b = new JCasBuilder(JCasFactory.createJCas());
		for (int i = 0; i < tokens / 2.0; i++) {
			b.add("a", Token.class);
			b.add(" ");
			b.add("b", Token.class);
			b.add(" ");
		}
		b.close();
		JCas jcas = b.getJCas();

		assertEquals(2.0 / 3.0, DramaUtil.ttr(JCasUtil.select(jcas, Token.class), 3, true), 1e-2);
		assertEquals(0.0, DramaUtil.ttr(JCasUtil.select(jcas, Token.class), tokens + 10, false), 1e-4);
		assertEquals(0.5, DramaUtil.ttr(JCasUtil.select(jcas, Token.class), 4, false), 1e-4);
		assertEquals(1 / 3.0, DramaUtil.ttr(JCasUtil.select(jcas, Token.class), 6, false), 1e-4);
		assertEquals(0.2, DramaUtil.ttr(JCasUtil.select(jcas, Token.class), 10, false), 1e-4);

	}
}
