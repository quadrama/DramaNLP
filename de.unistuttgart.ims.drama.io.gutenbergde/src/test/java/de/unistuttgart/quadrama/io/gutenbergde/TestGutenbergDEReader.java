package de.unistuttgart.quadrama.io.gutenbergde;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.uima.UIMAException;
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

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.unistuttgart.ims.drama.api.Act;
import de.unistuttgart.ims.drama.api.Drama;
import de.unistuttgart.ims.drama.api.DramatisPersonae;
import de.unistuttgart.ims.drama.api.FrontMatter;
import de.unistuttgart.ims.drama.api.MainMatter;
import de.unistuttgart.ims.drama.api.Scene;
import de.unistuttgart.ims.drama.api.Speaker;
import de.unistuttgart.ims.drama.api.Utterance;

public class TestGutenbergDEReader {

	CollectionReaderDescription description;

	@Before
	public void setUp() throws ResourceInitializationException {
		description = CollectionReaderFactory.createReaderDescription(GutenbergDEReader.class,
				GutenbergDEReader.PARAM_INPUT_DIRECTORY, "src/test/resources/test1");
	}

	@Test
	public void testReader() throws UIMAException, IOException {
		JCasIterator iter = SimplePipeline.iteratePipeline(description, AnalysisEngineFactory
				.createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION, "target/doc")).iterator();
		JCas jcas;

		jcas = iter.next();
		// sanity check
		// 1.xml
		assertEquals("1", JCasUtil.selectSingle(jcas, Drama.class).getDocumentId());
		assertTrue(JCasUtil.exists(jcas, Drama.class));
		assertTrue(JCasUtil.exists(jcas, Act.class));
		assertTrue(JCasUtil.exists(jcas, Scene.class));
		assertTrue(JCasUtil.exists(jcas, Speaker.class));
		assertTrue(JCasUtil.exists(jcas, DramatisPersonae.class));
		assertTrue(JCasUtil.exists(jcas, FrontMatter.class));
		assertTrue(JCasUtil.exists(jcas, MainMatter.class));
		assertEquals(5, JCasUtil.select(jcas, Act.class).size());
		assertEquals(31, JCasUtil.select(jcas, Scene.class).size());

		// Balkony scene
		Scene scene = JCasUtil.selectByIndex(jcas, Scene.class, 7);
		Iterator<Utterance> utteranceIter = JCasUtil.selectCovered(Utterance.class, scene).iterator();
		Utterance utter;
		utter = utteranceIter.next();
		assertTrue(utter.getCoveredText().endsWith("berühren möchte!"));
		Set<String> s = new HashSet<String>(JCasUtil.toText(JCasUtil.selectCovered(Speaker.class, scene)));
		assertEquals(2, s.size());

		jcas = iter.next();
		// sanity check
		// 2.xml

		// TODO: Currently, we don'T detect scenes and acts correctly
		assertEquals("2", JCasUtil.selectSingle(jcas, Drama.class).getDocumentId());
		assertTrue(JCasUtil.exists(jcas, Drama.class));
		// assertTrue(JCasUtil.exists(jcas, Act.class));
		// assertFalse(JCasUtil.exists(jcas, Scene.class));
		assertTrue(JCasUtil.exists(jcas, Speaker.class));
		assertTrue(JCasUtil.exists(jcas, DramatisPersonae.class));
		assertTrue(JCasUtil.exists(jcas, FrontMatter.class));
		assertTrue(JCasUtil.exists(jcas, MainMatter.class));
		// assertEquals(3, JCasUtil.select(jcas, Act.class).size());

	}
}
