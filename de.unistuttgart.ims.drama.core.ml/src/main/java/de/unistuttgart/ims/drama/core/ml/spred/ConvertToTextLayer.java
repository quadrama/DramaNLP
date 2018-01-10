package de.unistuttgart.ims.drama.core.ml.spred;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.resource.ResourceInitializationException;

import de.unistuttgart.ims.drama.api.ActHeading;
import de.unistuttgart.ims.drama.api.SceneHeading;
import de.unistuttgart.ims.drama.api.Speaker;
import de.unistuttgart.ims.drama.api.Speech;
import de.unistuttgart.ims.drama.api.StageDirection;
import de.unistuttgart.ims.drama.core.ml.api.TextLayer;
import de.unistuttgart.ims.uimautil.MapAnnotations;

public class ConvertToTextLayer {

	public static AnalysisEngineDescription getDescription() throws ResourceInitializationException {
		AggregateBuilder b = new AggregateBuilder();
		Class<?> clazz = Speech.class;
		b.add(AnalysisEngineFactory.createEngineDescription(MapAnnotations.class, MapAnnotations.PARAM_SOURCE_CLASS,
				clazz, MapAnnotations.PARAM_TARGET_CLASS, TextLayer.class, MapAnnotations.PARAM_FEATURE_NAME, "Name",
				MapAnnotations.PARAM_FEATURE_VALUE, clazz.getSimpleName()));
		clazz = Speaker.class;
		b.add(AnalysisEngineFactory.createEngineDescription(MapAnnotations.class, MapAnnotations.PARAM_SOURCE_CLASS,
				clazz, MapAnnotations.PARAM_TARGET_CLASS, TextLayer.class, MapAnnotations.PARAM_FEATURE_NAME, "Name",
				MapAnnotations.PARAM_FEATURE_VALUE, clazz.getSimpleName()));
		clazz = StageDirection.class;
		b.add(AnalysisEngineFactory.createEngineDescription(MapAnnotations.class, MapAnnotations.PARAM_SOURCE_CLASS,
				clazz, MapAnnotations.PARAM_TARGET_CLASS, TextLayer.class, MapAnnotations.PARAM_FEATURE_NAME, "Name",
				MapAnnotations.PARAM_FEATURE_VALUE, clazz.getSimpleName()));
		clazz = SceneHeading.class;
		b.add(AnalysisEngineFactory.createEngineDescription(MapAnnotations.class, MapAnnotations.PARAM_SOURCE_CLASS,
				clazz, MapAnnotations.PARAM_TARGET_CLASS, TextLayer.class, MapAnnotations.PARAM_FEATURE_NAME, "Name",
				MapAnnotations.PARAM_FEATURE_VALUE, clazz.getSimpleName()));
		clazz = ActHeading.class;
		b.add(AnalysisEngineFactory.createEngineDescription(MapAnnotations.class, MapAnnotations.PARAM_SOURCE_CLASS,
				clazz, MapAnnotations.PARAM_TARGET_CLASS, TextLayer.class, MapAnnotations.PARAM_FEATURE_NAME, "Name",
				MapAnnotations.PARAM_FEATURE_VALUE, clazz.getSimpleName()));
		return b.createAggregateDescription();

	}

}
