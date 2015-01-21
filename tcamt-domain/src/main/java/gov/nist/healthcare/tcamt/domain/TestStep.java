package gov.nist.healthcare.tcamt.domain;

import java.io.Serializable;

public class TestStep implements Cloneable, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -340313384609724428L;
	private Integer id;
	private String name;
	private String type;
	private Interaction interaction;
	private String description;
	private TestStory testStepStory;
	private Integer version;
	private Actor targetActor;
	
	
	public TestStep() {
		super();
		this.interaction = new Interaction();
		this.testStepStory = new TestStory();
		this.targetActor = new Actor();
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Interaction getInteraction() {
		return interaction;
	}
	public void setInteraction(Interaction interaction) {
		this.interaction = interaction;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public TestStory getTestStepStory() {
		return testStepStory;
	}

	public void setTestStepStory(TestStory testStepStory) {
		this.testStepStory = testStepStory;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Actor getTargetActor() {
		return targetActor;
	}

	public void setTargetActor(Actor targetActor) {
		this.targetActor = targetActor;
	}
	
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		TestStep cloned = (TestStep)super.clone();		
		cloned.setInteraction((Interaction) this.interaction.clone());
		cloned.setTestStepStory((TestStory)this.testStepStory.clone());
		cloned.setTargetActor((Actor)this.targetActor.clone());
		return cloned;
	}
}
