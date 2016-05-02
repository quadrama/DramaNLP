package de.unistuttgart.quadrama.io.tei.textgrid;

import static org.junit.Assert.assertEquals;
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
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.unistuttgart.quadrama.api.Act;
import de.unistuttgart.quadrama.api.ActHeading;
import de.unistuttgart.quadrama.api.Drama;
import de.unistuttgart.quadrama.api.DramatisPersonae;
import de.unistuttgart.quadrama.api.Figure;
import de.unistuttgart.quadrama.api.Scene;
import de.unistuttgart.quadrama.api.Speaker;

public class TestTextgridTEIUriReader {

	CollectionReaderDescription description;
	String csvFilename = "src/test/resources/numbers.csv";

	@SuppressWarnings("resource")
	@Test
	public void testReader() throws UIMAException, IOException {
		CSVParser reader =
				new CSVParser(new FileReader(new File(csvFilename)),
						CSVFormat.TDF.withHeader((String) null));
		List<CSVRecord> records = reader.getRecords();

		description =
				CollectionReaderFactory.createReaderDescription(
						TextgridTEIUrlReader.class,
						TextgridTEIUrlReader.PARAM_URL_LIST, csvFilename,
						TextgridTEIUrlReader.PARAM_LANGUAGE, "de");
		JCasIterator iter =
				SimplePipeline.iteratePipeline(
						description,
						AnalysisEngineFactory.createEngineDescription(
								XmiWriter.class,
								XmiWriter.PARAM_TARGET_LOCATION, "target/doc/",
								XmiWriter.PARAM_USE_DOCUMENT_ID, true))
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
		assertEquals((int) Integer.valueOf(gold.get(1)),
				JCasUtil.select(jcas, Act.class).size());
		assertEquals((int) Integer.valueOf(gold.get(2)),
				JCasUtil.select(jcas, Scene.class).size());
		assertEquals((int) Integer.valueOf(gold.get(3)),
				JCasUtil.select(jcas, Figure.class).size());

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
				assertEquals(1, JCasUtil.selectCovered(ActHeading.class, act)
						.size());
			}
		}
	}
}
