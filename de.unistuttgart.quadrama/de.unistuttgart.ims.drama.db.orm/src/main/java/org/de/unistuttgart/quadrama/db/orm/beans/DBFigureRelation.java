package org.de.unistuttgart.quadrama.db.orm.beans;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class DBFigureRelation {
	@DatabaseField(generatedId = true)
	int id;

	@DatabaseField(foreign = true)
	DBFigure figure1;

	@DatabaseField(foreign = true)
	DBFigure figure2;

	@DatabaseField(foreign = true)
	DBRelation relation;
}
