package de.unistuttgart.quadrama.io.dot;

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.jgrapht.DirectedGraph;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.VertexNameProvider;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedPseudograph;

import de.tudarmstadt.ukp.dkpro.core.api.io.JCasFileWriter_ImplBase;
import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.ims.drama.api.FigureMention;
import de.unistuttgart.ims.drama.api.Speaker;
import de.unistuttgart.ims.drama.api.Utterance;

@Deprecated
public class MentionNetworkExtractor extends JCasFileWriter_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		DirectedGraph<Figure, DefaultEdge> graph = new DirectedPseudograph<Figure, DefaultEdge>(DefaultEdge.class);

		for (Utterance utterance : JCasUtil.select(jcas, Utterance.class)) {
			Speaker speaker = JCasUtil.selectCovered(Speaker.class, utterance).get(0);
			for (FigureMention mention : JCasUtil.selectCovered(jcas, FigureMention.class, utterance)) {
				if (speaker.getFigure() != null && mention.getFigure() != null) {
					if (!graph.containsVertex(speaker.getFigure()))
						graph.addVertex(speaker.getFigure());
					if (!graph.containsVertex(mention.getFigure()))
						graph.addVertex(mention.getFigure());
					graph.addEdge(speaker.getFigure(), mention.getFigure());
				}
			}
		}

		OutputStream docOS = null;
		OutputStreamWriter writer = null;
		try {
			docOS = getOutputStream(jcas, ".dot");
			writer = new OutputStreamWriter(docOS);
			DOTExporter<Figure, DefaultEdge> exporter = new DOTExporter<Figure, DefaultEdge>(
					new VertexNameProvider<Figure>() {

						@Override
						public String getVertexName(Figure vertex) {
							return String.valueOf(vertex.getId());
						}
					}, new VertexNameProvider<Figure>() {

						@Override
						public String getVertexName(Figure vertex) {
							return vertex.getCoveredText();
						}
					}, null);
			exporter.export(writer, graph);

		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		} finally {
			closeQuietly(writer);
			closeQuietly(docOS);

		}
	}

}
