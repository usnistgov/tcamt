package gov.nist.healthcare.tcamt.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table
public class IntegratedProfile implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2135862492228284533L;

	@Id
    @GeneratedValue
	private long id;
	private String profileIdentifier;
	private String name;
	private String lastUpdateDate;
	
	@Column(columnDefinition="longtext")
	private String longDescription;
	
	@JsonIgnore
	@Column(columnDefinition="longtext")
	private String profile;
	
	@JsonIgnore
	@Column(columnDefinition="longtext")
	private String constraints;
	
	@JsonIgnore 
	@Column(columnDefinition="longtext")
	private String valueSet;

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

	public String getLongDescription() {
		return longDescription;
	}

	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public String getConstraints() {
		return constraints;
	}

	public void setConstraints(String constraints) {
		this.constraints = constraints;
	}

	public String getValueSet() {
		return valueSet;
	}

	public void setValueSet(String valueSet) {
		this.valueSet = valueSet;
	}

	public String getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(String lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public String getProfileIdentifier() {
		return profileIdentifier;
	}

	public void setProfileIdentifier(String profileIdentifier) {
		this.profileIdentifier = profileIdentifier;
	}
	
	
}
