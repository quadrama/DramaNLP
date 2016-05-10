package de.unistuttgart.ims.drama.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.unistuttgart.ims.drama.api.Drama;
import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.ims.drama.api.FigureType;
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

	public static Figure getFigure(Utterance u) {
		Speaker s = getSpeaker(u);
		if (s != null)
			return s.getFigure();
		else
			return null;
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

	public static FigureType assignFigureType(JCas jcas, Figure figure, String cl, String value) {
		FigureType ft = AnnotationFactory.createAnnotation(jcas, figure.getBegin(), figure.getEnd(), FigureType.class);
		ft.setTypeClass(cl);
		ft.setTypeValue(value);
		return ft;
	}

	public static Collection<FigureType> getAllFigureTypes(Figure figure) {
		if (figure != null)
			return JCasUtil.selectCovered(FigureType.class, figure);
		return new HashSet<FigureType>();
	}

	public static String getTypeValue(JCas jcas, Figure figure, String typeClass) {
		for (FigureType ft : JCasUtil.selectCovered(FigureType.class, figure)) {
			if (typeClass.equals(ft.getTypeClass()))
				return ft.getTypeValue();
		}
		return null;
	}

	public static String getDisplayId(JCas jcas) {
		String title = JCasUtil.selectSingle(jcas, Drama.class).getDocumentTitle();
		String author = JCasUtil.selectSingle(jcas, Drama.class).getAuthorname();
		if (title == null || author == null || author.length() == 0 || title.length() == 0) {
			return JCasUtil.selectSingle(jcas, Drama.class).getDocumentId();
		}
		StringBuilder b = new StringBuilder();
		for (String s : author.split(" ")) {
			b.append(s.charAt(0));
		}
		b.append('_');
		for (String s : title.split(" ")) {
			b.append(s.charAt(0));
		}
		return b.toString();
	}
}
