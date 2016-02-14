package de.unistuttgart.quadrama.io.gutenbergde;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.factory.JCasBuilder;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeVisitor;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.unistuttgart.quadrama.api.Act;
import de.unistuttgart.quadrama.api.Scene;
import de.unistuttgart.quadrama.api.Speaker;
import de.unistuttgart.quadrama.api.StageDirection;
import de.unistuttgart.quadrama.io.gutenbergde.type.HTMLAnnotation;

public class GutenbergDEReader extends JCasCollectionReader_ImplBase {

	public static final String PARAM_INPUT_DIRECTORY = "Input Directory";

	@ConfigurationParameter(name = PARAM_INPUT_DIRECTORY, mandatory = true)
	String inputDirectory;

	File[] files;

	int current = 0;

	@Override
	public void initialize(UimaContext context)
			throws ResourceInitializationException {
		super.initialize(context);

		File inputDir = new File(this.inputDirectory);
		files = inputDir.listFiles();
	}

	public boolean hasNext() throws IOException, CollectionException {
		return current < files.length - 1;
	}

	public Progress[] getProgress() {
		// TODO: implement
		return null;
	}

	@Override
	public void getNext(JCas jcas) throws IOException, CollectionException {

		DocumentMetaData dmd = DocumentMetaData.create(jcas);
		dmd.setDocumentId("test");

		File file = files[current++];

		try {
			String str = IOUtils.toString(new FileInputStream(file));
			org.jsoup.nodes.Document doc = Jsoup.parseBodyFragment(str);
			Visitor vis = new Visitor(jcas);
			doc.traverse(vis);
			jcas = vis.getJCas();
		} finally {}

		int currentSceneBegin = -1;
		int currentActBegin = -1;
		for (HTMLAnnotation anno : JCasUtil.select(jcas, HTMLAnnotation.class)) {
			if (anno.getCls().equals("speaker") && anno.getTag().equals("span")) {
				AnnotationFactory.createAnnotation(jcas, anno.getBegin(),
						anno.getEnd(), Speaker.class);
			} else if (anno.getCls().equals("regie")
					&& anno.getTag().equals("span")) {
				AnnotationFactory.createAnnotation(jcas, anno.getBegin(),
						anno.getEnd(), StageDirection.class);
			}
			if (anno.getTag().equals("h2")) {
				if (currentSceneBegin >= 0) {
					AnnotationFactory.createAnnotation(jcas, currentSceneBegin,
							anno.getBegin() - 1, Scene.class);
				}
				currentSceneBegin = anno.getBegin();
			}
			if (anno.getTag().equals("h1")) {
				if (currentActBegin >= 0) {
					AnnotationFactory.createAnnotation(jcas, currentActBegin,
							anno.getBegin() - 1, Act.class);
				}
				currentActBegin = anno.getBegin();
			}
			// TODO: Detecting an utterance
		}
	}

	public class Visitor implements NodeVisitor {

		JCasBuilder builder;
		Map<Node, Integer> beginMap = new HashMap<Node, Integer>();
		String[] blockElements = new String[] { "p", "br", "h1", "h2" };

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
				anno.setCls(elm.className());
				if (ArrayUtils.contains(blockElements, elm.tagName()))
					builder.add("\n");
			}
		}

		public JCas getJCas() {
			builder.close();
			return builder.getJCas();
		}
	}

}
