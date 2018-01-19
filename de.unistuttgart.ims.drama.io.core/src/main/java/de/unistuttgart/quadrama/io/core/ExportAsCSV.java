package de.unistuttgart.quadrama.io.core;

import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.io.JCasFileWriter_ImplBase;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;

/**
 * This exporter generates the base CSV files we analyse. Documentation etc.
 * uses the term "CSV", although the exact format (CSV vs. TSV) can be
 * parameterized.
 * 
 * @since 1.0.0
 */
public class ExportAsCSV extends JCasFileWriter_ImplBase {

	/**
	 * The CSV variant controls what is contained in the output. See
	 * {@link de.unistuttgart.quadrama.io.core.CSVVariant} for details.
	 */
	public static final String PARAM_CSV_VARIANT_NAME = "CSV Variant Name";

	/**
	 * The exact format. See {@link org.apache.commons.csv.CSVFormat} for
	 * details.
	 */
	public static final String PARAM_CSV_FORMAT_NAME = "CSV Format Name";

	@ConfigurationParameter(name = PARAM_CSV_VARIANT_NAME, defaultValue = "UtterancesWithTokens")
	String csvVariantName = "UtterancesWithTokens";

	@ConfigurationParameter(name = PARAM_CSV_FORMAT_NAME, defaultValue = "Default")
	String csvFormatName = "Default";

	CSVVariant csvVariant;
	CSVFormat csvFormat;

	@Override
	public void initialize(final UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		csvVariant = CSVVariant.valueOf(csvVariantName);
		csvFormat = CSVFormat.valueOf(csvFormatName);
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		DocumentMetaData dmd = DocumentMetaData.get(jcas);
		try {
			OutputStreamWriter os = new OutputStreamWriter(
					getOutputStream(dmd.getDocumentId(), "." + csvVariantName + ".csv"));
			CSVPrinter p = new CSVPrinter(os, csvFormat);
			csvVariant.header(p);
			csvVariant.convert(jcas, p);
			p.flush();
			os.flush();
			IOUtils.closeQuietly(os);
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

}
