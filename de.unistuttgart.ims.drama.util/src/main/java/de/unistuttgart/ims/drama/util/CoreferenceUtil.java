package de.unistuttgart.ims.drama.util;

import java.util.Random;

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

	/**
	 * Returns a random entity from a string of entity ids
	 */
	public static String[] getRandomEntity(String[] array) {
		int seed = 42;
		String[] newArray = new String[1];
		int rnd = new Random(seed).nextInt(array.length);
		newArray[0] = array[rnd];
		return newArray;
	}

}
