package de.unistuttgart.quadrama.graph;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import de.unistuttgart.quadrama.api.Figure;
import de.unistuttgart.quadrama.api.MainMatter;
import de.unistuttgart.quadrama.api.Scene;
import de.unistuttgart.quadrama.api.Speaker;
import de.unistuttgart.quadrama.graph.ext.GraphExporter;

public class NetworkExtractor extends JCasAnnotator_ImplBase {

	public static final String NETWORK_VIEW = "Network";
	public static final String PARAM_VIEW_NAME = "View Name";

	@ConfigurationParameter(name = PARAM_VIEW_NAME, mandatory = true)
	String viewName = null;

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		WeightedGraph<Figure, DefaultWeightedEdge> graph = null;
		graph =
				extractNetwork(jcas,
						JCasUtil.selectSingle(jcas, MainMatter.class));

		StringWriter sw = new StringWriter();
		GraphExporter gmlExporter = new GraphExporter();
		try {
			System.err.println("Now exporting");
			gmlExporter.export(sw, graph);
			sw.flush();
			sw.close();
			JCas graphView = jcas.createView(viewName);
			graphView.setDocumentText(sw.toString());
			graphView.setDocumentLanguage("");
		} catch (CASException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected WeightedGraph<Figure, DefaultWeightedEdge> extractNetwork(
			JCas jcas, Annotation range) {
		SimpleWeightedGraph<Figure, DefaultWeightedEdge> graph =
				new SimpleWeightedGraph<Figure, DefaultWeightedEdge>(
						DefaultWeightedEdge.class);
		for (Figure figure : JCasUtil.select(jcas, Figure.class)) {

			if (!graph.containsVertex(figure)) {
				graph.addVertex(figure);
			}
		}

		for (Scene scene : JCasUtil.select(jcas, Scene.class)) {
			List<Speaker> speakers =
					JCasUtil.selectCovered(Speaker.class, scene);
			for (Speaker s1 : speakers) {
				Figure gf1 = s1.getFigure();
				if (gf1 != null) for (Speaker s2 : speakers) {
					Figure gf2 = s2.getFigure();
					if (gf2 != null) if (graph.containsEdge(gf1, gf2)) {
						DefaultWeightedEdge edge = graph.getEdge(gf1, gf2);
						double w = graph.getEdgeWeight(edge);
						graph.setEdgeWeight(edge, w + 1.0);
					} else {
						if (gf1 != gf2) graph.addEdge(gf1, gf2);
					}
				}
			};
		}
		return graph;
	}

}
