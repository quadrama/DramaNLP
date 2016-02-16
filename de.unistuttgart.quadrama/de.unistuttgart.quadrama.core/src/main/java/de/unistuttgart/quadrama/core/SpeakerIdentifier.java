package de.unistuttgart.quadrama.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.unistuttgart.quadrama.api.Speaker;

public class SpeakerIdentifier extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		int speakerId = 1;
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (Speaker cm : JCasUtil.select(jcas, Speaker.class)) {
			String sName = cm.getCoveredText().trim().replaceAll("[.,;]", "");
			if (map.containsKey(sName))
				cm.setId(map.get(sName));
			else {
				map.put(sName, speakerId);
				cm.setId(speakerId++);
			}
		}

	}
}
