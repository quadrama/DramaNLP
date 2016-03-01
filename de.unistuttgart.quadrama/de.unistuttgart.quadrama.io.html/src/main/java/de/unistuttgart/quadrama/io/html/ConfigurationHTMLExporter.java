package de.unistuttgart.quadrama.io.html;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.json.JSONObject;

import de.tudarmstadt.ukp.dkpro.core.api.io.JCasFileWriter_ImplBase;

public class ConfigurationHTMLExporter extends JCasFileWriter_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		JSONObject object = null;
		OutputStream os = null;
		OutputStreamWriter osw = null;
		try {
			os = getOutputStream(jcas, ".json");
			osw = new OutputStreamWriter(os);
			osw.write(object.toString());
			osw.flush();
			osw.close();
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		} finally {
			IOUtils.closeQuietly(osw);
			IOUtils.closeQuietly(os);
		}
	}

}
