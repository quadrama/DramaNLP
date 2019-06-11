package de.unistuttgart.quadrama.core;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.factory.JCasBuilder;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.unistuttgart.ims.drama.api.Speaker;
import de.unistuttgart.quadrama.core.api.Origin;

/**
 * This component creates a new view called {@link D#SOFA_SPEAKERS} and writes
 * all utterances together with their origin position into this view.
 * 
 * @author Nils Reiter
 *
 */
public class DramaSpeakerPreparation extends JCasAnnotator_ImplBase {
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		JCas speakerCas = null;
		try {
			// create the new view
			speakerCas = jcas.createView(SP.SOFA_SPEAKERS);
			speakerCas.setDocumentLanguage(jcas.getDocumentLanguage());
		} catch (CASException e1) {
			throw new AnalysisEngineProcessException(e1);
		}

		// fill the jcas speaker-wise
		JCasBuilder b = new JCasBuilder(speakerCas);
		for (Speaker speaker : JCasUtil.select(jcas, Speaker.class)) {
			b.add(speaker.getCoveredText(), Origin.class).setOffset(speaker.getBegin());
			b.add("\n\n");
		}
		b.close();

	}
}
