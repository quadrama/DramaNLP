package de.unistuttgart.ims.drama.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.unistuttgart.ims.drama.api.Mention;

/**
 * Finds all mentions with identical entities on identical offsets and only
 * keeps one of these mentions.
 * 
 * @since 2.2.0
 */
public class RemoveDoubledMentions extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		// Get mapping of offset to mention
		Map<List<Integer>, List<Mention>> offset2Mention = new HashMap<List<Integer>, List<Mention>>();
		List<Integer> allIds = new ArrayList<Integer>();
		for (Mention mention : JCasUtil.select(jcas, Mention.class)) {
			if (!(mention.getEntity() == null)) {
				allIds.add(mention.getEntity().getId());
				List<Integer> offsets = new ArrayList<Integer>();
				List<Mention> currentMentions = new ArrayList<Mention>();
				offsets.add(mention.getBegin());
				offsets.add(mention.getEnd());
				if (offset2Mention.containsKey(offsets)) {
					currentMentions = offset2Mention.get(offsets);
					currentMentions.add(mention);
					offset2Mention.put(offsets, currentMentions);
				} else {
					currentMentions.add(mention);
					offset2Mention.put(offsets, currentMentions);
				}
			}
		}
		if (allIds.isEmpty()) {
		} else {
			// Get all mentions with identical offset and entity id
			List<ArrayList<Mention>> multipleEntities = new ArrayList<ArrayList<Mention>>();
			for (List<Integer> offset : offset2Mention.keySet()) {
				if (offset2Mention.get(offset).size() > 1) {
					multipleEntities.add((ArrayList<Mention>) offset2Mention.get(offset));
				}
			}
			// Remove mentions on same offset with identical entity id
			for (List<Mention> mentionList : multipleEntities) {
				List<Integer> Ids = new ArrayList<Integer>();
				for (Mention m : mentionList) {
					if (Ids.contains(m.getEntity().getId())) {
						m.removeFromIndexes(jcas);
					}
					Ids.add(m.getEntity().getId());
				}
			}
		}
	}
}