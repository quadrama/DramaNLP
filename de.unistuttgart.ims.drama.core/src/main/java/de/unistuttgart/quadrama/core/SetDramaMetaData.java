package de.unistuttgart.quadrama.core;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.unistuttgart.ims.drama.api.Drama;

public class SetDramaMetaData extends JCasAnnotator_ImplBase {

	public static final String PARAM_AUTHOR_NAME = "Author Name";
	public static final String PARAM_AUTHOR_PND = "Author PND";
	public static final String PARAM_DRAMAID = "Drama Id";
	public static final String PARAM_REFERENCEDATE = "Reference Date";

	public static final String PARAM_TRANSLATION = "Translation";
	public static final String PARAM_TRANSLATOR_NAME = "Translator Name";
	public static final String PARAM_TRANSLATOR_PND = "Translator PND";

	@ConfigurationParameter(name = PARAM_AUTHOR_NAME, mandatory = false)
	String authorName = null;

	@ConfigurationParameter(name = PARAM_AUTHOR_PND, mandatory = false)
	String authorPnd = null;

	@ConfigurationParameter(name = PARAM_DRAMAID, mandatory = false)
	String dramaId = null;

	@ConfigurationParameter(name = PARAM_REFERENCEDATE, mandatory = false)
	int referenceDate = -1;

	@ConfigurationParameter(name = PARAM_TRANSLATION, mandatory = false)
	boolean translation = false;

	@ConfigurationParameter(name = PARAM_TRANSLATOR_NAME, mandatory = false)
	String translatorName = null;

	@ConfigurationParameter(name = PARAM_TRANSLATOR_PND, mandatory = false)
	String translatorPnd = null;

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		Drama drama = JCasUtil.selectSingle(jcas, Drama.class);

		if (authorName != null)
			drama.setAuthorname(authorName);
		if (authorPnd != null)
			drama.setAuthorPnd(authorPnd);
		if (dramaId != null)
			drama.setDocumentId(dramaId);
		if (referenceDate > 0)
			drama.setReferenceDate(referenceDate);

		drama.setTranslation(translation);
		if (translatorName != null)
			drama.setTranslatorName(translatorName);
		if (translatorPnd != null)
			drama.setTranslatorPnd(translatorPnd);

	}
}
