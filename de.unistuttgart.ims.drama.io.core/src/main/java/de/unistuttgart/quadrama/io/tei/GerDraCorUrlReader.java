package de.unistuttgart.quadrama.io.tei;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.CasUtil;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import de.unistuttgart.ims.drama.api.Act;
import de.unistuttgart.ims.drama.api.ActHeading;
import de.unistuttgart.ims.drama.api.Author;
import de.unistuttgart.ims.drama.api.CastFigure;
import de.unistuttgart.ims.drama.api.DiscourseEntity;
import de.unistuttgart.ims.drama.api.Drama;
import de.unistuttgart.ims.drama.api.DramatisPersonae;
import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.ims.drama.api.FrontMatter;
import de.unistuttgart.ims.drama.api.MainMatter;
import de.unistuttgart.ims.drama.api.Mention;
import de.unistuttgart.ims.drama.api.Scene;
import de.unistuttgart.ims.drama.api.SceneHeading;
import de.unistuttgart.ims.drama.api.Speaker;
import de.unistuttgart.ims.drama.api.Speech;
import de.unistuttgart.ims.drama.api.StageDirection;
import de.unistuttgart.ims.drama.api.Translator;
import de.unistuttgart.ims.drama.api.Utterance;
import de.unistuttgart.ims.drama.util.UimaUtil;
import de.unistuttgart.ims.uimautil.AnnotationUtil;
import de.unistuttgart.quadrama.io.core.AbstractDramaUrlReader;
import de.unistuttgart.quadrama.io.core.GenericXmlReader;

public class GerDraCorUrlReader extends AbstractDramaUrlReader {

	public static final String PARAM_STRICT = "strict";

	public static final String PARAM_TEI_COMPAT = "TEI compatibility";

	@ConfigurationParameter(name = PARAM_STRICT, mandatory = false, defaultValue = "false")
	boolean strict = false;

	@ConfigurationParameter(name = PARAM_TEI_COMPAT, mandatory = false, defaultValue = "false")
	boolean teiCompatibility = false;

