package de.unistuttgart.quadrama.core.convert;

import java.util.Collection;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceChain;
import de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceLink;
import de.unistuttgart.ims.drama.api.Speaker;
import de.unistuttgart.quadrama.io.core.type.XMLElement;

@TypeCapability(inputs = { "de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceChain",
		"de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceLink" }, outputs = {
				"de.unistuttgart.quadrama.io.core.type.XMLElement" })
public class DkproCoreference2XmlElement extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

		Map<CoreferenceLink, Collection<Speaker>> index = JCasUtil.indexCovering(jcas, CoreferenceLink.class,
				Speaker.class);

		int id = 0;
		XMLElement h;
		for (CoreferenceChain cc : JCasUtil.select(jcas, CoreferenceChain.class)) {
			CoreferenceLink link = cc.getFirst();

			String xmlId = null;
			while (link != null) {
				if (!index.get(link).isEmpty()) {
					if (xmlId == null)
						xmlId = index.get(link).iterator().next().getXmlId(0);
				} else {
					h = AnnotationFactory.createAnnotation(jcas, link.getBegin(), link.getEnd(), XMLElement.class);
					h.setTag("rs");
					h.setAttributes(" ref=\"#" + (xmlId != null ? xmlId : id) + "\"");
				}
				link = link.getNext();
			}
			id++;
		}
	}

}
