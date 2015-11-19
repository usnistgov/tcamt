package gov.nist.healthcare.tcamt.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embedded;
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
public class TestObject implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6761499469482867140L;

	@JsonIgnore
	@Id
	@GeneratedValue
	private long id;
	
	private String name;
	
	@Embedded
	private ProfileContainer hl7v2;

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
	
	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public ProfileContainer getHl7v2() {
		return hl7v2;
	}

	public void setHl7v2(ProfileContainer hl7v2) {
		this.hl7v2 = hl7v2;
	}
	
	

}
