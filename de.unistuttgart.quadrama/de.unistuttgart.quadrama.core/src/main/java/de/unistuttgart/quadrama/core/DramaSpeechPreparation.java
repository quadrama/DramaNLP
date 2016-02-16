package de.unistuttgart.quadrama.core;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.factory.JCasBuilder;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.unistuttgart.quadrama.api.Speech;
import de.unistuttgart.quadrama.api.Utterance;
import de.unistuttgart.quadrama.core.api.Origin;

public class DramaSpeechPreparation extends JCasAnnotator_ImplBase {
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		JCas utteranceCas;
		try {
			utteranceCas =
					jcas.createView(DramaSpeechSegmenter.SOFA_UTTERANCES);
			utteranceCas.setDocumentLanguage(jcas.getDocumentLanguage());
			JCasBuilder b = new JCasBuilder(utteranceCas);
			for (Utterance utterance : JCasUtil.select(jcas, Utterance.class)) {
				for (Speech speech : JCasUtil.selectCovered(Speech.class,
						utterance)) {
					b.add(speech.getCoveredText(), Origin.class).setOffset(
							speech.getBegin());
				}
				b.add("\n\n\n");
			}
			b.close();
		} catch (CASException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
}
