package de.unistuttgart.ims.drama.meta;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;

public class TestMetaDataExport {
	@Test
	public void testExport() throws ResourceInitializationException, UIMAException, IOException {
		SimplePipeline.runPipeline(
				CollectionReaderFactory.createReaderDescription(XmiReader.class, XmiReader.PARAM_SOURCE_LOCATION,
						"src/test/resources/*.xmi", XmiReader.PARAM_LENIENT, true),
				AnalysisEngineFactory.createEngineDescription(MetaDataExport.class, MetaDataExport.PARAM_OUTPUT,
						"target/ontology.owl"));
	}
}
