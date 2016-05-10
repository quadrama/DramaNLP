package de.unistuttgart.quadrama.io.core;

import org.apache.uima.fit.component.JCasConsumer_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;

public abstract class AbstractExtractSpeechConsumer extends JCasConsumer_ImplBase {

	public static final String PARAM_OUTPUT_DIRECTORY = "Output Directory";
	@ConfigurationParameter(name = PARAM_OUTPUT_DIRECTORY)
	protected String outputDirectoryName;

}
