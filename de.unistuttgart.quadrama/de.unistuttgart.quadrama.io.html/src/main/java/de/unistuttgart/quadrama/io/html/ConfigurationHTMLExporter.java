package de.unistuttgart.quadrama.io.html;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.Level;
import org.json.JSONArray;
import org.json.JSONObject;

import de.tudarmstadt.ukp.dkpro.core.api.io.JCasFileWriter_ImplBase;
import de.unistuttgart.quadrama.api.Figure;
import de.unistuttgart.quadrama.api.Scene;
import de.unistuttgart.quadrama.api.Speaker;
import de.unistuttgart.quadrama.api.Speech;
import de.unistuttgart.quadrama.api.Utterance;

public class ConfigurationHTMLExporter extends JCasFileWriter_ImplBase {
	static String[] colors = new String[] { "#EEF", "#FEE", "#EFE" };

	Set<String> names = new HashSet<String>();

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

		JSONArray pbArr = new JSONArray();

		int c = 0;
		for (Scene segment : JCasUtil.select(jcas, Scene.class)) {
			JSONObject labelObj = new JSONObject();
			labelObj.put("text", segment.getCoveredText().substring(0, 10));
			JSONObject obj = new JSONObject();
			obj.put("from", segment.getBegin());
			obj.put("to", segment.getEnd());
			obj.put("color", colors[c++ % 3]);
			obj.put("label", labelObj);
			pbArr.put(obj);
		}

		JSONArray speakersArray = new JSONArray();
		for (Figure figure : JCasUtil.select(jcas, Figure.class)) {
			speakersArray.put(figure.getCoveredText());
		}

		Map<Figure, JSONObject> series = new HashMap<Figure, JSONObject>();
		int next_speaker_index = 1;
		HashMap<Figure, Integer> speaker_index = new HashMap<Figure, Integer>();

		for (Figure figure : JCasUtil.select(jcas, Figure.class)) {
			// we make one series for each cast member
			JSONObject j = new JSONObject();
			j.put("name", figure.getCoveredText());
			j.put("lineWidth", 5);
			j.put("data", new JSONArray());
			series.put(figure, j);

			// also, each cast member gets an integer (to be used as y-position
			// in the chart)
			speaker_index.put(figure, next_speaker_index++);

		}

		JSONObject serie = new JSONObject();
		serie.put("name", "characters");
		for (Utterance utterance : JCasUtil.select(jcas, Utterance.class)) {

			Figure figure =
					JCasUtil.selectCovered(Speaker.class, utterance).get(0)
					.getFigure();
			if (figure != null) {
				Speech speech =
						JCasUtil.selectCovered(Speech.class, utterance).get(0);

				// create two arrays for start and end of speech, each
				// containing an x and y value
				double yvalue = (-0.1 - (0.05 * speaker_index.get(figure)));
				JSONObject arrval = new JSONObject();
				arrval.put("x", speech.getBegin());
				arrval.put("y", yvalue);
				arrval.put("name", speech.getCoveredText().trim());
				series.get(figure).append("data", arrval);
				arrval = new JSONObject();
				arrval.put("x", speech.getEnd());
				arrval.put("y", yvalue);
				arrval.put("name", speech.getCoveredText().trim());
				series.get(figure).append("data", arrval);

				// immediately after both data points a null has to be
				// inserted to end the spoken intervall
				series.get(figure).append("data", null);
			} else {
				getLogger().log(Level.WARNING,
						"Not assigned: " + utterance.getCoveredText());
			}
		}

		JSONArray jsonSeries = new JSONArray();
		for (Figure s : series.keySet()) {
			if (((JSONArray) series.get(s).get("data")).length() > 10)
				jsonSeries.put(series.get(s));
		}

		NamedOutputStream os = null;
		OutputStreamWriter osw = null;
		try {
			os = getOutputStream(jcas, ".json");
			osw = new OutputStreamWriter(os);
			osw.write("var plotBands = " + pbArr.toString() + ";\n");
			osw.write("var data = " + jsonSeries.toString() + ";\n");
			osw.write("var speakers = " + speakersArray.toString() + ";\n");
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
	protected String getRelativePath(JCas aJCas) {
		return "data/" + super.getRelativePath(aJCas);
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
