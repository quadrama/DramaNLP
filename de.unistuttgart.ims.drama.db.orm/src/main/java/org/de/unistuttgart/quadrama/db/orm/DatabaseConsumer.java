package org.de.unistuttgart.quadrama.db.orm;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.fit.component.JCasConsumer_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.de.unistuttgart.quadrama.db.orm.beans.DBAuthor;
import org.de.unistuttgart.quadrama.db.orm.beans.DBDrama;
import org.de.unistuttgart.quadrama.db.orm.beans.DBFigure;
import org.de.unistuttgart.quadrama.db.orm.beans.DBFigureRelation;
import org.de.unistuttgart.quadrama.db.orm.beans.DBFigureType;
import org.de.unistuttgart.quadrama.db.orm.beans.DBPublisher;
import org.de.unistuttgart.quadrama.db.orm.beans.DBRelation;
import org.xml.sax.SAXException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.db.BaseDatabaseType;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import de.unistuttgart.ims.drama.api.Author;
import de.unistuttgart.ims.drama.api.Drama;
import de.unistuttgart.ims.drama.api.Figure;

public class DatabaseConsumer extends JCasConsumer_ImplBase {
	public static final String PARAM_DB_URL = "Database URL";
	public static final String PARAM_DATABASETYPE = "Database type";
	public static final String PARAM_DB_USERNAME = "Database Username";
	public static final String PARAM_DB_PASSWORD = "Database Password";

	@ConfigurationParameter(name = PARAM_DATABASETYPE)
	Class<? extends BaseDatabaseType> dbType;

	@ConfigurationParameter(name = PARAM_DB_URL, mandatory = true)
	String databaseUrl;

	@ConfigurationParameter(name = PARAM_DB_PASSWORD, mandatory = true)
	String databasePassword;

	@ConfigurationParameter(name = PARAM_DB_USERNAME, mandatory = true)
	String databaseUsername;

	ConnectionSource connectionSource;

	@Override
	public void initialize(final UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		try {
			BasicDataSource ds = new BasicDataSource();
			ds.setUrl(databaseUrl);
			ds.setUsername(databaseUsername);
			ds.setPassword(databasePassword);
			ds.getConnection().createStatement().execute("SELECT NOW();");
			connectionSource = new DataSourceConnectionSource(ds, databaseUrl);

			TableUtils.createTableIfNotExists(connectionSource, DBDrama.class);
			TableUtils.createTableIfNotExists(connectionSource, DBFigure.class);
			TableUtils.createTableIfNotExists(connectionSource, DBFigureType.class);
			TableUtils.createTableIfNotExists(connectionSource, DBAuthor.class);
			TableUtils.createTableIfNotExists(connectionSource, DBPublisher.class);
			TableUtils.createTableIfNotExists(connectionSource, DBRelation.class);
			TableUtils.createTableIfNotExists(connectionSource, DBFigureRelation.class);

		} catch (SQLException e) {
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			XmiCasSerializer.serialize(jcas.getCas(), os);
			String xmi = os.toString("UTF-8");
			Dao<DBAuthor, Integer> authorDao = DaoManager.createDao(connectionSource, DBAuthor.class);
			Dao<DBDrama, Integer> dramaDao = DaoManager.createDao(connectionSource, DBDrama.class);
			Dao<DBFigure, Integer> figureDao = DaoManager.createDao(connectionSource, DBFigure.class);

			Drama drama = JCasUtil.selectSingle(jcas, Drama.class);

			DBDrama dbDrama = new DBDrama();
			dbDrama.setXmi(xmi);
			dbDrama.setTitle(drama.getDocumentTitle());
			dbDrama.setTextgridUrl(drama.getDocumentUri());
			dbDrama.setDocumentId(drama.getDocumentId());

			Collection<Author> authors = JCasUtil.select(jcas, Author.class);
			List<DBAuthor> auths = null;
			DBAuthor dbAuthor;
			for (Author author : authors) {
				if (author.getPnd() != null) {
					auths = authorDao.queryForEq("pnd", author.getPnd());
				} else if (author.getName() != null) {
					auths = authorDao.queryForEq("name", author.getName());
				}
				if (auths.isEmpty()) {
					dbAuthor = new DBAuthor();
					dbAuthor.setPnd(author.getPnd());
					dbAuthor.setName(author.getName());
					authorDao.create(dbAuthor);
				} else
					dbAuthor = auths.get(0);
				dbDrama.setAuthor(dbAuthor);
			}

			dramaDao.create(dbDrama);

			for (Figure figure : JCasUtil.select(jcas, Figure.class)) {
				DBFigure dbf = new DBFigure();
				dbf.setDrama(dbDrama);
				dbf.setName(figure.getCoveredText());
				figureDao.create(dbf);
				figure.setId(dbf.getId());
			}

		} catch (SQLException e) {
			throw new AnalysisEngineProcessException(e);
		} catch (UnsupportedEncodingException e) {
			throw new AnalysisEngineProcessException(e);
		} catch (SAXException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}
}
