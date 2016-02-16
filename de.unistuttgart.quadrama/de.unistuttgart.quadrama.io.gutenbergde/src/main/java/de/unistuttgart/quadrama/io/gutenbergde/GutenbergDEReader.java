package de.unistuttgart.quadrama.io.gutenbergde;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeSet;

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

import de.unistuttgart.quadrama.api.Act;
import de.unistuttgart.quadrama.api.Drama;
import de.unistuttgart.quadrama.api.DramatisPersonae;
import de.unistuttgart.quadrama.api.Footnote;
import de.unistuttgart.quadrama.api.FrontMatter;
import de.unistuttgart.quadrama.api.MainMatter;
import de.unistuttgart.quadrama.api.Scene;
import de.unistuttgart.quadrama.api.Speaker;
import de.unistuttgart.quadrama.api.Speech;
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

		Drama drama = new Drama(jcas);
		drama.setDocumentId("test");
		drama.addToIndexes();
		jcas.setDocumentLanguage("de");

		File file = files[current++];

		String str = IOUtils.toString(new FileInputStream(file));
		org.jsoup.nodes.Document doc = Jsoup.parseBodyFragment(str);
		Visitor vis = new Visitor(jcas);
		doc.traverse(vis);
		jcas = vis.getJCas();
		Map<String, HTMLAnnotation> annoMap = vis.getAnnotationMap();

		// identify front and main matter
		select2Annotation(jcas, doc, annoMap, "div.gutenb:eq(0)",
				FrontMatter.class, null);
		selectRange2Annotation(jcas, doc, annoMap, "div.gutenb:eq(1)",
				"div.gutenb:last-child", MainMatter.class);
		FrontMatter frontMatter =
				JCasUtil.selectSingle(jcas, FrontMatter.class);
		MainMatter mainMatter = JCasUtil.selectSingle(jcas, MainMatter.class);

		// identify simple annotations
		select2Annotation(jcas, doc, annoMap, "span.speaker", Speaker.class,
				null);
		select2Annotation(jcas, doc, annoMap, "span.regie",
				StageDirection.class, mainMatter);
		select2Annotation(jcas, doc, annoMap, "span.footnote", Footnote.class,
				mainMatter);
		select2Annotation(jcas, doc, annoMap, "h3 + p", DramatisPersonae.class,
				frontMatter);

		select2Annotation(jcas, doc, annoMap, "p:has(span.speaker)",
				Utterance.class, mainMatter);

		this.assignSpeakerIds(jcas);

		annotateSpeech(jcas, mainMatter);

		// aggregating annotations
		// TODO: convert to range function
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

	protected void annotateSpeech(JCas jcas, Annotation mainMatter) {
		String text = jcas.getDocumentText();
		for (Utterance utterance : JCasUtil.selectCovered(Utterance.class,
				mainMatter)) {
			TreeSet<Annotation> except =
					new TreeSet<Annotation>(new Comparator<Annotation>() {

						public int compare(Annotation o1, Annotation o2) {
							return Integer.compare(o1.getBegin(), o2.getBegin());
						}

					});
			except.addAll(JCasUtil.selectCovered(StageDirection.class,
					utterance));
			except.addAll(JCasUtil.selectCovered(Speaker.class, utterance));
			except.addAll(JCasUtil.selectCovered(Footnote.class, utterance));
			int b = utterance.getBegin();
			for (Annotation exc : except) {
				if (exc.getBegin() > b) {
					AnnotationFactory.createAnnotation(jcas, b, exc.getBegin(),
							Speech.class);
				}
				b = exc.getEnd();
			}
			if (b < utterance.getEnd()) {
				AnnotationFactory.createAnnotation(jcas, b, utterance.getEnd(),
						Speech.class);
			}
		}
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

	protected void assignSpeakerIds(JCas jcas) {
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
