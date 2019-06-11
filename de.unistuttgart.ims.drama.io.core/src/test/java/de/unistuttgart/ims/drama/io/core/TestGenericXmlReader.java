package de.unistuttgart.ims.drama.io.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Before;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.ART;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.NN;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.V;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.unistuttgart.ims.uima.io.xml.GenericXmlReader;

public class TestGenericXmlReader {

	JCas jcas;
	GenericXmlReader<DocumentMetaData> gxr;

	@Before
	public void setUp() throws UIMAException {
		jcas = JCasFactory.createJCas();
		gxr = new GenericXmlReader<DocumentMetaData>(DocumentMetaData.class);
	}

	@Test
	public void test1() throws UIMAException, IOException {
		String xmlString = "<s><det>the</det> <noun>dog</noun> <verb>barks</verb></s>";
		gxr.addRule("det", ART.class);
		gxr.addRule("s", Sentence.class);
		gxr.addRule("noun", NN.class);
		gxr.addRule("verb", V.class);

		jcas = gxr.read(jcas, IOUtils.toInputStream(xmlString, "UTF-8"));

		assertNotNull(jcas);
		assertEquals("the dog barks", jcas.getDocumentText());

		assertTrue(JCasUtil.exists(jcas, Sentence.class));
		assertTrue(JCasUtil.exists(jcas, ART.class));
		assertTrue(JCasUtil.exists(jcas, NN.class));
		assertTrue(JCasUtil.exists(jcas, V.class));
	}

	@Test
	public void test2() throws UIMAException, IOException {

		String xmlString = "<text><s><pos pos=\"det\">the</pos> <pos pos=\"nn\">dog</pos> <pos pos=\"v\">barks</pos></s> <s><pos>The</pos> <pos>cat</pos> <pos>too</pos></s></text>";
		gxr.addRule("s", Sentence.class);
		gxr.addRule("pos", POS.class, (anno, xmlElement) -> {
			if (xmlElement.hasAttr("pos"))
				anno.setPosValue(xmlElement.attr("pos"));
		});

		jcas = gxr.read(jcas, IOUtils.toInputStream(xmlString, "UTF-8"));

		assertNotNull(jcas);
		assertEquals("the dog barks The cat too", jcas.getDocumentText());

		assertTrue(JCasUtil.exists(jcas, Sentence.class));
		assertEquals(2, JCasUtil.select(jcas, Sentence.class).size());

		assertTrue(JCasUtil.exists(jcas, POS.class));
		assertEquals(6, JCasUtil.select(jcas, POS.class).size());
		assertEquals("det", JCasUtil.selectByIndex(jcas, POS.class, 0).getPosValue());
		assertEquals("nn", JCasUtil.selectByIndex(jcas, POS.class, 1).getPosValue());

	}

	@Test
	public void test3() throws UIMAException, IOException {

		String xmlString = "<text><head><title>The Dog Story</title><title>bla</title></head><body><s><pos pos=\"det\">the</pos> <pos pos=\"nn\">dog</pos> <pos pos=\"v\">barks</pos></s> <s><pos>The</pos> <pos>cat</pos> <pos>too</pos></s></body></text>";
		gxr.setTextRootSelector("text > body");
		gxr.addGlobalRule("text > head > title:first-child", (d, e) -> {
			d.setDocumentTitle(e.text());
		});
		gxr.addRule("s", Sentence.class);
		gxr.addRule("pos", POS.class, (anno, xmlElement) -> {
			if (xmlElement.hasAttr("pos"))
				anno.setPosValue(xmlElement.attr("pos"));
		});

		jcas = gxr.read(jcas, IOUtils.toInputStream(xmlString, "UTF-8"));

		assertNotNull(jcas);
		assertEquals("the dog barks The cat too\n", jcas.getDocumentText());

		assertTrue(JCasUtil.exists(jcas, Sentence.class));
		assertEquals(2, JCasUtil.select(jcas, Sentence.class).size());

		assertTrue(JCasUtil.exists(jcas, POS.class));
		assertEquals(6, JCasUtil.select(jcas, POS.class).size());
		assertEquals("det", JCasUtil.selectByIndex(jcas, POS.class, 0).getPosValue());
		assertEquals("nn", JCasUtil.selectByIndex(jcas, POS.class, 1).getPosValue());

		assertEquals("The Dog Story", DocumentMetaData.get(jcas).getDocumentTitle());
	}
}
