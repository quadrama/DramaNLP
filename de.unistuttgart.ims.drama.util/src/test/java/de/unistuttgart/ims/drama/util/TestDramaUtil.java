package de.unistuttgart.ims.drama.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Iterator;

import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import de.unistuttgart.ims.drama.api.Author;
import de.unistuttgart.ims.drama.api.Drama;
import de.unistuttgart.ims.drama.api.Figure;
import de.unistuttgart.ims.drama.api.FigureType;
import de.unistuttgart.ims.drama.api.Speaker;
import de.unistuttgart.ims.drama.api.Speech;
import de.unistuttgart.ims.drama.api.Utterance;

public class TestDramaUtil {

	@SuppressWarnings("unused")
	@Test
	public void testGetSpeeches() throws Exception {
		JCas jcas = JCasFactory.createJCas();
		jcas.setDocumentText("Lorem ipsum dolor sit amet, consetetur sadipscing");

		assertEquals(0, DramaUtil.getSpeeches(jcas, null).size());

		Figure[] figure = new Figure[] { AnnotationFactory.createAnnotation(jcas, 0, 0, Figure.class),
				AnnotationFactory.createAnnotation(jcas, 0, 0, Figure.class) };
		assertEquals(0, DramaUtil.getSpeeches(jcas, figure[0]).size());
		assertEquals(0, DramaUtil.getSpeeches(jcas, figure[1]).size());

		Utterance[] utterances = new Utterance[] { AnnotationFactory.createAnnotation(jcas, 0, 5, Utterance.class),
				AnnotationFactory.createAnnotation(jcas, 6, 10, Utterance.class),
				AnnotationFactory.createAnnotation(jcas, 11, 15, Utterance.class) };
		assertEquals(0, DramaUtil.getSpeeches(jcas, figure[0]).size());
		assertEquals(0, DramaUtil.getSpeeches(jcas, figure[1]).size());
		Speaker[] speaker = new Speaker[] { AnnotationFactory.createAnnotation(jcas, 0, 1, Speaker.class),
				AnnotationFactory.createAnnotation(jcas, 6, 7, Speaker.class),
				AnnotationFactory.createAnnotation(jcas, 11, 12, Speaker.class), };
		assertEquals(0, DramaUtil.getSpeeches(jcas, figure[0]).size());
		assertEquals(0, DramaUtil.getSpeeches(jcas, figure[1]).size());
		Speech[] speech = new Speech[] { AnnotationFactory.createAnnotation(jcas, 1, 5, Speech.class),
				AnnotationFactory.createAnnotation(jcas, 7, 9, Speech.class),
				AnnotationFactory.createAnnotation(jcas, 9, 10, Speech.class),
				AnnotationFactory.createAnnotation(jcas, 13, 4, Speech.class) };
		speaker[0].setFigure(figure[0]);
		speaker[1].setFigure(figure[1]);
		speaker[2].setFigure(figure[0]);

		Collection<Speech> sp;
		sp = DramaUtil.getSpeeches(jcas, figure[0]);
		assertEquals(2, sp.size());

		sp = DramaUtil.getSpeeches(jcas, figure[1]);
		assertEquals(2, sp.size());

	}

	@Test
	public void TestGetFullUtterances() throws Exception {
		JCas jcas = JCasFactory.createJCas();
		jcas.setDocumentText("Lorem ipsum dolor sit amet, consetetur sadipscing");
		AnnotationFactory.createAnnotation(jcas, 0, 5, Utterance.class);
		AnnotationFactory.createAnnotation(jcas, 6, 7, Utterance.class);
		AnnotationFactory.createAnnotation(jcas, 8, 10, Utterance.class);

		Iterator<Utterance> iter = DramaUtil.selectFullUtterances(jcas).iterator();
		assertFalse(iter.hasNext());

		Speaker s1 = AnnotationFactory.createAnnotation(jcas, 0, 1, Speaker.class);
		Speaker s2 = AnnotationFactory.createAnnotation(jcas, 8, 9, Speaker.class);
		iter = DramaUtil.selectFullUtterances(jcas).iterator();
		assertFalse(iter.hasNext());

		s1.setFigure(AnnotationFactory.createAnnotation(jcas, 0, 0, Figure.class));
		s2.setFigure(AnnotationFactory.createAnnotation(jcas, 0, 0, Figure.class));
		iter = DramaUtil.selectFullUtterances(jcas).iterator();
		assertTrue(iter.hasNext());
		iter.next();
		assertTrue(iter.hasNext());
		iter.next();
		assertFalse(iter.hasNext());

	}

