package de.unistuttgart.quadrama.io.core;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.apache.commons.csv.CSVPrinter;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.Morpheme;
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
import de.unistuttgart.quadrama.io.core.ExportAsCONLL;

public enum CONLLVariant {
	/**
	 * The default format. Table contains the CoNLL2012 variant.
	 */
	CoNLL2012,
	/**
	 * Dirndl format
	 */
	Dirndl;
	/**
	 * Prints a record representing the header onto p
	 * 
	 * @param p The target
	 * @throws IOException If an I/O error occurs
	 */
	public void header(JCas jcas, CSVPrinter p) throws IOException {
		Drama drama = JCasUtil.selectSingle(jcas, Drama.class);
		switch (this) {
		case Dirndl:
			p.printRecord("#begin document ("
					+ drama.getDocumentUri().split("/")[drama.getDocumentUri().split("/").length - 1] + "."
					+ ExportAsCONLL.conllVariantName + ".conll" + "); part 000");
			break;
		default:
			p.printRecord("#begin document ("
					+ drama.getDocumentUri().split("/")[drama.getDocumentUri().split("/").length - 1] + "."
					+ ExportAsCONLL.conllVariantName + ".conll" + "); part 000");
		}
	}

	public void convert(JCas jcas, CSVPrinter p) throws IOException {
		switch (this) {
		case Dirndl:
			this.convertDirndl(jcas, p);
			break;
		default:
			this.convertCONLL(jcas, p);
		}

	}

