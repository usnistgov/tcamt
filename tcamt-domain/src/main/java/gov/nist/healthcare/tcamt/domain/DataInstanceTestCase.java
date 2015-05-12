package gov.nist.healthcare.tcamt.domain;

import java.io.Serializable;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table
public class DataInstanceTestCase implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8805967508478985159L;
	
	@Id
    @GeneratedValue
	private long id;
	private String name;
	private String description;
	private Integer version;
	
	@ManyToOne
    @JoinColumn(name="message_id")
	private Message message;
	@Embedded
	private TestStory testCaseStory = new TestStory();
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
	public TestStory getTestCaseStory() {
		return testCaseStory;
	}
	public void setTestCaseStory(TestStory testCaseStory) {
		this.testCaseStory = testCaseStory;
	}
}
