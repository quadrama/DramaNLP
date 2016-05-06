package de.unistuttgart.quadrama.core;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.Level;

import de.unistuttgart.ims.drama.api.Speaker;
import de.unistuttgart.ims.drama.api.Speech;
import de.unistuttgart.ims.drama.api.Utterance;

@Deprecated
public class UtteranceLinker extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		for (Utterance utterance : JCasUtil.select(jcas, Utterance.class)) {
			try {
				utterance.setSpeaker(JCasUtil.selectCovered(Speaker.class, utterance).get(0));
			} catch (IndexOutOfBoundsException e) {
				getLogger().log(Level.WARNING, "No speaker in utterance " + utterance.getCoveredText());
			}
			try {
				utterance.setSpeech(JCasUtil.selectCovered(Speech.class, utterance).get(0));
			} catch (IndexOutOfBoundsException e) {
				getLogger().log(Level.WARNING, "No speech in utterance " + utterance.getCoveredText());
			}
		}
	}

}
