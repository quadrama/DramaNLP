package de.unistuttgart.quadrama.core;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.unistuttgart.ims.drama.api.Figure;

public class FigureReferenceFactory {
	Map<Figure, String> figures = new HashMap<Figure, String>();
	Pattern pattern = Pattern.compile("\\p{Punct}", 0);

	public void addFigure(Figure f) {
		figures.put(f, f.getCoveredText());
	}

	public void done() {
		for (Figure figure : figures.keySet()) {
			String s = figure.getCoveredText();
			Matcher m = pattern.matcher(s);
			if (m.find()) {
				String refString = s.substring(0, m.start());
				if (figures.values().contains(refString)) {
					figures.put(figure, s);
				} else {
					figures.put(figure, refString);
				}
			}
		}

		for (Figure figure : figures.keySet()) {
			figure.setReference(figures.get(figure).replaceAll(" ", "_").replaceAll("[^a-z_A-Z]", "").toLowerCase());
		}
	}
}
