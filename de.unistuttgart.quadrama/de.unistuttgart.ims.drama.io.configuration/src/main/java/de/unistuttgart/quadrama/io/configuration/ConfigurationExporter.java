package de.unistuttgart.quadrama.io.configuration;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.IOUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.io.JCasFileWriter_ImplBase;
import de.unistuttgart.quadrama.api.Figure;
import de.unistuttgart.quadrama.api.Scene;
import de.unistuttgart.quadrama.api.Speaker;

public class ConfigurationExporter extends JCasFileWriter_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

		Map<Integer, Set<Integer>> map = new HashMap<Integer, Set<Integer>>();

		for (Scene scene : JCasUtil.select(jcas, Scene.class)) {
			Set<Integer> set = new HashSet<Integer>();
			for (Speaker figure : JCasUtil.selectCovered(jcas, Speaker.class,
					scene)) {
				if (figure.getFigure() != null)
					set.add(figure.getFigure().getBegin());
			}
			map.put(scene.getBegin(), set);
		}
		OutputStream os = null;
		CSVPrinter p = null;
		try {
			os = getOutputStream(jcas, ".csv");
			String[] header =
					new String[JCasUtil.select(jcas, Figure.class).size()];
			int f = 0;
			for (Figure fig : JCasUtil.select(jcas, Figure.class)) {
				header[f++] = fig.getCoveredText();
			}
			p =
					new CSVPrinter(new OutputStreamWriter(os), CSVFormat.EXCEL
							.withHeader(header).withCommentMarker('#'));

			int i = 0;
			TreeSet<Scene> scenes = new TreeSet<Scene>(new Comparator<Scene>() {

				public int compare(Scene o1, Scene o2) {
					return Integer.compare(o1.getBegin(), o2.getBegin());
				}
			});
			scenes.addAll(JCasUtil.select(jcas, Scene.class));
			for (Scene scene : scenes) {
				p.printComment(scene.getCoveredText().substring(0, 20));
				p.print(i++);
				for (Figure figure : JCasUtil.select(jcas, Figure.class)) {
					if (map.get(scene.getBegin()).contains(figure.getBegin()))
						p.print("1");
					else
						p.print("0");
				}
				p.println();
			}
			p.flush();
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		} finally {
			IOUtils.closeQuietly(p);
			IOUtils.closeQuietly(os);
		}

	}
}
