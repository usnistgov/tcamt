package gov.nist.healthcare.tcamt.service;

import gov.nist.healthcare.core.hl7.v2.parser.Parser;
import gov.nist.healthcare.core.hl7.v2.parser.ParserException;
import gov.nist.healthcare.core.hl7.v2.parser.ParserImpl;
import gov.nist.healthcare.hl7tools.domain.Component;
import gov.nist.healthcare.hl7tools.domain.ConformanceStatement;
import gov.nist.healthcare.hl7tools.domain.Datatype;
import gov.nist.healthcare.hl7tools.domain.Element;
import gov.nist.healthcare.hl7tools.domain.Field;
import gov.nist.healthcare.hl7tools.domain.MetaData;
import gov.nist.healthcare.hl7tools.domain.Predicate;
import gov.nist.healthcare.hl7tools.domain.Segment;
import gov.nist.healthcare.hl7tools.domain.StatementDetails;
import gov.nist.healthcare.hl7tools.domain.Usage;
import gov.nist.healthcare.hl7tools.service.serializer.ProfileSchemaVersion;
import gov.nist.healthcare.hl7tools.service.serializer.XMLSerializer;
import gov.nist.healthcare.hl7tools.v2.maker.core.domain.profile.MessageProfile;
import gov.nist.healthcare.tcamt.domain.InstanceSegment;
import gov.nist.healthcare.tcamt.domain.InstanceTestDataType;
import gov.nist.healthcare.tcamt.domain.Message;
import gov.nist.healthcare.tcamt.domain.MessageTreeModel;
import gov.nist.healthcare.tcamt.domain.ProfilePathOccurIGData;
import gov.nist.healthcare.tcamt.domain.SegmentTreeModel;
import gov.nist.healthcare.tcamt.domain.TestDataCategorization;
import gov.nist.healthcare.tcamt.domain.ValidationContext;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import nu.xom.Document;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

