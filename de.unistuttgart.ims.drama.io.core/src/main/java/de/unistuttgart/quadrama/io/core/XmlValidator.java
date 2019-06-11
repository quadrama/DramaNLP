package de.unistuttgart.quadrama.io.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

public class XmlValidator {
	URL schemaFile;

	public XmlValidator(URL schemaFile) {
		this.schemaFile = schemaFile;
	}

	public void validate(InputStream file) throws SAXException, IOException {
		Source xmlFile = new StreamSource(file);
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = schemaFactory.newSchema(schemaFile);
		Validator validator = schema.newValidator();
		validator.validate(xmlFile);
	}
}
