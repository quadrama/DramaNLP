package de.unistuttgart.quadrama.io.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import de.unistuttgart.ims.drama.util.DramaUtil;
import de.unistuttgart.quadrama.io.core.type.XMLElement;

/**
 * This class is used to generate a UIMA document from arbitrary XML. The core
 * idea is to put all text content of the XML document in the document text of
 * the JCas, and to create annotations for each XML element, covering the exact
 * string the element contains. Consider, as an example, the XML fragment
 * <code>&lt;s&gt;&lt;det&gt;the&lt;/det&gt; &lt;n&gt;dog&lt;/n&gt;&lt;/s&gt;</code>.
 * In the JCas, this will be represented as the document text "the dog", with
 * three annotations of the type {@link XMLElement}: One annotation covers the
 * entire string (and has the tag name <code>s</code> as a feature), one
 * annotation covers "the" (tag name: <code>det</code>), and one annotation
 * covers "dog" (tag name: <code>n</code>). In addition, we store a CSS selector
 * for each annotation, which allows finding the element in the DOM tree. After
 * the initial conversion, rules are applied to convert some XML elements to
 * other UIMA annotations. Rules are expressed in CSS-like syntax.
 * 
 * <h2>Rule syntax</h2> The CSS selectors are interpreted by the JSoup library.
 * See {@link org.jsoup.select.Selector} for a detailed description. Classes
 * implementing {@link de.unistuttgart.quadrama.io.core.AbstractDramaUrlReader}
 * contain usage examples.
 * 
 * 
 * 
 * <h2>CSS vs. XPath</h2> TODO: Why CSS and not XPath?
 * 
 * @since 1.0.0
 */
public class GenericXmlReader<D extends TOP> {

	/**
	 * The DOM
	 */
	Document doc;

	/**
	 * An XPath expression to specify the root for the documentText
	 */
	String textRootSelector = null;

	boolean preserveWhitespace = false;

	@SuppressWarnings("rawtypes")
	List<Rule> elementMapping = new LinkedList<Rule>();

	Map<String, Map.Entry<Element, FeatureStructure>> idRegistry = new HashMap<String, Map.Entry<Element, FeatureStructure>>();

	Class<D> documentClass;

	public GenericXmlReader(Class<D> documentClass) {
		this.documentClass = documentClass;
	}

	public JCas read(JCas jcas, InputStream xmlStream) throws IOException {

		// parse the input
		doc = Jsoup.parse(xmlStream, "UTF-8", "", Parser.xmlParser());

		// prepare traversing the DOM
		Visitor vis = new Visitor(jcas, isPreserveWhitespace());

		// select the root element
		Element root;
		if (textRootSelector == null)
			root = doc;
		else
			root = doc.select(textRootSelector).first();

		// this populates the JCas, and creates XML annotations
		root.traverse(vis);

		// closes the CAS
		vis.getJCas();

		// process rules
		for (Rule<?> mapping : elementMapping) {
			applyRule(jcas, (mapping.isGlobal() ? doc : root), vis.getAnnotationMap(), mapping);
		}

		return jcas;
	}

	public void addRule(Rule<?> rule) {
		elementMapping.add(rule);
	}

	public <T extends TOP> void addRule(String selector, Class<T> targetClass) {
		elementMapping.add(new Rule<T>(selector, targetClass, null));
	}

	public <T extends TOP> void addRule(String selector, Class<T> targetClass, BiConsumer<T, Element> callback) {
		elementMapping.add(new Rule<T>(selector, targetClass, callback));
	}

	public void addGlobalRule(String selector, BiConsumer<D, Element> callback) {
		Rule<D> r = new Rule<D>(selector, documentClass, callback, true);
		r.setUnique(true);
		elementMapping.add(r);
	}

	public <T extends TOP> void addGlobalRule(String selector, Class<T> targetClass, BiConsumer<T, Element> callback) {
		elementMapping.add(new Rule<T>(selector, targetClass, callback, true));
	}

	public Map.Entry<Element, FeatureStructure> getAnnotation(String id) {
		return idRegistry.get(id);
	}

	public boolean exists(String id) {
		return idRegistry.containsKey(id);
	}

	protected <T extends TOP> T getFeatureStructure(JCas jcas, XMLElement hAnno, Element elm, Rule<T> mapping) {
		T annotation = null;
		if (mapping.isUnique()) {
			annotation = DramaUtil.getOrCreate(jcas, mapping.getTargetClass());
		} else {
			annotation = jcas.getCas().createFS(JCasUtil.getType(jcas, mapping.getTargetClass()));
			jcas.getCas().addFsToIndexes(annotation);
			if (Annotation.class.isAssignableFrom(mapping.getTargetClass())) {
				((Annotation) annotation).setBegin(hAnno.getBegin());
				((Annotation) annotation).setEnd(hAnno.getEnd());
			}

			if (elm.hasAttr("xml:id") && !exists(elm.attr("xml:id"))) {
				String id = elm.attr("xml:id");
				idRegistry.put(id, new AbstractMap.SimpleEntry<Element, FeatureStructure>(elm, annotation));
			}

		}
		return annotation;
	}

	protected <T extends TOP> void applyRule(JCas jcas, Element rootElement, Map<String, XMLElement> annoMap,
			Rule<T> mapping) {
		Elements elms = rootElement.select(mapping.getSelector());
		for (Element elm : elms) {
			XMLElement hAnno = annoMap.get(elm.cssSelector());
			if (elm.hasText() || elm.childNodeSize() > 0) {
				T annotation = getFeatureStructure(jcas, hAnno, elm, mapping);
				if (mapping.getCallback() != null && annotation != null)
					mapping.getCallback().accept(annotation, elm);
			}
		}
	}

	/**
	 * This class represents the rules we apply
	 * 
	 *
	 * @param <T>
	 *            Rules are specific for a UIMA type
	 */
	public static class Rule<T extends TOP> {
		String selector;
		BiConsumer<T, Element> callback;
		Class<T> targetClass;
		boolean global;
		boolean unique = false;

		/**
		 * 
		 * @param selector
		 *            The CSS selector
		 * @param targetClass
		 *            The target class
		 * @param callback
		 *            A function to be called for every instance. Can be null.
		 * @param global
		 *            Whether to apply the rule globally or just for the text
		 *            part
		 * @param createFeatureStructures
		 *            Whether to create new feature structures
		 */
		public Rule(String selector, Class<T> targetClass, BiConsumer<T, Element> callback, boolean global) {
			this.selector = selector;
			this.callback = callback;
			this.targetClass = targetClass;
			this.global = global;
		}

		public Rule(String selector, Class<T> targetClass, BiConsumer<T, Element> callback) {
			this.selector = selector;
			this.callback = callback;
			this.targetClass = targetClass;
			this.global = false;
		}

		public Class<T> getTargetClass() {
			return this.targetClass;
		}

		public String getSelector() {
			return selector;
		}

		boolean isGlobal() {
			return this.global;
		};

		@Override
		public String toString() {
			return getSelector() + " -> " + getTargetClass().getName();
		}

		public BiConsumer<T, Element> getCallback() {
			return callback;
		}

		public boolean isUnique() {
			return unique;
		}

		public void setUnique(boolean singleton) {
			this.unique = singleton;
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
