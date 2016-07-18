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

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.unistuttgart.ims.entitydetection.api.TrainingArea;

public class TrainingAreaAnnotator extends JCasAnnotator_ImplBase {

	public static final String PARAM_INSTANCE_CLASS = "Instance class";

	@ConfigurationParameter(name = PARAM_INSTANCE_CLASS)
	String instanceClassName;

	Class<? extends Annotation> instanceClass;

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(final UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		try {
			instanceClass = (Class<? extends Annotation>) Class.forName(instanceClassName);
		} catch (ClassNotFoundException e) {
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		Map<Sentence, Boolean> map = new HashMap<Sentence, Boolean>();
		for (Sentence s : JCasUtil.select(jcas, Sentence.class)) {
			map.put(s, false);
		}
		for (Sentence s : JCasUtil.select(jcas, Sentence.class)) {
			if (JCasUtil.selectCovered(instanceClass, s).size() > 0) {
				map.put(s, true);
				for (Sentence s1 : JCasUtil.selectPreceding(Sentence.class, s, 3)) {
					map.put(s1, true);
				}
				for (Sentence s2 : JCasUtil.selectFollowing(Sentence.class, s, 3)) {
					map.put(s2, true);
				}
			}
		}

		int begin = -1;
		Sentence lastSentence = null;
		for (Sentence s : map.keySet()) {
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
