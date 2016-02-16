package de.unistuttgart.quadrama.io.dot;

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

		SimpleWeightedGraph<Integer, DefaultWeightedEdge> graph =
				new SimpleWeightedGraph<Integer, DefaultWeightedEdge>(
						DefaultWeightedEdge.class);
		final Map<Integer, String> nameMap = new HashMap<Integer, String>();
		for (Speaker speaker : JCasUtil.select(jcas, Speaker.class)) {
			if (!graph.containsVertex(speaker.getId())) {
				graph.addVertex(speaker.getId());
				nameMap.put(speaker.getId(), speaker.getCoveredText());
			}
		}

		for (Scene scene : JCasUtil.select(jcas, Scene.class)) {
			List<Speaker> speakers =
					JCasUtil.selectCovered(Speaker.class, scene);
			for (Speaker s1 : speakers) {
				for (Speaker s2 : speakers) {
					if (graph.containsEdge(s1.getId(), s2.getId())) {
						DefaultWeightedEdge edge =
								graph.getEdge(s1.getId(), s2.getId());
						double w = graph.getEdgeWeight(edge);
						graph.setEdgeWeight(edge, w + 1.0);
					} else {
						if (s1.getId() != s2.getId())
							graph.addEdge(s1.getId(), s2.getId());
					}
				}
			};
		}

		OutputStream docOS = null;
		OutputStreamWriter writer = null;
		try {
			docOS = getOutputStream(jcas, ".dot");
			writer = new OutputStreamWriter(docOS);
			DOTExporter<Integer, DefaultWeightedEdge> exporter =
					new DOTExporter<Integer, DefaultWeightedEdge>(
							new VertexNameProvider<Integer>() {

								public String getVertexName(Integer vertex) {
									return String.valueOf(vertex.hashCode());
								}
							}, new VertexNameProvider<Integer>() {

								public String getVertexName(Integer vertex) {
									return nameMap.get(vertex);
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
