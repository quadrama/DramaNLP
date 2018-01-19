package de.unistuttgart.quadrama.io.core;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.ims.drama.api.Speaker;
import de.unistuttgart.ims.drama.api.Speech;
import de.unistuttgart.ims.drama.api.Utterance;
import de.unistuttgart.ims.drama.util.DramaUtil;

@Deprecated
public class ExtractSpeechByFigure extends AbstractExtractSpeechConsumer {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		File file = new File(outputDirectory, DramaUtil.getDisplayId(jcas));
		file.mkdir();

		Map<String, Writer> writerMap = new HashMap<String, Writer>();
		try {
			for (Figure figure : JCasUtil.select(jcas, Figure.class)) {
				writerMap.put(figure.getReference(), new FileWriter(new File(file, figure.getCoveredText() + ".txt")));
			}
			for (Utterance utterance : JCasUtil.select(jcas, Utterance.class)) {
				try {
					Speaker speaker = JCasUtil.selectCovered(Speaker.class, utterance).get(0);
					if (speaker.getFigure() != null) {
						List<Speech> speeches = JCasUtil.selectCovered(Speech.class, utterance);
						String writerIndex = speaker.getFigure().getReference();

						for (Speech speech : speeches) {

							if (writerMap.containsKey(writerIndex)) {
								writerMap.get(writerIndex).write(speech.getCoveredText());
								writerMap.get(writerIndex).write(" ");
							}
						}
						if (writerMap.containsKey(writerIndex))
							writerMap.get(writerIndex).write("\n");
					}
				} catch (IndexOutOfBoundsException e) {
					// there is no speaker annotation in this utterance
				}
			}
			for (Writer writer : writerMap.values()) {
				writer.flush();
				writer.close();
			}

		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		} finally {
			for (Writer writer : writerMap.values()) {
				IOUtils.closeQuietly(writer);
			}
		}
	}

}
