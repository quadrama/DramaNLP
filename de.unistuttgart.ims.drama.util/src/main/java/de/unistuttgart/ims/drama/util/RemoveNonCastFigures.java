package de.unistuttgart.ims.drama.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.unistuttgart.ims.drama.api.Mention;

/**
 * Removes all mentions that do not refer to a character.
 * 
 * @since 2.2.0
 */
public class RemoveNonCastFigures extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		ArrayList<Mention> nonCastFigureMentions = new ArrayList<Mention>();
		for (Mention mention : JCasUtil.select(jcas, Mention.class)) {
			if (mention.getEntity() != null) {
				if (!mention.getEntity().getClass().getSimpleName().equals("CastFigure")) {
					nonCastFigureMentions.add(mention);
				}
			}
		}
		for (Mention m : nonCastFigureMentions) {
			m.removeFromIndexes(jcas);
		}
	}
}