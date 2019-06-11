package de.unistuttgart.ims.drama.core.ml.spred;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.unistuttgart.quadrama.io.tei.GerDraCorReader;

public class CreateDevelopmentSet {

	public static void main(String[] args) throws ResourceInitializationException, UIMAException, IOException {
		CollectionReaderDescription crd = CollectionReaderFactory.createReaderDescription(GerDraCorReader.class,
				GerDraCorReader.PARAM_INPUT, "src/main/resources/spred/devel/tei");

		AggregateBuilder b = new AggregateBuilder();
		b.add(ConvertToTextLayer.getDescription());
		b.add(AnalysisEngineFactory.createEngineDescription(BreakIteratorSegmenter.class,
				BreakIteratorSegmenter.PARAM_WRITE_SENTENCE, false));
		b.add(AnalysisEngineFactory.createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION,
				"src/main/resources/spred/devel/xmi"));

		SimplePipeline.runPipeline(crd, b.createAggregateDescription());
	}

}
