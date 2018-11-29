package de.unistuttgart.quadrama.core;

import org.apache.uima.analysis_component.AnalysisComponent;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.CAS;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.resource.ResourceInitializationException;

/**
 * This class provides a convenient way to get an aggregate analysis engine that
 * re-maps the views so that tokenization can be done on the speaker view
 * {@link SP#SOFA_SPEAKERS}.
 * 
 * @author Nils Reiter
 *
 */
public class SP {
	public static final String SOFA_SPEAKERS = "tmp:Speakers";
	

	public static AnalysisEngineDescription getWrappedSegmenterDescription(Class<? extends AnalysisComponent> compClass)
			throws ResourceInitializationException {
		AggregateBuilder builder = new AggregateBuilder();
		AnalysisEngineDescription aed = AnalysisEngineFactory.createEngineDescription(DramaSpeakerPreparation.class);
		builder.add(aed);
		builder.add(AnalysisEngineFactory.createEngineDescription(compClass), CAS.NAME_DEFAULT_SOFA, SOFA_SPEAKERS);
		builder.add(AnalysisEngineFactory.createEngineDescription(DramaSpeakerPostProcessing.class));

		return builder.createAggregateDescription();
	}

	public static AnalysisEngineDescription getDramatisPersonaeProcessing() throws ResourceInitializationException {
		AggregateBuilder builder = new AggregateBuilder();
		builder.add(AnalysisEngineFactory.createEngineDescription(FigureReferenceAnnotator.class));
		builder.add(AnalysisEngineFactory.createEngineDescription(FigureDetailsAnnotator.class));
		return builder.createAggregateDescription();
	}
}
