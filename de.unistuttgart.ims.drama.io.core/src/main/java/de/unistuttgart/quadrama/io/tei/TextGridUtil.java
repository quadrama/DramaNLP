package de.unistuttgart.quadrama.io.tei;

import static de.unistuttgart.quadrama.io.core.DramaIOUtil.select2Annotation;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import de.unistuttgart.ims.drama.api.Act;
import de.unistuttgart.ims.drama.api.ActHeading;
import de.unistuttgart.ims.drama.api.Author;
import de.unistuttgart.ims.drama.api.Drama;
import de.unistuttgart.ims.drama.api.DramatisPersonae;
import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.ims.drama.api.FigureDescription;
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

@Deprecated
public class TextGridUtil {

	public static void getNext(JCas jcas, InputStream file, Drama drama, boolean strict)
			throws IOException, CollectionException {

		Document doc = Jsoup.parse(file, "UTF-8", "", Parser.xmlParser());

		// meta data
		drama.setDocumentTitle(doc.select("titleStmt > title").first().text());
		if (!doc.select("idno[type=\"TextGridUri\"]").isEmpty())
			drama.setDocumentId(doc.select("idno[type=\"TextGridUri\"]").first().text().substring(9));

		// Author
		Elements authorElements = doc.select("author");
		for (int i = 0; i < authorElements.size(); i++) {
			Element authorElement = authorElements.get(i);
			Author author = new Author(jcas);
			author.setName(authorElement.text());
			if (authorElement.hasAttr("key")) {
				author.setPnd(authorElement.attr("key").replace("pnd:", "http://d-nb.info/gnd/"));
			}
			author.addToIndexes();
		}

		Visitor vis = new Visitor(jcas);

		Element root = doc.select("TEI > text").first();
		root.traverse(vis);
		jcas = vis.getJCas();

		select2Annotation(jcas, root, vis.getAnnotationMap(), "front", FrontMatter.class, null);
		select2Annotation(jcas, root, vis.getAnnotationMap(), "body", MainMatter.class, null);

		MainMatter mainMatter = JCasUtil.selectSingle(jcas, MainMatter.class);

		select2Annotation(jcas, root, vis.getAnnotationMap(), "speaker", Speaker.class, null);
		select2Annotation(jcas, root, vis.getAnnotationMap(), "stage", StageDirection.class, mainMatter);
		select2Annotation(jcas, root, vis.getAnnotationMap(), "sp", Utterance.class, null);
		select2Annotation(jcas, root, vis.getAnnotationMap(), "l", Speech.class, mainMatter);
		select2Annotation(jcas, root, vis.getAnnotationMap(), "ab", Speech.class, mainMatter);
		select2Annotation(jcas, root, vis.getAnnotationMap(), "p", Speech.class, mainMatter);

		readActsAndScenes(jcas, root, vis.getAnnotationMap(), strict);
		readDramatisPersonae(jcas, root, vis.getAnnotationMap());

		fixSpeakerAnnotations(jcas);

		AnnotationUtil.trim(new ArrayList<Figure>(JCasUtil.select(jcas, Figure.class)));
		AnnotationUtil.trim(new ArrayList<Speech>(JCasUtil.select(jcas, Speech.class)));
		AnnotationUtil.trim(new ArrayList<Utterance>(JCasUtil.select(jcas, Utterance.class)));
		AnnotationUtil.trim(new ArrayList<Scene>(JCasUtil.select(jcas, Scene.class)));
		AnnotationUtil.trim(new ArrayList<Act>(JCasUtil.select(jcas, Act.class)));

		// DramaIOUtil.cleanUp(jcas);

	}

	public static void readActs(JCas jcas, Element root, Map<String, XMLElement> map, boolean strict) {
		if (!root.select("div[type=act]").isEmpty()) {
			for (Act a : select2Annotation(jcas, root, map, "div[type=act]", Act.class, null)) {
				a.setRegular(true);
			}

			select2Annotation(jcas, root, map, "div[type=act] > div > desc > title", ActHeading.class, null);
			select2Annotation(jcas, root, map, "div[type=act] > div > head", ActHeading.class, null);

			for (Act prol : select2Annotation(jcas, root, map, "div[type=prologue]", Act.class, null)) {
				prol.setRegular(false);
			}

		}
		if (!strict && !JCasUtil.exists(jcas, Act.class)) {
			select2Annotation(jcas, root, map, "body > div", Act.class, null);
			select2Annotation(jcas, root, map, "body > div > head", ActHeading.class, null);
			select2Annotation(jcas, root, map, "body > div > desc > title", ActHeading.class, null);
		}
	}

