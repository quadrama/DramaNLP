package de.unistuttgart.quadrama.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.ims.drama.api.FigureDescription;
import de.unistuttgart.ims.drama.api.FigureName;

public class TestFigureDetailsAnnotator {

	JCas jcas;

	@Before
	public void setUp() throws UIMAException {
		jcas = JCasFactory.createText("Peter, König\nPetra,   Königin\nFritz,\nFranz, Offiziere\nVolk");
		AnnotationFactory.createAnnotation(jcas, 0, 12, Figure.class);
		AnnotationFactory.createAnnotation(jcas, 13, 29, Figure.class);
		AnnotationFactory.createAnnotation(jcas, 30, 36, Figure.class);
		AnnotationFactory.createAnnotation(jcas, 37, 53, Figure.class);
		AnnotationFactory.createAnnotation(jcas, 54, 58, Figure.class);

	};

	@Test
	public void testDetailsAnnotator() throws AnalysisEngineProcessException, ResourceInitializationException {
		SimplePipeline.runPipeline(jcas, AnalysisEngineFactory.createEngine(FigureDetailsAnnotator.class));

		Figure figure;
		FigureName name;
		FigureDescription desc;

		figure = JCasUtil.selectByIndex(jcas, Figure.class, 0);
		name = figure.getName();
		desc = figure.getDescription();
		assertEquals("Peter, König", figure.getCoveredText());
		assertNotNull(name);
		assertEquals("Peter", name.getCoveredText());
		assertNotNull(desc);
		assertEquals("König", desc.getCoveredText());

		figure = JCasUtil.selectByIndex(jcas, Figure.class, 1);
		name = figure.getName();
		desc = figure.getDescription();
		assertEquals("Petra,   Königin", figure.getCoveredText());
		assertNotNull(name);
		assertEquals("Petra", name.getCoveredText());
		assertNotNull(desc);
		assertEquals("Königin", desc.getCoveredText());

		figure = JCasUtil.selectByIndex(jcas, Figure.class, 2);
		name = figure.getName();
		desc = figure.getDescription();
		assertEquals("Fritz,", figure.getCoveredText());
		assertNotNull(name);
		assertEquals("Fritz", name.getCoveredText());
		assertNotNull(desc);
		assertEquals("Offiziere", desc.getCoveredText());

		figure = JCasUtil.selectByIndex(jcas, Figure.class, 3);
		name = figure.getName();
		desc = figure.getDescription();
		assertEquals("Franz, Offiziere", figure.getCoveredText());
		assertNotNull(name);
		assertEquals("Franz", name.getCoveredText());
		assertNotNull(desc);
		assertEquals("Offiziere", desc.getCoveredText());

		figure = JCasUtil.selectByIndex(jcas, Figure.class, 4);
		name = figure.getName();
		desc = figure.getDescription();
		assertEquals("Volk", figure.getCoveredText());
		assertNotNull(name);
		assertEquals("Volk", name.getCoveredText());
		assertNull(desc);

	}

}
