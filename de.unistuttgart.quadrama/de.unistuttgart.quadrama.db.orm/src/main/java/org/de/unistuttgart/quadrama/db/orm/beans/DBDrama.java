package org.de.unistuttgart.quadrama.db.orm.beans;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class DBDrama {

	@DatabaseField(generatedId = true)
	int id;

	@DatabaseField(columnDefinition = "TEXT")
	String title;

	@DatabaseField(foreign = true)
	DBAuthor author;

	@DatabaseField(foreign = true)
	DBPublisher publisher;

	@DatabaseField(columnDefinition = "LONGTEXT")
	String xmi;

	@DatabaseField
	String textgridUrl;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public DBAuthor getAuthor() {
		return author;
	}

	public void setAuthor(DBAuthor author) {
		this.author = author;
	}

	public DBPublisher getPublisher() {
		return publisher;
	}

	public void setPublisher(DBPublisher publisher) {
		this.publisher = publisher;
	}

	public String getXmi() {
		return xmi;
	}

	public void setXmi(String xmi) {
		this.xmi = xmi;
	}

	public String getTextgridUrl() {
		return textgridUrl;
	}

	public void setTextgridUrl(String textgridUrl) {
		this.textgridUrl = textgridUrl;
	}
}
