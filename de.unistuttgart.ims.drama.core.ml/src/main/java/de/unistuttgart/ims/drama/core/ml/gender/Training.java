package de.unistuttgart.ims.drama.core.ml.gender;

import java.io.File;
import java.util.Arrays;

import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.cleartk.ml.jar.DefaultDataWriterFactory;
import org.cleartk.ml.jar.DirectoryDataWriterFactory;
import org.cleartk.ml.jar.Train;
import org.cleartk.ml.libsvm.LibSvmStringOutcomeDataWriter;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.unistuttgart.ims.drama.api.DramatisPersonae;
import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.ims.drama.core.ml.PrepareClearTk;

public class Training {

	public static void main(String[] args) throws Exception {
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(XmiReader.class,
				XmiReader.PARAM_SOURCE_LOCATION, "src/main/resources/gender/*/*.xmi", XmiReader.PARAM_LENIENT, true);

		String tmpView = "DP";

		AggregateBuilder b = new AggregateBuilder();

		b.add(AnalysisEngineFactory.createEngineDescription(PrepareClearTk.class, PrepareClearTk.PARAM_VIEW_NAME,
				tmpView, PrepareClearTk.PARAM_ANNOTATION_TYPE, DramatisPersonae.class,
				PrepareClearTk.PARAM_SUBANNOTATIONS, Arrays.asList(Figure.class)));
		b.add(AnalysisEngineFactory.createEngineDescription(BreakIteratorSegmenter.class), CAS.NAME_DEFAULT_SOFA,
				tmpView);
		b.add(AnalysisEngineFactory.createEngineDescription(ClearTkGenderAnnotator.class,
				DefaultDataWriterFactory.PARAM_DATA_WRITER_CLASS_NAME, LibSvmStringOutcomeDataWriter.class,
				DirectoryDataWriterFactory.PARAM_OUTPUT_DIRECTORY, "target/models"), CAS.NAME_DEFAULT_SOFA, tmpView);
		b.add(AnalysisEngineFactory.createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION,
				"target/xmi"));
		SimplePipeline.runPipeline(reader, b.createAggregateDescription());

		Train.main(new File("target/models"), new String[] { "-t", "0" });
	}

}
