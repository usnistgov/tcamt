package gov.nist.healthcare.tcamt.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
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
public class IsolatedTestCaseGroup implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2555650104975908781L;
	
	@Id
    @GeneratedValue
	private long id;
	private String name;
	private String description;
	private Integer version;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval=true)
    @JoinTable(name = "itcg_itc", joinColumns = {@JoinColumn(name="testcasegroup_id")}, inverseJoinColumns = {@JoinColumn(name="testcase_id")} )
	private Set<IsolatedTestCase> testcases = new HashSet<IsolatedTestCase>();

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

	public Set<IsolatedTestCase> getTestcases() {
		return testcases;
	}

	public void setTestcases(Set<IsolatedTestCase> testcases) {
		this.testcases = testcases;
	}

	public void addTestCase(IsolatedTestCase testcase){
		this.testcases.add(testcase);
	}
	
	
}
