package de.unistuttgart.quadrama.io.tei.textgrid;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.resource.ResourceInitializationException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceChain;
import de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceLink;
import de.unistuttgart.ims.drama.api.Act;
import de.unistuttgart.ims.drama.api.ActHeading;
import de.unistuttgart.ims.drama.api.Author;
import de.unistuttgart.ims.drama.api.CastFigure;
import de.unistuttgart.ims.drama.api.Drama;
import de.unistuttgart.ims.drama.api.DramatisPersonae;
import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.ims.drama.api.FrontMatter;
import de.unistuttgart.ims.drama.api.MainMatter;
import de.unistuttgart.ims.drama.api.Scene;
import de.unistuttgart.ims.drama.api.SceneHeading;
import de.unistuttgart.ims.drama.api.Speaker;
import de.unistuttgart.ims.drama.api.Speech;
import de.unistuttgart.ims.drama.api.StageDirection;
import de.unistuttgart.ims.drama.api.Translator;
import de.unistuttgart.ims.drama.api.Utterance;
import de.unistuttgart.ims.drama.util.AnnotationComparator;
import de.unistuttgart.ims.uimautil.AnnotationUtil;
import de.unistuttgart.quadrama.io.core.AbstractDramaUrlReader;
import de.unistuttgart.quadrama.io.core.GenericXmlReader;

public class GerDraCorUrlReader extends AbstractDramaUrlReader {

	public static final String PARAM_STRICT = "strict";

	public static final String PARAM_TEI_COMPAT = "TEI compatibility";

	@ConfigurationParameter(name = PARAM_STRICT, mandatory = false, defaultValue = "false")
	boolean strict = false;

