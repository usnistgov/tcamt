package gov.nist.healthcare.tcamt.domain;

import java.io.Serializable;
import java.util.Comparator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
public class DataInstanceTestStep implements Cloneable, Serializable, Comparable<DataInstanceTestStep>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5928301465572845004L;
	
	
	@Id
    @GeneratedValue
	private long id;
	private String name;
	@Column(columnDefinition="longtext")
	private String longDescription;
	
	@OneToOne(fetch=FetchType.EAGER, cascade = {CascadeType.ALL}, orphanRemoval=true)
    @JoinColumn(name="message_id")
	private Message message;
	private Integer version;
	private int position;
	
	@Embedded
	private TestStory testStepStory = new TestStory();
	
	public DataInstanceTestStep(long id, String name, String longDescription, Integer version) {
		super();
		this.id = id;
		this.name = name;
		this.longDescription = longDescription;
		this.setVersion(version);
	}

	public DataInstanceTestStep() {
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

	public String getLongDescription() {
		if(longDescription == null || longDescription.equals("")){
			this.longDescription = "No Description";
		}
		return longDescription;
	}

	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
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

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
	
	public int compareTo(DataInstanceTestStep comparingTestStep) {
		int comparePosition = comparingTestStep.getPosition(); 
		return this.position - comparePosition;
	}
	
	public static Comparator<DataInstanceTestStep> testCasePositionComparator = new Comparator<DataInstanceTestStep>() {
		public int compare(DataInstanceTestStep ts1, DataInstanceTestStep ts2) {
			return ts1.compareTo(ts2);
		}
	};
}
