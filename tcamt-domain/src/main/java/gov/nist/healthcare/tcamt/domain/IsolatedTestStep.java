package gov.nist.healthcare.tcamt.domain;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table
public class IsolatedTestStep implements Cloneable, Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5928301465572845004L;
	
	
	@Id
    @GeneratedValue
	private long id;
	private String name;
	private String description;
	
	@ManyToOne
    @JoinColumn(name="s_actor_id")
	private Actor sActor;
	
	@ManyToOne
    @JoinColumn(name="r_actor_id")
	private Actor rActor;
	
	@OneToOne(fetch=FetchType.EAGER, cascade = {CascadeType.ALL}, orphanRemoval=true)
    @JoinColumn(name="message_id")
	private Message message;
	private Integer version;
	
	@Embedded
	private TestStory testStepStory = new TestStory();
	
	public IsolatedTestStep(long id, String name, String description, Integer version) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.setVersion(version);
	}

	public IsolatedTestStep() {
		super();
		this.message = new Message();
	}

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
		if(description == null || description.equals("")){
			this.description = "No Description";
		}
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

	public Actor getsActor() {
		return sActor;
	}

	public void setsActor(Actor sActor) {
		this.sActor = sActor;
	}

	public Actor getrActor() {
		return rActor;
	}

	public void setrActor(Actor rActor) {
		this.rActor = rActor;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}
	
	

	public TestStory getTestStepStory() {
		return testStepStory;
	}

	public void setTestStepStory(TestStory testStepStory) {
		this.testStepStory = testStepStory;
	}
	
	@Override
	public IsolatedTestStep clone() throws CloneNotSupportedException {
		IsolatedTestStep cloned = (IsolatedTestStep)super.clone();
		cloned.setId(0);
		cloned.setMessage(this.message.clone());
		cloned.setrActor(rActor);
		cloned.setsActor(sActor);
		cloned.setTestStepStory((TestStory)testStepStory.clone());
		
		return cloned;
	}
}
