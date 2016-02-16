package de.unistuttgart.quadrama.experiments;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.tudarmstadt.ukp.dkpro.core.languagetool.LanguageToolSegmenter;
import de.tudarmstadt.ukp.dkpro.core.matetools.MateParser;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordPosTagger;
import de.unistuttgart.quadrama.core.DramaSpeechSegmenter;

public class Preprocessing {
	public static void main(String[] args)
			throws ResourceInitializationException, UIMAException, IOException {
		CollectionReaderDescription crd =
				CollectionReaderFactory.createReaderDescription(
						XmiReader.class, XmiReader.PARAM_SOURCE_LOCATION,
						"src/main/resources/romeo-and-juliet/de/*.xmi");

		SimplePipeline
				.runPipeline(
						crd,
						createEngineDescription(FixGutenbergSpeech.class),
						DramaSpeechSegmenter
								.getWrappedSegmenterDescription(LanguageToolSegmenter.class),
						createEngineDescription(StanfordPosTagger.class),
						createEngineDescription(MateParser.class),
						createEngineDescription(XmiWriter.class,
								XmiWriter.PARAM_TARGET_LOCATION, "target/"));
	}
}
