package de.unistuttgart.ims.drama.main.annotation;

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
import de.unistuttgart.ims.drama.main.Options;
import de.unistuttgart.quadrama.io.tei.GerDraCorReader;

public class ExportForCorefAnnotator {

	public static void main(String[] args) throws ResourceInitializationException, UIMAException, IOException {

		runMain("", new String[] { "--input", "/Users/reiterns/Documents/QuaDramA/Coreference Annotations/Sources",
				"--output", "/Users/reiterns/Documents/QuaDramA/Coreference Annotations/CorefAnnotator" });
	}

	public static void runMain(String documentId, String[] args) throws UIMAException, IOException {
		Options options = CliFactory.parseArguments(Options.class, args);

		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(GerDraCorReader.class,
				GerDraCorReader.PARAM_INPUT, options.getInput(), GerDraCorReader.PARAM_TEI_COMPAT, true,
				GerDraCorReader.PARAM_REMOVE_XML_ANNOTATIONS, true);

		AggregateBuilder builder = new AggregateBuilder();
		builder.add(AnalysisEngineFactory.createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION,
				options.getOutput()));

		SimplePipeline.runPipeline(reader, builder.createAggregateDescription());
	}

}
