package gov.nist.healthcare.tools.hl7.v2.tcamt.lite.domain.profile.constraints;

import java.io.Serializable;

//@Entity
//@Table(name = "CONSTRAINTS")
public class Constraints implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// @Id 
	// @Column(name = "ID")
	// @GeneratedValue(strategy = GenerationType.AUTO)
	private String id;

	// @OneToOne(optional = false, fetch = FetchType.EAGER, cascade =
	// CascadeType.ALL, orphanRemoval = true)
	// @JoinColumn(name = "DATATYPES_ID")
	private Context datatypes;

	// @OneToOne(optional = false, fetch = FetchType.EAGER, cascade =
	// CascadeType.ALL, orphanRemoval = true)
	// @JoinColumn(name = "SEGMENTS_ID")
	private Context segments;

	// @OneToOne(optional = false, fetch = FetchType.EAGER, cascade =
	// CascadeType.ALL, orphanRemoval = true)
	// @JoinColumn(name = "GROUPS_ID")
	private Context groups;
	
	private Context messages;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Context getDatatypes() {
		return datatypes;
	}

	public void setDatatypes(Context datatypes) {
		this.datatypes = datatypes;
	}

	public Context getSegments() {
		return segments;
	}

	public void setSegments(Context segments) {
		this.segments = segments;
	}

	public Context getGroups() {
		return groups;
	}

	public void setGroups(Context groups) {
		this.groups = groups;
	}

	public Context getMessages() {
		return messages;
	}

	public void setMessages(Context messages) {
		this.messages = messages;
	}

}
