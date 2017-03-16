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
	public static final String NS = "http://github.com/quadrama/ontology/qdo.rdf#";

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

	public static final Resource Premiere = m_model.createResource(NS + "Premiere");
	public static final Resource GenreAssignment = m_model.createResource(NS + "GenreAssignment");
	public static final Resource LiteraryGenre = m_model.createResource(NS + "LiteraryGenre");
	public static final Resource Drama = m_model.createResource(NS + "Drama");
	public static final Resource Tragedy = m_model.createResource(NS + "Tragedy");
	public static final Property from = m_model.createProperty(NS + "From");
	public static final Property isAnnotated = m_model.createProperty(NS + "isAnnotated");
	public static final Property hasPremiere = m_model.createProperty(NS + "hasPremiere");
	public static final Property hasGenre = m_model.createProperty(NS + "hasGenre");
	public static final Property genreAssignment = m_model.createProperty(NS + "genreAssignment");
	public static final Property certainty = m_model.createProperty(NS + "certainty");
	public static final Property alsoSpelledAs = m_model.createProperty(NS + "alsoSpelledAs");

}
