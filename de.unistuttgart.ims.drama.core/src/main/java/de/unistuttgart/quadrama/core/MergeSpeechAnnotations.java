package de.unistuttgart.quadrama.core;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.unistuttgart.ims.drama.api.Speech;
import de.unistuttgart.ims.drama.api.StageDirection;
import de.unistuttgart.ims.drama.api.Utterance;
import de.unistuttgart.ims.uimautil.AnnotationComparator;

public class MergeSpeechAnnotations extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		Set<Annotation> toRemove = new HashSet<Annotation>();
		for (Utterance utterance : JCasUtil.select(jcas, Utterance.class)) {
			SortedSet<Annotation> annotations = new TreeSet<Annotation>(new AnnotationComparator());
			annotations.addAll(JCasUtil.selectCovered(Speech.class, utterance));
			annotations.addAll(JCasUtil.selectCovered(StageDirection.class, utterance));

			Annotation lastAnnotation = null;
			for (Annotation anno : annotations) {
				if (lastAnnotation != null) {
					if (lastAnnotation instanceof Speech && anno instanceof Speech) {
						lastAnnotation.setEnd(anno.getEnd());
						toRemove.add(anno);
					} else
						lastAnnotation = anno;
				} else
					lastAnnotation = anno;
			}

		}

		for (Annotation a : toRemove)
			a.removeFromIndexes();
	}

}
