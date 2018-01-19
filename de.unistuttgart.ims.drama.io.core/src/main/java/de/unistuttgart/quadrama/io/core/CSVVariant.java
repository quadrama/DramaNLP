package de.unistuttgart.quadrama.io.core;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.csv.CSVPrinter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unistuttgart.ims.drama.api.Act;
import de.unistuttgart.ims.drama.api.Author;
import de.unistuttgart.ims.drama.api.CastFigure;
import de.unistuttgart.ims.drama.api.DiscourseEntity;
import de.unistuttgart.ims.drama.api.Drama;
import de.unistuttgart.ims.drama.api.FigureMention;
import de.unistuttgart.ims.drama.api.Scene;
import de.unistuttgart.ims.drama.api.Speaker;
import de.unistuttgart.ims.drama.api.Translator;
import de.unistuttgart.ims.drama.api.Utterance;
import de.unistuttgart.ims.drama.util.DramaUtil;

public enum CSVVariant {
	/**
	 * The default format. Table contains utterances (with begin/end) and their
	 * tokens.
	 */
	UtterancesWithTokens,
	/**
	 * Table with act and scene boundaries (character positions)
	 */
	Segments,
	/**
	 * Drama meta data, as it is defined in the play
	 */
	Metadata,
	/**
	 * The list of characters defined in the play
	 */
	Characters;

	/**
	 * Prints a record representing the header onto p
	 * 
	 * @param p
	 *            The target
	 * @throws IOException
	 *             If an I/O error occurs
	 */
	public void header(CSVPrinter p) throws IOException {
		switch (this) {
		case Segments:
			p.printRecord("corpus", "drama", "begin.Act", "end.Act", "Number.Act", "begin.Scene", "end.Scene",
					"Number.Scene");
			break;
		case Metadata:
			p.printRecord("corpus", "drama", "documentTitle", "language", "Name", "Pnd", "Translator.Name",
					"Translator.Pnd", "Date.Written", "Date.Printed", "Date.Premiere", "Date.Translation");
			break;
		case Characters:
			p.printRecord("corpus", "drama", "figure_surface", "figure_id", "Gender", "Age");
			break;
		default:
			p.printRecord("corpus", "drama", "begin", "end", "Speaker.figure_surface", "Speaker.figure_id",
					"Token.surface", "Token.pos", "Token.lemma", "length", "Mentioned.figure_surface",
					"Mentioned.figure_id");
		}
	}

	public void convert(JCas jcas, CSVPrinter p) throws IOException {
		switch (this) {
		case Characters:
			this.convertCharacters(jcas, p);
			break;
		case Metadata:
			this.convertMeta(jcas, p);
			break;
		case Segments:
			this.convertSegments(jcas, p);
			break;
		default:
			this.convertUtterancesWithTokens(jcas, p);
		}

	}

	private void convertCharacters(JCas jcas, CSVPrinter p) throws IOException {
		Drama drama = JCasUtil.selectSingle(jcas, Drama.class);
		for (CastFigure cf : JCasUtil.select(jcas, CastFigure.class)) {
			p.printRecord(drama.getCollectionId(), drama.getDocumentId(), cf.getNames(0), cf.getXmlId(0),
					cf.getGender(), cf.getAge());
		}
	}

	private void convertMeta(JCas jcas, CSVPrinter p) throws IOException {
		Drama drama = JCasUtil.selectSingle(jcas, Drama.class);
		for (Author author : JCasUtil.select(jcas, Author.class)) {
			if (JCasUtil.exists(jcas, Translator.class))
				for (Translator transl : JCasUtil.select(jcas, Translator.class)) {
					p.printRecord(drama.getCollectionId(), drama.getDocumentId(), drama.getDocumentTitle(),
							drama.getLanguage(), author.getName(), author.getPnd(), transl.getName(), transl.getPnd(),
							drama.getDateWritten(), drama.getDatePrinted(), drama.getDatePremiere(),
							drama.getDateTranslation());
				}
			else {
				p.printRecord(drama.getCollectionId(), drama.getDocumentId(), drama.getDocumentTitle(),
						drama.getLanguage(), author.getName(), author.getPnd(), null, null, drama.getDateWritten(),
						drama.getDatePrinted(), drama.getDatePremiere(), drama.getDateTranslation());
			}
		}
	}

