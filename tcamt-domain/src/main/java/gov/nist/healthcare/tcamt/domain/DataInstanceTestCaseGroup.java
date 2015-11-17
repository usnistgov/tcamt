package gov.nist.healthcare.tcamt.domain;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
public class DataInstanceTestCaseGroup implements Cloneable, Comparable<DataInstanceTestCaseGroup> {

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
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	@JoinTable(name = "ditcg_ditc", joinColumns = { @JoinColumn(name = "testcasegroup_id") }, inverseJoinColumns = { @JoinColumn(name = "testcase_id") })
	private Set<DataInstanceTestCase> testcases = new HashSet<DataInstanceTestCase>();
	
	
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

	public Set<DataInstanceTestCase> getTestcases() {
		return testcases;
	}

	public void setTestcases(Set<DataInstanceTestCase> testcases) {
		this.testcases = testcases;
	}

	public void addTestCase(DataInstanceTestCase testcase) {
		this.testcases.add(testcase);
	}
	
	@Override
	public DataInstanceTestCaseGroup clone() throws CloneNotSupportedException {
		DataInstanceTestCaseGroup cloned = (DataInstanceTestCaseGroup)super.clone();
		cloned.setId(0);
		
		Set<DataInstanceTestCase> cTestcases = new HashSet<DataInstanceTestCase>();
		for(DataInstanceTestCase testcase:this.testcases){
			cTestcases.add(testcase.clone());
		}
		cloned.setTestcases(cTestcases);
		
		return cloned;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int compareTo(DataInstanceTestCaseGroup comparingTestCaseGroup) {
		int comparePosition = comparingTestCaseGroup.getPosition(); 
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

	public static Comparator<DataInstanceTestCaseGroup> getTestCaseGroupPositionComparator() {
		return testCaseGroupPositionComparator;
	}

	public static void setTestCaseGroupPositionComparator(
			Comparator<DataInstanceTestCaseGroup> testCaseGroupPositionComparator) {
		DataInstanceTestCaseGroup.testCaseGroupPositionComparator = testCaseGroupPositionComparator;
	}



	public static Comparator<DataInstanceTestCaseGroup> testCaseGroupPositionComparator = new Comparator<DataInstanceTestCaseGroup>() {
		public int compare(DataInstanceTestCaseGroup tg1, DataInstanceTestCaseGroup tg2) {
			return tg1.compareTo(tg2);
		}
	};
}
