package de.unistuttgart.quadrama.io.html;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Feature;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.json.JSONObject;

import de.tudarmstadt.ukp.dkpro.core.api.io.JCasFileWriter_ImplBase;
import de.unistuttgart.ims.drama.api.Act;
import de.unistuttgart.ims.drama.api.ActHeading;
import de.unistuttgart.ims.drama.api.Drama;
import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.ims.drama.api.Scene;
import de.unistuttgart.ims.drama.api.SceneHeading;

public class JsonExporter extends JCasFileWriter_ImplBase {

	public static final String PARAM_JAVASCRIPT = "Javascript declaration";

	@ConfigurationParameter(name = PARAM_JAVASCRIPT, mandatory = false)
	String javascriptVariableName = null;

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		JSONObject json = new JSONObject();
		JSONObject md = new JSONObject();

		// meta data and author
		Drama drama = JCasUtil.selectSingle(aJCas, Drama.class);
		md.put("documentId", drama.getDocumentId());
		md.put("documentTitle", drama.getDocumentTitle());

		JSONObject author = new JSONObject();
		author.put("name", drama.getAuthorname());
		author.put("pnd", drama.getAuthorPnd());
		md.put("author", author);

		// figures
		for (Figure figure : JCasUtil.select(aJCas, Figure.class)) {
			json.append("figures", convert(figure, true));
		}

		// segments
		for (Act act : JCasUtil.select(aJCas, Act.class)) {
			List<ActHeading> ahl = JCasUtil.selectCovered(ActHeading.class, act);
			if (ahl.isEmpty()) {
				json.append("acts", convert(act, false));
			} else {
				json.append("acts", convert(act, false).put("heading", ahl.get(0).getCoveredText()));
			}
			for (Scene scene : JCasUtil.selectCovered(Scene.class, act)) {
				List<SceneHeading> shl = JCasUtil.selectCovered(SceneHeading.class, scene);
				if (shl.isEmpty()) {
					json.append("scenes", convert(scene, false));
				} else {
					json.append("scenes", convert(scene, false).put("heading", shl.get(0).getCoveredText()));
				}
			}
		}

		// assembly
		json.put("metadata", md);
		OutputStream os = null;
		OutputStreamWriter osw;
		try {
			os = this.getOutputStream(aJCas, ".json");
			osw = new OutputStreamWriter(os);
			osw.write(json.toString());
			osw.flush();
			osw.close();
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		} finally {
			IOUtils.closeQuietly(os);
		}

	}

	public static <T extends Annotation> JSONObject convert(T annotation, boolean includeText) {
		JSONObject object = new JSONObject();
		if (includeText)
			object.put("coveredText", annotation.getCoveredText());
		for (Feature feature : annotation.getType().getFeatures()) {
			if (feature.getRange().isPrimitive()) {
				object.put(feature.getShortName(), annotation.getFeatureValueAsString(feature));
			}
		}

		return object;
	}
}
