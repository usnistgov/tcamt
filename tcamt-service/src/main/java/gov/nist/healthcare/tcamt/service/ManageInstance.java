package gov.nist.healthcare.tcamt.service;

import gov.nist.healthcare.tcamt.domain.Message;
import gov.nist.healthcare.tcamt.domain.data.ComponentModel;
import gov.nist.healthcare.tcamt.domain.data.FieldModel;
import gov.nist.healthcare.tcamt.domain.data.InstanceSegment;
import gov.nist.healthcare.tcamt.domain.data.MessageTreeModel;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.ProfileSerialization;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.ProfileSerializationImpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

public class ManageInstance  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 574095903906379334L;
	
	
//	public MessageProfile genMessageProfile(String str){
//		JSONConverterService jConverterService = new JSONConverterService();
//		try {
//			return jConverterService.fromStream(new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8)));
//		} catch (ConversionException e) {
//			e.printStackTrace();
//		}
//		
//		return null;
//	}
	
//	public TreeNode generateMessageTreeForElementOccur(Message message){
//		TreeNode treeNode = new DefaultTreeNode("root", null);
//		
//		MessageProfile mp = genMessageProfile(message.getMessageProfile());
//		String path = "";
//		travelMessageForElementOccur(mp, path, treeNode, message);
//		
//		return treeNode;
//		
//	}
	
//	public SegmentTreeModel findOccurField(List<TreeNode> toBeDeletedTreeNodes, SegmentTreeModel toBeRepeatedModel, TreeNode tn, int fieldNumber, int occur) throws CloneNotSupportedException{
//		for(TreeNode t: tn.getChildren()){
//			SegmentTreeModel stm = (SegmentTreeModel)t.getData();
//			if(((Field)stm.getNode()).getPosition() == fieldNumber){
//				toBeDeletedTreeNodes.add(t);
//				
//				if(toBeRepeatedModel == null) {
//					toBeRepeatedModel = (SegmentTreeModel)((SegmentTreeModel)t.getData()).clone();
//					toBeRepeatedModel.setOccurrence(occur);
//				}
//			}
//		}
//		return toBeRepeatedModel;
//	}
	
//	public void adjustOccur(Message message, List<TreeNode> toBeDeletedTreeNodes, SegmentTreeModel toBeRepeatedModel, InstanceSegment selectedInstanceSegment, int occur) throws CloneNotSupportedException{
//		int index=0;
//		for(TreeNode tn:toBeDeletedTreeNodes){
//			index = selectedInstanceSegment.getSegmentTreeNode().getChildren().indexOf(tn);
//			tn.getChildren().clear();
//			tn.getParent().getChildren().remove(tn);
//			
//		}
//		toBeDeletedTreeNodes = new ArrayList<TreeNode>();
//		
//		this.createRepeatedFieldByOccur(message, toBeRepeatedModel, occur, index, selectedInstanceSegment.getSegmentTreeNode());
//
//	}
	
//	public void generateProfilePathOccurIGData(Message message){
//		message.setProfilePathOccurIGData(new ArrayList<ProfilePathOccurIGData>());
//		MessageProfile mp = genMessageProfile(message.getMessageProfile());
//		String path = "";
//		travelMessageForProfilePathOccurIGData(mp, path, message);
//		
//	}
	
	
	
	
//	public gov.nist.healthcare.core.hl7.v2.instance.Message readHL7Message(Message message) throws IOException, ParserException{
//		XMLSerializer xmlSerializer = new XMLSerializer();
//		MessageProfile mp = genMessageProfile(message.getMessageProfile());
//		MetaData metaData = mp.getMetaData();
//		String schemaV = metaData.getHl7RulesSchema();
//		schemaV = schemaV.replace(".", "");
//		ProfileSchemaVersion schemaVersion = ProfileSchemaVersion.valueOf("V" + schemaV);
//		nu.xom.Element result = xmlSerializer.serialize(mp.getMessage(), metaData.getHl7Version(),	schemaVersion);
//		Parser parser = new ParserImpl();
//		return parser.parse(message.getHl7EndcodedMessage(), new Document(result).toXML());
//	}
	
//	public void generateHL7Message(List<InstanceSegment> instanceSegments, Message message) {
//		String messageString = "";
//		String fieldSeparator ="|";
//		String componentSeparator ="^";
//		String subComponentSeparator ="&";
//		String fieldRepeatSeparator ="~";
//		String escapeCharacter ="\\";
//
//		for (InstanceSegment is : instanceSegments) {
//			String segmentString = is.getPath().substring(is.getPath().lastIndexOf(".") + 1,is.getPath().lastIndexOf("["));
//			TreeNode segmentTN = is.getSegmentTreeNode();
//
//			
//			
//			String fieldPath = "";
//			for (TreeNode fieldTN : segmentTN.getChildren()) {
//				SegmentTreeModel fModel = (SegmentTreeModel) fieldTN.getData();
//				String fieldData;
//				if(fModel.getData() != null && fModel.getData().contains("::")){
//					fieldData = fModel.getData().split("::")[0];
//				}else {
//					fieldData = fModel.getData();
//				}
//				
//				
//				boolean isRepeat = (fModel.getPath().equals(fieldPath)) ? true:false;
//				
//				fieldPath = fModel.getPath();
//
//				if (fieldTN.getChildCount() == 0) {
//					if (fModel.getData() == null) {
//						if(fModel.getName().equals("Field Separator")){
//							if(fModel.getData() != null && !fModel.getData().equals("")){
//								fieldSeparator = fModel.getData();
//							}
//							segmentString = segmentString + fieldSeparator;
//						}else if (fModel.getName().equals("Encoding Characters")){
//							if(fModel.getData() != null && !fModel.getData().equals("") && fModel.getData().length() ==4){
//								String encodigCharacters = fModel.getData();
//								componentSeparator = encodigCharacters.substring(0, 1);
//								subComponentSeparator = encodigCharacters.substring(3, 4);
//								fieldRepeatSeparator = encodigCharacters.substring(1, 2);
//								escapeCharacter = encodigCharacters.substring(2, 3);
//							}
//							segmentString = segmentString + componentSeparator + fieldRepeatSeparator + escapeCharacter + subComponentSeparator;
//						}else {
//							segmentString = segmentString + ((isRepeat) ? fieldRepeatSeparator:fieldSeparator);
//						}
//						
//					} else {
//						if(fModel.getName().equals("Field Separator")){
//							if(fModel.getData() != null && !fModel.getData().equals("")){
//								fieldSeparator = fModel.getData();
//							}
//							segmentString = segmentString + fieldSeparator;
//						}else if (fModel.getName().equals("Encoding Characters")){
//							if(fModel.getData() != null && !fModel.getData().equals("") && fModel.getData().length() ==4){
//								String encodigCharacters = fModel.getData();
//								componentSeparator = encodigCharacters.substring(0, 1);
//								subComponentSeparator = encodigCharacters.substring(3, 4);
//								fieldRepeatSeparator = encodigCharacters.substring(1, 2);
//								escapeCharacter = encodigCharacters.substring(2, 3);
//							}
//							segmentString = segmentString + componentSeparator + fieldRepeatSeparator + escapeCharacter + subComponentSeparator;
//						}else {
//							segmentString = segmentString + ((isRepeat) ? fieldRepeatSeparator:fieldSeparator) + fieldData;
//						}
//					}
//
//				} else {
//					segmentString = segmentString + ((isRepeat) ? fieldRepeatSeparator:fieldSeparator);
//					int indexC = 0;
//					if (fModel.getOccurrence() == 0) {
//
//					} else {
//
//						for (TreeNode componentTN : fieldTN.getChildren()) {
//							indexC = indexC + 1;
//							SegmentTreeModel cModel = (SegmentTreeModel) componentTN.getData();
//
//							String cData;
//							if(cModel.getData() != null && cModel.getData().contains("::")){
//								cData = cModel.getData().split("::")[0];
//							}else {
//								cData = cModel.getData();
//							}
//							
//							if (componentTN.getChildCount() == 0) {
//								if (cModel.getData() == null) {
//									if (indexC == 1) {
//									} else {
//										segmentString = segmentString + componentSeparator;
//									}
//
//								} else {
//									if (indexC == 1) {
//										segmentString = segmentString + cData;
//									} else {
//										segmentString = segmentString + componentSeparator + cData;
//									}
//								}
//
//							} else {
//									if (indexC == 1) {
//										int indexS = 0;
//										for (TreeNode sComponentTN : componentTN.getChildren()) {
//											indexS = indexS + 1;
//											SegmentTreeModel sModel = (SegmentTreeModel) sComponentTN.getData();
//
//											String sData;
//											if(sModel.getData() != null && sModel.getData().contains("::")){
//												sData = sModel.getData().split("::")[0];
//											}else {
//												sData = sModel.getData();
//											}
//											
//											if (sModel.getData() == null) {
//												if (indexS == 1) {
//												} else {
//													segmentString = segmentString + subComponentSeparator;
//												}
//
//											} else {
//												if (indexS == 1) {
//													segmentString = segmentString + sData;
//												} else {
//													segmentString = segmentString + subComponentSeparator + sData;
//												}
//											}
//										}
//										segmentString = this.removeSeperator(segmentString, subComponentSeparator);
//									} else {
//										segmentString = segmentString + componentSeparator;
//										int indexS = 0;
//										for (TreeNode sComponentTN : componentTN.getChildren()) {
//											indexS = indexS + 1;
//											SegmentTreeModel sModel = (SegmentTreeModel) sComponentTN.getData();
//
//											String sData;
//											if(sModel.getData() != null && sModel.getData().contains("::")){
//												sData = sModel.getData().split("::")[0];
//											}else {
//												sData = sModel.getData();
//											}
//											
//											if (sModel.getData() == null) {
//												if (indexS == 1) {
//												} else {
//													segmentString = segmentString + subComponentSeparator;
//												}
//
//											} else {
//												if (indexS == 1) {
//													segmentString = segmentString + sData;
//												} else {
//													segmentString = segmentString + subComponentSeparator + sData;
//												}
//											}
//										}
//										segmentString = this.removeSeperator(segmentString, subComponentSeparator);
//									}
//							}
//						}
//						
//						segmentString = this.removeSeperator(segmentString, componentSeparator);
//					}
//				}
//			}
//
//			messageString = messageString + segmentString + System.lineSeparator();
//		}
//		message.setHl7EndcodedMessage(messageString);
//	}
	
