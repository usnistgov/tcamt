package gov.nist.healthcare.tcamt.domain;

import java.io.Serializable;


public class Interaction implements Cloneable, Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5928301465572845004L;
	private Integer id;
	private String name;
	private String description;
	private Actor sActor;
	private Actor rActor;
	private Message message;
	private Integer version;
	
	public Interaction(Integer id, String name, String description, Integer version) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.setVersion(version);
	}

	public Interaction() {
		super();
		this.message = new Message();
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

	public Actor getsActor() {
		return sActor;
	}

	public void setsActor(Actor sActor) {
		this.sActor = sActor;
	}

	public Actor getrActor() {
		return rActor;
	}

	public void setrActor(Actor rActor) {
		this.rActor = rActor;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		Interaction cloned = (Interaction)super.clone();
		cloned.setMessage((Message)this.message.clone());
		
		return cloned;
	}
}
