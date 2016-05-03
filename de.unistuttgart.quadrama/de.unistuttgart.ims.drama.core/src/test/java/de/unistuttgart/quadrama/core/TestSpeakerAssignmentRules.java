package de.unistuttgart.quadrama.core;

import static org.junit.Assert.assertNotNull;

import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import de.unistuttgart.ims.drama.api.Speaker;

public class TestSpeakerAssignmentRules {
	@Test
	public void testRules() throws Exception {
		org.apache.uima.fit.pipeline.JCasIterator iter =
				SimplePipeline
				.iteratePipeline(
						CollectionReaderFactory
										.createReaderDescription(
												XmiReader.class,
												XmiReader.PARAM_SOURCE_LOCATION,
												"src/test/resources/SpeakerAssignmentRules/*.xmi"),
								AnalysisEngineFactory
										.createEngineDescription(FigureReferenceAnnotator.class),
								AnalysisEngineFactory
								.createEngineDescription(
										SpeakerAssignmentRules.class,
										SpeakerAssignmentRules.PARAM_RULE_FILE,
										"src/test/resources/SpeakerAssignmentRules/speaker-assignment-mapping.csv"))
										.iterator();
		if (iter.hasNext()) {
			JCas jcas = iter.next();
			for (Speaker speaker : JCasUtil.select(jcas, Speaker.class)) {
				if (speaker.getCoveredText().equalsIgnoreCase("carlos"))
					assertNotNull(speaker.getCoveredText(), speaker.getFigure());
			}
		}
	}
}
