package de.unistuttgart.ims.drama.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.fit.util.CasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;

import de.unistuttgart.ims.drama.api.Mention;
import de.unistuttgart.ims.drama.api.DiscourseEntity;

/**
 * Finds all mention spans with more than one entity and generates new group
 * entities out of these entities. Reads and writes directly from and to the
 * Mention.class and DiscourseEntity.class indices.
 * 
 * @since 2.1.0
 */
public class CreateCoreferenceGroups extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		// Get mapping of offset to mention
		Map<List<Integer>, List<Mention>> offset2Mention = new HashMap<List<Integer>, List<Mention>>();
		List<Integer> allIds = new ArrayList<Integer>();
		for (Mention mention : JCasUtil.select(jcas, Mention.class)) {
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
		if (allIds.isEmpty()) {
		} else {
			// Get all mentions with identical offset, remove mentions with an unique offset
			List<ArrayList<Mention>> multipleEntities = new ArrayList<ArrayList<Mention>>();
			for (List<Integer> offset : offset2Mention.keySet()) {
				if (offset2Mention.get(offset).size() > 1) {
					multipleEntities.add((ArrayList<Mention>) offset2Mention.get(offset));
				}
			}
			// Map all entity groups to new ids
			Map<HashSet<Integer>, Integer> entityMap = new HashMap<HashSet<Integer>, Integer>();
			// Get highest current id
			Integer highestId = Collections.max(allIds);
			HashSet<HashSet<Integer>> groupIds = new HashSet<HashSet<Integer>>();
			for (List<Mention> mentionList : multipleEntities) {
				HashSet<Integer> currentIds = new HashSet<Integer>();
				for (Mention mention : mentionList) {
					currentIds.add(mention.getEntity().getId());
				}
				groupIds.add(currentIds);
			}
			for (HashSet<Integer> ids : groupIds) {
				highestId++;
				entityMap.put(ids, highestId);
			}
			// Create new group entities, annotate the respective mentions and delete old
			// mention annotations
			HashSet<DiscourseEntity> entityList = new HashSet<DiscourseEntity>();
			for (List<Mention> mentionList : multipleEntities) {
				Mention m = mentionList.get(0);
				FSArray entityFSArray = new FSArray(jcas, mentionList.size());
				HashSet<Integer> oldIds = new HashSet<Integer>();
				ArrayList<String> newDisplayNameArray = new ArrayList<String>();
				int i = 0;
				for (Mention mention : mentionList) {
					oldIds.add(mention.getEntity().getId());
					newDisplayNameArray.add(mention.getEntity().getDisplayName());
					entityFSArray.set(i, mention.getEntity());
					i++;
					mention.removeFromIndexes(jcas);
				}
				String newDisplayName = String.join("+", newDisplayNameArray);
				Integer groupId = entityMap.get(oldIds);
				DiscourseEntity de = m.getCAS().createFS(CasUtil.getType(m.getCAS(), DiscourseEntity.class));
				de.setId(groupId);
				StringArray xmlId = new StringArray(jcas, 1);
				de.setXmlId(xmlId);
				de.setDisplayName(newDisplayName);
				de.setEntityGroup(entityFSArray);
				entityList.add(de);
				m.setEntity(de);
				m.addToIndexes(jcas);
			}
			HashMap<Integer, DiscourseEntity> groupEntityMap = new HashMap<Integer, DiscourseEntity>();
			for (DiscourseEntity de : entityList) {
				groupEntityMap.put(de.getId(), de);
			}
			for (Integer i : groupEntityMap.keySet()) {
				DiscourseEntity de = groupEntityMap.get(i);
				de.addToIndexes(jcas);
			}
		}
	}
}
