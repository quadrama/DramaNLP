package de.unistuttgart.quadrama.experiments;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import de.tudarmstadt.ukp.dkpro.core.languagetool.LanguageToolSegmenter;
import de.unistuttgart.quadrama.core.DramaSpeechSegmenter;
import de.unistuttgart.quadrama.core.SpeakerIdentifier;
import de.unistuttgart.quadrama.io.configuration.ConfigurationExporter;

public class Configuration {

	public static void main(String[] args)
			throws ResourceInitializationException, UIMAException, IOException {
		System.setProperty("java.util.logging.config.file",
				"src/main/resources/logging.properties");

		CollectionReaderDescription crd =
				CollectionReaderFactory.createReaderDescription(
						XmiReader.class, XmiReader.PARAM_SOURCE_LOCATION,
						"src/main/resources/romeo-and-juliet/de/*.xmi");

		SimplePipeline
				.runPipeline(
						crd,
						DramaSpeechSegmenter
								.getWrappedSegmenterDescription(LanguageToolSegmenter.class),
						createEngineDescription(SpeakerIdentifier.class),
						createEngineDescription(ConfigurationExporter.class,
								ConfigurationExporter.PARAM_TARGET_LOCATION,
								"target/conf/"));
	}
}
