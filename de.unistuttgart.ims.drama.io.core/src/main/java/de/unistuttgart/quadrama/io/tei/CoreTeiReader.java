package de.unistuttgart.quadrama.io.tei;

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
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.unistuttgart.ims.drama.api.Act;
import de.unistuttgart.ims.drama.api.ActHeading;
import de.unistuttgart.ims.drama.api.CastFigure;
import de.unistuttgart.ims.drama.api.Drama;
import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.ims.drama.api.Scene;
import de.unistuttgart.ims.drama.api.SceneHeading;
import de.unistuttgart.ims.drama.api.Speaker;
import de.unistuttgart.ims.drama.api.Speech;
import de.unistuttgart.ims.drama.api.StageDirection;
import de.unistuttgart.ims.drama.api.Utterance;
import de.unistuttgart.ims.uima.io.xml.ArrayUtil;
import de.unistuttgart.ims.uima.io.xml.GenericXmlReader;
import de.unistuttgart.ims.uima.io.xml.type.XMLElement;
import de.unistuttgart.ims.uimautil.AnnotationUtil;
import de.unistuttgart.quadrama.io.core.AbstractDramaUrlReader;

public class CoreTeiReader extends AbstractDramaUrlReader {

	public static final String PARAM_STRICT = "strict";

	@ConfigurationParameter(name = PARAM_STRICT, mandatory = false, defaultValue = "false")
	boolean strict = false;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
	}

	@Override
	public void getNext(final JCas jcas, InputStream file, Drama drama) throws IOException, CollectionException {

		GenericXmlReader<Drama> gxr = new GenericXmlReader<Drama>(Drama.class);
		gxr.setTextRootSelector("TEI > text");
		gxr.setPreserveWhitespace(false);

		gxr.addGlobalRule("fileDesc > publicationStmt > idno[type=quadramaX]", (d, e) -> d.setDocumentId(e.text()));

		gxr.addGlobalRule("profileDesc > particDesc > listPerson > person", CastFigure.class, (cf, e) -> {
			cf.setNames(ArrayUtil.toStringArray(jcas, e.text()));
			cf.setXmlId(ArrayUtil.toStringArray(jcas, e.attr("xml:id")));
			cf.setDisplayName(cf.getNames(0));
		});

		// segmentation
		gxr.addRule("div[type=act]", Act.class, (a, e) -> a.setRegular(true));
		gxr.addRule("div[type=act] > head", ActHeading.class);

		gxr.addRule("div[type=scene]", Scene.class, (a, e) -> a.setRegular(true));
		gxr.addRule("div[type=scene] > head", SceneHeading.class);

		gxr.addRule("speaker", Speaker.class);
		gxr.addRule("stage", StageDirection.class);
		gxr.addRule("l", Speech.class);
		gxr.addRule("p", Speech.class);

		gxr.addRule("sp", Utterance.class, (u, e) -> {
			Collection<Speaker> speakers = JCasUtil.selectCovered(Speaker.class, u);
			for (Speaker sp : speakers) {
				String[] whos = e.attr("who").split(" ");
				sp.setXmlId(new StringArray(jcas, whos.length));
				sp.setCastFigure(new FSArray(jcas, whos.length));
				for (int i = 0; i < whos.length; i++) {
					String xmlid = whos[i].substring(1);
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
		AnnotationUtil.trim(new ArrayList<Speech>(JCasUtil.select(jcas, Speech.class)));
		AnnotationUtil.trim(new ArrayList<Utterance>(JCasUtil.select(jcas, Utterance.class)));
		AnnotationUtil.trim(new ArrayList<Scene>(JCasUtil.select(jcas, Scene.class)));
		AnnotationUtil.trim(new ArrayList<Act>(JCasUtil.select(jcas, Act.class)));

	}

	@Deprecated
	public static void readCast(JCas jcas, Drama drama, Document doc) {
		Map<String, CastFigure> idFigureMap = new HashMap<String, CastFigure>();
		Elements castEntries = doc.select("profileDesc > particDesc > listPerson > person");
		// castEntries.addAll(doc.select("profileDesc > particDesc > listPerson
		// > personGrp"));
		FSArray castListArray = new FSArray(jcas, castEntries.size());
		for (int i = 0; i < castEntries.size(); i++) {
			Element castEntry = castEntries.get(i);
			String id = castEntry.attr("xml:id");
			StringArray arr = new StringArray(jcas, 1);
			arr.set(0, castEntry.text());
			CastFigure figure = new CastFigure(jcas);
			figure.setXmlId(new StringArray(jcas, 1));
			figure.setXmlId(0, id);
			figure.setNames(arr);
			figure.addToIndexes();
			idFigureMap.put(id, figure);
			castListArray.set(i, figure);
		}
		drama.setCastList(castListArray);

		for (Speaker speaker : JCasUtil.select(jcas, Speaker.class)) {
			speaker.setCastFigure(new FSArray(jcas, speaker.getXmlId().size()));
			for (int i = 0; i < speaker.getXmlId().size(); i++)
				speaker.setCastFigure(i, idFigureMap.get(speaker.getXmlId(i)));
		}
	}

	@Deprecated

	public static void readActs(JCas jcas, Element root, Map<String, XMLElement> map, boolean strict) {
		for (Act a : select2Annotation(jcas, root, map, "div[type=act]", Act.class, null)) {
			a.setRegular(true);
		}

		select2Annotation(jcas, root, map, "div[type=act] > head", ActHeading.class, null);
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
	@Deprecated

	public static void readScenes(JCas jcas, Element root, Map<String, XMLElement> map, boolean strict) {
		select2Annotation(jcas, root, map, "div[type=scene]", Scene.class, null);
		select2Annotation(jcas, root, map, "div[type=scene] > head", SceneHeading.class, null);

		for (Scene scene : JCasUtil.select(jcas, Scene.class))
			scene.setRegular(true);
	}

	@Deprecated
	public static void readActsAndScenes(JCas jcas, Element root, Map<String, XMLElement> map, boolean strict) {
		readActs(jcas, root, map, strict);
		readScenes(jcas, root, map, strict);
	}

}
