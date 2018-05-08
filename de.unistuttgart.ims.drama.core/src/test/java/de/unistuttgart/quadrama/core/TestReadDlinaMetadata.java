package de.unistuttgart.quadrama.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Iterator;

import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.unistuttgart.ims.drama.api.DatePremiere;
import de.unistuttgart.ims.drama.api.DatePrint;
import de.unistuttgart.ims.drama.api.DateWritten;
import de.unistuttgart.ims.drama.api.Drama;
import de.unistuttgart.quadrama.io.tei.TextgridTEIUrlReader;

@Deprecated
public class TestReadDlinaMetadata {

	// @BeforeClass
	public static void setUp() throws Exception {
		SimplePipeline.runPipeline(
				CollectionReaderFactory.createReaderDescription(TextgridTEIUrlReader.class,
						TextgridTEIUrlReader.PARAM_INPUT, "src/test/resources/ReadDlinaMetadata/urls.txt"),
				AnalysisEngineFactory.createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION,
						"src/test/resources/ReadDlinaMetadata/xmi/"));
	}

	public void testReadDlinaMetadata() throws Exception {
		Iterator<JCas> iter = SimplePipeline.iteratePipeline(
				CollectionReaderFactory.createReaderDescription(XmiReader.class, XmiReader.PARAM_SOURCE_LOCATION,
						"src/test/resources/ReadDlinaMetadata/xmi/*.xmi"),
				AnalysisEngineFactory.createEngineDescription(ReadDlinaMetadata.class,
						ReadDlinaMetadata.PARAM_DLINA_DIRECTORY, "src/test/resources/ReadDlinaMetadata/zwischenformat"))
				.iterator();
		JCas jcas;
		Drama d;
		jcas = iter.next();
		assertNotNull(jcas);
		d = JCasUtil.selectSingle(jcas, Drama.class);
		assertNotNull(d);

		assertEquals(1802, JCasUtil.selectSingle(jcas, DatePrint.class).getYear());
		assertEquals(0, JCasUtil.selectSingle(jcas, DatePremiere.class).getYear());
		assertEquals(0, JCasUtil.selectSingle(jcas, DateWritten.class).getYear());

		jcas = iter.next();
		assertNotNull(jcas);
		d = JCasUtil.selectSingle(jcas, Drama.class);
		assertNotNull(d);
		assertEquals(1801, JCasUtil.selectSingle(jcas, DatePrint.class).getYear());
		assertEquals(1801, JCasUtil.selectSingle(jcas, DatePremiere.class).getYear());
		assertEquals(1801, JCasUtil.selectSingle(jcas, DateWritten.class).getYear());
	}
}
