package de.unistuttgart.ims.drama.core.ml;

import static org.junit.Assert.assertEquals;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

public class TestMapBack {
	@Test
	public void testRemoveView() throws UIMAException {
		JCas jcas = JCasFactory.createJCas();
		jcas.setDocumentText("bla bla");

		assertEquals(1, IteratorUtils.toList(jcas.getViewIterator()).size());

		JCas other = jcas.createView("View 2");
		other.setDocumentText("blubb blubb");
		assertEquals(2, IteratorUtils.toList(jcas.getViewIterator()).size());

		other.getCasImpl().reset();
		assertEquals(1, IteratorUtils.toList(jcas.getViewIterator()).size());

	}
}
