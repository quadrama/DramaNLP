package org.de.unistuttgart.quadrama.db.orm.beans;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class DBFigure {
	@DatabaseField(generatedId = true)
	int id;

	@DatabaseField
	String name;

	@DatabaseField(foreign = true)
	DBDrama drama;

	@DatabaseField(foreign = true)
	DBFigureType type;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DBDrama getDrama() {
		return drama;
	}

	public void setDrama(DBDrama drama) {
		this.drama = drama;
	}

	public DBFigureType getType() {
		return type;
	}

	public void setType(DBFigureType type) {
		this.type = type;
	}
}
