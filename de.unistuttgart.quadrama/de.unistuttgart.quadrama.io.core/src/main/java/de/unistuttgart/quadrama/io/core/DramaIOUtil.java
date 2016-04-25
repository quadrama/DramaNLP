package de.unistuttgart.quadrama.io.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.unistuttgart.quadrama.io.core.type.HTMLAnnotation;

public class DramaIOUtil {

	public static void cleanUp(JCas jcas) {
		jcas.removeAllIncludingSubtypes(HTMLAnnotation.type);
	}

	public static <T extends Annotation> Collection<T> select2Annotation(
			JCas jcas, Element rootElement,
			Map<String, HTMLAnnotation> annoMap, String cssSelector,
			Class<T> annoClass, Annotation coveringAnnotation) {
		HashSet<T> set = new HashSet<T>();
		Elements elms = rootElement.select(cssSelector);
		for (Element elm : elms) {
			HTMLAnnotation hAnno = annoMap.get(elm.cssSelector());
			if (coveringAnnotation == null
					|| (coveringAnnotation.getBegin() <= hAnno.getBegin() && coveringAnnotation
					.getEnd() >= hAnno.getEnd()))
				set.add(AnnotationFactory.createAnnotation(jcas,
						hAnno.getBegin(), hAnno.getEnd(), annoClass));
		}
		return set;
	}

	public static <T extends Annotation> T selectRange2Annotation(JCas jcas,
			Element rootElement, Map<String, HTMLAnnotation> annoMap,
			String beginCssSelector, String endCssSelector, Class<T> annoClass) {
		Elements elms = rootElement.select(beginCssSelector);
		int begin = jcas.size();
		for (Element elm : elms) {
			HTMLAnnotation hAnno = annoMap.get(elm.cssSelector());
			if (hAnno.getBegin() < begin) begin = hAnno.getBegin();
		}

		elms = rootElement.select(endCssSelector);
		int end = 0;
		for (Element elm : elms) {
			HTMLAnnotation hAnno = annoMap.get(elm.cssSelector());
			if (hAnno.getEnd() > end) end = hAnno.getEnd();
		}

		return AnnotationFactory.createAnnotation(jcas, begin, end, annoClass);
	}

}
