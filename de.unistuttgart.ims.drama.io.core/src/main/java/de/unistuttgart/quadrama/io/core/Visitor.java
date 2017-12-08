package de.unistuttgart.quadrama.io.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.uima.fit.factory.JCasBuilder;
import org.apache.uima.jcas.JCas;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeVisitor;

import de.unistuttgart.quadrama.io.core.type.HTMLAnnotation;

public class Visitor implements NodeVisitor {

	protected JCasBuilder builder;
	protected Map<Node, Integer> beginMap = new HashMap<Node, Integer>();

	protected Map<String, HTMLAnnotation> annotationMap =
			new HashMap<String, HTMLAnnotation>();

	protected String[] blockElements = new String[] { "l", "p", "sp" };

	protected boolean preserveWhitespace = false;

	public Visitor(JCas jcas) {
		builder = new JCasBuilder(jcas);
	}

	public Visitor(JCas jcas, boolean preserveWhitespace) {
		builder = new JCasBuilder(jcas);
		this.preserveWhitespace = preserveWhitespace;
	}

	public void head(Node node, int depth) {
		if (node.getClass().equals(TextNode.class)) {
			if (this.preserveWhitespace)
				builder.add(((TextNode) node).getWholeText());
			else
				builder.add(((TextNode) node).text());
		} else {
			beginMap.put(node, builder.getPosition());
		}
	}

	public void tail(Node node, int depth) {
		if (node.getClass().equals(Element.class)) {
			Element elm = (Element) node;
			HTMLAnnotation anno =
					builder.add(beginMap.get(node), HTMLAnnotation.class);
			anno.setTag(elm.tagName());
			anno.setId(elm.id());
			anno.setSelector(elm.cssSelector());
			anno.setAttributes(elm.attributes().html());
			if (elm.className().isEmpty())
				anno.setCls(elm.attr("type"));
			else
				anno.setCls(elm.className());
			annotationMap.put(elm.cssSelector(), anno);
			if (!this.preserveWhitespace)
				if (elm.isBlock() || ArrayUtils.contains(blockElements, elm.tagName()))
					builder.add("\n");
		}
	}

	public JCas getJCas() {
		builder.close();
		return builder.getJCas();
	}

	public Map<String, HTMLAnnotation> getAnnotationMap() {
		return annotationMap;
	}
}