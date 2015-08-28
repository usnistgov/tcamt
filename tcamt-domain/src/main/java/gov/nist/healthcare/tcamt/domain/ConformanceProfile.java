package gov.nist.healthcare.tcamt.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table
public class ConformanceProfile implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6466212787282447458L;

	@Id
    @GeneratedValue
	private long id;
	private String name;
	
	private String conformanceProfileId;
	private String valueSetLibraryId;
	private String constraintId;
	
	@Column(columnDefinition="longtext")
	private String sampleER7Message;
	
	private String lastUpdateDate;
	
	@ManyToOne
    @JoinColumn(name="integrated_profile_id")
	private IntegratedProfile integratedProfile;
	
	@JsonIgnore
	@Column(columnDefinition="longtext")
	private String messageContentXSLT;
	
	@JsonIgnore
	@Column(columnDefinition="longtext")
	private String messageContentJSONXSLT;
	
	@JsonIgnore
	@Column(columnDefinition="longtext")
	private String messageContentTabXSLT;
	
	@JsonIgnore
	@Column(columnDefinition="longtext")
	private String testDataSpecificationXSLT;
	
	@JsonIgnore
	@Column(columnDefinition="longtext")
	private String testDataSpecificationJSONXSLT;
	
	@JsonIgnore
	@Column(columnDefinition="longtext")
	private String testDataSpecificationTabXSLT;
	
	@JsonIgnore
	@Column(columnDefinition="longtext")
	private String jurorDocumentXSLT;
	
	@JsonIgnore
	@Column(columnDefinition="longtext")
	private String jurorDocumentJSONXSLT;

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

	public String getConformanceProfileId() {
		return conformanceProfileId;
	}

	public void setConformanceProfileId(String conformanceProfileId) {
		this.conformanceProfileId = conformanceProfileId;
	}

	public String getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(String lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public IntegratedProfile getIntegratedProfile() {
		return integratedProfile;
	}

	public void setIntegratedProfile(IntegratedProfile integratedProfile) {
		this.integratedProfile = integratedProfile;
	}

	public String getTestDataSpecificationXSLT() {
		return testDataSpecificationXSLT;
	}

	public void setTestDataSpecificationXSLT(String testDataSpecificationXSLT) {
		this.testDataSpecificationXSLT = testDataSpecificationXSLT;
	}

	public String getJurorDocumentXSLT() {
		return jurorDocumentXSLT;
	}

	public void setJurorDocumentXSLT(String jurorDocumentXSLT) {
		this.jurorDocumentXSLT = jurorDocumentXSLT;
	}

	public String getTestDataSpecificationJSONXSLT() {
		return testDataSpecificationJSONXSLT;
	}

	public void setTestDataSpecificationJSONXSLT(
			String testDataSpecificationJSONXSLT) {
		this.testDataSpecificationJSONXSLT = testDataSpecificationJSONXSLT;
	}

	public String getJurorDocumentJSONXSLT() {
		return jurorDocumentJSONXSLT;
	}

	public void setJurorDocumentJSONXSLT(String jurorDocumentJSONXSLT) {
		this.jurorDocumentJSONXSLT = jurorDocumentJSONXSLT;
	}

	public String getValueSetLibraryId() {
		return valueSetLibraryId;
	}

	public void setValueSetLibraryId(String valueSetLibraryId) {
		this.valueSetLibraryId = valueSetLibraryId;
	}

	public String getConstraintId() {
		return constraintId;
	}

	public void setConstraintId(String constraintId) {
		this.constraintId = constraintId;
	}

	public String getSampleER7Message() {
		return sampleER7Message;
	}

	public void setSampleER7Message(String sampleER7Message) {
		this.sampleER7Message = sampleER7Message;
	}

	public String getTestDataSpecificationTabXSLT() {
		return testDataSpecificationTabXSLT;
	}

	public void setTestDataSpecificationTabXSLT(String testDataSpecificationTabXSLT) {
		this.testDataSpecificationTabXSLT = testDataSpecificationTabXSLT;
	}

	public String getMessageContentXSLT() {
		return messageContentXSLT;
	}

	public void setMessageContentXSLT(String messageContentXSLT) {
		this.messageContentXSLT = messageContentXSLT;
	}

	public String getMessageContentJSONXSLT() {
		return messageContentJSONXSLT;
	}

	public void setMessageContentJSONXSLT(String messageContentJSONXSLT) {
		this.messageContentJSONXSLT = messageContentJSONXSLT;
	}

	public String getMessageContentTabXSLT() {
		return messageContentTabXSLT;
	}

	public void setMessageContentTabXSLT(String messageContentTabXSLT) {
		this.messageContentTabXSLT = messageContentTabXSLT;
	}
	
	
	
}
