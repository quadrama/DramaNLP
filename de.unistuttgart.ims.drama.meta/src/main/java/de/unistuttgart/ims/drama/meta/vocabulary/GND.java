package de.unistuttgart.ims.drama.meta.vocabulary;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

public class GND {
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
	public static final String NS = "http://d-nb.info/standards/elementset/gnd#";

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

	public static final Resource person = m_model.createResource("http://d-nb.info/standards/elementset/gnd#Person");
	public static final Resource differentiatedPerson = m_model
			.createResource("http://d-nb.info/standards/elementset/gnd#DifferentiatedPerson");
	public static final Resource work = m_model.createResource("http://d-nb.info/standards/elementset/gnd#Work");
	public static final Resource literaryOrLegendaryCharacter = m_model
			.createResource("http://d-nb.info/standards/elementset/gnd#LiteraryOrLegendaryCharacter");

	public static final Resource gender_male = m_model
			.createResource("http://d-nb.info/standards/vocab/gnd/gender#male");
	public static final Resource gender_female = m_model
			.createResource("http://d-nb.info/standards/vocab/gnd/gender#female");
	public static final Resource gender_notknown = m_model
			.createResource("http://d-nb.info/standards/vocab/gnd/gender#notKnown");

	public static final Property variantNameForThePerson = m_model
			.createProperty("http://d-nb.info/standards/elementset/gnd#variantNameForThePerson");
	public static final Property creator = m_model.createProperty("http://d-nb.info/standards/elementset/gnd#creator");
	public static final Property dateOfPublication = m_model
			.createProperty("http://d-nb.info/standards/elementset/gnd#dateOfPublication");
	public static final Property dateOfProduction = m_model
			.createProperty("http://d-nb.info/standards/elementset/gnd#dateOfProduction");
	public static final Property gender = m_model.createProperty("http://d-nb.info/standards/elementset/gnd#gender");

	public static final Property functionOrRoleAsLiteral = m_model
			.createProperty("http://d-nb.info/standards/elementset/gnd#functionOrRoleAsLiteral");

	public static final Property translator = m_model
			.createProperty("http://d-nb.info/standards/elementset/gnd#translator");

	public static final Property associatedDate = m_model
			.createProperty("http://d-nb.info/standards/elementset/gnd#associatedDate");
}