//	public void updateOccurDataByHL7Message(gov.nist.healthcare.core.hl7.v2.instance.Element e, String iPath, String path, Message message, TreeNode messageTreeRoot, List<InstanceSegment> iss) throws CloneNotSupportedException{
//		if(e.getName().contains(".")){
//			if(path.equals("")){
//				iPath = e.getName().substring(e.getName().lastIndexOf(".") + 1) + "[" + e.getInstanceNumber() + "]";
//				path = e.getName().substring(e.getName().lastIndexOf(".") + 1);
//			}else{
//				iPath = iPath + "." + e.getName().substring(e.getName().lastIndexOf(".") + 1) + "[" + e.getInstanceNumber() + "]";
//				path = path + "." + e.getName().substring(e.getName().lastIndexOf(".") + 1);
//			}
//		}else {
//			if(path.equals("")){
//				iPath = e.getName() + "[" + e.getInstanceNumber() + "]";
//				path = e.getName();
//			}else{
//				iPath = iPath + "." + e.getName() + "[" + e.getInstanceNumber() + "]";
//				path = path + "." + e.getName();
//			}
//		}
//		
//		if(e.getElementType().equals(gov.nist.healthcare.core.hl7.v2.enumeration.ElementType.SEGMENT)){			
//			TreeNode segmentTreeNode = new DefaultTreeNode("root", null);
//			this.travelSegment(message, this.findSegmentInMessage(messageTreeRoot, path), iPath, path, segmentTreeNode);
//			InstanceSegment instanceSegment = new InstanceSegment(iPath, segmentTreeNode);
//			iss.add(instanceSegment);
//			updateDataAndFieldOccur(e , iPath, path, iss);
//			
//		}else if(e.getElementType().equals(gov.nist.healthcare.core.hl7.v2.enumeration.ElementType.GROUP)){
//			TreeMap<Integer, List<gov.nist.healthcare.core.hl7.v2.instance.Element>> tmElements = e.getChildren();
//			Set<Integer> keySet = tmElements.keySet();
//			for(Integer i:keySet){
//				List<gov.nist.healthcare.core.hl7.v2.instance.Element> childElms = tmElements.get(i);
//				
//				for(gov.nist.healthcare.core.hl7.v2.instance.Element childE : childElms){
//					updateOccurDataByHL7Message(childE, iPath, path, message, messageTreeRoot, iss);
//				}
//			}
//		}
//	}
	
//	public void updateFieldData(String fieldStr, String cSeperator, String scSeperator, TreeNode tn){
//		if(fieldStr == null){
//			((SegmentTreeModel)tn.getData()).setData(null);
//			for(TreeNode child:tn.getChildren()){
//				((SegmentTreeModel)child.getData()).setData(null);
//				((SegmentTreeModel)child.getData()).setOccurrence(0);
//				for(TreeNode childchild:child.getChildren()){
//					((SegmentTreeModel)childchild.getData()).setData(null);
//					((SegmentTreeModel)childchild.getData()).setOccurrence(0);
//				}
//			}
//		}else {
//			if(fieldStr.contains(cSeperator)){
//				String[] cStrs = fieldStr.split("\\" +  cSeperator);
//				for(int i=0;i<cStrs.length; i++){
//					if(cStrs[i].contains(scSeperator)){
//						String[] scStrs = cStrs[i].split("\\" + scSeperator);
//						for(int j=0;j<scStrs.length; j++){
//							((SegmentTreeModel)tn.getChildren().get(i).getChildren().get(j).getData()).setData(scStrs[j]);
//							((SegmentTreeModel)tn.getChildren().get(i).getChildren().get(j).getData()).setOccurrence(1);
//						}
//					}else{
//						if(tn.getChildren().get(i).getChildCount() == 0){
//							((SegmentTreeModel)tn.getChildren().get(i).getData()).setData(cStrs[i]);
//							((SegmentTreeModel)tn.getChildren().get(i).getData()).setOccurrence(1);
//						}else {
//							((SegmentTreeModel)tn.getChildren().get(i).getChildren().get(0).getData()).setData(cStrs[i]);
//							((SegmentTreeModel)tn.getChildren().get(i).getChildren().get(0).getData()).setOccurrence(1);
//						}
//					}
//				}
//			}else{
//				if(tn.getChildCount() == 0){
//					((SegmentTreeModel)tn.getData()).setData(fieldStr);
//				}else if(tn.getChildren().get(0).getChildCount() == 0){
//					((SegmentTreeModel)tn.getChildren().get(0).getData()).setData(fieldStr);
//					((SegmentTreeModel)tn.getChildren().get(0).getData()).setOccurrence(1);;
//				}else{
//					((SegmentTreeModel)tn.getChildren().get(0).getChildren().get(0).getData()).setData(fieldStr);
//					((SegmentTreeModel)tn.getChildren().get(0).getChildren().get(0).getData()).setOccurrence(1);;
//				}
//			}
//		}
//		
//		
//	}
	
	public TreeNode loadMessage(Message m) throws CloneNotSupportedException {
		TreeNode treeNode = new DefaultTreeNode("root", null);
		
		ProfileSerialization ps = new ProfileSerializationImpl();
		Profile p = ps.deserializeXMLToProfile(m.getProfile(), m.getValueSet(), m.getConstraints());
		gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message mp = null;
		for(gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message m1:p.getMessages().getChildren()){
			mp = m1;
		}
		
		if(mp != null){
			m.setMessageObj(mp);
			List<SegmentRefOrGroup> segmentRefOrGroups = mp.getChildren();
			String path = m.getName();
			if (path == null || path.equals("")) {
				path = "NONAME";
			}			
			for (SegmentRefOrGroup sg : segmentRefOrGroups) {
				this.loadSegmentRefOrGroup(m, sg, path, treeNode);
			}
		}
		return treeNode;
	}
	
