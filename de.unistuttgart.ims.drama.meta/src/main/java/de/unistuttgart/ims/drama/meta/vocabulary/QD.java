package de.unistuttgart.ims.drama.meta.vocabulary;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

public class QD {
	/**
	 * <p>
	 * The RDF model that holds the vocabulary terms
	 * </p>
	 */
	private static final Model m_model = ModelFactory.createDefaultModel();

	/**
	 * <p>
	 * The namespace of the vocabalary as a string ({@value})
	 * </p>
	 */
	public static final String NS = "http://github.com/quadrama/metadata/ontology.owl";

	/**
	 * <p>
	 * The namespace of the vocabalary as a string
	 * </p>
	 * 
	 * @see #NS
	 */
	public static String getURI() {
		return NS;
	}

	/**
	 * <p>
	 * The namespace of the vocabalary as a resource
	 * </p>
	 */
	public static final Resource NAMESPACE = m_model.createResource(NS);

	public static final Resource Premiere = m_model
			.createResource("http://github.com/quadrama/metadata/dramav.rdf#Premiere");
	public static final Property premiere = m_model
			.createProperty("http://github.com/quadrama/metadata/dramav.rdf#Premiere");
	public static final Property from = m_model.createProperty("http://github.com/quadrama/metadata/dramav.rdf#from");

}
