package de.unistuttgart.quadrama.core;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.unistuttgart.ims.drama.api.Drama;

public class SetDramaMetaData extends JCasAnnotator_ImplBase {

	public static final String PARAM_AUTHOR = "Author";
	public static final String PARAM_DRAMAID = "Drama Id";

	@ConfigurationParameter(name = PARAM_AUTHOR, mandatory = false)
	String authorName = null;

	@ConfigurationParameter(name = PARAM_DRAMAID, mandatory = false)
	String dramaId = null;

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		Drama drama = JCasUtil.selectSingle(jcas, Drama.class);

		if (authorName != null) drama.setAuthorname(authorName);
		if (dramaId != null) {
			drama.setDramaId(dramaId);
			drama.setDocumentId(dramaId);
		}
	}
}
