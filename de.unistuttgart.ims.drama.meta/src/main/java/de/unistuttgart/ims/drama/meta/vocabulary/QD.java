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
	public static final Resource DramaticFigure = m_model.createResource(NS + "DramaticFigure");
	public static final Resource DramaticDifferentiatedFigure = m_model
			.createResource(NS + "DramaticDifferentiatedFigure");
	public static final Resource DramaticUndifferentiatedFigure = m_model
			.createResource(NS + "DramaticUndifferentiatedFigure");
	public static final Resource DramaticGroup = m_model.createResource(NS + "DramaticGroup");
	public static final Resource GenreAssignment = m_model.createResource(NS + "GenreAssignment");
	public static final Resource LiteraryGenre = m_model.createResource(NS + "LiteraryGenre");
	public static final Resource LiteraryPeriod = m_model.createResource(NS + "LiteraryPeriod");
	public static final Resource Drama = m_model.createResource(NS + "Drama");
	public static final Resource Tragedy = m_model.createResource(NS + "Tragedy");
	public static final Property from = m_model.createProperty(NS + "From");
	public static final Property hasPremiere = m_model.createProperty(NS + "hasPremiere");
	public static final Property hasGenre = m_model.createProperty(NS + "hasGenre");
	public static final Property genreAssignment = m_model.createProperty(NS + "genreAssignment");
	public static final Property certainty = m_model.createProperty(NS + "certainty");
	public static final Property alsoSpelledAs = m_model.createProperty(NS + "alsoSpelledAs");
	public static final Property isClassifiedAs = m_model.createProperty(NS + "isClassifiedAs");
	public static final Property associatedWith = m_model.createProperty(NS + "associatedWith");
	public static final Property isRelated = m_model.createProperty(NS + "isRelated");
	public static final Property siblingOf = m_model.createProperty(NS + "siblingOf");
	public static final Property spouseOf = m_model.createProperty(NS + "spouseOf");
	public static final Property childOf = m_model.createProperty(NS + "childOf");
	public static final Property parentOf = m_model.createProperty(NS + "parentOf");

}