//	public void createVC(SegmentTreeModel stm, Message m, String level) {
//		String iPath = stm.getiPath();
//		String data = stm.getData();
//		TestDataCategorization type = stm.getType();
//		Usage usage = Usage.NA;
//		String nodeName = "";
//		if (stm.getNode() instanceof Field) {
//			usage = ((Field) stm.getNode()).getUsage();
//			nodeName = ((Field) stm.getNode()).getDescription();
//		} else if (stm.getNode() instanceof Component) {
//			usage = ((Component) stm.getNode()).getUsage();
//			nodeName = ((Component) stm.getNode()).getDescription();
//		}
//
//		this.removeExistVC(iPath, m);
//		m.getInstanceTestDataTypes().add(new InstanceTestDataType(iPath, type));
//		if (type==null) {
//			
//		} else if (type.equals(TestDataCategorization.Indifferent)) {
//			
//		} else if (type.equals(TestDataCategorization.ContentIndifferent)) {
//			if (usage.equals(Usage.RE)) {
//				this.createRVC(iPath, nodeName, m, level);
//			}
//		} else if (type.equals(TestDataCategorization.Configurable)) {
//			if (usage.equals(Usage.RE)) {
//				this.createRVC(iPath, nodeName, m, level);
//			}
//		} else if (type.equals(TestDataCategorization.SystemGenerated)) {
//			if (usage.equals(Usage.RE)) {
//				this.createRVC(iPath, nodeName, m, level);
//			}
//		} else if (type.equals(TestDataCategorization.TestCaseProper)) {
//			if (usage.equals(Usage.RE)) {
//				this.createRVC(iPath, nodeName, m, level);
//			}
//		} else if (type.equals(TestDataCategorization.ProfileFixedPresence)) {
//			if (usage.equals(Usage.RE)) {
//				this.createRVC(iPath, nodeName, m, level);
//			}
//		} else if (type.equals(TestDataCategorization.NotValued)) {
//			if (usage.equals(Usage.RE)) {
//				this.createNotRVC(iPath, nodeName, m, level);
//			}
//		} else if (type.equals(TestDataCategorization.ProfileFixed)) {
//			
//		} else if (type.equals(TestDataCategorization.ProfileFixedList)) {
//			
//		} else if (type.equals(TestDataCategorization.TestCaseFixed)) {
//			if (usage.equals(Usage.RE)) {
//				this.createRVC(iPath, nodeName, m, level);
//				this.createRVVC(iPath, nodeName, data, m, level);
//			} else if (usage.equals(Usage.R)) {
//				this.createRVVC(iPath, nodeName, data, m, level);
//			}
//		} else if (type.equals(TestDataCategorization.TestCaseFixedList)) {
//			if (usage.equals(Usage.RE)) {
//				this.createRVC(iPath, nodeName, m, level);
//				this.createListRVVC(iPath, nodeName, data, m, level);
//			} else if (usage.equals(Usage.R)) {
//				this.createListRVVC(iPath, nodeName, data, m, level);
//			}
//		} else if (type.equals(TestDataCategorization.TestCaseFixedLength)) {
//			
//		}
//	}
	
//	public void updateInstanceSegmentsByTestDataTypeList(Message m, List<InstanceSegment> instanceSegments) {
//		for(InstanceTestDataType itdt:m.getInstanceTestDataTypes()){
//			InstanceSegment is = this.findInstanceSegmentByIPath(itdt.getiPath(), instanceSegments);
//			if(is != null) this.updateInstanceSegmentByITDT(itdt, is, m.getValidationContexts());
//		}
//	}
	
//	private void travelMessageForElementOccur(MessageProfile mp, String path, TreeNode parent, Message message){
//		List<Element> elements = mp.getChildren();
//		for (Element e : elements) {
//			travelElementForElementOccur(e, path, parent, message);
//		}
//	}
	
//	private void travelElementForElementOccur(Element el, String path, TreeNode parent, Message message) {
//		if(path.equals("")){
//			path = el.getShortName();
//		}else{
//			path = path + "." + el.getShortName();
//		}
//		
//		
//		MessageTreeModel messageTreeModel = new MessageTreeModel(message.getName(), el.getShortName(), el, path, findOccurByPath(path, message));
//		TreeNode treeNode = new DefaultTreeNode(messageTreeModel, parent);
//		
//		
//		if (el.getSegment() == null) {
//			List<Element> elements = el.getChildren();
//			for (Element e : elements) {
//				travelElementForElementOccur(e, path, treeNode, message);
//			}
//		}
//	}
	
//	private int findOccurByPath(String path, Message message){		
//		for(ProfilePathOccurIGData oid : message.getProfilePathOccurIGData()){
//			if(oid.getPath().equals(path)){
//				return oid.getOccur();
//			}
//		}
//		
//		return -1;
//	}
	
//	private Segment findSegmentInMessage(TreeNode messageTreeRoot, String path){
//		List<MessageTreeModel> models = new ArrayList<MessageTreeModel>();
//		this.findAllMessageTreeModel(messageTreeRoot, models);
//		for(MessageTreeModel mtm:models){
//			if(mtm.getPath().equals(path)) return ((Element)mtm.getNode()).getSegment();
//		}
//		return null;
//	}
//	
//	private void findAllMessageTreeModel(TreeNode messageTreeRoot, List<MessageTreeModel> models){
//		for(TreeNode tn:messageTreeRoot.getChildren()){
//			models.add((MessageTreeModel)tn.getData());
//			
//			this.findAllMessageTreeModel(tn,models);
//		}
//	}
	
//	private void travelSegment(Message message, Segment s, String iPath, String path, TreeNode parentNode) {
//		List<Field> fields = s.getFields();
//		for (Field f : fields) {
//			travelField(message, f, iPath, path, parentNode);
//		}
//	}
	
//	private void travelField(Message message, Field f, String iPath, String path, TreeNode parentNode) {
//		path = path + "." + f.getPosition();
//		iPath = iPath + "." + f.getPosition();
//		String segmentRootName = path.split("\\.")[0];
//		
//		int occur = this.findOccurByPath(path, message);
//		
//
//		Datatype dt = f.getDatatype();	
//		List<Component> components = dt.getComponents();
//		boolean isLeafNode = false;
//		
//		if(components == null || components.size() == 0){
//			isLeafNode = true;
//		}
//		
//	
//		
//		if(occur > 0){
//			for(int i=0; i<occur; i++){
//				String profileFixedData = this.findDataByPath(path, message);
//				TestDataCategorization dataType = null;
//				if(profileFixedData != null){
//					dataType = TestDataCategorization.ProfileFixed;
//					this.createRVVC(iPath, f.getDescription(), profileFixedData, message, dataType.getValue());
//				}
//				SegmentTreeModel segmentTreeModel = new SegmentTreeModel(segmentRootName, f.getDescription(), f, iPath + "[" + (i+1)+ "]", path, profileFixedData, dataType, null, isLeafNode, occur);
//				TreeNode treeNode = new DefaultTreeNode(segmentTreeModel, parentNode);
//				
//				travelDT(message, dt, iPath + "[" + (i+1)+ "]", path, treeNode, occur);	
//			}
//		}else{
//			String profileFixedData = this.findDataByPath(path, message);
//			TestDataCategorization dataType = null;
//			if(profileFixedData != null){
//				dataType = TestDataCategorization.ProfileFixed;
//			}
//
//			SegmentTreeModel segmentTreeModel = new SegmentTreeModel(segmentRootName, f.getDescription(), f, iPath + "[1]", path, profileFixedData, dataType, null, isLeafNode, occur);
//			TreeNode treeNode = new DefaultTreeNode(segmentTreeModel, parentNode);
//			travelDT(message, dt, iPath + "[1]", path, treeNode, occur);	
//		}
//	}
	
