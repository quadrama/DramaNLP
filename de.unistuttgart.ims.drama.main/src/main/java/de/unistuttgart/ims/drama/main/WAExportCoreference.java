package de.unistuttgart.ims.drama.main;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.resource.ResourceInitializationException;

import com.lexicalscope.jewel.cli.CliFactory;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.unistuttgart.ims.drama.main.webanno.WebAnnoPreparation;
import de.unistuttgart.quadrama.io.tei.textgrid.GerDraCorUrlReader;
import de.unistuttgart.quadrama.io.tei.textgrid.TEIWriter;

public class WAExportCoreference {

	public static void main(String[] args) throws ResourceInitializationException, UIMAException, IOException {
		Options options = CliFactory.parseArguments(Options.class, args);

		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(GerDraCorUrlReader.class,
				GerDraCorUrlReader.PARAM_INPUT, options.getInput(), GerDraCorUrlReader.PARAM_TEI_COMPAT, true);

		AggregateBuilder builder = new AggregateBuilder();
		builder.add(AnalysisEngineFactory.createEngineDescription(BreakIteratorSegmenter.class,
				BreakIteratorSegmenter.PARAM_WRITE_SENTENCE, false));
		builder.add(AnalysisEngineFactory.createEngineDescription(WebAnnoPreparation.class));
		builder.add(AnalysisEngineFactory.createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION,
				options.getOutput()));
		builder.add(AnalysisEngineFactory.createEngineDescription(TEIWriter.class, TEIWriter.PARAM_TARGET_LOCATION,
				options.getOutput()));

		SimplePipeline.runPipeline(reader, builder.createAggregateDescription());
	}

}
