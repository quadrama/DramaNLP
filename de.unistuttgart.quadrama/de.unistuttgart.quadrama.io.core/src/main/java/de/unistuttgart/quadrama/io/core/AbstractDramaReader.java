package de.unistuttgart.quadrama.io.core;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.factory.JCasBuilder;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeVisitor;

import de.unistuttgart.quadrama.io.core.type.HTMLAnnotation;

public abstract class AbstractDramaReader extends JCasCollectionReader_ImplBase {
	public static final String PARAM_INPUT_DIRECTORY = "Input Directory";

	@ConfigurationParameter(name = PARAM_INPUT_DIRECTORY, mandatory = true)
	String inputDirectory;

	protected File[] files;

	protected int current = 0;

	@Override
	public void initialize(UimaContext context)
			throws ResourceInitializationException {
		super.initialize(context);

		File inputDir = new File(inputDirectory);
		files = inputDir.listFiles();
	}

	public boolean hasNext() throws IOException, CollectionException {
		return current < files.length - 1;
	}

	public Progress[] getProgress() {
		// TODO: implement
		return null;
	}

	public <T extends Annotation> Collection<T> select2Annotation(JCas jcas,
			Element rootElement, Map<String, HTMLAnnotation> annoMap,
			String cssSelector, Class<T> annoClass,
			Annotation coveringAnnotation) {
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

	public <T extends Annotation> T selectRange2Annotation(JCas jcas,
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

	public class Visitor implements NodeVisitor {

		JCasBuilder builder;
		Map<Node, Integer> beginMap = new HashMap<Node, Integer>();

		Map<String, HTMLAnnotation> annotationMap =
				new HashMap<String, HTMLAnnotation>();

		String[] blockElements = new String[] { "l", "p", "sp" };

		public Visitor(JCas jcas) {
			builder = new JCasBuilder(jcas);
		}

		public void head(Node node, int depth) {
			if (node.getClass().equals(TextNode.class)) {
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
				if (elm.className().isEmpty())
					anno.setCls(elm.attr("type"));
				else
					anno.setCls(elm.className());
				annotationMap.put(elm.cssSelector(), anno);
				if (elm.isBlock()
						|| ArrayUtils.contains(blockElements, elm.tagName()))
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

}
