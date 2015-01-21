package gov.nist.healthcare.tcamt.domain;



public class TestStory implements Cloneable{
	private String description;
	private String comments;
	private String preCondition;
	private String postCondition;
	private String testObjectives;
	private String notes;
	
	public TestStory(String description, String comments, String preCondition,
			String postCondition, String testObjectives, String notes) {
		super();
		this.description = description;
		this.comments = comments;
		this.preCondition = preCondition;
		this.postCondition = postCondition;
		this.testObjectives = testObjectives;
		this.notes = notes;
	}

	public TestStory() {
		super();
		this.setComments("No Comments");
		this.setDescription(" No Description");
		this.setNotes("No Note");
		this.setPostCondition("No PostCondition");
		this.setPreCondition("No PreCondition");
		this.setTestObjectives("No Test Objectives");
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getPreCondition() {
		return preCondition;
	}

	public void setPreCondition(String preCondition) {
		this.preCondition = preCondition;
	}

	public String getPostCondition() {
		return postCondition;
	}

	public void setPostCondition(String postCondition) {
		this.postCondition = postCondition;
	}

	public String getTestObjectives() {
		return testObjectives;
	}

	public void setTestObjectives(String testObjectives) {
		this.testObjectives = testObjectives;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		TestStory cloned = (TestStory)super.clone();
		return cloned;
	}
}
