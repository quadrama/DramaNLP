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

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.tudarmstadt.ukp.dkpro.core.matetools.MateLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordNamedEntityRecognizer;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordPosTagger;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.unistuttgart.ims.drama.core.ml.gender.ClearTkGenderAnnotator;
import de.unistuttgart.ims.uimautil.SetCollectionId;
import de.unistuttgart.quadrama.core.D;
import de.unistuttgart.quadrama.core.FigureDetailsAnnotator;
import de.unistuttgart.quadrama.core.FigureMentionDetection;
import de.unistuttgart.quadrama.core.FigureReferenceAnnotator;
import de.unistuttgart.quadrama.core.ReadDlinaMetadata;
import de.unistuttgart.quadrama.core.SceneActAnnotator;
import de.unistuttgart.quadrama.core.SetReferenceDate;
import de.unistuttgart.quadrama.core.SpeakerIdentifier;
import de.unistuttgart.quadrama.io.core.AbstractDramaUrlReader;
import de.unistuttgart.quadrama.io.core.ExportAsCSV;
import de.unistuttgart.quadrama.io.tei.CoreTeiReader;
import de.unistuttgart.quadrama.io.tei.GerDraCorReader;
import de.unistuttgart.quadrama.io.tei.MapFiguresToCastFigures;
import de.unistuttgart.quadrama.io.tei.TextgridTEIUrlReader;
import de.unistuttgart.quadrama.io.tei.TheatreClassiqueReader;
import de.unistuttgart.quadrama.io.tei.TurmReader;

public class TEI2XMI {

	enum Corpus {
		GERDRACOR, TEXTGRID, TURM, THEATRECLASSIQUE, CORETEI, QUADRAMA
	}

	public static void main(String[] args) throws Exception {
		MyOptions options = CliFactory.parseArguments(MyOptions.class, args);

		CollectionReaderDescription reader = getReader(options);

		AggregateBuilder builder = new AggregateBuilder();

		builder.add(D.getWrappedSegmenterDescription(BreakIteratorSegmenter.class));
		if (options.getCorpus() == Corpus.TURM) {
			builder.add(createEngineDescription(SceneActAnnotator.class));
		}
		builder.add(createEngineDescription(FigureReferenceAnnotator.class));
		if (options.getCollectionId() != null)
			builder.add(createEngineDescription(SetCollectionId.class, SetCollectionId.PARAM_COLLECTION_ID,
					options.getCollectionId()));
		else {
			switch (options.getCorpus()) {
			case GERDRACOR: 
				builder.add(createEngineDescription(SetCollectionId.class, SetCollectionId.PARAM_COLLECTION_ID,
				"gdc"));
				break;
			case TEXTGRID:
				builder.add(createEngineDescription(SetCollectionId.class, SetCollectionId.PARAM_COLLECTION_ID,
				"tg"));
				break;
			case TURM:
				builder.add(createEngineDescription(SetCollectionId.class, SetCollectionId.PARAM_COLLECTION_ID,
				"turm"));
				break;
			case THEATRECLASSIQUE:
				builder.add(createEngineDescription(SetCollectionId.class, SetCollectionId.PARAM_COLLECTION_ID,
				"tc"));
				break;
			case CORETEI:
				builder.add(createEngineDescription(SetCollectionId.class, SetCollectionId.PARAM_COLLECTION_ID,
				"ctei"));
				break;
			case QUADRAMA:
				builder.add(createEngineDescription(SetCollectionId.class, SetCollectionId.PARAM_COLLECTION_ID,
				"qd"));
				break;
			}
		}
		builder.add(createEngineDescription(FigureDetailsAnnotator.class));
		if (!options.isSkipSpeakerIdentifier()) {
			builder.add(createEngineDescription(SpeakerIdentifier.class, SpeakerIdentifier.PARAM_CREATE_SPEAKER_FIGURE,
					true));
			builder.add(createEngineDescription(MapFiguresToCastFigures.class));
		}
		if (options.getDlinaDirectory() != null) {
			builder.add(createEngineDescription(ReadDlinaMetadata.class, ReadDlinaMetadata.PARAM_DLINA_DIRECTORY,
					options.getDlinaDirectory()));
			builder.add(createEngineDescription(SetReferenceDate.class));
		}
		if (options.getGenderModel() != null) {
			builder.add(ClearTkGenderAnnotator.getEngineDescription(options.getGenderModel().getAbsolutePath()));
		}
		builder.add(createEngineDescription(StanfordPosTagger.class));
		builder.add(createEngineDescription(MateLemmatizer.class));
		if (!options.isSkipNER())
			builder.add(createEngineDescription(StanfordNamedEntityRecognizer.class));
		builder.add(createEngineDescription(FigureMentionDetection.class));
		builder.add(SceneActAnnotator.getDescription());

		if (options.getOutput() != null)
			builder.add(createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION, options.getOutput()));

		if (options.getCSVOutput() != null) {
			builder.add(createEngineDescription(ExportAsCSV.class, ExportAsCSV.PARAM_TARGET_LOCATION,
					options.getCSVOutput(), ExportAsCSV.PARAM_CSV_VARIANT_NAME, "UtterancesWithTokens"));
			builder.add(createEngineDescription(ExportAsCSV.class, ExportAsCSV.PARAM_TARGET_LOCATION,
					options.getCSVOutput(), ExportAsCSV.PARAM_CSV_VARIANT_NAME, "Segments"));
			builder.add(createEngineDescription(ExportAsCSV.class, ExportAsCSV.PARAM_TARGET_LOCATION,
					options.getCSVOutput(), ExportAsCSV.PARAM_CSV_VARIANT_NAME, "Metadata"));
			builder.add(createEngineDescription(ExportAsCSV.class, ExportAsCSV.PARAM_TARGET_LOCATION,
					options.getCSVOutput(), ExportAsCSV.PARAM_CSV_VARIANT_NAME, "Characters"));
		}
		SimplePipeline.runPipeline(reader, builder.createAggregateDescription());

		if (options.isDoCleanup())
			for (File f : options.getOutput().listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith("xmi");
				}
			})) {
				XmlCleanup.cleanUp(f);
			}
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

		@Option(defaultValue = "de")
		String getLanguage();

		@Option()
		boolean isSkipNER();

		@Option()
		boolean isSkipSpeakerIdentifier();

		@Option
		Corpus getCorpus();

		/**
		 * Storage of the CSV files. Should be a directory.
		 * 
		 * @return A directory
		 */
		@Option(longName = "csvOutput", defaultToNull = true)
		File getCSVOutput();

	}

	protected static CollectionReaderDescription getReader(MyOptions options) throws ResourceInitializationException {
		switch (options.getCorpus()) {
		case QUADRAMA:
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
