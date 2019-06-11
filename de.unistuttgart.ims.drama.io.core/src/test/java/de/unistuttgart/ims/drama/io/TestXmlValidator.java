package de.unistuttgart.ims.drama.io;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;
import org.xml.sax.SAXException;

import de.unistuttgart.quadrama.io.core.XmlValidator;

public class TestXmlValidator {
	XmlValidator validator;

	@Test
	public void test() throws FileNotFoundException, SAXException, IOException {
		validator = new XmlValidator(getClass().getResource("/xsd/MinimalStructure.xsd"));
		validator.validate(getClass().getResourceAsStream("/minimalStructure/friends1.xml"));
	}
}
