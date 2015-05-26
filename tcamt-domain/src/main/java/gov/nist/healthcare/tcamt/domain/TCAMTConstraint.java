package gov.nist.healthcare.tcamt.domain;

import gov.nist.healthcare.tcamt.domain.data.TestDataCategorization;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
	
	private String messageName;
	private String usageList;
	private String ipath;
	private String iPosition;
	@Column(columnDefinition="longtext")
	private String comments;
	private String level;
	private TestDataCategorization categorization;
	private String data;
	@Column(columnDefinition="longtext")
	private String assertionScript;
	
	
	public TCAMTConstraint(String messageName, String usageList, String ipath, String iPosition, String comments,
			String level, TestDataCategorization categorization, String data,
			String assertionScript) {
		super();
		this.messageName = messageName;
		this.usageList = usageList;
		this.iPosition = iPosition;
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
		TCAMTConstraint cTCAMTConstraint = (TCAMTConstraint)super.clone();
		cTCAMTConstraint.setId(0);
		return cTCAMTConstraint;
	}

	public String getMessageName() {
		return messageName;
	}

	public void setMessageName(String messageName) {
		this.messageName = messageName;
	}

	public String getUsageList() {
		return usageList;
	}

	public void setUsageList(String usageList) {
		this.usageList = usageList;
	}

	public String getiPosition() {
		return iPosition;
	}

	public void setiPosition(String iPosition) {
		this.iPosition = iPosition;
	}
	
	public List<String> getTestConstraints(){
		List<String> result = new ArrayList<String>();
		result.add("AAAAA");
		result.add("BBBBB");
		return result;
		
	}
	
}
