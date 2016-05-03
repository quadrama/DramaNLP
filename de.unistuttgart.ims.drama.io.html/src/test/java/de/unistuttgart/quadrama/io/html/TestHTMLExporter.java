package de.unistuttgart.quadrama.io.html;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import de.tudarmstadt.ukp.dkpro.core.languagetool.LanguageToolSegmenter;
import de.unistuttgart.quadrama.core.DramaSpeechSegmenter;
import de.unistuttgart.quadrama.core.SpeakerIdentifier;

public class TestHTMLExporter {
	@Test
	public void testExporter() throws ResourceInitializationException,
	UIMAException, IOException {
		CollectionReaderDescription crd =
				CollectionReaderFactory.createReaderDescription(
						XmiReader.class, XmiReader.PARAM_SOURCE_LOCATION,
						"src/test/resources/*.xmi");

		SimplePipeline.runPipeline(crd, DramaSpeechSegmenter
				.getWrappedSegmenterDescription(LanguageToolSegmenter.class),
				createEngineDescription(SpeakerIdentifier.class),
				AnalysisEngineFactory.createEngineDescription(
						ConfigurationHTMLExporter.class,
						ConfigurationHTMLExporter.PARAM_TARGET_LOCATION,
						"target/"));
	}
}