	@Test
	public void TestGetTypeValue() throws Exception {
		JCas jcas = JCasFactory.createJCas();
		jcas.setDocumentText("Lorem ipsum dolor sit amet, consetetur sadipscing");
		Figure[] figure = new Figure[] { AnnotationFactory.createAnnotation(jcas, 0, 5, Figure.class),
				AnnotationFactory.createAnnotation(jcas, 6, 7, Figure.class),
				AnnotationFactory.createAnnotation(jcas, 8, 10, Figure.class) };

		assertNull(DramaUtil.getTypeValue(jcas, figure[0], "Gender"));
		assertNull(DramaUtil.getTypeValue(jcas, figure[1], "Gender"));
		assertNull(DramaUtil.getTypeValue(jcas, figure[2], "Gender"));

		FigureType ft = AnnotationFactory.createAnnotation(jcas, 6, 7, FigureType.class);

		assertNull(DramaUtil.getTypeValue(jcas, figure[0], "Gender"));
		assertNull(DramaUtil.getTypeValue(jcas, figure[1], "Gender"));
		assertNull(DramaUtil.getTypeValue(jcas, figure[2], "Gender"));

		ft.setTypeClass("Gender");
		ft.setTypeValue("m");
		assertNull(DramaUtil.getTypeValue(jcas, figure[0], "Gender"));
		assertNotNull(DramaUtil.getTypeValue(jcas, figure[1], "Gender"));
		assertEquals("m", DramaUtil.getTypeValue(jcas, figure[1], "Gender"));
		assertNull(DramaUtil.getTypeValue(jcas, figure[2], "Gender"));

		ft = AnnotationFactory.createAnnotation(jcas, 6, 7, FigureType.class);
		ft.setTypeClass("Gender");
		ft.setTypeValue("f");
		assertNull(DramaUtil.getTypeValue(jcas, figure[0], "Gender"));
		assertNotNull(DramaUtil.getTypeValue(jcas, figure[1], "Gender"));
		assertEquals("m", DramaUtil.getTypeValue(jcas, figure[1], "Gender"));
		assertNull(DramaUtil.getTypeValue(jcas, figure[2], "Gender"));
	}

	@Test
	public void testGetDisplayId() throws Exception {
		JCas jcas = JCasFactory.createJCas();
		jcas.setDocumentText("Lorem ipsum dolor sit amet, consetetur sadipscing");
		Drama drama;
		drama = new Drama(jcas);
		drama.addToIndexes();
		drama.setDocumentTitle("The dog barks");
		drama.setDocumentId("test");
		DramaUtil.createFeatureStructure(jcas, Author.class).setName("No author");

		assertEquals("Na_Tdb_test", DramaUtil.getDisplayId(jcas));

		drama.setDocumentTitle("The dog barks2");
		assertEquals("Na_Tdb_test", DramaUtil.getDisplayId(jcas));

		drama.setDocumentTitle("Romeo und Julia");
		assertEquals("Na_RuJ_test", DramaUtil.getDisplayId(jcas));

		drama.setDocumentTitle("");
		assertEquals("test", DramaUtil.getDisplayId(jcas));

		drama.setDocumentTitle(null);
		assertEquals("test", DramaUtil.getDisplayId(jcas));
	}
}
