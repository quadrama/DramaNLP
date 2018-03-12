package de.unistuttgart.ims.drama.io.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import de.unistuttgart.quadrama.io.core.ExtractSpeechByFigure;

@Deprecated
public class TestExtractSpeechByFigure {

	@Test
	public void testExtractFigureSpeech() throws ResourceInitializationException, UIMAException, IOException {
		File tdir = new File("target/texts/");
		FileUtils.deleteDirectory(tdir);
		SimplePipeline.runPipeline(
				CollectionReaderFactory.createReaderDescription(XmiReader.class, XmiReader.PARAM_SOURCE_LOCATION,
						"src/test/resources/*.xmi", XmiReader.PARAM_LENIENT, true),
				AnalysisEngineFactory.createEngineDescription(ExtractSpeechByFigure.class,
						ExtractSpeechByFigure.PARAM_OUTPUT_DIRECTORY, tdir.getAbsolutePath()));
		File rdir = new File(tdir, "SW_RuJ_vndf.0");
		assertTrue(rdir.exists());
		assertTrue(rdir.isDirectory());
		assertEquals(42, rdir.listFiles().length);
	}
}
