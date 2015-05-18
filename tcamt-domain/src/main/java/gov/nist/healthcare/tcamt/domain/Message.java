package gov.nist.healthcare.tcamt.domain;

import gov.nist.healthcare.tcamt.domain.data.TestDataCategorization;

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
import com.fasterxml.jackson.annotation.JsonIgnore;
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
	
	@JsonIgnore
	@Column(columnDefinition="longtext")
	private String hl7EndcodedMessage;
	
	@JsonIgnore
	@Column(columnDefinition="longtext")
	private String profile;
	
	@JsonIgnore
	@Column(columnDefinition="longtext")
	private String constraints;
	
	@JsonIgnore 
	@Column(columnDefinition="longtext")
	private String valueSet;
	
	@JsonIgnore
	@Transient
	private gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message messageObj;
	
	@JsonIgnore
	@ManyToOne
    @JoinColumn(name="author_id")
	private User author;	
	
	@JsonIgnore
	@OneToMany(fetch=FetchType.EAGER, cascade = {CascadeType.ALL})
    @JoinTable(name = "message_tcamtconstraint", joinColumns = {@JoinColumn(name="message_id")}, inverseJoinColumns = {@JoinColumn(name="tcamtConstraint_id")} )
	private List<TCAMTConstraint> tcamtConstraints = new ArrayList<TCAMTConstraint>();

	public Message() {
		this.tcamtConstraints = new ArrayList<TCAMTConstraint>();
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
		List<TCAMTConstraint> cTcamtConstraints = new ArrayList<TCAMTConstraint>(); 
		for(TCAMTConstraint c:this.tcamtConstraints){
			cTcamtConstraints.add(c.clone());
		}
		cloned.setTcamtConstraints(cTcamtConstraints);
		return cloned;
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

	public List<TCAMTConstraint> getTcamtConstraints() {
		return tcamtConstraints;
	}

	public void setTcamtConstraints(List<TCAMTConstraint> tcamtConstraints) {
		this.tcamtConstraints = tcamtConstraints;
	}
	
	public void addTCAMTConstraint(TCAMTConstraint tcamtConstraint){
		this.tcamtConstraints.add(tcamtConstraint);
	}
	
	public void deleteTCAMTConstraintByIPath(String iPath){
		for(TCAMTConstraint c:this.getTcamtConstraints()){
			if(c.getIpath().equals(iPath)){
				this.getTcamtConstraints().remove(c);
				return;
			}
		}
	}
	
	public TestDataCategorization findTCAMTConstraintByIPath(String iPath){
		for(TCAMTConstraint c:this.getTcamtConstraints()){
			if(c.getIpath().equals(iPath)){
				return c.getCategorization();
			}
		}
		return null;
	}

}
