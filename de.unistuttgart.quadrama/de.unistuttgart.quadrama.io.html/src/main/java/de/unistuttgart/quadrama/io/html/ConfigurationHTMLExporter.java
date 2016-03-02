package de.unistuttgart.quadrama.io.html;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.Level;
import org.json.JSONArray;
import org.json.JSONObject;

import de.tudarmstadt.ukp.dkpro.core.api.io.JCasFileWriter_ImplBase;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.unistuttgart.quadrama.api.Figure;
import de.unistuttgart.quadrama.api.Scene;
import de.unistuttgart.quadrama.api.Speech;
import de.unistuttgart.quadrama.api.Utterance;

public class ConfigurationHTMLExporter extends JCasFileWriter_ImplBase {
	static String[] colors = new String[] { "#EEF", "#FEE", "#EFE" };

	Map<String, JSONObject> objectMap = new HashMap<String, JSONObject>();

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

		JSONArray pbArr = new JSONArray();

		int c = 0;
		for (Scene segment : JCasUtil.select(jcas, Scene.class)) {
			JSONObject labelObj = new JSONObject();
			labelObj.put("text", segment.getCoveredText().substring(0, 15)
					.trim());
			labelObj.put("rotation", 270);
			labelObj.put("align", "center");
			labelObj.put("verticalAlign", "top");
			labelObj.put("y", -30);
			JSONObject obj = new JSONObject();
			obj.put("from", segment.getBegin());
			obj.put("to", segment.getEnd());
			obj.put("color", colors[c++ % 3]);
			obj.put("label", labelObj);
			pbArr.put(obj);
		}

		SortedSet<Figure> figures =
				new TreeSet<Figure>(new Comparator<Figure>() {

					public int compare(Figure o1, Figure o2) {

						return Integer.compare(o2.getNumberOfWords(),
								o1.getNumberOfWords());
					}
				});

		figures.addAll(JCasUtil.select(jcas, Figure.class));

		Map<Figure, JSONObject> series = new HashMap<Figure, JSONObject>();
		int next_speaker_index = 1;
		HashMap<Figure, Integer> speaker_index = new HashMap<Figure, Integer>();

		for (Figure figure : figures) {
			// we make one series for each cast member
			JSONObject j = new JSONObject();
			j.put("name", figure.getCoveredText());
			j.put("visible", figure.getNumberOfWords() > 100);
			j.put("lineWidth", 5);
			j.put("data", new JSONArray());
			series.put(figure, j);
			JSONObject statsObject = new JSONObject();
			statsObject.put("words", figure.getNumberOfWords());
			statsObject.put("utterances", figure.getNumberOfUtterances());
			if (Double.isNaN((figure.getUtteranceLengthArithmeticMean())))
				statsObject.put("meanUtteranceLength", 0);
			else
				statsObject.put("meanUtteranceLength",
						figure.getUtteranceLengthArithmeticMean());
			j.put("stats", statsObject);
			// also, each cast member gets an integer (to be used as y-position
			// in the chart)
			speaker_index.put(figure, next_speaker_index++);

		}

		JSONObject serie = new JSONObject();
		serie.put("name", "characters");
		for (Utterance utterance : JCasUtil.select(jcas, Utterance.class)) {
			Figure figure = null;
			if (utterance.getSpeaker() != null)
				figure = utterance.getSpeaker().getFigure();
			if (figure != null && speaker_index.containsKey(figure)) {
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

		JSONObject obj = new JSONObject();
		obj.put("plotBands", pbArr);
		obj.put("data", jsonSeries);
		obj.put("id", JCasUtil.selectSingle(jcas, DocumentMetaData.class)
				.getDocumentId());

		objectMap.put(JCasUtil.selectSingle(jcas, DocumentMetaData.class)
				.getDocumentId(), obj);

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

		try {
			is = getClass().getResourceAsStream("/html/jquery.dynatable.css");
			os = getOutputStream("jquery.dynatable", ".css");
			IOUtils.copy(is, os);
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		} finally {
			IOUtils.closeQuietly(os);
			IOUtils.closeQuietly(is);
		}

		try {
			is = getClass().getResourceAsStream("/html/jquery.dynatable.js");
			os = getOutputStream("jquery.dynatable", ".js");
			IOUtils.copy(is, os);
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		} finally {
			IOUtils.closeQuietly(os);
			IOUtils.closeQuietly(is);
		}

		JSONArray arr = new JSONArray();
		for (String n : objectMap.keySet()) {
			arr.put(objectMap.get(n));
		}
		try {
			os = getOutputStream("data", ".js");
			OutputStreamWriter osw = new OutputStreamWriter(os);
			osw.write("var data = " + arr.toString());
			osw.flush();
			osw.close();
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		} finally {
			IOUtils.closeQuietly(os);
		}
	}
}
