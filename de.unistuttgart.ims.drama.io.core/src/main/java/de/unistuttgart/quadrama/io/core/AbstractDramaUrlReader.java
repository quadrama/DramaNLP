package de.unistuttgart.quadrama.io.core;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Progress;

import de.unistuttgart.ims.drama.api.Drama;
import de.unistuttgart.quadrama.io.core.type.XMLElement;

/**
 * This abstract reader automatically reads from different sources, based on the
 * supplied input parameter:
 * <ul>
 * <li>Directory</li>
 * <li>XML File</li>
 * <li>TSV File</li>
 * </ul>
 * 
 * @author reiterns
 * @since 1.0
 */
public abstract class AbstractDramaUrlReader extends JCasCollectionReader_ImplBase {
	/**
	 * The input source
	 */
	public static final String PARAM_INPUT = "Input";

	/**
	 * The text language
	 */
	public static final String PARAM_LANGUAGE = "Language";

	/**
	 * Whether to remove annotations representing the XML elements
	 */
	public static final String PARAM_REMOVE_XML_ANNOTATIONS = "Remove XML Annotations";

	/**
	 * The collection id to store in the documents
	 */
	public static final String PARAM_COLLECTION_ID = "Collection Id";

	@ConfigurationParameter(name = PARAM_INPUT, mandatory = false)
	String input = null;

	@ConfigurationParameter(name = PARAM_LANGUAGE, mandatory = false, defaultValue = "de")
	String language = "de";

	@ConfigurationParameter(name = PARAM_REMOVE_XML_ANNOTATIONS, mandatory = false)
	boolean removeXmlAnnotations = false;

	@ConfigurationParameter(name = PARAM_COLLECTION_ID, mandatory = false, defaultValue = "")
	String collectionId;

	List<URL> urls = new LinkedList<URL>();
	int currentUrlIndex = 0;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		File inputFile = new File(input);

		if (inputFile.isDirectory()) {
			File inputDir = inputFile;
			File[] files = inputDir.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".xml") || name.endsWith(".tei");
				}

			});
			try {
				for (File file : files) {
					urls.add(file.toURI().toURL());
				}
			} catch (Exception e) {
				throw new ResourceInitializationException(e);

			}
		} else if (input.endsWith(".xml") || input.endsWith(".tei") || input.startsWith("http")) {
			try {
				urls.add(new URL(input));
			} catch (MalformedURLException e) {
				try {
					urls.add(inputFile.toURI().toURL());
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				}
			}
		} else {
			CSVParser r = null;
			try {
				r = new CSVParser(new FileReader(inputFile), CSVFormat.TDF);
				List<CSVRecord> records = r.getRecords();
				for (CSVRecord rec : records) {
					String s = rec.get(0);
					if (s.startsWith("/")) {
						urls.add(new File(s).toURI().toURL());
					} else {
						urls.add(new URL(s));
					}
				}
				getLogger().log(Level.FINE, "Found " + urls.size() + " URLs.");
			} catch (Exception e) {
				throw new ResourceInitializationException(e);
			} finally {
				IOUtils.closeQuietly(r);
			}
		}

	}

	@Override
	public boolean hasNext() throws IOException, CollectionException {
		return currentUrlIndex < urls.size();
	}

	@Override
	public Progress[] getProgress() {
		return null;
	}

	@Override
	public void getNext(JCas jcas) throws IOException, CollectionException {
		URL url = urls.get(currentUrlIndex++);

		getLogger().debug("Processing url " + url);

		Drama drama = new Drama(jcas);
		drama.setDocumentId(String.valueOf(currentUrlIndex));
		drama.setCollectionId(collectionId);
		// drama.setDocumentBaseUri("https://textgridlab.org/1.0/tgcrud-public/rest/");
		drama.setDocumentUri(url.toString());
		drama.addToIndexes();
		jcas.setDocumentLanguage(language);

		if (url.getProtocol().equalsIgnoreCase("http")) {
			URLConnection urlc = url.openConnection();
			urlc.setRequestProperty("Accept", "application/xml");
			getNext(jcas, urlc.getInputStream(), drama);
		} else {
			getNext(jcas, url.openStream(), drama);

		}

		if (removeXmlAnnotations) {
			jcas.removeAllIncludingSubtypes(XMLElement.type);
		}
	}

	public abstract void getNext(JCas jcas, InputStream is, Drama drama) throws IOException, CollectionException;

}
