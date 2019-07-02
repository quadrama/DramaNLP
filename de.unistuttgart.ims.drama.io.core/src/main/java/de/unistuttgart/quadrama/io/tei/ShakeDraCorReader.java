package de.unistuttgart.quadrama.io.tei;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unistuttgart.ims.drama.api.Act;
import de.unistuttgart.ims.drama.api.ActHeading;
import de.unistuttgart.ims.drama.api.Author;
import de.unistuttgart.ims.drama.api.CastFigure;
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
import de.unistuttgart.ims.drama.api.Utterance;
import de.unistuttgart.ims.uima.io.xml.ArrayUtil;
import de.unistuttgart.ims.uima.io.xml.GenericXmlReader;
import de.unistuttgart.ims.uimautil.AnnotationUtil;
import de.unistuttgart.quadrama.io.core.AbstractDramaUrlReader;

public class ShakeDraCorReader extends AbstractDramaUrlReader {

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

		gxr.setIgnoreFunction(e -> e.tagName().matches("^(lb|c|w)$"));

		// title
		gxr.addGlobalRule("teiHeader > fileDesc > titleStmt > title:first-child",
				(d, e) -> d.setDocumentTitle(e.text()));

		// id
		gxr.addGlobalRule("teiHeader > fileDesc > publicationStmt > idno:first-child",
				(d, e) -> d.setDocumentId(e.text()));

		// author
		gxr.addGlobalRule("teiHeader > fileDesc > titleStmt > author", Author.class, (author, e) -> {
			author.setName(e.text());
			if (e.hasAttr("key"))
				author.setPnd(e.attr("key").replace("pnd:", ""));
		});

		// date printed
		gxr.addGlobalRule("teiHeader > fileDesc > sourceDesc > bibl > date[type=print]",
				(d, e) -> d.setDatePrinted(getYear(e.attr("when"))));

		// date written
		gxr.addGlobalRule("teiHeader > fileDesc > sourceDesc > bibl > date[type=written]",
				(d, e) -> d.setDateWritten(getYear(e.attr("when"))));

		// date premiere
		gxr.addGlobalRule("teiHeader > fileDesc > sourceDesc > bibl > date[type=premiere]",
				(d, e) -> d.setDatePremiere(getYear(e.attr("when"))));

		gxr.addRule("front", FrontMatter.class);
		gxr.addRule("body", MainMatter.class);

		// Segmentation
		gxr.addRule("div[type=prologue]", Act.class, (a, e) -> a.setRegular(false));

		gxr.addRule("div[type=act]", Act.class, (a, e) -> a.setRegular(true));
		gxr.addRule("div[type=act] > head", ActHeading.class);

		gxr.addRule("div[type=scene]", Scene.class, (a, e) -> a.setRegular(true));
		gxr.addRule("div[type=scene] > head", SceneHeading.class);

		// Dramatis Personae
		gxr.addRule("front > castList castItem", Figure.class);
		gxr.addRule("front > castList", DramatisPersonae.class);
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
		gxr.addRule("l", Speech.class);
		gxr.addRule("p", Speech.class);
		gxr.addRule("ab", Speech.class);

		// this is disabled, because the pos tagger doesn't work properly
		if (false) {
			gxr.addRule("w", Token.class, (t, e) -> {
				if (e.hasAttr("lemma")) {
					Lemma l = AnnotationFactory.createAnnotation(jcas, t.getBegin(), t.getEnd(), Lemma.class);
					t.setLemma(l);
				}
			});
		}

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

		gxr.read(jcas, file);
		DocumentMetaData.get(jcas).setDocumentId(drama.getDocumentId());

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
}
