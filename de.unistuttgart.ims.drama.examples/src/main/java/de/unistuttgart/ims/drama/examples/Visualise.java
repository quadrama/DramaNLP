package de.unistuttgart.ims.drama.examples;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.tudarmstadt.ukp.dkpro.core.languagetool.LanguageToolSegmenter;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordPosTagger;
import de.unistuttgart.quadrama.core.DramaSpeechSegmenter;
import de.unistuttgart.quadrama.core.FigureMentionDetection;
import de.unistuttgart.quadrama.core.FigureReferenceAnnotator;
import de.unistuttgart.quadrama.core.FigureSpeechStatistics;
import de.unistuttgart.quadrama.core.SpeakerIdentifier;
import de.unistuttgart.quadrama.graph.NetworkExtractor;
import de.unistuttgart.quadrama.io.html.ConfigurationHTMLExporter;
import de.unistuttgart.quadrama.io.tei.textgrid.TextgridTEIUrlReader;

public class Visualise {

	public static void main(String[] args) throws ResourceInitializationException, UIMAException, IOException {
		System.setProperty("java.util.logging.config.file", "src/main/resources/logging.properties");

		CollectionReaderDescription crd = CollectionReaderFactory.createReaderDescription(TextgridTEIUrlReader.class,
				TextgridTEIUrlReader.PARAM_INPUT_DIRECTORY, "src/main/resources");

		SimplePipeline
				.runPipeline(crd, DramaSpeechSegmenter.getWrappedSegmenterDescription(LanguageToolSegmenter.class),
						createEngineDescription(FigureReferenceAnnotator.class),
						createEngineDescription(SpeakerIdentifier.class, SpeakerIdentifier.PARAM_CREATE_SPEAKER_FIGURE,
								true),
						createEngineDescription(StanfordPosTagger.class),
						// createEngineDescription(StanfordNamedEntityRecognizer.class),
						createEngineDescription(FigureSpeechStatistics.class),
						createEngineDescription(FigureMentionDetection.class),
						/*
						 * Extract copresence network
						 */
						createEngineDescription(NetworkExtractor.class),
						/*
						 * extract mention network
						 */
						createEngineDescription(NetworkExtractor.class, NetworkExtractor.PARAM_VIEW_NAME,
								"MentionNetwork", NetworkExtractor.PARAM_NETWORK_TYPE, "MentionNetwork"),
						/*
						 * Export html view of configuration
						 */
						createEngineDescription(ConfigurationHTMLExporter.class,
								ConfigurationHTMLExporter.PARAM_TARGET_LOCATION, "target/html/"),
						/*
						 * print xmi
						 */
						createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION, "target/xmi/"));
	}
}
