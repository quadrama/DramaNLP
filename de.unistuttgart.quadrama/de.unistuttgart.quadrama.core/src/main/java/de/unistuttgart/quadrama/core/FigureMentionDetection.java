package de.unistuttgart.quadrama.core;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unistuttgart.quadrama.api.Figure;
import de.unistuttgart.quadrama.api.FigureMention;
import de.unistuttgart.quadrama.api.Speech;

public class FigureMentionDetection extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		for (Speech speech : JCasUtil.select(jcas, Speech.class)) {
			for (Figure figure : JCasUtil.select(jcas, Figure.class)) {
				for (Token token : JCasUtil.selectCovered(Token.class, speech)) {
					if (token.getCoveredText().equalsIgnoreCase(
							figure.getCoveredText())) {
						FigureMention fm =
								AnnotationFactory.createAnnotation(jcas,
										token.getBegin(), token.getEnd(),
										FigureMention.class);
						fm.setFigure(figure);
					}
				}
			}
		}
	}

}
