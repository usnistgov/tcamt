package gov.nist.healthcare.tcamt.jsonDomain;

import java.util.HashSet;
import java.util.Set;

public class TestPlan {
	private String name;
	private String description;
	private String type;
	private boolean transport;
	private int position;
	private String domain;
	
	private Set<TestCaseGroup> children = new HashSet<TestCaseGroup>();
	
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean isTransport() {
		return transport;
	}
	public void setTransport(boolean transport) {
		this.transport = transport;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public Set<TestCaseGroup> getChildren() {
		return children;
	}
	public void setChildren(Set<TestCaseGroup> children) {
		this.children = children;
	}
	
	public void addChild(TestCaseGroup tcg){
		this.children.add(tcg);
	}
	
	
}
