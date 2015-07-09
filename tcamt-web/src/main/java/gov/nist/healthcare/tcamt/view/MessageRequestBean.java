package gov.nist.healthcare.tcamt.view;

import gov.nist.healthcare.tcamt.domain.Log;
import gov.nist.healthcare.tcamt.domain.Message;
import gov.nist.healthcare.tcamt.domain.TCAMTConstraint;
import gov.nist.healthcare.tcamt.domain.data.ComponentModel;
import gov.nist.healthcare.tcamt.domain.data.FieldModel;
import gov.nist.healthcare.tcamt.domain.data.InstanceSegment;
import gov.nist.healthcare.tcamt.domain.data.MessageTreeModel;
import gov.nist.healthcare.tcamt.domain.data.TestDataCategorization;
import gov.nist.healthcare.tcamt.service.ManageInstance;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

@ManagedBean
@SessionScoped
public class MessageRequestBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3147168726958584964L;
	
	private Message newMessage;
	private TreeNode selectedSegmentTreeRoot;
	
	private Message editMessage;
	private InstanceSegment selectedInstanceSegment;
	private TreeNode segmentTreeRoot;
	private TreeNode messageTreeRoot;
	private TreeNode constraintTreeRoot;
	private List<InstanceSegment> instanceSegments;
	private ManageInstance manageInstanceService;
	private Long shareTo;
	private Long conformanceProfileId;
	
	private String usageViewOption;
	private String usageViewOption2;
	private List<InstanceSegment> filteredInstanceSegments;
	private TreeNode filtedSegmentTreeRoot;
	
	private TreeNode selectedNode;
	
	private int activeIndexOfMessageInstancePanel;
	
	@ManagedProperty("#{sessionBeanTCAMT}")
	private SessionBeanTCAMT sessionBeanTCAMT;

	/**
	 * 
	 */
	public MessageRequestBean() {
		super();
		this.init();
	}
	
	private void init(){
		this.newMessage = new Message();
		this.selectedSegmentTreeRoot = new DefaultTreeNode("root", null);
		this.selectedNode = null;
		this.editMessage = new Message();
		this.setActiveIndexOfMessageInstancePanel(1);
		this.selectedInstanceSegment = null;
		this.segmentTreeRoot = new DefaultTreeNode("root", null);
		this.messageTreeRoot = new DefaultTreeNode("root", null);
		this.setConstraintTreeRoot(new DefaultTreeNode("root", null));
		this.instanceSegments = new ArrayList<InstanceSegment>();
		this.manageInstanceService = new ManageInstance();
		this.usageViewOption = "partial";
		this.usageViewOption2 = "partial";
		this.filteredInstanceSegments = new ArrayList<InstanceSegment>();
		this.filtedSegmentTreeRoot = new DefaultTreeNode("root", null);
		
	}
	
	public void delMessage(ActionEvent event) {
		try{
			String deletedMessageName = ((Message) event.getComponent().getAttributes().get("message")).getName();
			this.sessionBeanTCAMT.getDbManager().messageDelete((Message) event.getComponent().getAttributes().get("message"));
			this.sessionBeanTCAMT.updateMessages();
			
			FacesContext context = FacesContext.getCurrentInstance();
	        context.addMessage(null, new FacesMessage("Message Deleted.",  "Message: " + deletedMessageName + " has been created.") );
			
			this.init();
		}catch(Exception e){
			Log log = new Log(e.getMessage(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}
	
	public void cloneMessage(ActionEvent event) {
		try{
			Message m = (Message)((Message)event.getComponent().getAttributes().get("message")).clone();
			m.setName("Copy_" + m.getName());
			m.setVersion(1);
			this.sessionBeanTCAMT.getDbManager().messageInsert(m);
			this.sessionBeanTCAMT.updateMessages();
			
			
			this.editMessage = m;
			this.messageTreeRoot = this.manageInstanceService.loadMessage(this.editMessage);
			this.constraintTreeRoot = this.manageInstanceService.generateConstraintTree(this.editMessage);
			this.instanceSegments = new ArrayList<InstanceSegment>();
			this.readHL7Message();
			this.shareTo = null;
			
			this.setActiveIndexOfMessageInstancePanel(2);
			this.sessionBeanTCAMT.setmActiveIndex(1);
			FacesContext context = FacesContext.getCurrentInstance();
	        context.addMessage(null, new FacesMessage("Message Cloned.",  "Message: " + this.editMessage.getName() + " has been created.") );	
		}catch(Exception e){
			Log log = new Log(e.getMessage(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}
	
	public void createMessage() {
		try{
			this.newMessage = new Message();
			this.conformanceProfileId = null;	
		}catch(Exception e){
			Log log = new Log(e.getMessage(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}
	
	public void addMessage() {
		try{
			this.newMessage.setAuthor(this.sessionBeanTCAMT.getLoggedUser());
			this.newMessage.setConformanceProfile(this.sessionBeanTCAMT.getDbManager().getConformanceProfileById(this.conformanceProfileId));
			this.newMessage.setVersion(1);
			this.sessionBeanTCAMT.getDbManager().messageInsert(this.newMessage);
			this.sessionBeanTCAMT.updateMessages();
			
			this.editMessage = newMessage;
			this.messageTreeRoot = this.manageInstanceService.loadMessage(this.editMessage);
			this.constraintTreeRoot = this.manageInstanceService.generateConstraintTree(this.editMessage);
			this.instanceSegments = new ArrayList<InstanceSegment>();
			this.readHL7Message();
			this.shareTo = null;
			
			this.setActiveIndexOfMessageInstancePanel(2);
			this.sessionBeanTCAMT.setmActiveIndex(1);
			FacesContext context = FacesContext.getCurrentInstance();
	        context.addMessage(null, new FacesMessage("Message Created.",  "Message: " + this.editMessage.getName() + " has been created.") );			
		}catch(Exception e){
			Log log = new Log(e.getMessage(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}
	
	public void profileUpdateMessage() {
		try{
			this.setActiveIndexOfMessageInstancePanel(1);
			this.sessionBeanTCAMT.setmActiveIndex(1);
			this.messageTreeRoot = this.manageInstanceService.loadMessage(this.editMessage);
			this.constraintTreeRoot = this.manageInstanceService.generateConstraintTree(this.editMessage);
			this.instanceSegments = new ArrayList<InstanceSegment>();
			this.readHL7Message();			
		}catch(Exception e){
			Log log = new Log(e.getMessage(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}

	public void selectEditMessage(ActionEvent event) {
		try{
			this.init();
			this.editMessage = (Message) event.getComponent().getAttributes().get("message");
			this.editMessage = this.sessionBeanTCAMT.getDbManager().getMessageById(this.editMessage.getId());
			if(this.editMessage.getConformanceProfile() == null) this.conformanceProfileId = null;
			else this.conformanceProfileId = this.editMessage.getConformanceProfile().getId();
			
			this.messageTreeRoot = this.manageInstanceService.loadMessage(this.editMessage);
			this.constraintTreeRoot = this.manageInstanceService.generateConstraintTree(this.editMessage);
			this.instanceSegments = new ArrayList<InstanceSegment>();
			this.readHL7Message();
			this.shareTo = null;
			
			this.setActiveIndexOfMessageInstancePanel(2);
			this.sessionBeanTCAMT.setmActiveIndex(1);			
		}catch(Exception e){
			Log log = new Log(e.getMessage(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}
	
	public void delInstanceSegment(ActionEvent event) {
		try{
			InstanceSegment toBeDeletedInstanceSegment = (InstanceSegment)event.getComponent().getAttributes().get("instanceSegment");
			
			int rowIndex = this.instanceSegments.indexOf(toBeDeletedInstanceSegment);
			String[] lines = this.editMessage.getHl7EndcodedMessage().split(System.getProperty("line.separator"));
			String editedHl7EndcodedMessage = "";
			for(int i=0; i<lines.length;i++){
				if(i != rowIndex){
					editedHl7EndcodedMessage = editedHl7EndcodedMessage + lines[i] + System.getProperty("line.separator");
				}
			}
			this.editMessage.setHl7EndcodedMessage(editedHl7EndcodedMessage);
			this.readHL7Message();			
		}catch(Exception e){
			Log log = new Log(e.getMessage(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}

	public void createTCAMTConstraint(Object model) {
		try{
			String ipath = null;
			String data = null;
			String level = "Message";
			String iPosition = null;
			String messageName = null;
			String usageList = null;
			TestDataCategorization tdc = null;
			
			if(model instanceof FieldModel){
				FieldModel fModel = (FieldModel)model;
				ipath = fModel.getIpath();
				data = fModel.getData();
				tdc = fModel.getTdc();
				iPosition = fModel.getiPositionPath();
				messageName = fModel.getMessageName();
				usageList = fModel.getUsageList();
			}else if(model instanceof ComponentModel){
				ComponentModel cModel = (ComponentModel)model;
				ipath = cModel.getIpath();
				data = cModel.getData();
				tdc = cModel.getTdc();
				iPosition = cModel.getiPositionPath();
				messageName = cModel.getMessageName();
				usageList = cModel.getUsageList();
			}
			this.editMessage.deleteTCAMTConstraintByIPath(ipath);
			if(tdc != null && !tdc.getValue().equals("")){
				TCAMTConstraint tcamtConstraint = new TCAMTConstraint();
				tcamtConstraint.setCategorization(tdc);
				tcamtConstraint.setData(data);
				tcamtConstraint.setIpath(ipath);
				tcamtConstraint.setLevel(level);
				tcamtConstraint.setiPosition(iPosition);
				tcamtConstraint.setMessageName(messageName);
				tcamtConstraint.setUsageList(usageList);
				this.editMessage.addTCAMTConstraint(tcamtConstraint);
			}
			
			this.constraintTreeRoot = this.manageInstanceService.generateConstraintTree(this.editMessage);	
		}catch(Exception e){
			Log log = new Log(e.getMessage(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}
	
	public void deleteConstraint(String ipath){
		try{
			this.editMessage.deleteTCAMTConstraintByIPath(ipath);
			this.selectedInstanceSegment = null;
			this.segmentTreeRoot = new DefaultTreeNode("root", null);
			
			this.constraintTreeRoot = this.manageInstanceService.generateConstraintTree(this.editMessage);			
		}catch(Exception e){
			Log log = new Log(e.getMessage(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}

	public void shareMessage() {
		try{
			this.editMessage = this.editMessage.clone();
			this.editMessage.setAuthor(this.sessionBeanTCAMT.getDbManager().getUserById(this.shareTo));
			this.editMessage.setVersion(1);
			this.sessionBeanTCAMT.getDbManager().messageInsert(this.editMessage);
			this.sessionBeanTCAMT.updateMessages();
			
			FacesContext context = FacesContext.getCurrentInstance();
	        context.addMessage(null, new FacesMessage("Message sharing.",  "Message: " + this.editMessage.getName() + " has been sented to " + this.editMessage.getAuthor().getUserId()) );
	        
	        this.init();
	        
	        this.sessionBeanTCAMT.setmActiveIndex(0);			
		}catch(Exception e){
			Log log = new Log(e.getMessage(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}

	public void saveMessage() {
		try{
			if(this.editMessage != null && this.editMessage.getVersion() != null){
				FacesContext context = FacesContext.getCurrentInstance();
		        context.addMessage(null, new FacesMessage("Message Saved.",  "Message: " + this.editMessage.getName() + " has been saved.") );
				this.editMessage.setVersion(this.editMessage.getVersion() + 1);
				this.editMessage.setConformanceProfile(this.sessionBeanTCAMT.getDbManager().getConformanceProfileById(this.conformanceProfileId));
				this.sessionBeanTCAMT.getDbManager().messageUpdate(this.editMessage);
				this.sessionBeanTCAMT.updateMessages();	
			}			
		}catch(Exception e){
			Log log = new Log(e.getMessage(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}

	public void updateInstanceData(Object model) {
		try{
			int lineNum = this.instanceSegments.indexOf(this.selectedInstanceSegment);
			this.manageInstanceService.updateHL7Message(lineNum, this.manageInstanceService.generateLineStr(this.segmentTreeRoot), this.editMessage);
			this.readHL7Message();
			this.selectedInstanceSegment = this.instanceSegments.get(lineNum);
			this.activeIndexOfMessageInstancePanel = 3;			
		}catch(Exception e){
			Log log = new Log(e.getMessage(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}
	
	public void onInstanceSegmentSelect(SelectEvent event){
		try{
			this.segmentTreeRoot = new DefaultTreeNode("root", null);
			this.manageInstanceService.genSegmentTree(this.segmentTreeRoot, this.selectedInstanceSegment, this.editMessage);
			
			this.updateFilteredSegmentTree();
			
			this.activeIndexOfMessageInstancePanel = 3;			
		}catch(Exception e){
			Log log = new Log(e.getMessage(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}
	
	public void readHL7Message() throws Exception{
		try{
			this.instanceSegments = new ArrayList<InstanceSegment>();
			if(this.editMessage.getHl7EndcodedMessage() != null &&  this.editMessage.getConformanceProfile() != null && !this.editMessage.getHl7EndcodedMessage().equals("")){
				this.manageInstanceService.loadMessageInstance(this.editMessage, this.instanceSegments);
				this.selectedInstanceSegment = null;
			}
			this.activeIndexOfMessageInstancePanel = 2;
			this.updateFilteredInstanceSegments();	
		}catch(Exception e){
			Log log = new Log(e.getMessage(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}
	
	public void addRepeatedField(FieldModel fieldModel){
		try{
			this.manageInstanceService.addRepeatedField(fieldModel, this.segmentTreeRoot, this.editMessage);
			this.updateFilteredSegmentTree();	
		}catch(Exception e){
			Log log = new Log(e.getMessage(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}
	
	public void addNode(){
		try{
			TreeNode parent = this.selectedNode.getParent();
			
			int position = parent.getChildren().indexOf(this.selectedNode);
			
			MessageTreeModel model = (MessageTreeModel)this.selectedNode.getData();
			MessageTreeModel newModel = new MessageTreeModel(model.getMessageId(),model.getName(), model.getNode(), model.getPath(), model.getOccurrence());	
			TreeNode newNode = new DefaultTreeNode(((SegmentRefOrGroup)newModel.getNode()).getMax(), newModel, parent);
			
			this.manageInstanceService.populateTreeNode(newNode, this.editMessage);
			
			parent.getChildren().add(position + 1, newNode);			
		}catch(Exception e){
			Log log = new Log(e.getMessage(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}

	public void genrateHL7Message(){
		try{
			this.editMessage.setHl7EndcodedMessage(this.manageInstanceService.generateHL7Message(this.messageTreeRoot, this.editMessage));
			this.readHL7Message();			
		}catch(Exception e){
			Log log = new Log(e.getMessage(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}
	
	public void updateFilteredInstanceSegments() {
		try{
			this.filteredInstanceSegments = new ArrayList<InstanceSegment>();
			
			if(this.usageViewOption.equals("all")){
				this.filteredInstanceSegments = this.instanceSegments;
			}else{
				for(InstanceSegment is:this.instanceSegments){
					String[] usageList = is.getUsageList().split("-");
					boolean usageCheck = true;
					
		        	for(String u:usageList){
		        		if(!u.equals("R") && !u.equals("RE") && !u.equals("C")){
		        			usageCheck = false;
		        		}
		        	}
		        	
		        	if(usageCheck) this.filteredInstanceSegments.add(is);
				}
			}			
		}catch(Exception e){
			Log log = new Log(e.getMessage(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
    }
	
	public void updateFilteredSegmentTree(){
		try{
			if(this.usageViewOption2.equals("all")){
				this.filtedSegmentTreeRoot = this.segmentTreeRoot;
			}else {
				this.filtedSegmentTreeRoot = this.manageInstanceService.genRestrictedTree(this.segmentTreeRoot);
			}
		}catch(Exception e){
			Log log = new Log(e.getMessage(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}
	
	private String getStackTrace(final Throwable throwable) {
	     final StringWriter sw = new StringWriter();
	     final PrintWriter pw = new PrintWriter(sw, true);
	     throwable.printStackTrace(pw);
	     return sw.getBuffer().toString();
	}
	
	/**
	 * 
	 */

	public List<InstanceSegment> getInstanceSegments() {
		return instanceSegments;
	}

	public void setInstanceSegments(List<InstanceSegment> instanceSegments) {
		this.instanceSegments = instanceSegments;
	}

	public InstanceSegment getSelectedInstanceSegment() {
		return selectedInstanceSegment;
	}

	public void setSelectedInstanceSegment(
			InstanceSegment selectedInstanceSegment) {
		this.selectedInstanceSegment = selectedInstanceSegment;
	}
	
	public ManageInstance getManageInstanceService() {
		return manageInstanceService;
	}

	public void setManageInstanceService(ManageInstance manageInstanceService) {
		this.manageInstanceService = manageInstanceService;
	}

	public Message getEditMessage() {
		return editMessage;
	}

	public void setEditMessage(Message editMessage) {
		this.editMessage = editMessage;
	}

	public TreeNode getSegmentTreeRoot() {
		return segmentTreeRoot;
	}

	public void setSegmentTreeRoot(TreeNode segmentTreeRoot) {
		this.segmentTreeRoot = segmentTreeRoot;
	}

	public SessionBeanTCAMT getSessionBeanTCAMT() {
		return sessionBeanTCAMT;
	}

	public void setSessionBeanTCAMT(SessionBeanTCAMT sessionBeanTCAMT) {
		this.sessionBeanTCAMT = sessionBeanTCAMT;
	}

	public Long getShareTo() {
		return shareTo;
	}

	public void setShareTo(Long shareTo) {
		this.shareTo = shareTo;
	}

	public int getActiveIndexOfMessageInstancePanel() {
		return activeIndexOfMessageInstancePanel;
	}

	public void setActiveIndexOfMessageInstancePanel(
			int activeIndexOfMessageInstancePanel) {
		this.activeIndexOfMessageInstancePanel = activeIndexOfMessageInstancePanel;
	}

	public TreeNode getMessageTreeRoot() {
		return messageTreeRoot;
	}

	public void setMessageTreeRoot(TreeNode messageTreeRoot) {
		this.messageTreeRoot = messageTreeRoot;
	}

	public Message getNewMessage() {
		return newMessage;
	}

	public void setNewMessage(Message newMessage) {
		this.newMessage = newMessage;
	}

	public TreeNode getSelectedSegmentTreeRoot() {
		return selectedSegmentTreeRoot;
	}

	public void setSelectedSegmentTreeRoot(TreeNode selectedSegmentTreeRoot) {
		this.selectedSegmentTreeRoot = selectedSegmentTreeRoot;
	}

	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}

	public List<Message> getMessages(){
		return this.sessionBeanTCAMT.getMessages();
	}

	public TreeNode getConstraintTreeRoot() {
		return constraintTreeRoot;
	}

	public void setConstraintTreeRoot(TreeNode constraintTreeRoot) {
		this.constraintTreeRoot = constraintTreeRoot;
	}

	public String getUsageViewOption() {
		return usageViewOption;
	}

	public void setUsageViewOption(String usageViewOption) {
		this.usageViewOption = usageViewOption;
	}

	public List<InstanceSegment> getFilteredInstanceSegments() {
		return filteredInstanceSegments;
	}

	public void setFilteredInstanceSegments(List<InstanceSegment> filteredInstanceSegments) {
		this.filteredInstanceSegments = filteredInstanceSegments;
	}

	public String getUsageViewOption2() {
		return usageViewOption2;
	}

	public void setUsageViewOption2(String usageViewOption2) {
		this.usageViewOption2 = usageViewOption2;
	}

	public TreeNode getFiltedSegmentTreeRoot() {
		return filtedSegmentTreeRoot;
	}

	public void setFiltedSegmentTreeRoot(TreeNode filtedSegmentTreeRoot) {
		this.filtedSegmentTreeRoot = filtedSegmentTreeRoot;
	}

	public Long getConformanceProfileId() {
		return conformanceProfileId;
	}

	public void setConformanceProfileId(Long conformanceProfileId) {
		this.conformanceProfileId = conformanceProfileId;
	}
	
	
}
