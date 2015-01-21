package gov.nist.healthcare.tcamt.view;

import gov.nist.healthcare.core.hl7.v2.parser.ParserException;
import gov.nist.healthcare.hl7tools.domain.Component;
import gov.nist.healthcare.hl7tools.domain.Element;
import gov.nist.healthcare.hl7tools.domain.Field;
import gov.nist.healthcare.tcamt.db.DBImpl;
import gov.nist.healthcare.tcamt.domain.Actor;
import gov.nist.healthcare.tcamt.domain.InstanceSegment;
import gov.nist.healthcare.tcamt.domain.Interaction;
import gov.nist.healthcare.tcamt.domain.Message;
import gov.nist.healthcare.tcamt.domain.MessageTreeModel;
import gov.nist.healthcare.tcamt.domain.SegmentTreeModel;
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

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

@ManagedBean
@SessionScoped
public class InteractionRequestBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5405271712400762402L;

	private Interaction newInteraction;
	private Interaction editInteraction;
	private Interaction existInteraction;
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
	
	private Integer sActorId;
	private Integer rActorId;
	private Integer messageId;
	
	private String shareTo;

	/**
	 * 
	 */
	public void delInteraction(ActionEvent event) {
		this.dbManager.interactionDelete((Interaction) event.getComponent().getAttributes().get("interaction"));
		this.sessionBeanTCAMT.updateInteractions();
		this.init();
	}
	
	public void cloneInteraction(ActionEvent event) throws CloneNotSupportedException {	
		Interaction i = (Interaction)((Interaction)event.getComponent().getAttributes().get("interaction")).clone();
		i.setName("Copy_" + i.getName());
		i.setVersion(0);
		this.dbManager.interactionInsert(i, this.sessionBeanTCAMT.getLoggedId());
		this.sessionBeanTCAMT.updateInteractions();
		this.init();
	}
	
	public void delInstanceSegment(ActionEvent event) {
		int rowIndex = (Integer)event.getComponent().getAttributes().get("rowIndex");
		String[] lines = this.editInteraction.getMessage().getHl7EndcodedMessage().split(System.getProperty("line.separator"));
		String editedHl7EndcodedMessage = "";
		for(int i=0; i<lines.length;i++){
			if(i != rowIndex){
				editedHl7EndcodedMessage = editedHl7EndcodedMessage + lines[i] + System.getProperty("line.separator");
			}
		}
		this.editInteraction.getMessage().setHl7EndcodedMessage(editedHl7EndcodedMessage);
		this.readHL7Message();
	}

	public InteractionRequestBean() {
		super();
		this.init();
	}
	
	private void init(){
		this.newInteraction = new Interaction();
		this.editInteraction = new Interaction();
		this.existInteraction = new Interaction();
		this.selectedInstanceSegment = new InstanceSegment();
		this.messageTreeRoot = new DefaultTreeNode("root", null);
		this.instanceSegments = new ArrayList<InstanceSegment>();
		this.toBeDeletedTreeNodes = new ArrayList<TreeNode>();
		this.toBeRepeatedModel = null;
		this.manageInstanceService = new ManageInstance();
		this.selectedModel = null;
	}

	public void createInteraction() {
		this.newInteraction = new Interaction();
		this.sActorId = null;
		this.rActorId = null;
		this.messageId = null;
	}

	public void addInteraction() throws CloneNotSupportedException {
		this.newInteraction.setsActor(this.dbManager.getActorById(this.sActorId));
		this.newInteraction.setrActor(this.dbManager.getActorById(this.rActorId));
		this.newInteraction.setMessage(this.dbManager.getMessageById(this.messageId));
		
		this.dbManager.interactionInsert(this.newInteraction, this.sessionBeanTCAMT.getLoggedId());
		this.sessionBeanTCAMT.updateInteractions();
		
		this.sActorId = null;
		this.rActorId = null;
		this.messageId = null;
	}
	
	public void shareInteraction(){
		this.dbManager.interactionInsert(this.editInteraction, this.shareTo);
		this.sessionBeanTCAMT.updateInteractions();
	}
	
	public void updateInteraction() throws CloneNotSupportedException{
		this.editInteraction.setsActor(this.dbManager.getActorById(this.sActorId));
		this.editInteraction.setrActor(this.dbManager.getActorById(this.rActorId));
		this.editInteraction.setMessage(this.dbManager.getMessageById(this.messageId));
		
		this.readHL7Message();
		this.editInteraction.setVersion(this.editInteraction.getVersion() + 1);
		this.dbManager.interactionUpdate(this.editInteraction);
		this.sessionBeanTCAMT.updateInteractions();
		
		this.sActorId = null;
		this.rActorId = null;
		this.messageId = null;
	}
	
	public void selectEditInteraction(ActionEvent event) {
		this.init();
		this.existInteraction = (Interaction) event.getComponent().getAttributes().get("interaction");
		this.editInteraction.setId(existInteraction.getId());
		this.editInteraction.setName(existInteraction.getName());
		this.editInteraction.setDescription(existInteraction.getDescription());
		this.editInteraction.setVersion(existInteraction.getVersion());
		this.editInteraction.setMessage(existInteraction.getMessage());
		this.editInteraction.setsActor(existInteraction.getsActor());
		this.editInteraction.setrActor(existInteraction.getrActor());
		this.selectedInstanceSegment = new InstanceSegment();
		this.messageTreeRoot = this.manageInstanceService.generateMessageTreeForElementOccur(this.editInteraction.getMessage());
		this.instanceSegments = new ArrayList<InstanceSegment>();
		this.readHL7Message();
		
		this.rActorId = this.editInteraction.getrActor().getId();
		this.sActorId = this.editInteraction.getsActor().getId();
		this.messageId = this.editInteraction.getMessage().getId();
		
		this.shareTo = "";
	}
	
	public void reloadInteraction(ActionEvent event) throws CloneNotSupportedException {
		Interaction i = (Interaction) event.getComponent().getAttributes().get("interaction");
		
		boolean isChanged = false;
		
		for(Actor a: this.sessionBeanTCAMT.getActors()){
			if(a.getId().equals(i.getrActor().getId())){
				if(a.getVersion() != i.getrActor().getVersion()){
					i.setrActor((Actor)a.clone());
					isChanged = true;
				}
			}
			
			if(a.getId().equals(i.getsActor().getId())){
				if(a.getVersion() != i.getsActor().getVersion()){
					i.setsActor((Actor)a.clone());
					isChanged = true;
				}
			}
		}
		
		for(Message m: this.sessionBeanTCAMT.getMessages()){
			if(m.getId().equals(i.getMessage().getId())){
				if(m.getVersion() != i.getMessage().getVersion()){
					i.setMessage((Message)m.clone());
					isChanged = true;
				}
			}
		}
		
		if(isChanged){
			i.setVersion(i.getVersion() + 1);
			this.dbManager.interactionUpdate(i);
			this.sessionBeanTCAMT.updateInteractions();
		}
		
	}
	
	public void createValidationContext(SegmentTreeModel stm){
		this.manageInstanceService.createVC(stm, this.editInteraction.getMessage(), "Interaction");
	}
	
	public void updateInstanceFieldOccur(SegmentTreeModel stm) throws NumberFormatException, CloneNotSupportedException{		
		this.toBeRepeatedModel = this.manageInstanceService.findOccurField(this.toBeDeletedTreeNodes, this.toBeRepeatedModel, this.selectedInstanceSegment.getSegmentTreeNode(), ((Field)stm.getNode()).getPosition(), stm.getOccurrence());
		this.manageInstanceService.adjustOccur(this.editInteraction.getMessage(), this.toBeDeletedTreeNodes, this.toBeRepeatedModel, this.selectedInstanceSegment, stm.getOccurrence());
		this.toBeRepeatedModel  = null;
		this.toBeDeletedTreeNodes = new ArrayList<TreeNode>();
	}
	
	public void updateInstanceData(SegmentTreeModel stm) {
		this.manageInstanceService.generateHL7Message(this.instanceSegments, this.editInteraction.getMessage());
	}

	public void saveInteraction() {
		this.editInteraction.setVersion(this.editInteraction.getVersion() + 1);
		this.dbManager.interactionUpdate(this.editInteraction);
		this.sessionBeanTCAMT.updateInteractions();
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
		if(this.editInteraction.getMessage().getHl7EndcodedMessage() != null && !this.editInteraction.getMessage().getHl7EndcodedMessage().replaceAll("\\s","").equals("")){
			try {
				List<ValidationContext> newVC = new ArrayList<ValidationContext>();
				for(ValidationContext vc:this.editInteraction.getMessage().getValidationContexts()){
					if(!vc.getLevel().equals("Profile Fixed")) newVC.add(vc);
				}
				this.editInteraction.getMessage().setValidationContexts(newVC);
				
				
				this.instanceSegments = new ArrayList<InstanceSegment>();
				gov.nist.healthcare.core.hl7.v2.instance.Message hl7Message = this.manageInstanceService.readHL7Message(this.editInteraction.getMessage());
				TreeMap<Integer, List<gov.nist.healthcare.core.hl7.v2.instance.Element>> tmElements = hl7Message.getChildren();
				Set<Integer> keySet = tmElements.keySet();
				for(Integer i:keySet){
					List<gov.nist.healthcare.core.hl7.v2.instance.Element> childElms = tmElements.get(i);
					
					for(gov.nist.healthcare.core.hl7.v2.instance.Element childE : childElms){
						this.manageInstanceService.updateOccurDataByHL7Message(childE, "", "", this.editInteraction.getMessage(), this.messageTreeRoot, this.instanceSegments);
					}
				}
				
				this.manageInstanceService.updateInstanceSegmentsByTestDataTypeList(this.editInteraction.getMessage(), this.instanceSegments);
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
	
	public Interaction getNewInteraction() {
		return newInteraction;
	}

	public void setNewInteraction(Interaction newInteraction) {
		this.newInteraction = newInteraction;
	}

	public Interaction getEditInteraction() {
		return editInteraction;
	}

	public void setEditInteraction(Interaction editInteraction) {
		this.editInteraction = editInteraction;
	}

	public Interaction getExistInteraction() {
		return existInteraction;
	}

	public void setExistInteraction(Interaction existInteraction) {
		this.existInteraction = existInteraction;
	}

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

	public Integer getsActorId() {
		return sActorId;
	}

	public void setsActorId(Integer sActorId) {
		this.sActorId = sActorId;
	}

	public Integer getrActorId() {
		return rActorId;
	}

	public void setrActorId(Integer rActorId) {
		this.rActorId = rActorId;
	}

	public Integer getMessageId() {
		return messageId;
	}

	public void setMessageId(Integer messageId) {
		this.messageId = messageId;
	}
	
	public List<Interaction> getInteractions(){
		return this.sessionBeanTCAMT.getInteractions();
	}

	public SessionBeanTCAMT getSessionBeanTCAMT() {
		return sessionBeanTCAMT;
	}

	public void setSessionBeanTCAMT(SessionBeanTCAMT sessionBeanTCAMT) {
		this.sessionBeanTCAMT = sessionBeanTCAMT;
	}

	public String getShareTo() {
		return shareTo;
	}

	public void setShareTo(String shareTo) {
		this.shareTo = shareTo;
	}
	
	
	
}
