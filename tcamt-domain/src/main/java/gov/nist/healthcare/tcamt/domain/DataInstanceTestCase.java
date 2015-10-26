package gov.nist.healthcare.tcamt.domain;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table
public class DataInstanceTestCase implements Cloneable, Serializable, Comparable<DataInstanceTestCase>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8805967508478985159L;
	
	@JsonIgnore
	@Id
    @GeneratedValue
	private long id;
	
	private String name;
	
	@JsonProperty("description")
	@Column(columnDefinition="longtext")
	private String longDescription;
	
	@JsonIgnore
	private Integer version;
	
	private int position;
	
	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval=true)
    @JoinTable(name = "ditc_dits", joinColumns = {@JoinColumn(name="testcase_id")}, inverseJoinColumns = {@JoinColumn(name="teststep_id")} )
	private Set<DataInstanceTestStep> teststeps = new HashSet<DataInstanceTestStep>();
	
	
	@JsonIgnore
	@Embedded
	private TestStory testCaseStory = new TestStory();
	
	@JsonIgnore
	private boolean expanded;
	
	@JsonIgnore
	@Transient
	private boolean selected;
	
	@JsonIgnore
	@Transient
	private boolean changed;
	
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
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	
	
	public Set<DataInstanceTestStep> getTeststeps() {
		return teststeps;
	}
	public void setTeststeps(Set<DataInstanceTestStep> teststeps) {
		this.teststeps = teststeps;
	}
	public TestStory getTestCaseStory() {
		testCaseStory = testCaseStory.normalize();
		return testCaseStory;
	}
	public void setTestCaseStory(TestStory testCaseStory) {
		this.testCaseStory = testCaseStory;
	}
	
	public void addTestStep(DataInstanceTestStep teststep){
		this.teststeps.add(teststep);
	} 
	
	@Override
	public DataInstanceTestCase clone() throws CloneNotSupportedException {
		DataInstanceTestCase cloned = (DataInstanceTestCase)super.clone();
		cloned.setId(0);
		
		Set<DataInstanceTestStep> cTeststeps = new HashSet<DataInstanceTestStep>();
		for(DataInstanceTestStep teststep:this.teststeps){
			cTeststeps.add(teststep.clone());
		}
		cloned.setTeststeps(cTeststeps);
		cloned.setTestCaseStory((TestStory)testCaseStory.clone());
		
		return cloned;
	}
	
	public int getPosition() {
		return position;
	}
	
	public void setPosition(int position) {
		this.position = position;
	}
	
	public int compareTo(DataInstanceTestCase comparingTestCase) {
		int comparePosition = comparingTestCase.getPosition(); 
		return this.position - comparePosition;
	}

	public boolean isExpanded() {
		return expanded;
	}
	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public boolean isChanged() {
		return changed;
	}
	public void setChanged(boolean changed) {
		this.changed = changed;
	}
	public static Comparator<DataInstanceTestCase> getTestCasePositionComparator() {
		return testCasePositionComparator;
	}
	public static void setTestCasePositionComparator(
			Comparator<DataInstanceTestCase> testCasePositionComparator) {
		DataInstanceTestCase.testCasePositionComparator = testCasePositionComparator;
	}

	public static Comparator<DataInstanceTestCase> testCasePositionComparator = new Comparator<DataInstanceTestCase>() {
		public int compare(DataInstanceTestCase tc1, DataInstanceTestCase tc2) {
			return tc1.compareTo(tc2);
		}
	};
}
