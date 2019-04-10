package de.unistuttgart.quadrama.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.NN;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.languagetool.LanguageToolSegmenter;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordPosTagger;
import de.unistuttgart.ims.drama.util.TestUtil;

public class TestDramaSpeechPosTagging {

	AnalysisEngineDescription desc;

	@Before
	public void setUp() throws UIMAException, SAXException, IOException {
		desc = D.getWrappedSegmenterDescription(LanguageToolSegmenter.class);
	}

	@Test
	public void testSegmentation() throws Exception {

		JCas jcas = TestUtil.getJCas("/level-1/rfxf.0.xmi");
		jcas.setDocumentLanguage("de");

		SimplePipeline.runPipeline(jcas, desc, AnalysisEngineFactory.createEngineDescription(StanfordPosTagger.class));

		assertTrue(JCasUtil.exists(jcas, POS.class));
		assertEquals("Liebe", JCasUtil.selectByIndex(jcas, NN.class, 0).getCoveredText());
	}
}
