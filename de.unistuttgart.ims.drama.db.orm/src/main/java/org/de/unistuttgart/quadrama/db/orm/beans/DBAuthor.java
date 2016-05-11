package org.de.unistuttgart.quadrama.db.orm.beans;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class DBAuthor {

	@DatabaseField(generatedId = true)
	int id;

	@DatabaseField
	String name;

	@DatabaseField
	Date birth;

	@DatabaseField
	Date death;

	@DatabaseField(unique = true)
	long pnd;

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

	public Date getBirth() {
		return birth;
	}

	public void setBirth(Date birth) {
		this.birth = birth;
	}

	public Date getDeath() {
		return death;
	}

	public void setDeath(Date death) {
		this.death = death;
	}

	public long getPnd() {
		return pnd;
	}

	public void setPnd(long pnd) {
		this.pnd = pnd;
	}
}
