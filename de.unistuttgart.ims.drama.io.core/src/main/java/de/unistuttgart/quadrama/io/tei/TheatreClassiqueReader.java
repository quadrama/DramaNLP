package de.unistuttgart.quadrama.io.tei;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;

import de.unistuttgart.ims.drama.api.Act;
import de.unistuttgart.ims.drama.api.ActHeading;
import de.unistuttgart.ims.drama.api.Author;
import de.unistuttgart.ims.drama.api.CastFigure;
import de.unistuttgart.ims.drama.api.Drama;
import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.ims.drama.api.FrontMatter;
import de.unistuttgart.ims.drama.api.MainMatter;
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

public class TheatreClassiqueReader extends AbstractDramaUrlReader {

	public static final String PARAM_TEI_COMPAT = "TEI compatibility";

	public static final String PARAM_STRICT = "strict";

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
		gxr.addGlobalRule("titleStmt > title[type=main]", (d, e) -> d.setDocumentTitle(e.text()));

		// id
		gxr.addGlobalRule("publicationStmt > idno[type=cligs]", (d, e) -> d.setDocumentId(e.text()));

		// author
		gxr.addGlobalRule("author", Author.class, (author, e) -> {
			author.setName(e.select("name[type=full]").text());
		});

		// date printed
		gxr.addGlobalRule("sourceDesc > bibl[type=print-source] > date",
				(d, e) -> d.setDatePrinted(Integer.valueOf(e.text())));

		// data premiere
		gxr.addGlobalRule("sourceDesc > bibl[type=performance-first] > date",
				(d, e) -> d.setDatePrinted(Integer.valueOf(e.text())));

		gxr.addRule("front", FrontMatter.class);
		gxr.addRule("body", MainMatter.class);

		// segmentation
		gxr.addRule("div[type=act]", Act.class, (a, e) -> a.setRegular(true));
		gxr.addRule("div[type=act] > head", ActHeading.class);

		gxr.addRule("div[type=scene]", Scene.class, (a, e) -> a.setRegular(true));
		gxr.addRule("div[type=scene] > head", SceneHeading.class);

		gxr.addGlobalRule("castList > castItem > role", CastFigure.class, (cf, e) -> {
			List<String> nameList = new LinkedList<String>();
			List<String> xmlIdList = new LinkedList<String>();

			if (e.hasAttr("xml:id"))
				xmlIdList.add(e.attr("xml:id"));
			if (e.hasAttr("sex"))
				cf.setGender(e.attr("sex"));
			if (e.hasAttr("age"))
				cf.setAge(e.attr("age"));

			// gather names
			nameList.add(e.text());
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
					// theatreclassique does not use # before ids
					String xmlid = whos[i];
					sp.setXmlId(i, xmlid);
					if (gxr.exists(xmlid)) {
						sp.setCastFigure(i, (CastFigure) gxr.getAnnotation(xmlid).getValue());
						u.setCastFigure((CastFigure) gxr.getAnnotation(xmlid).getValue());
					}
				}
			}
		});

		gxr.read(jcas, file);

		AnnotationUtil.trim(new ArrayList<Figure>(JCasUtil.select(jcas, Figure.class)));
		try {
			AnnotationUtil.trim(new ArrayList<Speech>(JCasUtil.select(jcas, Speech.class)));
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		try {
			AnnotationUtil.trim(new ArrayList<Utterance>(JCasUtil.select(jcas, Utterance.class)));
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		AnnotationUtil.trim(new ArrayList<Scene>(JCasUtil.select(jcas, Scene.class)));
		AnnotationUtil.trim(new ArrayList<Act>(JCasUtil.select(jcas, Act.class)));

	}

}
