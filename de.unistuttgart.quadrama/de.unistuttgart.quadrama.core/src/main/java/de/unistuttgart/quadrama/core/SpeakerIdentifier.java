package de.unistuttgart.quadrama.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.unistuttgart.quadrama.api.Figure;
import de.unistuttgart.quadrama.api.Speaker;

public class SpeakerIdentifier extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		Map<String, Figure> map = new HashMap<String, Figure>();
		int figureId = 0;
		for (Figure figure : JCasUtil.select(jcas, Figure.class)) {
			figure.setId(figureId++);
			map.put(figure.getCoveredText().trim().toLowerCase(), figure);
		}

		for (Speaker cm : JCasUtil.select(jcas, Speaker.class)) {
			String sName =
					cm.getCoveredText().trim().replaceAll("[.,;]", "")
							.toLowerCase();
			if (map.containsKey(sName))
				cm.setFigure(map.get(sName));
			else {
				System.err.println("Could not assign " + sName);
			}
		}

	}
}
