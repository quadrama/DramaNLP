package de.unistuttgart.quadrama.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.languagetool.LanguageToolSegmenter;
import de.unistuttgart.ims.drama.util.TestUtil;

public class TestDramaSpeechSegmentation {

	AnalysisEngineDescription desc;

	@Before
	public void setUp() throws UIMAException, SAXException, IOException {

		desc = D.getWrappedSegmenterDescription(LanguageToolSegmenter.class);
	}

	@Test
	public void testSegmentation() throws Exception {
		JCas jcas = TestUtil.getJCas("/level-1/rfxf.0.xmi");
		jcas.setDocumentLanguage("de");
		SimplePipeline.runPipeline(jcas, desc);

		assertTrue(JCasUtil.exists(jcas, Token.class));
		assertEquals("Unbegreiflich", JCasUtil.selectByIndex(jcas, Token.class, 0).getCoveredText());
		assertEquals("ruhig", JCasUtil.selectByIndex(jcas, Token.class, 20).getCoveredText());
	}

}
