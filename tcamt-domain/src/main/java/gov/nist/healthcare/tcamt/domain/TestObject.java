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
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table
public class TestObject implements Cloneable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2555650104975908782L;

	@JsonIgnore
	@Id
	@GeneratedValue
	private long id;
	
	private String name;
	
	private String messageId;
	private String constraintId;
	private String valueSetLibraryId;
	
	@JsonProperty("description")
	@Column(columnDefinition="longtext")
	private String longDescription;
	
	private int position;
	
	@JsonIgnore
	@ManyToOne
    @JoinColumn(name="conformance_profile_id")
	private ConformanceProfile conformanceProfile;

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
		if(longDescription == null || longDescription.equals("")){
			this.longDescription = "No Description";
		}
		return longDescription;
	}

	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

	public ConformanceProfile getConformanceProfile() {
		return conformanceProfile;
	}

	public void setConformanceProfile(ConformanceProfile conformanceProfile) {
		this.conformanceProfile = conformanceProfile;
	}

	
	@Override
	public TestObject clone() throws CloneNotSupportedException {
		TestObject cloned = (TestObject)super.clone();
		cloned.setId(0);
		cloned.setLongDescription(longDescription);
		cloned.setName(name);
		cloned.setConformanceProfile(conformanceProfile);
		return cloned;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getConstraintId() {
		return constraintId;
	}

	public void setConstraintId(String constraintId) {
		this.constraintId = constraintId;
	}

	public String getValueSetLibraryId() {
		return valueSetLibraryId;
	}

	public void setValueSetLibraryId(String valueSetLibraryId) {
		this.valueSetLibraryId = valueSetLibraryId;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
	
	

}
