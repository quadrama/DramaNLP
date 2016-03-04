package de.unistuttgart.quadrama.io.tei.textgrid;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import de.unistuttgart.ims.uimautil.IMSUtil;
import de.unistuttgart.quadrama.api.Act;
import de.unistuttgart.quadrama.api.ActHeading;
import de.unistuttgart.quadrama.api.Drama;
import de.unistuttgart.quadrama.api.DramatisPersonae;
import de.unistuttgart.quadrama.api.Figure;
import de.unistuttgart.quadrama.api.FigureDescription;
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

public class TextgridTEIReader extends AbstractDramaReader {

	@Override
	public void getNext(JCas jcas) throws IOException, CollectionException {
		File file = files[current++];
		Drama drama = new Drama(jcas);
		drama.addToIndexes();
		drama.setDocumentId(file.getName());

		jcas.setDocumentLanguage(language);

		String str = IOUtils.toString(new FileInputStream(file));
		Document doc = Jsoup.parse(str, "", Parser.xmlParser());

		Visitor vis = new Visitor(jcas);

		Element root = doc.select("TEI > text").first();
		root.traverse(vis);
		jcas = vis.getJCas();

		select2Annotation(jcas, root, vis.getAnnotationMap(), "front",
				FrontMatter.class, null);
		select2Annotation(jcas, root, vis.getAnnotationMap(), "body",
				MainMatter.class, null);

		MainMatter mainMatter = JCasUtil.selectSingle(jcas, MainMatter.class);

		select2Annotation(jcas, root, vis.getAnnotationMap(), "speaker",
				Speaker.class, null);
		select2Annotation(jcas, root, vis.getAnnotationMap(), "stage",
				StageDirection.class, mainMatter);
		select2Annotation(jcas, root, vis.getAnnotationMap(), "sp",
				Utterance.class, null);
		select2Annotation(jcas, root, vis.getAnnotationMap(), "l",
				Speech.class, mainMatter);
		select2Annotation(jcas, root, vis.getAnnotationMap(), "ab",
				Speech.class, mainMatter);
		select2Annotation(jcas, root, vis.getAnnotationMap(), "p",
				Speech.class, mainMatter);

		readActsAndScenes(jcas, root, vis.getAnnotationMap());
		readDramatisPersonae(jcas, root, vis.getAnnotationMap());

		fixSpeakerAnnotations(jcas);

		cleanUp(jcas);

	}

	private void readActsAndScenes(JCas jcas, Element root,
			Map<String, HTMLAnnotation> map) {

		if (!root.select("div[type=scene]").isEmpty()) {
			select2Annotation(jcas, root, map, "div[type=scene]", Scene.class,
					null);
			select2Annotation(jcas, root, map,
					"div[type=scene] > div > desc > title", SceneHeading.class,
					null);
		}
		if (!root.select("div[type=act]").isEmpty()) {
			select2Annotation(jcas, root, map, "div[type=act]", Act.class, null);
			select2Annotation(jcas, root, map,
					"div[type=act] > div > desc > title", ActHeading.class,
					null);
		}
		if (!JCasUtil.exists(jcas, Act.class)) {
			select2Annotation(jcas, root, map, "body > div", Act.class, null);
		}
		if (!JCasUtil.exists(jcas, Scene.class)) {
			select2Annotation(jcas, root, map, "div > div", Scene.class, null);
		}
	}

	private void readDramatisPersonae(JCas jcas, Element root,
			Map<String, HTMLAnnotation> map) {
		DramatisPersonae dp;
		if (!root.select("castList").isEmpty()) {
			dp =
					select2Annotation(jcas, root, map, "castList",
							DramatisPersonae.class, null).iterator().next();

			Element castList = root.select("castList").first();
			if (!castList.select("castItem role").isEmpty()) {
				select2Annotation(jcas, castList, map, "castItem role",
						Figure.class, dp);
				Collection<FigureDescription> figDescs =
						select2Annotation(jcas, castList, map,
								"castItem roleDesc", FigureDescription.class,
								dp);
				for (FigureDescription figureDescription : figDescs) {
					Figure fig =
							JCasUtil.selectPreceding(Figure.class,
									figureDescription, 1).get(0);
					fig.setDescription(figureDescription);
				}
			} else {
				select2Annotation(jcas, castList, map, "castItem",
						Figure.class, null);
			}
		} else {
			dp =
					select2Annotation(jcas, root, map,
							"div[type=front] > div:has(p)",
							DramatisPersonae.class, null).iterator().next();

			IMSUtil.trim(select2Annotation(jcas, root, map, "p", Figure.class,
					dp));
			fixFigureAnnotations(jcas);
		}
	}

	private void fixFigureAnnotations(JCas jcas) {
		for (Figure figure : new HashSet<Figure>(JCasUtil.select(jcas,
				Figure.class))) {
			String s = figure.getCoveredText();
			if (s.contains(",")) {
				int oldEnd = figure.getEnd();
				int i = s.indexOf(',');
				figure.setEnd(figure.getBegin() + i);

				FigureDescription fd =
						IMSUtil.trim(AnnotationFactory.createAnnotation(jcas,
								figure.getEnd() + 1, oldEnd,
								FigureDescription.class));
				figure.setDescription(fd);

			}

		}

	}

	private void fixSpeakerAnnotations(JCas jcas) {
		for (Speaker speaker : new HashSet<Speaker>(JCasUtil.select(jcas,
				Speaker.class))) {
			IMSUtil.trim(speaker, '.', ' ', '\t', '\n', '\r', '\f');
		}

	}
}
