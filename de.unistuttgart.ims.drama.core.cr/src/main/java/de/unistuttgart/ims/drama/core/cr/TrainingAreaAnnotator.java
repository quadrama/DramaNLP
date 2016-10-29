package de.unistuttgart.ims.drama.core.cr;

import java.util.HashMap;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import de.unistuttgart.ims.entitydetection.api.TrainingArea;

@Deprecated
public class TrainingAreaAnnotator extends JCasAnnotator_ImplBase {

	public static final String PARAM_INSTANCE_CLASS = "Instance class";
	public static final String PARAM_CONTEXT_CLASS = "Context class";

	@ConfigurationParameter(name = PARAM_INSTANCE_CLASS)
	String instanceClassName;

	@ConfigurationParameter(name = PARAM_CONTEXT_CLASS)
	String contextClassName;

	Class<? extends Annotation> instanceClass;
	Class<? extends Annotation> contextClass;

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(final UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		try {
			instanceClass = (Class<? extends Annotation>) Class.forName(instanceClassName);
		} catch (ClassNotFoundException e) {
			throw new ResourceInitializationException(e);
		}
		try {
			contextClass = (Class<? extends Annotation>) Class.forName(contextClassName);
		} catch (ClassNotFoundException e) {
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		Map<Annotation, Boolean> map = new HashMap<Annotation, Boolean>();
		for (Annotation s : JCasUtil.select(jcas, Annotation.class)) {
			map.put(s, false);
		}
		for (Annotation s : JCasUtil.select(jcas, contextClass)) {
			if (JCasUtil.selectCovered(instanceClass, s).size() > 0) {
				map.put(s, true);
				for (Annotation s1 : JCasUtil.selectPreceding(Annotation.class, s, 3)) {
					map.put(s1, true);
				}
				for (Annotation s2 : JCasUtil.selectFollowing(Annotation.class, s, 3)) {
					map.put(s2, true);
				}
			}
		}

		int begin = -1;
		Annotation lastSentence = null;
		for (Annotation s : map.keySet()) {
			if (map.get(s) && begin < 0) {
				begin = s.getBegin();
			} else if (!map.get(s) && begin >= 0) {
				AnnotationFactory.createAnnotation(jcas, begin, lastSentence.getEnd(), TrainingArea.class);
				begin = -1;
			}
			lastSentence = s;
		}
	}

}
