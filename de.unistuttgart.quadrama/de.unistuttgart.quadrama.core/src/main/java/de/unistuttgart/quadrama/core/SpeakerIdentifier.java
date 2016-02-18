package de.unistuttgart.quadrama.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
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

		Set<Speaker> unassigned = new HashSet<Speaker>();

		for (Speaker cm : JCasUtil.select(jcas, Speaker.class)) {
			String sName =
					cm.getCoveredText().trim().replaceAll("[.,;]", "")
							.toLowerCase();
			if (map.containsKey(sName))
				cm.setFigure(map.get(sName));
			else {
				unassigned.add(cm);
			}
		}

		unassigned = assignLevel2(map.values(), unassigned);
		unassigned = assignLevel3(map.values(), unassigned);

		getLogger().log(Level.WARNING,
				"Unassigned speakers: " + JCasUtil.toText(unassigned));

	}

	protected Set<Speaker> assignLevel2(Collection<Figure> figures,
			Collection<Speaker> speakers) {
		Set<Speaker> unassigned = new HashSet<Speaker>();
		for (Speaker speaker : speakers) {
			for (Figure figure : figures) {
				String[] nameParts =
						figure.getCoveredText().toLowerCase().split(" +");
				if (ArrayUtils.contains(nameParts, speaker.getCoveredText()
						.toLowerCase().trim())) {
					speaker.setFigure(figure);
				} else {
					unassigned.add(speaker);
				}
			}
		}
		return unassigned;
	}

	protected Set<Speaker> assignLevel3(Collection<Figure> figures,
			Collection<Speaker> speakers) {
		Set<Speaker> unassigned = new HashSet<Speaker>();
		for (Speaker speaker : speakers) {
			for (Figure figure : figures) {
				if (figure.getDescription() != null) {
					String[] nameParts =
							figure.getDescription().toLowerCase().split(" +");
					if (ArrayUtils
							.contains(nameParts, speaker.getCoveredText())) {
						speaker.setFigure(figure);
					} else {
						unassigned.add(speaker);
					}
				}
			}
		}
		return unassigned;
	}
}
