package de.unistuttgart.quadrama.io.html;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import de.unistuttgart.ims.drama.api.Author;
import de.unistuttgart.ims.drama.api.Drama;
import de.unistuttgart.ims.drama.api.Field;
import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.ims.drama.api.Scene;
import de.unistuttgart.ims.drama.api.SceneHeading;
import de.unistuttgart.ims.drama.api.Speech;
import de.unistuttgart.ims.drama.api.Translator;
import de.unistuttgart.ims.drama.api.Utterance;
import de.unistuttgart.ims.drama.util.DramaUtil;
import de.unistuttgart.ims.uimautil.WordListDescription;

public class JsonExporter extends JCasFileWriter_ImplBase {

	public static final String PARAM_JAVASCRIPT = "Javascript declaration";

	@ConfigurationParameter(name = PARAM_JAVASCRIPT, mandatory = false)
	String javascriptVariableName = null;

	static boolean includeType = false;

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		List<Figure> figureList = new ArrayList<Figure>();
		Map<Figure, JSONObject> figureObjects = new HashMap<Figure, JSONObject>();

		JSONObject json = new JSONObject();
		JSONObject md = new JSONObject();

		// meta data and author
		Drama drama = JCasUtil.selectSingle(aJCas, Drama.class);
		md.put("documentId", drama.getDocumentId());
		md.put("documentTitle", drama.getDocumentTitle());

		for (Author author : JCasUtil.select(aJCas, Author.class))
			md.append("authors", convert(author, false));
		for (Translator translator : JCasUtil.select(aJCas, Translator.class))
			md.append("translators", convert(translator, false));

		// figures
		for (Figure figure : JCasUtil.select(aJCas, Figure.class)) {
			JSONObject fObj = convert(figure, true);
			json.append("figures", fObj);
			figureList.add(figure);
			figureObjects.put(figure, fObj);
		}

		// segments
		for (Act act : JCasUtil.select(aJCas, Act.class)) {
			List<ActHeading> ahl = JCasUtil.selectCovered(ActHeading.class, act);
			if (ahl.isEmpty()) {
				json.append("acts", convert(act, false));
			} else {
				json.append("acts", convert(act, false).put("head", ahl.get(0).getCoveredText()));
			}
			for (Scene scene : JCasUtil.selectCovered(Scene.class, act)) {
				List<SceneHeading> shl = JCasUtil.selectCovered(SceneHeading.class, scene);
				if (shl.isEmpty()) {
					json.append("scs", convert(scene, false));
				} else {
					json.append("scs", convert(scene, false).put("head", shl.get(0).getCoveredText()));
				}
			}
		}
		// fields
		JSONObject fieldsObject = new JSONObject();

		for (WordListDescription f : JCasUtil.select(aJCas, WordListDescription.class)) {
			fieldsObject.put(f.getName(), convert(f, false));
		}
		json.put("fields", fieldsObject);

		// utterances
		for (Utterance utterance : JCasUtil.select(aJCas, Utterance.class)) {
			Figure f = DramaUtil.getFigure(utterance);
			if (f == null)
				continue;
			int figureIndex = figureList.indexOf(f);
			JSONObject obj = new JSONObject();
			obj.put("f", figureIndex);
			obj.put("begin", utterance.getBegin());
			obj.put("end", utterance.getEnd());
			for (Speech speech : JCasUtil.selectCovered(Speech.class, utterance)) {
				JSONObject sObj = convert(speech, true);
				for (Field field : JCasUtil.selectCovered(Field.class, speech)) {
					sObj.append("fields", field.getName());
				}
				obj.append("s", sObj);
			}
			json.append("utt", obj);
			figureObjects.get(f).append("utt", (json.getJSONArray("utt").length() - 1));
		}

		// assembly
		json.put("meta", md);
		OutputStream os = null;
		OutputStreamWriter osw;
		try {
			os = this.getOutputStream(aJCas, (javascriptVariableName != null ? ".js" : ".json"));
			osw = new OutputStreamWriter(os);
			if (javascriptVariableName != null) {
				osw.write("var ");
				osw.write(javascriptVariableName);
				osw.write(" = ");
			}
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
			object.put("txt", annotation.getCoveredText());
		if (includeType)
			object.put("type", annotation.getType().getName());
		for (Feature feature : annotation.getType().getFeatures()) {
			if (feature.getRange().isPrimitive()) {
				object.put(feature.getShortName(), annotation.getFeatureValueAsString(feature));
			}
		}

		return object;
	}
}
