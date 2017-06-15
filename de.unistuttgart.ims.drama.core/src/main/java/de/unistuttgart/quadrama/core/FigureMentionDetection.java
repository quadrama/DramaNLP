package de.unistuttgart.quadrama.core;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.PR;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.ims.drama.api.FigureMention;
import de.unistuttgart.ims.drama.api.Speech;
import de.unistuttgart.ims.drama.api.Utterance;
import de.unistuttgart.ims.drama.util.DramaUtil;

@TypeCapability(inputs = { "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token",
		"de.unistuttgart.ims.drama.api.Figure", "de.unistuttgart.ims.drama.api.Speech",
		"de.unistuttgart.ims.drama.api.Speaker", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.PR",
		"de.unistuttgart.ims.drama.api.Utterance" }, outputs = { "de.unistuttgart.ims.drama.api.FigureMention" })
public class FigureMentionDetection extends JCasAnnotator_ImplBase {

	Map<String, List<String>> firstPersonPronouns = new HashMap<String, List<String>>();

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

		// we create indices with figure names, figure reference strings and
		// pronouns
		Map<String, Figure> figureMap = new HashMap<String, Figure>();
		for (Figure figure : JCasUtil.select(jcas, Figure.class)) {
			figureMap.put(figure.getCoveredText(), figure);
			figureMap.put(figure.getReference(), figure);
		}
		List<String> pronouns = firstPersonPronouns.get(jcas.getDocumentLanguage());
		if (pronouns == null) {
			pronouns = new LinkedList<String>();
		}

		for (Utterance utterance : JCasUtil.select(jcas, Utterance.class)) {
			Collection<Figure> figures = DramaUtil.getFigures(utterance);
			for (Figure currentFigure : figures) {
				for (Speech speech : JCasUtil.selectCovered(jcas, Speech.class, utterance)) {
					for (Token token : JCasUtil.selectCovered(Token.class, speech)) {
						String surface = token.getCoveredText();
						if (figureMap.containsKey(surface)) {
							FigureMention fm = AnnotationFactory.createAnnotation(jcas, token.getBegin(),
									token.getEnd(), FigureMention.class);
							fm.setFigure(figureMap.get(surface));
						}
					}
					if (figures.size() <= 1)
						for (PR pronoun : JCasUtil.selectCovered(jcas, PR.class, speech)) {
							if (pronouns.contains(pronoun.getCoveredText())) {
								AnnotationFactory.createAnnotation(jcas, pronoun.getBegin(), pronoun.getEnd(),
										FigureMention.class).setFigure(currentFigure);
							}
						}
				}
			}
		}

	}

}
