package de.unistuttgart.quadrama.io.tei.textgrid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
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

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.unistuttgart.ims.drama.api.Act;
import de.unistuttgart.ims.drama.api.ActHeading;
import de.unistuttgart.ims.drama.api.Author;
import de.unistuttgart.ims.drama.api.Drama;
import de.unistuttgart.ims.drama.api.DramatisPersonae;
import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.ims.drama.api.FrontMatter;
import de.unistuttgart.ims.drama.api.MainMatter;
import de.unistuttgart.ims.drama.api.Scene;
import de.unistuttgart.ims.drama.api.Speaker;

public class TestTextgridTEIFileReader {

	CollectionReaderDescription description;
	String csvFilename = "src/test/resources/numbers.csv";

	@Before
	public void setUp() throws ResourceInitializationException {
		description = CollectionReaderFactory.createReaderDescription(TextgridTEIUrlReader.class,
				TextgridTEIUrlReader.PARAM_INPUT, "src/test/resources/textgridFiles");
	}

	@Test
	public void testReaderFromFiles() throws UIMAException, IOException {
		JCasIterator iter = SimplePipeline.iteratePipeline(description, AnalysisEngineFactory
				.createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION, "target/doc")).iterator();

		JCas jcas;
		Speaker speaker;
		Figure figure;

		jcas = iter.next();
		assertTrue(JCasUtil.exists(jcas, Drama.class));
		assertEquals("ndtw.0", JCasUtil.selectSingle(jcas, Drama.class).getDocumentId());
		assertTrue(JCasUtil.exists(jcas, Act.class));
		assertTrue(JCasUtil.exists(jcas, Scene.class));
		assertTrue(JCasUtil.exists(jcas, Speaker.class));
		assertTrue(JCasUtil.exists(jcas, Author.class));
		// there is no Dramatis Personae in ndtw.0
		assertFalse(JCasUtil.exists(jcas, DramatisPersonae.class));
		assertFalse(JCasUtil.exists(jcas, Figure.class));

		assertEquals(3, JCasUtil.select(jcas, Act.class).size());
		assertEquals(19, JCasUtil.select(jcas, Scene.class).size());

		jcas = iter.next();
		assertTrue(JCasUtil.exists(jcas, Drama.class));
		assertEquals("r134.0", JCasUtil.selectSingle(jcas, Drama.class).getDocumentId());
		assertTrue(JCasUtil.exists(jcas, Figure.class));
		assertTrue(JCasUtil.exists(jcas, Act.class));
		assertTrue(JCasUtil.exists(jcas, Scene.class));
		assertTrue(JCasUtil.exists(jcas, Speaker.class));
		assertTrue(JCasUtil.exists(jcas, DramatisPersonae.class));
		assertTrue(JCasUtil.exists(jcas, Author.class));

		jcas = iter.next();
		// general sanity checking
		assertTrue(JCasUtil.exists(jcas, Drama.class));
		assertEquals("vndf.0", JCasUtil.selectSingle(jcas, Drama.class).getDocumentId());
		assertTrue(JCasUtil.exists(jcas, Figure.class));
		assertTrue(JCasUtil.exists(jcas, Act.class));
		assertTrue(JCasUtil.exists(jcas, Scene.class));
		assertTrue(JCasUtil.exists(jcas, Speaker.class));
		assertTrue(JCasUtil.exists(jcas, DramatisPersonae.class));
		assertTrue(JCasUtil.exists(jcas, Author.class));
		assertNotNull(JCasUtil.selectSingle(jcas, FrontMatter.class));
		assertNotNull(JCasUtil.selectSingle(jcas, MainMatter.class));
		assertEquals(5, JCasUtil.select(jcas, Act.class).size());
		assertEquals(24, JCasUtil.select(jcas, Scene.class).size());

