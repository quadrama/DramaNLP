package de.unistuttgart.quadrama.core;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS_PRON;
import de.unistuttgart.ims.drama.api.CastFigure;
import de.unistuttgart.ims.drama.api.Drama;
import de.unistuttgart.ims.drama.api.Mention;
import de.unistuttgart.ims.drama.api.Speech;
import de.unistuttgart.ims.drama.api.Utterance;
import de.unistuttgart.ims.drama.util.DramaUtil;
import de.unistuttgart.ims.uima.io.xml.ArrayUtil;

@TypeCapability(inputs = { "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token",
		"de.unistuttgart.ims.drama.api.Figure", "de.unistuttgart.ims.drama.api.Speech",
		"de.unistuttgart.ims.drama.api.Speaker", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.PR",
		"de.unistuttgart.ims.drama.api.Utterance" }, outputs = { "de.unistuttgart.ims.drama.api.FigureMention" })
public class FigureMentionDetection extends JCasAnnotator_ImplBase {

	public static final String PARAM_MANUAL_COREFERENCE = "Manual Coreference";

	@ConfigurationParameter(name = PARAM_MANUAL_COREFERENCE)
	boolean isManualCoreference;
	
	Map<String, List<String>> firstPersonPronouns = new HashMap<String, List<String>>();
	String[] posExceptions = new String[] { "ART" };

	@Override
	public void initialize(final UimaContext context) throws ResourceInitializationException {
		super.initialize(context);

		try {
			firstPersonPronouns.put("de", IOUtils.readLines(
					getClass().getResourceAsStream("/pronouns/de/person1-sg.csv"), Charset.forName("UTF-8")));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		
		// we create indices with all available figure names
		Map<String, CastFigure> figureMap = new HashMap<String, CastFigure>();
		for (CastFigure figure : JCasUtil.select(jcas, CastFigure.class)) {
			for (int i = 0; i < figure.getNames().size(); i++)
				figureMap.put(figure.getNames(i).toLowerCase(), figure);
		}
		// we create a list of first person pronouns
		List<String> pronouns = firstPersonPronouns.get(jcas.getDocumentLanguage());
		if (pronouns == null) {
			pronouns = new LinkedList<String>();
		}

		if (!isManualCoreference) {

			// Step 1: We search for each name in the full text (ignoring token
			// boundaries etc.)
			Pattern p;
			Matcher m;
			for (CastFigure cf : JCasUtil.select(jcas, CastFigure.class)) {
				for (int j = 0; j < cf.getNames().size(); j++) {
					String name = cf.getNames(j);

					p = Pattern.compile("\\b" + Pattern.quote(name) + "\\b", Pattern.CASE_INSENSITIVE);
					m = p.matcher(jcas.getDocumentText());
					while (m.find()) {
						// If the found token (or multi-token) looks ok given their
						// part of speech tags,
						// we consider it a mention
						if (!matches(jcas, m.start(), m.end())) {
							try {
								// If there is an existing mention annotation on this span, check if the entity
								// is the same as the cast figure
								Mention existingMention = JCasUtil.selectSingleAt(jcas, Mention.class, m.start(),
										m.end());
								if (!(existingMention.getEntity().getId() == cf.getId())) {
									// If it is not the same entity, create the mention
									Mention fm = AnnotationFactory.createAnnotation(jcas, m.start(), m.end(),
											Mention.class);
									fm.setSurfaceString(ArrayUtil.toStringArray(jcas, fm.getCoveredText().split(" ")));
									fm.setEntity(cf);
								}
							} catch (Exception e) {
								// If there is no existing mention annotation on the same span, simply create
								// the mention with the cast figure as entity
								Mention fm = AnnotationFactory.createAnnotation(jcas, m.start(), m.end(),
										Mention.class);
								fm.setSurfaceString(ArrayUtil.toStringArray(jcas, fm.getCoveredText().split(" ")));
								fm.setEntity(cf);
							}
						}
					}
				}
			}
		}

		// Step 2: We connect first person pronouns to their speakers
		for (Utterance utterance : JCasUtil.select(jcas, Utterance.class)) {
			Collection<CastFigure> figures = DramaUtil.getCastFigures(utterance);
			for (CastFigure currentFigure : figures) {
				if (currentFigure == null) {
					continue;
				}
				for (Speech speech : JCasUtil.selectCovered(jcas, Speech.class, utterance)) {
					if (figures.size() <= 1) {
						for (POS_PRON pronoun : JCasUtil.selectCovered(jcas, POS_PRON.class, speech)) {
							if (pronouns.contains(pronoun.getCoveredText().toLowerCase())) {
								try {
									// If there is an existing mention annotation on this span, check if the entity
									// is the same as the cast figure
									Mention existingMention = JCasUtil.selectSingleAt(jcas, Mention.class,
											pronoun.getBegin(), pronoun.getEnd());
									if (!(existingMention.getEntity().getId() == currentFigure.getId())) {
										// If it is not the same entity, create the mention
										Mention fm = AnnotationFactory.createAnnotation(jcas, pronoun.getBegin(),
												pronoun.getEnd(), Mention.class);
										fm.setSurfaceString(
												ArrayUtil.toStringArray(jcas, pronoun.getCoveredText().split(" ")));
										fm.setEntity(currentFigure);
									}
								} catch (Exception e) {
									// If there is no existing mention annotation on the same span, simply create
									// the mention with the cast figure as entity
									Mention fm = AnnotationFactory.createAnnotation(jcas, pronoun.getBegin(),
											pronoun.getEnd(), Mention.class);
									fm.setSurfaceString(
											ArrayUtil.toStringArray(jcas, pronoun.getCoveredText().split(" ")));
									fm.setEntity(currentFigure);
								}
							}
						}
					}
				}
			}
		}

	}

	boolean matches(JCas jcas, int begin, int end) {
		Collection<POS> poss = JCasUtil.selectCovered(jcas, POS.class, begin, end);
		StringBuilder b = new StringBuilder();
		for (POS pos : poss) {
			b.append(pos.getPosValue()).append(' ');
		}
		String profile = b.toString().trim();
		return ArrayUtils.contains(posExceptions, profile);
	}

}
