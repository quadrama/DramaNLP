package de.unistuttgart.ims.drama.io.tei;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
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

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.unistuttgart.ims.drama.api.Act;
import de.unistuttgart.ims.drama.api.ActHeading;
import de.unistuttgart.ims.drama.api.Drama;
import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.ims.drama.api.Scene;
import de.unistuttgart.ims.drama.api.Speaker;
import de.unistuttgart.quadrama.io.tei.TextgridTEIUrlReader;

public class TestTextgridTEIFileReader {

	CollectionReaderDescription description;
	String csvFilename = "src/test/resources/numbers.csv";

	@Before
	public void setUp() throws ResourceInitializationException {
		description = CollectionReaderFactory.createReaderDescription(TextgridTEIUrlReader.class,
				TextgridTEIUrlReader.PARAM_INPUT, "src/test/resources/textgridFiles");
	}

	@SuppressWarnings("resource")
	// @Test
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
		assertTrue(Drama.get(jcas).getDocumentId(), JCasUtil.exists(jcas, Drama.class));
		assertTrue(Drama.get(jcas).getDocumentId(), JCasUtil.exists(jcas, Speaker.class));

		if (JCasUtil.exists(jcas, ActHeading.class)) {
			for (Act act : JCasUtil.select(jcas, Act.class)) {
				assertEquals(Drama.get(jcas).getDocumentId(), 1, JCasUtil.selectCovered(ActHeading.class, act).size());
			}
		}
		// check that speaker annotations are not empty
		for (Speaker speaker : JCasUtil.select(jcas, Speaker.class)) {
			assertNotEquals(Drama.get(jcas).getDocumentId(), speaker.getBegin(), speaker.getEnd());
		}

		// check that figure annotations are not empty
		for (Figure figure : JCasUtil.select(jcas, Figure.class)) {
			assertNotEquals(figure.getBegin(), figure.getEnd());
		}
	}

}
