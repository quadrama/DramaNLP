package de.unistuttgart.quadrama.io.tei;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.io.JCasFileWriter_ImplBase;
import de.unistuttgart.quadrama.io.core.type.XMLElement;
import de.unistuttgart.quadrama.io.core.type.XMLParsingDescription;

public class TEIWriter extends JCasFileWriter_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		StringBuilder b = new StringBuilder(jcas.getDocumentText());

		Collection<XMLElement> htmls = JCasUtil.select(jcas, XMLElement.class);
		Map<Integer, List<XMLElement>> positions = new HashMap<Integer, List<XMLElement>>();

		for (XMLElement h : htmls) {
			if (!positions.containsKey(h.getBegin())) {
				positions.put(h.getBegin(), new LinkedList<XMLElement>());
			}
			positions.get(h.getBegin()).add(h);
			if (h.getBegin() != h.getEnd()) {

				if (!positions.containsKey(h.getEnd())) {
					positions.put(h.getEnd(), new LinkedList<XMLElement>());
				}
				positions.get(h.getEnd()).add(h);
			}

		}

		for (int i = b.length() + 10; i >= 0; i--) {
			final int currentPos = i;
			if (positions.containsKey(i)) {
				TreeSet<XMLElement> ts = new TreeSet<XMLElement>(new AnnotationChooser(currentPos));
				ts.addAll(positions.get(i));
				for (XMLElement h : ts) {
					if (h.getEnd() == h.getBegin()) {
						b.insert(i, "<" + h.getTag() + h.getAttributes() + "/>");
					} else {
						if (h.getEnd() == i) {
							b.insert(i, "</" + h.getTag() + ">");
						} else if (h.getBegin() == i) {
							b.insert(i, "<" + h.getTag() + h.getAttributes() + ">");
						}
					}
				}
			}
		}

		if (JCasUtil.exists(jcas, XMLParsingDescription.class)) {
			XMLParsingDescription xpd = JCasUtil.selectSingle(jcas, XMLParsingDescription.class);
			for (int i = xpd.getXmlDeclarations().size() - 1; i >= 0; i--) {
				b.insert(0, xpd.getXmlDeclarations(i));
			}
		} else {
			b.insert(0, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		}
		try {
			OutputStreamWriter fos = new OutputStreamWriter(getOutputStream(jcas, ".xml"));
			fos.write(b.toString());
			fos.flush();
			fos.close();
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

}
