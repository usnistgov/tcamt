package gov.nist.healthcare.tcamt.view;

import gov.nist.healthcare.tcamt.domain.Message;
import gov.nist.healthcare.tcamt.domain.ValidationContext;
import gov.nist.healthcare.tcamt.domain.data.FieldModel;
import gov.nist.healthcare.tcamt.domain.data.InstanceSegment;
import gov.nist.healthcare.tcamt.domain.data.MessageTreeModel;
import gov.nist.healthcare.tcamt.domain.data.SegmentTreeModel;
import gov.nist.healthcare.tcamt.service.ManageInstance;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
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
	
	private Message editMessage;
	private Message existMessage;
	private InstanceSegment selectedInstanceSegment;
	private TreeNode segmentTreeRoot;
	private TreeNode messageTreeRoot;
	private List<InstanceSegment> instanceSegments;
	private ManageInstance manageInstanceService;
	private Long shareTo;
	
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
		this.editMessage = new Message();
		this.setActiveIndexOfMessageInstancePanel(1);
		this.existMessage = new Message();
		this.selectedInstanceSegment = null;
		this.segmentTreeRoot = new DefaultTreeNode("root", null);
		this.messageTreeRoot = new DefaultTreeNode("root", null);
		this.instanceSegments = new ArrayList<InstanceSegment>();
		this.manageInstanceService = new ManageInstance();
	}

	public void selectEditMessage(ActionEvent event) throws CloneNotSupportedException, IOException {
		this.init();
		this.existMessage = (Message) event.getComponent().getAttributes().get("message");
		this.editMessage.setId(existMessage.getId());
		this.editMessage.setName(existMessage.getName());
		this.editMessage.setDescription(existMessage.getDescription());
		this.editMessage.setVersion(existMessage.getVersion());
		this.editMessage.setProfile(existMessage.getProfile());
		this.editMessage.setMessageObj(existMessage.getMessageObj());
		this.editMessage.setConstraints(existMessage.getConstraints());
		this.editMessage.setValueSet(existMessage.getValueSet());
		this.editMessage.setValidationContexts(existMessage.getValidationContexts());
		this.editMessage.setHl7EndcodedMessage(existMessage.getHl7EndcodedMessage());
		this.editMessage.setAuthor(existMessage.getAuthor());
		this.manageInstanceService.loadMessage(this.editMessage);
		this.instanceSegments = new ArrayList<InstanceSegment>();
		this.readHL7Message();
		this.shareTo = null;
		
		this.messageTreeRoot = this.manageInstanceService.loadMessage(this.editMessage);
		
		this.setActiveIndexOfMessageInstancePanel(2);
	}

	public void delInstanceSegment(ActionEvent event) throws CloneNotSupportedException, IOException {
		int rowIndex = (Integer)event.getComponent().getAttributes().get("rowIndex");
		String[] lines = this.editMessage.getHl7EndcodedMessage().split(System.getProperty("line.separator"));
		String editedHl7EndcodedMessage = "";
		for(int i=0; i<lines.length;i++){
			if(i != rowIndex){
				editedHl7EndcodedMessage = editedHl7EndcodedMessage + lines[i] + System.getProperty("line.separator");
			}
		}
		this.editMessage.setHl7EndcodedMessage(editedHl7EndcodedMessage);
		this.readHL7Message();
	}

	public void createValidationContext(SegmentTreeModel stm) {
//		this.manageInstanceService.createVC(stm, this.editMessage, "Message");
	}

	public void shareMessage() {
		this.editMessage.setAuthor(this.sessionBeanTCAMT.getDbManager().getUserById(this.shareTo));
		this.editMessage.setId(0);
		this.editMessage.setVersion(1);
		
		for(ValidationContext vc:editMessage.getValidationContexts()){
			vc.setId(0);
		}
		this.sessionBeanTCAMT.getDbManager().messageInsert(this.editMessage);
		this.sessionBeanTCAMT.updateMessages();
	}

	public void saveMessage() {
		this.editMessage.setVersion(this.editMessage.getVersion() + 1);
		this.sessionBeanTCAMT.getDbManager().messageUpdate(this.editMessage);
		this.sessionBeanTCAMT.updateMessages();
	}

	public void updateInstanceData(Object model) throws CloneNotSupportedException, IOException {
		int lineNum = this.instanceSegments.indexOf(this.selectedInstanceSegment);
		this.manageInstanceService.updateHL7Message(lineNum, this.manageInstanceService.generateLineStr(this.segmentTreeRoot), this.editMessage);
		this.readHL7Message();
		this.selectedInstanceSegment = this.instanceSegments.get(lineNum);
		this.activeIndexOfMessageInstancePanel = 3;
	}
	
	public void onInstanceSegmentSelect(SelectEvent event){
		this.segmentTreeRoot = new DefaultTreeNode("root", null);
		this.manageInstanceService.genSegmentTree(this.segmentTreeRoot, this.selectedInstanceSegment);
		this.activeIndexOfMessageInstancePanel = 3;
	}
	
	public void readHL7Message() throws CloneNotSupportedException, IOException{
		this.instanceSegments = new ArrayList<InstanceSegment>();
		if(this.editMessage.getHl7EndcodedMessage() != null && !this.editMessage.getHl7EndcodedMessage().equals("")){
			this.manageInstanceService.loadMessageInstance(this.editMessage, this.instanceSegments);
			this.selectedInstanceSegment = null;
		}
		this.activeIndexOfMessageInstancePanel = 2;
	}
	
	public void addRepeatedField(FieldModel fieldModel){
		this.manageInstanceService.addRepeatedField(fieldModel, this.segmentTreeRoot);
	}
	
	public void addNode(){
		
		TreeNode parent = this.selectedNode.getParent();
		
		int position = parent.getChildren().indexOf(this.selectedNode);
		
		MessageTreeModel model = (MessageTreeModel)this.selectedNode.getData();
		MessageTreeModel newModel = new MessageTreeModel(model.getMessageId(),model.getName(), model.getNode(), model.getPath(), model.getOccurrence());	
		TreeNode newNode = new DefaultTreeNode(((SegmentRefOrGroup)newModel.getNode()).getMax(), newModel, parent);
		
		this.manageInstanceService.populateTreeNode(newNode);
		
		parent.getChildren().add(position + 1, newNode);
	}

	public void genrateHL7Message() throws CloneNotSupportedException, IOException{
		this.editMessage.setHl7EndcodedMessage(this.manageInstanceService.generateHL7Message(this.messageTreeRoot));
		this.readHL7Message();
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

	public Message getExistMessage() {
		return existMessage;
	}

	public void setExistMessage(Message existMessage) {
		this.existMessage = existMessage;
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

	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}
	
	
}
