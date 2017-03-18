package de.unistuttgart.quadrama.core;

import org.apache.uima.analysis_component.AnalysisComponent;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.CAS;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.resource.ResourceInitializationException;

/**
 * This class provides a convenient way to get an aggregate analysis engine that
 * re-maps the views so that tokenization can be done on the utterances view
 * {@link D#SOFA_UTTERANCES}.
 * 
 * @author Nils Reiter
 *
 */
public class D {
	public static final String SOFA_UTTERANCES = "tmp:Utterances";

	public static AnalysisEngineDescription getWrappedSegmenterDescription(Class<? extends AnalysisComponent> compClass)
			throws ResourceInitializationException {
		AggregateBuilder builder = new AggregateBuilder();
		AnalysisEngineDescription aed = AnalysisEngineFactory.createEngineDescription(DramaSpeechPreparation.class);
		builder.add(aed);
		builder.add(AnalysisEngineFactory.createEngineDescription(compClass), CAS.NAME_DEFAULT_SOFA, SOFA_UTTERANCES);
		builder.add(AnalysisEngineFactory.createEngineDescription(DramaSpeechPostProcessing.class));

		return builder.createAggregateDescription();
	}

	public static AnalysisEngineDescription getDramatisPersonaeProcessing() throws ResourceInitializationException {
		AggregateBuilder builder = new AggregateBuilder();
		builder.add(AnalysisEngineFactory.createEngineDescription(FigureReferenceAnnotator.class));
		builder.add(AnalysisEngineFactory.createEngineDescription(FigureDetailsAnnotator.class));
		return builder.createAggregateDescription();
	}
}
