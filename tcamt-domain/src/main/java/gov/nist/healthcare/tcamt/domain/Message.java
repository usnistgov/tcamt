package gov.nist.healthcare.tcamt.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

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
public class Message implements Serializable, Cloneable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5059047349842226726L;
	@Id
    @GeneratedValue
	private long id;
	private String name;
	
	
	@Column(columnDefinition="longtext")
	private String longDescription;
	private String lastUpdateDate;
	private Integer version;

	
	@Column(columnDefinition="longtext")
	private String hl7EndcodedMessage;
	
	
	@Transient
	private String xmlEncodedSTDMessage;
	
	
	@Transient
	private String xmlEncodedNISTMessage;
	
	
	@Transient
	private String xmlEncodedMessageContent;
	
	
	@ManyToOne
    @JoinColumn(name="conformance_profile_id")
	private ConformanceProfile conformanceProfile;
	
	
	@Transient
	private gov.nist.healthcare.tools.hl7.v2.tcamt.lite.domain.profile.Message messageObj;
	
	
	@Transient
	private gov.nist.healthcare.tools.hl7.v2.tcamt.lite.domain.profile.Segments segments;
	
	
	@Transient
	private gov.nist.healthcare.tools.hl7.v2.tcamt.lite.domain.profile.Datatypes datatypes;
	
	
	@Transient
	private gov.nist.healthcare.tools.hl7.v2.tcamt.lite.domain.profile.Tables tables;
	
	
	@ManyToOne
    @JoinColumn(name="author_id")
	private User author;	
	
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "message_tcamtconstraint", joinColumns = {@JoinColumn(name="message_id")}, inverseJoinColumns = {@JoinColumn(name="tcamtConstraint_id")} )
	private Set<TCAMTConstraint> tcamtConstraints = new HashSet<TCAMTConstraint>();

	public Message() {
		this.tcamtConstraints = new HashSet<TCAMTConstraint>();
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

	
	
	public String getLongDescription() {
		return longDescription;
	}

	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Override
	public Message clone() throws CloneNotSupportedException {
		Message cloned = (Message)super.clone();
		cloned.setId(0);
		Set<TCAMTConstraint> cTcamtConstraints = new HashSet<TCAMTConstraint>(); 
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

	public gov.nist.healthcare.tools.hl7.v2.tcamt.lite.domain.profile.Message getMessageObj() {
		return messageObj;
	}

	public void setMessageObj(gov.nist.healthcare.tools.hl7.v2.tcamt.lite.domain.profile.Message messageObj) {
		this.messageObj = messageObj;
	}

	public Set<TCAMTConstraint> getTcamtConstraints() {
		return tcamtConstraints;
	}

	public void setTcamtConstraints(Set<TCAMTConstraint> tcamtConstraints) {
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
	
	public TCAMTConstraint findTCAMTConstraintByIPath(String iPath){
		for(TCAMTConstraint c:this.getTcamtConstraints()){
			if(c.getIpath().equals(iPath)){
				return c;
			}
		}
		
		return new TCAMTConstraint();
	}
	
	public String getXmlEncodedSTDMessage() {
		return xmlEncodedSTDMessage;
	}

	public void setXmlEncodedSTDMessage(String xmlEncodedSTDMessage) {
		this.xmlEncodedSTDMessage = xmlEncodedSTDMessage;
	}

	public String getXmlEncodedNISTMessage() {
		return xmlEncodedNISTMessage;
	}

	public void setXmlEncodedNISTMessage(String xmlEncodedNISTMessage) {
		this.xmlEncodedNISTMessage = xmlEncodedNISTMessage;
	}

	public String getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(String lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public gov.nist.healthcare.tools.hl7.v2.tcamt.lite.domain.profile.Segments getSegments() {
		return segments;
	}

	public void setSegments(
			gov.nist.healthcare.tools.hl7.v2.tcamt.lite.domain.profile.Segments segments) {
		this.segments = segments;
	}

	public gov.nist.healthcare.tools.hl7.v2.tcamt.lite.domain.profile.Datatypes getDatatypes() {
		return datatypes;
	}

	public void setDatatypes(
			gov.nist.healthcare.tools.hl7.v2.tcamt.lite.domain.profile.Datatypes datatypes) {
		this.datatypes = datatypes;
	}

	public gov.nist.healthcare.tools.hl7.v2.tcamt.lite.domain.profile.Tables getTables() {
		return tables;
	}

	public void setTables(
			gov.nist.healthcare.tools.hl7.v2.tcamt.lite.domain.profile.Tables tables) {
		this.tables = tables;
	}

	public ConformanceProfile getConformanceProfile() {
		return conformanceProfile;
	}

	public void setConformanceProfile(ConformanceProfile conformanceProfile) {
		this.conformanceProfile = conformanceProfile;
	}

	public String getXmlEncodedMessageContent() {
		return xmlEncodedMessageContent;
	}

	public void setXmlEncodedMessageContent(String xmlEncodedMessageContent) {
		this.xmlEncodedMessageContent = xmlEncodedMessageContent;
	}
	
	
	
}
