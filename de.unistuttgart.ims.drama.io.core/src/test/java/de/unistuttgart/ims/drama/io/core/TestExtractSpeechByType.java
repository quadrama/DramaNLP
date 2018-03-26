package de.unistuttgart.ims.drama.io.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import de.unistuttgart.quadrama.io.core.ExtractSpeechByType;

@Deprecated
public class TestExtractSpeechByType {

	@Test
	public void testExtractSpeechByType() throws Exception {
		File tdir = new File("target/ExtractSpeechByType/texts/");
		FileUtils.deleteDirectory(tdir);
		SimplePipeline.runPipeline(
				CollectionReaderFactory.createReaderDescription(XmiReader.class, XmiReader.PARAM_SOURCE_LOCATION,
						"src/test/resources/ExtractSpeechByType/*.xmi", XmiReader.PARAM_LENIENT, true),
				AnalysisEngineFactory.createEngineDescription(ExtractSpeechByType.class,
						ExtractSpeechByType.PARAM_OUTPUT_DIRECTORY, tdir.getAbsolutePath(),
						ExtractSpeechByType.PARAM_TYPE, "Gender"));
		assertTrue(tdir.exists());
		assertTrue(tdir.isDirectory());
		assertEquals(2, tdir.listFiles().length);
	}

	@Test
	public void testExtractSpeechByTypeUnmerged() throws Exception {
		File tdir = new File("target/ExtractSpeechByType/texts/");
		FileUtils.deleteDirectory(tdir);
		SimplePipeline.runPipeline(
				CollectionReaderFactory.createReaderDescription(XmiReader.class, XmiReader.PARAM_SOURCE_LOCATION,
						"src/test/resources/ExtractSpeechByType/*.xmi", XmiReader.PARAM_LENIENT, true),
				AnalysisEngineFactory.createEngineDescription(ExtractSpeechByType.class,
						ExtractSpeechByType.PARAM_OUTPUT_DIRECTORY, tdir.getAbsolutePath(),
						ExtractSpeechByType.PARAM_TYPE, "Gender", ExtractSpeechByType.PARAM_MERGED, false));
		assertTrue(tdir.exists());
		assertTrue(tdir.isDirectory());
		assertEquals(2, tdir.listFiles().length);
	}
}
