package de.unistuttgart.ims.drama.main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.lexicalscope.jewel.cli.CliFactory;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.XPathContext;

public class XmlCleanup {

	public static void cleanUp(File file) {
		Set<String> toRemove = new HashSet<String>();

		XPathContext xpc = new XPathContext();
		xpc.addNamespace("xmi", "http://www.omg.org/XMI");
		xpc.addNamespace("cas", "http:///uima/cas.ecore");

		try {
			Builder parser = new Builder();
			Document doc = parser.build(file);
			Nodes nodes_sofa = doc.query("/xmi:XMI/cas:Sofa", xpc);
			for (int i = 0; i < nodes_sofa.size(); i++) {
				Element node_sofa = (Element) nodes_sofa.get(i);
				String sofaName = node_sofa.getAttributeValue("sofaID");
				if (sofaName.startsWith("tmp:")) {
					String sofaId = node_sofa.getAttributeValue("id", "http://www.omg.org/XMI");
					toRemove.add(sofaId);
				}
			}
			for (String id : toRemove) {
				Nodes nodes_toRemove = doc.query("//*[@sofa=" + id + "]|//*[@xmi:id=" + id + "]", xpc);
				for (int i = 0; i < nodes_toRemove.size(); i++) {
					Element e = (Element) nodes_toRemove.get(i);
					e.getParent().removeChild(e);
				}
			}
			FileWriter fw = new FileWriter(file);
			fw.write(doc.toXML());
			fw.flush();
			fw.close();
		} catch (ParsingException ex) {
			System.err.println("Cafe con Leche is malformed today. How embarrassing!");
		} catch (IOException ex) {
			System.err.println("Could not connect to Cafe con Leche. The site may be down.");
		}
	}

	public static void main(String[] args) {
		Options options = CliFactory.parseArguments(Options.class, args);
		cleanUp(options.getInput());
	}

}
