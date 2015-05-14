package gov.nist.healthcare.tcamt.domain;

import gov.nist.healthcare.tcamt.domain.data.TestDataCategorization;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class TCAMTConstraint implements Serializable, Cloneable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3926211483462954685L;
	
	@Id
    @GeneratedValue
	private long id;
	
	private String ipath;
	@Column(columnDefinition="longblob")
	private String comments;
	private String level;
	private TestDataCategorization categorization;
	private String data;
	@Column(columnDefinition="longblob")
	private String assertionScript;
	
	
	public TCAMTConstraint(long id, String ipath, String comments,
			String level, TestDataCategorization categorization, String data,
			String assertionScript) {
		super();
		this.id = id;
		this.ipath = ipath;
		this.comments = comments;
		this.level = level;
		this.categorization = categorization;
		this.data = data;
		this.assertionScript = assertionScript;
	}
	
	public TCAMTConstraint(){
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getIpath() {
		return ipath;
	}

	public void setIpath(String ipath) {
		this.ipath = ipath;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public TestDataCategorization getCategorization() {
		return categorization;
	}

	public void setCategorization(TestDataCategorization categorization) {
		this.categorization = categorization;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getAssertionScript() {
		return assertionScript;
	}

	public void setAssertionScript(String assertionScript) {
		this.assertionScript = assertionScript;
	}
	
	@Override
	public TCAMTConstraint clone() throws CloneNotSupportedException {
		return (TCAMTConstraint)super.clone();
	}
	
}
