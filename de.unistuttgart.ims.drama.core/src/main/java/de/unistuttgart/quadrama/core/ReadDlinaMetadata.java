package de.unistuttgart.quadrama.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.unistuttgart.ims.drama.api.Drama;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;

public class ReadDlinaMetadata extends JCasAnnotator_ImplBase {

	public static final String PARAM_DLINA_DIRECTORY = "Dlina Directory";

	@ConfigurationParameter(name = PARAM_DLINA_DIRECTORY)
	String dlinaDirectoryName;
	File dlinaDirectory;

	Map<String, Document> fileIndex = new HashMap<String, Document>();

	String namespaceUri = "http://lina.digital";

	@Override
	public void initialize(final UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		dlinaDirectory = new File(dlinaDirectoryName);
		if (!dlinaDirectory.isDirectory())
			throw new ResourceInitializationException();

		for (File f : dlinaDirectory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".xml");
			}
		})) {
			try {
				Builder parser = new Builder();
				Document doc = parser.build(new FileInputStream(f));
				String sourceUrl = doc.getRootElement().getFirstChildElement("header", namespaceUri)
						.getFirstChildElement("source", namespaceUri).getValue();
				String sourceId = sourceUrl.substring(56).replace("/data", "");
				fileIndex.put(sourceId, doc);
			} catch (Exception e) {
				throw new ResourceInitializationException(e);
			}
		}
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		Drama d = JCasUtil.selectSingle(jcas, Drama.class);
		Document doc = fileIndex.get(d.getDocumentId());
		if (doc == null)
			return;

		Elements dateElements = doc.getRootElement().getFirstChildElement("header", namespaceUri)
				.getChildElements("date", namespaceUri);
		for (int i = 0; i < dateElements.size(); i++) {
			Element dateElement = dateElements.get(i);
			if (dateElement.getAttributeValue("when") != null) {
				String whenAttVal = dateElement.getAttributeValue("when");
				if (dateElement.getAttributeValue("type").equals("print")) {
					d.setDlinaDatePrint(Integer.valueOf(whenAttVal));
				} else if (dateElement.getAttributeValue("type").equals("written")) {
					d.setDlinaDateWritten(Integer.valueOf(whenAttVal));
				} else if (dateElement.getAttributeValue("type").equals("premiere")) {
					d.setDlinaDatePremiere(Integer.valueOf(whenAttVal));
				}
			}
		}

		d.setReferenceDate(2000);

		int date = d.getDlinaDatePremiere();
		if (date != 0 && date < d.getReferenceDate()) {
			d.setReferenceDate(date);
		}
		date = d.getDlinaDatePrint();
		if (date != 0 && date < d.getReferenceDate()) {
			d.setReferenceDate(date);
		}
		date = d.getDlinaDateWritten();
		if (date != 0 && date < d.getReferenceDate()) {
			d.setReferenceDate(date);
		}
		return;

	}

}
