package gov.nist.healthcare.tcamt.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TestCase implements Cloneable, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8805967508478985159L;
	private Integer id;
	private String name;
	private String description;
	private String type;
	private Integer version;
	private String umld;
	private List<Integer> testSteps;
	private List<String> transactions;
	private TestStory testCaseStory;
	private List<Actor> listActors;
	private Integer sutActorId;
	private String testScenario;

	public TestCase() {
		super();
		this.testSteps = new ArrayList<Integer>();
		this.transactions = new ArrayList<String>();
		this.listActors = new ArrayList<Actor>();
		this.testCaseStory = new TestStory();
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

	public List<Actor> getListActors() {
		return listActors;
	}

	public void setListActors(List<Actor> listActors) {
		this.listActors = listActors;
	}
	
	public void addActor(Actor actor){
		boolean existActor = false;
		for(Actor a: this.listActors){
			if(a.getId().equals(actor.getId())) existActor = true;
		}
		if(!existActor) this.listActors.add(actor);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public TestStory getTestCaseStory() {
		return testCaseStory;
	}

	public void setTestCaseStory(TestStory testCaseStory) {
		this.testCaseStory = testCaseStory;
	}

	public List<Integer> getTestSteps() {
		return testSteps;
	}

	public void setTestSteps(List<Integer> testSteps) {
		this.testSteps = testSteps;
	}

	public List<String> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<String> transactions) {
		this.transactions = transactions;
	}

	
	@Override
	public Object clone() throws CloneNotSupportedException {
		TestCase cloned = (TestCase)super.clone();
		List<Integer> cTestSteps = new ArrayList<Integer>();
		
		for(Integer tsid:this.testSteps){
			cTestSteps.add(tsid);
		}
		
		cloned.setTestSteps(cTestSteps);
		
		List<String> cTransactions = new ArrayList<String>();
		
		for(String tscName:this.transactions){
			cTransactions.add(tscName);
		}
		
		cloned.setTransactions(cTransactions);
		
		List<Actor> cActors = new ArrayList<Actor>();
		
		for(Actor a:this.listActors){
			cActors.add((Actor)a.clone());
		}
		
		cloned.setListActors(cActors);
		
		cloned.setTestCaseStory((TestStory)this.testCaseStory.clone());

		return cloned;
	}

	public Integer getSutActorId() {
		return sutActorId;
	}

	public void setSutActorId(Integer sutActorId) {
		this.sutActorId = sutActorId;
	}

	public String getTestScenario() {
		return testScenario;
	}

	public void setTestScenario(String testScenario) {
		this.testScenario = testScenario;
	}

}
