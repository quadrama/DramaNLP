package de.unistuttgart.quadrama.experiments;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.unistuttgart.quadrama.api.Speech;
import de.unistuttgart.quadrama.api.Utterance;

@Deprecated
public class FixGutenbergSpeech extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		for (Utterance utterance : JCasUtil.select(jcas, Utterance.class)) {
			Speech speech =
					JCasUtil.selectCovered(Speech.class, utterance).iterator()
							.next();
			if (speech.getCoveredText().startsWith(". ")) {
				speech.setBegin(speech.getBegin() + 2);
			}
		}
	}

}
