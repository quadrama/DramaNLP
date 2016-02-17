package de.unistuttgart.quadrama.graph.ext;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import de.unistuttgart.quadrama.api.Figure;

public class GraphImporter {
	public static WeightedGraph<Figure, DefaultWeightedEdge> getGraph(String s,
			Map<Integer, Figure> figureMap) {
		SimpleWeightedGraph<Figure, DefaultWeightedEdge> graph =
				new SimpleWeightedGraph<Figure, DefaultWeightedEdge>(
						DefaultWeightedEdge.class);
		Pattern pattern = Pattern.compile("(\\d+) (\\d+) (\\d.\\d+)");
		for (String line : s.split("\n")) {
			Matcher m = pattern.matcher(line);
			int sId = Integer.valueOf(m.group(1));
			int tId = Integer.valueOf(m.group(2));
			double w = Double.valueOf(m.group(3));
			Figure sFigure = figureMap.get(sId);
			Figure tFigure = figureMap.get(tId);
			if (!graph.containsVertex(sFigure)) graph.addVertex(sFigure);
			if (!graph.containsVertex(tFigure)) graph.addVertex(tFigure);
			DefaultWeightedEdge edge = graph.addEdge(sFigure, tFigure);
			graph.setEdgeWeight(edge, w);
		}

		return graph;

	}
}
