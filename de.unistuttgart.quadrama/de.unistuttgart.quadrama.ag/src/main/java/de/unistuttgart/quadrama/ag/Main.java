package de.unistuttgart.quadrama.ag;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.unistuttgart.quadrama.core.FigureReferenceAnnotator;
import de.unistuttgart.quadrama.core.SpeakerIdentifier;
import de.unistuttgart.quadrama.io.tei.textgrid.TextgridTEIUrlReader;

public class Main {

	public static void main(String[] args) throws Exception {
		// String dbUrl = "jdbc:mysql://localhost/de.unistuttgart.quadrama";

		CollectionReaderDescription crd =
				CollectionReaderFactory.createReaderDescription(
						TextgridTEIUrlReader.class,
						TextgridTEIUrlReader.PARAM_LANGUAGE, "de",
						TextgridTEIUrlReader.PARAM_URL_LIST,
						"src/main/resources/urls.txt");

		SimplePipeline.runPipeline(
				crd,
				createEngineDescription(SpeakerIdentifier.class,
						SpeakerIdentifier.PARAM_CREATE_SPEAKER_FIGURE, false),
				createEngineDescription(FigureReferenceAnnotator.class),
				createEngineDescription(DramaPropertiesAnnotator.class,
						DramaPropertiesAnnotator.PARAM_PROPERTIES_FILE,
						"src/main/resources/anja1.csv"),
						createEngineDescription(XmiWriter.class,
								XmiWriter.PARAM_TARGET_LOCATION, "target/xmi/",
								XmiWriter.PARAM_USE_DOCUMENT_ID, true));
	}
}
