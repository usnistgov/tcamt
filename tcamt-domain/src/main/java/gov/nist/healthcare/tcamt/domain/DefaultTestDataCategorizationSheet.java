package gov.nist.healthcare.tcamt.domain;

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

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table
public class DefaultTestDataCategorizationSheet {

	@Id
    @GeneratedValue
	private long id;

	private String sheetName;
	
	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval=true)
    @JoinTable(name = "dtdcs_dtdc", joinColumns = {@JoinColumn(name="sheet_id")}, inverseJoinColumns = {@JoinColumn(name="data_id")} )
	private Set<DefaultTestDataCategorization> defaultTestDataCategorizations = new HashSet<DefaultTestDataCategorization>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public Set<DefaultTestDataCategorization> getDefaultTestDataCategorizations() {
		return defaultTestDataCategorizations;
	}

	public void setDefaultTestDataCategorizations(Set<DefaultTestDataCategorization> defaultTestDataCategorizations) {
		this.defaultTestDataCategorizations = defaultTestDataCategorizations;
	}
	
	
}
