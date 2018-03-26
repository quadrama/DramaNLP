package de.unistuttgart.quadrama.core;

import static org.junit.Assert.assertEquals;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import de.unistuttgart.ims.drama.api.Speaker;
import de.unistuttgart.ims.drama.api.Speech;
import de.unistuttgart.ims.drama.api.StageDirection;
import de.unistuttgart.ims.drama.api.Utterance;

public class TestMergeSpeechAnnotations {
	JCas jcas;

	@Test
	public void testMerge() throws UIMAException {
		jcas = JCasFactory.createText("012345678901234567890123");
		AnnotationFactory.createAnnotation(jcas, 0, 5, Utterance.class);
		AnnotationFactory.createAnnotation(jcas, 0, 1, Speaker.class);
		AnnotationFactory.createAnnotation(jcas, 1, 2, StageDirection.class);
		AnnotationFactory.createAnnotation(jcas, 2, 3, Speech.class);
		AnnotationFactory.createAnnotation(jcas, 3, 4, Speech.class);
		AnnotationFactory.createAnnotation(jcas, 4, 5, Speech.class);

		AnnotationFactory.createAnnotation(jcas, 6, 20, Utterance.class);
		AnnotationFactory.createAnnotation(jcas, 6, 7, Speaker.class);
		AnnotationFactory.createAnnotation(jcas, 9, 10, Speech.class);
		AnnotationFactory.createAnnotation(jcas, 11, 12, Speech.class);
		AnnotationFactory.createAnnotation(jcas, 12, 13, StageDirection.class);
		AnnotationFactory.createAnnotation(jcas, 14, 15, Speech.class);

		SimplePipeline.runPipeline(jcas, AnalysisEngineFactory.createEngineDescription(MergeSpeechAnnotations.class));

		assertEquals(2, JCasUtil.select(jcas, Utterance.class).size());
		assertEquals(2, JCasUtil.select(jcas, StageDirection.class).size());
		assertEquals(3, JCasUtil.select(jcas, Speech.class).size());
		Speech speech = JCasUtil.selectByIndex(jcas, Speech.class, 0);
		assertEquals(2, speech.getBegin());
		assertEquals(5, speech.getEnd());
		speech = JCasUtil.selectByIndex(jcas, Speech.class, 1);
		assertEquals(9, speech.getBegin());
		assertEquals(12, speech.getEnd());
		speech = JCasUtil.selectByIndex(jcas, Speech.class, 2);
		assertEquals(14, speech.getBegin());
		assertEquals(15, speech.getEnd());
	}

}
