package de.unistuttgart.quadrama.io.tei;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

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
import de.unistuttgart.ims.uimautil.AnnotationUtil;
import de.unistuttgart.quadrama.io.core.AbstractDramaUrlReader;

public class TurmReader extends AbstractDramaUrlReader {

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

		gxr.addGlobalRule("castItem", CastFigure.class, (cf, e) -> {
			cf.setNames(ArrayUtil.toStringArray(jcas, e.text()));
			cf.setDisplayName(cf.getNames(0));
			cf.setXmlId(ArrayUtil.toStringArray(jcas, e.text()));
		});

		// segmentation
		gxr.addRule("body > div", Act.class, (a, e) -> a.setRegular(true));
		gxr.addRule("body > div  > head", ActHeading.class);

		gxr.addRule("body > div > div", Scene.class, (a, e) -> a.setRegular(true));
		gxr.addRule("body > div > div > head", SceneHeading.class);

		gxr.addRule("speaker", Speaker.class);
		gxr.addRule("stage", StageDirection.class);
		gxr.addRule("p", Speech.class);
		gxr.addRule("l", Speech.class);

		gxr.addRule("sp", Utterance.class);

		gxr.read(jcas, file);

		AnnotationUtil.trim(new ArrayList<Speaker>(JCasUtil.select(jcas, Speaker.class)));
		AnnotationUtil.trim(new ArrayList<Figure>(JCasUtil.select(jcas, Figure.class)));
		AnnotationUtil.trim(new ArrayList<Speech>(JCasUtil.select(jcas, Speech.class)));
		AnnotationUtil.trim(new ArrayList<Utterance>(JCasUtil.select(jcas, Utterance.class)));
		AnnotationUtil.trim(new ArrayList<Scene>(JCasUtil.select(jcas, Scene.class)));
		AnnotationUtil.trim(new ArrayList<Act>(JCasUtil.select(jcas, Act.class)));

	}

}
