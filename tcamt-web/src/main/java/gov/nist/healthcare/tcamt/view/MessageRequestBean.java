package gov.nist.healthcare.tcamt.view;

import gov.nist.healthcare.tcamt.domain.Message;
import gov.nist.healthcare.tcamt.domain.TCAMTConstraint;
import gov.nist.healthcare.tcamt.domain.data.ComponentModel;
import gov.nist.healthcare.tcamt.domain.data.FieldModel;
import gov.nist.healthcare.tcamt.domain.data.InstanceSegment;
import gov.nist.healthcare.tcamt.domain.data.MessageTreeModel;
import gov.nist.healthcare.tcamt.domain.data.SegmentTreeModel;
import gov.nist.healthcare.tcamt.domain.data.TestDataCategorization;
import gov.nist.healthcare.tcamt.service.ManageInstance;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;

import org.apache.commons.io.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
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
		this.newMessage = new Message();
		this.selectedSegmentTreeRoot = new DefaultTreeNode("root", null);
		this.selectedNode = null;
		this.editMessage = new Message();
		this.setActiveIndexOfMessageInstancePanel(1);
		this.existMessage = new Message();
		this.selectedInstanceSegment = null;
		this.segmentTreeRoot = new DefaultTreeNode("root", null);
		this.messageTreeRoot = new DefaultTreeNode("root", null);
		this.instanceSegments = new ArrayList<InstanceSegment>();
		this.manageInstanceService = new ManageInstance();
	}
	
	public void delMessage(ActionEvent event) {
		this.sessionBeanTCAMT.getDbManager().messageDelete((Message) event.getComponent().getAttributes().get("message"));
		this.sessionBeanTCAMT.updateMessages();
		this.init();
	}
	
	public void cloneMessage(ActionEvent event) throws CloneNotSupportedException {	
		Message m = (Message)((Message)event.getComponent().getAttributes().get("message")).clone();
		m.setId(0);
		m.setName("Copy_" + m.getName());
		m.setVersion(1);
		m.setAuthor(this.sessionBeanTCAMT.getLoggedUser());
		
		for(TCAMTConstraint c:m.getTcamtConstraints()){
			c.setId(0);
		}
		
		this.sessionBeanTCAMT.getDbManager().messageInsert(m);
		this.sessionBeanTCAMT.updateMessages();
		this.init();
	}
	
	public void createMessage() {
		this.newMessage = new Message();
	}
	
	public void addMessage() {
		this.newMessage.setAuthor(this.sessionBeanTCAMT.getLoggedUser());
		this.newMessage.setVersion(1);
		this.sessionBeanTCAMT.getDbManager().messageInsert(this.newMessage);
		this.sessionBeanTCAMT.updateMessages();
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
		this.editMessage.setTcamtConstraints(existMessage.getTcamtConstraints());
		this.editMessage.setHl7EndcodedMessage(existMessage.getHl7EndcodedMessage());
		this.editMessage.setAuthor(existMessage.getAuthor());
		this.messageTreeRoot = this.manageInstanceService.loadMessage(this.editMessage);
		this.instanceSegments = new ArrayList<InstanceSegment>();
		this.readHL7Message();
		this.shareTo = null;
		
		this.setActiveIndexOfMessageInstancePanel(2);
		this.sessionBeanTCAMT.setmActiveIndex(2);
	}
	
	private void readProfile(Message m) throws CloneNotSupportedException{
		if(m.getProfile() != null && m.getProfile().equals("") ){
			if(m.getValueSet() != null && m.getValueSet().equals("") ){
				if(m.getConstraints() != null && m.getConstraints().equals("") ){
					this.manageInstanceService.loadMessage(this.newMessage);
				}
			}
		}
	}
	
	public void uploadProfile(FileUploadEvent event) throws IOException, CloneNotSupportedException{
		this.newMessage.setProfile(IOUtils.toString(event.getFile().getInputstream(), "UTF-8"));
		this.readProfile(this.newMessage);
	}
	
	public void uploadConstraints(FileUploadEvent event) throws IOException, CloneNotSupportedException {
		this.newMessage.setConstraints(IOUtils.toString(event.getFile().getInputstream(), "UTF-8"));
		this.readProfile(this.newMessage);
	}
	
	public void uploadValueSet(FileUploadEvent event) throws IOException, CloneNotSupportedException {
		this.newMessage.setValueSet(IOUtils.toString(event.getFile().getInputstream(), "UTF-8"));
		this.readProfile(this.newMessage);
	}
	
	public void updateProfile(FileUploadEvent event) throws IOException, CloneNotSupportedException {
		this.editMessage.setProfile(IOUtils.toString(event.getFile().getInputstream(), "UTF-8"));
		this.readProfile(this.editMessage);
	}
	
	public void updateConstraints(FileUploadEvent event) throws IOException, CloneNotSupportedException {
		this.editMessage.setConstraints(IOUtils.toString(event.getFile().getInputstream(), "UTF-8"));
		this.readProfile(this.editMessage);
	}
	
	public void updateValueSet(FileUploadEvent event) throws IOException, CloneNotSupportedException {
		this.editMessage.setValueSet(IOUtils.toString(event.getFile().getInputstream(), "UTF-8"));
		this.readProfile(this.editMessage);
	}
	
	private void travelSegment(Segment s, String path, TreeNode parentNode) {
		List<Field> fields = s.getFields();
		for (Field f : fields) {
			travelField(f, path, parentNode);
		}
	}
	
	private void travelField(Field f, String path, TreeNode parentNode) {
		path = path + "." + f.getPosition();
		String segmentRootName = path.split("\\.")[0];
		
		Datatype dt = f.getDatatype();	
		List<Component> components = dt.getComponents();
		boolean isLeafNode = false;
		
		if(components == null || components.size() == 0){
			isLeafNode = true;
		}
		

		SegmentTreeModel segmentTreeModel = new SegmentTreeModel(segmentRootName, f.getName(), f, path, path, null, null, null, isLeafNode, f.getMin());
		TreeNode treeNode = new DefaultTreeNode(segmentTreeModel,  parentNode);

	
		travelDT(dt, path, treeNode);
	}

	private void travelDT(Datatype dt, String path, TreeNode parentNode) {
		List<Component> components = dt.getComponents();
		if (components == null) {
		} else {
			for (Component c : components) {
				String newPath = path + "." + c.getPosition();
				String segmentRootName = newPath.split("\\.")[0];
				
				Datatype childdt = c.getDatatype();	
				List<Component> childComponents = childdt.getComponents();
				boolean isLeafNode = false;
				
				if(childComponents == null || childComponents.size() == 0){
					isLeafNode = true;
				}

				SegmentTreeModel segmentTreeModel = new SegmentTreeModel(segmentRootName, c.getName(), c, newPath, newPath, null, null, null, isLeafNode, 1);
				TreeNode treeNode = (TreeNode) new DefaultTreeNode(segmentTreeModel, (org.primefaces.model.TreeNode) parentNode);

				travelDT(c.getDatatype(), newPath, treeNode);
			}
		}

	}
	
	public void onNodeSelect(NodeSelectEvent event) {
		SegmentRefOrGroup segmentRefOrGroup = (SegmentRefOrGroup)((MessageTreeModel) event.getTreeNode().getData()).getNode();

		if (segmentRefOrGroup != null) {
			this.selectedSegmentTreeRoot = (TreeNode) new DefaultTreeNode("root", null);
			
			if(segmentRefOrGroup instanceof SegmentRef){
				SegmentRef segmentRef = (SegmentRef)segmentRefOrGroup;
				this.travelSegment(segmentRef.getRef(), segmentRef.getRef().getName(), selectedSegmentTreeRoot);	
			}
		}
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

	public void createTCAMTConstraint(Object model) {
		String ipath = null;
		String data = null;
		String level = "Message";
		TestDataCategorization tdc = null;
		
		if(model instanceof FieldModel){
			FieldModel fModel = (FieldModel)model;
			ipath = fModel.getIpath();
			data = fModel.getData();
			tdc = fModel.getTdc();
		}else if(model instanceof ComponentModel){
			ComponentModel cModel = (ComponentModel)model;
			ipath = cModel.getIpath();
			data = cModel.getData();
			tdc = cModel.getTdc();
		}
		
		if(tdc == null || tdc.getValue().equals("")){
			this.editMessage.deleteTCAMTConstraintByIPath(ipath);
		}else{
			TCAMTConstraint tcamtConstraint = new TCAMTConstraint();
			tcamtConstraint.setCategorization(tdc);
			tcamtConstraint.setData(data);
			tcamtConstraint.setIpath(ipath);
			tcamtConstraint.setLevel(level);
			this.editMessage.addTCAMTConstraint(tcamtConstraint);
		}
	}
	
	public void deleteConstraint(String ipath){
		this.editMessage.deleteTCAMTConstraintByIPath(ipath);
		this.selectedInstanceSegment = null;
	}

	public void shareMessage() {
		this.editMessage.setAuthor(this.sessionBeanTCAMT.getDbManager().getUserById(this.shareTo));
		this.editMessage.setId(0);
		this.editMessage.setVersion(1);
		
		for(TCAMTConstraint c:editMessage.getTcamtConstraints()){
			c.setId(0);
		}
		this.sessionBeanTCAMT.getDbManager().messageInsert(this.editMessage);
		this.sessionBeanTCAMT.updateMessages();
	}

	public void saveMessage() {
		if(this.editMessage != null && this.editMessage.getVersion() != null){
			this.sessionBeanTCAMT.setmActiveIndex(0);
			this.editMessage.setVersion(this.editMessage.getVersion() + 1);
			this.sessionBeanTCAMT.getDbManager().messageUpdate(this.editMessage);
			this.sessionBeanTCAMT.updateMessages();	
		}
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
		this.manageInstanceService.genSegmentTree(this.segmentTreeRoot, this.selectedInstanceSegment, this.editMessage);
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
	
	
}
