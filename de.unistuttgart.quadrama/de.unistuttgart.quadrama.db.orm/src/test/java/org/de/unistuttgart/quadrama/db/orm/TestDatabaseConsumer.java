package org.de.unistuttgart.quadrama.db.orm;

import java.io.IOException;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.uima.UIMAException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Before;
import org.junit.Test;

import com.j256.ormlite.db.MysqlDatabaseType;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;

public class TestDatabaseConsumer {

	BasicDataSource dataSource;
	CollectionReaderDescription crd;
	String dbUrl = "jdbc:mysql://localhost/de.unistuttgart.quadrama";

	@Before
	public void setUp() throws ResourceInitializationException {
		crd =
				CollectionReaderFactory.createReaderDescription(
						XmiReader.class, XmiReader.PARAM_SOURCE_LOCATION,
						"src/test/resources/*.xmi");

	}

	@Test
	public void testDatabaseConsumer() throws ResourceInitializationException,
	UIMAException, IOException {
		SimplePipeline.runPipeline(crd, AnalysisEngineFactory
				.createEngineDescription(DatabaseConsumer.class,
						DatabaseConsumer.PARAM_DB_URL, dbUrl,
						DatabaseConsumer.PARAM_DB_PASSWORD, "",
						DatabaseConsumer.PARAM_DB_USERNAME, "root",
						DatabaseConsumer.PARAM_DATABASETYPE,
						MysqlDatabaseType.class));
	}
}
