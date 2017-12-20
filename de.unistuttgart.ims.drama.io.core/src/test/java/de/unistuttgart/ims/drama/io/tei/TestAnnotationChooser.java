package de.unistuttgart.ims.drama.io.tei;

import java.util.TreeSet;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.junit.Test;

import de.unistuttgart.quadrama.io.tei.AnnotationChooser;

public class TestAnnotationChooser {

	@Test
	public void testChooser() throws UIMAException {
		JCas jcas = JCasFactory.createText("the dog barks");

		TreeSet<Annotation> ts = new TreeSet<Annotation>(new AnnotationChooser(5));
		ts.add(AnnotationFactory.createAnnotation(jcas, 5, 5, Annotation.class));
		ts.add(AnnotationFactory.createAnnotation(jcas, 5, 10, Annotation.class));
		ts.add(AnnotationFactory.createAnnotation(jcas, 1, 5, Annotation.class));
		ts.add(AnnotationFactory.createAnnotation(jcas, 5, 8, Annotation.class));
		ts.add(AnnotationFactory.createAnnotation(jcas, 5, 5, Annotation.class));
		ts.add(AnnotationFactory.createAnnotation(jcas, 4, 5, Annotation.class));

		System.out.println(ts);
	}
}