//	private void travelDT(Message message, Datatype dt, String iPath, String path, TreeNode parentNode, int fieldOccurence) {
//		List<Component> components = dt.getComponents();
//		if (components == null) {
//		} else {
//			for (Component c : components) {
//				String newPath = path + "." + c.getPosition();
//				String newiPath = iPath + "." + c.getPosition();
//				String messageName = newPath.split("\\.")[0];
//
//				String profileFixedData = this.findDataByPath(newPath, message);
//				TestDataCategorization dataType = null;
//				if(profileFixedData != null){
//					dataType = TestDataCategorization.ProfileFixed;
//					this.createRVVC(newiPath, c.getDescription(), profileFixedData, message, dataType.getValue());
//				}
//				
//				Datatype childdt = c.getDatatype();	
//				List<Component> childComponents = childdt.getComponents();
//				boolean isLeafNode = false;
//				
//				if(childComponents == null || childComponents.size() == 0){
//					isLeafNode = true;
//				}
//
//				SegmentTreeModel segmentTreeModel = new SegmentTreeModel(messageName, c.getDescription(), c, newiPath, newPath, profileFixedData, dataType, null, isLeafNode, fieldOccurence);
//				TreeNode treeNode = new DefaultTreeNode(segmentTreeModel,
//						parentNode);
//				travelDT(message, c.getDatatype(), newiPath,  newPath, treeNode, fieldOccurence);
//			}
//		}
//	}
	
//	private void createRepeatedFieldByOccur(Message message, SegmentTreeModel toBeRepeatedModel, int occur, int index, TreeNode parent) throws CloneNotSupportedException{
//		if(occur == 0) occur = 1;
//		
//		for(int i=0; i<occur; i++){
//			
//			SegmentTreeModel stm = (SegmentTreeModel) toBeRepeatedModel.clone();
//			String oldiPath = stm.getiPath();
//			String newiPath = oldiPath.substring(0, oldiPath.lastIndexOf("[")) + "[" + (i+1) + "]";
//			String newPath = stm.getPath();
//			stm.setiPath(newiPath);
//			
//			
//			TreeNode addedNode = new DefaultTreeNode(stm, parent);
//			travelDT(message, ((Field) stm.getNode()).getDatatype(), newiPath, newPath, addedNode, stm.getOccurrence());
//			
//			
//			parent.getChildren().add(index + i, addedNode);
//		}
//	}
	
//	private void travelMessageForProfilePathOccurIGData(MessageProfile mp, String path, Message message){
//		List<Element> elements = mp.getChildren();
//		for (Element e : elements) {
//			travelElementForProfilePathOccurIGData(e, path, message);
//		}
//	}
	
//	private void travelElementForProfilePathOccurIGData(Element el, String path, Message message) {
//		if(path.equals("")){
//			path = el.getShortName();
//		}else{
//			path = path + "." + el.getShortName();
//		}
//
//		message.getProfilePathOccurIGData().add(new ProfilePathOccurIGData(path, el.getMin(), null));
//		
//		if (el.getSegment() == null) {
//			List<Element> elements = el.getChildren();
//			for (Element e : elements) {
//				travelElementForProfilePathOccurIGData(e, path, message);
//			}
//		} else {
//			Segment s = el.getSegment();
//			travelSegmentForProfilePathOccurIGData(s, path, message);
//		}
//	}
	
//	private void travelSegmentForProfilePathOccurIGData(Segment s, String path, Message message) {
//		List<Field> fields = s.getFields();
//		for (Field f : fields) {
//			travelFieldForProfilePathOccurIGData(f, path, message);
//		}
//	}
	
//	private void travelFieldForProfilePathOccurIGData(Field f, String path, Message message) {
//		path = path + "." + f.getPosition();
//		
//		String data = null;
//		List<ConformanceStatement> css = f.getConformanceStatementList();
//		if (css != null && css.size() > 0) {
//			for (ConformanceStatement cs : css) {
//				StatementDetails statementDetails = (StatementDetails) cs.getStatementDetails();
//				if (statementDetails.getPattern().equals("Constant Value Check")) {
//					if(statementDetails.getSubPattern().equals("Single Value")) {
//						if(!statementDetails.getVerb().contains("not") && !statementDetails.getVerb().contains("NOT")){
//							data = statementDetails.getLiteralValue();
//						}
//					} 
//				}
//			}
//		}
//		
//		message.getProfilePathOccurIGData().add(new ProfilePathOccurIGData(path, f.getMin(), data));
//		
//		
//		Datatype dt = f.getDatatype();
//		travelDTForProfilePathOccurIGData(dt, path, message);
//	}
	
//	private void travelDTForProfilePathOccurIGData(Datatype dt, String path, Message message) {
//		List<Component> components = dt.getComponents();
//		if (components == null) {
//		} else {
//			for (Component c : components) {
//				String newPath = path + "." + c.getPosition();
//				
//				String data = null;
//				List<ConformanceStatement> css = c.getConformanceStatementList();
//				if (css != null && css.size() > 0) {
//					for (ConformanceStatement cs : css) {
//						StatementDetails statementDetails = (StatementDetails) cs.getStatementDetails();
//						if (statementDetails.getPattern().equals("Constant Value Check")) {
//							if(statementDetails.getSubPattern().equals("Single Value")) {
//								if(!statementDetails.getVerb().contains("not") && !statementDetails.getVerb().contains("NOT")){
//									data = statementDetails.getLiteralValue();
//								}
//							} 
//						}
//					}
//				}
//				
//				message.getProfilePathOccurIGData().add(new ProfilePathOccurIGData(newPath, 1, data));
//				travelDTForProfilePathOccurIGData(c.getDatatype(), newPath, message);
//			}
//		}
//
//	}
	
	private String removeSeperator(String substring, String separator){
		if(substring.substring(substring.length()-1).equals(separator)){
			substring = substring.substring(0, substring.length()-1);
			substring = this.removeSeperator(substring, separator);
		}else {
			return substring;
		}
		
		return substring;
		
	}
	
