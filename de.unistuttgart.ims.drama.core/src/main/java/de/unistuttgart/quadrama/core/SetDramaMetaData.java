package de.unistuttgart.quadrama.core;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.unistuttgart.ims.drama.api.Author;
import de.unistuttgart.ims.drama.api.DateReference;
import de.unistuttgart.ims.drama.api.Drama;
import de.unistuttgart.ims.drama.api.Translator;
import de.unistuttgart.ims.drama.util.DramaUtil;

public class SetDramaMetaData extends JCasAnnotator_ImplBase {

	public static final String PARAM_AUTHOR_NAME = "Author Name";
	public static final String PARAM_AUTHOR_PND = "Author PND";
	public static final String PARAM_DRAMAID = "Drama Id";
	public static final String PARAM_DRAMATITLE = "Drama Title";

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

	@ConfigurationParameter(name = PARAM_DRAMATITLE, mandatory = false)
	String dramaTitle = null;

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

		Author author = new Author(jcas);
		author.addToIndexes();
		if (authorName != null)
			author.setName(authorName);
		if (authorPnd != null)
			author.setPnd(authorPnd);
		if (dramaId != null)
			drama.setDocumentId(dramaId);
		if (referenceDate > 0)
			DramaUtil.createFeatureStructure(jcas, DateReference.class).setYear(referenceDate);
		if (dramaTitle != null)
			drama.setDocumentTitle(dramaTitle);

		drama.setTranslation(translation);
		if (translation) {
			Translator translator;
			if (JCasUtil.exists(jcas, Translator.class)) {
				translator = JCasUtil.selectSingle(jcas, Translator.class);
			} else {
				translator = DramaUtil.createFeatureStructure(jcas, Translator.class);
			}
			if (translatorName != null)
				translator.setName(translatorName);
			if (translatorPnd != null)
				translator.setPnd(translatorPnd);
		}
	}
}
