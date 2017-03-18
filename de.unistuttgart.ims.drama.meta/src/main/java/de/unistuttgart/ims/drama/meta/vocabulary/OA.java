package de.unistuttgart.ims.drama.meta.vocabulary;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

public class OA {
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
	public static final String NS = "http://www.w3.org/ns/oa#";

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

	public static final Resource Annotation = m_model.createResource("http://www.w3.org/ns/oa#Annotation");
	public static final Resource TextPositionSelector = m_model
			.createResource("http://www.w3.org/ns/oa#TextPositionSelector");
	public static final Property hasTarget = m_model.createProperty("http://www.w3.org/ns/oa#hasTarget");
	public static final Property hasBody = m_model.createProperty("http://www.w3.org/ns/oa#hasBody");
	public static final Property start = m_model.createProperty("http://www.w3.org/ns/oa#start");
	public static final Property end = m_model.createProperty("http://www.w3.org/ns/oa#end");

}
