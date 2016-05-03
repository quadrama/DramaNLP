package de.unistuttgart.quadrama.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.unistuttgart.quadrama.api.Figure;

public class FigureReferenceAnnotator extends JCasAnnotator_ImplBase {

	Pattern pattern = Pattern.compile("\\p{Punct}", 0);

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		for (Figure figure : JCasUtil.select(jcas, Figure.class)) {
			String s = figure.getCoveredText();
			Matcher m = pattern.matcher(s);
			if (m.find()) {
				figure.setReference(s.substring(0, m.start()));
			} else {
				figure.setReference(s);
			}
		}
	}

}
