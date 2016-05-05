package de.unistuttgart.ims.drama.io.network;

import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.commons.io.IOUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.json.JSONObject;

import de.tudarmstadt.ukp.dkpro.core.api.io.JCasFileWriter_ImplBase;
import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.quadrama.graph.ext.GraphImporter;

public class JsonExporter extends JCasFileWriter_ImplBase {
	public static final String PARAM_VIEW_NAME = "View Name";
	public static final String PARAM_JAVASCRIPT_VARIABLE = "Javascript variable";

	@ConfigurationParameter(name = PARAM_VIEW_NAME, mandatory = true)
	String viewName = null;

	@ConfigurationParameter(name = PARAM_JAVASCRIPT_VARIABLE, mandatory = false)
	String javascriptVariable = null;

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		OutputStream docOS = null;
		OutputStreamWriter writer = null;
		try {
			Graph<Figure, DefaultWeightedEdge> graph = GraphImporter.getGraph(jcas, viewName);

			JSONObject json = new JSONObject();
			for (Figure figure : graph.vertexSet()) {
				JSONObject figObj = new JSONObject();
				figObj.put("id", figure.getReference());
				figObj.put("label", figure.getCoveredText());
				json.append("nodes", figObj);
			}
			for (DefaultWeightedEdge edge : graph.edgeSet()) {
				JSONObject eObj = new JSONObject();
				Figure src = graph.getEdgeSource(edge);
				Figure tgt = graph.getEdgeTarget(edge);
				eObj.put("source", src.getReference());
				eObj.put("target", tgt.getReference());
			}

			if (javascriptVariable != null) {
				docOS = getOutputStream(jcas, ".js");
			} else {
				docOS = getOutputStream(jcas, ".json");
			}
			writer = new OutputStreamWriter(docOS);
			if (javascriptVariable != null) {
				writer.write("var ");
				writer.write(javascriptVariable);
				writer.write(" = ");
			}
			writer.write(json.toString());
			if (javascriptVariable != null) {
				writer.write(";\n");
			}
			writer.flush();

		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		} finally {
			IOUtils.closeQuietly(docOS);
			IOUtils.closeQuietly(writer);
		}
	}

}
