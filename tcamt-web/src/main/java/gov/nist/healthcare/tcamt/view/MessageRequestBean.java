package gov.nist.healthcare.tcamt.view;

import gov.nist.healthcare.core.hl7.v2.parser.ParserException;
import gov.nist.healthcare.hl7tools.domain.Component;
import gov.nist.healthcare.hl7tools.domain.Element;
import gov.nist.healthcare.hl7tools.domain.Field;
import gov.nist.healthcare.tcamt.db.DBImpl;
import gov.nist.healthcare.tcamt.domain.InstanceSegment;
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
public class MessageRequestBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3147168726958584964L;
	
	private Message editMessage;
	private Message existMessage;
	private InstanceSegment selectedInstanceSegment;
	private TreeNode messageTreeRoot;
	private List<InstanceSegment> instanceSegments;
	private List<TreeNode> toBeDeletedTreeNodes;
	private SegmentTreeModel toBeRepeatedModel;
	private ManageInstance manageInstanceService;
	private Object selectedModel;
	private String shareTo;
	
	private DBImpl dbManager = new DBImpl();
	
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
		this.existMessage = new Message();
		this.selectedInstanceSegment = new InstanceSegment();
		this.messageTreeRoot = new DefaultTreeNode("root", null);
		this.instanceSegments = new ArrayList<InstanceSegment>();
		this.toBeDeletedTreeNodes = new ArrayList<TreeNode>();
		this.toBeRepeatedModel = null;
		this.manageInstanceService = new ManageInstance();
		this.selectedModel = null;
	}

	public void selectEditMessage(ActionEvent event) {
		this.init();
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
		this.messageTreeRoot = this.manageInstanceService.generateMessageTreeForElementOccur(this.editMessage);
		this.instanceSegments = new ArrayList<InstanceSegment>();
		this.readHL7Message();
		this.shareTo = "";
	}

	public void delInstanceSegment(ActionEvent event) {
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
		this.manageInstanceService.createVC(stm, this.editMessage, "Message");
	}

	public void shareMessage() {
		this.dbManager.messageInsert(this.editMessage, this.shareTo);
		this.sessionBeanTCAMT.updateMessages();
	}

	public void saveMessage() {
		this.editMessage.setVersion(this.editMessage.getVersion() + 1);
		this.dbManager.messageUpdate(this.editMessage);
		this.sessionBeanTCAMT.updateMessages();
	}

	public void updateInstanceFieldOccur(SegmentTreeModel stm)
			throws NumberFormatException, CloneNotSupportedException {
		this.toBeRepeatedModel = this.manageInstanceService.findOccurField(
				this.toBeDeletedTreeNodes, this.toBeRepeatedModel,
				this.selectedInstanceSegment.getSegmentTreeNode(),
				((Field) stm.getNode()).getPosition(), stm.getOccurrence());
		this.manageInstanceService.adjustOccur(this.editMessage,
				this.toBeDeletedTreeNodes, this.toBeRepeatedModel,
				this.selectedInstanceSegment, stm.getOccurrence());
		this.toBeRepeatedModel = null;
		this.toBeDeletedTreeNodes = new ArrayList<TreeNode>();
	}

	public void updateInstanceData(SegmentTreeModel stm) {
		this.manageInstanceService.generateHL7Message(this.instanceSegments, this.editMessage);
	}
	
	public void readHL7Message(){
		if(this.editMessage.getHl7EndcodedMessage() != null && !this.editMessage.getHl7EndcodedMessage().equals("")){
			try {
				List<ValidationContext> newVC = new ArrayList<ValidationContext>();
				for(ValidationContext vc:this.editMessage.getValidationContexts()){
					if(!vc.getLevel().equals("Profile Fixed")) newVC.add(vc);
				}
				this.editMessage.setValidationContexts(newVC);
				
				
				
				this.instanceSegments = new ArrayList<InstanceSegment>();
				gov.nist.healthcare.core.hl7.v2.instance.Message hl7Message = this.manageInstanceService.readHL7Message(this.editMessage);
				
				TreeMap<Integer, List<gov.nist.healthcare.core.hl7.v2.instance.Element>> tmElements = hl7Message.getChildren();
				Set<Integer> keySet = tmElements.keySet();
				for(Integer i:keySet){
					List<gov.nist.healthcare.core.hl7.v2.instance.Element> childElms = tmElements.get(i);
					for(gov.nist.healthcare.core.hl7.v2.instance.Element childE : childElms){
						this.manageInstanceService.updateOccurDataByHL7Message(childE, "", "", this.editMessage, this.messageTreeRoot, this.instanceSegments);
					}
				}
				
				this.manageInstanceService.updateInstanceSegmentsByTestDataTypeList(this.editMessage, this.instanceSegments);
				this.selectedInstanceSegment = new InstanceSegment();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParserException e) {
				e.printStackTrace();
			}
		}
	}
	
	public int vcSize(Object model) {
		if (model instanceof SegmentTreeModel) {
			SegmentTreeModel stm = (SegmentTreeModel) model;
			if (stm.getNode() instanceof Field) {
				Field f = (Field) stm.getNode();
				int vcSize = 0;
				if (f.getConformanceStatementList() != null) {
					vcSize += f.getConformanceStatementList().size();
				}
				if (f.getPredicate() != null) {
					vcSize++;
				}

				return vcSize;
			} else if (stm.getNode() instanceof Component) {
				Component c = (Component) stm.getNode();
				int vcSize = 0;
				if (c.getConformanceStatementList() != null) {
					vcSize += c.getConformanceStatementList().size();
				}
				if (c.getPredicate() != null) {
					vcSize++;
				}

				return vcSize;
			}
		} else if (model instanceof MessageTreeModel) {
			MessageTreeModel mtm = (MessageTreeModel) model;
			Element el = (Element) mtm.getNode();
			int vcSize = 0;
			if (el.getConformanceStatementList() != null) {
				vcSize += el.getConformanceStatementList().size();
			}
			if (el.getPredicate() != null) {
				vcSize++;
			}

			return vcSize;

		}
		return 0;

	}

	public void selectModel(ActionEvent event) {
		this.selectedModel = event.getComponent().getAttributes().get("model");
	}

	/**
	 * 
	 */
	
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

	public void setSelectedInstanceSegment(
			InstanceSegment selectedInstanceSegment) {
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

	public TreeNode getMessageTreeRoot() {
		return messageTreeRoot;
	}

	public void setMessageTreeRoot(TreeNode messageTreeRoot) {
		this.messageTreeRoot = messageTreeRoot;
	}

	public DBImpl getDbManager() {
		return dbManager;
	}

	public void setDbManager(DBImpl dbManager) {
		this.dbManager = dbManager;
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
