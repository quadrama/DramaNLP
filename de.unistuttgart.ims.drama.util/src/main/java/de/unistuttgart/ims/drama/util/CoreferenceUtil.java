package de.unistuttgart.ims.drama.util;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unistuttgart.ims.drama.api.Mention;

public class CoreferenceUtil {

	/**
	 * This function checks if a mention's surface form is identical to a token, if
	 * it starts with the token or ends with it and attaches corresponding markers.
	 * This format is compatible to the bracket structure in the CoNLL format.
	 */
	public static String createConllBrackets(String printId, Mention m, Token token) {
		if (m.getBegin() == token.getBegin() && m.getEnd() == token.getEnd()) {
			printId = "(" + m.getEntity().getId() + ")";
		} else if (m.getBegin() == token.getBegin()) {
			printId = "(" + m.getEntity().getId();
		} else if (m.getEnd() == token.getEnd()) {
			printId = m.getEntity().getId() + ")";
		} else {
		}
		return printId;
	}
	
}
