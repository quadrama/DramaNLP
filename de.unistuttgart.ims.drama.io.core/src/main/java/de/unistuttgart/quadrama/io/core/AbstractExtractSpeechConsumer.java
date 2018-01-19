package de.unistuttgart.quadrama.io.core;

import java.io.File;

import org.apache.uima.UimaContext;
import org.apache.uima.fit.component.JCasConsumer_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;

@Deprecated
public abstract class AbstractExtractSpeechConsumer extends JCasConsumer_ImplBase {

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

}
