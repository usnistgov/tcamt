package gov.nist.healthcare.tcamt.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TestPlan implements Cloneable, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2395022982324000085L;
	private Integer id;
	private String name;
	private String description;
	private Integer version;
	private List<Integer> testCases;

	public TestPlan() {
		super();
		this.testCases = new ArrayList<Integer>();
	}

	public TestPlan(Integer id, String name, String description,
			Integer version, List<Integer> testCases) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.version = version;
		this.testCases = testCases;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
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

	public List<Integer> getTestCases() {
		return testCases;
	}

	public void setTestCases(List<Integer> testCases) {
		this.testCases = testCases;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		TestPlan cloned = (TestPlan)super.clone();
		List<Integer> cTestCases = new ArrayList<Integer>();
		for(Integer tcid:this.testCases){
			cTestCases.add(tcid);
		}
		cloned.setTestCases(cTestCases);
		return cloned;
	}

	@Override
	public String toString() {
		return "TestPlan [id=" + id + ", name=" + name + ", description="
				+ description + ", version=" + version + ", testCases="
				+ testCases + "]";
	}

}
