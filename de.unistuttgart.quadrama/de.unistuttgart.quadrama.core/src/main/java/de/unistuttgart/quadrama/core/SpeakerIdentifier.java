package de.unistuttgart.quadrama.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.Level;

import de.unistuttgart.quadrama.api.Drama;
import de.unistuttgart.quadrama.api.Figure;
import de.unistuttgart.quadrama.api.Speaker;
import de.unistuttgart.quadrama.api.SpeakerFigure;

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
		// unassigned = assignLevel3(map.values(), unassigned);
		unassigned = assignLevDistance(map.values(), unassigned, threshold);

		if (!unassigned.isEmpty())
			getLogger().log(Level.WARNING,
					"Unassigned speakers: " + JCasUtil.toText(unassigned));

		if (true) {
			Map<String, TreeSet<Speaker>> unassignedMap =
					new HashMap<String, TreeSet<Speaker>>();
			for (Speaker speaker : unassigned) {
				if (!unassignedMap.containsKey(speaker.getCoveredText().trim()))
					unassignedMap.put(speaker.getCoveredText().trim(),
							new TreeSet<Speaker>(new AnnotationComparator()));
				unassignedMap.get(speaker.getCoveredText().trim()).add(speaker);
			}

			for (String s : unassignedMap.keySet()) {
				Speaker speaker = unassignedMap.get(s).first();
				getLogger().log(
						Level.WARNING,
						"Creating SpeakerFigure for "
								+ speaker.getCoveredText());
				Figure fig =
						AnnotationFactory.createAnnotation(jcas,
								speaker.getBegin(), speaker.getEnd(),
								SpeakerFigure.class);
				for (Speaker sp : unassignedMap.get(s)) {
					sp.setFigure(fig);
				}
			}
		}

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
				} else {}
			}
			if (speaker.getFigure() == null) unassigned.add(speaker);
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
					}
				}
			}
			if (speaker.getFigure() == null) unassigned.add(speaker);

		}
		return unassigned;
	}

	protected Set<Speaker> assignLevDistance(Collection<Figure> figures,
			Collection<Speaker> speakers, int maxDistance) {
		Set<Speaker> unassigned = new HashSet<Speaker>();
		for (Speaker speaker : speakers) {
			String sName = speaker.getCoveredText().trim().toLowerCase();
			for (Figure figure : figures) {
				String fName = figure.getCoveredText().trim().toLowerCase();
				int lev = StringUtils.getLevenshteinDistance(sName, fName);
				if (lev <= maxDistance) {
					speaker.setFigure(figure);
				}
			}
			if (speaker.getFigure() == null) unassigned.add(speaker);

		}
		return unassigned;
	}
}
