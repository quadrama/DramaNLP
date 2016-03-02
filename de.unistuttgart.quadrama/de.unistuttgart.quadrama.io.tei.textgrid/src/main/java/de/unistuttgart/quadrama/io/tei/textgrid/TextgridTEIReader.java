package de.unistuttgart.quadrama.io.tei.textgrid;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.io.IOUtils;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import de.unistuttgart.quadrama.api.Act;
import de.unistuttgart.quadrama.api.Drama;
import de.unistuttgart.quadrama.api.DramatisPersonae;
import de.unistuttgart.quadrama.api.Figure;
import de.unistuttgart.quadrama.api.FigureDescription;
import de.unistuttgart.quadrama.api.FrontMatter;
import de.unistuttgart.quadrama.api.MainMatter;
import de.unistuttgart.quadrama.api.Scene;
import de.unistuttgart.quadrama.api.Speaker;
import de.unistuttgart.quadrama.api.SpeechProse;
import de.unistuttgart.quadrama.api.SpeechVerse;
import de.unistuttgart.quadrama.api.StageDirection;
import de.unistuttgart.quadrama.api.Utterance;
import de.unistuttgart.quadrama.io.core.AbstractDramaReader;

public class TextgridTEIReader extends AbstractDramaReader {

	@Override
	public void getNext(JCas jcas) throws IOException, CollectionException {
		Drama drama = new Drama(jcas);
		drama.setDocumentId("testtei" + current);
		drama.addToIndexes();
		jcas.setDocumentLanguage(language);

		File file = files[current++];

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

		FrontMatter frontMatter =
				JCasUtil.selectSingle(jcas, FrontMatter.class);
		MainMatter mainMatter = JCasUtil.selectSingle(jcas, MainMatter.class);

		select2Annotation(jcas, root, vis.getAnnotationMap(), "speaker",
				Speaker.class, null);
		select2Annotation(jcas, root, vis.getAnnotationMap(), "stage",
				StageDirection.class, mainMatter);
		select2Annotation(jcas, root, vis.getAnnotationMap(), "sp",
				Utterance.class, null);
		select2Annotation(jcas, root, vis.getAnnotationMap(), "l",
				SpeechVerse.class, mainMatter);
		select2Annotation(jcas, root, vis.getAnnotationMap(), "p",
				SpeechProse.class, mainMatter);
		select2Annotation(jcas, root, vis.getAnnotationMap(),
				"div[type=scene]", Scene.class, null);
		if (!JCasUtil.exists(jcas, Scene.class)) {
			select2Annotation(jcas, root, vis.getAnnotationMap(), "div > div",
					Scene.class, null);

		}
		select2Annotation(jcas, root, vis.getAnnotationMap(), "body > div",
				Act.class, null);
		select2Annotation(jcas, root, vis.getAnnotationMap(), "castList",
				DramatisPersonae.class, null);
		if (!JCasUtil.exists(jcas, DramatisPersonae.class))
			select2Annotation(jcas, root, vis.getAnnotationMap(),
					"div[type=front] > div:has(p)", DramatisPersonae.class,
					frontMatter);

		// Detecting figure declarations
		if (!root.select("castList").isEmpty()) {

			select2Annotation(jcas, root, vis.getAnnotationMap(),
					"castList castItem role", Figure.class, null);
			Collection<FigureDescription> figDescs =
					select2Annotation(jcas, root, vis.getAnnotationMap(),
							"castList castItem roleDesc",
							FigureDescription.class, null);
			for (FigureDescription figureDescription : figDescs) {
				Figure fig =
						JCasUtil.selectPreceding(Figure.class,
								figureDescription, 1).get(0);
				fig.setDescription(figureDescription);
			}
		} else if (JCasUtil.exists(jcas, DramatisPersonae.class)) {
			select2Annotation(jcas, root, vis.getAnnotationMap(), "p",
					Figure.class,
					JCasUtil.selectSingle(jcas, DramatisPersonae.class));
			fixFigureAnnotations(jcas);
		}
		fixSpeakerAnnotations(jcas);

		cleanUp(jcas);
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
						AnnotationFactory.createAnnotation(jcas,
								figure.getEnd() + 1, oldEnd,
								FigureDescription.class);
				figure.setDescription(fd);

			}
			while (figure.getCoveredText().startsWith(" ")) {
				figure.setBegin(figure.getBegin() + 1);
			}
		}

	}

	private void fixSpeakerAnnotations(JCas jcas) {
		for (Speaker speaker : new HashSet<Speaker>(JCasUtil.select(jcas,
				Speaker.class))) {
			String s = speaker.getCoveredText();
			if (s.endsWith(".")) {
				speaker.setEnd(speaker.getEnd() - 1);
			}
		}

	}
}
