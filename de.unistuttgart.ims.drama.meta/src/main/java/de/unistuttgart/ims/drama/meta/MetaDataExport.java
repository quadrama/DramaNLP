package de.unistuttgart.ims.drama.meta;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.Ontology;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdfxml.xmloutput.impl.BaseXMLWriter;
import org.apache.jena.rdfxml.xmloutput.impl.Basic;
import org.apache.jena.vocabulary.DC_11;
import org.apache.jena.vocabulary.OWL2;
import org.apache.jena.vocabulary.RDFS;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.unistuttgart.ims.drama.api.Author;
import de.unistuttgart.ims.drama.api.Date;
import de.unistuttgart.ims.drama.api.DatePremiere;
import de.unistuttgart.ims.drama.api.DatePrint;
import de.unistuttgart.ims.drama.api.DateWritten;
import de.unistuttgart.ims.drama.api.Drama;
import de.unistuttgart.ims.drama.api.DramatisPersonae;
import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.ims.drama.api.Translator;
import de.unistuttgart.ims.drama.meta.vocabulary.GND;
import de.unistuttgart.ims.drama.meta.vocabulary.QD;

public class MetaDataExport extends JCasAnnotator_ImplBase {

	public static final String PARAM_OUTPUT = "Output";

	@ConfigurationParameter(name = PARAM_OUTPUT, mandatory = false)
	String outputFileName = null;

	OntModel model;

	@Override
	public void initialize(final UimaContext context) throws ResourceInitializationException {
		super.initialize(context);

		model = ModelFactory.createOntologyModel(); // .createDefaultModel();
		model.setNsPrefix("gndo", "http://d-nb.info/standards/elementset/gnd#");
		model.setNsPrefix("gnd", "http://d-nb.info/gnd/");
		model.setNsPrefix("dc", "http://purl.org/dc/elements/1.1/");
		Ontology o = model.createOntology("http://github.com/quadrama/metadata/ontology.owl");
		o.setRDFType(OWL2.Ontology);
		o.addImport(model.createResource("http://d-nb.info/standards/elementset/gnd.rdf"));
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		Drama d = JCasUtil.selectSingle(jcas, Drama.class);
		Resource dramaResource = model.createIndividual("http://textgridrep.org/textgrid:" + d.getDocumentId(),
				GND.work);
		dramaResource.addProperty(DC_11.title, d.getDocumentTitle());
		dramaResource.addProperty(RDFS.label, d.getDocumentTitle());
		for (Author a : JCasUtil.select(jcas, Author.class)) {
			Resource authorResource;
			if (a.getPnd() != null) {
				authorResource = model.createIndividual("http://d-nb.info/gnd/" + a.getPnd(), GND.differentiatedPerson);
			} else {
				authorResource = model.createResource(GND.person);
			}
			dramaResource.addProperty(GND.creator, authorResource);
			authorResource.addProperty(GND.variantNameForThePerson, a.getName());
		}

		for (Date date : JCasUtil.select(jcas, DateWritten.class)) {
			int year = date.getYear();
			dramaResource.addProperty(GND.dateOfProduction, String.valueOf(year));
		}

		for (Date date : JCasUtil.select(jcas, DatePrint.class)) {
			int year = date.getYear();
			dramaResource.addProperty(GND.dateOfPublication, String.valueOf(year));
		}
		for (Date date : JCasUtil.select(jcas, DatePremiere.class)) {
			int year = date.getYear();
			Resource rep = model.createResource(QD.Premiere);
			rep.addProperty(RDFS.label, "Premiere of " + d.getDocumentTitle());
			rep.addProperty(GND.associatedDate, String.valueOf(year));
			dramaResource.addProperty(QD.premiere, rep);
		}

		for (Translator a : JCasUtil.select(jcas, Translator.class)) {
			Resource authorResource;
			if (a.getPnd() != null) {
				authorResource = model.createIndividual("http://d-nb.info/gnd/" + a.getPnd(), GND.differentiatedPerson);
			} else {
				authorResource = model.createResource(GND.person);
			}
			dramaResource.addProperty(GND.translator, authorResource);
			authorResource.addProperty(GND.variantNameForThePerson, a.getName());

		}

		for (DramatisPersonae dp : JCasUtil.select(jcas, DramatisPersonae.class)) {
			for (Figure f : JCasUtil.selectCovered(jcas, Figure.class, dp)) {
				String s = f.getReference();
				s = s.split(",")[0];
				// String[] p = s.split(" ");
				// s = p[p.length - 1];
				Resource figureResource = model.createResource(
						"http://textgridrep.org/textgrid:" + d.getDocumentId() + "#" + s.toLowerCase(),
						GND.literaryOrLegendaryCharacter);
				figureResource.addProperty(QD.from, dramaResource);
				figureResource.addProperty(RDFS.label, s);
				if (f.getName() != null)
					figureResource.addProperty(GND.variantNameForThePerson, f.getName().getCoveredText());
				figureResource.addProperty(GND.variantNameForThePerson, f.getCoveredText());

				if (f.getGender() != null)
					figureResource.addProperty(GND.gender, f.getGender());
				if (f.getDescription() != null)
					figureResource.addProperty(GND.functionOrRoleAsLiteral, f.getDescription().getCoveredText());
			}
		}

	}

	@Override
	public void collectionProcessComplete() throws AnalysisEngineProcessException {
		BaseXMLWriter xmlWriter = new Basic();

		if (outputFileName == null) {
			xmlWriter.write(model, System.out, "");
			System.out.flush();
		} else {
			Writer w = null;
			try {
				w = new FileWriter(new File(outputFileName));
				xmlWriter.write(model, w, "");
				w.flush();
				w.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				IOUtils.closeQuietly(w);
			}

		}
	}

}
