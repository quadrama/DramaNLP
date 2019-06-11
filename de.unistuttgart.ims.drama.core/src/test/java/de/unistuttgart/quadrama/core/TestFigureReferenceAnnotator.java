package de.unistuttgart.quadrama.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterator;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Before;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import de.unistuttgart.ims.drama.api.Figure;

public class TestFigureReferenceAnnotator {
	File directory;

	@Before
	public void setUp() {
		directory = new File(getClass().getResource("/level-1").getFile());
	}

	@Test
	public void testFigureReferenceAnnotator1() throws ResourceInitializationException {
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(XmiReader.class,
				XmiReader.PARAM_SOURCE_LOCATION, new File(directory, "rfxf.0.xmi"), XmiReader.PARAM_LENIENT, true);
		AnalysisEngineDescription engine = AnalysisEngineFactory
				.createEngineDescription(FigureReferenceAnnotator.class);
		JCasIterator iterator = SimplePipeline.iteratePipeline(reader, engine).iterator();
		while (iterator.hasNext()) {
			JCas jcas = iterator.next();
			assertTrue(JCasUtil.exists(jcas, Figure.class));
			for (Figure figure : JCasUtil.select(jcas, Figure.class)) {
				assertNotNull(figure.getReference());
				assertFalse(figure.getReference().contains(","));
			}
		}
	}

	@Test
	public void testFigureReferenceAnnotator2() throws ResourceInitializationException {
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(XmiReader.class,
				XmiReader.PARAM_SOURCE_LOCATION, new File(directory, "w3zd.0.xmi"), XmiReader.PARAM_LENIENT, true);
		AnalysisEngineDescription engine = AnalysisEngineFactory
				.createEngineDescription(FigureReferenceAnnotator.class);
		JCasIterator iterator = SimplePipeline.iteratePipeline(reader, engine).iterator();
		while (iterator.hasNext()) {
			JCas jcas = iterator.next();
			assertTrue(JCasUtil.exists(jcas, Figure.class));
			for (Figure figure : JCasUtil.select(jcas, Figure.class)) {
				assertNotNull(figure.getReference());
			}
		}
	}
}
