package de.unistuttgart.quadrama.graph.ext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.jgrapht.Graph;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.ims.drama.api.Figure;

public class TestGraphIO {
	JCas jcas;

	Figure[] figures = new Figure[15];

	Random random;

	@Before
	public void setUp() throws UIMAException {

		jcas = JCasFactory.createJCas();
		jcas.setDocumentText(StringUtils.repeat("bla", 50));
		jcas.setDocumentLanguage("de");

		for (int i = 0; i < figures.length; i++) {
			figures[i] = AnnotationFactory.createAnnotation(jcas, i * 2, i * 2 + 1, Figure.class);
			figures[i].setId(i);
		}

		random = new Random();
	}

	@Test
	public void testGraphIO()
			throws IOException, CASException, ClassNotFoundException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		WeightedGraph<Figure, DefaultWeightedEdge> graph = new SimpleWeightedGraph<Figure, DefaultWeightedEdge>(
				DefaultWeightedEdge.class);
		for (int i = 0; i < figures.length; i++) {
			graph.addVertex(figures[i]);
		}
		for (int i = 0; i < figures.length - 1; i++) {
			DefaultWeightedEdge edge = graph.addEdge(figures[i], figures[i + 1]);
			graph.setEdgeWeight(edge, random.nextDouble());
		}

		GraphExporter ge = new GraphExporter();
		ge.export(jcas.createView("Testview"), graph);

		Graph<Figure, DefaultWeightedEdge> g2 = GraphImporter.getGraph(jcas, "Testview");

		assertNotNull(g2);
		assertFalse(g2.vertexSet().isEmpty());
		for (int i = 0; i < figures.length; i++) {
			for (int j = 0; j < figures.length; j++) {
				assertEquals(graph.containsEdge(figures[i], figures[j]), g2.containsEdge(figures[i], figures[j]));
				if (graph.containsEdge(figures[i], figures[j])) {
					assertEquals(graph.getEdgeWeight(graph.getEdge(figures[i], figures[j])),
							g2.getEdgeWeight(g2.getEdge(figures[i], figures[j])), 0.1);
				}
			}
		}

	}
}
