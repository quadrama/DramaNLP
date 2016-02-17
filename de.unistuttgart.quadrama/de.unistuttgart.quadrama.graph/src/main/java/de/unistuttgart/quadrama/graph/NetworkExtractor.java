package de.unistuttgart.quadrama.graph;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.TransformerConfigurationException;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.jgrapht.WeightedGraph;
import org.jgrapht.ext.GraphMLExporter;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.xml.sax.SAXException;

import de.unistuttgart.quadrama.api.MainMatter;
import de.unistuttgart.quadrama.api.Scene;
import de.unistuttgart.quadrama.api.Speaker;

public class NetworkExtractor extends JCasAnnotator_ImplBase {

	public static final String PARAM_BYACT = "By Act";

	@ConfigurationParameter(name = PARAM_BYACT, mandatory = false)
	boolean networkByAct = false;

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		WeightedGraph<GraphFigure, DefaultWeightedEdge> graph = null;
		if (networkByAct) {} else {
			graph =
					this.extractNetwork(jcas,
							JCasUtil.selectSingle(jcas, MainMatter.class));
		}

		StringWriter sw = new StringWriter();
		GraphMLExporter<GraphFigure, DefaultWeightedEdge> gmlExporter =
				new GraphMLExporter<GraphFigure, DefaultWeightedEdge>();
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

	protected WeightedGraph<GraphFigure, DefaultWeightedEdge> extractNetwork(
			JCas jcas, Annotation range) {
		SimpleWeightedGraph<GraphFigure, DefaultWeightedEdge> graph =
				new SimpleWeightedGraph<GraphFigure, DefaultWeightedEdge>(
						DefaultWeightedEdge.class);
		for (Speaker speaker : JCasUtil.selectCovered(Speaker.class, range)) {
			GraphFigure figure = GraphFigure.getGraphFigure(speaker);

			if (!graph.containsVertex(figure)) {
				graph.addVertex(figure);
			}
		}

		for (Scene scene : JCasUtil.select(jcas, Scene.class)) {
			List<Speaker> speakers =
					JCasUtil.selectCovered(Speaker.class, scene);
			for (Speaker s1 : speakers) {
				GraphFigure gf1 = GraphFigure.getGraphFigure(s1);
				for (Speaker s2 : speakers) {
					GraphFigure gf2 = GraphFigure.getGraphFigure(s2);

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

	public static class GraphFigure {
		static Map<Integer, GraphFigure> map =
				new HashMap<Integer, GraphFigure>();

		int id;
		String name;

		private GraphFigure(int id, String name) {
			super();
			this.id = id;
			this.name = name;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public static GraphFigure getGraphFigure(Speaker speaker) {
			if (!map.containsKey(speaker.getId()))
				map.put(speaker.getId(), new GraphFigure(speaker.getId(),
						speaker.getCoveredText()));
			return map.get(speaker.getId());
		}

		@Override
		public boolean equals(Object o) {
			return this.getId() == ((GraphFigure) o).getId();
		}

	}

}
