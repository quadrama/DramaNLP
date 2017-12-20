package de.unistuttgart.quadrama.io.core;

import org.apache.uima.jcas.tcas.Annotation;
import org.jsoup.nodes.Element;

@Deprecated
public interface Select2AnnotationCallback<T extends Annotation> {
	public void call(T annotation, Element xmlElement);
}
