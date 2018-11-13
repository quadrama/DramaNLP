package de.unistuttgart.quadrama.io.core;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.csv.CSVPrinter;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.unistuttgart.ims.drama.api.Act;
import de.unistuttgart.ims.drama.api.Author;
import de.unistuttgart.ims.drama.api.CastFigure;
import de.unistuttgart.ims.drama.api.DiscourseEntity;
import de.unistuttgart.ims.drama.api.Drama;
import de.unistuttgart.ims.drama.api.Mention;
import de.unistuttgart.ims.drama.api.Scene;
import de.unistuttgart.ims.drama.api.Speaker;
import de.unistuttgart.ims.drama.api.StageDirection;
import de.unistuttgart.ims.drama.api.Translator;
import de.unistuttgart.ims.drama.api.Utterance;
import de.unistuttgart.ims.drama.util.DramaUtil;

public enum CONLLVariant {
	/**
	 * The default format. Table contains the CoNLL2012 variant.
	 */
	CoNLL2012;
	/**
	 * Prints a record representing the header onto p
	 * 
	 * @param p The target
	 * @throws IOException If an I/O error occurs
	 */
	public void header(JCas jcas, CSVPrinter p) throws IOException {
		switch (this) {
		default:
			p.printRecord(
					"#begin document (" + JCasUtil.selectSingle(jcas, Drama.class).getDocumentId() + "); part 000");
		}
	}

	public void convert(JCas jcas, CSVPrinter p) throws IOException {
		switch (this) {
		default:
			this.convertCONLL(jcas, p);
		}

	}

	private void convertCONLL(JCas jcas, CSVPrinter p) throws IOException {
		Map<Token, Collection<Mention>> mentionMap = JCasUtil.indexCovering(jcas, Token.class, Mention.class);
		Drama drama = JCasUtil.selectSingle(jcas, Drama.class);
		Set<Mention> used = new HashSet<Mention>();
		for (Utterance utterance : JCasUtil.select(jcas, Utterance.class)) {
			for (Sentence sentence : JCasUtil.selectCovered(Sentence.class, utterance)) {
				Integer tokenId = 0;
				for (Token token : JCasUtil.selectCovered(Token.class, sentence)) {
					used.clear();
					p.print(drama.getDocumentId());
					p.print(0);
					p.print(tokenId);
					tokenId++;
					p.print(token.getCoveredText());
					p.print("-");
					p.print("-");
					p.print("-");
					p.print("-");
					p.print("-");
					p.print("*");
					String printId = null;
					if (mentionMap.containsKey(token)) {
						Collection<Mention> mList = mentionMap.get(token);
						for (Mention m : mList) {
							if (m.getEntity() == null) {
								printId = null;
							} else {
								if (!used.contains(m)) {
									try {
										if (printId == null) {
											printId = createBrackets(m, token);
										} else {
											printId = printId + "|" + createBrackets(m, token);
										}
										used.add(m);
									} catch (Exception e) {
										//
									}
								}
							}
						}
					} else {
						//
					}
					p.print(printId);
					p.println();
				}
				p.println();
			}
		}
	}

	/**
	 * This function returns the longest from a given collection of annotations.
	 * Length measured as <code>end - begin</code>.
	 * 
	 * @param coll The annotation collection
	 * @return The longest of the annotation.
	 */
	@Deprecated
	private <T extends Annotation> T selectLongest(Collection<T> coll) {
		int l = -1;
		T fm = null;
		for (T ment : coll) {
			int cl = ment.getEnd() - ment.getBegin();
			if (cl > l) {
				l = cl;
				fm = ment;
			}
		}
		if (fm == null)
			return coll.iterator().next();
		else
			return fm;
	}

	/**
	 * This function checks if a mention's surface form is identical to a token, if
	 * it starts with the token or ends with it and attaches corresponding markers.
	 */
	private String createBrackets(Mention m, Token token) {
		String printId = null;
		if (m.getCoveredText().equals(token.getCoveredText())) {
			printId = "(" + m.getEntity().getId() + ")";
		} else if (m.getCoveredText().startsWith(token.getCoveredText())) {
			printId = "(" + m.getEntity().getId();
		} else if (m.getCoveredText().endsWith(token.getCoveredText())) {
			printId = m.getEntity().getId() + ")";
		} else {
		}
		return printId;
	}
}