//	private void updateDataAndFieldOccur(gov.nist.healthcare.core.hl7.v2.instance.Element e, String iPath, String path, List<InstanceSegment> iss) throws CloneNotSupportedException {
//		TreeMap<Integer, List<gov.nist.healthcare.core.hl7.v2.instance.Element>> tm = e.getChildren();
//		if(tm == null){
//			InstanceSegment is = iss.get(iss.size() - 1);
//			for(TreeNode tn : is.getSegmentTreeNode().getChildren()){
//				((SegmentTreeModel)tn.getData()).setOccurrence(0);
//			}
//		}else {
//			for(int i=0; i<iss.get(iss.size() - 1).getSegmentTreeNode().getChildren().size(); i++){
//				TreeNode tn = iss.get(iss.size() - 1).getSegmentTreeNode().getChildren().get(i);
//				((SegmentTreeModel)tn.getData()).setOccurrence(0);
//				if(!((SegmentTreeModel)tn.getData()).isFirstField()){						
//					tn.getChildren().clear();
//			        tn.getParent().getChildren().remove(tn);
//			        tn.setParent(null);
//			        i = i - 1;
//				}
//				((SegmentTreeModel)tn.getData()).setData(null);
//				for(TreeNode child:tn.getChildren()){
//					((SegmentTreeModel)child.getData()).setData(null);
//					for(TreeNode childchild:child.getChildren()){
//						((SegmentTreeModel)childchild.getData()).setData(null);
//					}
//				}
//				
//			}
//			Set<Integer> keySet = tm.keySet();
//			for(Integer key:keySet){
//				List<gov.nist.healthcare.core.hl7.v2.instance.Element> fieldElms = tm.get(key);
//				for(int i=0; i<fieldElms.size(); i++){
//					gov.nist.healthcare.core.hl7.v2.instance.Element fieldE = fieldElms.get(i);
//					String iPathField = iPath + "." + fieldE.getPath().substring(fieldE.getPath().indexOf(".") + 1); 	
//					
//					if(fieldE.getInstanceNumber() == 1){
//						for(int j=0; j<iss.get(iss.size() - 1).getSegmentTreeNode().getChildren().size(); j++){
//							TreeNode tn = iss.get(iss.size() - 1).getSegmentTreeNode().getChildren().get(j);
//							if(((SegmentTreeModel)tn.getData()).getiPath().equals(iPathField)){
//								((SegmentTreeModel)tn.getData()).setOccurrence(fieldElms.size());
//								if(iPathField.contains("MSH[1].1[1]")){
//									((SegmentTreeModel)tn.getData()).setData(e.getStringRepresentation().substring(3, 4));
//								}else if(iPathField.contains("MSH[1].2[1]")){
//									this.cSeperator = e.getStringRepresentation().substring(4, 5);
//									this.scSeperator = e.getStringRepresentation().substring(7, 8);			
//									((SegmentTreeModel)tn.getData()).setData(e.getStringRepresentation().substring(4, 8));
//								}else {
//									if(((Field)((SegmentTreeModel)tn.getData()).getNode()).getDatatype().getName().equals("varies")){
//										((SegmentTreeModel)tn.getData()).setData(fieldE.getStringRepresentation());
//									}else {
//										updateFieldData(fieldE.getStringRepresentation(), this.cSeperator, this.scSeperator, tn);
//									}
//									
//								}
//							}
//						}
//					}else{
//						for(int j=0; j<iss.get(iss.size() - 1).getSegmentTreeNode().getChildren().size(); j++){
//							TreeNode tn = iss.get(iss.size() - 1).getSegmentTreeNode().getChildren().get(j);
//							if(((SegmentTreeModel)tn.getData()).getiPath().startsWith(iPathField.substring(0, iPathField.lastIndexOf("[")))){
//								
//								((SegmentTreeModel)tn.getData()).setOccurrence(fieldElms.size());
//									SegmentTreeModel clonedSegmentTreeModel = (SegmentTreeModel) ((SegmentTreeModel)tn.getData()).clone();
//									clonedSegmentTreeModel.setiPath(iPathField);
//									TreeNode copyedTN = new DefaultTreeNode(clonedSegmentTreeModel, iss.get(iss.size() - 1).getSegmentTreeNode());
//									for(int k=0; k<tn.getChildren().size();k++){
//										TreeNode cTN = tn.getChildren().get(k);	
//										SegmentTreeModel clonedCTNSegmentTreeModel = (SegmentTreeModel) ((SegmentTreeModel)cTN.getData()).clone();
//										clonedCTNSegmentTreeModel.setiPath(iPathField + "." + (k+1));
//										TreeNode copyedCTN = new DefaultTreeNode(clonedCTNSegmentTreeModel, copyedTN);
//										for(int l=0; l<cTN.getChildren().size();l++){
//											TreeNode scTN = cTN.getChildren().get(l);
//											SegmentTreeModel clonedSCTNSegmentTreeModel = (SegmentTreeModel) ((SegmentTreeModel)scTN.getData()).clone();
//											clonedSCTNSegmentTreeModel.setiPath(iPathField + "." + (k+1)+"." +(l+1));
//											new DefaultTreeNode(clonedSCTNSegmentTreeModel, copyedCTN);
//										}
//									}
//									if(((Field)((SegmentTreeModel)tn.getData()).getNode()).getDatatype().getName().equals("varies")){
//										((SegmentTreeModel)tn.getData()).setData(fieldE.getStringRepresentation());
//									}else {
//										updateFieldData(fieldE.getStringRepresentation(), this.cSeperator, this.scSeperator, copyedTN);
//									}
//									int index = iss.get(iss.size() - 1).getSegmentTreeNode().getChildren().indexOf(tn);
//									tn.getParent().getChildren().add(index + fieldE.getInstanceNumber() - 1, copyedTN);
//									j = iss.get(iss.size() - 1).getSegmentTreeNode().getChildren().size();
//									
//								
//							}
//						}
//					}
//					
//				}
//			}
//		}
//	}
	
	private void loadSegmentRefOrGroup(Message m, SegmentRefOrGroup sg, String path, TreeNode parentTreeNode) throws CloneNotSupportedException {
		
		if(sg instanceof SegmentRef){
			SegmentRef segment = (SegmentRef)sg;
			path = path + "." + segment.getRef().getName();
			String messageId = path.split("\\.")[0];
			String messagePath = path.replace(messageId + ".", "");
			
			MessageTreeModel messageTreeModel = new MessageTreeModel(messageId, segment.getRef().getName(), sg, messagePath, sg.getMin());
			new DefaultTreeNode(sg.getMax(), messageTreeModel, parentTreeNode);		
		}else if(sg instanceof Group){
			Group group = (Group)sg;
			path = group.getName();
			String messageId = path.split("\\.")[0];
			String messagePath = path.replace(messageId + ".", "");

			MessageTreeModel messageTreeModel = new MessageTreeModel(messageId, group.getName(), sg, messagePath, sg.getMin());
			TreeNode treeNode = new DefaultTreeNode(sg.getMax(), messageTreeModel, parentTreeNode);
			List<SegmentRefOrGroup> segmentRefOrGroups = group.getChildren();
			for (SegmentRefOrGroup child : segmentRefOrGroups) {
				loadSegmentRefOrGroup(m, child, path, treeNode);
			}
		}
	}
	
//	private void updateCS(ConformanceStatement cs, String path){
//		((StatementDetails)cs.getStatementDetails()).setPath(path);
//	}
//	
//	private void updateCP(Predicate cp, String path, Usage usage){
//		((StatementDetails)cp.getPredicateDetails()).setPath(path);
//		
//		if(usage.equals(Usage.C_O_O)){
//			cp.setTrueFalseUsages(Usage.C_O_O);
//			cp.setTrueUsage(Usage.O);
//			cp.setFalseUsage(Usage.O);
//		}else if(usage.equals(Usage.C_O_R)){
//			cp.setTrueFalseUsages(Usage.C_O_R);
//			cp.setTrueUsage(Usage.O);
//			cp.setFalseUsage(Usage.R);
//		}else if(usage.equals(Usage.C_O_RE)){
//			cp.setTrueFalseUsages(Usage.C_O_RE);
//			cp.setTrueUsage(Usage.O);
//			cp.setFalseUsage(Usage.RE);
//		}else if(usage.equals(Usage.C_O_X)){
//			cp.setTrueFalseUsages(Usage.C_O_X);
//			cp.setTrueUsage(Usage.O);
//			cp.setFalseUsage(Usage.X);
//		}else if(usage.equals(Usage.C_R_O)){
//			cp.setTrueFalseUsages(Usage.C_R_O);
//			cp.setTrueUsage(Usage.R);
//			cp.setFalseUsage(Usage.O);
//		}else if(usage.equals(Usage.C_R_R)){
//			cp.setTrueFalseUsages(Usage.C_R_R);
//			cp.setTrueUsage(Usage.R);
//			cp.setFalseUsage(Usage.R);
//		}else if(usage.equals(Usage.C_R_RE)){
//			cp.setTrueFalseUsages(Usage.C_R_RE);
//			cp.setTrueUsage(Usage.R);
//			cp.setFalseUsage(Usage.RE);
//		}else if(usage.equals(Usage.C_R_X)){
//			cp.setTrueFalseUsages(Usage.C_R_X);
//			cp.setTrueUsage(Usage.R);
//			cp.setFalseUsage(Usage.X);
//		}else if(usage.equals(Usage.C_RE_O)){
//			cp.setTrueFalseUsages(Usage.C_RE_O);
//			cp.setTrueUsage(Usage.RE);
//			cp.setFalseUsage(Usage.O);
//		}else if(usage.equals(Usage.C_RE_R)){
//			cp.setTrueFalseUsages(Usage.C_RE_R);
//			cp.setTrueUsage(Usage.RE);
//			cp.setFalseUsage(Usage.R);
//		}else if(usage.equals(Usage.C_RE_RE)){
//			cp.setTrueFalseUsages(Usage.C_RE_RE);
//			cp.setTrueUsage(Usage.RE);
//			cp.setFalseUsage(Usage.RE);
//		}else if(usage.equals(Usage.C_RE_X)){
//			cp.setTrueFalseUsages(Usage.C_RE_X);
//			cp.setTrueUsage(Usage.RE);
//			cp.setFalseUsage(Usage.X);
//		}else if(usage.equals(Usage.C_X_O)){
//			cp.setTrueFalseUsages(Usage.C_X_O);
//			cp.setTrueUsage(Usage.X);
//			cp.setFalseUsage(Usage.O);
//		}else if(usage.equals(Usage.C_X_R)){
//			cp.setTrueFalseUsages(Usage.C_X_R);
//			cp.setTrueUsage(Usage.X);
//			cp.setFalseUsage(Usage.R);
//		}else if(usage.equals(Usage.C_X_RE)){
//			cp.setTrueFalseUsages(Usage.C_X_RE);
//			cp.setTrueUsage(Usage.X);
//			cp.setFalseUsage(Usage.RE);
//		}
//	}
	
