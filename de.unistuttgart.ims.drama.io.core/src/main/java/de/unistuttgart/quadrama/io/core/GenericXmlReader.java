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

import de.unistuttgart.ims.drama.api.Drama;
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
 * <h2>Mapping vs. Action</h2> Two different kinds of rules can be specified.
 * <b>Mappings</b> are direct "translations" of XML elements into UIMA
 * annotations (as in the example above). <b>Actions</b> can be used, when some
 * XML Elements should not be directly mapped to UIMA annotations, but to other
 * kinds of data structures -- e.g., meta data is likely not directly
 * represented as an annotation, but as other feature stuctures.
 * 
 * <h2>CSS vs. XPath</h2> TODO: Why CSS and not XPath?
 * 
 * @since 1.0.0
 */
public class GenericXmlReader {

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
			select2Annotation(jcas, (mapping.isGlobal() ? doc : root), vis.getAnnotationMap(), mapping);
		}

		return jcas;
	}

	/**
	 * Creates a mapping from XML elements to UIMA annotations.
	 * 
	 * @param selector
	 *            The CSS selector specifying the XML elements
	 * @param target
	 *            The type to be created. If it is a sup type of
	 *            {@link Annotation}, begin and end are set.
	 */
	@Deprecated
	public <T extends TOP> void addMapping(String selector, Class<T> target) {
		elementMapping.add(new Rule<T>(selector, target, null));
	}

	/**
	 * Creates a mapping from XML elements to UIMA annotations
	 * 
	 * @param selector
	 *            The CSS selector specifying the XML elements
	 * @param target
	 *            The type to be created. If it is a sup type of
	 *            {@link Annotation}, begin and end are set.
	 * @param callback
	 *            A function that is called for each created annotation.
	 */
	@Deprecated
	public <T extends TOP> void addMapping(String selector, Class<T> target, BiConsumer<T, Element> callback) {
		elementMapping.add(new Rule<T>(selector, target, callback));
	}

	@Deprecated
	public <T extends TOP> void addDocumentMapping(String selector, Class<T> target, BiConsumer<T, Element> callback) {
		elementMapping.add(new Rule<T>(selector, target, callback, true, true));
	}

	@Deprecated
	public <T extends TOP> void addMappingAction(String selector, Class<T> target, BiConsumer<T, Element> callback) {
		elementMapping.add(new Rule<T>(selector, target, callback, true, false));
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

	public void addGlobalRule(String selector, BiConsumer<Drama, Element> callback) {
		Rule<Drama> r = new Rule<Drama>(selector, Drama.class, callback, true, false);
		r.setSingleton(true);
		elementMapping.add(r);
	}

	public <T extends TOP> void addGlobalRule(String selector, Class<T> targetClass, BiConsumer<T, Element> callback) {
		elementMapping.add(new Rule<T>(selector, targetClass, callback, true, true));
	}

	public Map.Entry<Element, FeatureStructure> getAnnotation(String id) {
		return idRegistry.get(id);
	}

	public boolean exists(String id) {
		return idRegistry.containsKey(id);
	}

	private <T extends TOP> T getFeatureStructure(JCas jcas, XMLElement hAnno, Element elm, Rule<T> mapping) {
		T annotation = null;
		if (mapping.isCreateFeatureStructures()) {
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

		} else if (mapping.isSingleton()) {
			annotation = DramaUtil.getOrCreate(jcas, mapping.getTargetClass());
		}
		return annotation;
	}

	public <T extends TOP> void select2Annotation(JCas jcas, Element rootElement, Map<String, XMLElement> annoMap,
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

	public class Rule<T extends TOP> {
		String selector;
		BiConsumer<T, Element> callback;
		Class<T> targetClass;
		boolean global;
		boolean createFeatureStructures;
		boolean singleton = false;

		/**
		 * 
		 * @param selector
		 * @param targetClass
		 * @param callback
		 * @param global
		 * @param createFeatureStructures
		 */
		public Rule(String selector, Class<T> targetClass, BiConsumer<T, Element> callback, boolean global,
				boolean createFeatureStructures) {
			this.selector = selector;
			this.callback = callback;
			this.targetClass = targetClass;
			this.global = global;
			this.createFeatureStructures = createFeatureStructures;
		}

		public Class<T> getTargetClass() {
			return this.targetClass;
		}

		public Rule(String selector, Class<T> targetClass, BiConsumer<T, Element> callback) {
			this.selector = selector;
			this.callback = callback;
			this.targetClass = targetClass;
			this.global = false;
			this.createFeatureStructures = true;
		}

		public String getSelector() {
			return selector;
		}

		boolean isCreateFeatureStructures() {
			return this.createFeatureStructures;
		};

		boolean isGlobal() {
			return this.global;
		};

		@Override
		public String toString() {
			return getSelector();
		}

		public BiConsumer<T, Element> getCallback() {
			return callback;
		}

		public boolean isSingleton() {
			return singleton;
		}

		public void setSingleton(boolean singleton) {
			this.singleton = singleton;
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
