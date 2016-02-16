package de.unistuttgart.quadrama.io.dot;

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.VertexNameProvider;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import de.tudarmstadt.ukp.dkpro.core.api.io.JCasFileWriter_ImplBase;
import de.unistuttgart.quadrama.api.Scene;
import de.unistuttgart.quadrama.api.Speaker;

public class DotExporter extends JCasFileWriter_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

		SimpleWeightedGraph<String, DefaultWeightedEdge> graph =
				new SimpleWeightedGraph<String, DefaultWeightedEdge>(
						DefaultWeightedEdge.class);

		for (Speaker speaker : JCasUtil.select(jcas, Speaker.class)) {
			graph.addVertex(speaker.getCoveredText());
		}

		for (Scene scene : JCasUtil.select(jcas, Scene.class)) {
			List<Speaker> speakers =
					JCasUtil.selectCovered(Speaker.class, scene);
			for (Speaker s1 : speakers) {
				for (Speaker s2 : speakers) {
					if (graph.containsEdge(s1.getCoveredText(),
							s2.getCoveredText())) {
						DefaultWeightedEdge edge =
								graph.getEdge(s1.getCoveredText(),
										s2.getCoveredText());
						double w = graph.getEdgeWeight(edge);
						graph.setEdgeWeight(edge, w + 1.0);
					} else {
						if (!s1.getCoveredText().equals(s2.getCoveredText()))
							graph.addEdge(s1.getCoveredText(),
									s2.getCoveredText());
					}
				}
			};
		}

		OutputStream docOS = null;
		OutputStreamWriter writer = null;
		try {
			docOS = getOutputStream(jcas, ".dot");
			writer = new OutputStreamWriter(docOS);
			DOTExporter<String, DefaultWeightedEdge> exporter =
					new DOTExporter<String, DefaultWeightedEdge>(
							new VertexNameProvider<String>() {

								public String getVertexName(String vertex) {
									return String.valueOf(vertex.hashCode());
								}
							}, new VertexNameProvider<String>() {

								public String getVertexName(String vertex) {
									return vertex;
								}
							}, null);
			exporter.export(writer, graph);

		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		} finally {
			closeQuietly(writer);
			closeQuietly(docOS);

		}
	}
}
