package de.unistuttgart.ims.drama.main.annotation;

import java.io.File;
import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;

import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.Option;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.unistuttgart.ims.drama.main.Options;
import de.unistuttgart.quadrama.core.convert.DkproCoreference2XmlElement;
import de.unistuttgart.quadrama.io.tei.TEIWriter;

public class Import {

	public static void main(String[] args) throws UIMAException, IOException {
		MyOptions options = CliFactory.parseArguments(MyOptions.class, args);

		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(XmiReader.class,
				XmiReader.PARAM_SOURCE_LOCATION, options.getOriginalInput());

		AggregateBuilder builder = new AggregateBuilder();

		builder.add(AnalysisEngineFactory.createEngineDescription(MergeAnnotations.class,
				MergeAnnotations.PARAM_SOURCE_LOCATION, options.getInput()));
		builder.add(AnalysisEngineFactory.createEngineDescription(DkproCoreference2XmlElement.class));
		builder.add(AnalysisEngineFactory.createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION,
				options.getOutput()));
		builder.add(AnalysisEngineFactory.createEngineDescription(TEIWriter.class, TEIWriter.PARAM_TARGET_LOCATION,
				options.getOutput()));

		SimplePipeline.runPipeline(reader, builder.createAggregateDescription());

	}

	interface MyOptions extends Options {
		@Option()
		File getOriginalInput();
	}
}
