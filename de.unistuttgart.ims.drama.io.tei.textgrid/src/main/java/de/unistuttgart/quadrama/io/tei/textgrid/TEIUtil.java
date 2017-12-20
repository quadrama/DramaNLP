package de.unistuttgart.quadrama.io.tei.textgrid;

import java.util.LinkedList;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import de.unistuttgart.ims.drama.api.CastFigure;
import de.unistuttgart.ims.drama.util.UimaUtil;

public class TEIUtil {

	@Deprecated
	public static CastFigure parsePersonElement(JCas jcas, Element personElement) {
		List<String> nameList = new LinkedList<String>();
		List<String> xmlIdList = new LinkedList<String>();

		CastFigure figure = new CastFigure(jcas);
		if (personElement.hasAttr("xml:id"))
			xmlIdList.add(personElement.attr("xml:id"));
		if (personElement.hasAttr("sex"))
			figure.setGender(personElement.attr("sex").toLowerCase());
		if (personElement.hasAttr("age"))
			figure.setAge(personElement.attr("age"));

		// gather names
		Elements nameElements = personElement.select("persName");

		for (int j = 0; j < nameElements.size(); j++) {
			nameList.add(nameElements.get(j).text());
			if (nameElements.get(j).hasAttr("xml:id"))
				xmlIdList.add(nameElements.get(j).attr("xml:id"));
		}
		for (TextNode tn : personElement.textNodes()) {
			if (tn.text().trim().length() > 0)
				nameList.add(tn.text().trim());
		}
		figure.setXmlId(UimaUtil.toStringArray(jcas, xmlIdList));
		figure.setNames(UimaUtil.toStringArray(jcas, nameList));
		figure.addToIndexes();
		return figure;

	}
}
