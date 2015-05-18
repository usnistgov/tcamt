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

@Entity
@Table
public class IsolatedTestCase implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8805967508478985159L;
	
	@Id
    @GeneratedValue
	private long id;
	private String name;
	private String description;
	private Integer version;
	

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval=true)
    @JoinTable(name = "itc_its", joinColumns = {@JoinColumn(name="testcase_id")}, inverseJoinColumns = {@JoinColumn(name="teststep_id")} )
	private Set<IsolatedTestStep> teststeps = new HashSet<IsolatedTestStep>();
	
	@Embedded
	private TestStory testCaseStory = new TestStory();
	
	
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public TestStory getTestCaseStory() {
		return testCaseStory;
	}
	public void setTestCaseStory(TestStory testCaseStory) {
		this.testCaseStory = testCaseStory;
	}
	public Set<IsolatedTestStep> getTeststeps() {
		return teststeps;
	}
	public void setTeststeps(Set<IsolatedTestStep> teststeps) {
		this.teststeps = teststeps;
	}
	
	public void addTestStep(IsolatedTestStep teststep){
		this.teststeps.add(teststep);
	} 
	
}
