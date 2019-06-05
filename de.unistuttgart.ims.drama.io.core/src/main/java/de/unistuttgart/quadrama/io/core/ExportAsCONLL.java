package de.unistuttgart.quadrama.io.core;

import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.io.JCasFileWriter_ImplBase;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.unistuttgart.ims.drama.api.Drama;

/**
 * This exporter generates the CoNLL 2012 tabular format.
 * 
 * @since 1.0.1
 */
public class ExportAsCONLL extends JCasFileWriter_ImplBase {

	/**
	 * The CONLL variant controls which CoNLL variant to use. See
	 * {@link de.unistuttgart.quadrama.io.core.CONLLVariant} for details.
	 */
	public static final String PARAM_CONLL_VARIANT_NAME = "CONLL Variant Name";

	/**
	 * The exact format. See {@link org.apache.commons.csv.CSVFormat} for details.
	 */
	public static final String PARAM_CSV_FORMAT_NAME = "CONLL Format Name";

	@ConfigurationParameter(name = PARAM_CONLL_VARIANT_NAME, defaultValue = "CoNLL2012")
	static String conllVariantName = "CoNLL2012";

	@ConfigurationParameter(name = PARAM_CSV_FORMAT_NAME, defaultValue = "TDF.withQuote(null)")
	String csvFormatName = "TDF.withQuote(null)";

	CONLLVariant conllVariant;
	CSVFormat csvFormat;

	@Override
	public void initialize(final UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		conllVariant = CONLLVariant.valueOf(conllVariantName);
		if (csvFormatName.equals("TDF.withQuote(null)")) {
			csvFormat = CSVFormat.TDF.withQuote(null);
		} else {
			csvFormat = CSVFormat.valueOf(csvFormatName);
		}
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		Drama drama = JCasUtil.selectSingle(jcas, Drama.class);
		try {
			conllVariantName = conllVariant.toString();
			OutputStreamWriter os = new OutputStreamWriter(
					getOutputStream(drama.getDocumentUri().split("/")[drama.getDocumentUri().split("/").length - 1],
							"." + conllVariantName + ".conll"));
			CSVPrinter p = new CSVPrinter(os, csvFormat);
			conllVariant.header(jcas, p);
			conllVariant.convert(jcas, p);
			p.flush();
			os.flush();
			IOUtils.closeQuietly(os);
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

}
