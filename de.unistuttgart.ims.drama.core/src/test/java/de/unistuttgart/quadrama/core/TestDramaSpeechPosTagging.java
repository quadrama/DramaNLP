package de.unistuttgart.quadrama.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
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

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.NN;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.tudarmstadt.ukp.dkpro.core.languagetool.LanguageToolSegmenter;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordPosTagger;

public class TestDramaSpeechPosTagging {
	JCas jcas;

	AnalysisEngineDescription desc;

	@Before
	public void setUp() throws UIMAException, SAXException, IOException {

		TypeSystemDescription tsd =
				TypeSystemDescriptionFactory.createTypeSystemDescription();
		jcas = JCasFactory.createJCas(tsd);
		XmiCasDeserializer.deserialize(
				getClass().getResourceAsStream("/test.xmi"), jcas.getCas(),
				true);
		desc =
				DramaSpeechSegmenter
				.getWrappedSegmenterDescription(LanguageToolSegmenter.class);
	}

	@Test
	public void testSegmentation() throws ResourceInitializationException,
	AnalysisEngineProcessException {

		SimplePipeline.runPipeline(jcas, desc, AnalysisEngineFactory
				.createEngineDescription(StanfordPosTagger.class),
				AnalysisEngineFactory.createEngineDescription(XmiWriter.class,
						XmiWriter.PARAM_TARGET_LOCATION, "target"));

		assertTrue(JCasUtil.exists(jcas, POS.class));
		assertEquals("Leder", JCasUtil.selectByIndex(jcas, NN.class, 0)
				.getCoveredText());
	}
}
