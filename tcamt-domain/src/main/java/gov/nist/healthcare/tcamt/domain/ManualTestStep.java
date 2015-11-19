package gov.nist.healthcare.tcamt.domain;

import java.io.Serializable;

public class ManualTestStep implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2380448743873287608L;
	private String name;
	private String description;
	private String type;
	private int position;

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

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
}
