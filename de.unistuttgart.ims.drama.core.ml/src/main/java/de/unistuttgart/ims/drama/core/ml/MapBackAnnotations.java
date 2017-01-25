package de.unistuttgart.ims.drama.core.ml;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.Feature;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import de.unistuttgart.quadrama.core.api.Origin;

public class MapBackAnnotations extends JCasAnnotator_ImplBase {
	public static final String PARAM_ANNOTATION_TYPE = "Annotation type";
	public static final String PARAM_VIEW_NAME = "View name";

	@ConfigurationParameter(name = PARAM_VIEW_NAME)
	String viewName;

	@ConfigurationParameter(name = PARAM_ANNOTATION_TYPE)
	String annotationClassName;

	Class<Annotation> annotationClass;

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(final UimaContext context) throws ResourceInitializationException {
		super.initialize(context);

		try {
			Class<?> cl = Class.forName(annotationClassName);
			annotationClass = (Class<Annotation>) cl;
		} catch (ClassNotFoundException e) {
			throw new ResourceInitializationException(e);
		}

	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		JCas other;
		try {
			other = jcas.getView(viewName);
			for (Origin origin : JCasUtil.select(other, Origin.class)) {
				int relativ = -origin.getBegin() + origin.getOffset();
				for (Annotation src : JCasUtil.selectCovered(annotationClass, origin)) {
					Annotation tgt = AnnotationFactory.createAnnotation(jcas, src.getBegin() + relativ,
							src.getEnd() + relativ, annotationClass);
					for (Feature feature : src.getType().getFeatures()) {
						if (feature.getRange().isPrimitive()) {
							if (feature.getRange().getName().equalsIgnoreCase("uima.cas.String")) {
								tgt.setStringValue(feature, src.getStringValue(feature));
							}
						}
					}
				}
			}
		} catch (CASException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

}
