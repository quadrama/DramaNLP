package de.unistuttgart.quadrama.graph.ext;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.jcas.JCas;
import org.jgrapht.Graph;
import org.jgrapht.WeightedGraph;

import de.unistuttgart.quadrama.api.Figure;
import de.unistuttgart.quadrama.graph.ext.api.GraphMetaData;

public class GraphExporter {

	public void export(JCas jcas, Graph<Figure, ?> graph) throws IOException {
		StringWriter sw = new StringWriter();
		for (Figure figure1 : graph.vertexSet()) {
			for (Figure figure2 : graph.vertexSet()) {
				if (graph.containsEdge(figure1, figure2)) {
					sw.write(figure1.getId() + " " + figure2.getId());
					Object edge = graph.getEdge(figure1, figure2);
					try {
						@SuppressWarnings("unchecked")
						double w =
						((WeightedGraph<Figure, Object>) graph)
						.getEdgeWeight(edge);
						sw.write(" " + w);
					} catch (ClassCastException e) {
						// we try to cast, but ignore it if impossible
					}
					sw.write("\n");
				}
			}
		}
		sw.flush();
		sw.close();
		jcas.setDocumentText(sw.toString());
		jcas.setDocumentLanguage("");
		GraphMetaData graphAnnotation =
				AnnotationFactory.createAnnotation(jcas, 0, 1,
						GraphMetaData.class);
		graphAnnotation.setGraphClassName(graph.getClass().getCanonicalName());
		graphAnnotation.setEdgeClassName(graph.edgeSet().iterator().next()
				.getClass().getCanonicalName());

	}
}
