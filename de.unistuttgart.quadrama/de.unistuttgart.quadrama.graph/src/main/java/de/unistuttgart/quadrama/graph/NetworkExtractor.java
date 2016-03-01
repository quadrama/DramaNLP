package de.unistuttgart.quadrama.graph;

import java.io.IOException;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedPseudograph;
import org.jgrapht.graph.SimpleWeightedGraph;

import de.unistuttgart.quadrama.api.Figure;
import de.unistuttgart.quadrama.api.FigureMention;
import de.unistuttgart.quadrama.api.MainMatter;
import de.unistuttgart.quadrama.api.Scene;
import de.unistuttgart.quadrama.api.Speaker;
import de.unistuttgart.quadrama.api.Utterance;
import de.unistuttgart.quadrama.graph.ext.GraphExporter;

public class NetworkExtractor extends JCasAnnotator_ImplBase {

	public static final String PARAM_VIEW_NAME = "View Name";
	public static final String PARAM_NETWORK_TYPE = "Network Type";

	@ConfigurationParameter(name = PARAM_VIEW_NAME, mandatory = true)
	String viewName = null;

	@ConfigurationParameter(name = PARAM_NETWORK_TYPE,
			defaultValue = "Copresence", mandatory = false)
	NetworkType networkType = null;

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		Graph<Figure, ? extends Object> graph = null;
		switch (networkType) {
		case MentionNetwork:
			graph = extractMentionNetwork(jcas);
			break;
		case Copresence:
		default:
			graph =
			extractNetwork(jcas,
					JCasUtil.selectSingle(jcas, MainMatter.class));
		}
		GraphExporter gmlExporter = new GraphExporter();
		try {
			JCas graphView = jcas.createView(viewName);
			System.err.println("Now exporting");
			gmlExporter.export(graphView, graph);

		} catch (CASException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected DirectedGraph<Figure, DefaultEdge>
	extractMentionNetwork(JCas jcas) {
		DirectedGraph<Figure, DefaultEdge> graph =
				new DirectedPseudograph<Figure, DefaultEdge>(DefaultEdge.class);

		for (Utterance utterance : JCasUtil.select(jcas, Utterance.class)) {
			Speaker speaker =
					JCasUtil.selectCovered(Speaker.class, utterance).get(0);
			for (FigureMention mention : JCasUtil.selectCovered(jcas,
					FigureMention.class, utterance)) {
				if (speaker.getFigure() != null && mention.getFigure() != null) {
					if (!graph.containsVertex(speaker.getFigure()))
						graph.addVertex(speaker.getFigure());
					if (!graph.containsVertex(mention.getFigure()))
						graph.addVertex(mention.getFigure());
					graph.addEdge(speaker.getFigure(), mention.getFigure());
				}
			}
		}
		return graph;
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
