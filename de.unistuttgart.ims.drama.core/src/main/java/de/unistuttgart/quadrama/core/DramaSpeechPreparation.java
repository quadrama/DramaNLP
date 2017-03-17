package de.unistuttgart.quadrama.core;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.factory.JCasBuilder;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.unistuttgart.ims.drama.api.Speech;
import de.unistuttgart.ims.drama.api.Utterance;
import de.unistuttgart.quadrama.core.api.Origin;

/**
 * This component creates a new view called
 * {@link D#SOFA_UTTERANCES} and writes all utterances
 * together with their origin position into this view.
 * 
 * @author Nils Reiter
 *
 */
public class DramaSpeechPreparation extends JCasAnnotator_ImplBase {
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		JCas utteranceCas = null;
		try {
			// create the new view
			utteranceCas =
					jcas.createView(D.SOFA_UTTERANCES);
			utteranceCas.setDocumentLanguage(jcas.getDocumentLanguage());
		} catch (CASException e1) {
			throw new AnalysisEngineProcessException(e1);
		}

		// fill the jcas utterance-wise
		JCasBuilder b = new JCasBuilder(utteranceCas);
		for (Utterance utterance : JCasUtil.select(jcas, Utterance.class)) {
			for (Speech speech : JCasUtil
					.selectCovered(Speech.class, utterance)) {
				b.add(speech.getCoveredText(), Origin.class).setOffset(
						speech.getBegin());
				b.add(" ");
			}
			b.add("\n\n");
		}
		b.close();

	}
}
