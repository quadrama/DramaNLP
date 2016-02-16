package de.unistuttgart.quadrama.core;

import org.apache.uima.analysis_component.AnalysisComponent;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.CAS;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.resource.ResourceInitializationException;

public class DramaSpeechSegmenter {
	public static final String SOFA_UTTERANCES = "Utterances";

	public static AnalysisEngineDescription getWrappedSegmenter(
			Class<? extends AnalysisComponent> compClass)
					throws ResourceInitializationException {
		AggregateBuilder builder = new AggregateBuilder();
		AnalysisEngineDescription aed =
				AnalysisEngineFactory
				.createEngineDescription(DramaSpeechPreparation.class);
		builder.add(aed);
		builder.add(AnalysisEngineFactory.createEngineDescription(compClass),
				CAS.NAME_DEFAULT_SOFA, SOFA_UTTERANCES);
		builder.add(AnalysisEngineFactory
				.createEngineDescription(DramaSpeechPostProcessing.class));

		return builder.createAggregateDescription();
	}
}