		Act act;
		act = JCasUtil.selectByIndex(jcas, Act.class, 0);
		assertEquals(5, JCasUtil.selectCovered(Scene.class, act).size());
		act = JCasUtil.selectByIndex(jcas, Act.class, 1);
		assertEquals(6, JCasUtil.selectCovered(Scene.class, act).size());
		act = JCasUtil.selectByIndex(jcas, Act.class, 2);
		assertEquals(5, JCasUtil.selectCovered(Scene.class, act).size());
		act = JCasUtil.selectByIndex(jcas, Act.class, 3);
		assertEquals(5, JCasUtil.selectCovered(Scene.class, act).size());
		act = JCasUtil.selectByIndex(jcas, Act.class, 4);
		assertEquals(3, JCasUtil.selectCovered(Scene.class, act).size());

		// figures
		// should be 24, but we can't identify the last lines
		assertEquals(26, JCasUtil.select(jcas, Figure.class).size());
		figure = JCasUtil.selectByIndex(jcas, Figure.class, 0);
		assertEquals("Escalus", figure.getCoveredText());
		assertEquals("Prinz von Verona", figure.getDescription().getCoveredText().trim());

		figure = JCasUtil.selectByIndex(jcas, Figure.class, 10);
		assertEquals("Bruder Marcus", figure.getCoveredText());
		assertEquals("von demselben Orden", figure.getDescription().getCoveredText().trim());

		// speakers
		speaker = JCasUtil.selectByIndex(jcas, Speaker.class, 0);
		assertEquals("SIMSON", speaker.getCoveredText());

	}

	@SuppressWarnings("resource")
	@Test
	public void testReaderFromURL() throws UIMAException, IOException {
		CSVParser reader = new CSVParser(new FileReader(new File(csvFilename)),
				CSVFormat.TDF.withHeader((String) null));
		List<CSVRecord> records = reader.getRecords();

		description = CollectionReaderFactory.createReaderDescription(TextgridTEIUrlReader.class,
				TextgridTEIUrlReader.PARAM_INPUT, csvFilename, TextgridTEIUrlReader.PARAM_LANGUAGE, "de");
		JCasIterator iter = SimplePipeline
				.iteratePipeline(description, AnalysisEngineFactory.createEngineDescription(XmiWriter.class,
						XmiWriter.PARAM_TARGET_LOCATION, "target/doc/", XmiWriter.PARAM_USE_DOCUMENT_ID, true))
				.iterator();

		JCas jcas;
		CSVRecord gold;
		int recordIndex = 0;

		while (iter.hasNext()) {
			jcas = iter.next();
			gold = records.get(recordIndex++);
			checkSanity(jcas);
			checkGold(jcas, gold);
		}

	}

	private void checkGold(JCas jcas, CSVRecord gold) {
		String documentId = DocumentMetaData.get(jcas).getDocumentId();
		assertEquals(documentId, (int) Integer.valueOf(gold.get(1)), JCasUtil.select(jcas, Act.class).size());
		assertEquals(documentId, (int) Integer.valueOf(gold.get(2)), JCasUtil.select(jcas, Scene.class).size());
		if (Integer.valueOf(gold.get(3)) > 0)
			assertEquals(documentId, (int) Integer.valueOf(gold.get(3)), JCasUtil.select(jcas, Figure.class).size());

	}

	private void checkSanity(JCas jcas) {
		assertTrue(JCasUtil.exists(jcas, Drama.class));
		assertTrue(JCasUtil.exists(jcas, Figure.class));
		assertTrue(JCasUtil.exists(jcas, Act.class));
		assertTrue(JCasUtil.exists(jcas, Scene.class));
		assertTrue(JCasUtil.exists(jcas, Speaker.class));
		assertTrue(JCasUtil.exists(jcas, DramatisPersonae.class));

		if (JCasUtil.exists(jcas, ActHeading.class)) {
			for (Act act : JCasUtil.select(jcas, Act.class)) {
				assertEquals(1, JCasUtil.selectCovered(ActHeading.class, act).size());
			}
		}
		// check that speaker annotations are not empty
		for (Speaker speaker : JCasUtil.select(jcas, Speaker.class)) {
			assertNotEquals(speaker.getBegin(), speaker.getEnd());
		}

		// check that figure annotations are not empty
		for (Figure figure : JCasUtil.select(jcas, Figure.class)) {
			assertNotEquals(figure.getBegin(), figure.getEnd());
		}
	}

}
