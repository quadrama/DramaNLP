package de.unistuttgart.ims.drama.core.ml;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.uima.analysis_component.AnalysisComponent;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.cleartk.eval.AnnotationStatistics;
import org.cleartk.eval.Evaluation_ImplBase;
import org.cleartk.util.cr.XReader;

public abstract class AbstractEvaluation extends Evaluation_ImplBase<File, AnnotationStatistics<String>> {

	protected Class<? extends AnalysisComponent> tagger;

	public AbstractEvaluation(Class<? extends AnalysisComponent> tagger, File baseDirectory) {
		super(baseDirectory);
		this.tagger = tagger;
	}

	@Override
	protected CollectionReader getCollectionReader(List<File> files) throws Exception {
		File current = new File("src/main/resources/gender");
		List<String> relativeFilenames = new LinkedList<String>();

		for (File f : files) {
			relativeFilenames.add(f.getName());

		}
		return CollectionReaderFactory.createReader(XReader.class, XReader.PARAM_FILE_NAMES, relativeFilenames,
				XReader.PARAM_ROOT_FILE, current, XReader.PARAM_XML_SCHEME, XReader.XMI);
	}

}
