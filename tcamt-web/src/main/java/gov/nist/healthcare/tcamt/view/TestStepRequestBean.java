package gov.nist.healthcare.tcamt.view;

import gov.nist.healthcare.core.hl7.v2.parser.ParserException;
import gov.nist.healthcare.hl7tools.domain.Component;
import gov.nist.healthcare.hl7tools.domain.Element;
import gov.nist.healthcare.hl7tools.domain.Field;
import gov.nist.healthcare.hl7tools.service.serializer.ProfileSchemaVersion;
import gov.nist.healthcare.hl7tools.service.serializer.XMLDeserializer;
import gov.nist.healthcare.hl7tools.v2.maker.core.ConversionException;
import gov.nist.healthcare.hl7tools.v2.maker.core.domain.profile.MessageProfile;
import gov.nist.healthcare.hl7tools.v2.profilemaker.service.JSONConverterService;
import gov.nist.healthcare.tcamt.db.DBImpl;
import gov.nist.healthcare.tcamt.domain.InstanceSegment;
import gov.nist.healthcare.tcamt.domain.Interaction;
import gov.nist.healthcare.tcamt.domain.Message;
import gov.nist.healthcare.tcamt.domain.MessageTreeModel;
import gov.nist.healthcare.tcamt.domain.SegmentTreeModel;
import gov.nist.healthcare.tcamt.domain.TestStep;
import gov.nist.healthcare.tcamt.domain.TestStory;
import gov.nist.healthcare.tcamt.domain.ValidationContext;
import gov.nist.healthcare.tcamt.service.ManageInstance;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

