package de.unistuttgart.quadrama.io.tei.textgrid;

import static de.unistuttgart.quadrama.io.core.DramaIOUtil.select2Annotation;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.resource.ResourceInitializationException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import de.unistuttgart.ims.drama.api.Act;
import de.unistuttgart.ims.drama.api.Author;
import de.unistuttgart.ims.drama.api.CastFigure;
import de.unistuttgart.ims.drama.api.Drama;
import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.ims.drama.api.FrontMatter;
import de.unistuttgart.ims.drama.api.MainMatter;
import de.unistuttgart.ims.drama.api.Scene;
import de.unistuttgart.ims.drama.api.Speaker;
import de.unistuttgart.ims.drama.api.Speech;
import de.unistuttgart.ims.drama.api.StageDirection;
import de.unistuttgart.ims.drama.api.Translator;
import de.unistuttgart.ims.drama.api.Utterance;
import de.unistuttgart.ims.uimautil.AnnotationUtil;
import de.unistuttgart.quadrama.io.core.AbstractDramaUrlReader;
import de.unistuttgart.quadrama.io.core.Select2AnnotationCallback;
import de.unistuttgart.quadrama.io.core.Visitor;

public class GerDraCorUrlReader extends AbstractDramaUrlReader {

	public static final String PARAM_STRICT = "strict";

	public static final String PARAM_TEI_COMPAT = "TEI compatibility";

	@ConfigurationParameter(name = PARAM_STRICT, mandatory = false, defaultValue = "false")
	boolean strict = false;

	@ConfigurationParameter(name = PARAM_TEI_COMPAT, mandatory = false, defaultValue = "false")
	boolean teiCompatibility = false;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
	}

	@Override
	public void getNext(final JCas jcas, InputStream file, Drama drama) throws IOException, CollectionException {
		Document doc = Jsoup.parse(file, "UTF-8", "", Parser.xmlParser());

		// meta data
		drama.setDocumentTitle(doc.select("titleStmt > title").first().text());
		if (!doc.select("sourceDesc > bibl > idno[type=\"URL\"]").isEmpty())
			drama.setDocumentId(doc.select("sourceDesc > bibl > idno[type=\"URL\"]").first().text().substring(36));

		// Author
		Elements authorElements = doc.select("author");
		for (int i = 0; i < authorElements.size(); i++) {
			Element authorElement = authorElements.get(i);
			Author author = new Author(jcas);
			author.setName(authorElement.text());
			if (authorElement.hasAttr("key")) {
				author.setPnd(authorElement.attr("key").replace("pnd:", ""));
			}
			author.addToIndexes();
		}
		// translator
		Elements editorElements = doc.select("editor[@role='translator']");
		for (int i = 0; i < editorElements.size(); i++) {
			Element editorElement = editorElements.get(i);
			Translator transl = new Translator(jcas);
			transl.setName(editorElement.text());
			if (editorElement.hasAttr("key"))
				transl.setPnd(editorElement.attr("key").replace("pnd:", ""));
		}

		// dates
		try {
			drama.setDatePrinted(Integer.valueOf(doc.select("date[type=\"print\"]").attr("when")));
		} catch (Exception e) {
			// fail silently
		}
		try {
			drama.setDateWritten(Integer.valueOf(doc.select("date[type=\"written\"]").attr("when")));
		} catch (Exception e) {
			// fail silently
		}
		try {
			drama.setDatePremiere(Integer.valueOf(doc.select("date[type=\"premiere\"]").attr("when")));
		} catch (Exception e) {
			// fail silently
		}
		Visitor vis = new Visitor(jcas, this.teiCompatibility);

		Element root;
		if (teiCompatibility)
			root = doc;
		else
			root = doc.select("TEI > text").first();

		root.traverse(vis);
		vis.getJCas();

		select2Annotation(jcas, root, vis.getAnnotationMap(), "front", FrontMatter.class, null);
		select2Annotation(jcas, root, vis.getAnnotationMap(), "body", MainMatter.class, null);

		MainMatter mainMatter = JCasUtil.selectSingle(jcas, MainMatter.class);

		select2Annotation(jcas, root, vis.getAnnotationMap(), "speaker", Speaker.class, null);
		select2Annotation(jcas, root, vis.getAnnotationMap(), "stage", StageDirection.class, mainMatter);
		select2Annotation(jcas, root, vis.getAnnotationMap(), "sp", Utterance.class, null,
				new Select2AnnotationCallback<Utterance>() {
					@Override
					public void call(Utterance annotation, Element xmlElement) {
						Collection<Speaker> speakers = JCasUtil.selectCovered(Speaker.class, annotation);
						for (Speaker sp : speakers) {
							String[] whos = xmlElement.attr("who").split(" ");
							sp.setXmlId(new StringArray(jcas, whos.length));
							for (int i = 0; i < whos.length; i++)
								sp.setXmlId(i, whos[i].substring(1));
						}
					}
				});
		select2Annotation(jcas, root, vis.getAnnotationMap(), "l", Speech.class, mainMatter);
		select2Annotation(jcas, root, vis.getAnnotationMap(), "ab", Speech.class, mainMatter);
		select2Annotation(jcas, root, vis.getAnnotationMap(), "p", Speech.class, mainMatter);

		TextGridUtil.readActsAndScenes(jcas, root, vis.getAnnotationMap(), true);
		TextGridUtil.readDramatisPersonae(jcas, root, vis.getAnnotationMap());

		readCast(jcas, drama, doc);

		AnnotationUtil.trim(new ArrayList<Figure>(JCasUtil.select(jcas, Figure.class)));
		AnnotationUtil.trim(new ArrayList<Speech>(JCasUtil.select(jcas, Speech.class)));
		AnnotationUtil.trim(new ArrayList<Utterance>(JCasUtil.select(jcas, Utterance.class)));
		AnnotationUtil.trim(new ArrayList<Scene>(JCasUtil.select(jcas, Scene.class)));
		AnnotationUtil.trim(new ArrayList<Act>(JCasUtil.select(jcas, Act.class)));

	}

	private static void readCast(JCas jcas, Drama drama, Document doc) {
		Map<String, CastFigure> idFigureMap = new HashMap<String, CastFigure>();
		Elements castEntries = doc.select("profileDesc > particDesc > listPerson > person");
		castEntries.addAll(doc.select("profileDesc > particDesc > listPerson > personGrp"));
		FSArray castListArray = new FSArray(jcas, castEntries.size());
		for (int i = 0; i < castEntries.size(); i++) {
			Element castEntry = castEntries.get(i);
			CastFigure figure = TEIUtil.parsePersonElement(jcas, castEntry);

			for (int j = 0; j < figure.getXmlId().size(); j++) {
				idFigureMap.put(figure.getXmlId(j), figure);
			}
			castListArray.set(i, figure);
		}
		drama.setCastList(castListArray);

		for (Speaker speaker : JCasUtil.select(jcas, Speaker.class)) {
			speaker.setCastFigure(new FSArray(jcas, speaker.getXmlId().size()));
			for (int i = 0; i < speaker.getXmlId().size(); i++)
				speaker.setCastFigure(i, idFigureMap.get(speaker.getXmlId(i)));
		}
	}

}
