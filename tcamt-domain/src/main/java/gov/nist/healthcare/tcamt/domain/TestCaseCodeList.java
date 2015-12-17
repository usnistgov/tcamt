package gov.nist.healthcare.tcamt.domain;

import java.io.Serializable;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table
public class TestCaseCodeList implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3620675905904568507L;

	@Id
    @GeneratedValue
	private long id;
	
	private String name;
	
	@Column(columnDefinition="longtext")
	private String description;
	
	private String lastUpdateDate;
	
	@ManyToOne
    @JoinColumn(name="author_id")
	private User author;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval=true)
    @JoinTable(name = "tccl_cl", joinColumns = {@JoinColumn(name="codelist_id")}, inverseJoinColumns = {@JoinColumn(name="code_id")} )
	private Set<TestCaseCode> codes = new HashSet<TestCaseCode>();

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

	public String getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(String lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public Set<TestCaseCode> getCodes() {
		return codes;
	}

	public void setCodes(Set<TestCaseCode> codes) {
		this.codes = codes;
	}
	
	
}
