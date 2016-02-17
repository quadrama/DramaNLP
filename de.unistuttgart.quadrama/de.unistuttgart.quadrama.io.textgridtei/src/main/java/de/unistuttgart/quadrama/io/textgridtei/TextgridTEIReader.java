package de.unistuttgart.quadrama.io.textgridtei;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import de.unistuttgart.quadrama.api.Act;
import de.unistuttgart.quadrama.api.Drama;
import de.unistuttgart.quadrama.api.DramatisPersonae;
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
		drama.setDocumentId("testtei");
		drama.addToIndexes();
		jcas.setDocumentLanguage("de");

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
		select2Annotation(jcas, root, vis.getAnnotationMap(), "body > div",
				Act.class, null);
		select2Annotation(jcas, root, vis.getAnnotationMap(),
				"front div:has(p)", DramatisPersonae.class, frontMatter);

		cleanUp(jcas);
	}
}
