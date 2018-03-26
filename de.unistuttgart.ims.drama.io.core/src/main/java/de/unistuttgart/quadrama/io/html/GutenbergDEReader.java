package de.unistuttgart.quadrama.io.html;

import static de.unistuttgart.quadrama.io.core.DramaIOUtil.select2Annotation;
import static de.unistuttgart.quadrama.io.core.DramaIOUtil.selectRange2Annotation;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.io.IOUtils;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.unistuttgart.ims.drama.api.Act;
import de.unistuttgart.ims.drama.api.ActHeading;
import de.unistuttgart.ims.drama.api.Drama;
import de.unistuttgart.ims.drama.api.DramatisPersonae;
import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.ims.drama.api.Footnote;
import de.unistuttgart.ims.drama.api.FrontMatter;
import de.unistuttgart.ims.drama.api.MainMatter;
import de.unistuttgart.ims.drama.api.Scene;
import de.unistuttgart.ims.drama.api.SceneHeading;
import de.unistuttgart.ims.drama.api.Speaker;
import de.unistuttgart.ims.drama.api.Speech;
import de.unistuttgart.ims.drama.api.StageDirection;
import de.unistuttgart.ims.drama.api.Utterance;
import de.unistuttgart.ims.uima.io.xml.Visitor;
import de.unistuttgart.ims.uima.io.xml.type.XMLElement;
import de.unistuttgart.ims.uimautil.AnnotationUtil;
import de.unistuttgart.quadrama.io.core.AbstractDramaUrlReader;
import de.unistuttgart.quadrama.io.core.DramaIOUtil;

public class GutenbergDEReader extends AbstractDramaUrlReader {

	@Override
	public void getNext(JCas jcas, InputStream file, Drama drama) throws IOException, CollectionException {

		String str = IOUtils.toString(file, "UTF-8");
		org.jsoup.nodes.Document doc = Jsoup.parseBodyFragment(str);
		Visitor vis = new Visitor(jcas);
		doc.traverse(vis);
		jcas = vis.getJCas();
		Map<String, XMLElement> annoMap = vis.getAnnotationMap();

		// identify front and main matter
		select2Annotation(jcas, doc, annoMap, "div.gutenb:eq(0)", FrontMatter.class, null);
		selectRange2Annotation(jcas, doc, annoMap, "div.gutenb:eq(1)", "div.gutenb:last-child", MainMatter.class);
		FrontMatter frontMatter = JCasUtil.selectSingle(jcas, FrontMatter.class);
		MainMatter mainMatter = JCasUtil.selectSingle(jcas, MainMatter.class);

		// identify simple annotations
		select2Annotation(jcas, doc, annoMap, "span.speaker", Speaker.class, mainMatter);
		select2Annotation(jcas, doc, annoMap, "span.speaker", Figure.class, frontMatter);
		select2Annotation(jcas, doc, annoMap, "span.regie", StageDirection.class, mainMatter);
		select2Annotation(jcas, doc, annoMap, "span.footnote", Footnote.class, mainMatter);
		select2Annotation(jcas, doc, annoMap, "h3 + p", DramatisPersonae.class, frontMatter);

		// find utterances
		select2Annotation(jcas, doc, annoMap, "p:has(span.speaker)", Utterance.class, mainMatter);

		// some utterances continue in the next paragraph
		// they are (in RuJ) marked with class leftmarg
		Elements elms = doc.select("p.leftmarg");
		for (Element elm : elms) {
			XMLElement hAnno = annoMap.get(elm.cssSelector());
			Utterance utterance = JCasUtil.selectPreceding(Utterance.class, hAnno, 1).get(0);
			utterance.setEnd(hAnno.getEnd());
		}

		annotateSpeech(jcas, mainMatter);

		// aggregating annotations
		// TODO: convert to range function
		int currentSceneBegin = -1;
		int currentActBegin = -1;
		for (XMLElement anno : JCasUtil.select(jcas, XMLElement.class)) {
			if (anno.getTag().equals("h2") && !anno.getCls().contains("author")) {
				AnnotationFactory.createAnnotation(jcas, anno.getBegin(), anno.getEnd(), SceneHeading.class);
				if (currentSceneBegin >= 0) {
					AnnotationFactory.createAnnotation(jcas, currentSceneBegin, anno.getBegin() - 1, Scene.class);
				}
				currentSceneBegin = anno.getBegin();
			}
			if (anno.getTag().equals("h1") && !anno.getCls().contains("title")) {
				AnnotationFactory.createAnnotation(jcas, anno.getBegin(), anno.getEnd(), ActHeading.class);
				if (currentActBegin >= 0) {
					AnnotationFactory.createAnnotation(jcas, currentActBegin, anno.getBegin() - 1, Act.class);
					if (currentSceneBegin >= 0) {
						AnnotationFactory.createAnnotation(jcas, currentSceneBegin, anno.getBegin() - 1, Scene.class);
						currentSceneBegin = -1;
					}
				}
				currentActBegin = anno.getBegin();
			}
		}
		if (currentActBegin >= 0) {
			AnnotationFactory.createAnnotation(jcas, currentActBegin, mainMatter.getEnd(), Act.class);
		}
		if (currentSceneBegin >= 0) {
			AnnotationFactory.createAnnotation(jcas, currentSceneBegin, mainMatter.getEnd(), Scene.class);
		}
		AnnotationUtil.trim(new ArrayList<Scene>(JCasUtil.select(jcas, Scene.class)));
		AnnotationUtil.trim(new ArrayList<Act>(JCasUtil.select(jcas, Act.class)));

		DramaIOUtil.cleanUp(jcas);

	}

