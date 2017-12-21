package de.unistuttgart.ims.drama.io.html;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
import de.unistuttgart.quadrama.io.html.GutenbergDEReader;

public class TestGutenbergDEReader {

	CollectionReaderDescription description;

	@Before
	public void setUp() throws ResourceInitializationException {

	}

	@Test
	public void testReader1() throws UIMAException, IOException {
		description = CollectionReaderFactory.createReaderDescription(GutenbergDEReader.class,
				GutenbergDEReader.PARAM_INPUT, "src/test/resources/test1");
		JCasIterator iter = SimplePipeline.iteratePipeline(description, AnalysisEngineFactory
				.createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION, "target/doc")).iterator();
		JCas jcas;
		Scene scene;

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
		// there are two scenes on
		// http://gutenberg.spiegel.de/buch/romeo-und-juliette-2179/26, that's
		// why 31 chapters on gutenberg.spiegel.de translate to 31 scenes (the
		// first chapter being the front matter)
		assertEquals(31, JCasUtil.select(jcas, Scene.class).size());

		// 6th scene
		scene = JCasUtil.selectByIndex(jcas, Scene.class, 5);
		assertEquals("oppeln.]", scene.getCoveredText().substring(scene.getCoveredText().length() - 8));
		assertTrue(scene.getCoveredText().endsWith("verdoppeln.]"));

		// Balkony scene
		scene = JCasUtil.selectByIndex(jcas, Scene.class, 7);
		Iterator<Utterance> utteranceIter = JCasUtil.selectCovered(Utterance.class, scene).iterator();
		Utterance utter;
		utter = utteranceIter.next();
		assertTrue(utter.getCoveredText().endsWith("berühren möchte!"));
		Set<String> s = new HashSet<String>(JCasUtil.toText(JCasUtil.selectCovered(Speaker.class, scene)));
		assertEquals(2, s.size());

		assertFalse(iter.hasNext());

	}

	public void testReader2() throws ResourceInitializationException {
		description = CollectionReaderFactory.createReaderDescription(GutenbergDEReader.class,
				GutenbergDEReader.PARAM_INPUT, "src/test/resources/test2");
		JCasIterator iter = SimplePipeline.iteratePipeline(description, AnalysisEngineFactory
				.createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION, "target/doc")).iterator();
		JCas jcas;

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

		assertFalse(iter.hasNext());
	}
}
