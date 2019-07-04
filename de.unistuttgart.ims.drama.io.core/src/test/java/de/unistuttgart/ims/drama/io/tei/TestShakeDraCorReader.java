package de.unistuttgart.ims.drama.io.tei;

import static org.junit.Assert.assertEquals;

import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterator;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.unistuttgart.ims.drama.api.Drama;
import de.unistuttgart.ims.drama.io.TestGenerics;
import de.unistuttgart.quadrama.io.tei.ShakeDraCorReader;

public class TestShakeDraCorReader {

	public void testRomeoAndJuliet() throws ResourceInitializationException {

		CollectionReaderDescription description = CollectionReaderFactory.createReaderDescription(
				ShakeDraCorReader.class, ShakeDraCorReader.PARAM_INPUT, "src/test/resources/shakedracor/",
				ShakeDraCorReader.PARAM_LANGUAGE, "en");
		AggregateBuilder b = new AggregateBuilder();
		if (TestGenerics.debug)
			b.add(AnalysisEngineFactory.createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION,
					"target/doc"));
		JCasIterator iterator = SimplePipeline.iteratePipeline(description, b.createAggregateDescription()).iterator();
		JCas jcas = iterator.next();

		TestGenerics.checkMinimalStructure(jcas);
		TestGenerics.checkMetadata(jcas);

		assertEquals("Rom", JCasUtil.selectSingle(jcas, Drama.class).getDocumentId());
		assertEquals(202029, jcas.getDocumentText().length());
		assertEquals("en", jcas.getDocumentLanguage());

	}

}
