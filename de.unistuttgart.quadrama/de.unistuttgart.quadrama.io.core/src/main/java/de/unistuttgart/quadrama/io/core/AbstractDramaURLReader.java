package de.unistuttgart.quadrama.io.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;

import de.unistuttgart.quadrama.api.Drama;

public abstract class AbstractDramaURLReader extends
JCasCollectionReader_ImplBase {
	public static final String PARAM_URL_LIST = "URL List";
	public static final String PARAM_LANGUAGE = "Language";

	@ConfigurationParameter(name = PARAM_URL_LIST)
	String urlListFilename;

	@ConfigurationParameter(name = PARAM_LANGUAGE)
	String language;

	List<String> urls;

	int currentUrlIndex = 0;

	@Override
	public void initialize(UimaContext context)
			throws ResourceInitializationException {
		super.initialize(context);

		try {
			IOUtils.readLines(new FileInputStream(new File(urlListFilename)));
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}

	}

	public boolean hasNext() throws IOException, CollectionException {
		return currentUrlIndex < urls.size();
	}

	public Progress[] getProgress() {
		return null;
	}

	@Override
	public void getNext(JCas jcas) throws IOException, CollectionException {
		String currentUrl = urls.get(currentUrlIndex);
		URL url = new URL(currentUrl);

		getLogger().debug("Processing url " + currentUrl);

		Drama drama = new Drama(jcas);
		drama.setDocumentUri(currentUrl);
		drama.addToIndexes();
		jcas.setDocumentLanguage(language);

		getNext(jcas, url.openStream(), drama);
	}

	public abstract void getNext(JCas jcas, InputStream is, Drama drama)
			throws IOException, CollectionException;

}
