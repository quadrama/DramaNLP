package de.unistuttgart.ims.drama.core.ml;

import java.util.LinkedList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.Feature;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.factory.JCasBuilder;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import de.unistuttgart.quadrama.core.api.Origin;

public class PrepareClearTk extends JCasAnnotator_ImplBase {

	public static final String PARAM_VIEW_NAME = "View name";
	public static final String PARAM_ANNOTATION_TYPE = "Annotation type";
	public static final String PARAM_SUBANNOTATIONS = "Sub annotations";

	@ConfigurationParameter(name = PARAM_VIEW_NAME)
	String viewName;

	@ConfigurationParameter(name = PARAM_ANNOTATION_TYPE)
	String annotationClassName;

	@ConfigurationParameter(name = PARAM_SUBANNOTATIONS)
	List<String> subAnnotationClassnames;

	Class<Annotation> annotationClass;
	List<Class<Annotation>> subAnnotations = new LinkedList<Class<Annotation>>();

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

		try {
			for (String className : subAnnotationClassnames) {
				Class<?> cl = Class.forName(className);
				subAnnotations.add((Class<Annotation>) cl);
			}
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}

	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		JCas newView;
		try {
			newView = jcas.createView(viewName);
		} catch (CASException e) {
			e.printStackTrace();
			throw new AnalysisEngineProcessException(e);
		}

		JCasBuilder builder = new JCasBuilder(newView);

		for (Annotation a : JCasUtil.select(jcas, annotationClass)) {
			int relativ = -a.getBegin() + builder.getPosition();
			Origin o = builder.add(a.getCoveredText(), Origin.class);
			o.setOffset(a.getBegin());

			for (Class<Annotation> subClass : subAnnotations) {
				for (Annotation sub : JCasUtil.selectCovered(subClass, a)) {
					int tgtBegin = sub.getBegin() + relativ, tgtEnd = sub.getEnd() + relativ;
					Annotation tgt = AnnotationFactory.createAnnotation(newView, sub.getBegin() + relativ,
							sub.getEnd() + relativ, subClass);

					for (Feature feature : sub.getType().getFeatures()) {
						if (feature.getRange().isPrimitive())
							tgt.setFeatureValueFromString(feature, sub.getFeatureValueAsString(feature));
					}
					tgt.setBegin(tgtBegin);
					tgt.setEnd(tgtEnd);
				}
			}
		}
		builder.close();
	}

}
