package de.unistuttgart.quadrama.io.tei;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
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
	public void getNext(final JCas jcas, InputStream file, Drama drama)
			throws IOException, CollectionException, ArrayIndexOutOfBoundsException {

		Map<String, Integer> entityIds = new HashMap<String, Integer>();
		entityIds.put("__dummy__", -1);

		GenericXmlReader<Drama> gxr = new GenericXmlReader<Drama>(Drama.class);
		gxr.setTextRootSelector(teiCompatibility ? null : "TEI > text");
		gxr.setPreserveWhitespace(teiCompatibility);

		// title

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
			cf.setDisplayName(e.attr("xml:id"));
			if (!entityIds.containsKey(ArrayUtil.toStringArray(jcas, xmlIdList).get(0))) {
				entityIds.put(ArrayUtil.toStringArray(jcas, xmlIdList).get(0), Collections.max(entityIds.values()) + 1);
			}
			cf.setId(entityIds.get(ArrayUtil.toStringArray(jcas, xmlIdList).get(0)));
		});

		gxr.addRule("speaker", Speaker.class);
		gxr.addRule("stage", StageDirection.class);
		gxr.addRule("l > hi", StageDirection.class);
		gxr.addRule("p > hi", StageDirection.class);
		gxr.addRule("ab > hi", StageDirection.class);
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

		gxr.addRule("text *[xml:id]", DiscourseEntity.class, (de, e) -> {
			de.setDisplayName(e.attr("xml:id"));
			String[] splitted = null;
			splitted = e.attr("xml:id").split(" ");
			de.setXmlId(ArrayUtil.toStringArray(jcas, splitted));
			for (int i = 0; i < splitted.length; i++) {
				if (!entityIds.containsKey(splitted[i])) {
					entityIds.put(splitted[i], Collections.max(entityIds.values()) + 1);
				}
				de.setId(entityIds.get(splitted[i]));
			}
		});

		Map<String, DiscourseEntity> fallbackEntities = new HashMap<String, DiscourseEntity>();
		gxr.addRule("rs", Mention.class, (m, e) -> {
			if (e.hasAttr("ref") || e.hasAttr("xml:id")) {
				String[] splitted = null;
				if (e.hasAttr("ref")) {
					splitted = e.attr("ref").split(" ");
					String[] temp = new String[splitted.length];
					for (int i = 0; i < splitted.length; i++) {
						temp[i] = splitted[i].substring(1);
					}
					splitted = temp;
				} else if (e.hasAttr("xml:id")) {
					splitted = e.attr("xml:id").split(" ");
				}
				if (e.hasAttr("func")) {
					if (e.attr("func").equals("and")) {
						// default
					} else if (e.attr("func").equals("or")) {
						splitted = getRandomEntity(splitted);
					} else {
						// Should be handled by XMLSchema
					}
				}
				// gather names
				Set<String> nameList = new HashSet<String>();
				for (TextNode tn : e.textNodes()) {
					if (tn.text().trim().length() > 0)
						nameList.add(tn.text().trim());
				}
				DiscourseEntity de = null;
				if (splitted.length > 1) {
					if (fallbackEntities.containsKey(String.join("_", splitted))) {
						de = fallbackEntities.get(String.join("_", splitted));
					} else {
						de = m.getCAS().createFS(CasUtil.getType(m.getCAS(), DiscourseEntity.class));
						de.addToIndexes();
						String displayName = String.join("_", splitted);
						de.setDisplayName(displayName);
						de.setXmlId(ArrayUtil.toStringArray(jcas, splitted));
						if (!entityIds.containsKey(displayName)) {
							entityIds.put(displayName, Collections.max(entityIds.values()) + 1);
						}
						de.setId(entityIds.get(displayName));
						FSArray arr = new FSArray(jcas, splitted.length);
						DiscourseEntity deMember = null;
						for (int i = 0; i < splitted.length; i++) {
							if (gxr.exists(splitted[i])) {
								FeatureStructure fs = gxr.getAnnotation(splitted[i]).getValue();
								if (fs instanceof DiscourseEntity) {
									deMember = (DiscourseEntity) fs;
									arr.set(i, deMember);
								}
							} else {
								deMember = m.getCAS().createFS(CasUtil.getType(m.getCAS(), DiscourseEntity.class));
								deMember.addToIndexes();
								String displayNameMember = splitted[i];
								deMember.setDisplayName(displayNameMember);
								deMember.setXmlId(ArrayUtil.toStringArray(jcas, splitted[i]));
								if (!entityIds.containsKey(displayNameMember)) {
									entityIds.put(displayNameMember, Collections.max(entityIds.values()) + 1);
								}
								deMember.setId(entityIds.get(displayNameMember));
								arr.set(i, deMember);
							}
						}
						de.setEntityGroup(arr);
						fallbackEntities.put(displayName, de);
					}
					m.setSurfaceString(ArrayUtil.toStringArray(jcas, m.getCoveredText().split(" ")));
					m.setEntity(de);
				} else {
					if (gxr.exists(splitted[0])) {
						FeatureStructure fs = gxr.getAnnotation(splitted[0]).getValue();
						if (fs instanceof DiscourseEntity)
							de = (DiscourseEntity) fs;
					}
					if (fallbackEntities.containsKey(splitted[0]))
						de = fallbackEntities.get(splitted[0]);
					if (de == null) {
						de = m.getCAS().createFS(CasUtil.getType(m.getCAS(), DiscourseEntity.class));
						de.addToIndexes();
						de.setDisplayName(splitted[0]);
						de.setXmlId(ArrayUtil.toStringArray(jcas, splitted));
						if (!entityIds.containsKey(splitted[0])) {
							entityIds.put(splitted[0], Collections.max(entityIds.values()) + 1);
						}
						de.setId(entityIds.get(splitted[0]));
						fallbackEntities.put(splitted[0], de);
					}
					m.setSurfaceString(ArrayUtil.toStringArray(jcas, m.getCoveredText().split(" ")));
					m.setEntity(de);
				}
			}
		});

		gxr.read(jcas, file);

		try {
			AnnotationUtil.trim(new ArrayList<Figure>(JCasUtil.select(jcas, Figure.class)));
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		try {
			AnnotationUtil.trim(new ArrayList<Speech>(JCasUtil.select(jcas, Speech.class)));
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		try {
			AnnotationUtil.trim(new ArrayList<Speaker>(JCasUtil.select(jcas, Speaker.class)));
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		try {
			AnnotationUtil.trim(new ArrayList<Utterance>(JCasUtil.select(jcas, Utterance.class)));
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		try {
			AnnotationUtil.trim(new ArrayList<Scene>(JCasUtil.select(jcas, Scene.class)));
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		try {
			AnnotationUtil.trim(new ArrayList<Act>(JCasUtil.select(jcas, Act.class)));
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		try {
			AnnotationUtil.trim(new ArrayList<StageDirection>(JCasUtil.select(jcas, StageDirection.class)));
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		try {
			AnnotationUtil.trim(new ArrayList<Mention>(JCasUtil.select(jcas, Mention.class)));
		} catch (ArrayIndexOutOfBoundsException e) {
		}
	}

	int getYear(String s) {
		Pattern p = Pattern.compile("\\d\\d\\d\\d");
		Matcher m = p.matcher(s);
		if (m.find()) {
			return Integer.valueOf(m.group());
		} else
			return 0;
	}

	public static String[] getRandomEntity(String[] array) {
		int seed = 42;
		String[] newArray = new String[1];
		int rnd = new Random(seed).nextInt(array.length);
		newArray[0] = array[rnd];
		return newArray;
	}
}
