package de.unistuttgart.quadrama.io.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.resource.ResourceInitializationException;

import de.unistuttgart.ims.drama.api.CastFigure;
import de.unistuttgart.ims.drama.api.Drama;
import de.unistuttgart.ims.drama.api.Scene;
import de.unistuttgart.ims.drama.api.Speaker;
import de.unistuttgart.ims.drama.api.Speech;
import de.unistuttgart.ims.drama.api.StageDirection;
import de.unistuttgart.ims.drama.api.Utterance;
import de.unistuttgart.ims.uima.io.xml.GenericXmlReader;
import de.unistuttgart.ims.uimautil.AnnotationUtil;
import de.unistuttgart.quadrama.io.core.AbstractDramaUrlReader;

public class MinimalStructureReader extends AbstractDramaUrlReader {

	public static final String PARAM_STRICT = "strict";
	public static final String PARAM_VALIDATE = "validate";

	@ConfigurationParameter(name = PARAM_STRICT, mandatory = false, defaultValue = "true")
	boolean strict = true;

	@ConfigurationParameter(name = PARAM_VALIDATE, mandatory = false, defaultValue = "true")
	boolean validate = true;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
	}

	@Override
	public void getNext(final JCas jcas, InputStream file, Drama drama) throws IOException, CollectionException {

		GenericXmlReader<Drama> gxr = new GenericXmlReader<Drama>(Drama.class);
		gxr.setTextRootSelector("TEI > text");
		gxr.setPreserveWhitespace(false);

		gxr.addRule("speaker", Speaker.class);
		gxr.addRule("stage", StageDirection.class);
		gxr.addRule("p", Speech.class);
		gxr.addRule("div[type=scene]", Scene.class);

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
					} else {
						// TODO: create CastFigure on the fly
					}

				}
			}
		});

		gxr.read(jcas, file);

		AnnotationUtil.trim(new ArrayList<Speech>(JCasUtil.select(jcas, Speech.class)));
		AnnotationUtil.trim(new ArrayList<Utterance>(JCasUtil.select(jcas, Utterance.class)));

	}

}
