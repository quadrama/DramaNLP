package de.unistuttgart.quadrama.core;

import java.util.HashSet;
import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.ims.drama.api.FigureDescription;
import de.unistuttgart.ims.drama.api.FigureName;
import de.unistuttgart.ims.uimautil.AnnotationUtil;

@TypeCapability(inputs = { "de.unistuttgart.ims.drama.api.Figure" }, outputs = {
		"de.unistuttgart.ims.drama.api.figure.Name", "de.unistuttgart.ims.drama.api.figure.Description" })
public class FigureDetailsAnnotator extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		FigureName fName = null;
		FigureDescription fDesc = null;
		Set<Figure> waitingForDescription = new HashSet<Figure>();
		for (Figure figure : JCasUtil.select(jcas, Figure.class)) {
			if (figure.getCoveredText().split(",").length <= 2) {
				int b = figure.getBegin();
				int commaPosition = figure.getCoveredText().indexOf(',');
				if (figure.getCoveredText().endsWith(",")) {
					// the line ends with a comma
					fName = AnnotationFactory.createAnnotation(jcas, b, b + commaPosition, FigureName.class);

					AnnotationUtil.trim(fName);
					figure.setName(fName);
					waitingForDescription.add(figure);
				} else if (commaPosition != -1) {
					fName = AnnotationFactory.createAnnotation(jcas, b, b + commaPosition, FigureName.class);
					fDesc = AnnotationFactory.createAnnotation(jcas, b + commaPosition + 1, figure.getEnd(),
							FigureDescription.class);

					AnnotationUtil.trim(fName);
					try {
						AnnotationUtil.trim(fName, ',', '.');
					} catch (java.lang.ArrayIndexOutOfBoundsException e) {
					}
					AnnotationUtil.trim(fDesc);
					try {
						AnnotationUtil.trim(fDesc, ',', '.');
					} catch (java.lang.ArrayIndexOutOfBoundsException e) {
					}

					figure.setName(fName);
					figure.setDescription(fDesc);
					for (Figure f : waitingForDescription) {
						f.setDescription(fDesc);
					}
				} else {
					// if no comma is contained in the line, we assume it to be
					// a name (for the time being)
					fName = AnnotationFactory.createAnnotation(jcas, b, figure.getEnd(), FigureName.class);
					figure.setName(fName);

				}
			}
		}
	}

}
