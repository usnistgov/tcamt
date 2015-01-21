package gov.nist.healthcare.tcamt.domain;

import gov.nist.healthcare.hl7tools.domain.ConformanceStatement;
import gov.nist.healthcare.hl7tools.domain.Predicate;
import gov.nist.healthcare.hl7tools.v2.maker.core.domain.profile.MessageProfile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class,
property = "@id")
public class Message implements Cloneable, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7669461668488662066L;
	private Integer id;
	private String name;
	private String description;
	private Integer version;
	private MessageProfile messageProfile;
	private List<ConformanceStatement> listCSs;
	private List<Predicate> listCPs;
	private List<ProfilePathOccurIGData> profilePathOccurIGData;
	private List<InstanceTestDataType> instanceTestDataTypes;
	private List<ValidationContext> validationContexts;
	private String hl7EndcodedMessage;

	public Message() {
		this.listCSs = new ArrayList<ConformanceStatement>();
		this.listCPs = new ArrayList<Predicate>();
		this.profilePathOccurIGData = new ArrayList<ProfilePathOccurIGData>();
		this.instanceTestDataTypes = new ArrayList<InstanceTestDataType>();
		this.validationContexts = new ArrayList<ValidationContext>(); 
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
	
	public MessageProfile getMessageProfile() {
		return messageProfile;
	}

	public void setMessageProfile(MessageProfile messageProfile) {
		this.messageProfile = messageProfile;
	}

	@Override
	public String toString() {
		return "Document [id=" + id + ", name=" + name + ", description="
				+ description + ", messageProfile=" + messageProfile
				+ ", version=" + version + "]";
	}


	@Override
	public Object clone() throws CloneNotSupportedException {
		Message cloned = (Message)super.clone();
		
		List<ProfilePathOccurIGData> cInstancePathOccurIGData = new ArrayList<ProfilePathOccurIGData>();
		List<ValidationContext> cValidationContexts = new ArrayList<ValidationContext>(); 
		List<InstanceTestDataType> cinstanceTestDataTypes = new ArrayList<InstanceTestDataType>();
		
		for(ProfilePathOccurIGData i:this.profilePathOccurIGData){
			cInstancePathOccurIGData.add((ProfilePathOccurIGData)i.clone());
		}
		
		for(InstanceTestDataType i:this.instanceTestDataTypes){
			cinstanceTestDataTypes.add((InstanceTestDataType)i.clone());
		}
		
		for(ValidationContext v:this.validationContexts){
			cValidationContexts.add((ValidationContext)v.clone());
		}
		
		cloned.setProfilePathOccurIGData(cInstancePathOccurIGData);
		cloned.setInstanceTestDataTypes(cinstanceTestDataTypes);
		cloned.setValidationContexts(cValidationContexts);
		
		return cloned;
	}

	public List<ConformanceStatement> getListCSs() {
		return listCSs;
	}

	public void setListCSs(List<ConformanceStatement> listCSs) {
		this.listCSs = listCSs;
	}

	public List<Predicate> getListCPs() {
		return listCPs;
	}

	public void setListCPs(List<Predicate> listCPs) {
		this.listCPs = listCPs;
	}

	public List<ProfilePathOccurIGData> getProfilePathOccurIGData() {
		return profilePathOccurIGData;
	}

	public void setProfilePathOccurIGData(List<ProfilePathOccurIGData> profilePathOccurIGData) {
		this.profilePathOccurIGData = profilePathOccurIGData;
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

	public List<InstanceTestDataType> getInstanceTestDataTypes() {
		return instanceTestDataTypes;
	}

	public void setInstanceTestDataTypes(List<InstanceTestDataType> instanceTestDataTypes) {
		this.instanceTestDataTypes = instanceTestDataTypes;
	}

}
