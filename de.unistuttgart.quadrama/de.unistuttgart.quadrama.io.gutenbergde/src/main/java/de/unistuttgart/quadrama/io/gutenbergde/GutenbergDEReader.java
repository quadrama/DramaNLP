package de.unistuttgart.quadrama.io.gutenbergde;

import java.io.IOException;

import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.Progress;

public class GutenbergDEReader extends JCasCollectionReader_ImplBase {

	public boolean hasNext() throws IOException, CollectionException {
		// TODO Auto-generated method stub
		return false;
	}

	public Progress[] getProgress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void getNext(JCas jCas) throws IOException, CollectionException {
		// TODO Auto-generated method stub

	}

}
