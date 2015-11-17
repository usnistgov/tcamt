package gov.nist.healthcare.tcamt.domain;

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
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table
public class DataInstanceTestStep implements Cloneable, Comparable<DataInstanceTestStep>{
	
	@Id
    @GeneratedValue
    @JsonIgnore
	private long id;
	
	
	private String name;
	
	
	@Column(columnDefinition="longtext")
	@JsonProperty("description")
	private String longDescription;
	
	@OneToOne(fetch=FetchType.EAGER, cascade = {CascadeType.ALL}, orphanRemoval=true)
    @JoinColumn(name="message_id")
	@JsonIgnore
	private Message message;
	
	@JsonIgnore
	private Integer version;
	
	private int position;
	
	
	@Embedded
	@JsonIgnore
	private TestStory testStepStory = new TestStory();
	
	
	@Embedded
	private ProfileContainer hl7v2;
	
	private String type;
	
	@JsonIgnore
	@Transient
	private boolean selected;
	
	
	@JsonIgnore
	@Transient
	private boolean changed;
	
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
		testStepStory = testStepStory.normalize();
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
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public ProfileContainer getHl7v2() {
		return hl7v2;
	}

	public void setHl7v2(ProfileContainer hl7v2) {
		this.hl7v2 = hl7v2;
	}

	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	public static Comparator<DataInstanceTestStep> getTestCasePositionComparator() {
		return testCasePositionComparator;
	}

	public static void setTestCasePositionComparator(
			Comparator<DataInstanceTestStep> testCasePositionComparator) {
		DataInstanceTestStep.testCasePositionComparator = testCasePositionComparator;
	}

	public static Comparator<DataInstanceTestStep> testCasePositionComparator = new Comparator<DataInstanceTestStep>() {
		public int compare(DataInstanceTestStep ts1, DataInstanceTestStep ts2) {
			return ts1.compareTo(ts2);
		}
	};
}
