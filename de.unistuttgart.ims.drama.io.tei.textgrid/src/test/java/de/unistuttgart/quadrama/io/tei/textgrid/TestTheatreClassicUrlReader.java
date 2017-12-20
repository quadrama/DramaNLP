package de.unistuttgart.quadrama.io.tei.textgrid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.unistuttgart.ims.drama.api.Act;
import de.unistuttgart.ims.drama.api.CastFigure;
import de.unistuttgart.ims.drama.api.Drama;
import de.unistuttgart.ims.drama.api.Scene;
import de.unistuttgart.ims.drama.api.Speaker;

public class TestTheatreClassicUrlReader {
	CollectionReaderDescription description;
	JCas jcas;

	@Test
	public void testTc0623() throws ResourceInitializationException {
		description = CollectionReaderFactory.createReaderDescription(TheatreClassicUrlReader.class,
				TheatreClassicUrlReader.PARAM_INPUT, "src/test/resources/tc/tc0623.tei.xml",
				TheatreClassicUrlReader.PARAM_LANGUAGE, "fr");
		AggregateBuilder b = new AggregateBuilder();
		if (TestGenerics.debug)
			b.add(AnalysisEngineFactory.createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION,
					"target/doc"));
		jcas = SimplePipeline.iteratePipeline(description, b.createAggregateDescription()).iterator().next();

		assertEquals("tc0623", JCasUtil.selectSingle(jcas, Drama.class).getDocumentId());
		assertTrue(JCasUtil.exists(jcas, CastFigure.class));
		assertEquals(12, JCasUtil.select(jcas, CastFigure.class).size());
		assertEquals(5, JCasUtil.select(jcas, Act.class).size());
		assertEquals(32, JCasUtil.select(jcas, Scene.class).size());

		for (Speaker speaker : JCasUtil.select(jcas, Speaker.class)) {
			assertNotNull(speaker.getCastFigure());
			assertFalse(speaker.getCastFigure().size() == 0);
		}

		for (CastFigure cf : JCasUtil.select(jcas, CastFigure.class)) {
			assertNotNull(cf.getNames());
			assertNotNull(cf.getXmlId());
		}
	}
}
