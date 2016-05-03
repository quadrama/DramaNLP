package de.unistuttgart.quadrama.io.tei.textgrid;

import java.io.IOException;
import java.io.InputStream;

import org.apache.uima.collection.CollectionException;
import org.apache.uima.jcas.JCas;

import de.unistuttgart.ims.drama.api.Drama;
import de.unistuttgart.quadrama.io.core.AbstractDramaUrlReader;

public class TextgridTEIUrlReader extends AbstractDramaUrlReader {

	@Override
	public void getNext(JCas jcas, InputStream file, Drama drama)
			throws IOException, CollectionException {

		TextGridUtil.getNext(jcas, file, drama);

	}

}
