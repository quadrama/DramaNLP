package de.unistuttgart.quadrama.core;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.unistuttgart.ims.drama.api.Date;
import de.unistuttgart.ims.drama.api.DateReference;
import de.unistuttgart.ims.drama.util.DramaUtil;

/**
 * Based on the three dlina dates, we set the reference date to the earliest
 * possible.
 * 
 * @author reiterns
 *
 */
@TypeCapability(inputs = { "de.unistuttgart.ims.drama.api.DateWritten", "de.unistuttgart.ims.drama.api.DatePrint",
		"de.unistuttgart.ims.drama.api.DatePremiere" }, outputs = { "de.unistuttgart.ims.drama.api.DateReference" })

public class SetReferenceDate extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

		int year = Integer.MAX_VALUE;
		for (Date date : JCasUtil.select(jcas, Date.class)) {
			if (date.getYear() <= year) {
				year = date.getYear();
			}
		}

		if (year != Integer.MAX_VALUE) {
			try {
				JCasUtil.selectSingle(jcas, DateReference.class).setYear(year);
			} catch (IllegalArgumentException e) {
				DramaUtil.createFeatureStructure(jcas, DateReference.class).setYear(year);
			}
		}

	}

}
