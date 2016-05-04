package de.unistuttgart.quadrama.io.core;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasConsumer_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.ims.drama.api.Speaker;
import de.unistuttgart.ims.drama.api.Speech;
import de.unistuttgart.ims.drama.api.Utterance;

public class ExtractFigureSpeech extends JCasConsumer_ImplBase {

	public static final String PARAM_OUTPUT_DIRECTORY = "Output Directory";

	@ConfigurationParameter(name = PARAM_OUTPUT_DIRECTORY)
	String outputDirectoryName;

	File outputDirectory;

	@Override
	public void initialize(final UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		outputDirectory = new File(outputDirectoryName);

		if (!outputDirectory.exists())
			outputDirectory.mkdirs();
		if (!outputDirectory.isDirectory())
			throw new ResourceInitializationException();
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		String documentId = DocumentMetaData.get(jcas).getDocumentId();
		File file = new File(outputDirectory, documentId);
		file.mkdir();
		Map<Figure, Writer> writerMap = new HashMap<Figure, Writer>();
		try {
			for (Figure figure : JCasUtil.select(jcas, Figure.class)) {
				writerMap.put(figure, new FileWriter(new File(file, figure.getCoveredText())));
			}
			for (Utterance utterance : JCasUtil.select(jcas, Utterance.class)) {
				try {
					Speaker speaker = JCasUtil.selectCovered(Speaker.class, utterance).get(0);
					if (speaker.getFigure() != null) {
						List<Speech> speeches = JCasUtil.selectCovered(Speech.class, utterance);
						for (Speech speech : speeches) {
							writerMap.get(speaker.getFigure()).write(speech.getCoveredText());
							writerMap.get(speaker.getFigure()).write(" ");
						}
					}
					writerMap.get(speaker.getFigure()).write("\n");
				} catch (IndexOutOfBoundsException e) {
					// there is no speaker annotation in this utterance
				}
			}
			for (Figure figure : JCasUtil.select(jcas, Figure.class)) {
				writerMap.get(figure).flush();
				writerMap.get(figure).close();
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
