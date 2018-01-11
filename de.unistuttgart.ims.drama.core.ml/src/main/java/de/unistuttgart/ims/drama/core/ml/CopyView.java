package de.unistuttgart.ims.drama.core.ml;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.CasCopier;

public class CopyView extends JCasAnnotator_ImplBase {
	/**
	 * The parameter name for the name of the source view
	 */
	public static final String PARAM_SOURCE_VIEW_NAME = "sourceViewName";
	@ConfigurationParameter(mandatory = true)
	private String sourceViewName;

	/**
	 * The parameter name for the name of the destination view
	 */
	public static final String PARAM_DESTINATION_VIEW_NAME = "destinationViewName";
	@ConfigurationParameter(mandatory = true)
	private String destinationViewName;

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		JCas destinationView;

		try {
			try {
				destinationView = jcas.getView(destinationViewName);
			} catch (CASRuntimeException | CASException ce) {
				destinationView = jcas.createView(destinationViewName);
			}
			CasCopier cc = new CasCopier(jcas.getView(sourceViewName).getCas(), destinationView.getCas());
			cc.copyCasView(jcas.getView(sourceViewName).getCas(), destinationView.getCas(), true);
		} catch (CASException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

}
