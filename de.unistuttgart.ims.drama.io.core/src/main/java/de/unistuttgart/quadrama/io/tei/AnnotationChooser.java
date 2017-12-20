package de.unistuttgart.quadrama.io.tei;

import java.util.Comparator;

import org.apache.uima.jcas.tcas.Annotation;

public class AnnotationChooser implements Comparator<Annotation> {

	int currentPosition;

	public AnnotationChooser(int cPos) {
		currentPosition = cPos;
	}

	@Override
	public int compare(Annotation o1, Annotation o2) {
		boolean o1Begin = o1.getBegin() == currentPosition, o2Begin = o2.getBegin() == currentPosition;

		int o1Length = o1.getEnd() - o1.getBegin();
		int o2Length = o2.getEnd() - o2.getBegin();
		// both start here
		if (o1Begin && o2Begin) {
			if (o1Length >= o2Length)
				return 1;
			return -1;
			// one starts here, the other doesnt
		} else if (o1Begin && !o2Begin) {
			return -1;
		} else if (o2Begin && !o1Begin) {
			return 1;
		}
		if (o1Length >= o2Length)
			return -1;
		if (o2Length > o1Length)
			return 1;

		return 0;
	}

}
