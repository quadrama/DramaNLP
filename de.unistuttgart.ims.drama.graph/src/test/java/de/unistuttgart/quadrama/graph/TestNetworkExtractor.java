package de.unistuttgart.quadrama.graph;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Before;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.unistuttgart.quadrama.core.SpeakerIdentifier;

public class TestNetworkExtractor {

	CollectionReaderDescription crd;

	@Before
	public void setUp() throws ResourceInitializationException {
		crd = CollectionReaderFactory.createReaderDescription(XmiReader.class, XmiReader.PARAM_SOURCE_LOCATION,
				"src/test/resources/*.xmi");
	}

	@Test
	public void testNetworkExtractor() throws ResourceInitializationException, UIMAException, IOException {
		SimplePipeline.runPipeline(crd, AnalysisEngineFactory.createEngineDescription(SpeakerIdentifier.class),
				AnalysisEngineFactory.createEngineDescription(NetworkExtractor.class), AnalysisEngineFactory
						.createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION, "target/"));
	}
}
