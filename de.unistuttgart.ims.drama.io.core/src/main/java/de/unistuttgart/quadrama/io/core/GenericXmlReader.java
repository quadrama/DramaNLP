package de.unistuttgart.quadrama.io.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import de.unistuttgart.ims.drama.api.Drama;
import de.unistuttgart.ims.drama.util.DramaUtil;
import de.unistuttgart.quadrama.io.core.type.HTMLAnnotation;

public class GenericXmlReader {

	Document doc;

	/**
	 * An XPath expression to specify the root for the documentText
	 */
	String textRootSelector = null;

	boolean preserveWhitespace = false;

	@SuppressWarnings("rawtypes")
	List<XmlElementMapping> elementMapping = new LinkedList<XmlElementMapping>();

	@SuppressWarnings("rawtypes")
	List<XmlElementAction> elementActions = new LinkedList<XmlElementAction>();

	Map<String, Map.Entry<Element, FeatureStructure>> idRegistry = new HashMap<String, Map.Entry<Element, FeatureStructure>>();

	public JCas read(JCas jcas, InputStream xmlStream) throws IOException {
		doc = Jsoup.parse(xmlStream, "UTF-8", "", Parser.xmlParser());

		Visitor vis = new Visitor(jcas, isPreserveWhitespace());

		Element root;
		if (textRootSelector == null)
			root = doc;
		else
			root = doc.select(textRootSelector).first();
		root.traverse(vis);
		// closes the CAS
		vis.getJCas();

		for (XmlElementAction<?> action : elementActions) {
			Elements elms = doc.select(action.getSelector());
			for (Element elm : elms) {
				if (action.getTarget() == Drama.class) {
					@SuppressWarnings("unchecked")
					XmlElementAction<Drama> rAction = (XmlElementAction<Drama>) action;
					rAction.getCallback().accept(DramaUtil.getDrama(jcas), elm);
				} else {
					@SuppressWarnings("unchecked")
					XmlElementAction<JCas> rAction = (XmlElementAction<JCas>) action;
					rAction.getCallback().accept(jcas, elm);
				}
			}
		}

		for (XmlElementMapping<?> mapping : elementMapping) {
			select2Annotation(jcas, (mapping.isDocumentRoot() ? doc : root), vis.getAnnotationMap(), mapping);
		}

		return jcas;
	}

	public <T> void addAction(String selector, Class<T> target, BiConsumer<T, Element> action) {
		elementActions.add(new XmlElementAction<T>(selector, target, action));
	}

	public void addAction(String selector, BiConsumer<JCas, Element> action) {
		elementActions.add(new XmlElementAction<JCas>(selector, JCas.class, action));
	}

	public <T extends FeatureStructure> void addMapping(String selector, Class<T> target) {
		elementMapping.add(new XmlElementMapping<T>(selector, target));
	}

	public <T extends FeatureStructure> void addMapping(String selector, Class<T> target,
			BiConsumer<T, Element> callback) {
		elementMapping.add(new XmlElementMapping<T>(selector, target, callback));
	}

	public <T extends FeatureStructure> void addDocumentMapping(String selector, Class<T> target,
			BiConsumer<T, Element> callback) {
		elementMapping.add(new XmlElementMapping<T>(selector, target, callback, true));
	}

	public Map.Entry<Element, FeatureStructure> getAnnotation(String id) {
		return idRegistry.get(id);
	}

	public <T extends FeatureStructure> Collection<T> select2Annotation(JCas jcas, Element rootElement,
			Map<String, HTMLAnnotation> annoMap, XmlElementMapping<T> mapping) {
		HashSet<T> set = new HashSet<T>();
		Elements elms = rootElement.select(mapping.getSelector());
		for (Element elm : elms) {
			HTMLAnnotation hAnno = annoMap.get(elm.cssSelector());
			if (elm.hasText() || elm.childNodeSize() > 0) {
				T annotation = jcas.getCas().createFS(JCasUtil.getType(jcas, mapping.getTargetClass()));
				jcas.getCas().addFsToIndexes(annotation);
				if (Annotation.class.isAssignableFrom(mapping.getTargetClass())) {
					((Annotation) annotation).setBegin(hAnno.getBegin());
					((Annotation) annotation).setEnd(hAnno.getEnd());
				}

				set.add(annotation);

				if (elm.hasAttr("xml:id")) {
					String id = elm.attr("xml:id");
					idRegistry.put(id, new AbstractMap.SimpleEntry<Element, FeatureStructure>(elm, annotation));
				}

				if (mapping.getCallback() != null)
					mapping.getCallback().accept(annotation, elm);

			}
		}
		return set;
	}

	public class XmlElementAction<T> {
		final String selector;
		final Class<T> target;
		final BiConsumer<T, Element> callback;

		public XmlElementAction(String selector, Class<T> target, BiConsumer<T, Element> callback) {
			this.selector = selector;
			this.callback = callback;
			this.target = target;
		}

		public String getSelector() {
			return selector;
		}

		public BiConsumer<T, Element> getCallback() {
			return callback;
		}

		public Class<T> getTarget() {
			return target;
		}

	}

	public class XmlElementMapping<T extends FeatureStructure> {

		final String selector;
		final Class<T> targetClass;
		final BiConsumer<T, Element> callback;
		final boolean documentRoot;

		public XmlElementMapping(String selector, Class<T> targetClass) {
			super();
			this.selector = selector;
			this.targetClass = targetClass;
			this.callback = null;
			this.documentRoot = false;
		}

		public XmlElementMapping(String selector, Class<T> targetClass, BiConsumer<T, Element> cb) {
			super();
			this.selector = selector;
			this.targetClass = targetClass;
			this.callback = cb;
			this.documentRoot = false;
		}

		public XmlElementMapping(String selector, Class<T> targetClass, BiConsumer<T, Element> cb,
				boolean documentRoot) {
			super();
			this.selector = selector;
			this.targetClass = targetClass;
			this.callback = cb;
			this.documentRoot = documentRoot;
		}

		public String getSelector() {
			return selector;
		}

		public Class<T> getTargetClass() {
			return targetClass;
		}

		public BiConsumer<T, Element> getCallback() {
			return callback;
		}

		public boolean isDocumentRoot() {
			return documentRoot;
		}
	}

	public String getTextRootSelector() {
		return textRootSelector;
	}

	public void setTextRootSelector(String textRootSelector) {
		this.textRootSelector = textRootSelector;
	}

	public Document getDocument() {
		return doc;
	}

	public boolean isPreserveWhitespace() {
		return preserveWhitespace;
	}

	public void setPreserveWhitespace(boolean preserveWhitespace) {
		this.preserveWhitespace = preserveWhitespace;
	}

}
