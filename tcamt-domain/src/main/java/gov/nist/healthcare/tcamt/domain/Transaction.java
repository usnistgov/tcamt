package gov.nist.healthcare.tcamt.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Transaction implements Cloneable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 766770578631979162L;

	private Integer id;
	private String name;
	private String description;
	private Integer version;
	private List<Integer> testSteps;
	private String umld;

	public Transaction(Integer id, String name, String description,
			Integer version, String umld) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.setVersion(version);
		this.setTestSteps(new ArrayList<Integer>());
		this.umld = umld;
	}

	public Transaction() {
		super();
		this.setTestSteps(new ArrayList<Integer>());
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

	public String getUmld() {
		return umld;
	}

	public void setUmld(String umld) {
		this.umld = umld;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		Transaction cloned = (Transaction) super.clone();
		List<Integer> cTestSteps = new ArrayList<Integer>();

		for (Integer i : this.testSteps) {
			cTestSteps.add(i);
		}

		cloned.setTestSteps(cTestSteps);
		return cloned;
	}

	public List<Integer> getTestSteps() {
		return testSteps;
	}

	public void setTestSteps(List<Integer> testSteps) {
		this.testSteps = testSteps;
	}
}
