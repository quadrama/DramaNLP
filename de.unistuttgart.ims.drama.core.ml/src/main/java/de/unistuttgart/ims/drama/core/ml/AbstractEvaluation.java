package de.unistuttgart.ims.drama.core.ml;

import java.io.File;
import java.util.List;

import org.apache.uima.analysis_component.AnalysisComponent;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.cleartk.eval.AnnotationStatistics;
import org.cleartk.eval.Evaluation_ImplBase;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;

public abstract class AbstractEvaluation extends Evaluation_ImplBase<File, AnnotationStatistics<String>> {

	protected Class<? extends AnalysisComponent> tagger;

	public AbstractEvaluation(Class<? extends AnalysisComponent> tagger, File baseDirectory) {
		super(baseDirectory);
		this.tagger = tagger;
	}

	@Override
	protected CollectionReader getCollectionReader(List<File> files) throws Exception {
		return CollectionReaderFactory.createReader(XmiReader.class, XmiReader.PARAM_SOURCE_LOCATION,
				files.get(0).getParent() + "/*.xmi");
	}

}
