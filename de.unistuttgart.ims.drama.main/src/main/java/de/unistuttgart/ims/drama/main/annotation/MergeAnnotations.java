package de.unistuttgart.ims.drama.main.annotation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.xml.sax.SAXException;

import de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceChain;
import de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceLink;

public class MergeAnnotations extends JCasAnnotator_ImplBase {

	public static final String PARAM_SOURCE_LOCATION = "Source Location";

	@ConfigurationParameter(name = PARAM_SOURCE_LOCATION)
	String sourceFilename;

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		JCas importJcas;
		try {
			importJcas = JCasFactory.createJCas();
			XmiCasDeserializer.deserialize(new FileInputStream(new File(this.sourceFilename)), importJcas.getCas(),
					true);
		} catch (SAXException | IOException e) {
			throw new AnalysisEngineProcessException(e);
		} catch (UIMAException e) {
			throw new AnalysisEngineProcessException(e);
		}

		for (CoreferenceChain chain : JCasUtil.select(importJcas, CoreferenceChain.class)) {
			CoreferenceChain newChain = new CoreferenceChain(jcas);
			newChain.addToIndexes();
			CoreferenceLink oldCurrent = chain.getFirst();
			CoreferenceLink newCurrent = null;
			while (oldCurrent != null) {
				CoreferenceLink newLink = AnnotationFactory.createAnnotation(jcas, oldCurrent.getBegin(),
						oldCurrent.getEnd(), CoreferenceLink.class);
				if (newChain.getFirst() == null)
					newChain.setFirst(newLink);
				if (newCurrent != null)
					newCurrent.setNext(newLink);
				newCurrent = newLink;
				oldCurrent = oldCurrent.getNext();
			}

		}

	}

}
