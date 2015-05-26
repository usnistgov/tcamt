package gov.nist.healthcare.tcamt.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table
public class Actor implements Cloneable, Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 5815768987412850270L;
	
	@Id
    @GeneratedValue
	private long id;
	
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false)
	private String role;
	
	@Column(nullable = false)
	private String reference;
	
	private Integer version;
	
    @ManyToOne
    @JoinColumn(name="author_id")
	private User author;

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
	public Actor clone() throws CloneNotSupportedException {
		Actor cloned = (Actor) super.clone();
		return cloned;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

}