	protected void annotateSpeech(JCas jcas, Annotation mainMatter) {
		for (Utterance utterance : JCasUtil.selectCovered(Utterance.class, mainMatter)) {
			TreeSet<Annotation> except = new TreeSet<Annotation>(new Comparator<Annotation>() {

				@Override
				public int compare(Annotation o1, Annotation o2) {
					return Integer.compare(o1.getBegin(), o2.getBegin());
				}

			});
			except.addAll(JCasUtil.selectCovered(StageDirection.class, utterance));
			except.addAll(JCasUtil.selectCovered(Speaker.class, utterance));
			except.addAll(JCasUtil.selectCovered(Footnote.class, utterance));
			int b = utterance.getBegin();
			for (Annotation exc : except) {
				if (exc.getBegin() > b) {
					AnnotationUtil.trim(AnnotationFactory.createAnnotation(jcas, b, exc.getBegin(), Speech.class));
				}
				b = exc.getEnd();
			}
			if (b < utterance.getEnd()) {
				AnnotationUtil.trim(AnnotationFactory.createAnnotation(jcas, b, utterance.getEnd(), Speech.class));
			}
		}

		for (Speech speech : new ArrayList<Speech>(JCasUtil.select(jcas, Speech.class))) {
			if (speech.getCoveredText().matches("^\\s*$")) {
				speech.removeFromIndexes();
			} else
				try {
					AnnotationUtil.trimBegin(speech, '.', ' ');
				} catch (ArrayIndexOutOfBoundsException e) {
					// ignore
				}
		}
	}

	protected void assignSpeakerIds(JCas jcas) {
		DramatisPersonae dp = JCasUtil.selectSingle(jcas, DramatisPersonae.class);

		int speakerId = 1;
		Map<String, Speaker> speakerMap = new HashMap<String, Speaker>();
		for (Speaker speaker : JCasUtil.selectCovered(Speaker.class, dp)) {
			speaker.setId(speakerId++);
			speakerMap.put(speaker.getCoveredText(), speaker);
		}
		;

		for (Speaker speaker : JCasUtil.select(jcas, Speaker.class)) {
			if (speaker.getId() == 0) {
				try {
					speaker.setId(speakerMap.get(speaker.getCoveredText()).getId());
				} catch (NullPointerException e) {
					// no entry in speaker map
				}
			}
		}
	}

}
