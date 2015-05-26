package gov.nist.healthcare.tcamt.service;

import gov.nist.healthcare.tcamt.domain.Message;
import gov.nist.healthcare.tcamt.domain.TCAMTConstraint;
import gov.nist.healthcare.tcamt.domain.data.ComponentModel;
import gov.nist.healthcare.tcamt.domain.data.Constraint;
import gov.nist.healthcare.tcamt.domain.data.FieldModel;
import gov.nist.healthcare.tcamt.domain.data.InstanceSegment;
import gov.nist.healthcare.tcamt.domain.data.MessageTreeModel;
import gov.nist.healthcare.tcamt.domain.data.TestDataCategorization;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.ProfileSerialization;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.ProfileSerializationImpl;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class ManageInstance  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 574095903906379334L;
	
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
	
	private String removeSeperator(String substring, String separator){
		if(substring.substring(substring.length()-1).equals(separator)){
			substring = substring.substring(0, substring.length()-1);
			substring = this.removeSeperator(substring, separator);
		}else {
			return substring;
		}
		
		return substring;
		
	}
	
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
	
	private void generateMessageStructure(SegmentRefOrGroup srog, Group parentGroup, String messageStructID, Map<String,SegmentRef> messageStrucutreMap, Map<String,String> usageMap, String usageList, Map<String,String> positionPathMap, String postionPath){
		if(srog instanceof SegmentRef){
			SegmentRef sr = (SegmentRef)srog;
			if(parentGroup == null){
				messageStrucutreMap.put(sr.getRef().getName(), sr);
				usageMap.put(sr.getRef().getName(), sr.getUsage().name());
				positionPathMap.put(sr.getRef().getName(), "" + sr.getPosition());
			}else{
				messageStrucutreMap.put(parentGroup.getName().replace(messageStructID + ".", "") + "." + sr.getRef().getName(), sr);
				usageMap.put(parentGroup.getName().replace(messageStructID + ".", "") + "." + sr.getRef().getName(), usageList + "-" + sr.getUsage().name());
				positionPathMap.put(parentGroup.getName().replace(messageStructID + ".", "") + "." + sr.getRef().getName(), postionPath + "." + sr.getPosition());
			}
		}else if(srog instanceof Group){
			Group gr = (Group)srog;			
			for(SegmentRefOrGroup child:gr.getChildren()){
				String childUsageList;
				String childPositionPath;
				if(parentGroup == null){
					childUsageList = gr.getUsage().name();
					childPositionPath = "" + gr.getPosition();
				}else{
					childUsageList = usageList + "-" + gr.getUsage().name();
					childPositionPath = postionPath + "." + gr.getPosition();
				}
				this.generateMessageStructure(child, gr, messageStructID, messageStrucutreMap, usageMap, childUsageList, positionPathMap, childPositionPath);
			}
		}
		
	}

	public void loadMessageInstance(Message m, List<InstanceSegment> instanceSegments) {
		List<String> ipathList = new ArrayList<String>();
		List<String> pathList = new ArrayList<String>();
		List<String> iPositionPathList = new ArrayList<String>();
		Map<String,SegmentRef> messageStrucutreMap = new LinkedHashMap<String,SegmentRef>();
		Map<String,String> usageMap = new LinkedHashMap<String,String>();
		Map<String,String> positionPathMap = new LinkedHashMap<String,String>();
		for(SegmentRefOrGroup srog:m.getMessageObj().getChildren()){
			this.generateMessageStructure(srog, null, m.getMessageObj().getStructID(), messageStrucutreMap, usageMap, "", positionPathMap, "");
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
					iPositionPathList.add(this.makeIPath(positionPathMap.get(this.getPath(messageStrucutreMap.keySet(), segmentName))));
					adjustedMessage.add(line);
				}else{
					validMessage = false;
				}
			}else{
				validMessage = false;
			}
		}
		
		this.modifyIPath(ipathList);
		this.modifyIPath(iPositionPathList);
		
		
		int previousPathSize = 1;
		for(int i=0; i<pathList.size();i++){
			String[] pathDivided = ipathList.get(i).split("\\.");
			if(previousPathSize + 1 == pathDivided.length ){	
				instanceSegments.add(new InstanceSegment(ipathList.get(i),pathList.get(i), m.getMessageObj().getStructID(), iPositionPathList.get(i), lines[i], true ,messageStrucutreMap.get(pathList.get(i)),usageMap.get(pathList.get(i))));
			}else{
				instanceSegments.add(new InstanceSegment(ipathList.get(i),pathList.get(i), m.getMessageObj().getStructID(), iPositionPathList.get(i), lines[i], false ,messageStrucutreMap.get(pathList.get(i)), usageMap.get(pathList.get(i))));
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
	
		this.generateXMLFromMesageInstance(m, instanceSegments, true);
		this.generateXMLFromMesageInstance(m, instanceSegments, false);
	}

	private void modifyIPath(List<String> ipathList){
		String previousSegName = "";
		int segIndex = 1;
		for(int i=0;i<ipathList.size();i++){
			
			String[] pathDivided = ipathList.get(i).split("\\.");
			
			if(pathDivided.length > 1){	
				String groupName = ipathList.get(i).substring(0, ipathList.get(i).lastIndexOf("."));
				
				int groupIndex = 0;
				for(int j=i+1;j<ipathList.size();j++){
					if(ipathList.get(i).equals(ipathList.get(j))){
						groupIndex = groupIndex + 1;
					}
					
					if(ipathList.get(j).startsWith(groupName)){
						int currentIndex = Integer.parseInt(groupName.substring(groupName.lastIndexOf("[") + 1, groupName.lastIndexOf("]")));
						String newGroupName = groupName.substring(0,groupName.lastIndexOf("[")) + "[" + (groupIndex + currentIndex) + "]";						
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
					String iPositionPath = selectedInstanceSegment.getiPositionPath() + "." + (i+1) + "[" + (j+1) + "]";
					String usageList = selectedInstanceSegment.getUsageList() + "-" + segment.getFields().get(i).getUsage().name();
					
					if(segment.getFields().get(i).getDatatype().getComponents().size() > 0){
						FieldModel fieldModel = new FieldModel(selectedInstanceSegment.getMessageName(), path, iPath, iPositionPath, usageList, segment.getFields().get(i), fieldStr[j], m.findTCAMTConstraintByIPath(iPath), false);
						TreeNode fieldTreeNode = new DefaultTreeNode(fieldModel, segmentTreeRoot);
						
						String[] componentStr = fieldStr[j].split("\\^");
						
						for(int k=0;k<segment.getFields().get(i).getDatatype().getComponents().size();k++){
							String componentPath = path + "." + (k + 1);
							String componentIPath = iPath + "." + (k+1) + "[1]";
							String componentIPositionPath = iPositionPath + "." + (k+1) + "[1]";
							String componentUsageList = usageList + "-" + segment.getFields().get(i).getDatatype().getComponents().get(k).getUsage().name();
							TreeNode componentTreeNode;
							String[] subComponentStr;
							if(k >= componentStr.length){
								if(segment.getFields().get(i).getDatatype().getComponents().get(k).getDatatype().getComponents().size() > 0){
									ComponentModel componentModel = new ComponentModel(selectedInstanceSegment.getMessageName(), componentPath, componentIPath, componentIPositionPath, componentUsageList, segment.getFields().get(i).getDatatype().getComponents().get(k), "", m.findTCAMTConstraintByIPath(componentIPath), false);
									componentTreeNode = new DefaultTreeNode(componentModel, fieldTreeNode);
									subComponentStr = new String[]{""};	
								}else{
									ComponentModel componentModel = new ComponentModel(selectedInstanceSegment.getMessageName(), componentPath, componentIPath, componentIPositionPath, componentUsageList, segment.getFields().get(i).getDatatype().getComponents().get(k), "", m.findTCAMTConstraintByIPath(componentIPath), true);
									componentTreeNode = new DefaultTreeNode(componentModel, fieldTreeNode);
									subComponentStr = new String[]{""};
								}
								
								
							}else{
								if(segment.getFields().get(i).getDatatype().getComponents().get(k).getDatatype().getComponents().size() > 0){
									ComponentModel componentModel = new ComponentModel(selectedInstanceSegment.getMessageName(), componentPath, componentIPath, componentIPositionPath, componentUsageList, segment.getFields().get(i).getDatatype().getComponents().get(k), componentStr[k], m.findTCAMTConstraintByIPath(componentIPath), false);
									componentTreeNode = new DefaultTreeNode(componentModel, fieldTreeNode);
									subComponentStr = componentStr[k].split("\\&");
								}else{
									ComponentModel componentModel = new ComponentModel(selectedInstanceSegment.getMessageName(), componentPath, componentIPath, componentIPositionPath, componentUsageList, segment.getFields().get(i).getDatatype().getComponents().get(k), componentStr[k], m.findTCAMTConstraintByIPath(componentIPath), true);
									componentTreeNode = new DefaultTreeNode(componentModel, fieldTreeNode);
									subComponentStr = componentStr[k].split("\\&");	
								}
								
							}
							
							for(int l=0;l<segment.getFields().get(i).getDatatype().getComponents().get(k).getDatatype().getComponents().size();l++){
								String subComponentPath = componentPath + "." + (l + 1);
								String subComponentIPath = componentIPath + "." + (l+1) + "[1]";
								String subComponentUsageList = componentUsageList + "-" + segment.getFields().get(i).getDatatype().getComponents().get(k).getDatatype().getComponents().get(l).getUsage().name();
								String subComponentIPositionPath = componentIPositionPath + "." + (l+1) + "[1]";
								if(l >= subComponentStr.length){
									ComponentModel subComponentModel = new ComponentModel(selectedInstanceSegment.getMessageName(), subComponentPath, subComponentIPath, subComponentIPositionPath, subComponentUsageList, segment.getFields().get(i).getDatatype().getComponents().get(k).getDatatype().getComponents().get(l), "", m.findTCAMTConstraintByIPath(subComponentIPath), true);
									new DefaultTreeNode(subComponentModel, componentTreeNode);
								}else{
									ComponentModel subComponentModel = new ComponentModel(selectedInstanceSegment.getMessageName(), subComponentPath, subComponentIPath, subComponentIPositionPath, subComponentUsageList, segment.getFields().get(i).getDatatype().getComponents().get(k).getDatatype().getComponents().get(l), subComponentStr[l], m.findTCAMTConstraintByIPath(subComponentIPath), true);
									new DefaultTreeNode(subComponentModel, componentTreeNode);
								}
							}
						}
					}else{
						if(path.equals("MSH.1")){
							FieldModel fieldModel = new FieldModel(selectedInstanceSegment.getMessageName(), path, iPath, iPositionPath, usageList, segment.getFields().get(i), "|", m.findTCAMTConstraintByIPath(iPath), true);
							new DefaultTreeNode(fieldModel, segmentTreeRoot);
						}else if(path.equals("MSH.2")){
							FieldModel fieldModel = new FieldModel(selectedInstanceSegment.getMessageName(), path, iPath, iPositionPath, usageList, segment.getFields().get(i), "^" + "~" + "\\" + "&", m.findTCAMTConstraintByIPath(iPath), true);
							new DefaultTreeNode(fieldModel, segmentTreeRoot);
						}else {
							FieldModel fieldModel = new FieldModel(selectedInstanceSegment.getMessageName(), path, iPath, iPositionPath, usageList, segment.getFields().get(i), fieldStr[j], m.findTCAMTConstraintByIPath(iPath), true);
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
		String iPositionPath = fieldModel.getiPositionPath().substring(0,fieldModel.getiPositionPath().length()-3) + "[" + (count + 1) + "]";
		
		
		FieldModel repeatedFieldModel = new FieldModel(fieldModel.getMessageName(), path, iPath, iPositionPath, fieldModel.getUsageList(), fieldModel.getNode(), "", null, fieldModel.isLeafNode());
		TreeNode addedField = new DefaultTreeNode(repeatedFieldModel, segmentTreeRoot);
		
		if(!fieldModel.isLeafNode()){
			for(int i=0;i<fieldModel.getNode().getDatatype().getComponents().size();i++){
				gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component c = fieldModel.getNode().getDatatype().getComponents().get(i);
				String componentPath = path + "." + (i+1);
				String componentIPath = iPath + "." + (i+1) + "[1]";
				String componentIPositionPath = iPositionPath + "." + (i+1) + "[1]";
				String componentUsageList = fieldModel.getUsageList() + "-" + c.getUsage().name();
				
				if(c.getDatatype().getComponents().size() > 0){
					ComponentModel repatedComponentModel = new ComponentModel(fieldModel.getMessageName(), componentPath, componentIPath, componentIPositionPath, componentUsageList ,c, "", null, false);	
					TreeNode addedComponent = new DefaultTreeNode(repatedComponentModel, addedField);
					
					for(int j=0;j<repatedComponentModel.getNode().getDatatype().getComponents().size();j++){
						gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component sc = repatedComponentModel.getNode().getDatatype().getComponents().get(j);
						String subComponentPath = componentPath + "." + (j+1);
						String subComponentIPath = componentIPath + "." + (j+1) + "[1]";
						String subComponentIPositionPath = componentIPositionPath + "." + (j+1) + "[1]";
						String subComponentUsageList = componentUsageList + "-" + sc.getUsage().name();
						
						ComponentModel repatedSubComponentModel = new ComponentModel(fieldModel.getMessageName(), subComponentPath, subComponentIPath, subComponentIPositionPath, subComponentUsageList, sc, "", null, true);
						new DefaultTreeNode(repatedSubComponentModel, addedComponent);
					}
				}else {
					ComponentModel repatedComponentModel = new ComponentModel(fieldModel.getMessageName(), componentPath, componentIPath, componentIPositionPath, componentUsageList, c, "", null, true);
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

	public void createConstraintDocument(org.w3c.dom.Document constraintDom, Message m) {
		
		Element elmConstraints = (Element)constraintDom.getElementsByTagName("Constraints").item(0);
		Element elmGroup = (Element)elmConstraints.getElementsByTagName("Group").item(0);
		
		Element elmByName = constraintDom.createElement("ByName");
		String messageName=null;
		for(TCAMTConstraint c: m.getTcamtConstraints()){
			messageName = c.getMessageName();
			String usageList = c.getUsageList();
			String iPositionPath = c.getiPosition();
			String iPath = c.getIpath();
			String tdc = c.getCategorization().getValue();
			
			
			if(c.getCategorization().equals(TestDataCategorization.Indifferent)){
			}else if(c.getCategorization().equals(TestDataCategorization.ContentIndifferent)){
				this.createPresenceCheck(usageList, iPositionPath,iPath, tdc,  elmByName);
			}else if(c.getCategorization().equals(TestDataCategorization.Configurable)){
				this.createPresenceCheck(usageList, iPositionPath,iPath, tdc,  elmByName);
			}else if(c.getCategorization().equals(TestDataCategorization.SystemGenerated)){
				this.createPresenceCheck(usageList, iPositionPath,iPath, tdc,  elmByName);
			}else if(c.getCategorization().equals(TestDataCategorization.TestCaseProper)){
				this.createPresenceCheck(usageList, iPositionPath,iPath, tdc,  elmByName);
			}else if(c.getCategorization().equals(TestDataCategorization.NotValued)){
			}else if(c.getCategorization().equals(TestDataCategorization.TestCaseFixed)){
				this.createPresenceCheck(usageList, iPositionPath,iPath, tdc,  elmByName);
				this.createPlainTextCheck(c.getData(), iPositionPath,iPath, tdc,  elmByName);
			}else if(c.getCategorization().equals(TestDataCategorization.TestCaseFixedList)){
				this.createPresenceCheck(usageList, iPositionPath,iPath, tdc,  elmByName);
				this.createStringListCheck(c.getData(), iPositionPath,iPath, tdc,  elmByName);
			}
		}
		if(messageName != null){
			elmByName.setAttribute("Name", messageName);	
		}
		elmGroup.appendChild(elmByName);
	}
	
	private void createStringListCheck(String values, String iPositionPath, String iPath, String tdc, Element parent){
		Element elmConstraint = parent.getOwnerDocument().createElement("Constraint");
		
		Element elmReference = parent.getOwnerDocument().createElement("Reference");
		elmReference.setAttribute("GeneratedBy", "TCAMT");
		elmReference.setAttribute("Path", iPath);
		elmReference.setAttribute("TestDataCategorization", tdc);
		elmConstraint.appendChild(elmReference);
		
		elmConstraint.setAttribute("ID", "TCAMT_" + iPath);
		elmConstraint.setAttribute("Target", iPositionPath);
		Element elmDescription = parent.getOwnerDocument().createElement("Description");
		elmDescription.appendChild(parent.getOwnerDocument().createTextNode(iPath + " SHALL be one of list values: " + values + " because " + iPath + " is " + tdc + "."));
		Element elmAssertion = parent.getOwnerDocument().createElement("Assertion");
		Element elmStringList = parent.getOwnerDocument().createElement("StringList");
		elmStringList.setAttribute("Path", iPositionPath);
		elmStringList.setAttribute("CSV", values);
		elmStringList.setAttribute("IgnoreCase", "false");
		elmAssertion.appendChild(elmStringList);
		elmConstraint.appendChild(elmDescription);
		elmConstraint.appendChild(elmAssertion);
		parent.appendChild(elmConstraint);
	}
	
	private void createPlainTextCheck(String value, String iPositionPath, String iPath, String tdc, Element parent){
		Element elmConstraint = parent.getOwnerDocument().createElement("Constraint");
		
		Element elmReference = parent.getOwnerDocument().createElement("Reference");
		elmReference.setAttribute("GeneratedBy", "TCAMT");
		elmReference.setAttribute("Path", iPath);
		elmReference.setAttribute("TestDataCategorization", tdc);
		elmConstraint.appendChild(elmReference);
		
		elmConstraint.setAttribute("ID", "TCAMT_" + iPath);
		elmConstraint.setAttribute("Target", iPositionPath);
		Element elmDescription = parent.getOwnerDocument().createElement("Description");
		elmDescription.appendChild(parent.getOwnerDocument().createTextNode(iPath + " SHALL be valued '" + value + "' because " + iPath + " is " + tdc + "."));
		Element elmAssertion = parent.getOwnerDocument().createElement("Assertion");
		Element elmPlainText = parent.getOwnerDocument().createElement("PlainText");
		elmPlainText.setAttribute("Path", iPositionPath);
		elmPlainText.setAttribute("Text", value);
		elmPlainText.setAttribute("IgnoreCase", "false");
		elmAssertion.appendChild(elmPlainText);
		elmConstraint.appendChild(elmDescription);
		elmConstraint.appendChild(elmAssertion);
		parent.appendChild(elmConstraint);
	}
	
	private void createPresenceCheck(String usageList, String iPositionPath, String iPath, String tdc, Element parent){
		String[] uList = usageList.split("-");
		String myIPositionPath = "";
		String myIPath = "";
		for(int i=0; i<uList.length; i++){
			if(i==0){
				myIPositionPath = iPositionPath.split("\\.")[i];
				myIPath = iPath.split("\\.")[i];
			}else {
				myIPositionPath = myIPositionPath + "." + iPositionPath.split("\\.")[i];
				myIPath = myIPath + "." + iPath.split("\\.")[i];
			}			
			if(!uList[i].equals("R")){
				Element elmConstraint = parent.getOwnerDocument().createElement("Constraint");
				
				Element elmReference = parent.getOwnerDocument().createElement("Reference");
				elmReference.setAttribute("GeneratedBy", "TCAMT");
				elmReference.setAttribute("Path", iPath);
				elmReference.setAttribute("TestDataCategorization", tdc);
				elmConstraint.appendChild(elmReference);
				
				elmConstraint.setAttribute("ID", "TCAMT_" + iPath + "_" + (i+1));
				elmConstraint.setAttribute("Target", myIPositionPath);
				Element elmDescription = parent.getOwnerDocument().createElement("Description");
				elmDescription.appendChild(parent.getOwnerDocument().createTextNode(myIPath + " SHALL be valued, because " + iPath + " is " + tdc + "."));
				Element elmAssertion = parent.getOwnerDocument().createElement("Assertion");
				Element elmPresence = parent.getOwnerDocument().createElement("Presence");
				elmPresence.setAttribute("Path", myIPositionPath);
				elmAssertion.appendChild(elmPresence);
				elmConstraint.appendChild(elmDescription);
				elmConstraint.appendChild(elmAssertion);
				parent.appendChild(elmConstraint);
			}
		}
	}

	private void generateXMLFromMesageInstance(Message message, List<InstanceSegment> instanceSegments, boolean isSTD) {
		try {
			String messageName = message.getMessageObj().getStructID();
			org.w3c.dom.Document xmlHL7MessageInstanceDom = XMLManager.stringToDom("<" + messageName + "/>");
			Element rootElm = (Element)xmlHL7MessageInstanceDom.getElementsByTagName(messageName).item(0);
			
			rootElm.setAttribute("xmlns", "urn:hl7-org:v2xml");
			rootElm.setAttribute("xmlns:xsi", "urn:hl7-org:v2xml");
			rootElm.setAttribute("xsi:schemaLocation", "urn:hl7-org:v2xml " + messageName + ".xsd");

			
			for(InstanceSegment instanceSegment:instanceSegments){
				String[] iPathList = instanceSegment.getIpath().split("\\.");
				if(iPathList.length == 1){
					Element segmentElm = xmlHL7MessageInstanceDom.createElement(iPathList[0].substring(0, iPathList[0].lastIndexOf("[")));
					if(isSTD) this.generateSegment(segmentElm, instanceSegment);
					else this.generateNISTSegment(segmentElm, instanceSegment);
					rootElm.appendChild(segmentElm);
				}else {
					Element parentElm = rootElm;
					
					for(int i=0; i<iPathList.length; i++){
						String iPath = iPathList[i];
						if(i==iPathList.length - 1){
							Element segmentElm = xmlHL7MessageInstanceDom.createElement(iPath.substring(0, iPath.lastIndexOf("[")));
							if(isSTD) this.generateSegment(segmentElm, instanceSegment);
							else this.generateNISTSegment(segmentElm, instanceSegment);
							parentElm.appendChild(segmentElm);
						}else {
							String groupName = iPath.substring(0,iPath.lastIndexOf("["));
							int groupIndex = Integer.parseInt(iPath.substring(iPath.lastIndexOf("[") + 1,iPath.lastIndexOf("]")));
							
							NodeList groups = parentElm.getElementsByTagName(messageName + "." + groupName);
							if(groups == null || groups.getLength()<groupIndex){
								Element group = xmlHL7MessageInstanceDom.createElement(messageName + "." + groupName);
								parentElm.appendChild(group);
								parentElm = group;
								
							}else {
								parentElm = (Element)groups.item(groupIndex - 1);
							}	
						}
					}
				}
				
			}
			
			if(isSTD) message.setXmlEncodedSTDMessage(XMLManager.docToString(xmlHL7MessageInstanceDom));
			else message.setXmlEncodedNISTMessage(XMLManager.docToString(xmlHL7MessageInstanceDom));
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
			
	}

	private void generateNISTSegment(Element segmentElm, InstanceSegment instanceSegment) {
		String lineStr = instanceSegment.getLineStr();
		String segmentName = lineStr.substring(0,3);		
		
		if(lineStr.startsWith("MSH")){
			lineStr = "MSH|%SEGMENTDVIDER%|%ENCODINGDVIDER%"+ lineStr.substring(8);
		}
		
		String[] fieldStrs = lineStr.substring(4).split("\\|");
		
		for(int i=0; i<fieldStrs.length; i ++){
			String[] fieldStrRepeats = fieldStrs[i].split("\\~");
			for(String fieldStr:fieldStrRepeats){
				if(fieldStr.equals("%SEGMENTDVIDER%")){
					Element fieldElm = segmentElm.getOwnerDocument().createElement("MSH.1");
					Text value = segmentElm.getOwnerDocument().createTextNode("|");
					fieldElm.appendChild(value);
					segmentElm.appendChild(fieldElm);
				}else if(fieldStr.equals("%ENCODINGDVIDER%")){
					Element fieldElm = segmentElm.getOwnerDocument().createElement("MSH.2");
					Text value = segmentElm.getOwnerDocument().createTextNode("^~\\&");
					fieldElm.appendChild(value);
					segmentElm.appendChild(fieldElm);
				}else {
					if(fieldStr != null && !fieldStr.equals("")){
						Element fieldElm = segmentElm.getOwnerDocument().createElement(segmentName + "." + (i+1));
						String[] componentStrs = fieldStr.split("\\^");
						
						if(componentStrs.length == 1){
							Text value = segmentElm.getOwnerDocument().createTextNode(fieldStr);
							fieldElm.appendChild(value);
						}else{
							for(int j=0; j<componentStrs.length;j++){
								String componentStr = componentStrs[j];
								if(componentStr != null && !componentStr.equals("")){
									Element componentElm = segmentElm.getOwnerDocument().createElement(segmentName + "." + (i+1) + "." + (j+1));
									String[] subComponentStrs = componentStr.split("\\&");
									if(subComponentStrs.length == 1){
										Text value = segmentElm.getOwnerDocument().createTextNode(componentStr);
										componentElm.appendChild(value);
									}else{
										for(int k=0; k<subComponentStrs.length;k++){
											Element subComponentElm = segmentElm.getOwnerDocument().createElement(segmentName + "." + (i+1) + "." + (j+1) + "." + (k+1));
											Text value = segmentElm.getOwnerDocument().createTextNode(subComponentStrs[k]);
											subComponentElm.appendChild(value);
											componentElm.appendChild(subComponentElm);
										}
									}
									
									fieldElm.appendChild(componentElm);
								}
								
							}
						}
						segmentElm.appendChild(fieldElm);
					}
				}
			}
		}
	}

	private void generateSegment(Element segmentElm, InstanceSegment instanceSegment) {
		String lineStr = instanceSegment.getLineStr();
		String segmentName = lineStr.substring(0,3);
		Segment segment = instanceSegment.getSegmentRef().getRef();
		
		String variesDT = "";
		
		
		if(lineStr.startsWith("MSH")){
			lineStr = "MSH|%SEGMENTDVIDER%|%ENCODINGDVIDER%"+ lineStr.substring(8);
		}
		
		
		String[] fieldStrs = lineStr.substring(4).split("\\|");
		
		for(int i=0; i<fieldStrs.length; i ++){
			String[] fieldStrRepeats = fieldStrs[i].split("\\~");
			for(String fieldStr:fieldStrRepeats){
				if(fieldStr.equals("%SEGMENTDVIDER%")){
					Element fieldElm = segmentElm.getOwnerDocument().createElement("MSH.1");
					Text value = segmentElm.getOwnerDocument().createTextNode("|");
					fieldElm.appendChild(value);
					segmentElm.appendChild(fieldElm);
				}else if(fieldStr.equals("%ENCODINGDVIDER%")){
					Element fieldElm = segmentElm.getOwnerDocument().createElement("MSH.2");
					Text value = segmentElm.getOwnerDocument().createTextNode("^~\\&");
					fieldElm.appendChild(value);
					segmentElm.appendChild(fieldElm);
				}else {
					if(fieldStr != null && !fieldStr.equals("")){
						if(i<segment.getFields().size()){
							Field field = segment.getFields().get(i);
							Element fieldElm = segmentElm.getOwnerDocument().createElement(segmentName + "." + field.getPosition());
							if(field.getDatatype().getComponents() == null || field.getDatatype().getComponents().size() == 0 ){
								if(lineStr.startsWith("OBX")){
									if(field.getPosition().equals(2)){
										variesDT = fieldStr;
										Text value = segmentElm.getOwnerDocument().createTextNode(fieldStr);
										fieldElm.appendChild(value);
									}else if (field.getPosition().equals(5)){
										String[] componentStrs = fieldStr.split("\\^");
										
										for(int index = 0 ; index <componentStrs.length; index++){
											String componentStr = componentStrs[index];
											Element componentElm = segmentElm.getOwnerDocument().createElement(variesDT + "." + (index+1));
											Text value = segmentElm.getOwnerDocument().createTextNode(componentStr);
											componentElm.appendChild(value);
											fieldElm.appendChild(componentElm);
											
										}
									}
								}else{
									Text value = segmentElm.getOwnerDocument().createTextNode(fieldStr);
									fieldElm.appendChild(value);
								}
							}else{
								String[] componentStrs = fieldStr.split("\\^");
								String componentDataTypeName = field.getDatatype().getName();
								for(int j=0; j < componentStrs.length; j++){
									if(j < field.getDatatype().getComponents().size()){
										Component component = field.getDatatype().getComponents().get(j);
										String componentStr = componentStrs[j];
										if(componentStr!=null && !componentStr.equals("")){
											Element componentElm = segmentElm.getOwnerDocument().createElement(componentDataTypeName + "." + (j+1));
											if(component.getDatatype().getComponents() == null || component.getDatatype().getComponents().size() == 0 ){
												Text value = segmentElm.getOwnerDocument().createTextNode(componentStr);
												componentElm.appendChild(value);
											}else{
												String[] subComponentStrs = componentStr.split("\\&");
												String subComponentDataTypeName = component.getDatatype().getName();
												
												for(int k=0; k < subComponentStrs.length; k++){
													String subComponentStr = subComponentStrs[k];
													if(subComponentStr!=null && !subComponentStr.equals("")){
														Element subComponentElm = segmentElm.getOwnerDocument().createElement(subComponentDataTypeName + "." + (k+1));
														Text value = segmentElm.getOwnerDocument().createTextNode(subComponentStr);
														subComponentElm.appendChild(value);
														componentElm.appendChild(subComponentElm);
													}
												}
												
											}
											fieldElm.appendChild(componentElm);
										}
									}
								}
								
							}
							segmentElm.appendChild(fieldElm);
						}
					}
				}
			}
		}
			
		
	}

	public TreeNode generateConstraintTree(Message m) {
		TreeNode root = new DefaultTreeNode("", null);
		for(TCAMTConstraint c:m.getTcamtConstraints()){
			TreeNode cTreeNode = new DefaultTreeNode(c, root);
			cTreeNode.setExpanded(true);
			String usageList = c.getUsageList();
			String iPositionPath = c.getiPosition();
			String iPath = c.getIpath();
			if(c.getCategorization().equals(TestDataCategorization.Indifferent)){
			}else if(c.getCategorization().equals(TestDataCategorization.ContentIndifferent)){
				this.createPresenceTree(usageList, iPositionPath, iPath, cTreeNode);
			}else if(c.getCategorization().equals(TestDataCategorization.Configurable)){
				this.createPresenceTree(usageList, iPositionPath, iPath, cTreeNode);
			}else if(c.getCategorization().equals(TestDataCategorization.SystemGenerated)){
				this.createPresenceTree(usageList, iPositionPath, iPath, cTreeNode);
			}else if(c.getCategorization().equals(TestDataCategorization.TestCaseProper)){
				this.createPresenceTree(usageList, iPositionPath, iPath, cTreeNode);
			}else if(c.getCategorization().equals(TestDataCategorization.NotValued)){
			}else if(c.getCategorization().equals(TestDataCategorization.TestCaseFixed)){
				this.createPresenceTree(usageList, iPositionPath, iPath, cTreeNode);
				this.createPlainTextTree(c.getData(), iPositionPath,iPath, cTreeNode);
			}else if(c.getCategorization().equals(TestDataCategorization.TestCaseFixedList)){
				this.createPresenceTree(usageList, iPositionPath, iPath, cTreeNode);
				this.createStringListTree(c.getData(), iPositionPath, iPath, cTreeNode);
			}
		}
			
			
		return root;
	}

	private void createStringListTree(String data, String iPositionPath, String iPath, TreeNode parent) {
		Constraint c = new Constraint();
		c.setData(data);
		c.setDescription("The value of " + iPath + " SHALL be one of List Values: " + data);
		c.setId("TCAMT");
		c.setIpath(iPath);
		c.setiPositionPath(iPositionPath);
		c.setType("StringList");
		new DefaultTreeNode(c, parent);
		
	}

	private void createPlainTextTree(String data, String iPositionPath, String iPath, TreeNode parent) {
		Constraint c = new Constraint();
		c.setData(data);
		c.setDescription("The value of " + iPath + " SHALL be '" + data + "'.");
		c.setId("TCAMT");
		c.setIpath(iPath);
		c.setiPositionPath(iPositionPath);
		c.setType("PlainText");
		new DefaultTreeNode(c, parent);
		
	}

	private void createPresenceTree(String usageList, String iPositionPath, String iPath,  TreeNode parent) {
		String[] uList = usageList.split("-");
		String myIPositionPath = "";
		String myIPath = "";
		for(int i=0; i<uList.length; i++){
			if(i==0){
				myIPath = iPath.split("\\.")[i];
				myIPositionPath = iPositionPath.split("\\.")[i];
			}else {
				myIPath = myIPath + "." + iPath.split("\\.")[i];
				myIPositionPath = myIPositionPath + "." + iPositionPath.split("\\.")[i];
			}			
			if(!uList[i].equals("R")){
				Constraint c = new Constraint();
				c.setDescription(myIPath + " SHALL be valued.");
				c.setId("TCAMT");
				c.setIpath(myIPath);
				c.setiPositionPath(myIPositionPath);
				c.setType("Presence");
				new DefaultTreeNode(c, parent);
			}
		}
		
	}
}
