package gov.nist.healthcare.tcamt.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class,
property = "@id")
@Entity
@Table
public class Message implements Cloneable, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7669461668488662066L;
	
	
	@Id
    @GeneratedValue
	private long id;
	private String name;
	private String description;
	private Integer version;
	
	@Column(columnDefinition="longtext")
	private String hl7EndcodedMessage;
	
	@Column(columnDefinition="longtext")
	private String profile;
	
	@Column(columnDefinition="longtext")
	private String constraints;
	
	@Column(columnDefinition="longtext")
	private String valueSet;
	
	@Transient
	private gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message messageObj;
	
	@ManyToOne
    @JoinColumn(name="author_id")
	private User author;	
	
	@OneToMany(fetch=FetchType.EAGER, cascade = {CascadeType.ALL})
    @JoinTable(name = "message_validationcontext", joinColumns = {@JoinColumn(name="message_id")},
               inverseJoinColumns = {@JoinColumn(name="validationcontext_id")} )
	private List<ValidationContext> validationContexts = new ArrayList<ValidationContext>();

	public Message() {
		this.validationContexts = new ArrayList<ValidationContext>(); 
	}

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

	@Override
	public Object clone() throws CloneNotSupportedException {
		Message cloned = (Message)super.clone();
		
		List<ValidationContext> cValidationContexts = new ArrayList<ValidationContext>(); 
		
		
		for(ValidationContext v:this.validationContexts){
			cValidationContexts.add((ValidationContext)v.clone());
		}
		
		cloned.setValidationContexts(cValidationContexts);
		
		return cloned;
	}
	
	public List <ValidationContext> getValidationContexts() {
		return validationContexts;
	}

	public void setValidationContexts(List <ValidationContext> validationContexts) {
		this.validationContexts = validationContexts;
	}

	public String getHl7EndcodedMessage() {
		return hl7EndcodedMessage;
	}

	public void setHl7EndcodedMessage(String hl7EndcodedMessage) {
		this.hl7EndcodedMessage = hl7EndcodedMessage;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public String getConstraints() {
		return constraints;
	}

	public void setConstraints(String constraints) {
		this.constraints = constraints;
	}

	public String getValueSet() {
		return valueSet;
	}

	public void setValueSet(String valueSet) {
		this.valueSet = valueSet;
	}

	public gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message getMessageObj() {
		return messageObj;
	}

	public void setMessageObj(gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message messageObj) {
		this.messageObj = messageObj;
	}
	
	

}
