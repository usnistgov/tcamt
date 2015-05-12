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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table
public class IsolatedTestPlan implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8324105895492403037L;

	@Id
    @GeneratedValue
	private long id;
	private String name;
	private String description;
	private Integer version;
	
	@OneToMany(fetch=FetchType.EAGER, cascade = {CascadeType.ALL})
    @JoinTable(name = "itp_itc", joinColumns = {@JoinColumn(name="testplan_id")}, inverseJoinColumns = {@JoinColumn(name="testcase_id")} )
	private Set<IsolatedTestCase> testcases = new HashSet<IsolatedTestCase>();
	
	@OneToMany(fetch=FetchType.EAGER, cascade = {CascadeType.ALL})
    @JoinTable(name = "itp_itcg", joinColumns = {@JoinColumn(name="testplan_id")}, inverseJoinColumns = {@JoinColumn(name="testcasegroup_id")} )
	private Set<IsolatedTestCaseGroup> testcasegroups = new HashSet<IsolatedTestCaseGroup>();
	
	@ManyToOne
    @JoinColumn(name="author_id")
	private User author;
	
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
	
	public User getAuthor() {
		return author;
	}
	public void setAuthor(User author) {
		this.author = author;
	}
	
	public void addTestCase(IsolatedTestCase testcase){
		this.testcases.add(testcase);
	}
	
	public void addTestCaseGroup(IsolatedTestCaseGroup testcasegroup){
		this.testcasegroups.add(testcasegroup);
	}
	public Set<IsolatedTestCase> getTestcases() {
		return testcases;
	}
	public void setTestcases(Set<IsolatedTestCase> testcases) {
		this.testcases = testcases;
	}
	public Set<IsolatedTestCaseGroup> getTestcasegroups() {
		return testcasegroups;
	}
	public void setTestcasegroups(Set<IsolatedTestCaseGroup> testcasegroups) {
		this.testcasegroups = testcasegroups;
	}

	
	
	
	
}
