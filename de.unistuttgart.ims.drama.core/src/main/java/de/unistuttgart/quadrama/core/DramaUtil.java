package de.unistuttgart.quadrama.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.ims.drama.api.Speaker;
import de.unistuttgart.ims.drama.api.Speech;
import de.unistuttgart.ims.drama.api.Utterance;

public class DramaUtil {
	public static Collection<Speech> getSpeeches(JCas jcas, Figure figure) {
		List<Speech> ret = new LinkedList<Speech>();
		for (Utterance u : JCasUtil.select(jcas, Utterance.class)) {
			Speaker sp = DramaUtil.getSpeaker(u);
			if (sp != null && sp.getFigure() == figure) {
				ret.addAll(JCasUtil.selectCovered(jcas, Speech.class, u));
			}
		}
		return ret;
	}

	public static Speaker getSpeaker(Utterance utterance) {
		try {
			return JCasUtil.selectCovered(Speaker.class, utterance).get(0);
		} catch (Exception e) {
			return null;
		}
	}

	public static Collection<Utterance> selectFullUtterances(JCas jcas) {
		final Iterator<Utterance> baseIterator = JCasUtil.iterator(jcas, Utterance.class);

		List<Utterance> fullUtterances = new ArrayList<Utterance>();
		while (baseIterator.hasNext()) {
			Utterance utt = baseIterator.next();
			if (!JCasUtil.selectCovered(jcas, Speaker.class, utt).isEmpty()
					&& JCasUtil.selectCovered(jcas, Speaker.class, utt).get(0).getFigure() != null)
				fullUtterances.add(utt);
		}
		return fullUtterances;

	}
}
