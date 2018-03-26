package de.unistuttgart.ims.drama.core.ml;

import java.io.IOException;
import java.util.Arrays;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.unistuttgart.ims.drama.api.DramatisPersonae;
import de.unistuttgart.ims.drama.api.Figure;

public class TestPrepareClearTk {
	@Test
	public void testPrepareClearTk() throws ResourceInitializationException, UIMAException, IOException {
		SimplePipeline.runPipeline(
				CollectionReaderFactory.createReaderDescription(XmiReader.class, XmiReader.PARAM_SOURCE_LOCATION,
						"src/main/resources/gender/training/11d2m.0.xmi", XmiReader.PARAM_LENIENT, true),
				AnalysisEngineFactory.createEngineDescription(PrepareClearTk.class,
						PrepareClearTk.PARAM_ANNOTATION_TYPE, DramatisPersonae.class,
						PrepareClearTk.PARAM_SUBANNOTATIONS, Arrays.asList(Figure.class.getName()),
						PrepareClearTk.PARAM_VIEW_NAME, "DP"),
				AnalysisEngineFactory.createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION,
						"target/"));
	}
}
