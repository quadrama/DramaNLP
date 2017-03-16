package de.unistuttgart.quadrama.core;

import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.unistuttgart.ims.drama.api.Figure;

@TypeCapability(inputs = { "de.unistuttgart.ims.drama.api.Figure" }, outputs = {
		"de.unistuttgart.ims.drama.api.Figure:Reference" })
public class FigureReferenceAnnotator extends JCasAnnotator_ImplBase {

	Pattern pattern = Pattern.compile("\\p{Punct}", 0);

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		FigureReferenceFactory fact = new FigureReferenceFactory();

		for (Figure figure : JCasUtil.select(jcas, Figure.class)) {
			fact.addFigure(figure);
		}

		fact.done();
	}

}
