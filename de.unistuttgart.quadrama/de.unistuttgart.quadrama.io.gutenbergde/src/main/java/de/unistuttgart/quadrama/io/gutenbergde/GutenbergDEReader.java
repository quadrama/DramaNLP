package de.unistuttgart.quadrama.io.gutenbergde;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

import de.unistuttgart.ims.uimautil.IMSUtil;
import de.unistuttgart.quadrama.api.Act;
import de.unistuttgart.quadrama.api.ActHeading;
import de.unistuttgart.quadrama.api.Drama;
import de.unistuttgart.quadrama.api.DramatisPersonae;
import de.unistuttgart.quadrama.api.Figure;
import de.unistuttgart.quadrama.api.Footnote;
import de.unistuttgart.quadrama.api.FrontMatter;
import de.unistuttgart.quadrama.api.MainMatter;
import de.unistuttgart.quadrama.api.Scene;
import de.unistuttgart.quadrama.api.SceneHeading;
import de.unistuttgart.quadrama.api.Speaker;
import de.unistuttgart.quadrama.api.Speech;
import de.unistuttgart.quadrama.api.StageDirection;
import de.unistuttgart.quadrama.api.Utterance;
import de.unistuttgart.quadrama.io.core.AbstractDramaReader;
import de.unistuttgart.quadrama.io.core.type.HTMLAnnotation;

public class GutenbergDEReader extends AbstractDramaReader {

	@Override
	public void getNext(JCas jcas) throws IOException, CollectionException {
		File file = files[current++];

		Drama drama = new Drama(jcas);
		drama.setDocumentId(file.getName());
		drama.addToIndexes();
		jcas.setDocumentLanguage(language);

		getLogger().debug("Processing file " + file.getAbsolutePath());

		String str = IOUtils.toString(new FileInputStream(file));
		org.jsoup.nodes.Document doc = Jsoup.parseBodyFragment(str);
		Visitor vis = new Visitor(jcas);
		doc.traverse(vis);
		jcas = vis.getJCas();
		Map<String, HTMLAnnotation> annoMap = vis.getAnnotationMap();

		// identify front and main matter
		select2Annotation(jcas, doc, annoMap, "div.gutenb:eq(0)",
				FrontMatter.class, null);
		selectRange2Annotation(jcas, doc, annoMap, "div.gutenb:eq(1)",
				"div.gutenb:last-child", MainMatter.class);
		FrontMatter frontMatter =
				JCasUtil.selectSingle(jcas, FrontMatter.class);
		MainMatter mainMatter = JCasUtil.selectSingle(jcas, MainMatter.class);

		// identify simple annotations
		select2Annotation(jcas, doc, annoMap, "span.speaker", Speaker.class,
				mainMatter);
		select2Annotation(jcas, doc, annoMap, "span.speaker", Figure.class,
				frontMatter);
		select2Annotation(jcas, doc, annoMap, "span.regie",
				StageDirection.class, mainMatter);
		select2Annotation(jcas, doc, annoMap, "span.footnote", Footnote.class,
				mainMatter);
		select2Annotation(jcas, doc, annoMap, "h3 + p", DramatisPersonae.class,
				frontMatter);

		select2Annotation(jcas, doc, annoMap, "p:has(span.speaker)",
				Utterance.class, mainMatter);

		annotateSpeech(jcas, mainMatter);

		// aggregating annotations
		// TODO: convert to range function
		int currentSceneBegin = -1;
		int currentActBegin = -1;
		for (HTMLAnnotation anno : JCasUtil.select(jcas, HTMLAnnotation.class)) {
			if (anno.getTag().equals("h2") && !anno.getCls().contains("author")) {
				AnnotationFactory.createAnnotation(jcas, anno.getBegin(),
						anno.getEnd(), SceneHeading.class);
				if (currentSceneBegin >= 0) {
					AnnotationFactory.createAnnotation(jcas, currentSceneBegin,
							anno.getBegin() - 1, Scene.class);
				}
				currentSceneBegin = anno.getBegin();
			}
			if (anno.getTag().equals("h1") && !anno.getCls().contains("title")) {
				AnnotationFactory.createAnnotation(jcas, anno.getBegin(),
						anno.getEnd(), ActHeading.class);
				if (currentActBegin >= 0) {
					AnnotationFactory.createAnnotation(jcas, currentActBegin,
							anno.getBegin() - 1, Act.class);
				}
				currentActBegin = anno.getBegin();
			}
		}
		if (currentActBegin >= 0) {
			AnnotationFactory.createAnnotation(jcas, currentActBegin,
					mainMatter.getEnd(), Act.class);
		}
		if (currentSceneBegin >= 0) {
			AnnotationFactory.createAnnotation(jcas, currentSceneBegin,
					mainMatter.getEnd(), Scene.class);
		}
		IMSUtil.trim(new ArrayList<Scene>(JCasUtil.select(jcas, Scene.class)));
		IMSUtil.trim(new ArrayList<Act>(JCasUtil.select(jcas, Act.class)));

		this.cleanUp(jcas);

	}

	protected void annotateSpeech(JCas jcas, Annotation mainMatter) {
		for (Utterance utterance : JCasUtil.selectCovered(Utterance.class,
				mainMatter)) {
			TreeSet<Annotation> except =
					new TreeSet<Annotation>(new Comparator<Annotation>() {

						public int compare(Annotation o1, Annotation o2) {
							return Integer.compare(o1.getBegin(), o2.getBegin());
						}

					});
			except.addAll(JCasUtil.selectCovered(StageDirection.class,
					utterance));
			except.addAll(JCasUtil.selectCovered(Speaker.class, utterance));
			except.addAll(JCasUtil.selectCovered(Footnote.class, utterance));
			int b = utterance.getBegin();
			for (Annotation exc : except) {
				if (exc.getBegin() > b) {
					IMSUtil.trim(AnnotationFactory.createAnnotation(jcas, b,
							exc.getBegin(), Speech.class));
				}
				b = exc.getEnd();
			}
			if (b < utterance.getEnd()) {
				IMSUtil.trim(AnnotationFactory.createAnnotation(jcas, b,
						utterance.getEnd(), Speech.class));
			}
		}
	}

	protected void assignSpeakerIds(JCas jcas) {
		DramatisPersonae dp =
				JCasUtil.selectSingle(jcas, DramatisPersonae.class);

		int speakerId = 1;
		Map<String, Speaker> speakerMap = new HashMap<String, Speaker>();
		for (Speaker speaker : JCasUtil.selectCovered(Speaker.class, dp)) {
			speaker.setId(speakerId++);
			speakerMap.put(speaker.getCoveredText(), speaker);
		};

		for (Speaker speaker : JCasUtil.select(jcas, Speaker.class)) {
			if (speaker.getId() == 0) {
				try {
					speaker.setId(speakerMap.get(speaker.getCoveredText())
							.getId());
				} catch (NullPointerException e) {
					// no entry in speaker map
				}
			}
		}
	}

}
