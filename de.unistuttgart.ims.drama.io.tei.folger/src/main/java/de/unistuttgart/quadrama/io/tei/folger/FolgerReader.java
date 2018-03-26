package de.unistuttgart.quadrama.io.tei.folger;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang.ArrayUtils;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.Level;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Parser;

import de.unistuttgart.ims.drama.api.Drama;
import de.unistuttgart.ims.uima.io.xml.Visitor;
import de.unistuttgart.ims.uima.io.xml.type.XMLElement;
import de.unistuttgart.quadrama.io.core.AbstractDramaUrlReader;

public class FolgerReader extends AbstractDramaUrlReader {

	@Override
	public void getNext(JCas jcas, InputStream file, Drama drama) throws IOException, CollectionException {

		getLogger().log(Level.INFO, "Now parsing XML document");
		Document doc = Jsoup.parse(file, "UTF-8", "", Parser.xmlParser());

		Visitor vis = new FolgerVisitor(jcas);
		Element root = doc.select("TEI > text > body").first();
		getLogger().log(Level.INFO, "Traversing XML nodes");
		root.traverse(vis);
		jcas = vis.getJCas();

		getLogger().log(Level.INFO, "Finished Traversing");

	}

	public class FolgerVisitor extends Visitor {

		public FolgerVisitor(JCas jcas) {
			super(jcas);
		}

		@Override
		public void head(Node node, int depth) {
			if (node.getClass().equals(TextNode.class)) {
				builder.add(((TextNode) node).text());
			} else {
				// beginMap.put(node, builder.getPosition());

			}
		}

		@Override
		public void tail(Node node, int depth) {
			if (node.getClass().equals(Element.class)) {
				Element elm = (Element) node;
				XMLElement anno = builder.add(beginMap.get(node), XMLElement.class);
				anno.setTag(elm.tagName());
				anno.setId(elm.id());
				if (elm.className().isEmpty())
					anno.setCls(elm.attr("type"));
				else
					anno.setCls(elm.className());
				annotationMap.put(elm.cssSelector(), anno);
				if (elm.isBlock() || ArrayUtils.contains(blockElements, elm.tagName()))
					builder.add("\n");

			}
		}

	}

}
