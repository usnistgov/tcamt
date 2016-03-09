package gov.nist.healthcare.tcamt.jsonDomain;

import java.util.HashSet;
import java.util.Set;

public class TestCaseGroup {
	private String name;
	private String description;
	private int position;
	
	private Set<Object> children = new HashSet<Object>();
	
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
	public Set<Object> getChildren() {
		return children;
	}
	public void setChildren(Set<Object> children) {
		this.children = children;
	}
	
	public void addChild(Object o){
		this.children.add(o);
	}
}
