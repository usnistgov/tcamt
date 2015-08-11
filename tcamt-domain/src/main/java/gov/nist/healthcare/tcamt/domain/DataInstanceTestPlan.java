package gov.nist.healthcare.tcamt.domain;

import java.io.Serializable;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table
public class DataInstanceTestPlan implements Cloneable, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8324105895492403037L;

	@Id
    @GeneratedValue
    @JsonIgnore
	private long id;
	
	private String name;
	
	@Column(columnDefinition="longtext")
	@JsonProperty("description")
	private String longDescription;
	
	@JsonIgnore
	private String lastUpdateDate;
	
	@JsonIgnore
	private Integer version;
	
	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval=true)
    @JoinTable(name = "ditp_ditc", joinColumns = {@JoinColumn(name="testplan_id")}, inverseJoinColumns = {@JoinColumn(name="testcase_id")} )
	private Set<DataInstanceTestCase> testcases = new HashSet<DataInstanceTestCase>();
	
	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval=true)
    @JoinTable(name = "ditp_ditcg", joinColumns = {@JoinColumn(name="testplan_id")}, inverseJoinColumns = {@JoinColumn(name="testcasegroup_id")} )
	private Set<DataInstanceTestCaseGroup> testcasegroups = new HashSet<DataInstanceTestCaseGroup>();
	
	@JsonIgnore
	@ManyToOne
    @JoinColumn(name="author_id")
	private User author;
	
	private String type;
	
	@JsonIgnore
	private boolean expanded;
	
	@JsonIgnore
	@Transient
	private boolean selected;
	
	@JsonIgnore
	@Embedded
	private Metadata metadata = new Metadata();
	
	@JsonIgnore
	@ManyToOne
    @JoinColumn(name="specific_juror_document_id")
	private JurorDocument specificJurorDocument;
	
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
		this.metadata.setTestSuiteName(name);
		this.name = name;
	}
	public String getLongDescription() {
		if(longDescription == null || longDescription.equals("")){
			this.longDescription = "No Description";
		}
		return longDescription;
	}
	public void setLongDescription(String longDescription) {
		this.metadata.setTestSuiteDescription(longDescription);
		this.longDescription = longDescription;
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
	
	public void addTestCase(DataInstanceTestCase testcase){
		this.testcases.add(testcase);
	}
	
	public void addTestCaseGroup(DataInstanceTestCaseGroup testcasegroup){
		this.testcasegroups.add(testcasegroup);
	}
	public Set<DataInstanceTestCase> getTestcases() {
		return testcases;
	}
	public void setTestcases(Set<DataInstanceTestCase> testcases) {
		this.testcases = testcases;
	}
	public Set<DataInstanceTestCaseGroup> getTestcasegroups() {
		return testcasegroups;
	}
	public void setTestcasegroups(Set<DataInstanceTestCaseGroup> testcasegroups) {
		this.testcasegroups = testcasegroups;
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
	
	@Override
	public DataInstanceTestPlan clone() throws CloneNotSupportedException {
		DataInstanceTestPlan cloned = (DataInstanceTestPlan)super.clone();
		cloned.setId(0);
		cloned.setVersion(0);
		
		cloned.setMetadata(this.metadata.clone());
		
		Set<DataInstanceTestCase> cTestcases = new HashSet<DataInstanceTestCase>();
		for(DataInstanceTestCase testcase:this.testcases){
			cTestcases.add(testcase.clone());
		}
		cloned.setTestcases(cTestcases);
		
		Set<DataInstanceTestCaseGroup> ctestcasegroups = new HashSet<DataInstanceTestCaseGroup>();
		for(DataInstanceTestCaseGroup group:this.testcasegroups){
			ctestcasegroups.add(group.clone());
		}
		cloned.setTestcasegroups(ctestcasegroups);
		
		return cloned;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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
	public JurorDocument getSpecificJurorDocument() {
		return specificJurorDocument;
	}
	public void setSpecificJurorDocument(JurorDocument specificJurorDocument) {
		this.specificJurorDocument = specificJurorDocument;
	}
	
	
	
}
