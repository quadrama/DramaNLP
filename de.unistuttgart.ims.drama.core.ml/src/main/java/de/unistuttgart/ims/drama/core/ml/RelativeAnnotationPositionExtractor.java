package de.unistuttgart.ims.drama.core.ml;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.cleartk.ml.Feature;
import org.cleartk.ml.feature.extractor.CleartkExtractorException;
import org.cleartk.ml.feature.extractor.NamedFeatureExtractor1;

public class RelativeAnnotationPositionExtractor<T extends Annotation> implements NamedFeatureExtractor1<T> {
	private static final String FEATURE_NAME = "relative-position";

	@Override
	public List<Feature> extract(JCas view, T focusAnnotation) throws CleartkExtractorException {
		List<Feature> features = new ArrayList<Feature>();

		double rpos = focusAnnotation.getBegin() / (double) view.getDocumentText().length();
		Feature f = new Feature(getFeatureName(), rpos);
		features.add(f);
		return features;
	}

	@Override
	public String getFeatureName() {
		return FEATURE_NAME;
	}
}