public class ManageInstance  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 574095903906379334L;
	private String cSeperator = "^";
	private String scSeperator = "&";
	
	public TreeNode generateMessageTreeForElementOccur(Message message){
		TreeNode treeNode = new DefaultTreeNode("root", null);
		
		MessageProfile mp = message.getMessageProfile();
		String path = "";
		travelMessageForElementOccur(mp, path, treeNode, message);
		
		return treeNode;
		
	}
	
	public SegmentTreeModel findOccurField(List<TreeNode> toBeDeletedTreeNodes, SegmentTreeModel toBeRepeatedModel, TreeNode tn, int fieldNumber, int occur) throws CloneNotSupportedException{
		for(TreeNode t: tn.getChildren()){
			SegmentTreeModel stm = (SegmentTreeModel)t.getData();
			if(((Field)stm.getNode()).getPosition() == fieldNumber){
				toBeDeletedTreeNodes.add(t);
				
				if(toBeRepeatedModel == null) {
					toBeRepeatedModel = (SegmentTreeModel)((SegmentTreeModel)t.getData()).clone();
					toBeRepeatedModel.setOccurrence(occur);
				}
			}
		}
		return toBeRepeatedModel;
	}
	
	public void adjustOccur(Message message, List<TreeNode> toBeDeletedTreeNodes, SegmentTreeModel toBeRepeatedModel, InstanceSegment selectedInstanceSegment, int occur) throws CloneNotSupportedException{
		int index=0;
		for(TreeNode tn:toBeDeletedTreeNodes){
			index = selectedInstanceSegment.getSegmentTreeNode().getChildren().indexOf(tn);
			tn.getChildren().clear();
			tn.getParent().getChildren().remove(tn);
			
		}
		toBeDeletedTreeNodes = new ArrayList<TreeNode>();
		
		this.createRepeatedFieldByOccur(message, toBeRepeatedModel, occur, index, selectedInstanceSegment.getSegmentTreeNode());

	}
	
	public void generateProfilePathOccurIGData(Message message){
		message.setProfilePathOccurIGData(new ArrayList<ProfilePathOccurIGData>());
		MessageProfile mp = message.getMessageProfile();
		String path = "";
		travelMessageForProfilePathOccurIGData(mp, path, message);
		
	}
	
	public gov.nist.healthcare.core.hl7.v2.instance.Message readHL7Message(Message message) throws IOException, ParserException{
		XMLSerializer xmlSerializer = new XMLSerializer();
		MetaData metaData = message.getMessageProfile().getMetaData();
		String schemaV = message.getMessageProfile().getMetaData().getHl7RulesSchema();
		schemaV = schemaV.replace(".", "");
		ProfileSchemaVersion schemaVersion = ProfileSchemaVersion.valueOf("V" + schemaV);
		nu.xom.Element result = xmlSerializer.serialize(message.getMessageProfile().getMessage(), metaData.getHl7Version(),	schemaVersion);
		Parser parser = new ParserImpl();
		return parser.parse(message.getHl7EndcodedMessage(), new Document(result).toXML());
	}
	
	public void generateHL7Message(List<InstanceSegment> instanceSegments, Message message) {
		String messageString = "";
		String fieldSeparator ="|";
		String componentSeparator ="^";
		String subComponentSeparator ="&";
		String fieldRepeatSeparator ="~";
		String escapeCharacter ="\\";

		for (InstanceSegment is : instanceSegments) {
			String segmentString = is.getPath().substring(is.getPath().lastIndexOf(".") + 1,is.getPath().lastIndexOf("["));
			TreeNode segmentTN = is.getSegmentTreeNode();

			
			
			String fieldPath = "";
			for (TreeNode fieldTN : segmentTN.getChildren()) {
				SegmentTreeModel fModel = (SegmentTreeModel) fieldTN.getData();
				String fieldData;
				if(fModel.getData() != null && fModel.getData().contains("::")){
					fieldData = fModel.getData().split("::")[0];
				}else {
					fieldData = fModel.getData();
				}
				
				
				boolean isRepeat = (fModel.getPath().equals(fieldPath)) ? true:false;
				
				fieldPath = fModel.getPath();

				if (fieldTN.getChildCount() == 0) {
					if (fModel.getData() == null) {
						if(fModel.getName().equals("Field Separator")){
							if(fModel.getData() != null && !fModel.getData().equals("")){
								fieldSeparator = fModel.getData();
							}
							segmentString = segmentString + fieldSeparator;
						}else if (fModel.getName().equals("Encoding Characters")){
							if(fModel.getData() != null && !fModel.getData().equals("") && fModel.getData().length() ==4){
								String encodigCharacters = fModel.getData();
								componentSeparator = encodigCharacters.substring(0, 1);
								subComponentSeparator = encodigCharacters.substring(3, 4);
								fieldRepeatSeparator = encodigCharacters.substring(1, 2);
								escapeCharacter = encodigCharacters.substring(2, 3);
							}
							segmentString = segmentString + componentSeparator + fieldRepeatSeparator + escapeCharacter + subComponentSeparator;
						}else {
							segmentString = segmentString + ((isRepeat) ? fieldRepeatSeparator:fieldSeparator);
						}
						
					} else {
						if(fModel.getName().equals("Field Separator")){
							if(fModel.getData() != null && !fModel.getData().equals("")){
								fieldSeparator = fModel.getData();
							}
							segmentString = segmentString + fieldSeparator;
						}else if (fModel.getName().equals("Encoding Characters")){
							if(fModel.getData() != null && !fModel.getData().equals("") && fModel.getData().length() ==4){
								String encodigCharacters = fModel.getData();
								componentSeparator = encodigCharacters.substring(0, 1);
								subComponentSeparator = encodigCharacters.substring(3, 4);
								fieldRepeatSeparator = encodigCharacters.substring(1, 2);
								escapeCharacter = encodigCharacters.substring(2, 3);
							}
							segmentString = segmentString + componentSeparator + fieldRepeatSeparator + escapeCharacter + subComponentSeparator;
						}else {
							segmentString = segmentString + ((isRepeat) ? fieldRepeatSeparator:fieldSeparator) + fieldData;
						}
					}

				} else {
					segmentString = segmentString + ((isRepeat) ? fieldRepeatSeparator:fieldSeparator);
					int indexC = 0;
					if (fModel.getOccurrence() == 0) {

					} else {

						for (TreeNode componentTN : fieldTN.getChildren()) {
							indexC = indexC + 1;
							SegmentTreeModel cModel = (SegmentTreeModel) componentTN.getData();

							String cData;
							if(cModel.getData() != null && cModel.getData().contains("::")){
								cData = cModel.getData().split("::")[0];
							}else {
								cData = cModel.getData();
							}
							
							if (componentTN.getChildCount() == 0) {
								if (cModel.getData() == null) {
									if (indexC == 1) {
									} else {
										segmentString = segmentString + componentSeparator;
									}

								} else {
									if (indexC == 1) {
										segmentString = segmentString + cData;
									} else {
										segmentString = segmentString + componentSeparator + cData;
									}
								}

							} else {
									if (indexC == 1) {
										int indexS = 0;
										for (TreeNode sComponentTN : componentTN.getChildren()) {
											indexS = indexS + 1;
											SegmentTreeModel sModel = (SegmentTreeModel) sComponentTN.getData();

											String sData;
											if(sModel.getData() != null && sModel.getData().contains("::")){
												sData = sModel.getData().split("::")[0];
											}else {
												sData = sModel.getData();
											}
											
											if (sModel.getData() == null) {
												if (indexS == 1) {
												} else {
													segmentString = segmentString + subComponentSeparator;
												}

											} else {
												if (indexS == 1) {
													segmentString = segmentString + sData;
												} else {
													segmentString = segmentString + subComponentSeparator + sData;
												}
											}
										}
										segmentString = this.removeSeperator(segmentString, subComponentSeparator);
									} else {
										segmentString = segmentString + componentSeparator;
										int indexS = 0;
										for (TreeNode sComponentTN : componentTN.getChildren()) {
											indexS = indexS + 1;
											SegmentTreeModel sModel = (SegmentTreeModel) sComponentTN.getData();

											String sData;
											if(sModel.getData() != null && sModel.getData().contains("::")){
												sData = sModel.getData().split("::")[0];
											}else {
												sData = sModel.getData();
											}
											
											if (sModel.getData() == null) {
												if (indexS == 1) {
												} else {
													segmentString = segmentString + subComponentSeparator;
												}

											} else {
												if (indexS == 1) {
													segmentString = segmentString + sData;
												} else {
													segmentString = segmentString + subComponentSeparator + sData;
												}
											}
										}
										segmentString = this.removeSeperator(segmentString, subComponentSeparator);
									}
							}
						}
						
						segmentString = this.removeSeperator(segmentString, componentSeparator);
					}
				}
			}

			messageString = messageString + segmentString + System.lineSeparator();
		}
		message.setHl7EndcodedMessage(messageString);
	}
	
	public void updateOccurDataByHL7Message(gov.nist.healthcare.core.hl7.v2.instance.Element e, String iPath, String path, Message message, TreeNode messageTreeRoot, List<InstanceSegment> iss) throws CloneNotSupportedException{
		if(e.getName().contains(".")){
			if(path.equals("")){
				iPath = e.getName().substring(e.getName().lastIndexOf(".") + 1) + "[" + e.getInstanceNumber() + "]";
				path = e.getName().substring(e.getName().lastIndexOf(".") + 1);
			}else{
				iPath = iPath + "." + e.getName().substring(e.getName().lastIndexOf(".") + 1) + "[" + e.getInstanceNumber() + "]";
				path = path + "." + e.getName().substring(e.getName().lastIndexOf(".") + 1);
			}
		}else {
			if(path.equals("")){
				iPath = e.getName() + "[" + e.getInstanceNumber() + "]";
				path = e.getName();
			}else{
				iPath = iPath + "." + e.getName() + "[" + e.getInstanceNumber() + "]";
				path = path + "." + e.getName();
			}
		}
		
		if(e.getElementType().equals(gov.nist.healthcare.core.hl7.v2.enumeration.ElementType.SEGMENT)){			
			TreeNode segmentTreeNode = new DefaultTreeNode("root", null);
			this.travelSegment(message, this.findSegmentInMessage(messageTreeRoot, path), iPath, path, segmentTreeNode);
			InstanceSegment instanceSegment = new InstanceSegment(iPath, segmentTreeNode);
			iss.add(instanceSegment);
			updateDataAndFieldOccur(e , iPath, path, iss);
			
		}else if(e.getElementType().equals(gov.nist.healthcare.core.hl7.v2.enumeration.ElementType.GROUP)){
			TreeMap<Integer, List<gov.nist.healthcare.core.hl7.v2.instance.Element>> tmElements = e.getChildren();
			Set<Integer> keySet = tmElements.keySet();
			for(Integer i:keySet){
				List<gov.nist.healthcare.core.hl7.v2.instance.Element> childElms = tmElements.get(i);
				
				for(gov.nist.healthcare.core.hl7.v2.instance.Element childE : childElms){
					updateOccurDataByHL7Message(childE, iPath, path, message, messageTreeRoot, iss);
				}
			}
		}
	}
	
	public void updateFieldData(String fieldStr, String cSeperator, String scSeperator, TreeNode tn){
		if(fieldStr == null){
			((SegmentTreeModel)tn.getData()).setData(null);
			for(TreeNode child:tn.getChildren()){
				((SegmentTreeModel)child.getData()).setData(null);
				((SegmentTreeModel)child.getData()).setOccurrence(0);
				for(TreeNode childchild:child.getChildren()){
					((SegmentTreeModel)childchild.getData()).setData(null);
					((SegmentTreeModel)childchild.getData()).setOccurrence(0);
				}
			}
		}else {
			if(fieldStr.contains(cSeperator)){
				String[] cStrs = fieldStr.split("\\" +  cSeperator);
				for(int i=0;i<cStrs.length; i++){
					if(cStrs[i].contains(scSeperator)){
						String[] scStrs = cStrs[i].split("\\" + scSeperator);
						for(int j=0;j<scStrs.length; j++){
							((SegmentTreeModel)tn.getChildren().get(i).getChildren().get(j).getData()).setData(scStrs[j]);
							((SegmentTreeModel)tn.getChildren().get(i).getChildren().get(j).getData()).setOccurrence(1);
						}
					}else{
						if(tn.getChildren().get(i).getChildCount() == 0){
							((SegmentTreeModel)tn.getChildren().get(i).getData()).setData(cStrs[i]);
							((SegmentTreeModel)tn.getChildren().get(i).getData()).setOccurrence(1);
						}else {
							((SegmentTreeModel)tn.getChildren().get(i).getChildren().get(0).getData()).setData(cStrs[i]);
							((SegmentTreeModel)tn.getChildren().get(i).getChildren().get(0).getData()).setOccurrence(1);
						}
					}
				}
			}else{
				if(tn.getChildCount() == 0){
					((SegmentTreeModel)tn.getData()).setData(fieldStr);
				}else if(tn.getChildren().get(0).getChildCount() == 0){
					((SegmentTreeModel)tn.getChildren().get(0).getData()).setData(fieldStr);
					((SegmentTreeModel)tn.getChildren().get(0).getData()).setOccurrence(1);;
				}else{
					((SegmentTreeModel)tn.getChildren().get(0).getChildren().get(0).getData()).setData(fieldStr);
					((SegmentTreeModel)tn.getChildren().get(0).getChildren().get(0).getData()).setOccurrence(1);;
				}
			}
		}
		
		
	}
	
	public TreeNode loadMessage(Message m, int option) throws CloneNotSupportedException {
		List<Element> elements = m.getMessageProfile().getChildren();
		String path = m.getName();
		if (path == null || path.equals("")) {
			path = "NONAME";
		}

		TreeNode treeNode = new DefaultTreeNode("root", null);

		if(option == 0){
			m.setListCPs(new ArrayList<Predicate>());
			m.setListCSs(new ArrayList<ConformanceStatement>());
		}
		
		for (Element e : elements) {
			loadElement(m, e, path, treeNode, option);
		}
		
		return treeNode;
	}
	
	public void createVC(SegmentTreeModel stm, Message m, String level) {
		String iPath = stm.getiPath();
		String data = stm.getData();
		TestDataCategorization type = stm.getType();
		Usage usage = Usage.NA;
		String nodeName = "";
		if (stm.getNode() instanceof Field) {
			usage = ((Field) stm.getNode()).getUsage();
			nodeName = ((Field) stm.getNode()).getDescription();
		} else if (stm.getNode() instanceof Component) {
			usage = ((Component) stm.getNode()).getUsage();
			nodeName = ((Component) stm.getNode()).getDescription();
		}

		this.removeExistVC(iPath, m);
		m.getInstanceTestDataTypes().add(new InstanceTestDataType(iPath, type));
		if (type==null) {
			
		} else if (type.equals(TestDataCategorization.Indifferent)) {
			
		} else if (type.equals(TestDataCategorization.ContentIndifferent)) {
			if (usage.equals(Usage.RE)) {
				this.createRVC(iPath, nodeName, m, level);
			}
		} else if (type.equals(TestDataCategorization.Configurable)) {
			if (usage.equals(Usage.RE)) {
				this.createRVC(iPath, nodeName, m, level);
			}
		} else if (type.equals(TestDataCategorization.SystemGenerated)) {
			if (usage.equals(Usage.RE)) {
				this.createRVC(iPath, nodeName, m, level);
			}
		} else if (type.equals(TestDataCategorization.TestCaseProper)) {
			if (usage.equals(Usage.RE)) {
				this.createRVC(iPath, nodeName, m, level);
			}
		} else if (type.equals(TestDataCategorization.ProfileFixedPresence)) {
			if (usage.equals(Usage.RE)) {
				this.createRVC(iPath, nodeName, m, level);
			}
		} else if (type.equals(TestDataCategorization.NotValued)) {
			if (usage.equals(Usage.RE)) {
				this.createNotRVC(iPath, nodeName, m, level);
			}
		} else if (type.equals(TestDataCategorization.ProfileFixed)) {
			
		} else if (type.equals(TestDataCategorization.ProfileFixedList)) {
			
		} else if (type.equals(TestDataCategorization.TestCaseFixed)) {
			if (usage.equals(Usage.RE)) {
				this.createRVC(iPath, nodeName, m, level);
				this.createRVVC(iPath, nodeName, data, m, level);
			} else if (usage.equals(Usage.R)) {
				this.createRVVC(iPath, nodeName, data, m, level);
			}
		} else if (type.equals(TestDataCategorization.TestCaseFixedList)) {
			if (usage.equals(Usage.RE)) {
				this.createRVC(iPath, nodeName, m, level);
				this.createListRVVC(iPath, nodeName, data, m, level);
			} else if (usage.equals(Usage.R)) {
				this.createListRVVC(iPath, nodeName, data, m, level);
			}
		} else if (type.equals(TestDataCategorization.TestCaseFixedLength)) {
			
		}
	}
	
	public void updateInstanceSegmentsByTestDataTypeList(Message m, List<InstanceSegment> instanceSegments) {
		for(InstanceTestDataType itdt:m.getInstanceTestDataTypes()){
			InstanceSegment is = this.findInstanceSegmentByIPath(itdt.getiPath(), instanceSegments);
			if(is != null) this.updateInstanceSegmentByITDT(itdt, is, m.getValidationContexts());
		}
	}
	
	private void travelMessageForElementOccur(MessageProfile mp, String path, TreeNode parent, Message message){
		List<Element> elements = mp.getChildren();
		for (Element e : elements) {
			travelElementForElementOccur(e, path, parent, message);
		}
	}
	
	private void travelElementForElementOccur(Element el, String path, TreeNode parent, Message message) {
		if(path.equals("")){
			path = el.getShortName();
		}else{
			path = path + "." + el.getShortName();
		}
		
		
		MessageTreeModel messageTreeModel = new MessageTreeModel(message.getName(), el.getShortName(), el, path, findOccurByPath(path, message));
		TreeNode treeNode = new DefaultTreeNode(messageTreeModel, parent);
		
		
		if (el.getSegment() == null) {
			List<Element> elements = el.getChildren();
			for (Element e : elements) {
				travelElementForElementOccur(e, path, treeNode, message);
			}
		}
	}
	
	private int findOccurByPath(String path, Message message){		
		for(ProfilePathOccurIGData oid : message.getProfilePathOccurIGData()){
			if(oid.getPath().equals(path)){
				return oid.getOccur();
			}
		}
		
		return -1;
	}
	
	private String findDataByPath(String path, Message message){		
		for(ProfilePathOccurIGData oid : message.getProfilePathOccurIGData()){
			if(oid.getPath().equals(path)){
				return oid.getIgData();
			}
		}
		
		return null;
	}
	
	private Segment findSegmentInMessage(TreeNode messageTreeRoot, String path){
		List<MessageTreeModel> models = new ArrayList<MessageTreeModel>();
		this.findAllMessageTreeModel(messageTreeRoot, models);
		for(MessageTreeModel mtm:models){
			if(mtm.getPath().equals(path)) return ((Element)mtm.getNode()).getSegment();
		}
		return null;
	}
	
	private void findAllMessageTreeModel(TreeNode messageTreeRoot, List<MessageTreeModel> models){
		for(TreeNode tn:messageTreeRoot.getChildren()){
			models.add((MessageTreeModel)tn.getData());
			
			this.findAllMessageTreeModel(tn,models);
		}
	}
	
	private void travelSegment(Message message, Segment s, String iPath, String path, TreeNode parentNode) {
		List<Field> fields = s.getFields();
		for (Field f : fields) {
			travelField(message, f, iPath, path, parentNode);
		}
	}
	
	private void travelField(Message message, Field f, String iPath, String path, TreeNode parentNode) {
		path = path + "." + f.getPosition();
		iPath = iPath + "." + f.getPosition();
		String segmentRootName = path.split("\\.")[0];
		
		int occur = this.findOccurByPath(path, message);
		

		Datatype dt = f.getDatatype();	
		List<Component> components = dt.getComponents();
		boolean isLeafNode = false;
		
		if(components == null || components.size() == 0){
			isLeafNode = true;
		}
		
	
		
		if(occur > 0){
			for(int i=0; i<occur; i++){
				String profileFixedData = this.findDataByPath(path, message);
				TestDataCategorization dataType = null;
				if(profileFixedData != null){
					dataType = TestDataCategorization.ProfileFixed;
					this.createRVVC(iPath, f.getDescription(), profileFixedData, message, dataType.getValue());
				}
				SegmentTreeModel segmentTreeModel = new SegmentTreeModel(segmentRootName, f.getDescription(), f, iPath + "[" + (i+1)+ "]", path, profileFixedData, dataType, null, isLeafNode, occur);
				TreeNode treeNode = new DefaultTreeNode(segmentTreeModel, parentNode);
				
				travelDT(message, dt, iPath + "[" + (i+1)+ "]", path, treeNode, occur);	
			}
		}else{
			String profileFixedData = this.findDataByPath(path, message);
			TestDataCategorization dataType = null;
			if(profileFixedData != null){
				dataType = TestDataCategorization.ProfileFixed;
			}

			SegmentTreeModel segmentTreeModel = new SegmentTreeModel(segmentRootName, f.getDescription(), f, iPath + "[1]", path, profileFixedData, dataType, null, isLeafNode, occur);
			TreeNode treeNode = new DefaultTreeNode(segmentTreeModel, parentNode);
			travelDT(message, dt, iPath + "[1]", path, treeNode, occur);	
		}
	}
	
	private void travelDT(Message message, Datatype dt, String iPath, String path, TreeNode parentNode, int fieldOccurence) {
		List<Component> components = dt.getComponents();
		if (components == null) {
		} else {
			for (Component c : components) {
				String newPath = path + "." + c.getPosition();
				String newiPath = iPath + "." + c.getPosition();
				String messageName = newPath.split("\\.")[0];

				String profileFixedData = this.findDataByPath(newPath, message);
				TestDataCategorization dataType = null;
				if(profileFixedData != null){
					dataType = TestDataCategorization.ProfileFixed;
					this.createRVVC(newiPath, c.getDescription(), profileFixedData, message, dataType.getValue());
				}
				
				Datatype childdt = c.getDatatype();	
				List<Component> childComponents = childdt.getComponents();
				boolean isLeafNode = false;
				
				if(childComponents == null || childComponents.size() == 0){
					isLeafNode = true;
				}

				SegmentTreeModel segmentTreeModel = new SegmentTreeModel(messageName, c.getDescription(), c, newiPath, newPath, profileFixedData, dataType, null, isLeafNode, fieldOccurence);
				TreeNode treeNode = new DefaultTreeNode(segmentTreeModel,
						parentNode);
				travelDT(message, c.getDatatype(), newiPath,  newPath, treeNode, fieldOccurence);
			}
		}
	}
	
	private void createRepeatedFieldByOccur(Message message, SegmentTreeModel toBeRepeatedModel, int occur, int index, TreeNode parent) throws CloneNotSupportedException{
		if(occur == 0) occur = 1;
		
		for(int i=0; i<occur; i++){
			
			SegmentTreeModel stm = (SegmentTreeModel) toBeRepeatedModel.clone();
			String oldiPath = stm.getiPath();
			String newiPath = oldiPath.substring(0, oldiPath.lastIndexOf("[")) + "[" + (i+1) + "]";
			String newPath = stm.getPath();
			stm.setiPath(newiPath);
			
			
			TreeNode addedNode = new DefaultTreeNode(stm, parent);
			travelDT(message, ((Field) stm.getNode()).getDatatype(), newiPath, newPath, addedNode, stm.getOccurrence());
			
			
			parent.getChildren().add(index + i, addedNode);
		}
	}
	
	private void travelMessageForProfilePathOccurIGData(MessageProfile mp, String path, Message message){
		List<Element> elements = mp.getChildren();
		for (Element e : elements) {
			travelElementForProfilePathOccurIGData(e, path, message);
		}
	}
	
	private void travelElementForProfilePathOccurIGData(Element el, String path, Message message) {
		if(path.equals("")){
			path = el.getShortName();
		}else{
			path = path + "." + el.getShortName();
		}

		message.getProfilePathOccurIGData().add(new ProfilePathOccurIGData(path, el.getMin(), null));
		
		if (el.getSegment() == null) {
			List<Element> elements = el.getChildren();
			for (Element e : elements) {
				travelElementForProfilePathOccurIGData(e, path, message);
			}
		} else {
			Segment s = el.getSegment();
			travelSegmentForProfilePathOccurIGData(s, path, message);
		}
	}
	
	private void travelSegmentForProfilePathOccurIGData(Segment s, String path, Message message) {
		List<Field> fields = s.getFields();
		for (Field f : fields) {
			travelFieldForProfilePathOccurIGData(f, path, message);
		}
	}
	
	private void travelFieldForProfilePathOccurIGData(Field f, String path, Message message) {
		path = path + "." + f.getPosition();
		
		String data = null;
		List<ConformanceStatement> css = f.getConformanceStatementList();
		if (css != null && css.size() > 0) {
			for (ConformanceStatement cs : css) {
				StatementDetails statementDetails = (StatementDetails) cs.getStatementDetails();
				if (statementDetails.getPattern().equals("Constant Value Check")) {
					if(statementDetails.getSubPattern().equals("Single Value")) {
						if(!statementDetails.getVerb().contains("not") && !statementDetails.getVerb().contains("NOT")){
							data = statementDetails.getLiteralValue();
						}
					} 
				}
			}
		}
		
		message.getProfilePathOccurIGData().add(new ProfilePathOccurIGData(path, f.getMin(), data));
		
		
		Datatype dt = f.getDatatype();
		travelDTForProfilePathOccurIGData(dt, path, message);
	}
	
	private void travelDTForProfilePathOccurIGData(Datatype dt, String path, Message message) {
		List<Component> components = dt.getComponents();
		if (components == null) {
		} else {
			for (Component c : components) {
				String newPath = path + "." + c.getPosition();
				
				String data = null;
				List<ConformanceStatement> css = c.getConformanceStatementList();
				if (css != null && css.size() > 0) {
					for (ConformanceStatement cs : css) {
						StatementDetails statementDetails = (StatementDetails) cs.getStatementDetails();
						if (statementDetails.getPattern().equals("Constant Value Check")) {
							if(statementDetails.getSubPattern().equals("Single Value")) {
								if(!statementDetails.getVerb().contains("not") && !statementDetails.getVerb().contains("NOT")){
									data = statementDetails.getLiteralValue();
								}
							} 
						}
					}
				}
				
				message.getProfilePathOccurIGData().add(new ProfilePathOccurIGData(newPath, 1, data));
				travelDTForProfilePathOccurIGData(c.getDatatype(), newPath, message);
			}
		}

	}
	
	private String removeSeperator(String substring, String separator){
		if(substring.substring(substring.length()-1).equals(separator)){
			substring = substring.substring(0, substring.length()-1);
			substring = this.removeSeperator(substring, separator);
		}else {
			return substring;
		}
		
		return substring;
		
	}
	
	private void updateDataAndFieldOccur(gov.nist.healthcare.core.hl7.v2.instance.Element e, String iPath, String path, List<InstanceSegment> iss) throws CloneNotSupportedException {
		TreeMap<Integer, List<gov.nist.healthcare.core.hl7.v2.instance.Element>> tm = e.getChildren();
		if(tm == null){
			InstanceSegment is = iss.get(iss.size() - 1);
			for(TreeNode tn : is.getSegmentTreeNode().getChildren()){
				((SegmentTreeModel)tn.getData()).setOccurrence(0);
			}
		}else {
			for(int i=0; i<iss.get(iss.size() - 1).getSegmentTreeNode().getChildren().size(); i++){
				TreeNode tn = iss.get(iss.size() - 1).getSegmentTreeNode().getChildren().get(i);
				((SegmentTreeModel)tn.getData()).setOccurrence(0);
				if(!((SegmentTreeModel)tn.getData()).isFirstField()){						
					tn.getChildren().clear();
			        tn.getParent().getChildren().remove(tn);
			        tn.setParent(null);
			        i = i - 1;
				}
				((SegmentTreeModel)tn.getData()).setData(null);
				for(TreeNode child:tn.getChildren()){
					((SegmentTreeModel)child.getData()).setData(null);
					for(TreeNode childchild:child.getChildren()){
						((SegmentTreeModel)childchild.getData()).setData(null);
					}
				}
				
			}
			Set<Integer> keySet = tm.keySet();
			for(Integer key:keySet){
				List<gov.nist.healthcare.core.hl7.v2.instance.Element> fieldElms = tm.get(key);
				for(int i=0; i<fieldElms.size(); i++){
					gov.nist.healthcare.core.hl7.v2.instance.Element fieldE = fieldElms.get(i);
					String iPathField = iPath + "." + fieldE.getPath().substring(fieldE.getPath().indexOf(".") + 1); 	
					
					if(fieldE.getInstanceNumber() == 1){
						for(int j=0; j<iss.get(iss.size() - 1).getSegmentTreeNode().getChildren().size(); j++){
							TreeNode tn = iss.get(iss.size() - 1).getSegmentTreeNode().getChildren().get(j);
							if(((SegmentTreeModel)tn.getData()).getiPath().equals(iPathField)){
								((SegmentTreeModel)tn.getData()).setOccurrence(fieldElms.size());
								if(iPathField.contains("MSH[1].1[1]")){
									((SegmentTreeModel)tn.getData()).setData(e.getStringRepresentation().substring(3, 4));
								}else if(iPathField.contains("MSH[1].2[1]")){
									this.cSeperator = e.getStringRepresentation().substring(4, 5);
									this.scSeperator = e.getStringRepresentation().substring(7, 8);			
									((SegmentTreeModel)tn.getData()).setData(e.getStringRepresentation().substring(4, 8));
								}else {
									if(((Field)((SegmentTreeModel)tn.getData()).getNode()).getDatatype().getName().equals("varies")){
										((SegmentTreeModel)tn.getData()).setData(fieldE.getStringRepresentation());
									}else {
										updateFieldData(fieldE.getStringRepresentation(), this.cSeperator, this.scSeperator, tn);
									}
									
								}
							}
						}
					}else{
						for(int j=0; j<iss.get(iss.size() - 1).getSegmentTreeNode().getChildren().size(); j++){
							TreeNode tn = iss.get(iss.size() - 1).getSegmentTreeNode().getChildren().get(j);
							if(((SegmentTreeModel)tn.getData()).getiPath().startsWith(iPathField.substring(0, iPathField.lastIndexOf("[")))){
								
								((SegmentTreeModel)tn.getData()).setOccurrence(fieldElms.size());
									SegmentTreeModel clonedSegmentTreeModel = (SegmentTreeModel) ((SegmentTreeModel)tn.getData()).clone();
									clonedSegmentTreeModel.setiPath(iPathField);
									TreeNode copyedTN = new DefaultTreeNode(clonedSegmentTreeModel, iss.get(iss.size() - 1).getSegmentTreeNode());
									for(int k=0; k<tn.getChildren().size();k++){
										TreeNode cTN = tn.getChildren().get(k);	
										SegmentTreeModel clonedCTNSegmentTreeModel = (SegmentTreeModel) ((SegmentTreeModel)cTN.getData()).clone();
										clonedCTNSegmentTreeModel.setiPath(iPathField + "." + (k+1));
										TreeNode copyedCTN = new DefaultTreeNode(clonedCTNSegmentTreeModel, copyedTN);
										for(int l=0; l<cTN.getChildren().size();l++){
											TreeNode scTN = cTN.getChildren().get(l);
											SegmentTreeModel clonedSCTNSegmentTreeModel = (SegmentTreeModel) ((SegmentTreeModel)scTN.getData()).clone();
											clonedSCTNSegmentTreeModel.setiPath(iPathField + "." + (k+1)+"." +(l+1));
											new DefaultTreeNode(clonedSCTNSegmentTreeModel, copyedCTN);
										}
									}
									if(((Field)((SegmentTreeModel)tn.getData()).getNode()).getDatatype().getName().equals("varies")){
										((SegmentTreeModel)tn.getData()).setData(fieldE.getStringRepresentation());
									}else {
										updateFieldData(fieldE.getStringRepresentation(), this.cSeperator, this.scSeperator, copyedTN);
									}
									int index = iss.get(iss.size() - 1).getSegmentTreeNode().getChildren().indexOf(tn);
									tn.getParent().getChildren().add(index + fieldE.getInstanceNumber() - 1, copyedTN);
									j = iss.get(iss.size() - 1).getSegmentTreeNode().getChildren().size();
									
								
							}
						}
					}
					
				}
			}
		}
	}
	
	private void loadElement(Message m, Element el, String path, TreeNode parentTreeNode, int option) throws CloneNotSupportedException {
		path = path + "." + el.getShortName();
		String messageId = path.split("\\.")[0];
		String messagePath = path.replace(messageId + ".", "");
		
		if(option == 0){
			List<ConformanceStatement> css = el.getConformanceStatementList();
			if (css != null && css.size() > 0) {
				for (ConformanceStatement cs : css) {
						ConformanceStatement editedCS = cs.clone();
						this.updateCS(editedCS, messagePath);
						m.getListCSs().add(editedCS);
				}
			}
			Predicate cp = el.getPredicate();
			if (cp != null){
				this.updateCP(cp, messagePath, el.getUsage());
				m.getListCPs().add(cp);
			}
				
		}



		MessageTreeModel messageTreeModel = new MessageTreeModel(messageId,
				el.getShortName(), el, messagePath, el.getMin());
		TreeNode treeNode = new DefaultTreeNode(messageTreeModel, parentTreeNode);
		if (el.getSegment() == null) {
			List<Element> elements = el.getChildren();
			for (Element e : elements) {
				loadElement(m, e, path, treeNode, option);
			}
		} else {
			Segment s = el.getSegment();
			loadSegment(m, s, path, option);
		}
	}
	
	private void loadSegment(Message m, Segment s, String path, int option) throws CloneNotSupportedException {
		List<Field> fields = s.getFields();
		for (Field f : fields) {
			loadField(m, f, path, option);
		}
	}
	
	private void loadField(Message m, Field f, String path, int option) throws CloneNotSupportedException {
		path = path + "." + f.getPosition();
		String messageId = path.split("\\.")[0];
		String messagePath = path.replace(messageId + ".", "");

		if(option == 0){
			List<ConformanceStatement> css = f.getConformanceStatementList();
			if (css != null && css.size() > 0) {
				for (ConformanceStatement cs : css) {
						ConformanceStatement editedCS = cs.clone();
						this.updateCS(editedCS, messagePath);
						m.getListCSs().add(editedCS);
				}
			}

			Predicate cp = f.getPredicate();
			if (cp != null){
				this.updateCP(cp, messagePath, f.getUsage());
				m.getListCPs().add(cp);
			}
		}
		

		Datatype dt = f.getDatatype();
		loadDT(m, dt, path, option);
	}
	
	private void loadDT(Message m, Datatype dt, String path, int option) throws CloneNotSupportedException {
		List<Component> components = dt.getComponents();
		if (components == null) {
		} else {
			for (Component c : components) {
				String newPath = path + "." + c.getPosition();
				String messageId = newPath.split("\\.")[0];
				String messagePath = newPath.replace(messageId + ".", "");
				
				if(option == 0){
					List<ConformanceStatement> css = c
							.getConformanceStatementList();
					if (css != null && css.size() > 0) {
						for (ConformanceStatement cs : css) {

								ConformanceStatement editedCS = cs.clone();
								this.updateCS(editedCS, messagePath);
								m.getListCSs().add(editedCS);
						}
					}
					Predicate cp = c.getPredicate();
					if (cp != null){
						this.updateCP(cp, messagePath, c.getUsage());
						m.getListCPs().add(cp);
					}
				}

				

				loadDT(m, c.getDatatype(), newPath, option);
			}
		}

	}
	
	private void updateCS(ConformanceStatement cs, String path){
		((StatementDetails)cs.getStatementDetails()).setPath(path);
	}
	
	private void updateCP(Predicate cp, String path, Usage usage){
		((StatementDetails)cp.getPredicateDetails()).setPath(path);
		
		if(usage.equals(Usage.C_O_O)){
			cp.setTrueFalseUsages(Usage.C_O_O);
			cp.setTrueUsage(Usage.O);
			cp.setFalseUsage(Usage.O);
		}else if(usage.equals(Usage.C_O_R)){
			cp.setTrueFalseUsages(Usage.C_O_R);
			cp.setTrueUsage(Usage.O);
			cp.setFalseUsage(Usage.R);
		}else if(usage.equals(Usage.C_O_RE)){
			cp.setTrueFalseUsages(Usage.C_O_RE);
			cp.setTrueUsage(Usage.O);
			cp.setFalseUsage(Usage.RE);
		}else if(usage.equals(Usage.C_O_X)){
			cp.setTrueFalseUsages(Usage.C_O_X);
			cp.setTrueUsage(Usage.O);
			cp.setFalseUsage(Usage.X);
		}else if(usage.equals(Usage.C_R_O)){
			cp.setTrueFalseUsages(Usage.C_R_O);
			cp.setTrueUsage(Usage.R);
			cp.setFalseUsage(Usage.O);
		}else if(usage.equals(Usage.C_R_R)){
			cp.setTrueFalseUsages(Usage.C_R_R);
			cp.setTrueUsage(Usage.R);
			cp.setFalseUsage(Usage.R);
		}else if(usage.equals(Usage.C_R_RE)){
			cp.setTrueFalseUsages(Usage.C_R_RE);
			cp.setTrueUsage(Usage.R);
			cp.setFalseUsage(Usage.RE);
		}else if(usage.equals(Usage.C_R_X)){
			cp.setTrueFalseUsages(Usage.C_R_X);
			cp.setTrueUsage(Usage.R);
			cp.setFalseUsage(Usage.X);
		}else if(usage.equals(Usage.C_RE_O)){
			cp.setTrueFalseUsages(Usage.C_RE_O);
			cp.setTrueUsage(Usage.RE);
			cp.setFalseUsage(Usage.O);
		}else if(usage.equals(Usage.C_RE_R)){
			cp.setTrueFalseUsages(Usage.C_RE_R);
			cp.setTrueUsage(Usage.RE);
			cp.setFalseUsage(Usage.R);
		}else if(usage.equals(Usage.C_RE_RE)){
			cp.setTrueFalseUsages(Usage.C_RE_RE);
			cp.setTrueUsage(Usage.RE);
			cp.setFalseUsage(Usage.RE);
		}else if(usage.equals(Usage.C_RE_X)){
			cp.setTrueFalseUsages(Usage.C_RE_X);
			cp.setTrueUsage(Usage.RE);
			cp.setFalseUsage(Usage.X);
		}else if(usage.equals(Usage.C_X_O)){
			cp.setTrueFalseUsages(Usage.C_X_O);
			cp.setTrueUsage(Usage.X);
			cp.setFalseUsage(Usage.O);
		}else if(usage.equals(Usage.C_X_R)){
			cp.setTrueFalseUsages(Usage.C_X_R);
			cp.setTrueUsage(Usage.X);
			cp.setFalseUsage(Usage.R);
		}else if(usage.equals(Usage.C_X_RE)){
			cp.setTrueFalseUsages(Usage.C_X_RE);
			cp.setTrueUsage(Usage.X);
			cp.setFalseUsage(Usage.RE);
		}
	}
	
	private void removeExistVC(String iPath, Message m) {
		List<ValidationContext> newVCList = new ArrayList<ValidationContext>();
		for (ValidationContext vc : m.getValidationContexts()) {
			if (!vc.getPath().equals(iPath)) {
				newVCList.add(vc);
			}
		}
		
		List<InstanceTestDataType> newITDTList = new ArrayList<InstanceTestDataType>();
		for (InstanceTestDataType itdt : m.getInstanceTestDataTypes()) {
			if (!itdt.getiPath().equals(iPath)) {
				newITDTList.add(itdt);
			}
		}
		
		m.setValidationContexts(newVCList);
		m.setInstanceTestDataTypes(newITDTList);
	}

	private void createRVC(String iPath, String nodeName, Message m, String level) {
		ValidationContext vcPresence = new ValidationContext();
		vcPresence.setId(UUID.randomUUID().toString());
		vcPresence.setLevel(level);
		vcPresence.setPath(iPath);
		StatementDetails sd = new StatementDetails();
		sd.setId(vcPresence.getId());
		sd.setConstructionType("Single");
		sd.setPattern("Presence Check");
		sd.setLevel(level);
		sd.setPredicate(false);
		sd.setAnotherNodeLocation(iPath);
		sd.setAnotherNodeName(nodeName);
		sd.setVerb("SHALL be");
		sd.generateStatementText(true, true);
		vcPresence.setStatementDetails(sd);
		vcPresence.setDescription(sd.getStatementText());
		m.getValidationContexts().add(vcPresence);
	}
	
	private void createNotRVC(String iPath, String nodeName, Message m, String level) {
		ValidationContext vcPresence = new ValidationContext();
		vcPresence.setId(UUID.randomUUID().toString());
		vcPresence.setLevel(level);
		vcPresence.setPath(iPath);
		StatementDetails sd = new StatementDetails();
		sd.setId(vcPresence.getId());
		sd.setConstructionType("Single");
		sd.setPattern("Presence Check");
		sd.setLevel(level);
		sd.setPredicate(false);
		sd.setAnotherNodeLocation(iPath);
		sd.setAnotherNodeName(nodeName);
		sd.setVerb("SHALL NOT be");
		sd.generateStatementText(true, true);
		vcPresence.setStatementDetails(sd);
		vcPresence.setDescription(sd.getStatementText());
		m.getValidationContexts().add(vcPresence);
	}

	private void createRVVC(String iPath, String nodeName, String data, Message m, String level) {
		ValidationContext vcConstant = new ValidationContext();
		vcConstant.setId(UUID.randomUUID().toString());
		vcConstant.setLevel(level);
		vcConstant.setPath(iPath);
		StatementDetails sd = new StatementDetails();
		sd.setId(vcConstant.getId());
		sd.setConstructionType("Single");
		sd.setPattern("Constant Value Check");
		sd.setSubPattern("Single Value");
		sd.setLevel(level);

		sd.setPredicate(false);
		sd.setTargetNodeLocation(iPath);
		sd.setTargetNodeName(nodeName);
		sd.setVerb("SHALL be");
		sd.setLiteralValue(data);
		sd.generateStatementText(true, true);
		vcConstant.setStatementDetails(sd);
		vcConstant.setDescription(sd.getStatementText());
		m.getValidationContexts().add(vcConstant);
	}
	
	private void createListRVVC(String iPath, String nodeName, String data, Message m, String level) {
		ValidationContext vcConstant = new ValidationContext();
		vcConstant.setId(UUID.randomUUID().toString());
		vcConstant.setLevel(level);
		vcConstant.setPath(iPath);
		StatementDetails sd = new StatementDetails();
		sd.setId(vcConstant.getId());
		sd.setConstructionType("Single");
		sd.setPattern("Constant Value Check");
		sd.setSubPattern("List Values");
		sd.setLevel(level);

		sd.setPredicate(false);
		sd.setTargetNodeLocation(iPath);
		sd.setTargetNodeName(nodeName);
		sd.setVerb("should be");
		sd.setLiteralValues(Arrays.asList(data.split("::")));
		sd.generateStatementText(true, true);
		vcConstant.setStatementDetails(sd);
		vcConstant.setDescription(sd.getStatementText());
		m.getValidationContexts().add(vcConstant);
	}

	private void updateInstanceSegmentByITDT(InstanceTestDataType itdt, InstanceSegment is, List<ValidationContext> vclist) {
		for(TreeNode fieldTN:is.getSegmentTreeNode().getChildren()){
			SegmentTreeModel fstm = (SegmentTreeModel)fieldTN.getData();
			
			if(fstm.getiPath().equals(itdt.getiPath())){
				fstm.setType(itdt.getType());
				if(itdt.getType().equals(TestDataCategorization.TestCaseFixedList)){
					ValidationContext vc = this.findVCByIPath(vclist, itdt.getiPath());
					if(vc != null) {
						String data = "";;
						for(String s: vc.getStatementDetails().getLiteralValues()){
							data = data + "::" + s;
						}
						data = data.substring(2);
						fstm.setData(data);
					}
				}
				
				
				return;
			}
			
			for(TreeNode componentTN:fieldTN.getChildren()){
				SegmentTreeModel cstm = (SegmentTreeModel)componentTN.getData();
				
				if(cstm.getiPath().equals(itdt.getiPath())){
					cstm.setType(itdt.getType());
					if(itdt.getType().equals(TestDataCategorization.TestCaseFixedList)){
						ValidationContext vc = this.findVCByIPath(vclist, itdt.getiPath());
						if(vc != null) {
							String data = "";;
							for(String s: vc.getStatementDetails().getLiteralValues()){
								data = data + "::" + s;
							}
							data = data.substring(2);
							cstm.setData(data);
						}
					}
					return;
				}
				
				for(TreeNode subComponentTN:fieldTN.getChildren()){
					SegmentTreeModel scstm = (SegmentTreeModel)subComponentTN.getData();
					
					if(scstm.getiPath().equals(itdt.getiPath())){
						scstm.setType(itdt.getType());
						if(itdt.getType().equals(TestDataCategorization.TestCaseFixedList)){
							ValidationContext vc = this.findVCByIPath(vclist, itdt.getiPath());
							if(vc != null) {
								String data = "";;
								for(String s: vc.getStatementDetails().getLiteralValues()){
									data = data + "::" + s;
								}
								data = data.substring(2);
								scstm.setData(data);
							}
						}
						return;
					}
				}
			}
		}
		
	}

	private ValidationContext findVCByIPath(List<ValidationContext> vclist, String path) {
		for(ValidationContext vc:vclist){
			if(path.equals(vc.getPath())){
				return vc;
			}
		}
		return null;
	}

	private InstanceSegment findInstanceSegmentByIPath(String iPath, List<InstanceSegment> instanceSegments) {
		for(InstanceSegment is: instanceSegments){
			if(iPath.contains(is.getPath())){
				return is;
			}
		}
		return null;
	}
}