	private void convertSegments(JCas jcas, CSVPrinter p) throws IOException {
		Drama drama = JCasUtil.selectSingle(jcas, Drama.class);
		if (JCasUtil.exists(jcas, Act.class)) {
			for (Act act : JCasUtil.select(jcas, Act.class)) {
				Collection<Scene> scenes = JCasUtil.selectCovered(Scene.class, act);
				if (scenes.isEmpty()) {
					p.printRecord(drama.getCollectionId(), drama.getDocumentId(), act.getBegin(), act.getEnd(),
							act.getNumber(), null, null, null);

				} else
					for (Scene scene : scenes) {
						p.printRecord(drama.getCollectionId(), drama.getDocumentId(), act.getBegin(), act.getEnd(),
								act.getNumber(), scene.getBegin(), scene.getEnd(), scene.getNumber());
					}
			}
		} else {
			Collection<Scene> scenes = JCasUtil.select(jcas, Scene.class);
			if (scenes.isEmpty()) {
				p.printRecord(drama.getCollectionId(), drama.getDocumentId(), null, null, null, null, null, null);

			} else
				for (Scene scene : scenes) {
					p.printRecord(drama.getCollectionId(), drama.getDocumentId(), null, null, null, scene.getBegin(),
							scene.getEnd(), scene.getNumber());
				}
		}
	}

	private void convertUtterancesWithTokens(JCas jcas, CSVPrinter p) throws IOException {
		Map<Token, Collection<FigureMention>> mentionMap = JCasUtil.indexCovering(jcas, Token.class,
				FigureMention.class);
		Drama drama = JCasUtil.selectSingle(jcas, Drama.class);
		int length = JCasUtil.select(jcas, Token.class).size();
		Set<FigureMention> used = new HashSet<FigureMention>();
		for (Utterance utterance : JCasUtil.select(jcas, Utterance.class)) {
			for (Speaker speaker : DramaUtil.getSpeakers(utterance)) {
				for (int i = 0; i < speaker.getCastFigure().size(); i++) {
					for (Token token : JCasUtil.selectCovered(Token.class, utterance)) {
						p.print(drama.getCollectionId());
						p.print(drama.getDocumentId());
						p.print(utterance.getBegin());
						p.print(utterance.getEnd());
						try {
							p.print(speaker.getCastFigure(i).getNames(0));
						} catch (Exception e) {
							p.print(null);
						}
						try {
							p.print(speaker.getCastFigure(i).getXmlId(0));
						} catch (Exception e) {
							p.print(null);
						}
						p.print(token.getCoveredText());
						p.print(token.getPos().getPosValue());
						p.print(token.getLemma().getValue());
						p.print(length);
						if (mentionMap.containsKey(token)) {
							FigureMention fm = selectLongest(mentionMap.get(token));
							if (used.contains(fm) || fm.getEntity() == null) {
								p.print(null);
								p.print(null);
							} else {
								DiscourseEntity de = fm.getEntity(0);
								CastFigure cf = null;
								if (de instanceof CastFigure) {
									cf = (CastFigure) de;
								}
								try {
									p.print(cf == null ? null : cf.getNames(0));
								} catch (Exception e) {
									p.print(null);
								}
								try {
									p.print(cf == null ? null : cf.getXmlId(0));
								} catch (Exception e) {
									p.print(null);
								}
								used.add(fm);
							}
						} else {
							p.print(null);
							p.print(null);
						}
						p.println();
					}
				}
			}
		}
	}

	/**
	 * This function returns the longest from a given collection of annotations.
	 * Length measured as <code>end - begin</code>.
	 * 
	 * @param coll
	 *            The annotation collection
	 * @return The longest of the annotation.
	 */
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
}
