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

public class ExportAsCSV extends JCasFileWriter_ImplBase {

	public static final String PARAM_CSV_VARIANT_NAME = "CSV Variant Name";
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
			csvVariant.convert(jcas, p);
			p.flush();
			os.flush();
			IOUtils.closeQuietly(os);
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

}
