package de.unistuttgart.ims.drama.io.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
import de.unistuttgart.ims.drama.api.DiscourseEntity;
import de.unistuttgart.ims.drama.api.DramaSegment;
import de.unistuttgart.ims.drama.api.Utterance;
import de.unistuttgart.ims.drama.io.TestGenerics;
import de.unistuttgart.quadrama.io.xml.MinimalStructureReader;

public class TestMinimalStructureReader {
	CollectionReaderDescription description;
	JCas jcas;

	@Test
	public void test() throws ResourceInitializationException {
		description = CollectionReaderFactory.createReaderDescription(MinimalStructureReader.class,
				MinimalStructureReader.PARAM_INPUT, "src/test/resources/minimalStructure/friends1.xml");
		AggregateBuilder b = new AggregateBuilder();
		if (TestGenerics.debug)
			b.add(AnalysisEngineFactory.createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION,
					"target/doc"));
		jcas = SimplePipeline.iteratePipeline(description, b.createAggregateDescription()).iterator().next();

		assertNotNull(jcas);
		assertEquals(1, JCasUtil.select(jcas, DramaSegment.class).size());
		assertEquals(4, JCasUtil.select(jcas, Utterance.class).size());
		assertEquals(0, JCasUtil.select(jcas, DiscourseEntity.class).size());
	}
}
