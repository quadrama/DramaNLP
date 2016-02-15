package de.unistuttgart.quadrama.io.gutenbergde;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.factory.JCasBuilder;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeVisitor;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.unistuttgart.quadrama.api.Act;
import de.unistuttgart.quadrama.api.DramatisPersonae;
import de.unistuttgart.quadrama.api.Footnote;
import de.unistuttgart.quadrama.api.Scene;
import de.unistuttgart.quadrama.api.Speaker;
import de.unistuttgart.quadrama.api.StageDirection;
import de.unistuttgart.quadrama.api.Utterance;
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

		String str = IOUtils.toString(new FileInputStream(file));
		org.jsoup.nodes.Document doc = Jsoup.parseBodyFragment(str);
		Visitor vis = new Visitor(jcas);
		doc.traverse(vis);
		jcas = vis.getJCas();
		Map<String, HTMLAnnotation> annoMap = vis.getAnnotationMap();
		select2Annotation(jcas, doc, annoMap, "span.speaker", Speaker.class);
		select2Annotation(jcas, doc, annoMap, "span.regie",
				StageDirection.class);
		select2Annotation(jcas, doc, annoMap, "span.footnote", Footnote.class);
		select2Annotation(jcas, doc, annoMap, "h3 + p", DramatisPersonae.class);

		for (Utterance utter : select2Annotation(jcas, doc, annoMap,
				"p:has(span.speaker)", Utterance.class)) {
			utter.setSpeaker(JCasUtil.selectCovered(Speaker.class, utter)
					.get(0));
			try {
				utter.setStage(JCasUtil.selectCovered(StageDirection.class,
						utter).get(0));
			} catch (IndexOutOfBoundsException e) {
				// many utterances don't have stage directions
			}
			// TODO: identify speech content
		};

		this.assignSpeakerIds(jcas);

		int currentSceneBegin = -1;
		int currentActBegin = -1;
		for (HTMLAnnotation anno : JCasUtil.select(jcas, HTMLAnnotation.class)) {
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
		}
	}

	public <T extends Annotation> Collection<T> select2Annotation(JCas jcas,
			Element rootElement, Map<String, HTMLAnnotation> annoMap,
			String cssSelector, Class<T> annoClass) {
		HashSet<T> set = new HashSet<T>();
		Elements elms = rootElement.select(cssSelector);
		for (Element elm : elms) {
			HTMLAnnotation hAnno = annoMap.get(elm.cssSelector());
			if (JCasUtil.selectCovering(DramatisPersonae.class, hAnno)
					.isEmpty())
				set.add(AnnotationFactory.createAnnotation(jcas,
						hAnno.getBegin(), hAnno.getEnd(), annoClass));
		}
		return set;
	}

	public void assignSpeakerIds(JCas jcas) {
		DramatisPersonae dp =
				JCasUtil.selectSingle(jcas, DramatisPersonae.class);

		int speakerId = 1;
		Map<String, Speaker> speakerMap = new HashMap<String, Speaker>();
		for (Speaker speaker : JCasUtil.selectCovered(Speaker.class, dp)) {
			speaker.setId(speakerId++);
			speakerMap.put(speaker.getCoveredText(), speaker);
		};

		for (Speaker speaker : JCasUtil.select(jcas, Speaker.class)) {
			if (speaker.getId() == 0) {
				try {
					speaker.setId(speakerMap.get(speaker.getCoveredText())
							.getId());
				} catch (NullPointerException e) {
					// no entry in speaker map
				}
			}
		}
	}

	public class Visitor implements NodeVisitor {

		JCasBuilder builder;
		Map<Node, Integer> beginMap = new HashMap<Node, Integer>();

		Map<String, HTMLAnnotation> annotationMap =
				new HashMap<String, HTMLAnnotation>();

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
				annotationMap.put(elm.cssSelector(), anno);
				if (elm.isBlock()) builder.add("\n");
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