@ManagedBean
@SessionScoped
public class TestStepRequestBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5553580108472239146L;


	private TestStep newTestStep;
	private TestStep editTestStep;
	private TestStep existTestStep;
	
	private InstanceSegment selectedInstanceSegment;
	private TreeNode messageTreeRoot;
	private List<InstanceSegment> instanceSegments;
	private List<TreeNode> toBeDeletedTreeNodes;
	private SegmentTreeModel toBeRepeatedModel;
	private ManageInstance manageInstanceService;
	private Object selectedModel;
	private DBImpl dbManager = new DBImpl();
	@ManagedProperty("#{sessionBeanTCAMT}")
	private SessionBeanTCAMT sessionBeanTCAMT;
	
	private Integer interactionId;
	private Integer targetActorId;
	
	private String shareTo;
	
	private TestStep toBeDeletedTestStep;
	private boolean canDelete;
	

	/**
	 * 
	 */
	public void checkTestStepToDelete(ActionEvent event){
		this.toBeDeletedTestStep = (TestStep) event.getComponent().getAttributes().get("teststep");
		this.setCanDelete(this.dbManager.checkAvailabilityDeleteTestStep(this.toBeDeletedTestStep.getId()));
	}
	
	public void delTestStep(ActionEvent event) {
		this.dbManager.testStepDelete(this.toBeDeletedTestStep);
		this.sessionBeanTCAMT.updateTestSteps();
		this.init();
	}
	
	public void cloneTestStep(ActionEvent event) throws CloneNotSupportedException {	
		TestStep ts = (TestStep)((TestStep)event.getComponent().getAttributes().get("teststep")).clone();
		ts.setName("Copy_" + ts.getName());
		ts.setVersion(0);
		this.dbManager.testStepInsert(ts, this.sessionBeanTCAMT.getLoggedId());
		this.sessionBeanTCAMT.updateTestSteps();
		this.init();
	}
	
	public void delInstanceSegment(ActionEvent event) {
		int rowIndex = (Integer)event.getComponent().getAttributes().get("rowIndex");
		String[] lines = this.editTestStep.getInteraction().getMessage().getHl7EndcodedMessage().split(System.getProperty("line.separator"));
		String editedHl7EndcodedMessage = "";
		for(int i=0; i<lines.length;i++){
			if(i != rowIndex){
				editedHl7EndcodedMessage = editedHl7EndcodedMessage + lines[i] + System.getProperty("line.separator");
			}
		}
		this.editTestStep.getInteraction().getMessage().setHl7EndcodedMessage(editedHl7EndcodedMessage);
		this.readHL7Message();
	}

	public TestStepRequestBean() {
		super();
		this.init();
	}
	
	private void init(){
		this.newTestStep = new TestStep();
		this.editTestStep = new TestStep();
		this.existTestStep = new TestStep();
		this.selectedInstanceSegment = new InstanceSegment();
		this.messageTreeRoot = new DefaultTreeNode("root", null);
		this.instanceSegments = new ArrayList<InstanceSegment>();
		this.toBeDeletedTreeNodes = new ArrayList<TreeNode>();
		this.toBeRepeatedModel = null;
		this.manageInstanceService = new ManageInstance();
		this.selectedModel = null;
		this.interactionId = null;
		this.targetActorId = null;
		
	}

	public void createTestStep() {
		this.newTestStep = new TestStep();
		this.interactionId = null;
		this.targetActorId = null;
	}
	
	public void uploadMessageProfile(FileUploadEvent event)
			throws ConversionException {
		try {
			if(event.getFile().getFileName().endsWith(".PROFILE")){
				JSONConverterService jConverterService = new JSONConverterService();
				MessageProfile profile = jConverterService.fromStream(event.getFile().getInputstream());
				Interaction interaction = new Interaction();
				Message message = new Message();
				message.setMessageProfile(profile);
				message.setName(profile.getName());
				interaction.setMessage(message);
				this.newTestStep.setInteraction(interaction);
			}else if (event.getFile().getFileName().endsWith(".xml")){
				XMLDeserializer xmlDeserializer = new XMLDeserializer();
				MessageProfile profile = new MessageProfile(xmlDeserializer.deserialize(event.getFile().getInputstream(), ProfileSchemaVersion.V29));
				Interaction interaction = new Interaction();
				Message message = new Message();
				message.setMessageProfile(profile);
				message.setName(profile.getName());
				interaction.setMessage(message);
				this.newTestStep.setInteraction(interaction);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addTestStep() throws CloneNotSupportedException {
		if(this.newTestStep.getType().equals("Manual")){
			this.newTestStep.setTargetActor(this.dbManager.getActorById(this.targetActorId));
		}else if (this.newTestStep.getType().equals("Message")){
			Interaction interaction = new Interaction();
			interaction.setMessage(this.dbManager.getMessageById(this.interactionId));
			this.newTestStep.setInteraction(interaction);
		}
		
		this.newTestStep.setTestStepStory(new TestStory());
		this.dbManager.testStepInsert(this.newTestStep, this.sessionBeanTCAMT.getLoggedId());
		this.sessionBeanTCAMT.updateTestSteps();
		
		this.interactionId = null;
		this.targetActorId = null;
	}
	
	public void shareTestStep(){
		this.dbManager.testStepInsert(this.editTestStep, this.shareTo);
		this.sessionBeanTCAMT.updateTestSteps();
	}
	
	public void addInteraction(){
		this.newTestStep.setInteraction(this.dbManager.getInteractionById(this.interactionId));
	}
	
	public void updateInteraction(){
		this.editTestStep.setInteraction(this.dbManager.getInteractionById(this.interactionId));
	}
	
	public void updateTestStep() throws CloneNotSupportedException{
		if(this.editTestStep.getType().equals("Manual")){
			this.editTestStep.setTargetActor(this.dbManager.getActorById(this.targetActorId));
		}
		
		this.editTestStep.setVersion(this.editTestStep.getVersion() + 1);
		this.dbManager.testStepUpdate(this.editTestStep);
		this.sessionBeanTCAMT.updateTestSteps();
		
		this.interactionId = null;
		this.targetActorId = null;
	}
	
	public void selectEditTestStep(ActionEvent event) {
		this.init();
		this.existTestStep = (TestStep) event.getComponent().getAttributes().get("teststep");
		this.editTestStep.setId(existTestStep.getId());
		this.editTestStep.setDescription(this.existTestStep.getDescription());
		this.editTestStep.setName(this.existTestStep.getName());
		this.editTestStep.setType(this.existTestStep.getType());
		this.editTestStep.setVersion(this.existTestStep.getVersion());
		this.editTestStep.setInteraction(this.existTestStep.getInteraction());
		this.editTestStep.setTestStepStory(this.existTestStep.getTestStepStory());
		this.editTestStep.setTargetActor(this.existTestStep.getTargetActor());

		if(!this.editTestStep.getType().equals("Manual")){
			this.selectedInstanceSegment = new InstanceSegment();
			this.messageTreeRoot = this.manageInstanceService.generateMessageTreeForElementOccur(this.editTestStep.getInteraction().getMessage());
			this.instanceSegments = new ArrayList<InstanceSegment>();
			this.readHL7Message();
		}
		
		this.interactionId = this.editTestStep.getInteraction().getId();
		this.targetActorId = this.editTestStep.getTargetActor().getId();
		
		this.shareTo = "";
	}
	
	public void reloadTestStep(ActionEvent event) throws CloneNotSupportedException {
		TestStep ts = (TestStep) event.getComponent().getAttributes().get("teststep");
		
		boolean isChanged = false;
		
		for(Interaction i: this.sessionBeanTCAMT.getInteractions()){
			if(i.getId().equals(ts.getInteraction().getId())){
				if(i.getVersion() != ts.getInteraction().getVersion()){
					ts.setInteraction((Interaction)i.clone());
					isChanged = true;
				}
			}
		}
		
		if(isChanged){
			ts.setVersion(ts.getVersion() + 1);
			this.dbManager.testStepUpdate(ts);
			this.sessionBeanTCAMT.updateTestSteps();
		}
		
	}
	
	public void createValidationContext(SegmentTreeModel stm){
		this.manageInstanceService.createVC(stm, this.editTestStep.getInteraction().getMessage(), "TestStep");
	}
	
	public void updateInstanceFieldOccur(SegmentTreeModel stm) throws NumberFormatException, CloneNotSupportedException{		
		this.toBeRepeatedModel = this.manageInstanceService.findOccurField(this.toBeDeletedTreeNodes, this.toBeRepeatedModel, this.selectedInstanceSegment.getSegmentTreeNode(), ((Field)stm.getNode()).getPosition(), stm.getOccurrence());
		this.manageInstanceService.adjustOccur(this.editTestStep.getInteraction().getMessage(), this.toBeDeletedTreeNodes, this.toBeRepeatedModel, this.selectedInstanceSegment, stm.getOccurrence());
		this.toBeRepeatedModel  = null;
		this.toBeDeletedTreeNodes = new ArrayList<TreeNode>();
	}
	
	public void updateInstanceData(SegmentTreeModel stm) {
		this.manageInstanceService.generateHL7Message(this.instanceSegments, this.editTestStep.getInteraction().getMessage());
	}

	public void saveTestStep() {
		this.editTestStep.setVersion(this.editTestStep.getVersion() + 1);
		this.dbManager.testStepUpdate(this.editTestStep);
		this.sessionBeanTCAMT.updateTestSteps();
	}
	
	public int vcSize(Object model){
		if(model instanceof SegmentTreeModel){
			SegmentTreeModel stm = (SegmentTreeModel)model;
			if(stm.getNode() instanceof Field){
				Field f = (Field)stm.getNode();
				int vcSize = 0;
				if(f.getConformanceStatementList() != null){
					vcSize += f.getConformanceStatementList().size();
				}
				if(f.getPredicate() != null){
					vcSize++;
				}
				
				return vcSize;
			}else if(stm.getNode() instanceof Component){
				Component c = (Component)stm.getNode();
				int vcSize = 0;
				if(c.getConformanceStatementList() != null){
					vcSize += c.getConformanceStatementList().size();
				}
				if(c.getPredicate() != null){
					vcSize++;
				}
				
				return vcSize;
			}	
		}else if(model instanceof MessageTreeModel){
			MessageTreeModel mtm = (MessageTreeModel)model;
			Element el = (Element)mtm.getNode();
			int vcSize = 0;
			if(el.getConformanceStatementList() != null){
				vcSize += el.getConformanceStatementList().size();
			}
			if(el.getPredicate() != null){
				vcSize++;
			}
			
			return vcSize;
			
		}
		return 0;
		
	}
	
	public void selectModel(ActionEvent event) {
		this.selectedModel = event.getComponent().getAttributes().get("model");
	}
	
	
	public void readHL7Message(){
		if(this.editTestStep.getInteraction().getMessage().getHl7EndcodedMessage() != null && !this.editTestStep.getInteraction().getMessage().getHl7EndcodedMessage().replaceAll("\\s","").equals("")){
			try {
				List<ValidationContext> newVC = new ArrayList<ValidationContext>();
				for(ValidationContext vc:this.editTestStep.getInteraction().getMessage().getValidationContexts()){
					if(!vc.getLevel().equals("Profile Fixed")) newVC.add(vc);
				}
				this.editTestStep.getInteraction().getMessage().setValidationContexts(newVC);
				
				
				this.instanceSegments = new ArrayList<InstanceSegment>();
				gov.nist.healthcare.core.hl7.v2.instance.Message hl7Message = this.manageInstanceService.readHL7Message(this.editTestStep.getInteraction().getMessage());
				TreeMap<Integer, List<gov.nist.healthcare.core.hl7.v2.instance.Element>> tmElements = hl7Message.getChildren();
				Set<Integer> keySet = tmElements.keySet();
				for(Integer i:keySet){
					List<gov.nist.healthcare.core.hl7.v2.instance.Element> childElms = tmElements.get(i);
					
					for(gov.nist.healthcare.core.hl7.v2.instance.Element childE : childElms){
						this.manageInstanceService.updateOccurDataByHL7Message(childE, "", "", this.editTestStep.getInteraction().getMessage(), this.messageTreeRoot, this.instanceSegments);
					}
				}
				
				this.manageInstanceService.updateInstanceSegmentsByTestDataTypeList(this.editTestStep.getInteraction().getMessage(), this.instanceSegments);
				this.selectedInstanceSegment = new InstanceSegment();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParserException e) {
				e.printStackTrace();
			}
		}
	}
	
	

	/**
	 * 
	 */

	public TreeNode getMessageTreeRoot() {
		return messageTreeRoot;
	}

	public void setMessageTreeRoot(TreeNode messageTreeRoot) {
		this.messageTreeRoot = messageTreeRoot;
	}
	
	public List<TreeNode> getToBeDeletedTreeNodes() {
		return toBeDeletedTreeNodes;
	}

	public void setToBeDeletedTreeNodes(List<TreeNode> toBeDeletedTreeNodes) {
		this.toBeDeletedTreeNodes = toBeDeletedTreeNodes;
	}
	
	public List<InstanceSegment> getInstanceSegments() {
		return instanceSegments;
	}

	public void setInstanceSegments(List<InstanceSegment> instanceSegments) {
		this.instanceSegments = instanceSegments;
	}

	public InstanceSegment getSelectedInstanceSegment() {
		return selectedInstanceSegment;
	}

	public void setSelectedInstanceSegment(InstanceSegment selectedInstanceSegment) {
		this.selectedInstanceSegment = selectedInstanceSegment;
	}

	public SegmentTreeModel getToBeRepeatedModel() {
		return toBeRepeatedModel;
	}

	public void setToBeRepeatedModel(SegmentTreeModel toBeRepeatedModel) {
		this.toBeRepeatedModel = toBeRepeatedModel;
	}

	public ManageInstance getManageInstanceService() {
		return manageInstanceService;
	}

	public void setManageInstanceService(ManageInstance manageInstanceService) {
		this.manageInstanceService = manageInstanceService;
	}

	public Object getSelectedModel() {
		return selectedModel;
	}

	public void setSelectedModel(Object selectedModel) {
		this.selectedModel = selectedModel;
	}

	public DBImpl getDbManager() {
		return dbManager;
	}

	public void setDbManager(DBImpl dbManager) {
		this.dbManager = dbManager;
	}

	public List<TestStep> getTestSteps(){
		return this.sessionBeanTCAMT.getTeststeps();
	}

	public List<TestStep> getTestStepsForIsolatedTestCase(){
		List<TestStep> allTestSteps = this.sessionBeanTCAMT.getTeststeps();
		List<TestStep> testStepsForisolatedTestCase = new ArrayList<TestStep>();
		for (TestStep ts:allTestSteps){
			if(ts.getType().equals("Manual") || ts.getType().equals("Interaction")){
				testStepsForisolatedTestCase.add(ts);
			}
		}
		return testStepsForisolatedTestCase;
	}
	
	public SessionBeanTCAMT getSessionBeanTCAMT() {
		return sessionBeanTCAMT;
	}

	public void setSessionBeanTCAMT(SessionBeanTCAMT sessionBeanTCAMT) {
		this.sessionBeanTCAMT = sessionBeanTCAMT;
	}

	public TestStep getNewTestStep() {
		return newTestStep;
	}

	public void setNewTestStep(TestStep newTestStep) {
		this.newTestStep = newTestStep;
	}

	public TestStep getEditTestStep() {
		return editTestStep;
	}

	public void setEditTestStep(TestStep editTestStep) {
		this.editTestStep = editTestStep;
	}

	public TestStep getExistTestStep() {
		return existTestStep;
	}

	public void setExistTestStep(TestStep existTestStep) {
		this.existTestStep = existTestStep;
	}

	public Integer getInteractionId() {
		return interactionId;
	}

	public void setInteractionId(Integer interactionId) {
		this.interactionId = interactionId;
	}

	public Integer getTargetActorId() {
		return targetActorId;
	}

	public void setTargetActorId(Integer targetActorId) {
		this.targetActorId = targetActorId;
	}

	public String getShareTo() {
		return shareTo;
	}

	public void setShareTo(String shareTo) {
		this.shareTo = shareTo;
	}

	public boolean isCanDelete() {
		return canDelete;
	}

	public void setCanDelete(boolean canDelete) {
		this.canDelete = canDelete;
	}
	
	
	
}
