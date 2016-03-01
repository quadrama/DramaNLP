package de.unistuttgart.quadrama.graph;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.WeightedGraph;

/**
 * 
 * @author reiterns
 *
 */
public enum NetworkType {
	/**
	 * Two vertices are connected if the figures are co-present within a scene
	 */
	Copresence,
	/**
	 * Directed network. Two vertices are connected, if the first figure
	 * mentions the second.
	 */
	MentionNetwork;

	@SuppressWarnings("rawtypes")
	public static Class<? extends Graph> getClassForNetwork(NetworkType type) {
		switch (type) {
		case MentionNetwork:
			return DirectedGraph.class;
		default:
			return WeightedGraph.class;
		}

	}
}
