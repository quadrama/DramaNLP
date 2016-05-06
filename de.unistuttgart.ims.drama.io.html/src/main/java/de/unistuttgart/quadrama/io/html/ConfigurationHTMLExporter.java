package de.unistuttgart.quadrama.io.html;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.Level;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.json.JSONArray;
import org.json.JSONObject;

import de.tudarmstadt.ukp.dkpro.core.api.io.JCasFileWriter_ImplBase;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.unistuttgart.ims.commons.Counter;
import de.unistuttgart.ims.drama.api.Field;
import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.ims.drama.api.Heading;
import de.unistuttgart.ims.drama.api.Scene;
import de.unistuttgart.ims.drama.api.Speech;
import de.unistuttgart.ims.drama.api.Utterance;
import de.unistuttgart.quadrama.core.DramaUtil;
import de.unistuttgart.quadrama.graph.ext.GraphImporter;

public class ConfigurationHTMLExporter extends JCasFileWriter_ImplBase {
	static String[] colors = new String[] { "#EEF", "#FEE", "#EFE" };

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

		String documentId = DocumentMetaData.get(jcas).getDocumentId();
		JSONArray pbArr = new JSONArray();

		int c = 0;
		for (Scene segment : JCasUtil.select(jcas, Scene.class)) {
			JSONObject labelObj = new JSONObject();
			List<Heading> headings = JCasUtil.selectCovered(Heading.class, segment);
			if (headings.isEmpty()) {
				labelObj.put("text", segment.getCoveredText().substring(0, 15).trim());
			} else {
				labelObj.put("text", headings.get(0).getCoveredText());
			}
			labelObj.put("rotation", 270);
			labelObj.put("align", "center");
			labelObj.put("verticalAlign", "bottom");
			labelObj.put("y", -30);
			JSONObject obj = new JSONObject();
			obj.put("from", segment.getBegin());
			obj.put("to", segment.getEnd());
			obj.put("color", colors[c++ % 3]);
			obj.put("label", labelObj);
			pbArr.put(obj);
		}

