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
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.Level;

import de.unistuttgart.ims.drama.api.Drama;
import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.ims.drama.api.Speaker;
import de.unistuttgart.ims.drama.api.SpeakerFigure;
import de.unistuttgart.ims.drama.util.AnnotationComparator;

/**
 * Before reference string mean 446.82978723404256 18.148936170212774 min 3.0
 * 1.0 max 1771.0 79.0
 * 
 * @author reiterns
 *
 */
@TypeCapability(inputs = { "de.unistuttgart.quadrama.api.Figure", "de.unistuttgart.quadrama.api.Speaker" }, outputs = {
		"de.unistuttgart.quadrama.api.Speaker:Figure", "de.unistuttgart.quadrama.api.SpeakerFigure" })
@Deprecated
public class SpeakerIdentifier extends JCasAnnotator_ImplBase {

	public static final String PARAM_CREATE_SPEAKER_FIGURE = "Create speaker figure";

	@ConfigurationParameter(name = PARAM_CREATE_SPEAKER_FIGURE, mandatory = false)
	boolean createSpeakerFigure = false;

	int threshold = 2;

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		getLogger().log(Level.INFO,
				"Running " + getClass().getName() + " on " + JCasUtil.selectSingle(jcas, Drama.class).getDocumentId());

		Map<String, Figure> map = new HashMap<String, Figure>();
		Map<String, Figure> referenceMap = new HashMap<String, Figure>();
		int figureId = 0;
		for (Figure figure : JCasUtil.select(jcas, Figure.class)) {
			figure.setId(figureId++);
			map.put(figure.getCoveredText().trim().toLowerCase(), figure);
			if (figure.getReference() != null)
				referenceMap.put(figure.getReference().trim().toLowerCase(), figure);
		}

		Set<Speaker> unassigned = new HashSet<Speaker>();

		for (Speaker cm : JCasUtil.select(jcas, Speaker.class)) {
			if (cm.getFigure() == null) {
				String sName = cm.getCoveredText().trim().replaceAll("[.,;]", "").toLowerCase();
				if (map.containsKey(sName))
					cm.setFigure(map.get(sName));
				else if (referenceMap.containsKey(sName))
					cm.setFigure(referenceMap.get(sName));
				else
					unassigned.add(cm);
			}
		}

		unassigned = assignLevel2(map.values(), unassigned);
		// unassigned = assignLevel3(map.values(), unassigned);
		unassigned = assignLevDistance(map.values(), unassigned, threshold);

		if (!unassigned.isEmpty())
			getLogger().log(Level.WARNING, unassigned.size() + " unassigned speakers: " + JCasUtil.toText(unassigned));

		if (createSpeakerFigure) {
			Map<String, TreeSet<Speaker>> unassignedMap = new HashMap<String, TreeSet<Speaker>>();
			for (Speaker speaker : unassigned) {
				if (!unassignedMap.containsKey(speaker.getCoveredText().trim()))
					unassignedMap.put(speaker.getCoveredText().trim(),
							new TreeSet<Speaker>(new AnnotationComparator()));
				unassignedMap.get(speaker.getCoveredText().trim()).add(speaker);
			}

			for (String s : unassignedMap.keySet()) {
				Speaker speaker = unassignedMap.get(s).first();
				getLogger().log(Level.WARNING, "Creating SpeakerFigure for " + speaker.getCoveredText());
				Figure fig = AnnotationFactory.createAnnotation(jcas, speaker.getBegin(), speaker.getEnd(),
						SpeakerFigure.class);
				fig.setId(figureId++);
				fig.setReference(speaker.getCoveredText());
				for (Speaker sp : unassignedMap.get(s)) {
					sp.setFigure(fig);
				}
			}
		}

	}

	protected Set<Speaker> assignLevel2(Collection<Figure> figures, Collection<Speaker> speakers) {
		Set<Speaker> unassigned = new HashSet<Speaker>();
		for (Speaker speaker : speakers) {
			for (Figure figure : figures) {
				String[] nameParts = figure.getCoveredText().toLowerCase().split(" +");
				if (ArrayUtils.contains(nameParts, speaker.getCoveredText().toLowerCase().trim())) {
					speaker.setFigure(figure);
				} else {
				}
			}
			if (speaker.getFigure() == null)
				unassigned.add(speaker);
		}
		return unassigned;
	}

	protected Set<Speaker> assignLevel3(Collection<Figure> figures, Collection<Speaker> speakers) {
		Set<Speaker> unassigned = new HashSet<Speaker>();
		for (Speaker speaker : speakers) {
			for (Figure figure : figures) {
				if (figure.getDescription() != null) {
					String[] nameParts = figure.getDescription().getCoveredText().toLowerCase().split(" +");
					if (ArrayUtils.contains(nameParts, speaker.getCoveredText())) {
						speaker.setFigure(figure);
					}
				}
			}
			if (speaker.getFigure() == null)
				unassigned.add(speaker);

		}
		return unassigned;
	}

	protected Set<Speaker> assignLevDistance(Collection<Figure> figures, Collection<Speaker> speakers,
			int maxDistance) {
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
			if (speaker.getFigure() == null)
				unassigned.add(speaker);

		}
		return unassigned;
	}
}
