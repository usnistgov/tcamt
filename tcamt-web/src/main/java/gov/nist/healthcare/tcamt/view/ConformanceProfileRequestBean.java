package gov.nist.healthcare.tcamt.view;


import gov.nist.healthcare.tcamt.domain.ConformanceProfile;
import gov.nist.healthcare.tcamt.domain.ContextFreeTestPlan;
import gov.nist.healthcare.tcamt.domain.DefaultTestDataCategorization;
import gov.nist.healthcare.tcamt.domain.DefaultTestDataCategorizationSheet;
import gov.nist.healthcare.tcamt.domain.IntegratedProfile;
import gov.nist.healthcare.tcamt.domain.JurorDocument;
import gov.nist.healthcare.tcamt.domain.Metadata;
import gov.nist.healthcare.tcamt.domain.ProfileContainer;
import gov.nist.healthcare.tcamt.domain.TestObject;
import gov.nist.healthcare.tcamt.domain.data.TestDataCategorization;
import gov.nist.healthcare.tcamt.service.XMLManager;
import gov.nist.healthcare.tcamt.service.converter.ContextFreeTestPlanConverter;
import gov.nist.healthcare.tcamt.service.converter.JsonContextFreeTestPlanConverter;
import gov.nist.healthcare.tcamt.service.converter.JsonMetadataConverter;
import gov.nist.healthcare.tcamt.service.converter.JsonTestObjectConverter;
import gov.nist.healthcare.tcamt.service.converter.MetadataConverter;
import gov.nist.healthcare.tcamt.service.converter.TestObjectConverter;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.ProfileSerialization;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.ProfileSerializationImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

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
	private JurorDocument newJurorDocument = new JurorDocument();
	private ContextFreeTestPlan selectedContextFreeTestPlan = new ContextFreeTestPlan();
	private ConformanceProfile selectedProfile = null;
	private IntegratedProfile selectedIntegratedProfile = null;
	private TestObject selectedTestObject = new TestObject();
	private long conformanceProfileIDForTestObject;
	
	private MetadataConverter metadataConverter;
	private ContextFreeTestPlanConverter cftpConverter;
	private TestObjectConverter toConverter;
	
	private transient StreamedContent zipResourceBundleFile;
	
	private String conformanceProfileId;

	
	/**
	 * 
	 */
	
	public void selectProfile(ActionEvent event) throws CloneNotSupportedException, IOException {
		this.selectedProfile = (ConformanceProfile) event.getComponent().getAttributes().get("profile");
		this.selectedProfile = this.sessionBeanTCAMT.getDbManager().getConformanceProfileById(this.selectedProfile.getId());
		this.conformanceProfileId = this.selectedProfile.getConformanceProfileId();
	}
	
	public void selectPlan(ActionEvent event) throws CloneNotSupportedException, IOException {
		this.selectedContextFreeTestPlan = (ContextFreeTestPlan) event.getComponent().getAttributes().get("plan");
		this.selectedContextFreeTestPlan = this.sessionBeanTCAMT.getDbManager().getContextFreeTestPlanById(this.selectedContextFreeTestPlan.getId());
	}
	
	public void selectIntegratedProfile(ActionEvent event) throws CloneNotSupportedException, IOException {
		this.selectedIntegratedProfile = (IntegratedProfile) event.getComponent().getAttributes().get("profile");
		this.selectedIntegratedProfile = this.sessionBeanTCAMT.getDbManager().getIntegratedProfileById(this.selectedIntegratedProfile.getId());
	}


	public void initNewIntegratedProfile() {
		this.newIntegratedProfile = new IntegratedProfile();
	}
	
	public void initNewContextFreeTestPlan() {
		this.selectedContextFreeTestPlan = new ContextFreeTestPlan();
	}
	
	public void initNewJurorDocument() {
		this.newJurorDocument = new JurorDocument();
	}
	
	private void updateConformanceProfile(IntegratedProfile ip) {
		if(ip.getProfile() != null && !ip.getProfile().equals("") ){
			if(ip.getValueSet() != null && !ip.getValueSet().equals("") ){
				if(ip.getConstraints() != null && !ip.getConstraints().equals("") ){
					ProfileSerialization ps = new ProfileSerializationImpl();
					Profile p = ps.deserializeXMLToProfile(ip.getProfile(), ip.getValueSet(), ip.getConstraints());
					
					ip.setProfileIdentifier(p.getMetaData().getIdentifier());
					
					for(gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message message:p.getMessages().getChildren()){
						ConformanceProfile cp = this.findConformanceProfile(message.getIdentifier());
						
						if(cp == null){
							ConformanceProfile newCP = new ConformanceProfile();
							newCP.setIntegratedProfile(ip);
							newCP.setConformanceProfileId(message.getIdentifier());
							newCP.setValueSetLibraryId(p.getTables().getValueSetLibraryIdentifier());
							newCP.setConstraintId(p.getConstraintId());
							newCP.setName(message.getStructID() + "[" + ip.getName() + "]" + "[" + message.getIdentifier() + "]");
							
							this.sessionBeanTCAMT.getDbManager().conformanceProfileInsert(newCP);
						}else {
							cp.setIntegratedProfile(ip);
							cp.setConformanceProfileId(message.getIdentifier());
							cp.setValueSetLibraryId(p.getTables().getValueSetLibraryIdentifier());
							cp.setConstraintId(p.getConstraintId());
							cp.setName(message.getStructID() + "[" + ip.getName() + "]" + "[" + message.getIdentifier() + "]");
							
							this.sessionBeanTCAMT.getDbManager().conformanceProfileUpdate(cp);
						}
					}
				}
			}
		}
	}
	
	private ConformanceProfile findConformanceProfile(String identifier) {
		for(ConformanceProfile cp:this.sessionBeanTCAMT.getDbManager().getAllConformanceProfiles()){
			if(cp.getConformanceProfileId().equals(identifier)) return cp;
		}
		return null;
	}
	
	public void uploadJDefaultTDC(FileUploadEvent event) throws IOException{
		this.sessionBeanTCAMT.getDbManager().defaultTestDataCategorizationSheetAllDelete();
		XSSFRow row;
		XSSFWorkbook workbook = new XSSFWorkbook(event.getFile().getInputstream());
		
		int sheetCn = workbook.getNumberOfSheets();
		
		for(int cn = 0; cn < sheetCn; cn++){
			DefaultTestDataCategorizationSheet dSheet = new DefaultTestDataCategorizationSheet();
			dSheet.setSheetName(workbook.getSheetName(cn));
						
			XSSFSheet sheet = workbook.getSheetAt(cn);
			int rows = sheet.getPhysicalNumberOfRows();
			
			for (int r = 1; r < rows; r++) {
				row = sheet.getRow(r);
				if (row != null) {
					DefaultTestDataCategorization data = new DefaultTestDataCategorization();
					
					Cell segmentCell = row.getCell(0);
					segmentCell.setCellType(Cell.CELL_TYPE_STRING);
					data.setSegmentName(segmentCell.getStringCellValue());
					
					Cell pathCell = row.getCell(1);
					pathCell.setCellType(Cell.CELL_TYPE_STRING);
					String[] paths = pathCell.getStringCellValue().split("_");
					
					if(paths.length == 1){
						data.setFieldPosition(Integer.parseInt(paths[0]));
					}else if(paths.length == 2){
						data.setFieldPosition(Integer.parseInt(paths[0]));
						data.setComponentPosition(Integer.parseInt(paths[1]));
					}else if(paths.length == 3){
						data.setFieldPosition(Integer.parseInt(paths[0]));
						data.setComponentPosition(Integer.parseInt(paths[1]));
						data.setSubComponentPosition(Integer.parseInt(paths[2]));
					}
					
					Cell categorizatioCell = row.getCell(2);
					
					if(categorizatioCell != null){
						categorizatioCell.setCellType(Cell.CELL_TYPE_STRING);
						data.setCategorization(TestDataCategorization.valueOf(categorizatioCell.getStringCellValue()));
						
						dSheet.getDefaultTestDataCategorizations().add(data);	
					}
				}
			}
			this.sessionBeanTCAMT.getDbManager().defaultTestDataCategorizationSheetInsert(dSheet);
		}
		workbook.close();
	}
	
	public void updateProfileForSelectedProfile(FileUploadEvent event) throws IOException{
		this.selectedProfile.getIntegratedProfile().setProfile(IOUtils.toString(event.getFile().getInputstream(), "UTF-8"));
	}
	
	public void updateConstraintsForSelectedProfile(FileUploadEvent event) throws IOException{
		this.selectedProfile.getIntegratedProfile().setConstraints(IOUtils.toString(event.getFile().getInputstream(), "UTF-8"));
	}
	
	public void updateValueSetForSelectedProfile(FileUploadEvent event) throws IOException{
		this.selectedProfile.getIntegratedProfile().setValueSet(IOUtils.toString(event.getFile().getInputstream(), "UTF-8"));
	}
	
	public void updateProfile(FileUploadEvent event) throws IOException{
		this.selectedIntegratedProfile.setProfile(IOUtils.toString(event.getFile().getInputstream(), "UTF-8"));
	}
	
	public void updateConstraints(FileUploadEvent event) throws IOException{
		this.selectedIntegratedProfile.setConstraints(IOUtils.toString(event.getFile().getInputstream(), "UTF-8"));
	}
	
	public void updateValueSet(FileUploadEvent event) throws IOException{
		this.selectedIntegratedProfile.setValueSet(IOUtils.toString(event.getFile().getInputstream(), "UTF-8"));
	}
	
	public void uploadProfile(FileUploadEvent event) throws IOException{
		this.newIntegratedProfile.setProfile(IOUtils.toString(event.getFile().getInputstream(), "UTF-8"));
	}
	
	public void uploadConstraints(FileUploadEvent event) throws IOException {
		this.newIntegratedProfile.setConstraints(IOUtils.toString(event.getFile().getInputstream(), "UTF-8"));
	}
	
	public void uploadValueSet(FileUploadEvent event) throws IOException {
		this.newIntegratedProfile.setValueSet(IOUtils.toString(event.getFile().getInputstream(), "UTF-8"));
	}
	
	public void uploadMessageContentHTMLXSLT(FileUploadEvent event) throws IOException{
		this.selectedProfile.setMessageContentXSLT(IOUtils.toString(event.getFile().getInputstream(), "UTF-8"));
	}
	
	public void uploadMessageContentJSONXSLT(FileUploadEvent event) throws IOException{
		this.selectedProfile.setMessageContentJSONXSLT(IOUtils.toString(event.getFile().getInputstream(), "UTF-8"));
	}
	
	public void uploadTestDataSpecificationXSLT(FileUploadEvent event) throws IOException{
		this.selectedProfile.setTestDataSpecificationXSLT(IOUtils.toString(event.getFile().getInputstream(), "UTF-8"));
	}
	
	public void uploadTestDataSpecificationJSONXSLT(FileUploadEvent event) throws IOException{
		this.selectedProfile.setTestDataSpecificationJSONXSLT(IOUtils.toString(event.getFile().getInputstream(), "UTF-8"));
	}
	
	public void uploadJurorDocumentXSLT(FileUploadEvent event) throws IOException{
		this.selectedProfile.setJurorDocumentXSLT(IOUtils.toString(event.getFile().getInputstream(), "UTF-8"));
	}
	
	public void uploadJurorDocumentJSONXSLT(FileUploadEvent event) throws IOException{
		this.selectedProfile.setJurorDocumentJSONXSLT(IOUtils.toString(event.getFile().getInputstream(), "UTF-8"));
	}
	
	public void uploadJurorDocumentHTML(FileUploadEvent event) throws IOException{
		this.newJurorDocument.setJurorDocumentHTML(IOUtils.toString(event.getFile().getInputstream(), "UTF-8"));
	}
	
	public void uploadJurorDocumentJSON(FileUploadEvent event) throws IOException{
		this.newJurorDocument.setJurorDocumentJSON(IOUtils.toString(event.getFile().getInputstream(), "UTF-8"));
	}
	
	public void addProfile() throws CloneNotSupportedException, IOException {
		ProfileSerialization ps = new ProfileSerializationImpl();
		Profile p = ps.deserializeXMLToProfile(this.newIntegratedProfile.getProfile(), this.newIntegratedProfile.getValueSet(), this.newIntegratedProfile.getConstraints());
		this.newIntegratedProfile.setProfileIdentifier(p.getMetaData().getIdentifier());
		
		this.sessionBeanTCAMT.getDbManager().integratedProfileInsert(this.newIntegratedProfile);
		for(gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message message:p.getMessages().getChildren()){
			ConformanceProfile newCP = new ConformanceProfile();
			
			newCP.setIntegratedProfile(this.newIntegratedProfile);
			newCP.setConformanceProfileId(message.getIdentifier());
			newCP.setValueSetLibraryId(p.getTables().getValueSetLibraryIdentifier());
			newCP.setConstraintId(p.getConstraintId());
			newCP.setName(message.getStructID() + "[" + this.newIntegratedProfile.getName() + "]" + "[" + message.getIdentifier() + "]");
			
			this.sessionBeanTCAMT.getDbManager().conformanceProfileInsert(newCP);
		}
		
		this.sessionBeanTCAMT.updateConformanceProfiles();
		this.sessionBeanTCAMT.updateIntegratedProfiles();
		
		FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage("Profile Uploaded.",  "Profile: " + this.newIntegratedProfile.getName() + " has been uploaded.") );
	}
	
	public void addContextFreeTestPlan(){
		if(this.selectedContextFreeTestPlan.getId() == 0){
			this.sessionBeanTCAMT.getDbManager().contextFreeTestPlanInsert(this.selectedContextFreeTestPlan);
		}else{
			this.sessionBeanTCAMT.getDbManager().contextFreeTestPlanUpdate(this.selectedContextFreeTestPlan);
		}
		this.sessionBeanTCAMT.updateContextFreeTestPlans();
		
		FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage("Context Free Test Plan Uploaded.",  "TestPlan: " + this.selectedContextFreeTestPlan.getMetadata().getTestSuiteName() + " has been uploaded.") );
	}
	
	public void addTestObject(){
		this.selectedTestObject.setConformanceProfile(this.sessionBeanTCAMT.getDbManager().getConformanceProfileById(this.conformanceProfileIDForTestObject));
		this.selectedContextFreeTestPlan.getTestObjects().add(this.selectedTestObject);
		
		this.selectedTestObject = new TestObject();
		this.conformanceProfileIDForTestObject = 0;

	}
	
	public void addJurorDocument(){
		this.sessionBeanTCAMT.getDbManager().jurorDocumentInsert(this.newJurorDocument);
		this.sessionBeanTCAMT.updateJurorDocuments();
		
		FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage("Juror Document Uploaded.",  "Juror Document has been uploaded."));
	}
	
	public void updateJurorDocument(){
		this.sessionBeanTCAMT.getDbManager().jurorDocumentUpdate(this.newJurorDocument);
		this.sessionBeanTCAMT.updateJurorDocuments();
		
		FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage("Juror Document Updated.",  "Juror Document has been updated."));
	}
	
	public void adjTDC(ActionEvent event){
		this.sessionBeanTCAMT.getDbManager().adjustAllTCAMTConstraints();
	}
	
	public void delMCHTMLXSLT(ActionEvent event) {
		ConformanceProfile cp = (ConformanceProfile) event.getComponent().getAttributes().get("profile");
		cp.setMessageContentXSLT(null);
		this.sessionBeanTCAMT.getDbManager().conformanceProfileUpdate(cp);
		this.sessionBeanTCAMT.updateConformanceProfiles();
	}
	
	public void delMCJSONXSLT(ActionEvent event) {
		ConformanceProfile cp = (ConformanceProfile) event.getComponent().getAttributes().get("profile");
		cp.setMessageContentJSONXSLT(null);
		this.sessionBeanTCAMT.getDbManager().conformanceProfileUpdate(cp);
		this.sessionBeanTCAMT.updateConformanceProfiles();
	}
	
	public void delTDSHTMLXSLT(ActionEvent event) {
		ConformanceProfile cp = (ConformanceProfile) event.getComponent().getAttributes().get("profile");
		cp.setTestDataSpecificationXSLT(null);
		this.sessionBeanTCAMT.getDbManager().conformanceProfileUpdate(cp);
		this.sessionBeanTCAMT.updateConformanceProfiles();
	}
	
	public void delTDSJSONXSLT(ActionEvent event) {
		ConformanceProfile cp = (ConformanceProfile) event.getComponent().getAttributes().get("profile");
		cp.setTestDataSpecificationJSONXSLT(null);
		this.sessionBeanTCAMT.getDbManager().conformanceProfileUpdate(cp);
		this.sessionBeanTCAMT.updateConformanceProfiles();
	}
	
	public void delJurorHTMLXSLT(ActionEvent event) {
		ConformanceProfile cp = (ConformanceProfile) event.getComponent().getAttributes().get("profile");
		cp.setJurorDocumentXSLT(null);
		this.sessionBeanTCAMT.getDbManager().conformanceProfileUpdate(cp);
		this.sessionBeanTCAMT.updateConformanceProfiles();
	}
	
	public void delJurorJSONXSLT(ActionEvent event) {
		ConformanceProfile cp = (ConformanceProfile) event.getComponent().getAttributes().get("profile");
		cp.setJurorDocumentJSONXSLT(null);
		this.sessionBeanTCAMT.getDbManager().conformanceProfileUpdate(cp);
		this.sessionBeanTCAMT.updateConformanceProfiles();
	}
	
	public void delSampleER7Message(ActionEvent event) {
		ConformanceProfile cp = (ConformanceProfile) event.getComponent().getAttributes().get("profile");
		cp.setSampleER7Message(null);
		this.sessionBeanTCAMT.getDbManager().conformanceProfileUpdate(cp);
		this.sessionBeanTCAMT.updateConformanceProfiles();
	}
	
	public void delProfile(ActionEvent event) {
		this.sessionBeanTCAMT.getDbManager().conformanceProfileDelete((ConformanceProfile) event.getComponent().getAttributes().get("profile"));
		this.sessionBeanTCAMT.updateConformanceProfiles();
	}
	
	public void delPlan(ActionEvent event) {
		this.sessionBeanTCAMT.getDbManager().contextFreeTestPlanDelete((ContextFreeTestPlan) event.getComponent().getAttributes().get("plan"));
		this.sessionBeanTCAMT.updateContextFreeTestPlans();
	}
	
	public void delTestObject(ActionEvent event) {
		this.selectedContextFreeTestPlan.getTestObjects().remove((TestObject) event.getComponent().getAttributes().get("testobject"));
	}
	
	
	public void delIntegratedProfile(ActionEvent event) {
		this.sessionBeanTCAMT.getDbManager().integratedProfileDelete((IntegratedProfile) event.getComponent().getAttributes().get("profile"));
		this.sessionBeanTCAMT.updateIntegratedProfiles();
	}
	
	public void delJurorDocument(ActionEvent event) {
		this.sessionBeanTCAMT.getDbManager().jurorDocumentDelete((JurorDocument) event.getComponent().getAttributes().get("juror"));
		this.sessionBeanTCAMT.updateJurorDocuments();
	}
	
	public void editJurorDocument(ActionEvent event) {
		this.newJurorDocument = (JurorDocument) event.getComponent().getAttributes().get("juror");
	}
	
	public void updateConformanceProfile() {
		ProfileSerialization ps = new ProfileSerializationImpl();
		Profile p = ps.deserializeXMLToProfile(this.selectedProfile.getIntegratedProfile().getProfile(), this.selectedProfile.getIntegratedProfile().getValueSet(), this.selectedProfile.getIntegratedProfile().getConstraints());
		this.selectedProfile.getIntegratedProfile().setProfileIdentifier(p.getMetaData().getIdentifier());
		
		this.selectedProfile.setConformanceProfileId(conformanceProfileId);
		this.selectedProfile.setConstraintId(p.getConstraintId());
		this.selectedProfile.setValueSetLibraryId(p.getTables().getValueSetLibraryIdentifier());
		
		this.sessionBeanTCAMT.getDbManager().conformanceProfileUpdate(this.selectedProfile);
		this.sessionBeanTCAMT.getDbManager().integratedProfileUpdate(this.selectedProfile.getIntegratedProfile());
		this.sessionBeanTCAMT.updateConformanceProfiles();
		this.sessionBeanTCAMT.updateIntegratedProfiles();
		this.sessionBeanTCAMT.updateContextFreeTestPlans();
		
		this.conformanceProfileId = null;
	}
	
	public void updateIntegratedProfile() {
		this.updateConformanceProfile(this.selectedIntegratedProfile);
		this.sessionBeanTCAMT.getDbManager().integratedProfileUpdate(this.selectedIntegratedProfile);
		this.sessionBeanTCAMT.updateIntegratedProfiles();
		this.sessionBeanTCAMT.updateConformanceProfiles();
		this.sessionBeanTCAMT.updateContextFreeTestPlans();
	}
	
	public void downloadResourceBundleForTestPlan(ContextFreeTestPlan tp) throws Exception{
		tp.setDescription(tp.getMetadata().getTestSuiteDescription());
		tp.setName(tp.getMetadata().getTestSuiteName());
		
		
		this.setCftpConverter(new JsonContextFreeTestPlanConverter());
		this.setToConverter(new JsonTestObjectConverter());
		this.setMetadataConverter(new JsonMetadataConverter());
		
		
		String outFilename = "TestPlan_" + tp.getMetadata().getTestSuiteName() + ".zip";
		ByteArrayOutputStream outputStream = null;
		byte[] bytes;
		outputStream = new ByteArrayOutputStream();
		ZipOutputStream out = new ZipOutputStream(outputStream);
		
		this.generateMetaDataRB(out, tp.getMetadata());
		this.generateContextFreeRB(out, tp);
		this.generateDocumentationRB(out, tp);
		this.generateProfilesConstraintsValueSetsRB(out, tp);
		this.generateXSLTRB(out, tp);
		
		out.close();
		bytes = outputStream.toByteArray();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
		this.setZipResourceBundleFile(new DefaultStreamedContent(inputStream, "application/zip", outFilename));
	}
	
	private void generateContextFreeRB(ZipOutputStream out, ContextFreeTestPlan tp) throws Exception {
		this.generateTestPlanJsonRB(out, tp);
		for(TestObject to:tp.getTestObjects()){
			String testcasePath = "Contextfree" + File.separator + to.getName();
			this.generateTestObjectJsonRB(out, to, testcasePath);
		}
	}
	
	private void generateTestPlanJsonRB(ZipOutputStream out, ContextFreeTestPlan tp) {
		try {
			byte[] buf = new byte[1024];
			out.putNextEntry(new ZipEntry("Contextfree" + File.separator + "TestPlan.json"));
			InputStream inTP = IOUtils.toInputStream(this.cftpConverter.toString(tp));
			int lenTP;
	        while ((lenTP = inTP.read(buf)) > 0) {
	            out.write(buf, 0, lenTP);
	        }
	        out.closeEntry();
	        inTP.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void generateTestObjectJsonRB(ZipOutputStream out, TestObject to, String testcasePath) {
		try {
			ProfileContainer hl7v2 = new ProfileContainer();
			hl7v2.setMessageId(to.getConformanceProfile().getConformanceProfileId());
			hl7v2.setConstraintId(to.getConformanceProfile().getConstraintId());
			hl7v2.setValueSetLibraryId(to.getConformanceProfile().getValueSetLibraryId());
			to.setHl7v2(hl7v2);
			
			byte[] buf = new byte[1024];
			out.putNextEntry(new ZipEntry(testcasePath + File.separator + "TestObject.json"));
			InputStream inTO = IOUtils.toInputStream(this.toConverter.toString(to));
			int lenTO;
	        while ((lenTO = inTO.read(buf)) > 0) {
	            out.write(buf, 0, lenTO);
	        }
	        out.closeEntry();
	        inTO.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void generateProfilesConstraintsValueSetsRB(ZipOutputStream out, ContextFreeTestPlan tp){		
		for(TestObject to:tp.getTestObjects()){
			this.generateProfileRB(out, to.getConformanceProfile().getIntegratedProfile().getProfile() , to.getConformanceProfile().getIntegratedProfile().getName() + "_Profile.xml");
			this.generateValueSetRB(out, to.getConformanceProfile().getIntegratedProfile().getValueSet(), to.getConformanceProfile().getIntegratedProfile().getName() + "_ValueSetLibrary.xml");
			this.generateConstraintRB(out, to.getConformanceProfile().getIntegratedProfile().getConstraints(), to.getConformanceProfile().getIntegratedProfile().getName() + "_Constraints.xml");	
		}
	}
	
	private void generateProfileRB(ZipOutputStream out, String profileStr, String fileName) {
		try {
			byte[] buf = new byte[1024];
			out.putNextEntry(new ZipEntry("Global" + File.separator + "Profiles" + File.separator + fileName));
			if(profileStr != null){
				InputStream inMessage = IOUtils.toInputStream(XMLManager.docToString(XMLManager.stringToDom(profileStr)),"UTF-8");
				int lenMessage;
	            while ((lenMessage = inMessage.read(buf)) > 0) {
	                out.write(buf, 0, lenMessage);
	            }
	            inMessage.close();
			}
	        out.closeEntry();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void generateConstraintRB(ZipOutputStream out, String constraintStr, String fileName) {
		try {
			byte[] buf = new byte[1024];
			out.putNextEntry(new ZipEntry("Global" + File.separator + "Constraints" + File.separator + fileName));		
			if(constraintStr != null){
				InputStream inMessage = IOUtils.toInputStream(constraintStr, "UTF-8");
				int lenMessage;
				while ((lenMessage = inMessage.read(buf)) > 0) {
					out.write(buf, 0, lenMessage);
				}
				inMessage.close();
			}
			out.closeEntry();
		} catch (Exception e) {
				e.printStackTrace();
		}
		
		
	}
	
	private void generateValueSetRB(ZipOutputStream out, String valueSetStr, String fileName) {
		try {
			byte[] buf = new byte[1024];
			out.putNextEntry(new ZipEntry("Global" + File.separator + "ValueSetLibrary" + File.separator + fileName));
			if(valueSetStr != null){
				InputStream inMessage = IOUtils.toInputStream(XMLManager.docToString(XMLManager.stringToDom(valueSetStr)), "UTF-8");
				int lenMessage;
	            while ((lenMessage = inMessage.read(buf)) > 0) {
	                out.write(buf, 0, lenMessage);
	            }
	            inMessage.close();
			}
	        out.closeEntry();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void generateMetaDataRB(ZipOutputStream out, Metadata md) {
		try {
			byte[] buf = new byte[1024];
			out.putNextEntry(new ZipEntry("About" + File.separator + "Metadata.json"));
			InputStream inMetadata = IOUtils.toInputStream(this.metadataConverter.toString(md));
			int lenMeta;
	        while ((lenMeta = inMetadata.read(buf)) > 0) {
	            out.write(buf, 0, lenMeta);
	        }
	        out.closeEntry();
	        inMetadata.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void generateDocumentationRB(ZipOutputStream out, ContextFreeTestPlan tp){
		try {
			out.putNextEntry(new ZipEntry("Documentation" + File.separator));
			out.closeEntry();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void generateXSLTRB(ZipOutputStream out, ContextFreeTestPlan tp){
		try {
			out.putNextEntry(new ZipEntry("Global" + File.separator + "xslt" + File.separator));
			out.closeEntry();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	
	public List<IntegratedProfile> getIntegratedProfiles(){
		return this.sessionBeanTCAMT.getIntegratedProfiles();
	}
	
	public List<ContextFreeTestPlan> getContextFreeTestPlans(){
		return this.sessionBeanTCAMT.getContextFreeTestPlans();
	}
	
	public List<JurorDocument> getJurorDocuments(){
		return this.sessionBeanTCAMT.getJurorDocuments();
	}

	public ConformanceProfile getSelectedProfile() {
		return selectedProfile;
	}

	public void setSelectedProfile(ConformanceProfile selectedProfile) {
		this.selectedProfile = selectedProfile;
	}

	public IntegratedProfile getSelectedIntegratedProfile() {
		return selectedIntegratedProfile;
	}

	public void setSelectedIntegratedProfile(IntegratedProfile selectedIntegratedProfile) {
		this.selectedIntegratedProfile = selectedIntegratedProfile;
	}

	public ContextFreeTestPlan getSelectedContextFreeTestPlan() {
		return selectedContextFreeTestPlan;
	}

	public void setSelectedContextFreeTestPlan(ContextFreeTestPlan selectedContextFreeTestPlan) {
		this.selectedContextFreeTestPlan = selectedContextFreeTestPlan;
	}

	public TestObject getSelectedTestObject() {
		return selectedTestObject;
	}

	public void setSelectedTestObject(TestObject selectedTestObject) {
		this.selectedTestObject = selectedTestObject;
	}


	public long getConformanceProfileIDForTestObject() {
		return conformanceProfileIDForTestObject;
	}


	public void setConformanceProfileIDForTestObject(
			long conformanceProfileIDForTestObject) {
		this.conformanceProfileIDForTestObject = conformanceProfileIDForTestObject;
	}

	public StreamedContent getZipResourceBundleFile() {
		return zipResourceBundleFile;
	}

	public void setZipResourceBundleFile(StreamedContent zipResourceBundleFile) {
		this.zipResourceBundleFile = zipResourceBundleFile;
	}

	public MetadataConverter getMetadataConverter() {
		return metadataConverter;
	}

	public void setMetadataConverter(MetadataConverter metadataConverter) {
		this.metadataConverter = metadataConverter;
	}

	public ContextFreeTestPlanConverter getCftpConverter() {
		return cftpConverter;
	}

	public void setCftpConverter(ContextFreeTestPlanConverter cftpConverter) {
		this.cftpConverter = cftpConverter;
	}

	public TestObjectConverter getToConverter() {
		return toConverter;
	}

	public void setToConverter(TestObjectConverter toConverter) {
		this.toConverter = toConverter;
	}

	public String getConformanceProfileId() {
		return conformanceProfileId;
	}

	public void setConformanceProfileId(String conformanceProfileId) {
		this.conformanceProfileId = conformanceProfileId;
	}

	public JurorDocument getNewJurorDocument() {
		return newJurorDocument;
	}

	public void setNewJurorDocument(JurorDocument newJurorDocument) {
		this.newJurorDocument = newJurorDocument;
	}
	
	public List<DefaultTestDataCategorizationSheet> getDefaultTestDataCategorizationSheets(){
		return this.sessionBeanTCAMT.getDbManager().getAllDefaultTestDataCategorizationSheets();
	}
	
}
