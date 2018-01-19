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
	List<XmlElementMapping> elementMapping = new LinkedList<XmlElementMapping>();

	@SuppressWarnings("rawtypes")
	List<XmlElementAction> elementActions = new LinkedList<XmlElementAction>();

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

		// process actions
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
		// process mappings
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

	/**
	 * Creates a mapping from XML elements to UIMA annotations.
	 * 
	 * @param selector
	 *            The CSS selector specifying the XML elements
	 * @param target
	 *            The type to be created. If it is a sup type of
	 *            {@link Annotation}, begin and end are set.
	 */
	public <T extends FeatureStructure> void addMapping(String selector, Class<T> target) {
		elementMapping.add(new XmlElementMapping<T>(selector, target));
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

	public boolean exists(String id) {
		return idRegistry.containsKey(id);
	}

	public <T extends FeatureStructure> Collection<T> select2Annotation(JCas jcas, Element rootElement,
			Map<String, XMLElement> annoMap, XmlElementMapping<T> mapping) {
		HashSet<T> set = new HashSet<T>();
		Elements elms = rootElement.select(mapping.getSelector());
		for (Element elm : elms) {
			XMLElement hAnno = annoMap.get(elm.cssSelector());
			if (elm.hasText() || elm.childNodeSize() > 0) {
				T annotation = jcas.getCas().createFS(JCasUtil.getType(jcas, mapping.getTargetClass()));
				jcas.getCas().addFsToIndexes(annotation);
				if (Annotation.class.isAssignableFrom(mapping.getTargetClass())) {
					((Annotation) annotation).setBegin(hAnno.getBegin());
					((Annotation) annotation).setEnd(hAnno.getEnd());
				}

				set.add(annotation);

				if (elm.hasAttr("xml:id") && !exists(elm.attr("xml:id"))) {
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
