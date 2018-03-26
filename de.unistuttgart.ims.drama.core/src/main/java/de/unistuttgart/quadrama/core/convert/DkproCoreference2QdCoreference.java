package de.unistuttgart.quadrama.core.convert;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceChain;
import de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceLink;
import de.unistuttgart.ims.drama.api.DiscourseEntity;
import de.unistuttgart.ims.drama.api.Mention;
import de.unistuttgart.ims.uima.io.xml.ArrayUtil;

// TODO: Optionally remove dkprocoref
// TODO: Distinguish DiscourseEntity and CastFigure
// TODO: Merge coref chains
public class DkproCoreference2QdCoreference extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		for (CoreferenceChain chain : JCasUtil.select(jcas, CoreferenceChain.class)) {
			DiscourseEntity entity = new DiscourseEntity(jcas);
			entity.addToIndexes();
			CoreferenceLink next = chain.getFirst();
			while (next != null) {
				Mention m = AnnotationFactory.createAnnotation(jcas, next.getBegin(), next.getEnd(), Mention.class);
				m.setEntity(ArrayUtil.toFSArray(jcas, entity));
			}
		}
	}

}
