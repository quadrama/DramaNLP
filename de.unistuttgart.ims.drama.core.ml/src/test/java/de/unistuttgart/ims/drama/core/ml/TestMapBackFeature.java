package de.unistuttgart.ims.drama.core.ml;

import static org.junit.Assert.assertEquals;

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

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.unistuttgart.quadrama.core.api.Origin;

public class TestMapBackFeature {
	JCas jcas;
	JCas view;
	String viewName = "Other";

	@Before
	public void setUp() throws UIMAException {
		jcas = JCasFactory.createJCas();
		DocumentMetaData.create(jcas).setDocumentId("test");
		jcas.setDocumentText(
				"Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut");
		AnnotationFactory.createAnnotation(jcas, 0, 5, POS.class);
		AnnotationFactory.createAnnotation(jcas, 6, 11, POS.class);
		AnnotationFactory.createAnnotation(jcas, 12, 17, POS.class);

		view = jcas.createView(viewName);
		view.setDocumentText("dolor sit amet\n\nsadipscing");
		AnnotationFactory.createAnnotation(view, 0, 14, Origin.class).setOffset(12);
		AnnotationFactory.createAnnotation(view, 17, 26, Origin.class).setOffset(39);
		AnnotationFactory.createAnnotation(view, 0, 5, POS.class).setPosValue("1");
	}

	@Test
	public void testMapBackFeature() {
		try {
			SimplePipeline.runPipeline(view,
					AnalysisEngineFactory.createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION,
							"target/test", XmiWriter.PARAM_USE_DOCUMENT_ID, true),
					AnalysisEngineFactory.createEngineDescription(MapBackFeature.class,
							MapBackFeature.PARAM_ANNOTATION_TYPE, POS.class, MapBackFeature.PARAM_FEATURE_NAME,
							"PosValue", MapBackFeature.PARAM_VIEW_NAME, viewName));
		} catch (AnalysisEngineProcessException | ResourceInitializationException e) {
			e.printStackTrace();
		}
		POS pos = JCasUtil.selectByIndex(jcas, POS.class, 2);
		assertEquals("1", pos.getPosValue());

	}
}
