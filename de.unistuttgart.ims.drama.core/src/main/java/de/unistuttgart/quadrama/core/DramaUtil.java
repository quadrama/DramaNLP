package de.unistuttgart.quadrama.core;

import java.util.LinkedList;
import java.util.List;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.ims.drama.api.Speaker;
import de.unistuttgart.ims.drama.api.Speech;
import de.unistuttgart.ims.drama.api.Utterance;

public class DramaUtil {
	public static List<Speech> getSpeeches(JCas jcas, Figure figure) {
		List<Speech> ret = new LinkedList<Speech>();
		for (Utterance u : JCasUtil.select(jcas, Utterance.class)) {
			Speaker sp;
			try {
				sp = JCasUtil.selectCovered(jcas, Speaker.class, u).get(0);
			} catch (Exception e) {
				continue;
			}
			if (sp != null && sp.getFigure() == figure) {
				ret.addAll(JCasUtil.selectCovered(jcas, Speech.class, u));
			}
		}
		return ret;
	}
}
