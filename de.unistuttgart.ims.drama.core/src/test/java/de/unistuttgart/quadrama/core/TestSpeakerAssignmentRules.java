package de.unistuttgart.quadrama.core;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
	public void testRules1() throws Exception {
		org.apache.uima.fit.pipeline.JCasIterator iter = SimplePipeline
				.iteratePipeline(CollectionReaderFactory.createReaderDescription(XmiReader.class,
						XmiReader.PARAM_SOURCE_LOCATION, "src/test/resources/SpeakerAssignmentRules/tx4z.0.xmi"),
						AnalysisEngineFactory.createEngineDescription(FigureReferenceAnnotator.class),
						AnalysisEngineFactory.createEngineDescription(SpeakerAssignmentRules.class,
								SpeakerAssignmentRules.PARAM_RULE_FILE, "src/test/resources/SpeakerAssignmentRules/speaker-assignment-mapping.tsv"))
				.iterator();
		if (iter.hasNext()) {
			JCas jcas = iter.next();
			for (Speaker speaker : JCasUtil.select(jcas, Speaker.class)) {
				if (speaker.getCoveredText().equalsIgnoreCase("carlos"))
					assertNotNull(speaker.getCoveredText(), speaker.getFigure());
			}
		}
	}

	@Test
	public void testRules2() throws Exception {
		org.apache.uima.fit.pipeline.JCasIterator iter = SimplePipeline
				.iteratePipeline(CollectionReaderFactory.createReaderDescription(XmiReader.class,
						XmiReader.PARAM_SOURCE_LOCATION, "src/test/resources/SpeakerAssignmentRules/w3zd.0.xmi"),
						AnalysisEngineFactory.createEngineDescription(FigureReferenceAnnotator.class),
						AnalysisEngineFactory.createEngineDescription(SpeakerAssignmentRules.class,
								SpeakerAssignmentRules.PARAM_RULE_FILE, "src/test/resources/SpeakerAssignmentRules/speaker-assignment-mapping.tsv"))
				.iterator();
		assertTrue(iter.hasNext());
		JCas jcas = iter.next();
		assertNotNull(jcas);
		for (Speaker speaker : JCasUtil.select(jcas, Speaker.class)) {
			if (speaker.getCoveredText().equalsIgnoreCase("der capitain"))
				assertNotNull(speaker.getCoveredText(), speaker.getFigure());
		}
	}

}
