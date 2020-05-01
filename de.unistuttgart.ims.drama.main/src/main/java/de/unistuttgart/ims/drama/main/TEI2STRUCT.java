package de.unistuttgart.ims.drama.main;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.resource.ResourceInitializationException;

import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.Option;

import de.tudarmstadt.ukp.dkpro.core.berkeleyparser.BerkeleyParser;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.tudarmstadt.ukp.dkpro.core.languagetool.LanguageToolSegmenter;
import de.tudarmstadt.ukp.dkpro.core.matetools.MateLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.matetools.MateMorphTagger;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordNamedEntityRecognizer;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordPosTagger;
import de.unistuttgart.ims.drama.core.ml.gender.ClearTkGenderAnnotator;
import de.unistuttgart.ims.drama.main.TEI2XMI.Corpus;
import de.unistuttgart.ims.drama.main.TEI2XMI.MyOptions;
import de.unistuttgart.ims.drama.util.CreateCoreferenceGroups;
import de.unistuttgart.ims.drama.util.RemoveDoubledMentions;
import de.unistuttgart.ims.uimautil.SetCollectionId;
import de.unistuttgart.quadrama.core.D;
import de.unistuttgart.quadrama.core.FigureDetailsAnnotator;
import de.unistuttgart.quadrama.core.FigureMentionDetection;
import de.unistuttgart.quadrama.core.FigureReferenceAnnotator;
import de.unistuttgart.quadrama.core.ReadDlinaMetadata;
import de.unistuttgart.quadrama.core.SD;
import de.unistuttgart.quadrama.core.SP;
import de.unistuttgart.quadrama.core.SceneActAnnotator;
import de.unistuttgart.quadrama.core.SetReferenceDate;
import de.unistuttgart.quadrama.core.SpeakerIdentifier;
import de.unistuttgart.quadrama.io.core.AbstractDramaUrlReader;
import de.unistuttgart.quadrama.io.core.ExportAsCONLL;
import de.unistuttgart.quadrama.io.core.ExportAsCSV;
import de.unistuttgart.quadrama.io.tei.CoreTeiReader;
import de.unistuttgart.quadrama.io.tei.GerDraCorReader;
import de.unistuttgart.quadrama.io.tei.MapFiguresToCastFigures;
import de.unistuttgart.quadrama.io.tei.QuaDramAReader;
import de.unistuttgart.quadrama.io.tei.TextgridTEIUrlReader;
import de.unistuttgart.quadrama.io.tei.TheatreClassiqueReader;
import de.unistuttgart.quadrama.io.tei.TurmReader;

public class TEI2STRUCT {
	
	enum Corpus {
		GERDRACOR, TEXTGRID, TURM, THEATRECLASSIQUE, CORETEI, QUADRAMA
	}

	public static void main(String[] args) throws Exception {
		MyOptions options = CliFactory.parseArguments(MyOptions.class, args);

		CollectionReaderDescription reader = getReader(options);

		AggregateBuilder builder = new AggregateBuilder();

		// Tokenize Utterances
		builder.add(D.getWrappedSegmenterDescription(LanguageToolSegmenter.class));
		// Tokenize Stage Directions
		builder.add(SD.getWrappedSegmenterDescription(LanguageToolSegmenter.class));
		// Tokenize Speaker Tags
		builder.add(SP.getWrappedSegmenterDescription(LanguageToolSegmenter.class));
		if (options.getCorpus() == Corpus.TURM) {
			builder.add(createEngineDescription(SceneActAnnotator.class));
		}
		builder.add(createEngineDescription(FigureMentionDetection.class, FigureMentionDetection.PARAM_MANUAL_COREFERENCE, options.isManualCoreference()));
		builder.add(SceneActAnnotator.getDescription());
		builder.add(createEngineDescription(RemoveDoubledMentions.class));
		if (options.getCSVOutput() != null) {
			builder.add(createEngineDescription(ExportAsCSV.class, ExportAsCSV.PARAM_TARGET_LOCATION,
					options.getCSVOutput(), ExportAsCSV.PARAM_CSV_VARIANT_NAME, "Structure"));
		}
		SimplePipeline.runPipeline(reader, builder.createAggregateDescription());
	}