//	private void removeExistVC(String iPath, Message m) {
//		List<ValidationContext> newVCList = new ArrayList<ValidationContext>();
//		for (ValidationContext vc : m.getValidationContexts()) {
//			if (!vc.getPath().equals(iPath)) {
//				newVCList.add(vc);
//			}
//		}
//		
//		List<InstanceTestDataType> newITDTList = new ArrayList<InstanceTestDataType>();
//		for (InstanceTestDataType itdt : m.getInstanceTestDataTypes()) {
//			if (!itdt.getiPath().equals(iPath)) {
//				newITDTList.add(itdt);
//			}
//		}
//		
//		m.setValidationContexts(newVCList);
//		m.setInstanceTestDataTypes(newITDTList);
//	}
//
//	private void createRVC(String iPath, String nodeName, Message m, String level) {
//		ValidationContext vcPresence = new ValidationContext();
//		vcPresence.setLevel(level);
//		vcPresence.setPath(iPath);
//		StatementDetails sd = new StatementDetails();
//		sd.setId("CREATED-RVC");
//		sd.setConstructionType("Single");
//		sd.setPattern("Presence Check");
//		sd.setLevel(level);
//		sd.setPredicate(false);
//		sd.setAnotherNodeLocation(iPath);
//		sd.setAnotherNodeName(nodeName);
//		sd.setVerb("SHALL be");
//		sd.generateStatementText(true, true);
//		vcPresence.setStatementDetails(sd);
//		vcPresence.setDescription(sd.getStatementText());
//		m.getValidationContexts().add(vcPresence);
//	}
//	
//	private void createNotRVC(String iPath, String nodeName, Message m, String level) {
//		ValidationContext vcPresence = new ValidationContext();
//		vcPresence.setLevel(level);
//		vcPresence.setPath(iPath);
//		StatementDetails sd = new StatementDetails();
//		sd.setId("CREATED-NOTRVC");
//		sd.setConstructionType("Single");
//		sd.setPattern("Presence Check");
//		sd.setLevel(level);
//		sd.setPredicate(false);
//		sd.setAnotherNodeLocation(iPath);
//		sd.setAnotherNodeName(nodeName);
//		sd.setVerb("SHALL NOT be");
//		sd.generateStatementText(true, true);
//		vcPresence.setStatementDetails(sd);
//		vcPresence.setDescription(sd.getStatementText());
//		m.getValidationContexts().add(vcPresence);
//	}
//
//	private void createRVVC(String iPath, String nodeName, String data, Message m, String level) {
//		ValidationContext vcConstant = new ValidationContext();
//		vcConstant.setLevel(level);
//		vcConstant.setPath(iPath);
//		StatementDetails sd = new StatementDetails();
//		sd.setId("CREATED-RVVC");
//		sd.setConstructionType("Single");
//		sd.setPattern("Constant Value Check");
//		sd.setSubPattern("Single Value");
//		sd.setLevel(level);
//
//		sd.setPredicate(false);
//		sd.setTargetNodeLocation(iPath);
//		sd.setTargetNodeName(nodeName);
//		sd.setVerb("SHALL be");
//		sd.setLiteralValue(data);
//		sd.generateStatementText(true, true);
//		vcConstant.setStatementDetails(sd);
//		vcConstant.setDescription(sd.getStatementText());
//		m.getValidationContexts().add(vcConstant);
//	}
//	
//	private void createListRVVC(String iPath, String nodeName, String data, Message m, String level) {
//		ValidationContext vcConstant = new ValidationContext();
//		vcConstant.setLevel(level);
//		vcConstant.setPath(iPath);
//		StatementDetails sd = new StatementDetails();
//		sd.setId("CREATED-LISTRVVC");
//		sd.setConstructionType("Single");
//		sd.setPattern("Constant Value Check");
//		sd.setSubPattern("List Values");
//		sd.setLevel(level);
//
//		sd.setPredicate(false);
//		sd.setTargetNodeLocation(iPath);
//		sd.setTargetNodeName(nodeName);
//		sd.setVerb("should be");
//		sd.setLiteralValues(Arrays.asList(data.split("::")));
//		sd.generateStatementText(true, true);
//		vcConstant.setStatementDetails(sd);
//		vcConstant.setDescription(sd.getStatementText());
//		m.getValidationContexts().add(vcConstant);
//	}

//	private void updateInstanceSegmentByITDT(InstanceTestDataType itdt, InstanceSegment is, List<ValidationContext> vclist) {
//		for(TreeNode fieldTN:is.getSegmentTreeNode().getChildren()){
//			SegmentTreeModel fstm = (SegmentTreeModel)fieldTN.getData();
//			
//			if(fstm.getiPath().equals(itdt.getiPath())){
//				fstm.setType(itdt.getType());
//				if(itdt.getType().equals(TestDataCategorization.TestCaseFixedList)){
//					ValidationContext vc = this.findVCByIPath(vclist, itdt.getiPath());
//					if(vc != null) {
//						String data = "";;
//						for(String s: vc.getStatementDetails().getLiteralValues()){
//							data = data + "::" + s;
//						}
//						data = data.substring(2);
//						fstm.setData(data);
//					}
//				}
//				
//				
//				return;
//			}
//			
//			for(TreeNode componentTN:fieldTN.getChildren()){
//				SegmentTreeModel cstm = (SegmentTreeModel)componentTN.getData();
//				
//				if(cstm.getiPath().equals(itdt.getiPath())){
//					cstm.setType(itdt.getType());
//					if(itdt.getType().equals(TestDataCategorization.TestCaseFixedList)){
//						ValidationContext vc = this.findVCByIPath(vclist, itdt.getiPath());
//						if(vc != null) {
//							String data = "";;
//							for(String s: vc.getStatementDetails().getLiteralValues()){
//								data = data + "::" + s;
//							}
//							data = data.substring(2);
//							cstm.setData(data);
//						}
//					}
//					return;
//				}
//				
//				for(TreeNode subComponentTN:fieldTN.getChildren()){
//					SegmentTreeModel scstm = (SegmentTreeModel)subComponentTN.getData();
//					
//					if(scstm.getiPath().equals(itdt.getiPath())){
//						scstm.setType(itdt.getType());
//						if(itdt.getType().equals(TestDataCategorization.TestCaseFixedList)){
//							ValidationContext vc = this.findVCByIPath(vclist, itdt.getiPath());
//							if(vc != null) {
//								String data = "";;
//								for(String s: vc.getStatementDetails().getLiteralValues()){
//									data = data + "::" + s;
//								}
//								data = data.substring(2);
//								scstm.setData(data);
//							}
//						}
//						return;
//					}
//				}
//			}
//		}
//		
//	}

//	private ValidationContext findVCByIPath(List<ValidationContext> vclist, String path) {
//		for(ValidationContext vc:vclist){
//			if(path.equals(vc.getPath())){
//				return vc;
//			}
//		}
//		return null;
//	}