		SortedSet<Figure> figures = new TreeSet<Figure>(new Comparator<Figure>() {

			@Override
			public int compare(Figure o1, Figure o2) {

				return Integer.compare(o2.getNumberOfWords(), o1.getNumberOfWords());
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
				statsObject.put("meanUtteranceLength", figure.getUtteranceLengthArithmeticMean());
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
				String s = StringUtils.join(JCasUtil.toText(JCasUtil.selectCovered(jcas, Speech.class, utterance)),
						'\n');

				double yvalue = (-0.1 - (0.05 * speaker_index.get(figure)));
				JSONObject arrval = new JSONObject();
				arrval.put("x", utterance.getBegin());
				arrval.put("y", yvalue);
				arrval.put("name", s);
				series.get(figure).append("data", arrval);
				arrval = new JSONObject();
				arrval.put("x", utterance.getEnd());
				arrval.put("y", yvalue);
				arrval.put("name", s);
				series.get(figure).append("data", arrval);

				// immediately after both data points a null has to be
				// inserted to end the spoken intervall
				series.get(figure).append("data", null);

			} else {
				getLogger().log(Level.WARNING, "Not assigned: " + utterance.getCoveredText());
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
		obj.put("id", JCasUtil.selectSingle(jcas, DocumentMetaData.class).getDocumentId());

		Graph<Figure, DefaultWeightedEdge> graph;
		try {
			graph = GraphImporter.getGraph(jcas, "MentionNetwork");
			if (graph != null) {
				JSONObject json = new JSONObject();
				List<Figure> figureList = new ArrayList<Figure>(graph.vertexSet());
				for (Figure figure : figureList) {
					JSONObject figObj = new JSONObject();
					figObj.put("label", figure.getCoveredText());
					json.append("nodes", figObj);
				}
				for (DefaultWeightedEdge edge : graph.edgeSet()) {
					JSONObject eObj = new JSONObject();
					Figure src = graph.getEdgeSource(edge);
					Figure tgt = graph.getEdgeTarget(edge);
					eObj.put("source", figureList.indexOf(src));
					eObj.put("target", figureList.indexOf(tgt));
					json.append("links", eObj);
				}
				obj.put("network", json);
			}
		} catch (CASException | ClassNotFoundException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (JCasUtil.exists(jcas, Field.class)) {
			obj.put("field", extractFieldInformation(jcas));
		}

		InputStream is = null;
		OutputStream os = null;
		OutputStreamWriter osw = null;

		try {
			os = getOutputStream(documentId, ".html");
			is = getClass().getResourceAsStream("/html/index.html");
			String index = new String(IOUtils.toByteArray(is), "UTF-8");
			index = index.replace("${DOCUMENTID}", documentId);
			osw = new OutputStreamWriter(os);
			osw.write(index);
			osw.flush();
			osw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(os);
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(osw);
		}

		try {
			os = getOutputStream(documentId, ".js");
			osw = new OutputStreamWriter(os);
			osw.write("var data = " + obj.toString());
			osw.flush();
			osw.close();
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		} finally {
			IOUtils.closeQuietly(os);
		}

	}

	private JSONObject extractFieldInformation(JCas jcas) {
		JSONObject json = new JSONObject();
		SortedSet<String> fields = new TreeSet<String>();
		for (Figure figure : JCasUtil.select(jcas, Figure.class)) {
			JSONObject figObj = new JSONObject();
			if (figure.getNumberOfWords() > 100) {
				Counter<String> fieldCount = new Counter<String>();
				for (Speech speech : DramaUtil.getSpeeches(jcas, figure)) {
					for (Field field : JCasUtil.selectCovered(jcas, Field.class, speech)) {
						fieldCount.add(field.getName());
						fields.add(field.getName());
					}

				}
				for (String fName : fields) {
					figObj.append("fields", fieldCount.get(fName));
				}
			}
			figObj.put("name", figure.getReference());
			json.append("figures", figObj);
		}
		for (String fName : fields) {
			json.append("fields", fName);
		}

		return json;
	}

	@Override
	public void collectionProcessComplete() throws AnalysisEngineProcessException {
		super.collectionProcessComplete();

		InputStream is = null;
		OutputStream os = null;

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
			is = getClass().getResourceAsStream("/html/format.css");
			os = getOutputStream("format", ".css");
			IOUtils.copy(is, os);
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		} finally {
			IOUtils.closeQuietly(os);
			IOUtils.closeQuietly(is);
		}

		try {
			is = getClass().getResourceAsStream("/html/datatables.min.css");
			os = getOutputStream("datatables.min", ".css");
			IOUtils.copy(is, os);
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		} finally {
			IOUtils.closeQuietly(os);
			IOUtils.closeQuietly(is);
		}

		try {
			is = getClass().getResourceAsStream("/html/datatables.min.js");
			os = getOutputStream("datatables.min", ".js");
			IOUtils.copy(is, os);
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		} finally {
			IOUtils.closeQuietly(os);
			IOUtils.closeQuietly(is);
		}

		try {
			copyFile("/html/script.js", "script", ".js");
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		}
		try {
			copyFile("/html/jquery-ui.css", "jquery-ui", ".css");
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		}
		try {
			copyFile("/html/jquery-ui.js", "jquery-ui", ".js");
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		}

	}

	protected void copyFile(String sourcePath, String targetName, String targetExt) throws IOException {
		InputStream is = null;
		OutputStream os = null;

		try {
			is = getClass().getResourceAsStream(sourcePath);
			os = getOutputStream(targetName, targetExt);
			IOUtils.copy(is, os);
		} finally {
			IOUtils.closeQuietly(os);
			IOUtils.closeQuietly(is);
		}
	}
}
