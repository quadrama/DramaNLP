package de.unistuttgart.quadrama.core;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.factory.JCasBuilder;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.unistuttgart.ims.drama.api.StageDirection;
import de.unistuttgart.quadrama.core.api.Origin;

/**
 * This component creates a new view called {@link SD#SOFA_STAGEDIRECTIONS} and
 * writes all stage directions together with their origin position into this
 * view.
 *
 */
public class DramaStagePreparation extends JCasAnnotator_ImplBase {
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		JCas stageCas = null;
		try {
			// create the new view
			stageCas = jcas.createView(SD.SOFA_STAGEDIRECTIONS);
			stageCas.setDocumentLanguage(jcas.getDocumentLanguage());
		} catch (CASException e1) {
			throw new AnalysisEngineProcessException(e1);
		}

		// fill the jcas stage-direction-wise
		JCasBuilder b = new JCasBuilder(stageCas);
		for (StageDirection sd : JCasUtil.select(jcas, StageDirection.class)) {
			b.add(sd.getCoveredText(), Origin.class).setOffset(sd.getBegin());
			b.add("\n\n");
		}
		b.close();

	}
}