package de.unistuttgart.quadrama.io.dot;

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.jgrapht.WeightedGraph;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.VertexNameProvider;
import org.jgrapht.graph.DefaultWeightedEdge;

import de.tudarmstadt.ukp.dkpro.core.api.io.JCasFileWriter_ImplBase;
import de.unistuttgart.quadrama.api.Figure;
import de.unistuttgart.quadrama.graph.ext.GraphImporter;

public class DotExporter extends JCasFileWriter_ImplBase {

	public static final String PARAM_VIEW_NAME = "View Name";
	@ConfigurationParameter(name = PARAM_VIEW_NAME, mandatory = true)
	String viewName = null;

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

		JCas view;
		try {
			view = jcas.getView(viewName);

			WeightedGraph<Figure, DefaultWeightedEdge> graph =
					GraphImporter.getGraph(view.getDocumentText(), jcas);

			OutputStream docOS = null;
			OutputStreamWriter writer = null;
			try {
				docOS = getOutputStream(jcas, ".dot");
				writer = new OutputStreamWriter(docOS);
				DOTExporter<Figure, DefaultWeightedEdge> exporter =
						new DOTExporter<Figure, DefaultWeightedEdge>(
								new VertexNameProvider<Figure>() {

									public String getVertexName(Figure vertex) {
										return String.valueOf(vertex.getId());
									}
								}, new VertexNameProvider<Figure>() {

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
		} catch (CASException e1) {
			throw new AnalysisEngineProcessException(e1);
		}

	}
}
