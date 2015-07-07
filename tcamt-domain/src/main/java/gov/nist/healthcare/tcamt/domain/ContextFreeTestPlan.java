package gov.nist.healthcare.tcamt.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table
public class ContextFreeTestPlan implements Cloneable, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8324105895492403073L;

	@JsonIgnore
	@Id
    @GeneratedValue
	private long id;
	
	private String name;
	private String description;
	
	
	@JsonIgnore
	@Embedded
	private Metadata metadata = new Metadata();
	
	@JsonIgnore
	private String lastUpdateDate;
	
	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval=true)
    @JoinTable(name = "cftp_to", joinColumns = {@JoinColumn(name="testplan_id")}, inverseJoinColumns = {@JoinColumn(name="testobject_id")} )
	private Set<TestObject> testObjects = new HashSet<TestObject>();
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Metadata getMetadata() {
		return metadata;
	}

	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

	public String getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(String lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public Set<TestObject> getTestObjects() {
		return testObjects;
	}

	public void setTestObjects(Set<TestObject> testObjects) {
		this.testObjects = testObjects;
	}

	@Override
	public ContextFreeTestPlan clone() throws CloneNotSupportedException {
		ContextFreeTestPlan cloned = (ContextFreeTestPlan)super.clone();
		cloned.setId(0);
		
		cloned.setMetadata(this.metadata.clone());
		cloned.setTestObjects(testObjects);
		
		Set<TestObject> cTestObjects = new HashSet<TestObject>();
		for(TestObject o:this.testObjects){
			cTestObjects.add(o.clone());
		}
		cloned.setTestObjects(cTestObjects);
		
		return cloned;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
