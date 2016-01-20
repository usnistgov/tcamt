package gov.nist.healthcare.tcamt.domain;

public class TestStep {
	private String name;
	private String description;
	private String type;
	private int position;
	private ProfileContainer hl7v2;
	private String protocol;
	
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
	public ProfileContainer getHl7v2() {
		return hl7v2;
	}
	public void setHl7v2(ProfileContainer hl7v2) {
		this.hl7v2 = hl7v2;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	
}
