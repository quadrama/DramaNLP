package de.unistuttgart.quadrama.io.core;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import de.unistuttgart.ims.drama.api.FigureType;
import de.unistuttgart.ims.drama.api.Speaker;
import de.unistuttgart.ims.drama.api.Speech;
import de.unistuttgart.ims.drama.api.Utterance;
import de.unistuttgart.ims.drama.util.DramaUtil;

public class ExtractSpeechByFigure extends JCasConsumer_ImplBase {

	public static final String PARAM_OUTPUT_DIRECTORY = "Output Directory";

	@Deprecated
	public static final String PARAM_TYPE = "Sorting Type";

	@ConfigurationParameter(name = PARAM_OUTPUT_DIRECTORY)
	String outputDirectoryName;

	@Deprecated
	@ConfigurationParameter(name = PARAM_TYPE, mandatory = false)
	String sortingType = null;

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
		Set<String> typeValues = new HashSet<String>();
		if (sortingType != null)
			for (FigureType ft : JCasUtil.select(jcas, FigureType.class)) {
				if (ft.getTypeClass().equalsIgnoreCase(sortingType))
					typeValues.add(ft.getTypeValue());
			}

		Map<String, Writer> writerMap = new HashMap<String, Writer>();
		try {
			if (sortingType == null)
				for (Figure figure : JCasUtil.select(jcas, Figure.class)) {
					writerMap.put(figure.getReference(),
							new FileWriter(new File(file, figure.getCoveredText() + ".txt")));
				}
			else
				for (String v : typeValues) {
					writerMap.put(v, new FileWriter(new File(file, v + ".txt")));
				}
			for (Utterance utterance : JCasUtil.select(jcas, Utterance.class)) {
				try {
					Speaker speaker = JCasUtil.selectCovered(Speaker.class, utterance).get(0);
					if (speaker.getFigure() != null) {
						List<Speech> speeches = JCasUtil.selectCovered(Speech.class, utterance);
						String writerIndex = speaker.getFigure().getReference();
						if (sortingType != null) {
							writerIndex = DramaUtil.getTypeValue(jcas, speaker.getFigure(), sortingType);
						}
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
