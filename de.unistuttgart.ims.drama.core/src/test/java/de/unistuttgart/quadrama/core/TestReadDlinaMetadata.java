package de.unistuttgart.quadrama.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Iterator;

import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.unistuttgart.ims.drama.api.Drama;
import de.unistuttgart.quadrama.io.tei.textgrid.TextgridTEIUrlReader;

public class TestReadDlinaMetadata {

	// @BeforeClass
	public static void setUp() throws Exception {
		SimplePipeline.runPipeline(
				CollectionReaderFactory.createReaderDescription(TextgridTEIUrlReader.class,
						TextgridTEIUrlReader.PARAM_URL_LIST, "src/test/resources/ReadDlinaMetadata/urls.txt"),
				AnalysisEngineFactory.createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION,
						"src/test/resources/ReadDlinaMetadata/xmi/"));
	}

	@Test
	public void testReadDlinaMetadata() throws Exception {
		Iterator<JCas> iter = SimplePipeline
				.iteratePipeline(
						CollectionReaderFactory.createReaderDescription(XmiReader.class,
								XmiReader.PARAM_SOURCE_LOCATION, "src/test/resources/ReadDlinaMetadata/xmi/*.xmi"),
						AnalysisEngineFactory.createEngineDescription(ReadDlinaMetadata.class,
								ReadDlinaMetadata.PARAM_DLINA_DIRECTORY,
								"src/test/resources/ReadDlinaMetadata/zwischenformat"))
				.iterator();
		JCas jcas;
		Drama d;
		jcas = iter.next();
		assertNotNull(jcas);
		d = JCasUtil.selectSingle(jcas, Drama.class);
		assertNotNull(d);
		assertEquals(1802, d.getDlinaDatePrint());
		assertEquals(0, d.getDlinaDatePremiere());
		assertEquals(0, d.getDlinaDateWritten());
		assertEquals(1802, d.getReferenceDate());

		jcas = iter.next();
		assertNotNull(jcas);
		d = JCasUtil.selectSingle(jcas, Drama.class);
		assertNotNull(d);
		assertEquals(1801, d.getDlinaDatePrint());
		assertEquals(1801, d.getDlinaDatePremiere());
		assertEquals(1801, d.getDlinaDateWritten());
		assertEquals(1801, d.getReferenceDate());
	}
}
