package de.unistuttgart.quadrama.core;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import de.unistuttgart.ims.drama.api.Act;
import de.unistuttgart.ims.drama.api.Scene;
import de.unistuttgart.ims.drama.api.StageDirection;
import de.unistuttgart.ims.drama.api.Utterance;
import de.unistuttgart.ims.uimautil.AnnotationComparator;

public class SceneActAnnotator extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

		SortedSet<Annotation> anchors = new TreeSet<Annotation>(new AnnotationComparator());
		anchors.addAll(JCasUtil.select(jcas, Utterance.class));
		anchors.addAll(JCasUtil.select(jcas, Scene.class));
		anchors.addAll(JCasUtil.select(jcas, Act.class));
		anchors.addAll(JCasUtil.select(jcas, StageDirection.class));

		if (anchors.isEmpty())
			return;

		// Acts
		if (!JCasUtil.exists(jcas, Act.class)) {
			AnnotationFactory.createAnnotation(jcas, anchors.first().getBegin(), anchors.last().getEnd(), Act.class)
					.setRegular(false);
		}

		// Scenes
		for (Act act : JCasUtil.select(jcas, Act.class)) {
			Collection<Scene> scenes = JCasUtil.selectCovered(Scene.class, act);
			if (scenes.isEmpty())
				AnnotationFactory.createAnnotation(jcas, act.getBegin(), act.getEnd(), Scene.class).setRegular(false);
		}
	}

	public static AnalysisEngineDescription getDescription() throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(SceneActAnnotator.class);
	}

}
