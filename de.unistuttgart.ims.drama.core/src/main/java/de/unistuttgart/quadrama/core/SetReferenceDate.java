package de.unistuttgart.quadrama.core;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.unistuttgart.ims.drama.api.Drama;

/**
 * Based on the three dlina dates, we set the reference date to the earliest
 * possible.
 * 
 * @author reiterns
 *
 */
@TypeCapability(inputs = { "de.unistuttgart.ims.drama.api.Drama:DlinaDateWritten",
		"de.unistuttgart.ims.drama.api.Drama:DlinaDatePrint",
		"de.unistuttgart.ims.drama.api.Drama:DlinaDatePremiere" }, outputs = {
				"de.unistuttgart.ims.drama.api.Drama:ReferenceDate" })

public class SetReferenceDate extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		Drama d = JCasUtil.selectSingle(jcas, Drama.class);

		d.setReferenceDate(2000);

		int date = d.getDlinaDatePremiere();
		if (date != 0 && date < d.getReferenceDate()) {
			d.setReferenceDate(date);
		}
		date = d.getDlinaDatePrint();
		if (date != 0 && date < d.getReferenceDate()) {
			d.setReferenceDate(date);
		}
		date = d.getDlinaDateWritten();
		if (date != 0 && date < d.getReferenceDate()) {
			d.setReferenceDate(date);
		}
	}

}
