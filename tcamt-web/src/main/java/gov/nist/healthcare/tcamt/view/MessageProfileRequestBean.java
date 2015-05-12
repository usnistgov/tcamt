package gov.nist.healthcare.tcamt.view;

import gov.nist.healthcare.tcamt.domain.Message;
import gov.nist.healthcare.tcamt.domain.ValidationContext;
import gov.nist.healthcare.tcamt.domain.data.MessageTreeModel;
import gov.nist.healthcare.tcamt.domain.data.SegmentTreeModel;
import gov.nist.healthcare.tcamt.service.ManageInstance;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;

import org.apache.commons.io.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

@ManagedBean
@SessionScoped
public class MessageProfileRequestBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 432938680402529031L;
	
	@ManagedProperty("#{sessionBeanTCAMT}")
	private SessionBeanTCAMT sessionBeanTCAMT;
	
	private Message newMessage;
	private Message editMessage;
	private Message existMessage;
	private TreeNode selectedSegmentTreeRoot;
	private TreeNode selectedMessageElementRoot;
	private ManageInstance manageInstanceService;
	private TreeNode selectedNode;

	/**
	 * 
	 */
	
	public MessageProfileRequestBean() {
		super();
		this.init();
	}

	private void init(){
		this.newMessage = new Message();
		this.editMessage = new Message();
		this.existMessage = new Message();
		this.selectedSegmentTreeRoot = new DefaultTreeNode("root", null);
		this.selectedMessageElementRoot = new DefaultTreeNode("root", null);
		this.manageInstanceService = new ManageInstance();
		this.selectedNode = null;
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
		
		for(ValidationContext vc:m.getValidationContexts()){
			vc.setId(0);
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

	public void editMessage() {
		this.editMessage.setVersion(this.editMessage.getVersion() + 1);
		this.sessionBeanTCAMT.getDbManager().messageUpdate(this.editMessage);
		this.sessionBeanTCAMT.updateMessages();
	}

	public void selectEditMessage(ActionEvent event) throws CloneNotSupportedException {
		this.init();
		this.editMessage = new Message();
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
		
		this.selectedMessageElementRoot = this.manageInstanceService.loadMessage(this.editMessage);
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

	/**
	 * 
	 */

	public Message getNewMessage() {
		return newMessage;
	}

	public void setNewMessage(Message newMessage) {
		this.newMessage = newMessage;
	}

	public Message getEditMessage() {
		return editMessage;
	}

	public void setEditMessage(Message editMessage) {
		this.editMessage = editMessage;
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

	public TreeNode getSelectedMessageElementRoot() {
		return selectedMessageElementRoot;
	}

	public void setSelectedMessageElementRoot(TreeNode selectedMessageElementRoot) {
		this.selectedMessageElementRoot = selectedMessageElementRoot;
	}
	
	public List<Message> getMessages(){
		return this.sessionBeanTCAMT.getMessages();
	}

	public SessionBeanTCAMT getSessionBeanTCAMT() {
		return sessionBeanTCAMT;
	}

	public void setSessionBeanTCAMT(SessionBeanTCAMT sessionBeanTCAMT) {
		this.sessionBeanTCAMT = sessionBeanTCAMT;
	}
	
	
}
