package de.unistuttgart.quadrama.graph.ext;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.cas.CASException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.jgrapht.Graph;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.quadrama.graph.ext.api.GraphMetaData;

public class GraphImporter {
	public static Graph<Figure, DefaultWeightedEdge> getGraph(JCas jcas, String viewName)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, CASException {

		JCas graphView = jcas.getView(viewName);

		Map<Integer, Figure> figureMap = new HashMap<Integer, Figure>();
		for (Figure figure : JCasUtil.select(jcas, Figure.class)) {
			figureMap.put(figure.getBegin(), figure);
		}
		GraphMetaData gmd = JCasUtil.selectSingle(graphView, GraphMetaData.class);

		Class<?> cl = Class.forName(gmd.getGraphClassName());
		@SuppressWarnings("unchecked")
		Graph<Figure, DefaultWeightedEdge> graph = (Graph<Figure, DefaultWeightedEdge>) cl.getConstructor(Class.class)
				.newInstance(DefaultWeightedEdge.class);
		boolean weighted = false;
		if (graph instanceof WeightedGraph) {
			weighted = true;
		}

		// SimpleWeightedGraph<Figure, DefaultWeightedEdge> graph =
		// new SimpleWeightedGraph<Figure, DefaultWeightedEdge>(
		// DefaultWeightedEdge.class);
		Pattern pattern;
		if (weighted)
			pattern = Pattern.compile("(-?\\d+) (-?\\d+) (\\d+.\\d+)");
		else
			pattern = Pattern.compile("(-?\\d+) (-?\\d+)");
		for (String line : graphView.getDocumentText().split("\n")) {
			Matcher m = pattern.matcher(line);
			if (m.find()) {
				int sId = Integer.valueOf(m.group(1));
				int tId = Integer.valueOf(m.group(2));

				Figure sFigure = figureMap.get(sId);
				Figure tFigure = figureMap.get(tId);
				if (!graph.containsVertex(sFigure))
					graph.addVertex(sFigure);
				if (!graph.containsVertex(tFigure))
					graph.addVertex(tFigure);
				Object edge = graph.addEdge(sFigure, tFigure);
				if (weighted) {
					double w = Double.valueOf(m.group(3));
					if (edge != null)
						((WeightedGraph<Figure, DefaultWeightedEdge>) graph).setEdgeWeight((DefaultWeightedEdge) edge,
								w);
				}
			}
		}

		return graph;

	}
}
