package de.unistuttgart.quadrama.core.convert;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceChain;
import de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceLink;
import de.unistuttgart.ims.drama.api.DiscourseEntity;
import de.unistuttgart.ims.drama.api.Mention;
import de.unistuttgart.ims.drama.api.Speaker;

@TypeCapability(inputs = { "de.unistuttgart.ims.drama.api.Mention", "de.unistuttgart.ims.drama.api.DiscourseEntity",
		"de.unistuttgart.ims.drama.api.Speaker" }, outputs = {
				"de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceChain",
				"de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceLink" })
public class QdCoreference2DkproCoreference extends JCasAnnotator_ImplBase {

	public static final String PARAM_INCLUDE_SPEAKERS = "Include Speakers";
	public static final String PARAM_CLEAN_BEFORE = "Clean";

	@ConfigurationParameter(name = PARAM_INCLUDE_SPEAKERS, defaultValue = "false")
	boolean includeSpeakers = false;

	@ConfigurationParameter(name = PARAM_CLEAN_BEFORE, defaultValue = "true")
	boolean cleanBeforeAdding = true;

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

		if (cleanBeforeAdding) {
			Collection<CoreferenceLink> cls = JCasUtil.select(jcas, CoreferenceLink.class);
			if (!cls.isEmpty()) {
				jcas.removeAllExcludingSubtypes(cls.iterator().next().getTypeIndexID());
			}
			Collection<CoreferenceChain> ccs = JCasUtil.select(jcas, CoreferenceChain.class);
			if (!ccs.isEmpty()) {
				jcas.removeAllExcludingSubtypes(ccs.iterator().next().getTypeIndexID());
			}
		}

		Map<DiscourseEntity, CoreferenceChain> chainMap = new HashMap<DiscourseEntity, CoreferenceChain>();
		Map<DiscourseEntity, CoreferenceLink> lastLink = new HashMap<DiscourseEntity, CoreferenceLink>();
		for (DiscourseEntity de : JCasUtil.select(jcas, DiscourseEntity.class)) {
			CoreferenceChain cc = new CoreferenceChain(jcas);
			cc.addToIndexes();
			chainMap.put(de, cc);
		}

		SortedSet<Annotation> mentions = new TreeSet<Annotation>(new Comparator<Annotation>() {
			@Override
			public int compare(Annotation o1, Annotation o2) {
				return Integer.compare(o1.getBegin(), o2.getBegin());
			}
		});
		mentions.addAll(JCasUtil.select(jcas, Mention.class));
		if (includeSpeakers)
			mentions.addAll(JCasUtil.select(jcas, Speaker.class));

		for (Annotation a : mentions) {
			FSArray cFigures = null;
			if (a instanceof Speaker) {
				cFigures = ((Speaker) a).getCastFigure();
			} else if (a instanceof Mention) {
				cFigures = ((Mention) a).getEntity();
			}
			if (cFigures != null) {
				for (int i = 0; i < cFigures.size(); i++) {
					CoreferenceLink cl = AnnotationFactory.createAnnotation(jcas, a.getBegin(), a.getEnd(),
							CoreferenceLink.class);
					DiscourseEntity cf = (DiscourseEntity) cFigures.get(i);
					if (lastLink.containsKey(cf)) {
						lastLink.get(cf).setNext(cl);
					} else {
						chainMap.get(cf).setFirst(cl);
					}
					lastLink.put(cf, cl);

				}

			}
		}
	}

}
