package de.unistuttgart.quadrama.io.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
import de.unistuttgart.ims.drama.api.Mention;
import de.unistuttgart.ims.drama.api.Scene;
import de.unistuttgart.ims.drama.api.Speaker;
import de.unistuttgart.ims.drama.api.Speech;
import de.unistuttgart.ims.drama.api.StageDirection;
import de.unistuttgart.ims.drama.api.Translator;
import de.unistuttgart.ims.drama.api.Utterance;
import de.unistuttgart.ims.drama.util.DramaUtil;
import de.unistuttgart.ims.drama.util.CoreferenceUtil;

public enum CSVVariant {
	/**
	 * The default format. Table contains utterances (with begin/end) and their
	 * tokens.
	 */
	UtterancesWithTokens,
	/**
	 * Table with the stage directions.
	 */
	StageDirections,
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
	Characters,
	/**
	 * A list of mentions to characters
	 */
	Mentions,
	/**
	 * List of all entity IDs and mapping to their surface representation
	 */
	Entities,
	/**
	 * List of tokens with structure information
	 */
	Structure;

	/**
	 * Prints a record representing the header onto p
	 * 
	 * @param p The target
	 * @throws IOException If an I/O error occurs
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
		case StageDirections:
			p.printRecord("corpus", "drama", "begin", "end", "Speaker.figure_surface", "Speaker.figure_id",
					"Token.surface", "Token.pos", "Token.lemma", "length", "Mentioned.figure_surface",
					"Mentioned.figure_id");
			break;
		case Entities:
			p.printRecord("corpus", "drama", "Entity.surface", "Entity.id", "Entity.group_members");
			break;
		case Structure:
			break;
		case Mentions:
			p.printRecord("corpus", "drama", "utteranceBegin", "utteranceEnd", "utteranceSpeakerId", "mentionBegin",
					"mentionEnd", "mentionSurface", "entityId");
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
		case StageDirections:
			this.convertStageDirections(jcas, p);
			break;
		case Mentions:
			this.convertMentions(jcas, p);
			break;
		case Entities:
			this.convertEntities(jcas, p);
			break;
		case Structure:
			this.convertStructure(jcas, p);
			break;
		default:
			this.convertUtterancesWithTokens(jcas, p);
		}

	}

	private void convertMentions(JCas jcas, CSVPrinter p) throws IOException {
		Map<Mention, Collection<Utterance>> mention2utterances = JCasUtil.indexCovering(jcas, Mention.class,
				Utterance.class);
		Map<Mention, Collection<Speech>> mention2speech = JCasUtil.indexCovering(jcas, Mention.class, Speech.class);
		Map<Mention, Collection<StageDirection>> mention2direction = JCasUtil.indexCovering(jcas, Mention.class,
				StageDirection.class);

		Drama drama = JCasUtil.selectSingle(jcas, Drama.class);
		for (Mention mention : JCasUtil.select(jcas, Mention.class)) {
			if (mention2speech.get(mention).isEmpty()) {
				if (!mention2direction.get(mention).isEmpty()) {
					StageDirection sd = mention2direction.get(mention).iterator().next();
					p.print(drama.getCollectionId());
					p.print(drama.getDocumentId());
					p.print(sd.getBegin());
					p.print(sd.getEnd());
					p.print("_stage");
					p.print(mention.getBegin());
					p.print(mention.getEnd());
					p.print(mention.getCoveredText());
					p.print(mention.getEntity().getXmlId(0));
					p.println();
				}
			} else {
				if (!mention2utterances.get(mention).isEmpty()) {
					Utterance utterance = mention2utterances.get(mention).iterator().next();
					for (Speaker speaker : DramaUtil.getSpeakers(utterance)) {
						p.print(drama.getCollectionId());
						p.print(drama.getDocumentId());
						p.print(utterance.getBegin());
						p.print(utterance.getEnd());
						try {
							p.print(speaker.getCastFigure(0).getXmlId(0));
						} catch (NullPointerException e) {
							p.print(null);
						}
						p.print(mention.getBegin());
						p.print(mention.getEnd());
						p.print(mention.getCoveredText());
						if (mention.getEntity() == null)
							p.print(null);
						else
							p.print(mention.getEntity().getXmlId(0));
					}
					p.println();
				}
			}
		}
	}

	private void convertCharacters(JCas jcas, CSVPrinter p) throws IOException {
		Drama drama = JCasUtil.selectSingle(jcas, Drama.class);
		for (CastFigure cf : JCasUtil.select(jcas, CastFigure.class)) {
			p.printRecord(drama.getCollectionId(), drama.getDocumentId(),
					(cf.getNames().size() > 0 ? cf.getNames(0) : null), cf.getXmlId(0), cf.getGender(), cf.getAge());
		}
	}

	private void convertMeta(JCas jcas, CSVPrinter p) throws IOException {
		Drama drama = JCasUtil.selectSingle(jcas, Drama.class);
		String cleanedDocumentTitle = drama.getDocumentTitle().replace("\n", " ").replaceAll(" +", " ").trim();
		if (JCasUtil.exists(jcas, Author.class))
			for (Author author : JCasUtil.select(jcas, Author.class)) {
				if (JCasUtil.exists(jcas, Translator.class))
					for (Translator transl : JCasUtil.select(jcas, Translator.class)) {
						p.printRecord(drama.getCollectionId(), drama.getDocumentId(), cleanedDocumentTitle,
								drama.getLanguage(), author.getName(), author.getPnd(), transl.getName(),
								transl.getPnd(), drama.getDateWritten(), drama.getDatePrinted(),
								drama.getDatePremiere(), drama.getDateTranslation());
					}
				else {
					p.printRecord(drama.getCollectionId(), drama.getDocumentId(), cleanedDocumentTitle,
							drama.getLanguage(), author.getName(), author.getPnd(), null, null, drama.getDateWritten(),
							drama.getDatePrinted(), drama.getDatePremiere(), drama.getDateTranslation());
				}
			}
		else
			p.printRecord(drama.getCollectionId(), drama.getDocumentId(), cleanedDocumentTitle, drama.getLanguage(), "",
					"", null, null, drama.getDateWritten(), drama.getDatePrinted(), drama.getDatePremiere(),
					drama.getDateTranslation());
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
		Map<Token, Collection<Mention>> mentionMap = JCasUtil.indexCovering(jcas, Token.class, Mention.class);
		Map<Token, Collection<StageDirection>> stageMap = JCasUtil.indexCovering(jcas, Token.class,
				StageDirection.class);
		Drama drama = JCasUtil.selectSingle(jcas, Drama.class);
		int length = JCasUtil.select(jcas, Token.class).size();
		Set<Mention> used = new HashSet<Mention>();
		for (Utterance utterance : JCasUtil.select(jcas, Utterance.class)) {
			for (Speaker speaker : DramaUtil.getSpeakers(utterance)) {
				for (int i = 0; i < speaker.getCastFigure().size(); i++) {
					for (Speech speech : JCasUtil.selectCovered(Speech.class, utterance)) {
						for (Token token : JCasUtil.selectCovered(Token.class, speech)) {
							if (stageMap.containsKey(token)) {
								continue;
							}
							used.clear();
							p.print(drama.getCollectionId());
							p.print(drama.getDocumentId());
							p.print(utterance.getBegin());
							p.print(utterance.getEnd());
							try {
								p.print(speaker.getCastFigure(i).getNames(0));
							} catch (NullPointerException e) {
								p.print(null);
							}
							try {
								p.print(speaker.getCastFigure(i).getXmlId(0));
							} catch (NullPointerException e) {
								p.print(null);
							}
							p.print(token.getCoveredText());
							p.print(token.getPos().getPosValue());
							p.print(token.getLemma().getValue());
							p.print(length);
							String printSurface = null;
							String printId = null;
							if (mentionMap.containsKey(token)) {
								Collection<Mention> mList = mentionMap.get(token);
								for (Mention m : mList) {
									if (m.getEntity() == null) {
										printSurface = null;
										printId = null;
									} else {
										if (!used.contains(m)) {
											if (m.getEntity() instanceof CastFigure) {
												printSurface = m.getEntity().getDisplayName();
											}
											try {
												if (printId == null) {
													printId = CoreferenceUtil.createConllBrackets(printId, m, token);
												} else {
													printId = printId + "|"
															+ CoreferenceUtil.createConllBrackets(printId, m, token);
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
							p.print(printSurface);
							p.print(printId);
							p.println();
						}
					}
				}
			}
		}
	}

	private void convertStageDirections(JCas jcas, CSVPrinter p) throws IOException {
		Map<Token, Collection<Mention>> mentionMap = JCasUtil.indexCovering(jcas, Token.class, Mention.class);
		Map<StageDirection, Collection<Utterance>> utteranceStageMap = JCasUtil.indexCovering(jcas,
				StageDirection.class, Utterance.class);
		Drama drama = JCasUtil.selectSingle(jcas, Drama.class);
		int length = JCasUtil.select(jcas, Token.class).size();
		Set<Mention> used = new HashSet<Mention>();
		for (StageDirection sd : JCasUtil.select(jcas, StageDirection.class)) {
			Collection<Utterance> utterances = utteranceStageMap.get(sd);
			if (!utterances.isEmpty()) {
				for (Utterance utt : JCasUtil.selectCovering(Utterance.class, sd)) {
					for (Speaker speaker : DramaUtil.getSpeakers(utt)) {
						for (int i = 0; i < speaker.getCastFigure().size(); i++) {
							for (Token token : JCasUtil.selectCovered(Token.class, sd)) {
								used.clear();
								p.print(drama.getCollectionId());
								p.print(drama.getDocumentId());
								p.print(sd.getBegin());
								p.print(sd.getEnd());
								try {
									p.print(speaker.getCastFigure(i).getNames(0));
								} catch (NullPointerException e) {
									p.print(null);
								}
								try {
									p.print(speaker.getCastFigure(i).getXmlId(0));
								} catch (NullPointerException e) {
									p.print(null);
								}
								p.print(token.getCoveredText());
								p.print(token.getPos().getPosValue());
								p.print(token.getLemma().getValue());
								p.print(length);
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
														printId = CoreferenceUtil.createConllBrackets(printId, m,
																token);
													} else {
														printId = printId + "|" + CoreferenceUtil
																.createConllBrackets(printId, m, token);
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
								p.print(null);
								p.print(printId);
								p.println();
							}
						}
					}
				}
			} else {
				for (Token token : JCasUtil.selectCovered(Token.class, sd)) {
					used.clear();
					p.print(drama.getCollectionId());
					p.print(drama.getDocumentId());
					p.print(sd.getBegin());
					p.print(sd.getEnd());
					p.print("_Stage");
					p.print("_Stage");
					p.print(token.getCoveredText());
					p.print(token.getPos().getPosValue());
					p.print(token.getLemma().getValue());
					p.print(length);
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
											printId = CoreferenceUtil.createConllBrackets(printId, m, token);
										} else {
											printId = printId + "|"
													+ CoreferenceUtil.createConllBrackets(printId, m, token);
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
					p.print(null);
					p.print(printId);
					p.println();
				}
			}
		}
	}

	private void convertEntities(JCas jcas, CSVPrinter p) throws IOException {
		Drama drama = JCasUtil.selectSingle(jcas, Drama.class);
		for (DiscourseEntity de : JCasUtil.select(jcas, DiscourseEntity.class)) {
			p.print(drama.getCollectionId());
			p.print(drama.getDocumentId());
			p.print(de.getDisplayName());
			p.print(de.getId());
			if (de.getEntityGroup() != null) {
				ArrayList<String> memberIds = new ArrayList<String>();
				int id;
				for (DiscourseEntity member : JCasUtil.select(de.getEntityGroup(), DiscourseEntity.class)) {
					id = member.getId();
					memberIds.add(Integer.toString(id));
				}
				p.print(String.join(",", memberIds));
			} else {
				p.print(null);
			}
			p.println();
		}
	}

	private void convertStructure(JCas jcas, CSVPrinter p) throws IOException {
		Map<Token, Collection<Act>> tokactMap = JCasUtil.indexCovering(jcas, Token.class, Act.class);
		Map<Act, Collection<Token>> acttokMap = JCasUtil.indexCovered(jcas, Act.class, Token.class);
		Map<Token, Collection<Scene>> toksceMap = JCasUtil.indexCovering(jcas, Token.class, Scene.class);
		Map<Scene, Collection<Token>> scetokMap = JCasUtil.indexCovered(jcas, Scene.class, Token.class);
		Map<Token, Collection<StageDirection>> toksdMap = JCasUtil.indexCovering(jcas, Token.class,
				StageDirection.class);
		Map<StageDirection, Collection<Token>> sdtokMap = JCasUtil.indexCovered(jcas, StageDirection.class,
				Token.class);
		Map<Token, Collection<Utterance>> tokuttMap = JCasUtil.indexCovering(jcas, Token.class, Utterance.class);
		Map<Utterance, Collection<Token>> utttokMap = JCasUtil.indexCovered(jcas, Utterance.class, Token.class);
		Map<Token, Collection<Speech>> tokspeeMap = JCasUtil.indexCovering(jcas, Token.class, Speech.class);
		Map<Speech, Collection<Token>> speetokMap = JCasUtil.indexCovered(jcas, Speech.class, Token.class);
		String structCol;
		Token firstAct = null;
		Token lastAct = null;
		Token firstScene = null;
		Token lastScene = null;
		Token firstSd = null;
		Token lastSd = null;
		Token firstUtt = null;
		Token lastUtt = null;
		Token firstSpee = null;
		Token lastSpee = null;
		for (Token token : JCasUtil.select(jcas, Token.class)) {
			if (tokactMap.containsKey(token)) {
				Collection<Act> actColl = tokactMap.get(token);
				ArrayList<Act> actList = new ArrayList<Act>(actColl);
				Act act = actList.get(0);
				if (acttokMap.containsKey(act)) {
					Collection<Token> tokColl = acttokMap.get(act);
					ArrayList<Token> tokList = new ArrayList<Token>(tokColl);
					firstAct = tokList.get(0);
					lastAct = tokList.get(tokList.size() - 1);
				}
			}
			if (toksceMap.containsKey(token)) {
				Collection<Scene> sceColl = toksceMap.get(token);
				ArrayList<Scene> sceList = new ArrayList<Scene>(sceColl);
				Scene sce = sceList.get(0);
				if (scetokMap.containsKey(sce)) {
					Collection<Token> tokColl = scetokMap.get(sce);
					ArrayList<Token> tokList = new ArrayList<Token>(tokColl);
					firstScene = tokList.get(0);
					lastScene = tokList.get(tokList.size() - 1);
				}
			}
			if (toksdMap.containsKey(token)) {
				Collection<StageDirection> sdColl = toksdMap.get(token);
				ArrayList<StageDirection> sdList = new ArrayList<StageDirection>(sdColl);
				StageDirection sd = sdList.get(0);
				if (sdtokMap.containsKey(sd)) {
					Collection<Token> tokColl = sdtokMap.get(sd);
					ArrayList<Token> tokList = new ArrayList<Token>(tokColl);
					firstSd = tokList.get(0);
					lastSd = tokList.get(tokList.size() - 1);
				}
			}
			if (tokuttMap.containsKey(token)) {
				Collection<Utterance> uttColl = tokuttMap.get(token);
				ArrayList<Utterance> uttList = new ArrayList<Utterance>(uttColl);
				Utterance utt = uttList.get(0);
				if (utttokMap.containsKey(utt)) {
					Collection<Token> tokColl = utttokMap.get(utt);
					ArrayList<Token> tokList = new ArrayList<Token>(tokColl);
					firstUtt = tokList.get(0);
					lastUtt = tokList.get(tokList.size() - 1);
				}
			}
			if (tokspeeMap.containsKey(token)) {
				Collection<Speech> speeColl = tokspeeMap.get(token);
				ArrayList<Speech> speeList = new ArrayList<Speech>(speeColl);
				Speech spee = speeList.get(0);
				if (speetokMap.containsKey(spee)) {
					Collection<Token> tokColl = speetokMap.get(spee);
					ArrayList<Token> tokList = new ArrayList<Token>(tokColl);
					firstSpee = tokList.get(0);
					lastSpee = tokList.get(tokList.size() - 1);
				}
			}
			p.print(token.getCoveredText());
			structCol = "";
			if (token.equals(firstAct)) {
				structCol = structCol + " AB";
			}
			if (token.equals(lastAct)) {
				structCol = structCol + " AE";
			}
			if (token.equals(firstScene)) {
				structCol = structCol + " SB";
			}
			if (token.equals(lastScene)) {
				structCol = structCol + " SE";
			}
			if (token.equals(firstSd)) {
				structCol = structCol + " SDB";
			}
			if (token.equals(lastSd)) {
				structCol = structCol + " SDE";
			}
			if (token.equals(firstSpee)) {
				structCol = structCol + " SPB";
			}
			if (token.equals(lastSpee)) {
				structCol = structCol + " SPE";
			}
			if (token.equals(firstUtt)) {
				structCol = structCol + " UB";
			}
			if (token.equals(lastUtt)) {
				structCol = structCol + " UE";
			}
			if (structCol.equals("")) {
				structCol = "-";
			}
			p.print(structCol);
			p.println();
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
}