	@Override
	public void getNext(final JCas jcas, InputStream file, Drama drama) throws IOException, CollectionException {

		GenericXmlReader gxr = new GenericXmlReader();
		gxr.setTextRootSelector(teiCompatibility ? null : "TEI > text");
		gxr.setPreserveWhitespace(teiCompatibility);

		// title
		gxr.addAction("titleStmt > title:first-child", Drama.class, (d, e) -> d.setDocumentTitle(e.text()));

		// id
		gxr.addAction("sourceDesc > bibl > idno[type=URL]", Drama.class,
				(d, e) -> d.setDocumentId(e.text().substring(36)));

		// author
		gxr.addAction("author", (jc, e) -> {
			Author author = new Author(jcas);
			author.setName(e.text());
			if (e.hasAttr("key")) {
				author.setPnd(e.attr("key").replace("pnd:", ""));
			}
			author.addToIndexes();
		});

		// translator
		gxr.addAction("editor[role=translator]", (j, e) -> {
			Translator transl = new Translator(jcas);
			transl.setName(e.text());
			if (e.hasAttr("key"))
				transl.setPnd(e.attr("key").replace("pnd:", ""));
		});

		// date printed
		gxr.addAction("date[type=print][when]", Drama.class,
				(d, e) -> d.setDatePrinted(Integer.valueOf(e.attr("when"))));

		// date written
		gxr.addAction("date[type=written][when]", Drama.class,
				(d, e) -> d.setDateWritten(Integer.valueOf(e.attr("when"))));

		// date premiere
		gxr.addAction("date[type=premiere][when]", Drama.class,
				(d, e) -> d.setDatePremiere(Integer.valueOf(e.attr("when"))));

		gxr.addMapping("front", FrontMatter.class);
		gxr.addMapping("body", MainMatter.class);

		// Segmentation
		gxr.addMapping("div[type=prologue]", Act.class, (a, e) -> a.setRegular(false));

		gxr.addMapping("div[type=act]", Act.class, (a, e) -> a.setRegular(true));
		gxr.addMapping("div[type=act] > div > desc > title", ActHeading.class);
		gxr.addMapping("div[type=act] > div > head", ActHeading.class);

		gxr.addMapping("div[type=scene]", Scene.class, (a, e) -> a.setRegular(true));
		gxr.addMapping("div[type=scene] > div > desc > title", SceneHeading.class);

		// Dramatis Personae
		gxr.addMapping("body castList castItem", Figure.class);
		gxr.addMapping("div[type=Dramatis_Personae]", DramatisPersonae.class);
		Map<String, String> xmlAlias = new HashMap<String, String>();
		gxr.addDocumentMapping("particDesc > listPerson > person", CastFigure.class, (cf, e) -> {
			List<String> nameList = new LinkedList<String>();
			List<String> xmlIdList = new LinkedList<String>();

			if (e.hasAttr("xml:id"))
				xmlIdList.add(e.attr("xml:id"));
			if (e.hasAttr("sex"))
				cf.setGender(e.attr("sex"));
			if (e.hasAttr("age"))
				cf.setAge(e.attr("age"));

			// gather names
			Elements nameElements = e.select("persName");

			for (int j = 0; j < nameElements.size(); j++) {
				nameList.add(nameElements.get(j).text());
				if (nameElements.get(j).hasAttr("xml:id")) {
					xmlIdList.add(nameElements.get(j).attr("xml:id"));
					xmlAlias.put(nameElements.get(j).attr("xml:id"), e.attr("xml:id"));
				}
			}
			for (TextNode tn : e.textNodes()) {
				if (tn.text().trim().length() > 0)
					nameList.add(tn.text().trim());
			}
			cf.setXmlId(UimaUtil.toStringArray(jcas, xmlIdList));
			cf.setNames(UimaUtil.toStringArray(jcas, nameList));
			cf.setDisplayName(cf.getNames(0));

		});

		gxr.addMapping("speaker", Speaker.class);
		gxr.addMapping("stage", StageDirection.class);
		gxr.addMapping("l", Speech.class);
		gxr.addMapping("p", Speech.class);
		gxr.addMapping("ab", Speech.class);
		gxr.addMapping("sp", Utterance.class, (u, e) -> {
			Collection<Speaker> speakers = JCasUtil.selectCovered(Speaker.class, u);
			for (Speaker sp : speakers) {
				String[] whos = e.attr("who").split(" ");
				sp.setXmlId(new StringArray(jcas, whos.length));
				sp.setCastFigure(new FSArray(jcas, whos.length));
				for (int i = 0; i < whos.length; i++) {
					String xmlid = whos[i].substring(1);
					sp.setXmlId(i, xmlid);
					if (xmlAlias.containsKey(xmlid))
						xmlid = xmlAlias.get(xmlid);
					if (gxr.exists(xmlid)) {
						sp.setCastFigure(i, (CastFigure) gxr.getAnnotation(xmlid).getValue());
						u.setCastFigure((CastFigure) gxr.getAnnotation(xmlid).getValue());
					}
				}
			}
		});

		gxr.addMapping("text *[xml:id]", DiscourseEntity.class, (de, e) -> de.setDisplayName(e.attr("xml:id")));

		gxr.addMapping("text *[xml:id]", Mention.class, (m, e) -> {
			String id = e.attr("xml:id");
			FSArray arr = new FSArray(jcas, 1);
			arr.addToIndexes();
			m.setEntity(arr);
			m.setEntity(0, (DiscourseEntity) gxr.getAnnotation(id).getValue());
		});

		Map<String, DiscourseEntity> fallbackEntities = new HashMap<String, DiscourseEntity>();
		// mentions
		gxr.addMapping("text *[ref]", Mention.class, (cl, e) -> {
			String[] splitted = e.attr("ref").split(" ");
			FSArray arr = new FSArray(jcas, splitted.length);
			for (int i = 0; i < splitted.length; i++) {
				String xmlId = splitted[i].substring(1);

				DiscourseEntity de = null;
				if (gxr.exists(xmlId)) {
					FeatureStructure fs = gxr.getAnnotation(xmlId).getValue();
					if (fs instanceof DiscourseEntity)
						de = (DiscourseEntity) fs;
				}
				if (fallbackEntities.containsKey(xmlId))
					de = fallbackEntities.get(xmlId);
				if (de == null) {
					de = cl.getCAS().createFS(CasUtil.getType(cl.getCAS(), DiscourseEntity.class));
					de.addToIndexes();
					de.setDisplayName(cl.getCoveredText());
					fallbackEntities.put(xmlId, de);
				}
				arr.set(i, de);
			}
			cl.setEntity(arr);
		});

		gxr.read(jcas, file);

		AnnotationUtil.trim(new ArrayList<Figure>(JCasUtil.select(jcas, Figure.class)));
		AnnotationUtil.trim(new ArrayList<Speech>(JCasUtil.select(jcas, Speech.class)));
		AnnotationUtil.trim(new ArrayList<Utterance>(JCasUtil.select(jcas, Utterance.class)));
		AnnotationUtil.trim(new ArrayList<Scene>(JCasUtil.select(jcas, Scene.class)));
		AnnotationUtil.trim(new ArrayList<Act>(JCasUtil.select(jcas, Act.class)));
		AnnotationUtil.trim(new ArrayList<StageDirection>(JCasUtil.select(jcas, StageDirection.class)));

	}
}
