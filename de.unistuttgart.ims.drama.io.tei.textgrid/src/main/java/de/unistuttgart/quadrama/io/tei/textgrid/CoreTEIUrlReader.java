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
import de.unistuttgart.ims.uimautil.AnnotationUtil;
import de.unistuttgart.quadrama.io.core.AbstractDramaUrlReader;
import de.unistuttgart.quadrama.io.core.Select2AnnotationCallback;
import de.unistuttgart.quadrama.io.core.Visitor;
import de.unistuttgart.quadrama.io.core.type.HTMLAnnotation;

public class CoreTEIUrlReader extends AbstractDramaUrlReader {

	public static final String PARAM_STRICT = "strict";

	@ConfigurationParameter(name = PARAM_STRICT, mandatory = false, defaultValue = "false")
	boolean strict = false;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
	}

	@Override
	public void getNext(final JCas jcas, InputStream file, Drama drama) throws IOException, CollectionException {
		Document doc = Jsoup.parse(file, "UTF-8", "", Parser.xmlParser());

		Visitor vis = new Visitor(jcas);

		Element root = doc.select("TEI > text").first();
		root.traverse(vis);
		vis.getJCas();

		select2Annotation(jcas, root, vis.getAnnotationMap(), "speaker", Speaker.class, null);
		select2Annotation(jcas, root, vis.getAnnotationMap(), "stage", StageDirection.class, null);
		select2Annotation(jcas, root, vis.getAnnotationMap(), "sp", Utterance.class, null,
				new Select2AnnotationCallback<Utterance>() {
					@Override
					public void call(Utterance annotation, Element xmlElement) {
						Collection<Speaker> speakers = JCasUtil.selectCovered(Speaker.class, annotation);
						for (Speaker sp : speakers) {
							String[] whos = xmlElement.attr("who").split(" ");
							sp.setXmlId(new StringArray(jcas, whos.length));
							for (int i = 0; i < whos.length; i++)
								sp.setXmlId(i, whos[i]);
						}
					}
				});
		select2Annotation(jcas, root, vis.getAnnotationMap(), "l", Speech.class, null);

		readActsAndScenes(jcas, root, vis.getAnnotationMap(), true);

		readCast(jcas, drama, doc);

		AnnotationUtil.trim(new ArrayList<Figure>(JCasUtil.select(jcas, Figure.class)));
		AnnotationUtil.trim(new ArrayList<Speech>(JCasUtil.select(jcas, Speech.class)));
		AnnotationUtil.trim(new ArrayList<Utterance>(JCasUtil.select(jcas, Utterance.class)));
		AnnotationUtil.trim(new ArrayList<Scene>(JCasUtil.select(jcas, Scene.class)));
		AnnotationUtil.trim(new ArrayList<Act>(JCasUtil.select(jcas, Act.class)));

	}

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

	public static void readActs(JCas jcas, Element root, Map<String, HTMLAnnotation> map, boolean strict) {
		for (Act a : select2Annotation(jcas, root, map, "div[type=act]", Act.class, null)) {
			a.setRegular(true);
		}

		select2Annotation(jcas, root, map, "div[type=act] > head", ActHeading.class, null);
	}

	/**
	 * Detect scenes. The following things are checked:
	 * <ol>
	 * <li>if they are explicitly marked with <code>type=scnee</code>, we take
	 * them and return.</li>
	 * <li>if Act annotations do exist in the JCas, we search for divs that have
	 * head annotations.</li>
	 * </ol>
	 * 
	 * @param jcas
	 * @param root
	 * @param map
	 */
	public static void readScenes(JCas jcas, Element root, Map<String, HTMLAnnotation> map, boolean strict) {
		select2Annotation(jcas, root, map, "div[type=scene]", Scene.class, null);
		select2Annotation(jcas, root, map, "div[type=scene] > head", SceneHeading.class, null);

		for (Scene scene : JCasUtil.select(jcas, Scene.class))
			scene.setRegular(true);
	}

	public static void readActsAndScenes(JCas jcas, Element root, Map<String, HTMLAnnotation> map, boolean strict) {
		readActs(jcas, root, map, strict);
		readScenes(jcas, root, map, strict);
	}

}
