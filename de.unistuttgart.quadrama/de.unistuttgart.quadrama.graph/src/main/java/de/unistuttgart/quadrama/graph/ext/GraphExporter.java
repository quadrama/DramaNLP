package de.unistuttgart.quadrama.graph.ext;

import java.io.IOException;
import java.io.Writer;

import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import de.unistuttgart.quadrama.api.Figure;

public class GraphExporter {

	public void export(Writer writer,
			WeightedGraph<Figure, DefaultWeightedEdge> graph)
			throws IOException {
		for (Figure figure1 : graph.vertexSet()) {
			for (Figure figure2 : graph.vertexSet()) {
				if (graph.containsEdge(figure1, figure2)) {
					DefaultWeightedEdge edge = graph.getEdge(figure1, figure2);
					double w = graph.getEdgeWeight(edge);
					writer.write(figure1.getId() + " " + figure2.getId() + " "
							+ w + "\n");
				}
			}
		}
	}
}
