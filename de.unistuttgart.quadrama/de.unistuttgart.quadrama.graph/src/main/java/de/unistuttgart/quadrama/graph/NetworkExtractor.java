package de.unistuttgart.quadrama.graph;

import java.io.StringWriter;
import java.util.List;

import javax.xml.transform.TransformerConfigurationException;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.jgrapht.WeightedGraph;
import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.GraphMLExporter;
import org.jgrapht.ext.VertexNameProvider;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.xml.sax.SAXException;

import de.unistuttgart.quadrama.api.Figure;
import de.unistuttgart.quadrama.api.MainMatter;
import de.unistuttgart.quadrama.api.Scene;
import de.unistuttgart.quadrama.api.Speaker;

public class NetworkExtractor extends JCasAnnotator_ImplBase {

	public static final String PARAM_BYACT = "By Act";

	@ConfigurationParameter(name = PARAM_BYACT, mandatory = false)
	boolean networkByAct = false;

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		WeightedGraph<Figure, DefaultWeightedEdge> graph = null;
		if (networkByAct) {} else {
			graph =
					this.extractNetwork(jcas,
							JCasUtil.selectSingle(jcas, MainMatter.class));
		}

		StringWriter sw = new StringWriter();
		GraphMLExporter<Figure, DefaultWeightedEdge> gmlExporter =
				new GraphMLExporter<Figure, DefaultWeightedEdge>(
						new VertexNameProvider<Figure>() {

							public String getVertexName(Figure vertex) {
								return String.valueOf(vertex.getId());
							}
						}, new VertexNameProvider<Figure>() {

							public String getVertexName(Figure vertex) {
								return vertex.getCoveredText();
							}
						}, new EdgeNameProvider<DefaultWeightedEdge>() {

							public String getEdgeName(DefaultWeightedEdge edge) {
								return String.valueOf(edge.hashCode());
							}
						}, null);
		try {
			gmlExporter.export(sw, graph);
			JCas graphView = jcas.createView("Graph");
			graphView.setDocumentText(sw.toString());
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (CASException e) {
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
				for (Speaker s2 : speakers) {
					Figure gf2 = s2.getFigure();

					if (graph.containsEdge(gf1, gf2)) {
						DefaultWeightedEdge edge = graph.getEdge(gf1, gf2);
						double w = graph.getEdgeWeight(edge);
						graph.setEdgeWeight(edge, w + 1.0);
					} else {
						if (s1.getId() != s2.getId()) graph.addEdge(gf1, gf2);
					}
				}
			};
		}
		return graph;
	}

}
