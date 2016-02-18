package de.unistuttgart.quadrama.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.Level;

import de.unistuttgart.quadrama.api.Drama;
import de.unistuttgart.quadrama.api.Figure;
import de.unistuttgart.quadrama.api.Speaker;

public class SpeakerIdentifier extends JCasAnnotator_ImplBase {

	int threshold = 2;

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		getLogger().log(
				Level.FINE,
				"Now processing "
						+ JCasUtil.selectSingle(jcas, Drama.class)
								.getDocumentId());

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
				for (Figure figure : map.values()) {
					if (figure.getCoveredText().trim().toLowerCase()
							.contains(sName)) {
						cm.setFigure(figure);
						break;
					}
				}
				if (cm.getFigure() == null)
					for (Figure figure : map.values()) {
						if (figure.getDescription() != null
								&& figure.getDescription().toLowerCase().trim()
								.contains(sName)) {
							cm.setFigure(figure);
							break;
						}
					}
				if (cm.getFigure() == null) {
					for (Figure figure : map.values()) {
						int lev =
								StringUtils.getLevenshteinDistance(figure
										.getCoveredText().trim().toLowerCase(),
										sName);
						if (lev <= threshold) {
							cm.setFigure(figure);
							break;
						}

					}
				}
				if (cm.getFigure() == null) {
					getLogger().log(Level.INFO, "Could not assign " + sName);
				}
			}
		}

	}
}
