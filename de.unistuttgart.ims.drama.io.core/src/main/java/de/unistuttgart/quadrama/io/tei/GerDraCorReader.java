package de.unistuttgart.quadrama.io.tei;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import de.unistuttgart.ims.uima.io.xml.ArrayUtil;
import de.unistuttgart.ims.uima.io.xml.GenericXmlReader;
import de.unistuttgart.ims.uimautil.AnnotationUtil;
import de.unistuttgart.quadrama.io.core.AbstractDramaUrlReader;

public class GerDraCorReader extends AbstractDramaUrlReader {

	public static final String PARAM_STRICT = "strict";

	public static final String PARAM_TEI_COMPAT = "TEI compatibility";

	@ConfigurationParameter(name = PARAM_STRICT, mandatory = false, defaultValue = "false")
	boolean strict = false;

	@ConfigurationParameter(name = PARAM_TEI_COMPAT, mandatory = false, defaultValue = "false")
	boolean teiCompatibility = false;

	@Override
	public void getNext(final JCas jcas, InputStream file, Drama drama) throws IOException, CollectionException {

		GenericXmlReader<Drama> gxr = new GenericXmlReader<Drama>(Drama.class);
		gxr.setTextRootSelector(teiCompatibility ? null : "TEI > text");
		gxr.setPreserveWhitespace(teiCompatibility);

		// title
		// gxr.addAction("titleStmt > title:first-child", Drama.class, (d, e) ->
		// d.setDocumentTitle(e.text()));

		gxr.addGlobalRule("titleStmt > title:first-child", (d, e) -> d.setDocumentTitle(e.text()));

		// id
		gxr.addGlobalRule("sourceDesc > bibl > idno[type=URL]", (d, e) -> d.setDocumentId(e.text().substring(36)));

		// author
		gxr.addGlobalRule("author", Author.class, (author, e) -> {
			author.setName(e.text());
			if (e.hasAttr("key"))
				author.setPnd(e.attr("key").replace("pnd:", ""));

		});

		// translator
		gxr.addGlobalRule("editor[role=translator]", Translator.class, (transl, e) -> {
			transl.setName(e.text());
			if (e.hasAttr("key"))
				transl.setPnd(e.attr("key").replace("pnd:", ""));
		});

		// date printed
		gxr.addGlobalRule("date[type=print][when]", (d, e) -> d.setDatePrinted(getYear(e.attr("when"))));

		// date written
		gxr.addGlobalRule("date[type=written][when]", (d, e) -> d.setDateWritten(getYear(e.attr("when"))));

		// date premiere
		gxr.addGlobalRule("date[type=premiere][when]", (d, e) -> d.setDatePremiere(getYear(e.attr("when"))));

		gxr.addRule("front", FrontMatter.class);
		gxr.addRule("body", MainMatter.class);

		// Segmentation
		gxr.addRule("div[type=prologue]", Act.class, (a, e) -> a.setRegular(false));

		gxr.addRule("div[type=act]", Act.class, (a, e) -> a.setRegular(true));
		gxr.addRule("div[type=act] > div > desc > title", ActHeading.class);
		gxr.addRule("div[type=act] > div > head", ActHeading.class);

		gxr.addRule("div[type=scene]", Scene.class, (a, e) -> a.setRegular(true));
		gxr.addRule("div[type=scene] > div > desc > title", SceneHeading.class);

		// Dramatis Personae
		gxr.addRule("body castList castItem", Figure.class);
		gxr.addRule("div[type=Dramatis_Personae]", DramatisPersonae.class);
		Map<String, String> xmlAlias = new HashMap<String, String>();
		gxr.addGlobalRule("particDesc > listPerson > person", CastFigure.class, (cf, e) -> {
			Set<String> nameList = new HashSet<String>();
			Set<String> xmlIdList = new HashSet<String>();

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
			cf.setXmlId(ArrayUtil.toStringArray(jcas, xmlIdList));
			cf.setNames(ArrayUtil.toStringArray(jcas, nameList));
			cf.setDisplayName(cf.getNames(0));

		});

		gxr.addRule("speaker", Speaker.class);
		gxr.addRule("stage", StageDirection.class);
		gxr.addRule("l", Speech.class);
		gxr.addRule("p", Speech.class);
		gxr.addRule("ab", Speech.class);
		gxr.addRule("sp", Utterance.class, (u, e) -> {
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

		gxr.addRule("text *[xml:id]", DiscourseEntity.class, (de, e) -> de.setDisplayName(e.attr("xml:id")));

		gxr.addRule("text *[xml:id]", Mention.class, (m, e) -> {
			String id = e.attr("xml:id");
			FSArray arr = new FSArray(jcas, 1);
			arr.addToIndexes();
			m.setEntity(arr);
			m.setEntity(0, (DiscourseEntity) gxr.getAnnotation(id).getValue());
		});

		Map<String, DiscourseEntity> fallbackEntities = new HashMap<String, DiscourseEntity>();
		// mentions
		gxr.addRule("text *[ref]", Mention.class, (cl, e) -> {
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

	int getYear(String s) {
		Pattern p = Pattern.compile("\\d\\d\\d\\d");
		Matcher m = p.matcher(s);
		if (m.find()) {
			return Integer.valueOf(m.group());
		} else
			return 0;
	}
}
