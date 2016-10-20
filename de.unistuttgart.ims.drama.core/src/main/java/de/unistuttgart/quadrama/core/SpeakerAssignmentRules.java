package de.unistuttgart.quadrama.core;

import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
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

import de.unistuttgart.ims.drama.api.Drama;
import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.ims.drama.api.Speaker;

/**
 * This component reads manually created assignment rules from a CSV file. The
 * assignment rules must contain lines that look like this:
 * <p>
 * <code>
 * DRAMAID\tSPEAKER\tFIGURE_REFERENCE
 * </code>
 *
 * </p>
 * <table summary="table column definitions">
 * <tr>
 * <th>DRAMAID</th>
 * <td>The document id of the drama. If the textgrid reader has been used, this
 * is the 6 to 7 character string similar to <code>vndf.0</code>.</td>
 * </tr>
 * <tr>
 * <th>SPEAKER</th>
 * <td>The speaker entry within the drama text without punctuation. In TEI, this
 * is the string enclosed in &lt;speaker&gt; tags.</td>
 * </tr>
 * <tr>
 * <th>FIGURE_REFERENCE</th>
 * <td>This is the entry from the dramatis personae table, in one of two
 * variants:
 * <ol>
 * <li>up to the first punctuation string. E.g., the FIGURE_REFERENCE for
 * "Romeo, Montagues Sohn" would be "Romeo"</li>
 * <li>The entire string covered by the {@link Figure} annotation.</li>
 * </ol>
 * </td>
 * </tr>
 * </table>
 * An example for such a speaker assignment file can be found <a href=
 * "https://raw.githubusercontent.com/quadrama/DramaNLP/master/de.unistuttgart.ims.drama.core/src/test/resources/SpeakerAssignmentRules/speaker-assignment-mapping.tsv">
 * online</a> or in this package unter
 * <code>src/test/resources/SpakerAssignmentRules</code>.
 * 
 * @author reiterns
 *
 */
@TypeCapability(inputs = { "de.unistuttgart.quadrama.api.Figure", "de.unistuttgart.quadrama.api.Figure:Reference",
		"de.unistuttgart.quadrama.api.Speaker" }, outputs = { "de.unistuttgart.quadrama.api.Speaker:Figure" })
public class SpeakerAssignmentRules extends JCasAnnotator_ImplBase {

	public static final String PARAM_RULE_FILE_URL = "Rule File";

	@ConfigurationParameter(name = PARAM_RULE_FILE_URL)
	String ruleFileUrlString;

	Map<String, Map<String, String>> ruleMap = new HashMap<String, Map<String, String>>();

	@Override
	public void initialize(final UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		CSVParser p = null;
		URL ruleFileUrl = null;
		try {
			ruleFileUrl = new URL(ruleFileUrlString);
		} catch (MalformedURLException e1) {
			throw new ResourceInitializationException(e1);
		}

		try {
			p = new CSVParser(new InputStreamReader(ruleFileUrl.openStream()), CSVFormat.TDF.withHeader((String) null));
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
			referenceMap.put(figure.getCoveredText(), figure);
		}
		String tgId = JCasUtil.selectSingle(jcas, Drama.class).getDocumentId();
		if (ruleMap.containsKey(tgId)) {
			Map<String, String> myMap = ruleMap.get(tgId);
			for (Speaker speaker : JCasUtil.select(jcas, Speaker.class)) {
				if (myMap.containsKey(speaker.getCoveredText())) {
					speaker.setFigure(referenceMap.get(myMap.get(speaker.getCoveredText())));
				}
			}
		}
	}

}
