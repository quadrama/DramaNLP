package de.unistuttgart.quadrama.io.tei;

import java.io.IOException;
import java.io.InputStream;

import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.unistuttgart.ims.drama.api.Drama;
import de.unistuttgart.quadrama.io.core.AbstractDramaUrlReader;

public class TextgridTEIUrlReader extends AbstractDramaUrlReader {

	public static final String PARAM_STRICT = "strict";

	@ConfigurationParameter(name = PARAM_STRICT, mandatory = false, defaultValue = "false")
	boolean strict = false;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
	}

	@Override
	public void getNext(JCas jcas, InputStream file, Drama drama) throws IOException, CollectionException {

		TextGridUtil.getNext(jcas, file, drama, strict);

	}

}