	/**
	 * Detect scenes. The following things are checked:
	 * <ol>
	 * <li>if they are explicitly marked with <code>type=scnee</code>, we take them
	 * and return.</li>
	 * <li>if Act annotations do exist in the JCas, we search for divs that have
	 * head annotations.</li>
	 * </ol>
	 * 
	 * @param jcas
	 * @param root
	 * @param map
	 */
	public static void readScenes(JCas jcas, Element root, Map<String, XMLElement> map, boolean strict) {
		if (!root.select("div[type=scene]").isEmpty()) {
			select2Annotation(jcas, root, map, "div[type=scene]", Scene.class, null);
			select2Annotation(jcas, root, map, "div[type=scene] > div > desc > title", SceneHeading.class, null);

			for (Scene scene : JCasUtil.select(jcas, Scene.class))
				scene.setRegular(true);
		} else if (!strict) {
			if (JCasUtil.exists(jcas, Act.class))
				for (Act act : JCasUtil.select(jcas, Act.class)) {

					Collection<Scene> scenes = select2Annotation(jcas, root, map, "body > div > div:has(head)",
							Scene.class, act);
					for (Scene sc : scenes) {
						select2Annotation(jcas, root, map, "div > desc > title", SceneHeading.class, sc);
					}
				}
			else {
				select2Annotation(jcas, root, map, "body > div > div:not(:has(desc > title))", Scene.class, null);
				select2Annotation(jcas, root, map, "body > div > div > head", SceneHeading.class, null);
				select2Annotation(jcas, root, map, "body > div > div > desc > title", SceneHeading.class, null);
			}
		}
	}

	public static void readActsAndScenes(JCas jcas, Element root, Map<String, XMLElement> map, boolean strict) {
		readActs(jcas, root, map, strict);
		readScenes(jcas, root, map, strict);
	}

	public static void readDramatisPersonae(JCas jcas, Element root, Map<String, XMLElement> map) {
		DramatisPersonae dp;
		if (!root.select("castList").isEmpty()) {
			dp = select2Annotation(jcas, root, map, "castList", DramatisPersonae.class, null).iterator().next();

			Element castList = root.select("castList").first();
			if (!castList.select("castItem role").isEmpty()) {
				select2Annotation(jcas, castList, map, "castItem role", Figure.class, dp);
				Collection<FigureDescription> figDescs = select2Annotation(jcas, castList, map, "castItem roleDesc",
						FigureDescription.class, dp);
				for (FigureDescription figureDescription : figDescs) {
					Figure fig = JCasUtil.selectPreceding(Figure.class, figureDescription, 1).get(0);
					fig.setDescription(figureDescription);
				}
			} else {
				select2Annotation(jcas, castList, map, "castItem", Figure.class, null);
				// fixFigureAnnotations(jcas);
			}
		} else {
			try {
				dp = select2Annotation(jcas, root, map, "div[type=front] > div:has(p)", DramatisPersonae.class, null)
						.iterator().next();

				AnnotationUtil.trim(select2Annotation(jcas, root, map, "p", Figure.class, dp));
				// fixFigureAnnotations(jcas);
			} catch (NoSuchElementException e) {
				System.err.println("No dramatis personae annotation in drama " + Drama.get(jcas).getDocumentId());
				// e.printStackTrace();
			}
		}
	}

	public static void fixFigureAnnotations(JCas jcas) {
		for (Figure figure : new HashSet<Figure>(JCasUtil.select(jcas, Figure.class))) {
			String s = figure.getCoveredText();
			if (s.contains(",")) {
				int oldEnd = figure.getEnd();
				int i = s.indexOf(',');
				figure.setEnd(figure.getBegin() + i);

				FigureDescription fd = AnnotationUtil.trim(
						AnnotationFactory.createAnnotation(jcas, figure.getEnd() + 1, oldEnd, FigureDescription.class));
				figure.setDescription(fd);

			}

		}

	}

	public static void fixSpeakerAnnotations(JCas jcas) {
		for (Speaker speaker : new HashSet<Speaker>(JCasUtil.select(jcas, Speaker.class))) {
			AnnotationUtil.trim(speaker, '.', ' ', '\t', '\n', '\r', '\f');
		}

	}

}
