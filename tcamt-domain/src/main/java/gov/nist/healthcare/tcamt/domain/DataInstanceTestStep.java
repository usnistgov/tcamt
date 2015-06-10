package gov.nist.healthcare.tcamt.domain;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table
public class DataInstanceTestStep implements Cloneable, Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5928301465572845004L;
	
	
	@Id
    @GeneratedValue
	private long id;
	private String name;
	private String description;
	
	@OneToOne(fetch=FetchType.EAGER, cascade = {CascadeType.ALL}, orphanRemoval=true)
    @JoinColumn(name="message_id")
	private Message message;
	private Integer version;
	
	@Embedded
	private TestStory testStepStory = new TestStory();
	
	public DataInstanceTestStep(long id, String name, String description, Integer version) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.setVersion(version);
	}

	public DataInstanceTestStep() {
		super();
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
	public DataInstanceTestStep clone() throws CloneNotSupportedException {
		DataInstanceTestStep cloned = (DataInstanceTestStep)super.clone();
		cloned.setId(0);
		if(this.message == null){
			cloned.setMessage(null);
		}else{
			Message cMessage = this.message.clone();
			cloned.setMessage(cMessage);
		}
		cloned.setTestStepStory((TestStory)testStepStory.clone());
		
		return cloned;
	}
}
