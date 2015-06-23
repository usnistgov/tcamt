package gov.nist.healthcare.tcamt.view;

import gov.nist.healthcare.tcamt.domain.ConformanceProfile;
import gov.nist.healthcare.tcamt.domain.IntegratedProfile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.ProfileSerialization;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.ProfileSerializationImpl;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.io.IOUtils;
import org.primefaces.event.FileUploadEvent;

@ManagedBean
@SessionScoped
public class ConformanceProfileRequestBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 432938680402529031L;
	
	@ManagedProperty("#{sessionBeanTCAMT}")
	private SessionBeanTCAMT sessionBeanTCAMT;
	
	private IntegratedProfile newIntegratedProfile = new IntegratedProfile();
	private ConformanceProfile selectedProfile = null;

	
	/**
	 * 
	 */
	
	public void selectProfile(ActionEvent event) throws CloneNotSupportedException, IOException {
		this.selectedProfile = (ConformanceProfile) event.getComponent().getAttributes().get("profile");
		this.selectedProfile = this.sessionBeanTCAMT.getDbManager().getConformanceProfileById(this.selectedProfile.getId());
	}

	public void initNewIntegratedProfile() {
		this.newIntegratedProfile = new IntegratedProfile();
	}
	
	private void readProfile(IntegratedProfile ip) throws CloneNotSupportedException{
		if(ip.getProfile() != null && ip.getProfile().equals("") ){
			if(ip.getValueSet() != null && ip.getValueSet().equals("") ){
				if(ip.getConstraints() != null && ip.getConstraints().equals("") ){
					//TODO
				}
			}
		}
	}
	
	public void uploadProfile(FileUploadEvent event) throws IOException, CloneNotSupportedException{
		this.newIntegratedProfile.setProfile(IOUtils.toString(event.getFile().getInputstream(), "UTF-8"));
		this.readProfile(this.newIntegratedProfile);
	}
	
	public void uploadConstraints(FileUploadEvent event) throws IOException, CloneNotSupportedException {
		this.newIntegratedProfile.setConstraints(IOUtils.toString(event.getFile().getInputstream(), "UTF-8"));
		this.readProfile(this.newIntegratedProfile);
	}
	
	public void uploadValueSet(FileUploadEvent event) throws IOException, CloneNotSupportedException {
		this.newIntegratedProfile.setValueSet(IOUtils.toString(event.getFile().getInputstream(), "UTF-8"));
		this.readProfile(this.newIntegratedProfile);
	}
	
	public void uploadTestDataSpecificationXSLT(FileUploadEvent event) throws IOException, CloneNotSupportedException{
		this.selectedProfile.setTestDataSpecificationXSLT(IOUtils.toString(event.getFile().getInputstream(), "UTF-8"));
	}
	
	public void uploadJurorDocumentXSLT(FileUploadEvent event) throws IOException, CloneNotSupportedException{
		this.selectedProfile.setJurorDocumentXSLT(IOUtils.toString(event.getFile().getInputstream(), "UTF-8"));
	}
	
	public void addProfile() throws CloneNotSupportedException, IOException {
		this.sessionBeanTCAMT.getDbManager().integratedProfileInsert(this.newIntegratedProfile);
		ProfileSerialization ps = new ProfileSerializationImpl();
		Profile p = ps.deserializeXMLToProfile(this.newIntegratedProfile.getProfile(), this.newIntegratedProfile.getValueSet(), this.newIntegratedProfile.getConstraints());
		for(gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message message:p.getMessages().getChildren()){
			ConformanceProfile newCP = new ConformanceProfile();
			
			newCP.setIntegratedProfile(this.newIntegratedProfile);
			newCP.setConformanceProfileId(message.getIdentifier());
			newCP.setName("[" + this.newIntegratedProfile.getName() + "]" + message.getIdentifier());
			
			this.sessionBeanTCAMT.getDbManager().conformanceProfileInsert(newCP);
		}
		
		this.sessionBeanTCAMT.updateConformanceProfiles();
		
		FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage("Profile Uploaded.",  "Profile: " + this.newIntegratedProfile.getName() + " has been uploaded.") );
	}
	
	public void delProfile(ActionEvent event) {
		this.sessionBeanTCAMT.getDbManager().conformanceProfileDelete((ConformanceProfile) event.getComponent().getAttributes().get("profile"));
		this.sessionBeanTCAMT.updateConformanceProfiles();
	}
	
	public void updateProfile() {
		this.sessionBeanTCAMT.getDbManager().conformanceProfileUpdate(this.selectedProfile);
		this.sessionBeanTCAMT.updateConformanceProfiles();
	}
	

	/**
	 * 
	 */
	
	public SessionBeanTCAMT getSessionBeanTCAMT() {
		return sessionBeanTCAMT;
	}

	public void setSessionBeanTCAMT(SessionBeanTCAMT sessionBeanTCAMT) {
		this.sessionBeanTCAMT = sessionBeanTCAMT;
	}

	public IntegratedProfile getNewIntegratedProfile() {
		return newIntegratedProfile;
	}

	public void setNewIntegratedProfile(IntegratedProfile newIntegratedProfile) {
		this.newIntegratedProfile = newIntegratedProfile;
	}

	public List<ConformanceProfile> getConformanceProfiles(){
		return this.sessionBeanTCAMT.getConformanceProfiles();
	}

	public ConformanceProfile getSelectedProfile() {
		return selectedProfile;
	}

	public void setSelectedProfile(ConformanceProfile selectedProfile) {
		this.selectedProfile = selectedProfile;
	}
	
	
}
