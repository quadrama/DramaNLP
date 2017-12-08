package de.unistuttgart.ims.drama.main.annotation;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.unistuttgart.ims.drama.api.ActHeading;
import de.unistuttgart.ims.drama.api.SceneHeading;
import de.unistuttgart.ims.drama.api.Speaker;
import de.unistuttgart.ims.drama.api.Speech;
import de.unistuttgart.ims.drama.api.StageDirection;
import webanno.custom.RA;

public class WebAnnoPreparation extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		for (Speaker speaker : JCasUtil.select(jcas, Speaker.class)) {
			AnnotationFactory.createAnnotation(jcas, speaker.getBegin(), speaker.getEnd(), RA.class);
			AnnotationFactory.createAnnotation(jcas, speaker.getBegin(), speaker.getEnd(), Sentence.class);
		}

		for (Speech speaker : JCasUtil.select(jcas, Speech.class)) {
			AnnotationFactory.createAnnotation(jcas, speaker.getBegin(), speaker.getEnd(), Sentence.class);
		}

		for (StageDirection speaker : JCasUtil.select(jcas, StageDirection.class)) {
			AnnotationFactory.createAnnotation(jcas, speaker.getBegin(), speaker.getEnd(), Sentence.class);
		}

		for (ActHeading speaker : JCasUtil.select(jcas, ActHeading.class)) {
			AnnotationFactory.createAnnotation(jcas, speaker.getBegin(), speaker.getEnd(), Sentence.class);
		}

		for (SceneHeading speaker : JCasUtil.select(jcas, SceneHeading.class)) {
			AnnotationFactory.createAnnotation(jcas, speaker.getBegin(), speaker.getEnd(), Sentence.class);
		}

	}

}
