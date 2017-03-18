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
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordNamedEntityRecognizer;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordPosTagger;
import de.unistuttgart.quadrama.core.D;
import de.unistuttgart.quadrama.core.FigureMentionDetection;
import de.unistuttgart.quadrama.core.FigureReferenceAnnotator;
import de.unistuttgart.quadrama.core.SpeakerIdentifier;
import de.unistuttgart.quadrama.graph.NetworkExtractor;
import de.unistuttgart.quadrama.io.tei.textgrid.TextgridTEIUrlReader;

@Deprecated
public class Process {

	public static void main(String[] args) throws ResourceInitializationException, UIMAException, IOException {
		System.setProperty("java.util.logging.config.file", "src/main/resources/logging.properties");

		CollectionReaderDescription crd = CollectionReaderFactory.createReaderDescription(TextgridTEIUrlReader.class,
				TextgridTEIUrlReader.PARAM_INPUT, "src/main/resources");

		SimplePipeline.runPipeline(crd,
				/*
				 * Do segmentation.
				 */
				D.getWrappedSegmenterDescription(LanguageToolSegmenter.class),
				createEngineDescription(FigureReferenceAnnotator.class),
				createEngineDescription(SpeakerIdentifier.class, SpeakerIdentifier.PARAM_CREATE_SPEAKER_FIGURE, true),
				/*
				 * standard NLP components. This works because dkpro only sees
				 * tokens and sentences. The segmenter creates those only for
				 * the figure speech (and not for stage directions)
				 */
				createEngineDescription(StanfordPosTagger.class),
				createEngineDescription(StanfordNamedEntityRecognizer.class),
				createEngineDescription(FigureMentionDetection.class),
				/*
				 * Extract copresence network
				 */
				createEngineDescription(NetworkExtractor.class),
				/*
				 * extract mention network
				 */
				createEngineDescription(NetworkExtractor.class, NetworkExtractor.PARAM_VIEW_NAME, "MentionNetwork",
						NetworkExtractor.PARAM_NETWORK_TYPE, "MentionNetwork"),
				/*
				 * print xmi
				 */
				createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION, "target/xmi/"));
	}
}
