package de.unistuttgart.ims.drama.core.ml;

import java.lang.reflect.InvocationTargetException;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.unistuttgart.ims.drama.api.FigureMention;

public class PrepareEvaluation extends JCasAnnotator_ImplBase {

	public static final String PARAM_GOLD_VIEW_NAME = "Gold View";

	@ConfigurationParameter(name = PARAM_GOLD_VIEW_NAME)
	String goldViewName;

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		JCas newView;
		try {
			try {
				newView = jcas.getView(goldViewName);
			} catch (CASRuntimeException e) {
				newView = jcas.createView(goldViewName);
			}
			newView.setDocumentText(jcas.getDocumentText());
			newView.setDocumentLanguage(jcas.getDocumentLanguage());
			for (FigureMention entity : JCasUtil.select(jcas, FigureMention.class)) {
				AnnotationFactory.createAnnotation(newView, entity.getBegin(), entity.getEnd(), FigureMention.class);

			}

			Annotation a = FigureMention.class.getConstructor(JCas.class).newInstance(jcas);
			jcas.removeAllIncludingSubtypes(a.getTypeIndexID());
			// newView.removeAllIncludingSubtypes(a.getTypeIndexID());
		} catch (CASException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
