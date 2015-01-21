package gov.nist.healthcare.tcamt.view;

import gov.nist.healthcare.hl7tools.domain.Component;
import gov.nist.healthcare.hl7tools.domain.ConformanceStatement;
import gov.nist.healthcare.hl7tools.domain.Datatype;
import gov.nist.healthcare.hl7tools.domain.Element;
import gov.nist.healthcare.hl7tools.domain.Field;
import gov.nist.healthcare.hl7tools.domain.Segment;
import gov.nist.healthcare.hl7tools.domain.StatementDetails;
import gov.nist.healthcare.hl7tools.service.serializer.ProfileSchemaVersion;
import gov.nist.healthcare.hl7tools.service.serializer.XMLDeserializer;
import gov.nist.healthcare.hl7tools.v2.maker.core.ConversionException;
import gov.nist.healthcare.hl7tools.v2.maker.core.domain.profile.MessageProfile;
import gov.nist.healthcare.hl7tools.v2.profilemaker.service.JSONConverterService;
import gov.nist.healthcare.tcamt.db.DBImpl;
import gov.nist.healthcare.tcamt.domain.Message;
import gov.nist.healthcare.tcamt.domain.MessageTreeModel;
import gov.nist.healthcare.tcamt.domain.SegmentTreeModel;
import gov.nist.healthcare.tcamt.domain.TestDataCategorization;
import gov.nist.healthcare.tcamt.service.ManageInstance;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;

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
	
	private DBImpl dbManager = new DBImpl();

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
		this.dbManager.messageDelete((Message) event.getComponent().getAttributes().get("message"));
		this.sessionBeanTCAMT.updateMessages();
		this.init();
	}
	
	public void cloneMessage(ActionEvent event) throws CloneNotSupportedException {	
		Message m = (Message)((Message)event.getComponent().getAttributes().get("message")).clone();
		m.setName("Copy_" + m.getName());
		m.setVersion(0);
		this.dbManager.messageInsert(m, this.sessionBeanTCAMT.getLoggedId());
		this.sessionBeanTCAMT.updateMessages();
		this.init();
	}

	public void createMessage() {
		this.newMessage = new Message();
	}

	public void addMessage() {
		this.dbManager.messageInsert(this.newMessage, this.sessionBeanTCAMT.getLoggedId());
		this.sessionBeanTCAMT.updateMessages();
	}

	public void editMessage() {
		this.editMessage.setVersion(this.editMessage.getVersion() + 1);
		this.dbManager.messageUpdate(this.editMessage);
		this.sessionBeanTCAMT.updateMessages();
	}

	public void selectEditMessage(ActionEvent event) {
		this.init();
		this.editMessage = new Message();
		this.existMessage = (Message) event.getComponent().getAttributes().get("message");		
		this.editMessage.setId(existMessage.getId());
		this.editMessage.setName(existMessage.getName());
		this.editMessage.setDescription(existMessage.getDescription());
		this.editMessage.setVersion(existMessage.getVersion());
		this.editMessage.setMessageProfile(existMessage.getMessageProfile());
		this.editMessage.setListCSs(existMessage.getListCSs());
		this.editMessage.setListCPs(existMessage.getListCPs());
		this.editMessage.setProfilePathOccurIGData(existMessage.getProfilePathOccurIGData());
		this.editMessage.setInstanceTestDataTypes(existMessage.getInstanceTestDataTypes());
		this.editMessage.setValidationContexts(existMessage.getValidationContexts());
		this.editMessage.setHl7EndcodedMessage(existMessage.getHl7EndcodedMessage());
		
		this.selectedMessageElementRoot = this.manageInstanceService.loadMessage(this.editMessage, 1);
	}

	public void uploadMessageProfile(FileUploadEvent event)
			throws ConversionException {
		try {
			if(event.getFile().getFileName().endsWith(".PROFILE")){
				JSONConverterService jConverterService = new JSONConverterService();
				MessageProfile profile = jConverterService.fromStream(event.getFile().getInputstream());
				this.newMessage.setMessageProfile(profile);
				this.manageInstanceService.loadMessage(this.newMessage, 0);
			}else if (event.getFile().getFileName().endsWith(".xml")){
				XMLDeserializer xmlDeserializer = new XMLDeserializer();
				MessageProfile profile = new MessageProfile(xmlDeserializer.deserialize(event.getFile().getInputstream(), ProfileSchemaVersion.V29));
				this.newMessage.setMessageProfile(profile);
				this.manageInstanceService.loadMessage(this.newMessage, 0);
			}
			
			this.manageInstanceService.generateProfilePathOccurIGData(this.newMessage);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updateMessageProfile(FileUploadEvent event)
			throws ConversionException {
		try {
			JSONConverterService jConverterService = new JSONConverterService();
			MessageProfile profile = jConverterService.fromStream(event.getFile().getInputstream());
			this.editMessage.setMessageProfile(profile);
			
			this.manageInstanceService.loadMessage(this.editMessage, 0);
			this.manageInstanceService.generateProfilePathOccurIGData(this.editMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		
		String data = null;
		TestDataCategorization type = null;
		List<ConformanceStatement> css = f.getConformanceStatementList();
		if (css != null && css.size() > 0) {
			for (ConformanceStatement cs : css) {
				StatementDetails statementDetails = (StatementDetails) cs.getStatementDetails();
				if (statementDetails.getPattern().equals("Constant Value Check")) {
					if(statementDetails.getSubPattern().equals("Single Value")) {
						if(!statementDetails.getVerb().contains("not") && !statementDetails.getVerb().contains("NOT")){
							data = statementDetails.getLiteralValue();
							type = TestDataCategorization.ProfileFixed; 
						}
					} 
				}
			}
		}
		Datatype dt = f.getDatatype();	
		List<Component> components = dt.getComponents();
		boolean isLeafNode = false;
		
		if(components == null || components.size() == 0){
			isLeafNode = true;
		}
		

		SegmentTreeModel segmentTreeModel = new SegmentTreeModel(segmentRootName, f.getDescription(), f, path, path, data, type, null, isLeafNode, f.getMin());
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
				
				String data = null;
				TestDataCategorization type = null;
				List<ConformanceStatement> css = c.getConformanceStatementList();
				if (css != null && css.size() > 0) {
					for (ConformanceStatement cs : css) {
						StatementDetails statementDetails = (StatementDetails) cs.getStatementDetails();
						if (statementDetails.getPattern().equals("Constant Value Check")) {
							if(statementDetails.getSubPattern().equals("Single Value")) {
								if(!statementDetails.getVerb().contains("not") && !statementDetails.getVerb().contains("NOT")){
									data = statementDetails.getLiteralValue();
									type = TestDataCategorization.ProfileFixed; 
								}
							} 
						}
					}
				}
				
				
				Datatype childdt = c.getDatatype();	
				List<Component> childComponents = childdt.getComponents();
				boolean isLeafNode = false;
				
				if(childComponents == null || childComponents.size() == 0){
					isLeafNode = true;
				}

				SegmentTreeModel segmentTreeModel = new SegmentTreeModel(segmentRootName, c.getDescription(), c, newPath, newPath, data, type, null, isLeafNode, 1);
				TreeNode treeNode = (TreeNode) new DefaultTreeNode(segmentTreeModel, (org.primefaces.model.TreeNode) parentNode);

				travelDT(c.getDatatype(), newPath, treeNode);
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

	public void onNodeSelect(NodeSelectEvent event) {
		Element el = (Element)((MessageTreeModel) event.getTreeNode().getData()).getNode();

		if (el != null) {
			Segment segment = el.getSegment();
			this.selectedSegmentTreeRoot = (TreeNode) new DefaultTreeNode("root", null);
			if(segment != null){
				this.travelSegment(segment, segment.getName(), selectedSegmentTreeRoot);	
			}
		}
	}

	public TreeNode getSelectedMessageElementRoot() {
		return selectedMessageElementRoot;
	}

	public void setSelectedMessageElementRoot(TreeNode selectedMessageElementRoot) {
		this.selectedMessageElementRoot = selectedMessageElementRoot;
	}

	public DBImpl getDbManager() {
		return dbManager;
	}

	public void setDbManager(DBImpl dbManager) {
		this.dbManager = dbManager;
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
