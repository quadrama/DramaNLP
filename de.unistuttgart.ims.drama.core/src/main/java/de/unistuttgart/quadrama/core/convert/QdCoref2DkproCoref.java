package de.unistuttgart.quadrama.core.convert;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceChain;
import de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceLink;
import de.unistuttgart.ims.drama.api.DiscourseEntity;
import de.unistuttgart.ims.drama.api.Mention;
import de.unistuttgart.ims.drama.api.Speaker;
import de.unistuttgart.ims.drama.util.AnnotationComparator;

@TypeCapability(inputs = { "de.unistuttgart.ims.drama.api.Mention", "de.unistuttgart.ims.drama.api.DiscourseEntity",
		"de.unistuttgart.ims.drama.api.Speaker" }, outputs = {
				"de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceChain",
				"de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceLink" })
public class QdCoref2DkproCoref extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		Map<DiscourseEntity, CoreferenceChain> chains = new HashMap<DiscourseEntity, CoreferenceChain>();
		for (DiscourseEntity cf : JCasUtil.select(jcas, DiscourseEntity.class)) {
			CoreferenceChain cc = new CoreferenceChain(jcas);
			cc.addToIndexes();
			chains.put(cf, cc);
		}

		SortedSet<Annotation> mentions = new TreeSet<Annotation>(new AnnotationComparator());
		mentions.addAll(JCasUtil.select(jcas, Mention.class));
		mentions.addAll(JCasUtil.select(jcas, Speaker.class));

		for (Annotation anno : mentions) {
			if (anno instanceof Speaker) {
				Speaker speaker = (Speaker) anno;
				for (int i = 0; i < speaker.getCastFigure().size(); i++) {
					CoreferenceLink cl = AnnotationFactory.createAnnotation(jcas, anno.getBegin(), anno.getEnd(),
							CoreferenceLink.class);
					addToChain(chains.get(speaker.getCastFigure(i)), cl);

				}
			} else if (anno instanceof Mention) {
				Mention mention = (Mention) anno;
				for (int i = 0; i < mention.getEntity().size(); i++) {
					CoreferenceLink cl = AnnotationFactory.createAnnotation(jcas, anno.getBegin(), anno.getEnd(),
							CoreferenceLink.class);
					addToChain(chains.get(mention.getEntity(i)), cl);
				}
			}
		}

	}

	void addToChain(CoreferenceChain chain, CoreferenceLink link) {
		if (chain.getFirst() == null)
			chain.setFirst(link);
		else {
			CoreferenceLink cur = chain.getFirst();
			while (cur.getNext() != null)
				cur = cur.getNext();
			cur.setNext(link);
		}
	}

}
