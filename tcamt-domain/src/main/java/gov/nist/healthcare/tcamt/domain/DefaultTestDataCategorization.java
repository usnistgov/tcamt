package gov.nist.healthcare.tcamt.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import gov.nist.healthcare.tcamt.domain.data.TestDataCategorization;

@Entity
@Table
public class DefaultTestDataCategorization implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1144934354500662296L;

	@Id
    @GeneratedValue
	private long id;
	
	private String segmentName;
	private Integer fieldPosition;
	private Integer componentPosition;
	private Integer subComponentPosition;
	private TestDataCategorization categorization;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getSegmentName() {
		return segmentName;
	}
	public void setSegmentName(String segmentName) {
		this.segmentName = segmentName;
	}
	public Integer getFieldPosition() {
		return fieldPosition;
	}
	public void setFieldPosition(Integer fieldPosition) {
		this.fieldPosition = fieldPosition;
	}
	public Integer getComponentPosition() {
		return componentPosition;
	}
	public void setComponentPosition(Integer componentPosition) {
		this.componentPosition = componentPosition;
	}
	public Integer getSubComponentPosition() {
		return subComponentPosition;
	}
	public void setSubComponentPosition(Integer subComponentPosition) {
		this.subComponentPosition = subComponentPosition;
	}
	public TestDataCategorization getCategorization() {
		return categorization;
	}
	public void setCategorization(TestDataCategorization categorization) {
		this.categorization = categorization;
	}
	
	
}
