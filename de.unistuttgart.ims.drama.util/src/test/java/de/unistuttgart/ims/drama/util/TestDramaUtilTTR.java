package de.unistuttgart.ims.drama.util;

import static org.junit.Assert.assertEquals;

import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class TestDramaUtilTTR {
	@Test
	public void testTTR() throws Exception {
		JCas jcas = JCasFactory.createJCas();
		jcas.setDocumentText("b b c");
		AnnotationFactory.createAnnotation(jcas, 0, 1, Token.class);
		AnnotationFactory.createAnnotation(jcas, 2, 3, Token.class);
		AnnotationFactory.createAnnotation(jcas, 4, 5, Token.class);

		assertEquals(2.0 / 3.0, DramaUtil.ttr(JCasUtil.select(jcas, Token.class), 3, true), 1e-4);
	}
}
