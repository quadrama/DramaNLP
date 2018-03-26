package de.unistuttgart.quadrama.io.tei;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;

import de.unistuttgart.ims.drama.api.CastFigure;
import de.unistuttgart.ims.drama.api.Drama;
import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.ims.drama.api.Speaker;

public class MapFiguresToCastFigures extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		Collection<Figure> figures = JCasUtil.select(jcas, Figure.class);
		FSArray arr = new FSArray(jcas, figures.size());
		Drama drama = (Drama) Drama.get(jcas);
		drama.setCastList(arr);
		int i = 0;
		Map<Figure, CastFigure> figureMap = new HashMap<Figure, CastFigure>();
		for (Figure figure : figures) {
			CastFigure cFigure = new CastFigure(jcas);
			cFigure.setNames(new StringArray(jcas, 1));
			cFigure.setXmlId(new StringArray(jcas, 1));
			cFigure.setXmlId(0, String.valueOf(figure.getId()));
			cFigure.setNames(0, figure.getCoveredText());
			cFigure.addToIndexes();
			drama.setCastList(i++, cFigure);
			figureMap.put(figure, cFigure);
		}

		for (Speaker speaker : JCasUtil.select(jcas, Speaker.class)) {
			if (speaker.getFigure() != null) {
				speaker.setCastFigure(new FSArray(jcas, 1));
				speaker.setCastFigure(0, figureMap.get(speaker.getFigure()));
			}
		}

	}

}
