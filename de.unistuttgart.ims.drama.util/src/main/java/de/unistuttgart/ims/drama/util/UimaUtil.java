package de.unistuttgart.ims.drama.util;

import java.util.Collection;
import java.util.Iterator;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;

public class UimaUtil {

	public static StringArray toStringArray(JCas jcas, Collection<String> coll) {
		StringArray arr = new StringArray(jcas, coll.size());
		Iterator<String> collIter = coll.iterator();
		int i = 0;
		while (collIter.hasNext()) {
			arr.set(i++, collIter.next());
		}
		return arr;
	}

}
