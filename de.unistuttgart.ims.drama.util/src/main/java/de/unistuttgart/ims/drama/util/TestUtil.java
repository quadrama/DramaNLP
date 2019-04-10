package de.unistuttgart.ims.drama.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.xml.sax.SAXException;

public class TestUtil {
	public static JCas getJCas(InputStream is) throws SAXException, IOException, UIMAException {
		JCas jcas = JCasFactory.createJCas();
		XmiCasDeserializer.deserialize(is, jcas.getCas(), true);
		return jcas;
	}

	public static JCas getJCas(String resourceName) throws UIMAException, SAXException, IOException {
		return getJCas(TestUtil.class.getResourceAsStream(resourceName));
	}
}