	@ConfigurationParameter(name = PARAM_TEI_COMPAT, mandatory = false, defaultValue = "false")
	boolean teiCompatibility = false;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
	}

	@Override
	public void getNext(final JCas jcas, InputStream file, Drama drama) throws IOException, CollectionException {

		GenericXmlReader gxr = new GenericXmlReader();
		gxr.setTextRootSelector(teiCompatibility ? null : "TEI > text");
		gxr.setPreserveWhitespace(teiCompatibility);

		// title
		gxr.addAction("titleStmt > title:first-child", Drama.class, (d, e) -> d.setDocumentTitle(e.text()));

		// id
		gxr.addAction("sourceDesc > bibl > idno[type=URL]", Drama.class,
				(d, e) -> d.setDocumentId(e.text().substring(36)));

		// author
		gxr.addAction("author", (jc, e) -> {
			Author author = new Author(jcas);
			author.setName(e.text());
			if (e.hasAttr("key")) {
				author.setPnd(e.attr("key").replace("pnd:", ""));
			}
			author.addToIndexes();
		});

		// translator
		gxr.addAction("editor[role=translator]", (j, e) -> {
			Translator transl = new Translator(jcas);
			transl.setName(e.text());
			if (e.hasAttr("key"))
				transl.setPnd(e.attr("key").replace("pnd:", ""));
		});

		// date printed
		gxr.addAction("date[type=print][when]", Drama.class,
				(d, e) -> d.setDatePrinted(Integer.valueOf(e.attr("when"))));

		// date written
		gxr.addAction("date[type=written][when]", Drama.class,
				(d, e) -> d.setDateWritten(Integer.valueOf(e.attr("when"))));

		// date premiere
		gxr.addAction("date[type=premiere][when]", Drama.class,
				(d, e) -> d.setDatePremiere(Integer.valueOf(e.attr("when"))));

		gxr.addMapping("front", FrontMatter.class);
		gxr.addMapping("body", MainMatter.class);

		// Segmentation
		gxr.addMapping("div[type=prologue]", Act.class, (a, e) -> a.setRegular(false));

		gxr.addMapping("div[type=act]", Act.class, (a, e) -> a.setRegular(true));
		gxr.addMapping("div[type=act] > div > desc > title", ActHeading.class);
		gxr.addMapping("div[type=act] > div > head", ActHeading.class);

		gxr.addMapping("div[type=scene]", Scene.class, (a, e) -> a.setRegular(true));
		gxr.addMapping("div[type=scene] > div > desc > title", SceneHeading.class);

		// Dramatis Personae
		gxr.addMapping("body castList castItem", Figure.class);
		gxr.addMapping("div[type=Dramatis_Personae]", DramatisPersonae.class);
		Map<String, String> xmlAlias = new HashMap<String, String>();
		gxr.addDocumentMapping("particDesc > listPerson > person", CastFigure.class, (cf, e) -> {
			List<String> nameList = new LinkedList<String>();
			List<String> xmlIdList = new LinkedList<String>();

			if (e.hasAttr("xml:id"))
				xmlIdList.add(e.attr("xml:id"));
			if (e.hasAttr("sex"))
				cf.setGender(e.attr("sex"));
			if (e.hasAttr("age"))
				cf.setAge(e.attr("age"));

			// gather names
			Elements nameElements = e.select("persName");

			for (int j = 0; j < nameElements.size(); j++) {
				nameList.add(nameElements.get(j).text());
				if (nameElements.get(j).hasAttr("xml:id")) {
					xmlIdList.add(nameElements.get(j).attr("xml:id"));
					xmlAlias.put(nameElements.get(j).attr("xml:id"), e.attr("xml:id"));
				}
			}
			for (TextNode tn : e.textNodes()) {
				if (tn.text().trim().length() > 0)
					nameList.add(tn.text().trim());
			}
			cf.setXmlId(TEIUtil.toStringArray(jcas, xmlIdList));
			cf.setNames(TEIUtil.toStringArray(jcas, nameList));

		});

		gxr.addMapping("speaker", Speaker.class);
		gxr.addMapping("stage", StageDirection.class);
		gxr.addMapping("l", Speech.class);
		gxr.addMapping("p", Speech.class);
		gxr.addMapping("ab", Speech.class);
		gxr.addMapping("sp", Utterance.class, (u, e) -> {
			Collection<Speaker> speakers = JCasUtil.selectCovered(Speaker.class, u);
			for (Speaker sp : speakers) {
				String[] whos = e.attr("who").split(" ");
				sp.setXmlId(new StringArray(jcas, whos.length));
				sp.setCastFigure(new FSArray(jcas, whos.length));
				for (int i = 0; i < whos.length; i++) {
					String xmlid = whos[i].substring(1);
					sp.setXmlId(i, xmlid);
					if (xmlAlias.containsKey(xmlid))
						xmlid = xmlAlias.get(xmlid);
					if (gxr.exists(xmlid))
						sp.setCastFigure(i, (CastFigure) gxr.getAnnotation(xmlid).getValue());

				}
			}
		});

		// mentions
		final Map<String, SortedSet<CoreferenceLink>> id2link = new HashMap<String, SortedSet<CoreferenceLink>>();
		gxr.addMapping("sp *[ref]", CoreferenceLink.class, (cl, e) -> {
			String xmlId = e.attr("ref");
			if (!id2link.containsKey(xmlId)) {
				id2link.put(xmlId, new TreeSet<CoreferenceLink>(new AnnotationComparator()));
			}
			id2link.get(xmlId).add(cl);
		});

		gxr.read(jcas, file);

		// Connect coreference chains, add some of them to CastFigures
		for (String xmlId : id2link.keySet()) {
			CoreferenceChain cc = new CoreferenceChain(jcas);
			cc.addToIndexes();
			// attach to cast figure if possible
			if (gxr.exists(xmlId))
				((CastFigure) gxr.getAnnotation(xmlId).getValue()).setChain(cc);
			CoreferenceLink last = null;
			for (CoreferenceLink cl : id2link.get(xmlId)) {
				if (cc.getFirst() == null) {
					cc.setFirst(cl);
					last = cl;
				} else if (last != null) {
					last.setNext(cl);
					last = cl;
				}
			}
		}

		AnnotationUtil.trim(new ArrayList<Figure>(JCasUtil.select(jcas, Figure.class)));
		AnnotationUtil.trim(new ArrayList<Speech>(JCasUtil.select(jcas, Speech.class)));
		AnnotationUtil.trim(new ArrayList<Utterance>(JCasUtil.select(jcas, Utterance.class)));
		AnnotationUtil.trim(new ArrayList<Scene>(JCasUtil.select(jcas, Scene.class)));
		AnnotationUtil.trim(new ArrayList<Act>(JCasUtil.select(jcas, Act.class)));
		AnnotationUtil.trim(new ArrayList<StageDirection>(JCasUtil.select(jcas, StageDirection.class)));

	}

	@Deprecated
	private static void readCast(JCas jcas, Drama drama, Document doc) {
		Map<String, CastFigure> idFigureMap = new HashMap<String, CastFigure>();
		Elements castEntries = doc.select("profileDesc > particDesc > listPerson > person");
		castEntries.addAll(doc.select("profileDesc > particDesc > listPerson > personGrp"));
		FSArray castListArray = new FSArray(jcas, castEntries.size());
		for (int i = 0; i < castEntries.size(); i++) {
			Element castEntry = castEntries.get(i);
			CastFigure figure = TEIUtil.parsePersonElement(jcas, castEntry);

			for (int j = 0; j < figure.getXmlId().size(); j++) {
				idFigureMap.put(figure.getXmlId(j), figure);
			}
			castListArray.set(i, figure);
		}
		drama.setCastList(castListArray);

		for (Speaker speaker : JCasUtil.select(jcas, Speaker.class)) {
			speaker.setCastFigure(new FSArray(jcas, speaker.getXmlId().size()));
			for (int i = 0; i < speaker.getXmlId().size(); i++)
				speaker.setCastFigure(i, idFigureMap.get(speaker.getXmlId(i)));
		}
	}

}
