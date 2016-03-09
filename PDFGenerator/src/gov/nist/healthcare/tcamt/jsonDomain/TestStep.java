package gov.nist.healthcare.tcamt.jsonDomain;

public class TestStep {
	private String name;
	private String description;
	private String type;
	private int position;
	
	private String testStory;
	private String message;
	private String messageXML;
	private String testDataSpecification;
	private String messageContent;
	private String jurorDocument;
	
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
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTestStory() {
		return testStory;
	}
	public void setTestStory(String testStory) {
		this.testStory = testStory;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getTestDataSpecification() {
		return testDataSpecification;
	}
	public void setTestDataSpecification(String testDataSpecification) {
		this.testDataSpecification = testDataSpecification;
	}
	public String getMessageContent() {
		return messageContent;
	}
	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}
	public String getJurorDocument() {
		return jurorDocument;
	}
	public void setJurorDocument(String jurorDocument) {
		this.jurorDocument = jurorDocument;
	}
	public String getMessageXML() {
		return messageXML;
	}
	public void setMessageXML(String messageXML) {
		this.messageXML = messageXML;
	}
}
