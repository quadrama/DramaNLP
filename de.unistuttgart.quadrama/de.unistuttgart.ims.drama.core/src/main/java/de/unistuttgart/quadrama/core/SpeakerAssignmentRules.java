package de.unistuttgart.quadrama.core;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.unistuttgart.quadrama.api.Drama;
import de.unistuttgart.quadrama.api.Figure;
import de.unistuttgart.quadrama.api.Speaker;

@TypeCapability(inputs = { "de.unistuttgart.quadrama.api.Figure:Reference" },
outputs = { "de.unistuttgart.quadrama.api.Speaker:Figure" })
public class SpeakerAssignmentRules extends JCasAnnotator_ImplBase {

	public static final String PARAM_RULE_FILE = "Rule File";

	@ConfigurationParameter(name = PARAM_RULE_FILE)
	String ruleFilename;

	Map<String, Map<String, String>> ruleMap =
			new HashMap<String, Map<String, String>>();

	@Override
	public void initialize(final UimaContext context)
			throws ResourceInitializationException {
		super.initialize(context);
		CSVParser p = null;
		try {
			p =
					new CSVParser(new FileReader(new File(ruleFilename)),
							CSVFormat.TDF.withHeader((String) null));
			Iterator<CSVRecord> iter = p.iterator();
			while (iter.hasNext()) {
				CSVRecord rec = iter.next();
				if (!ruleMap.containsKey(rec.get(0)))
					ruleMap.put(rec.get(0), new HashMap<String, String>());
				ruleMap.get(rec.get(0)).put(rec.get(1), rec.get(2));
			}
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		} finally {
			IOUtils.closeQuietly(p);
		}
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		Map<String, Figure> referenceMap = new HashMap<String, Figure>();
		for (Figure figure : JCasUtil.select(jcas, Figure.class)) {
			referenceMap.put(figure.getReference(), figure);
		}
		String tgId = JCasUtil.selectSingle(jcas, Drama.class).getDocumentId();
		if (ruleMap.containsKey(tgId)) {
			Map<String, String> myMap = ruleMap.get(tgId);
			for (Speaker speaker : JCasUtil.select(jcas, Speaker.class)) {
				if (myMap.containsKey(speaker.getCoveredText())) {
					speaker.setFigure(referenceMap.get(myMap.get(speaker
							.getCoveredText())));
				}
			}
		}
	}

}
