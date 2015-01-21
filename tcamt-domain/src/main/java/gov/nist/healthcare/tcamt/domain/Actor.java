package gov.nist.healthcare.tcamt.domain;

import java.io.Serializable;

public class Actor implements Cloneable, Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 5815768987412850270L;
	private Integer id;
	private String name;
	private String role;
	private String reference;
	private Integer version;

	public Actor() {
	}

	public Actor(Integer id, String name, String role, String reference,
			Integer version) {
		super();
		this.id = id;
		this.name = name;
		this.role = role;
		this.reference = reference;
		this.version = version;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "Actor [id=" + id + ", name=" + name + ", role=" + role
				+ ", reference=" + reference + ", version=" + version + "]";
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		Actor cloned = (Actor) super.clone();
		return cloned;
	}

}
