package gov.nist.healthcare.tcamt.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class JurorDocument implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4728437163851527856L;

	@Id
    @GeneratedValue
	private long id;
	private String name;
	
	@Column(columnDefinition="longtext")
	private String jurorDocumentHTML;
	
	@Column(columnDefinition="longtext")
	private String jurorDocumentJSON;
	
	private String lastUpdateDate;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getJurorDocumentHTML() {
		return jurorDocumentHTML;
	}

	public void setJurorDocumentHTML(String jurorDocumentHTML) {
		this.jurorDocumentHTML = jurorDocumentHTML;
	}

	public String getJurorDocumentJSON() {
		return jurorDocumentJSON;
	}

	public void setJurorDocumentJSON(String jurorDocumentJSON) {
		this.jurorDocumentJSON = jurorDocumentJSON;
	}

	public String getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(String lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	
}
