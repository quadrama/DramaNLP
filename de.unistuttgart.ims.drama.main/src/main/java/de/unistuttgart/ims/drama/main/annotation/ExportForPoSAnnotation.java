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
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.unistuttgart.ims.drama.main.Options;
import de.unistuttgart.quadrama.io.tei.GerDraCorReader;

public class ExportForPoSAnnotation {

	public static void main(String[] args) throws ResourceInitializationException, UIMAException, IOException {

		// runMain(new String[] { "--input",
		// "/Users/reiterns/Documents/QuaDramA/gerdracor/data-tgids/rksp.0.xml",
		// "--output", "target/" });
		// runMain(new String[] { "--input",
		// "/Users/reiterns/Documents/QuaDramA/gerdracor/data-tgids/rjmw.0.xml",
		// "--output", "target/" });
		runMain(new String[] { "--input", "/Users/reiterns/Documents/QuaDramA/gerdracor/data-tgids/11g5g.0.xml",
				"--output", "target/" });
		runMain(new String[] { "--input", "/Users/reiterns/Documents/QuaDramA/gerdracor/data-tgids/jn6k.0.xml",
				"--output", "target/" });
		runMain(new String[] { "--input", "/Users/reiterns/Documents/QuaDramA/gerdracor/data-tgids/k93c.0.xml",
				"--output", "target/" });
		runMain(new String[] { "--input", "/Users/reiterns/Documents/QuaDramA/gerdracor/data-tgids/kjfz.0.xml",
				"--output", "target/" });
		runMain(new String[] { "--input", "/Users/reiterns/Documents/QuaDramA/gerdracor/data-tgids/kmdh.0.xml",
				"--output", "target/" });
		runMain(new String[] { "--input", "/Users/reiterns/Documents/QuaDramA/gerdracor/data-tgids/knb2.0.xml",
				"--output", "target/" });
	}

	public static void runMain(String[] args) throws UIMAException, IOException {
		Options options = CliFactory.parseArguments(Options.class, args);

		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(GerDraCorReader.class,
				GerDraCorReader.PARAM_INPUT, options.getInput(), GerDraCorReader.PARAM_TEI_COMPAT, true,
				GerDraCorReader.PARAM_REMOVE_XML_ANNOTATIONS, true);

		AggregateBuilder builder = new AggregateBuilder();
		builder.add(AnalysisEngineFactory.createEngineDescription(BreakIteratorSegmenter.class,
				BreakIteratorSegmenter.PARAM_WRITE_SENTENCE, false));
		builder.add(AnalysisEngineFactory.createEngineDescription(PreparePosAnnotation.class));
		builder.add(AnalysisEngineFactory.createEngineDescription(PreparePosAnnotation.class,
				PreparePosAnnotation.PARAM_SCENE_BEGIN, 18, PreparePosAnnotation.PARAM_SCENE_END, 21));

		builder.add(AnalysisEngineFactory.createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION,
				options.getOutput()));

		SimplePipeline.runPipeline(reader, builder.createAggregateDescription());
	}

}