//	private InstanceSegment findInstanceSegmentByIPath(String iPath, List<InstanceSegment> instanceSegments) {
//		for(InstanceSegment is: instanceSegments){
//			if(iPath.contains(is.getPath())){
//				return is;
//			}
//		}
//		return null;
//	}
	
	
	private void generateMessageStructure(SegmentRefOrGroup srog, Group parentGroup, String messageStructID, Map<String,SegmentRef> messageStrucutreMap){
		if(srog instanceof SegmentRef){
			SegmentRef sr = (SegmentRef)srog;
			if(parentGroup == null){
				messageStrucutreMap.put(sr.getRef().getName(), sr);
			}else{
				messageStrucutreMap.put(parentGroup.getName().replace(messageStructID + ".", "") + "." + sr.getRef().getName(), sr);
			}
		}else if(srog instanceof Group){
			Group gr = (Group)srog;			
			for(SegmentRefOrGroup child:gr.getChildren()){
				this.generateMessageStructure(child, gr, messageStructID, messageStrucutreMap);
			}
		}
		
	}

	public void loadMessageInstance(Message m, List<InstanceSegment> instanceSegments) {
		List<String> ipathList = new ArrayList<String>();
		List<String> pathList = new ArrayList<String>();
		Map<String,SegmentRef> messageStrucutreMap = new LinkedHashMap<String,SegmentRef>();
		for(SegmentRefOrGroup srog:m.getMessageObj().getChildren()){
			this.generateMessageStructure(srog, null, m.getMessageObj().getStructID(), messageStrucutreMap);
		}
		String[] lines = m.getHl7EndcodedMessage().split(System.getProperty("line.separator"));
		List<String> adjustedMessage = new ArrayList<String>();
		boolean validMessage = true; 
		for(String line:lines){
			if(line.length() >3){
				String segmentName = line.substring(0,3);
				String path = this.getPath(messageStrucutreMap.keySet(), segmentName);
				if(path != null){
					pathList.add(this.getPath(messageStrucutreMap.keySet(), segmentName));
					ipathList.add(this.makeIPath(this.getPath(messageStrucutreMap.keySet(), segmentName)));
					adjustedMessage.add(line);
				}else{
					validMessage = false;
				}
			}else{
				validMessage = false;
			}
		}
		
		this.modifyIPath(ipathList);
		int previousPathSize = 1;
		for(int i=0; i<pathList.size();i++){
			String[] pathDivided = ipathList.get(i).split("\\.");
			if(previousPathSize + 1 == pathDivided.length ){	
				instanceSegments.add(new InstanceSegment(ipathList.get(i),pathList.get(i),  lines[i], true ,messageStrucutreMap.get(pathList.get(i))));
			}else{
				instanceSegments.add(new InstanceSegment(ipathList.get(i),pathList.get(i),  lines[i], false ,messageStrucutreMap.get(pathList.get(i))));
			}
			previousPathSize = pathDivided.length;
		}
		
		if(!validMessage){
			String message = "";
			for(String line:adjustedMessage){
				message = message + line + System.getProperty("line.separator");
			}
			m.setHl7EndcodedMessage(message);
		}
	}
	
	private void modifyIPath(List<String> ipathList){
		int previousPathSize = 1;
		String previousSegName = "";
		int segIndex = 1;
		for(int i=0;i<ipathList.size();i++){
			
			String[] pathDivided = ipathList.get(i).split("\\.");
			
			if(previousPathSize + 1 == pathDivided.length ){	
				String groupName = ipathList.get(i).substring(0, ipathList.get(i).lastIndexOf("."));
				
				int groupIndex = 1;
				for(int j=i+1;j<ipathList.size();j++){
					if(ipathList.get(i).equals(ipathList.get(j))){
						groupIndex = groupIndex + 1;
					}
					if(ipathList.get(j).startsWith(groupName)){
						String newGroupName = groupName.substring(0,groupName.length()-3) + "[" + groupIndex + "]";
						ipathList.set(j, ipathList.get(j).replace(groupName, newGroupName));
					}
					
				}
				
			}else if(pathDivided.length  == 1){
				if(previousSegName.equals(pathDivided[0].substring(0,3))){
					ipathList.set(i, ipathList.get(i).replace("[1]", "[" + (segIndex + 1) + "]"));
					segIndex = segIndex + 1;
				}else{
					segIndex = 1;
				}
			}
			previousPathSize = pathDivided.length;
			previousSegName = pathDivided[0].substring(0,3);
		}
		
	}
	
	private String makeIPath(String path){
		return path.replace(".", "[1].") + "[1]";
	}
	
	private String getPath(Set<String> keySet, String segmentName){
		for(String s:keySet){
			if(s.contains(segmentName)){
				return s;
			}
		}
		return null;
	}

	public void genSegmentTree(TreeNode segmentTreeRoot,InstanceSegment selectedInstanceSegment, Message m) {
		String segmentStr = selectedInstanceSegment.getLineStr();
		gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment segment = selectedInstanceSegment.getSegmentRef().getRef();
		
		
		
		if(segment.getName().equals("MSH")){
			segmentStr = "MSH|FieldSeperator|Encoding|" + segmentStr.substring(9);
		}
		
		String[] wholeFieldStr = segmentStr.split("\\|");
		if(wholeFieldStr.length > 0){
			for(int i=0;i<segment.getFields().size();i++){
				String[] fieldStr;
				
				if(i >= wholeFieldStr.length-1){
					fieldStr = new String[]{""};
				}else{
					fieldStr = wholeFieldStr[i+1].split("\\~");
				}
				
				for(int j=0;j<fieldStr.length; j++){
					String path = selectedInstanceSegment.getPath() + "." + (i+1);
					String iPath = selectedInstanceSegment.getIpath() + "." + (i+1) + "[" + (j+1) + "]";
					
					if(segment.getFields().get(i).getDatatype().getComponents().size() > 0){
						FieldModel fieldModel = new FieldModel(path, iPath, segment.getFields().get(i), fieldStr[j], m.findTCAMTConstraintByIPath(iPath), false);
						TreeNode fieldTreeNode = new DefaultTreeNode(fieldModel, segmentTreeRoot);
						
						String[] componentStr = fieldStr[j].split("\\^");
						
						for(int k=0;k<segment.getFields().get(i).getDatatype().getComponents().size();k++){
							String componentPath = path + "." + (k + 1);
							String componentIPath = iPath + "." + (k+1) + "[1]";
							TreeNode componentTreeNode;
							String[] subComponentStr;
							if(k >= componentStr.length){
								if(segment.getFields().get(i).getDatatype().getComponents().get(k).getDatatype().getComponents().size() > 0){
									ComponentModel componentModel = new ComponentModel(componentPath, componentIPath, segment.getFields().get(i).getDatatype().getComponents().get(k), "", m.findTCAMTConstraintByIPath(componentIPath), false);
									componentTreeNode = new DefaultTreeNode(componentModel, fieldTreeNode);
									subComponentStr = new String[]{""};	
								}else{
									ComponentModel componentModel = new ComponentModel(componentPath, componentIPath, segment.getFields().get(i).getDatatype().getComponents().get(k), "", m.findTCAMTConstraintByIPath(componentIPath), true);
									componentTreeNode = new DefaultTreeNode(componentModel, fieldTreeNode);
									subComponentStr = new String[]{""};
								}
								
								
							}else{
								if(segment.getFields().get(i).getDatatype().getComponents().get(k).getDatatype().getComponents().size() > 0){
									ComponentModel componentModel = new ComponentModel(componentPath, componentIPath, segment.getFields().get(i).getDatatype().getComponents().get(k), componentStr[k], m.findTCAMTConstraintByIPath(componentIPath), false);
									componentTreeNode = new DefaultTreeNode(componentModel, fieldTreeNode);
									subComponentStr = componentStr[k].split("\\&");
								}else{
									ComponentModel componentModel = new ComponentModel(componentPath, componentIPath, segment.getFields().get(i).getDatatype().getComponents().get(k), componentStr[k], m.findTCAMTConstraintByIPath(componentIPath), true);
									componentTreeNode = new DefaultTreeNode(componentModel, fieldTreeNode);
									subComponentStr = componentStr[k].split("\\&");	
								}
								
							}
							
							for(int l=0;l<segment.getFields().get(i).getDatatype().getComponents().get(k).getDatatype().getComponents().size();l++){
								String subComponentPath = componentPath + "." + (l + 1);
								String subComponentIPath = componentIPath + "." + (l+1) + "[1]";
								
								if(l >= subComponentStr.length){
									ComponentModel subComponentModel = new ComponentModel(subComponentPath, subComponentIPath, segment.getFields().get(i).getDatatype().getComponents().get(k).getDatatype().getComponents().get(l), "", m.findTCAMTConstraintByIPath(subComponentIPath), true);
									new DefaultTreeNode(subComponentModel, componentTreeNode);
								}else{
									ComponentModel subComponentModel = new ComponentModel(subComponentPath, subComponentIPath, segment.getFields().get(i).getDatatype().getComponents().get(k).getDatatype().getComponents().get(l), subComponentStr[l], m.findTCAMTConstraintByIPath(subComponentIPath), true);
									new DefaultTreeNode(subComponentModel, componentTreeNode);
								}
							}
						}
					}else{
						if(path.equals("MSH.1")){
							FieldModel fieldModel = new FieldModel(path, iPath, segment.getFields().get(i), "|", m.findTCAMTConstraintByIPath(iPath), true);
							new DefaultTreeNode(fieldModel, segmentTreeRoot);
						}else if(path.equals("MSH.2")){
							FieldModel fieldModel = new FieldModel(path, iPath, segment.getFields().get(i), "^" + "~" + "\\" + "&", m.findTCAMTConstraintByIPath(iPath), true);
							new DefaultTreeNode(fieldModel, segmentTreeRoot);
						}else {
							FieldModel fieldModel = new FieldModel(path, iPath, segment.getFields().get(i), fieldStr[j], m.findTCAMTConstraintByIPath(iPath), true);
							new DefaultTreeNode(fieldModel, segmentTreeRoot);	
						}
					}
				}
			}
		}
		
	}

	public String generateLineStr(TreeNode segmentTreeRoot) {
		String lineStr = "";
		String previousPath = "";
		for(TreeNode fieldTN:segmentTreeRoot.getChildren()){
			FieldModel fieldModel = (FieldModel)fieldTN.getData();
			if(fieldModel.getPath().equals(previousPath)){
				lineStr = lineStr + "~";
			}else{
				lineStr = lineStr + "|";	
			}
			if(fieldModel.isLeafNode()){
				lineStr = lineStr + fieldModel.getData();
			}else{
				for(int i=0;i<fieldTN.getChildren().size();i++){
					TreeNode componentTN = fieldTN.getChildren().get(i);
					if(i==0){
						
					}else{
						lineStr = lineStr + "^";
					}
					
					ComponentModel componentModel = (ComponentModel)componentTN.getData();
					if(componentModel.isLeafNode()){
						lineStr = lineStr + componentModel.getData();
					}else{
						String subComponentStr = "";
						for(int j=0;j<componentTN.getChildren().size();j++){
							TreeNode subComponentTN = componentTN.getChildren().get(j);
							if(j==0){
								
							}else{
								subComponentStr = subComponentStr + "&";
							}
							
							ComponentModel subComponentModel = (ComponentModel)subComponentTN.getData();
							subComponentStr = subComponentStr + subComponentModel.getData();
						}
						
						lineStr = lineStr + subComponentStr;
						lineStr = this.removeSeperator(lineStr, "&");
					}
				}
				lineStr = this.removeSeperator(lineStr, "^");
			}
			previousPath = fieldModel.getPath();
		}
		
		lineStr = this.removeSeperator(lineStr, "|");
		
		
		return lineStr.replaceAll("\\r\\n|\\r|\\n", "");
	}

	public void updateHL7Message(int lineNum, String generateLineStr, Message m) {
		String[] lines = m.getHl7EndcodedMessage().split(System.getProperty("line.separator"));
		String hl7Message = "";
		for(int i=0;i<lines.length;i++){
			if(i == lineNum){
				if(i==0){
					hl7Message = "MSH|" + "^" + "~" + "\\" + "&" + generateLineStr.substring(7) + System.getProperty("line.separator");
				}else {
					hl7Message = hl7Message + lines[i].substring(0,3) + generateLineStr + System.getProperty("line.separator");
				}
			}else{
				hl7Message = hl7Message + lines[i] + System.getProperty("line.separator");
			}
		}
		
		m.setHl7EndcodedMessage(hl7Message);
		
	}
	
	public void addRepeatedField(FieldModel fieldModel, TreeNode segmentTreeRoot){
		String path = fieldModel.getPath();
		int count = 0;
		int position = 0;
		for(int i=0; i < segmentTreeRoot.getChildren().size(); i++){
			FieldModel fm = (FieldModel)segmentTreeRoot.getChildren().get(i).getData();
			if(fm.getPath().equals(path)){
				count = count + 1;
				position = i;
			}
		}
		
		String iPath = fieldModel.getIpath().substring(0,fieldModel.getIpath().length()-3) + "[" + (count + 1) + "]";
		
		
		
		FieldModel repeatedFieldModel = new FieldModel(path, iPath, fieldModel.getNode(), "", null, fieldModel.isLeafNode());
		TreeNode addedField = new DefaultTreeNode(repeatedFieldModel, segmentTreeRoot);
		
		if(!fieldModel.isLeafNode()){
			for(int i=0;i<fieldModel.getNode().getDatatype().getComponents().size();i++){
				gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component c = fieldModel.getNode().getDatatype().getComponents().get(i);
				String componentPath = path + "." + (i+1);
				String componentIPath = iPath + "." + (i+1) + "[1]";
				
				if(c.getDatatype().getComponents().size() > 0){
					ComponentModel repatedComponentModel = new ComponentModel(componentPath, componentIPath, c, "", null, false);	
					TreeNode addedComponent = new DefaultTreeNode(repatedComponentModel, addedField);
					
					for(int j=0;j<repatedComponentModel.getNode().getDatatype().getComponents().size();j++){
						gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component sc = repatedComponentModel.getNode().getDatatype().getComponents().get(j);
						String subComponentPath = componentPath + "." + (j+1);
						String subComponentIPath = componentIPath + "." + (j+1) + "[1]";
						
						ComponentModel repatedSubComponentModel = new ComponentModel(subComponentPath, subComponentIPath, sc, "", null, true);
						new DefaultTreeNode(repatedSubComponentModel, addedComponent);
					}
				}else {
					ComponentModel repatedComponentModel = new ComponentModel(componentPath, componentIPath, c, "", null, true);
					new DefaultTreeNode(repatedComponentModel, addedField);
				}
				
			}
		}
		
		segmentTreeRoot.getChildren().add(position + 1, addedField);
		
		
	}

	public List<Integer> generatePosition(String path, TreeNode messageTreeRoot) {
		String[] pathData = path.split("\\.");
		List<Integer> result = new ArrayList<Integer>();
		
		TreeNode currentNode = messageTreeRoot;
		
		for(String p:pathData){
			Integer position = null;
			
			for(int i=0; i < currentNode.getChildren().size(); i++){
				MessageTreeModel model = (MessageTreeModel)currentNode.getChildren().get(i).getData();
				
				String[] currentPathData = model.getPath().split("\\.");
				
				if(p.equals(currentPathData[currentPathData.length - 1])){
					position = i + 1;
					i = currentNode.getChildren().size();
				}
			}
			result.add(position);
			currentNode = currentNode.getChildren().get(position - 1);
		}
		
		
		return result;
	}

	public void populateTreeNode(TreeNode parent) {
		MessageTreeModel model = (MessageTreeModel)parent.getData();
		Object n = model.getNode();
		
		if(n instanceof SegmentRef){
			return;
		}else{
			Group g = (Group)n;
			
			for(SegmentRefOrGroup child:g.getChildren()){
				if(child instanceof SegmentRef){
					SegmentRef segmentRef = (SegmentRef)child;
					String path = model.getPath() + "." + segmentRef.getRef().getName();
					
					MessageTreeModel newModel = new MessageTreeModel(model.getMessageId(), segmentRef.getRef().getName(), segmentRef, path, 0);	
					
					new DefaultTreeNode(segmentRef.getMax(), newModel, parent);
				}else{
					Group group = (Group)child;
					String path = group.getName();
					String messageId = path.split("\\.")[0];
					String messagePath = path.replace(messageId + ".", "");
					MessageTreeModel newModel = new MessageTreeModel(model.getMessageId(), group.getName(), group, messagePath, 0);	
					TreeNode childNode = new DefaultTreeNode(group.getMax(), newModel, parent);
					this.populateTreeNode(childNode);
				}
			}
		}
		
	}

	public String generateHL7Message(TreeNode messageTreeRoot) {
		
		String message = "MSH|" + "^" + "~" + "\\" + "&" + "|" + System.getProperty("line.separator");
		for(TreeNode child:messageTreeRoot.getChildren()){
			message = this.travel(child, message);
		}
		
		return message;
	}

	private String travel(TreeNode node, String message) {
		MessageTreeModel model = (MessageTreeModel)node.getData();
		
		if(model.getNode() instanceof SegmentRef){
			SegmentRef segmentRef = (SegmentRef)model.getNode();
			if(!segmentRef.getRef().getName().equals("MSH")){
				if(!segmentRef.getMax().equals("0")) message =  message + segmentRef.getRef().getName() + "|" + System.getProperty("line.separator");
			}
			return message;
		}else{
			for(TreeNode child:node.getChildren()){
				message = this.travel(child, message);	
			}
		}

		
		
		return message;
	}
}