	private void convertDirndl(JCas jcas, CSVPrinter p) throws IOException {

		Map<Token, Collection<Mention>> mentionMap = JCasUtil.indexCovering(jcas, Token.class, Mention.class);
		Map<Utterance, Collection<Speaker>> speakerMap = JCasUtil.indexCovered(jcas, Utterance.class, Speaker.class);
		Map<Token, Collection<Utterance>> token2utteranceMap = JCasUtil.indexCovering(jcas, Token.class,
				Utterance.class);
		Drama drama = JCasUtil.selectSingle(jcas, Drama.class);
		Set<Mention> used = new HashSet<Mention>();
		Pattern numberPattern = Pattern.compile("^.*number=(.+?)(\\|.*$|$)");
		Pattern genderPattern = Pattern.compile("^.*gender=(.+?)(\\|.*$|$)");
		for (Sentence sentence : JCasUtil.select(jcas, Sentence.class)) {
			Integer tokenId = 0;
			for (Token token : JCasUtil.selectCovered(Token.class, sentence)) {
				Collection<Utterance> uttList = token2utteranceMap.get(token);
				Collection<Speaker> speakerList = Collections.emptyList();
				for (Utterance utt : uttList) {
					speakerList = speakerMap.get(utt);
				}
				if (token.getCoveredText().equals(" ")) {
					continue;
				}
				used.clear();
				p.print(drama.getDocumentUri().split("/")[drama.getDocumentUri().split("/").length - 1] + "."
						+ ExportAsCONLL.conllVariantName + ".conll");
				p.print("000");
				p.print(tokenId);
				tokenId++;
				p.print(token.getCoveredText()); // Form
				p.print(token.getPos().getPosValue()); // Tag
				p.print("*"); // CFG
				p.print(token.getLemma().getValue()); // Lemma
				List<Morpheme> morph = JCasUtil.selectCovered(Morpheme.class, token);
				String morphTag = morph.get(0).getMorphTag();
				Matcher numberMatcher = numberPattern.matcher(morphTag);
				Matcher genderMatcher = genderPattern.matcher(morphTag);
				if (numberMatcher.find()) {
					p.print(numberMatcher.group(1)); // Number
				} else {
					p.print("-");
				}
				if (genderMatcher.find()) {
					p.print(genderMatcher.group(1)); // Gender
				} else {
					p.print("-");
				}
				if (speakerList.isEmpty()) {
					p.print("_stage");
				} else {
					Speaker speaker = speakerList.iterator().next();
					try {
						p.print(speaker.getCastFigure(0).getXmlId(0)); // Speaker
					} catch (NullPointerException e) {
						p.print("-");
					}
				}
				p.print(printNE(token)); // NE
				p.print("-"); // Tobi
				p.print("-"); // Tone Boundary
				p.print("-"); // Nucleus
				String printId = "-";
				if (mentionMap.containsKey(token)) {
					Collection<Mention> mList = mentionMap.get(token);
					for (Mention m : mList) {
						if (m.getEntity() == null) {
							printId = "-";
						} else {
							if (!used.contains(m)) {
								try {
									if (printId.equals("-")) {
										printId = createBrackets(printId, m, token);
									} else {
										printId = printId + "|" + createBrackets(printId, m, token);
									}
									used.add(m);
								} catch (NullPointerException e) {
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
		p.print("#end document");
		p.println();
	}

	private void convertCONLL(JCas jcas, CSVPrinter p) throws IOException {

		Map<Token, Collection<Mention>> mentionMap = JCasUtil.indexCovering(jcas, Token.class, Mention.class);
		Drama drama = JCasUtil.selectSingle(jcas, Drama.class);
		Set<Mention> used = new HashSet<Mention>();
		Pattern numberPattern = Pattern.compile("^.*number=(.+?)(\\|.*$|$)");
		Pattern genderPattern = Pattern.compile("^.*gender=(.+?)(\\|.*$|$)");
		for (Sentence sentence : JCasUtil.select(jcas, Sentence.class)) {
			Integer tokenId = 0;
			for (Token token : JCasUtil.selectCovered(Token.class, sentence)) {
				if (token.getCoveredText().equals(" ")) {
					continue;
				}
				used.clear();
				p.print(drama.getDocumentUri().split("/")[drama.getDocumentUri().split("/").length - 1] + "."
						+ ExportAsCONLL.conllVariantName + ".conll");
				p.print("000");
				p.print(tokenId);
				tokenId++;
				p.print(token.getCoveredText()); // Form
				p.print(token.getPos().getPosValue()); // Tag
				p.print("*"); // CFG
				p.print(token.getLemma().getValue()); // Lemma
				List<Morpheme> morph = JCasUtil.selectCovered(Morpheme.class, token);
				String morphTag = morph.get(0).getMorphTag();
				Matcher numberMatcher = numberPattern.matcher(morphTag);
				Matcher genderMatcher = genderPattern.matcher(morphTag);
				if (numberMatcher.find()) {
					p.print(numberMatcher.group(1)); // Number
				} else {
					p.print("-");
				}
				if (genderMatcher.find()) {
					p.print(genderMatcher.group(1)); // Gender
				} else {
					p.print("-");
				}
				p.print(printNE(token)); // NE
				String printId = "-";
				if (mentionMap.containsKey(token)) {
					Collection<Mention> mList = mentionMap.get(token);
					for (Mention m : mList) {
						if (m.getEntity() == null) {
							printId = "-";
						} else {
							if (!used.contains(m)) {
								try {
									if (printId.equals("-")) {
										printId = createBrackets(printId, m, token);
									} else {
										printId = printId + "|" + createBrackets(printId, m, token);
									}
									used.add(m);
								} catch (NullPointerException e) {
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
		p.print("#end document");
		p.println();
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
	private String createBrackets(String printId, Mention m, Token token) {
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
	 * This function creates the format for NE required by the CoNLL format.
	 */
	private String printNE(Token token) {
		String str = null;
		List<NamedEntity> ne = JCasUtil.selectCovered(NamedEntity.class, token);
		if (!ne.isEmpty()) {
			if (ne.get(0).getBegin() == token.getBegin() && ne.get(0).getEnd() == token.getEnd()) {
				str = "(" + ne.get(0).getValue().replace("I-", "") + "*)";
			} else if (ne.get(0).getBegin() == token.getBegin()) {
				str = "(" + ne.get(0).getValue().replace("I-", "") + "*";
			} else if (ne.get(0).getEnd() == token.getEnd()) {
				str = "*)";
			} else {
				str = "*";
			}
		} else {
			str = "-";
		}
		return str;
	}
}
