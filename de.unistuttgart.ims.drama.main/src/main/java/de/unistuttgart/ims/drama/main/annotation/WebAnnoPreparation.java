package de.unistuttgart.ims.drama.main.annotation;

import java.util.HashMap;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceChain;
import de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceLink;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.unistuttgart.ims.drama.api.ActHeading;
import de.unistuttgart.ims.drama.api.CastFigure;
import de.unistuttgart.ims.drama.api.Drama;
import de.unistuttgart.ims.drama.api.SceneHeading;
import de.unistuttgart.ims.drama.api.Speaker;
import de.unistuttgart.ims.drama.api.Speech;
import de.unistuttgart.ims.drama.api.StageDirection;

public class WebAnnoPreparation extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		Drama d = JCasUtil.selectSingle(jcas, Drama.class);
		Map<CastFigure, CoreferenceChain> chains = new HashMap<CastFigure, CoreferenceChain>();
		Map<CastFigure, CoreferenceLink> lastLink = new HashMap<CastFigure, CoreferenceLink>();
		for (CastFigure cf : JCasUtil.select(jcas, CastFigure.class)) {
			CoreferenceChain cc = new CoreferenceChain(jcas);
			cc.addToIndexes();
			chains.put(cf, cc);
		}

		for (Speaker speaker : JCasUtil.select(jcas, Speaker.class)) {
			CoreferenceLink cl = AnnotationFactory.createAnnotation(jcas, speaker.getBegin(), speaker.getEnd(),
					CoreferenceLink.class);
			if (speaker.getCastFigure().size() == 1) {
				CastFigure cf = speaker.getCastFigure(0);
				CoreferenceChain cc = chains.get(cf);
				if (cc.getFirst() == null) {
					cc.setFirst(cl);
				} else {
					lastLink.get(cf).setNext(cl);
				}
				lastLink.put(cf, cl);

			} else {
				CoreferenceChain cc = new CoreferenceChain(jcas);
				cc.setFirst(cl);
				cc.addToIndexes();
			}
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
