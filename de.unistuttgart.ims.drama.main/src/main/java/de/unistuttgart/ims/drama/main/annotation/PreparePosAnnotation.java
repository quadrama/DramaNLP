package de.unistuttgart.ims.drama.main.annotation;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unistuttgart.ims.drama.api.Scene;
import de.unistuttgart.ims.drama.util.DramaUtil;

public class PreparePosAnnotation extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		String did = DramaUtil.getDrama(aJCas).getDocumentId();
		if (did.equalsIgnoreCase("rksp.0"))
			prepareRksp0(aJCas);
	}

	public void prepareRksp0(JCas jcas) {
		for (Scene scene : JCasUtil.select(jcas, Scene.class)) {
			if (scene.getBegin() == 36351 || scene.getBegin() == 39097 || scene.getBegin() == 39975) {
				for (Token token : JCasUtil.selectCovered(Token.class, scene)) {
					AnnotationFactory.createAnnotation(jcas, token.getBegin(), token.getEnd(), POS.class);
				}
			}
		}
	}

}
