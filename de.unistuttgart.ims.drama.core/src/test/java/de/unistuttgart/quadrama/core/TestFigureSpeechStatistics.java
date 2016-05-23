package de.unistuttgart.quadrama.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterator;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.ims.drama.api.SpeakerFigure;

public class TestFigureSpeechStatistics {
	@Test
	public void testTTR() throws Exception {
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(XmiReader.class,
				XmiReader.PARAM_SOURCE_LOCATION, "src/test/resources/FigureSpeechStatistics/t4rw.0.xmi");
		JCasIterator iterator = SimplePipeline.iteratePipeline(reader,
				AnalysisEngineFactory.createEngineDescription(SpeakerIdentifier.class,
						SpeakerIdentifier.PARAM_CREATE_SPEAKER_FIGURE, true),
				AnalysisEngineFactory.createEngineDescription(FigureSpeechStatistics.class)).iterator();

		assertTrue(iterator.hasNext());
		JCas jcas = iterator.next();
		assertNotNull(jcas);

		for (Figure figure : JCasUtil.select(jcas, SpeakerFigure.class)) {
			assertEquals(0.0, figure.getTypeTokenRatio100(), 1e-9);
		}
	}
}