	@SuppressWarnings("unchecked")
	public static Class<? extends AbstractDramaUrlReader> getReaderClass(String readerClassname) {
		Class<?> cl;
		try {
			cl = Class.forName(readerClassname);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return TextgridTEIUrlReader.class;
		}
		if (AbstractDramaUrlReader.class.isAssignableFrom(cl))
			return (Class<? extends AbstractDramaUrlReader>) cl;
		return TextgridTEIUrlReader.class;

	}

	interface MyOptions extends Options {
		@Option(defaultToNull = true)
		File getDlinaDirectory();

		@Option(defaultToNull = true)
		String getCollectionId();

		@Option(defaultToNull = true)
		File getGenderModel();

		@Option()
		boolean isDoCleanup();

		@Deprecated
		@Option(defaultValue = "de.unistuttgart.quadrama.io.tei.textgrid.TextgridTEIUrlReader")
		String getReaderClassname();
		
		/*
		 * Disabled by default. If the coreference annotations are manual,
		 * do not export the character names automatically.
		 */
		@Option()
		boolean isManualCoreference();

		@Option(defaultValue = "de")
		String getLanguage();
		
		@Option
		Corpus getCorpus();

		/**
		 * Storage of the CSV files. Should be a directory.
		 * 
		 * @return A directory
		 */
		@Option(longName = "csvOutput", defaultToNull = true)
		File getCSVOutput();

		/**
		 * Storage of the CoNLL files. Should be a directory.
		 * 
		 * @return A directory
		 */
		@Option(longName = "conllOutput", defaultToNull = true)
		File getCONLLOutput();

	}

	protected static CollectionReaderDescription getReader(MyOptions options) throws ResourceInitializationException {
		switch (options.getCorpus()) {
		case QUADRAMA:
			return CollectionReaderFactory.createReaderDescription(QuaDramAReader.class,
					AbstractDramaUrlReader.PARAM_INPUT, options.getInput(),
					AbstractDramaUrlReader.PARAM_REMOVE_XML_ANNOTATIONS, true, AbstractDramaUrlReader.PARAM_LANGUAGE,
					options.getLanguage());
		case GERDRACOR:
			return CollectionReaderFactory.createReaderDescription(GerDraCorReader.class,
					AbstractDramaUrlReader.PARAM_INPUT, options.getInput(),
					AbstractDramaUrlReader.PARAM_REMOVE_XML_ANNOTATIONS, true, AbstractDramaUrlReader.PARAM_LANGUAGE,
					options.getLanguage());
		case THEATRECLASSIQUE:
			return CollectionReaderFactory.createReaderDescription(TheatreClassiqueReader.class,
					TheatreClassiqueReader.PARAM_INPUT, options.getInput(),
					TheatreClassiqueReader.PARAM_REMOVE_XML_ANNOTATIONS, true, TheatreClassiqueReader.PARAM_LANGUAGE,
					options.getLanguage());
		case CORETEI:
			return CollectionReaderFactory.createReaderDescription(CoreTeiReader.class, CoreTeiReader.PARAM_INPUT,
					options.getInput(), CoreTeiReader.PARAM_REMOVE_XML_ANNOTATIONS, true, CoreTeiReader.PARAM_LANGUAGE,
					options.getLanguage());
		case TURM:
			return CollectionReaderFactory.createReaderDescription(TurmReader.class, AbstractDramaUrlReader.PARAM_INPUT,
					options.getInput(), TurmReader.PARAM_REMOVE_XML_ANNOTATIONS, true, TurmReader.PARAM_LANGUAGE, "de");
		case TEXTGRID:
		default:
			return CollectionReaderFactory.createReaderDescription(TextgridTEIUrlReader.class,
					TextgridTEIUrlReader.PARAM_INPUT, options.getInput(),
					TextgridTEIUrlReader.PARAM_REMOVE_XML_ANNOTATIONS, true, TextgridTEIUrlReader.PARAM_STRICT, true,
					TextgridTEIUrlReader.PARAM_LANGUAGE, options.getLanguage());

		}
	}

}
