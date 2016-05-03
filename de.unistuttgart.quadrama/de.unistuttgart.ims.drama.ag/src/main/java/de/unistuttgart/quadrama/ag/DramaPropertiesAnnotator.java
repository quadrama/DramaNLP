package de.unistuttgart.quadrama.ag;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.unistuttgart.ims.drama.api.Drama;
import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.ims.drama.api.ag.FigureProperties;

public class DramaPropertiesAnnotator extends JCasAnnotator_ImplBase {
	public static final String PARAM_PROPERTIES_FILE = "Prop File";

	@ConfigurationParameter(name = PARAM_PROPERTIES_FILE)
	String propertiesFilename;

	Map<String, Map<String, CSVRecord>> map =
			new HashMap<String, Map<String, CSVRecord>>();

	@Override
	public void initialize(final UimaContext context)
			throws ResourceInitializationException {
		super.initialize(context);
		CSVParser p = null;

		try {
			p =
					new CSVParser(new FileReader(new File(propertiesFilename)),
							CSVFormat.EXCEL.withHeader().withIgnoreEmptyLines());
			Iterator<CSVRecord> iterator = p.iterator();
			while (iterator.hasNext()) {
				CSVRecord rec = iterator.next();
				if (!map.containsKey(rec.get(0))) {
					map.put(rec.get(0), new HashMap<String, CSVRecord>());
				}
				map.get(rec.get(0)).put(rec.get(1), rec);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ResourceInitializationException(e);
		} finally {
			IOUtils.closeQuietly(p);
		}

	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		String tgId = JCasUtil.selectSingle(jcas, Drama.class).getDocumentId();
		if (map.containsKey(tgId)) {
			for (Figure figure : JCasUtil.select(jcas, Figure.class)) {
				String ref = figure.getReference();

				CSVRecord rec = map.get(tgId).get(ref);
				if (rec != null) {
					FigureProperties fp =
							AnnotationFactory.createAnnotation(jcas,
									figure.getBegin(), figure.getEnd(),
									FigureProperties.class);
					fp.setFigure(figure);
					fp.setGender(rec.get(4));
					fp.setPolarity(rec.get(3));
					fp.setSocialClass(rec.get(5));
					fp.setFigureType(rec.get(2));
				}
			}
		}
	}
}
