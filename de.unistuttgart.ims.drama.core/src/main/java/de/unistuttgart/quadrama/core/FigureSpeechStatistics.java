package de.unistuttgart.quadrama.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.JCasBuilder;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.ims.drama.api.Utterance;
import de.unistuttgart.ims.drama.util.DramaUtil;

public class FigureSpeechStatistics extends JCasAnnotator_ImplBase {

	public static final String PARAM_WRITE_CSV = "Write CSV";

	public static final String VIEW_NAME = "Statistics";

	@ConfigurationParameter(name = PARAM_WRITE_CSV, mandatory = false)
	boolean writeCSV = false;

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		Map<Figure, SummaryStatistics> spokenWords = new HashMap<Figure, SummaryStatistics>();

		for (Figure figure : JCasUtil.select(jcas, Figure.class)) {
			spokenWords.put(figure, new SummaryStatistics());
		}

		for (Utterance utterance : JCasUtil.select(jcas, Utterance.class)) {
			try {
				Figure figure = DramaUtil.getSpeaker(utterance).getFigure();
				spokenWords.get(figure).addValue(JCasUtil.selectCovered(Token.class, utterance).size());
			} catch (NullPointerException e) {
			}
		}

		try {
			JCas statView = jcas.createView(VIEW_NAME);
			JCasBuilder b = new JCasBuilder(statView);
			for (Figure figure : JCasUtil.select(jcas, Figure.class)) {
				SummaryStatistics ss = spokenWords.get(figure);
				b.add(figure.getCoveredText());
				b.add("\n");
				b.add(ss.toString());
				b.add("====\n");

				figure.setNumberOfUtterances(ss.getN());
				figure.setUtteranceLengthArithmeticMean(ss.getMean());
				figure.setUtteranceLengthMin((int) ss.getMin());
				figure.setUtteranceLengthMax((int) ss.getMax());
				figure.setUtteranceLengthStandardDeviation(ss.getStandardDeviation());
				figure.setNumberOfWords((int) (ss.getSum()));

			}
			b.close();

		} catch (CASException e) {
			throw new AnalysisEngineProcessException(e);
		}

	}
}
