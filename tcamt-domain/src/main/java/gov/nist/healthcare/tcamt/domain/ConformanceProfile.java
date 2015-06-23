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
	private String lastUpdateDate;
	
	@ManyToOne
    @JoinColumn(name="integrated_profile_id")
	private IntegratedProfile integratedProfile;
	
	@JsonIgnore
	@Column(columnDefinition="longtext")
	private String testDataSpecificationXSLT;
	
	@JsonIgnore
	@Column(columnDefinition="longtext")
	private String jurorDocumentXSLT;

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
	
	
	
}
