package gov.nist.healthcare.tcamt.jsonDomain;

import java.util.HashSet;
import java.util.Set;

public class TestCase {
	private String name;
	private String description;
	private int position;
	private String protocol;
	private String sut;
	
	private Set<TestStep> children = new HashSet<TestStep>();
	
	
	private String testStory;
	
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
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getSut() {
		return sut;
	}
	public void setSut(String sut) {
		this.sut = sut;
	}
	public String getTestStory() {
		return testStory;
	}
	public void setTestStory(String testStory) {
		this.testStory = testStory;
	}
	public Set<TestStep> getChildren() {
		return children;
	}
	public void setChildren(Set<TestStep> children) {
		this.children = children;
	}
	
	public void addChild(TestStep ts){
		this.children.add(ts);
	}
}
