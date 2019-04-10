package de.unistuttgart.quadrama.core;

import org.apache.uima.analysis_component.AnalysisComponent;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.CAS;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.resource.ResourceInitializationException;

/**
 * This class provides a convenient way to get an aggregate analysis engine that
 * re-maps the views so that tokenization can be done on the stage directions view
 * {@link SD#SOFA_STAGEDIRECTIONS}.
 *
 */
public class SD {
	public static final String SOFA_STAGEDIRECTIONS = "tmp:StageDirections";

	public static AnalysisEngineDescription getWrappedSegmenterDescription(Class<? extends AnalysisComponent> compClass)
			throws ResourceInitializationException {
		AggregateBuilder builder = new AggregateBuilder();
		AnalysisEngineDescription aed = AnalysisEngineFactory.createEngineDescription(DramaStagePreparation.class);
		builder.add(aed);
		builder.add(AnalysisEngineFactory.createEngineDescription(compClass), CAS.NAME_DEFAULT_SOFA, SOFA_STAGEDIRECTIONS);
		builder.add(AnalysisEngineFactory.createEngineDescription(DramaStagePostProcessing.class));

		return builder.createAggregateDescription();
	}

	@Deprecated
	public static AnalysisEngineDescription getDramatisPersonaeProcessing() throws ResourceInitializationException {
		AggregateBuilder builder = new AggregateBuilder();
		builder.add(AnalysisEngineFactory.createEngineDescription(FigureReferenceAnnotator.class));
		builder.add(AnalysisEngineFactory.createEngineDescription(FigureDetailsAnnotator.class));
		return builder.createAggregateDescription();
	}
}
