package de.unistuttgart.quadrama.core;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterator;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.unistuttgart.ims.commons.Counter;
import de.unistuttgart.ims.drama.api.Drama;
import de.unistuttgart.ims.drama.api.Speaker;

@Deprecated
public class TestSpeakerIdentifier {

	public void evaluateSpeakerAssignments() throws Exception {
		JCasIterator iter = SimplePipeline.iteratePipeline(
				CollectionReaderFactory.createReaderDescription(XmiReader.class, XmiReader.PARAM_SOURCE_LOCATION,
						"src/test/resources/SpeakerIdentifier/tx4z.0.xmi", XmiReader.PARAM_LENIENT, true),
				createEngineDescription(FigureReferenceAnnotator.class),
				createEngineDescription(SpeakerIdentifier.class, SpeakerIdentifier.PARAM_CREATE_SPEAKER_FIGURE, false),
				createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION, "target/doc")).iterator();
		StringBuilder b = new StringBuilder();
		CSVPrinter writer = new CSVPrinter(b, CSVFormat.TDF);
		SummaryStatistics types = new SummaryStatistics();
		SummaryStatistics tokens = new SummaryStatistics();
		while (iter.hasNext()) {
			JCas jcas = iter.next();
			int s = 0;
			int all = 0;
			Counter<String> unassigned = new Counter<String>();
			for (Speaker speaker : JCasUtil.select(jcas, Speaker.class)) {
				if (speaker.getFigure() == null) {
					unassigned.add(speaker.getCoveredText());
					s++;
				}
				all++;
			}
			types.addValue(unassigned.size());
			tokens.addValue(s);
			writer.printRecord(JCasUtil.selectSingle(jcas, Drama.class).getDocumentId(), s, unassigned.size(), all);
		}
		writer.printRecord("mean", tokens.getMean(), types.getMean());
		writer.printRecord("min", tokens.getMin(), types.getMin());
		writer.printRecord("max", tokens.getMax(), types.getMax());
		writer.close();
		System.out.println(b.toString());
	}
}
