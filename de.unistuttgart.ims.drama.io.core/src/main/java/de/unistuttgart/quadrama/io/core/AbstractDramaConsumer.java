package de.unistuttgart.quadrama.io.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.uima.UimaContext;
import org.apache.uima.fit.component.JCasConsumer_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.unistuttgart.ims.drama.api.Drama;

public abstract class AbstractDramaConsumer extends JCasConsumer_ImplBase {
	public static final String PARAM_OUTPUT_DIRECTORY = "Output Directory";

	@ConfigurationParameter(name = PARAM_OUTPUT_DIRECTORY)
	protected String outputDirectoryName;

	protected File outputDirectory;

	@Override
	public void initialize(final UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		outputDirectory = new File(outputDirectoryName);

		if (!outputDirectory.exists())
			outputDirectory.mkdirs();
		if (!outputDirectory.isDirectory())
			throw new ResourceInitializationException();

	}

	protected Writer getWriter(JCas jcas, String suffix) throws IOException {
		return new FileWriter(
				new File(outputDirectory, JCasUtil.selectSingle(jcas, Drama.class).getDocumentId() + suffix));

	}
}
