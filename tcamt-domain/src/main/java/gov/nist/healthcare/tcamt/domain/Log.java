package gov.nist.healthcare.tcamt.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class Log {

	@Id
    @GeneratedValue
	private long id;
	
	private String lastUpdateDate;
	
	@Column(columnDefinition="longtext")
	private String name;
	
	private String type;
	
	@Column(columnDefinition="longtext")
	private String description;
	
	public Log(String name, String type, String description) {
		super();
		this.name = name;
		this.type = type;
		this.description = description;
	}

	public Log() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(String lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	
}
