package de.unistuttgart.quadrama.core;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.unistuttgart.ims.drama.api.Figure;

@TypeCapability(inputs = { "de.unistuttgart.ims.drama.api.Figure" }, outputs = {
		"de.unistuttgart.ims.drama.api.Figure:Reference" })
public class FigureReferenceAnnotator extends JCasAnnotator_ImplBase {

	Pattern pattern = Pattern.compile("\\p{Punct}", 0);

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		Set<String> usedReferences = new HashSet<String>();

		for (Figure figure : JCasUtil.select(jcas, Figure.class)) {
			String s = figure.getCoveredText();
			Matcher m = pattern.matcher(s);
			if (m.find()) {
				String refString = s.substring(0, m.start());
				if (usedReferences.contains(refString)) {
					figure.setReference(s);
					getLogger().warn(DocumentMetaData.get(jcas).getDocumentId()
							+ " contains ambiguous reference string(s). Only asigned to the first occurrence.");
				} else {
					usedReferences.add(refString);
					figure.setReference(refString);
				}
			} else {
				figure.setReference(s);
			}
		}
	}

}
