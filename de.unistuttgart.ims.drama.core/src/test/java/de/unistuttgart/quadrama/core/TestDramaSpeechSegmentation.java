package de.unistuttgart.quadrama.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.languagetool.LanguageToolSegmenter;

public class TestDramaSpeechSegmentation {
	JCas jcas;

	AnalysisEngineDescription desc;

	@Before
	public void setUp() throws UIMAException, SAXException, IOException {

		TypeSystemDescription tsd = TypeSystemDescriptionFactory.createTypeSystemDescription();
		jcas = JCasFactory.createJCas(tsd);
		XmiCasDeserializer.deserialize(getClass().getResourceAsStream("/rfxf.0.xmi"), jcas.getCas(), true);
		jcas.setDocumentLanguage("de");

		desc = DramaSpeechSegmenter.getWrappedSegmenterDescription(LanguageToolSegmenter.class);
	}

	@Test
	public void testSegmentation() throws ResourceInitializationException, AnalysisEngineProcessException {

		SimplePipeline.runPipeline(jcas, desc);

		assertTrue(JCasUtil.exists(jcas, Token.class));
		assertEquals("Unbegreiflich", JCasUtil.selectByIndex(jcas, Token.class, 0).getCoveredText());
		assertEquals("ruhig", JCasUtil.selectByIndex(jcas, Token.class, 20).getCoveredText());
	}
}
