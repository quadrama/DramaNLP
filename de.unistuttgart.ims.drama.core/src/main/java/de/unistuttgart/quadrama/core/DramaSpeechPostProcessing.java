package de.unistuttgart.quadrama.core;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.Level;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unistuttgart.quadrama.core.api.Origin;

/**
 * This component re-adds the annotations done on another view back into the
 * main view.
 * 
 * @author Nils Reiter
 *
 */
public class DramaSpeechPostProcessing extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

		try {
			// map tokens
			JCas utteranceCas = jcas.getView(D.SOFA_UTTERANCES);
			for (Origin origin : JCasUtil.select(utteranceCas, Origin.class)) {
				for (Token token : JCasUtil.selectCovered(Token.class, origin)) {
					int begin = token.getBegin() + origin.getOffset() - origin.getBegin();
					int end = token.getEnd() + origin.getOffset() - origin.getBegin();
					AnnotationFactory.createAnnotation(jcas, begin, end, Token.class);
				}
			}

			// map sentences
			Map<Token, Collection<Origin>> covers = JCasUtil.indexCovering(utteranceCas, Token.class, Origin.class);
			for (Sentence sentence : JCasUtil.select(utteranceCas, Sentence.class)) {
				List<Token> tokens = JCasUtil.selectCovered(utteranceCas, Token.class, sentence);

				// we search for the first and last token
				Token firstToken = tokens.get(0);
				Token lastToken = tokens.get(tokens.size() - 1);
				Origin firstOrigin = null;
				try {
					firstOrigin = covers.get(firstToken).iterator().next();
				} catch (NoSuchElementException e) {
					getLogger().log(Level.SEVERE,
							"Re-mapping of token '" + firstToken.getCoveredText() + "' did not succeed.");
					continue;
				}
				int begin = sentence.getBegin() + firstOrigin.getOffset() - firstOrigin.getBegin();
				Origin lastOrigin = covers.get(lastToken).iterator().next();
				int end = sentence.getEnd() + lastOrigin.getOffset() - lastOrigin.getBegin();

				// annotations in the target view may span non-token content
				AnnotationFactory.createAnnotation(jcas, begin, end, Sentence.class);
			}

		} catch (CASException e) {
			throw new AnalysisEngineProcessException(e);
		}

	}
}
