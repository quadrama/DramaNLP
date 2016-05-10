package de.unistuttgart.quadrama.io.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
import de.unistuttgart.ims.drama.api.Speaker;
import de.unistuttgart.ims.drama.api.Speech;
import de.unistuttgart.ims.drama.api.Utterance;
import de.unistuttgart.ims.drama.util.DramaUtil;

public class ExtractSpeechByType extends JCasConsumer_ImplBase {

	public static final String PARAM_OUTPUT_DIRECTORY = "Output Directory";
	public static final String PARAM_TYPE = "Sorting Type";
	public static final String PARAM_MERGED = "Merged";

	@ConfigurationParameter(name = PARAM_OUTPUT_DIRECTORY)
	String outputDirectoryName;

	@ConfigurationParameter(name = PARAM_TYPE, mandatory = true)
	String sortingType = null;

	@ConfigurationParameter(name = PARAM_MERGED, mandatory = false, defaultValue = "true")
	boolean merged = true;

	File outputDirectory;
	Map<String, Writer> writerMap = new HashMap<String, Writer>();

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

		File file;
		// if (merged) {
		file = outputDirectory;
		// } else {
		// file = new File(outputDirectory, documentId);
		// file.mkdir();
		// }

		try {
			for (Utterance utterance : JCasUtil.select(jcas, Utterance.class)) {
				try {
					Speaker speaker = JCasUtil.selectCovered(Speaker.class, utterance).get(0);
					if (speaker.getFigure() != null) {
						List<Speech> speeches = JCasUtil.selectCovered(Speech.class, utterance);
						String writerIndex = DramaUtil.getTypeValue(jcas, speaker.getFigure(), sortingType);
						if (writerIndex != null) {
							if (!merged)
								writerIndex = documentId + "_" + writerIndex;
							for (Speech speech : speeches) {
								getWriter(file, writerIndex).write(speech.getCoveredText());
								getWriter(file, writerIndex).write(" ");
							}
							getWriter(file, writerIndex).write("\n");
						}
					}
				} catch (IndexOutOfBoundsException e) {
					// there is no speaker annotation in this utterance
				}
			}
			if (!merged)
				closeWriters();

		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		} finally {
			if (!merged)
				for (Writer writer : writerMap.values()) {
					IOUtils.closeQuietly(writer);
				}
		}

	}

	@Override
	public void collectionProcessComplete() throws AnalysisEngineProcessException {
		super.collectionProcessComplete();
		if (merged)
			closeWriters();
	}

	protected void closeWriters() {
		for (Writer w : writerMap.values()) {
			try {
				w.flush();
			} catch (IOException e) {
			}
			IOUtils.closeQuietly(w);
		}
		writerMap.clear();
	}

	protected Writer getWriter(File file, String value) throws IOException {
		if (!writerMap.containsKey(value)) {
			writerMap.put(value, new FileWriter(new File(file, value + ".txt")));
		}
		return writerMap.get(value);
	}
}
