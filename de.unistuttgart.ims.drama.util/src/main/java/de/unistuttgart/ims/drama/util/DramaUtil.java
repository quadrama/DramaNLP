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
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;

import de.unistuttgart.ims.drama.api.Author;
import de.unistuttgart.ims.drama.api.CastFigure;
import de.unistuttgart.ims.drama.api.DateReference;
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
			Speaker sp = DramaUtil.getFirstSpeaker(u);
			if (sp != null && sp.getFigure() == figure) {
				ret.addAll(JCasUtil.selectCovered(jcas, Speech.class, u));
			}
		}
		return ret;
	}

	public static Collection<Speech> getSpeeches(JCas jcas, Figure figure, Annotation coveringAnnotation) {
		List<Speech> ret = new LinkedList<Speech>();
		for (Utterance u : JCasUtil.selectCovered(jcas, Utterance.class, coveringAnnotation)) {
			Speaker sp = DramaUtil.getFirstSpeaker(u);
			if (sp != null && sp.getFigure() == figure) {
				ret.addAll(JCasUtil.selectCovered(jcas, Speech.class, u));
			}
		}
		return ret;
	}

	public static Collection<Speaker> getSpeakers(Utterance utterance) {
		try {
			return JCasUtil.selectCovered(Speaker.class, utterance);
		} catch (Exception e) {
			return null;
		}
	}

	public static Speaker getFirstSpeaker(Utterance utterance) {
		try {
			return JCasUtil.selectCovered(Speaker.class, utterance).get(0);
		} catch (Exception e) {
			return null;
		}
	}

	public static Collection<Figure> getFigures(Utterance u) {
		Collection<Speaker> s = getSpeakers(u);
		Collection<Figure> f = new LinkedList<Figure>();
		for (Speaker speaker : s) {
			f.add(speaker.getFigure());
		}
		return f;
	}

	public static Collection<CastFigure> getCastFigures(Utterance u) {
		Collection<Speaker> s = getSpeakers(u);
		Collection<CastFigure> f = new LinkedList<CastFigure>();
		for (Speaker speaker : s) {
			for (int i = 0; i < speaker.getCastFigure().size(); i++)
				f.add(speaker.getCastFigure(i));
		}
		return f;
	}

	public static Figure getFirstFigure(Utterance u) {
		Speaker s = getFirstSpeaker(u);
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
		FigureType ft = AnnotationFactory.createAnnotation(jcas, figure.getBegin(), figure.getBegin() + 1,
				FigureType.class);
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

		String author = "";
		if (JCasUtil.exists(jcas, Author.class))
			author = JCasUtil.select(jcas, Author.class).iterator().next().getName();
		int date = 0;
		if (JCasUtil.exists(jcas, DateReference.class)) {
			date = JCasUtil.selectSingle(jcas, DateReference.class).getYear();
		}
		if (title == null || author == null || author.length() == 0 || title.length() == 0) {
			return JCasUtil.selectSingle(jcas, Drama.class).getDocumentId();
		}
		StringBuilder b = new StringBuilder();
		if (date != 0)
			b.append(date).append("_");
		for (String s : author.split(" ")) {
			b.append(s.charAt(0));
		}
		b.append('_');
		for (String s : title.split(" ")) {
			b.append(s.charAt(0));
		}
		b.append("_");
		b.append(JCasUtil.selectSingle(jcas, Drama.class).getDocumentId());
		return b.toString();
	}

	public static <T extends TOP> T createFeatureStructure(JCas jcas, Class<T> cls) {
		T fs = jcas.getCas().createFS(JCasUtil.getType(jcas, cls));
		fs.addToIndexes();
		return fs;
	}

	public static Drama getDrama(JCas jcas) {
		if (JCasUtil.exists(jcas, Drama.class)) {
			return JCasUtil.selectSingle(jcas, Drama.class);
		} else {
			Drama d = new Drama(jcas);
			d.addToIndexes();
			return d;
		}
	}

	public static <T extends TOP> T getOrCreate(JCas jcas, Class<T> targetClass) {
		if (JCasUtil.exists(jcas, targetClass)) {
			return JCasUtil.selectSingle(jcas, targetClass);
		} else {
			T annotation = jcas.getCas().createFS(JCasUtil.getType(jcas, targetClass));
			jcas.getCas().addFsToIndexes(annotation);
			return annotation;
		}
	}
}
