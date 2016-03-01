package de.unistuttgart.quadrama.io.html;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.json.JSONObject;

import de.tudarmstadt.ukp.dkpro.core.api.io.JCasFileWriter_ImplBase;

public class ConfigurationHTMLExporter extends JCasFileWriter_ImplBase {

	Set<String> names = new HashSet<String>();

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		JSONObject object = new JSONObject();
		NamedOutputStream os = null;
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

	@Override
	public void collectionProcessComplete()
			throws AnalysisEngineProcessException {
		super.collectionProcessComplete();

		InputStream is = null;
		OutputStream os = null;
		try {
			is = getClass().getResourceAsStream("/html/index.html");
			os = getOutputStream("index", ".html");
			IOUtils.copy(is, os);
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		} finally {
			IOUtils.closeQuietly(os);
			IOUtils.closeQuietly(is);
		}

		try {
			is = getClass().getResourceAsStream("/html/exporting.js");
			os = getOutputStream("exporting", ".js");
			IOUtils.copy(is, os);
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		} finally {
			IOUtils.closeQuietly(os);
			IOUtils.closeQuietly(is);
		}

		try {
			is = getClass().getResourceAsStream("/html/highcharts.js");
			os = getOutputStream("highcharts", ".js");
			IOUtils.copy(is, os);
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		} finally {
			IOUtils.closeQuietly(os);
			IOUtils.closeQuietly(is);
		}

		try {
			is = getClass().getResourceAsStream("/html/jquery-1.11.3.min.js");
			os = getOutputStream("jquery-1.11.3.min", ".js");
			IOUtils.copy(is, os);
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		} finally {
			IOUtils.closeQuietly(os);
			IOUtils.closeQuietly(is);
		}

	}

}
