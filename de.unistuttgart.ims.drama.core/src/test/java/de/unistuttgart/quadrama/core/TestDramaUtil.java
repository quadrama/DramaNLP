package de.unistuttgart.quadrama.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.ims.drama.api.Speaker;
import de.unistuttgart.ims.drama.api.Utterance;

public class TestDramaUtil {

	@Test
	public void TestGetFullUtterances() throws Exception {
		JCas jcas = JCasFactory.createJCas();
		jcas.setDocumentText("Lorem ipsum dolor sit amet, consetetur sadipscing");
		AnnotationFactory.createAnnotation(jcas, 0, 5, Utterance.class);
		AnnotationFactory.createAnnotation(jcas, 6, 7, Utterance.class);
		AnnotationFactory.createAnnotation(jcas, 8, 10, Utterance.class);

		Iterator<Utterance> iter = DramaUtil.selectFullUtterances(jcas).iterator();
		assertFalse(iter.hasNext());

		Speaker s1 = AnnotationFactory.createAnnotation(jcas, 0, 1, Speaker.class);
		Speaker s2 = AnnotationFactory.createAnnotation(jcas, 8, 9, Speaker.class);
		iter = DramaUtil.selectFullUtterances(jcas).iterator();
		assertFalse(iter.hasNext());

		s1.setFigure(AnnotationFactory.createAnnotation(jcas, 0, 0, Figure.class));
		s2.setFigure(AnnotationFactory.createAnnotation(jcas, 0, 0, Figure.class));
		iter = DramaUtil.selectFullUtterances(jcas).iterator();
		assertTrue(iter.hasNext());
		iter.next();
		assertTrue(iter.hasNext());
		iter.next();
		assertFalse(iter.hasNext());

	}
}
