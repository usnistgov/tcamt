package gov.nist.healthcare.tcamt.service;

import gov.nist.healthcare.tcamt.domain.IntegratedProfile;
import gov.nist.healthcare.tcamt.domain.Message;
import gov.nist.healthcare.tcamt.domain.TCAMTConstraint;
import gov.nist.healthcare.tcamt.domain.data.ComponentModel;
import gov.nist.healthcare.tcamt.domain.data.Constraint;
import gov.nist.healthcare.tcamt.domain.data.FieldModel;
import gov.nist.healthcare.tcamt.domain.data.InstanceSegment;
import gov.nist.healthcare.tcamt.domain.data.MessageTreeModel;
import gov.nist.healthcare.tcamt.domain.data.TestDataCategorization;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Usage;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.ProfileSerialization;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.ProfileSerializationImpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class ManageInstance implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 574095903906379334L;

	public TreeNode loadMessage(Message m) throws CloneNotSupportedException {
		TreeNode treeNode = new DefaultTreeNode("root", null);
		gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message mp = null;
		Profile p = null;

		if (m.getConformanceProfile() != null) {
			IntegratedProfile ip = m.getConformanceProfile()
					.getIntegratedProfile();
			ProfileSerialization ps = new ProfileSerializationImpl();
			p = ps.deserializeXMLToProfile(ip.getProfile(), ip.getValueSet(),
					ip.getConstraints());
			for (gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message message : p
					.getMessages().getChildren()) {
				if (message.getIdentifier().equals(
						m.getConformanceProfile().getConformanceProfileId())) {
					mp = message;
				}
			}
		}

		if (mp != null) {
			m.setMessageObj(mp);
			m.setSegments(p.getSegments());
			m.setDatatypes(p.getDatatypes());
			m.setTables(p.getTables());
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

	private String removeSeperator(String substring, String separator) {
		if (substring.substring(substring.length() - 1).equals(separator)) {
			substring = substring.substring(0, substring.length() - 1);
			substring = this.removeSeperator(substring, separator);
		} else {
			return substring;
		}

		return substring;

	}

	private void loadSegmentRefOrGroup(Message m, SegmentRefOrGroup sg, String path, TreeNode parentTreeNode) throws CloneNotSupportedException {
		if (sg instanceof SegmentRef) {
			SegmentRef segment = (SegmentRef) sg;
			String segmentName = m.getSegments().findOne(segment.getRef()).getName();
			path = path + "." + segmentName;
			String messageId = path.split("\\.")[0];
			String messagePath = path.replace(messageId + ".", "");

			MessageTreeModel messageTreeModel = new MessageTreeModel(messageId, segmentName, sg, messagePath, sg.getMin());
			new DefaultTreeNode(sg.getMax(), messageTreeModel, parentTreeNode);
		} else if (sg instanceof Group) {
			Group group = (Group) sg;
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

	private void generateMessageStructure(Message m, SegmentRefOrGroup srog,
			Group parentGroup, String messageStructID,
			Map<String, SegmentRef> messageStrucutreMap,
			Map<String, String> usageMap, String usageList,
			Map<String, String> positionPathMap, String postionPath) {
		if (srog instanceof SegmentRef) {
			SegmentRef sr = (SegmentRef) srog;
			String segmentName = m.getSegments().findOne(sr.getRef()).getName();
			if (parentGroup == null) {
				messageStrucutreMap.put(segmentName, sr);
				usageMap.put(segmentName, sr.getUsage().name());
				positionPathMap.put(segmentName, "" + sr.getPosition());
			} else {
				messageStrucutreMap.put(
						parentGroup.getName()
								.replace(messageStructID + ".", "")
								+ "."
								+ segmentName, sr);
				usageMap.put(
						parentGroup.getName()
								.replace(messageStructID + ".", "")
								+ "."
								+ segmentName, usageList + "-"
								+ sr.getUsage().name());
				positionPathMap.put(
						parentGroup.getName()
								.replace(messageStructID + ".", "")
								+ "."
								+ segmentName,
						postionPath + "." + sr.getPosition());
			}
		} else if (srog instanceof Group) {
			Group gr = (Group) srog;
			for (SegmentRefOrGroup child : gr.getChildren()) {
				String childUsageList;
				String childPositionPath;
				if (parentGroup == null) {
					childUsageList = gr.getUsage().name();
					childPositionPath = "" + gr.getPosition();
				} else {
					childUsageList = usageList + "-" + gr.getUsage().name();
					childPositionPath = postionPath + "." + gr.getPosition();
				}
				this.generateMessageStructure(m, child, gr, messageStructID,
						messageStrucutreMap, usageMap, childUsageList,
						positionPathMap, childPositionPath);
			}
		}

	}

	public void loadMessageInstance(Message m, List<InstanceSegment> instanceSegments, String testCaseName) throws Exception {
		List<String> ipathList = new ArrayList<String>();
		List<String> pathList = new ArrayList<String>();
		List<String> iPositionPathList = new ArrayList<String>();
		Map<String, SegmentRef> messageStrucutreMap = new LinkedHashMap<String, SegmentRef>();
		Map<String, String> usageMap = new LinkedHashMap<String, String>();
		Map<String, String> positionPathMap = new LinkedHashMap<String, String>();

		for (SegmentRefOrGroup srog : m.getMessageObj().getChildren()) {
			this.generateMessageStructure(m, srog, null, m.getMessageObj().getStructID(), messageStrucutreMap, usageMap, "", positionPathMap, "");
		}
		String[] lines = m.getHl7EndcodedMessage().split(
				System.getProperty("line.separator"));
		List<String> adjustedMessage = new ArrayList<String>();
		boolean validMessage = true;
		for (String line : lines) {
			if (line.length() > 3) {
				String segmentName = line.substring(0, 3);
				String path = this.getPath(messageStrucutreMap.keySet(), segmentName);
				if (path != null) {
					pathList.add(this.getPath(messageStrucutreMap.keySet(), segmentName));
					ipathList.add(this.makeIPath(this.getPath(messageStrucutreMap.keySet(), segmentName)));
					iPositionPathList.add(this.makeIPath(positionPathMap.get(this.getPath(messageStrucutreMap.keySet(),segmentName))));
					adjustedMessage.add(line);
				} else {
					validMessage = false;
				}
			} else {
				validMessage = false;
			}
		}

		this.modifyIPath(ipathList);
		this.modifyIPath(iPositionPathList);

		int previousPathSize = 1;
		for (int i = 0; i < pathList.size(); i++) {
			String[] pathDivided = ipathList.get(i).split("\\.");
			if (previousPathSize + 1 == pathDivided.length) {
				instanceSegments.add(new InstanceSegment(ipathList.get(i),
						pathList.get(i), m.getMessageObj().getStructID(),
						iPositionPathList.get(i), lines[i], true,
						messageStrucutreMap.get(pathList.get(i)), usageMap
								.get(pathList.get(i))));
			} else {
				instanceSegments.add(new InstanceSegment(ipathList.get(i),
						pathList.get(i), m.getMessageObj().getStructID(),
						iPositionPathList.get(i), lines[i], false,
						messageStrucutreMap.get(pathList.get(i)), usageMap
								.get(pathList.get(i))));
			}
			previousPathSize = pathDivided.length;
		}

		if (!validMessage) {
			String message = "";
			for (String line : adjustedMessage) {
				message = message + line + System.getProperty("line.separator");
			}
			m.setHl7EndcodedMessage(message);
		}

		this.generateXMLFromMesageInstance(m, instanceSegments, true, testCaseName);
		this.generateXMLFromMesageInstance(m, instanceSegments, false, testCaseName);
		this.generateXMLforMessageContent(m, instanceSegments);
	}

	private void generateXMLforMessageContent(Message m, List<InstanceSegment> instanceSegments) throws Exception {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		docBuilder = docFactory.newDocumentBuilder();

		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("MessageContent");
		doc.appendChild(rootElement);

		for (InstanceSegment instanceSegment : instanceSegments) {
			Segment segment = m.getSegments().findOne(instanceSegment.getSegmentRef().getRef());
			String segName = segment.getName();
			String segDesc = segment.getDescription();
			String segmentiPath = instanceSegment.getIpath();
			String obx5DTStr = "";

			Element segmentElement = doc.createElement("Segment");
			segmentElement.setAttribute("Name", segName);
			segmentElement.setAttribute("Description", segDesc);
			rootElement.appendChild(segmentElement);

			for (Field field : segment.getFields()) {
				if (field.getUsage().equals(Usage.R) || field.getUsage().equals(Usage.RE) || field.getUsage().equals(Usage.C)) {
					String wholeFieldStr = this.getFieldStrFromSegment(segName, instanceSegment, field.getPosition());
					int fieldRepeatIndex = 0;

					for (String fieldStr : wholeFieldStr.split("\\~")) {
						Datatype fieldDT = m.getDatatypes().findOne(field.getDatatype());
						if (segName.equals("OBX") && field.getPosition() == 2) {
							obx5DTStr = fieldStr;
						}

						if (segName.equals("OBX") && field.getPosition() == 5) {
							// TODO OBX Dynamic mapping needed
							fieldDT = m.getDatatypes().findOneDatatypeByBase(obx5DTStr);
						}
						
						if (segName.equals("MSH") && field.getPosition() == 1) {
							fieldStr = "|";
						}
						
						if (segName.equals("MSH") && field.getPosition() == 2) {
							fieldStr = "^~\\&";
						}

						fieldRepeatIndex = fieldRepeatIndex + 1;
						String fieldiPath = "." + field.getPosition() + "[" + fieldRepeatIndex + "]";
						if (fieldDT == null || fieldDT.getComponents() == null || fieldDT.getComponents().size() == 0) {
							Element fieldElement = doc.createElement("Field");
							fieldElement.setAttribute("Location", segName + "." + field.getPosition());
							fieldElement.setAttribute("DataElement", field.getName());
							fieldElement.setAttribute("Data", fieldStr);
							fieldElement.setAttribute("Categrization", this.findTestDataCategorization(m, segmentiPath + fieldiPath));
							segmentElement.appendChild(fieldElement);
						} else {
							Element fieldElement = doc.createElement("Field");
							fieldElement.setAttribute("Location", segName + "." + field.getPosition());
							fieldElement.setAttribute("DataElement", field.getName());
							segmentElement.appendChild(fieldElement);

							for (Component c : fieldDT.getComponents()) {
								String componentiPath = "." + c.getPosition() + "[1]";
								if (c.getUsage().equals(Usage.R) || c.getUsage().equals(Usage.RE) || c.getUsage().equals(Usage.C)) {
									String componentStr = this .getComponentStrFromField(fieldStr, c.getPosition());
									if (m.getDatatypes().findOne(c.getDatatype()).getComponents() == null || m.getDatatypes().findOne(c.getDatatype()).getComponents().size() == 0) {
										Element componentElement = doc.createElement("Component");
										componentElement.setAttribute("Location", segName + "." + field.getPosition() + "." + c.getPosition());
										componentElement.setAttribute("DataElement", c.getName());
										componentElement.setAttribute("Data", componentStr);
										componentElement.setAttribute("Categrization", this.findTestDataCategorization(m, segmentiPath + fieldiPath + componentiPath));
										fieldElement.appendChild(componentElement);
									} else {
										Element componentElement = doc.createElement("Component");
										componentElement.setAttribute("Location", segName + "." + field.getPosition() + "." + c.getPosition());
										componentElement.setAttribute("DataElement", c.getName());
										fieldElement.appendChild(componentElement);

										for (Component sc : m.getDatatypes().findOne(c.getDatatype()).getComponents()) {
											String subcomponentiPath = "." + sc.getPosition() + "[1]";
											String subcomponentStr = this.getSubComponentStrFromField(componentStr, sc.getPosition());

											Element subComponentElement = doc.createElement("SubComponent");
											subComponentElement.setAttribute("Location", segName + "." + field.getPosition() + "." + c.getPosition() + "." + sc.getPosition());
											subComponentElement.setAttribute("DataElement", sc.getName());
											subComponentElement.setAttribute("Data", subcomponentStr);
											subComponentElement.setAttribute("Categrization", this.findTestDataCategorization(m, segmentiPath + fieldiPath + componentiPath + subcomponentiPath));
											componentElement.appendChild(subComponentElement);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		m.setXmlEncodedMessageContent(XMLManager.docToString(doc));
	}
	
	private void modifyIPath(List<String> ipathList) {
		String previousSegName = "";
		int segIndex = 1;
		for (int i = 0; i < ipathList.size(); i++) {

			String[] pathDivided = ipathList.get(i).split("\\.");

			if (pathDivided.length > 1) {
				String groupName = ipathList.get(i).substring(0,
						ipathList.get(i).lastIndexOf("."));

				int groupIndex = 0;
				for (int j = i + 1; j < ipathList.size(); j++) {
					if (ipathList.get(i).equals(ipathList.get(j))) {
						groupIndex = groupIndex + 1;
					}

					if (ipathList.get(j).startsWith(groupName)) {
						int currentIndex = Integer.parseInt(groupName
								.substring(groupName.lastIndexOf("[") + 1,
										groupName.lastIndexOf("]")));
						String newGroupName = groupName.substring(0,
								groupName.lastIndexOf("["))
								+ "[" + (groupIndex + currentIndex) + "]";
						ipathList.set(
								j,
								ipathList.get(j).replace(groupName,
										newGroupName));
					}

				}

			} else if (pathDivided.length == 1) {
				if (previousSegName.equals(pathDivided[0].substring(0, 3))) {
					ipathList.set(
							i,
							ipathList.get(i).replace("[1]",
									"[" + (segIndex + 1) + "]"));
					segIndex = segIndex + 1;
				} else {
					segIndex = 1;
				}
			}
			previousSegName = pathDivided[0].substring(0, 3);
		}

	}

	private String makeIPath(String path) {
		return path.replace(".", "[1].") + "[1]";
	}

	private String getPath(Set<String> keySet, String segmentName) {
		for (String s : keySet) {
			if (s.contains(segmentName)) {
				return s;
			}
		}
		return null;
	}

	public void genSegmentTree(TreeNode segmentTreeRoot,
			InstanceSegment selectedInstanceSegment, Message m) {
		String segmentStr = selectedInstanceSegment.getLineStr();
		gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment segment = m
				.getSegments().findOne(
						selectedInstanceSegment.getSegmentRef().getRef());

		if (segment.getName().equals("MSH")) {
			segmentStr = "MSH|FieldSeperator|Encoding|"
					+ segmentStr.substring(9);
		}

		String[] wholeFieldStr = segmentStr.split("\\|");
		String obx5DTStr = "";
		if (wholeFieldStr.length > 0) {
			for (int i = 0; i < segment.getFields().size(); i++) {
				String[] fieldStr;

				if (i >= wholeFieldStr.length - 1) {
					fieldStr = new String[] { "" };
				} else {
					fieldStr = wholeFieldStr[i + 1].split("\\~");
				}
				
				for (int j = 0; j < fieldStr.length; j++) {
					String path = selectedInstanceSegment.getPath() + "." + (i + 1);
					String iPath = selectedInstanceSegment.getIpath() + "." + (i + 1) + "[" + (j + 1) + "]";
					String iPositionPath = selectedInstanceSegment.getiPositionPath() + "." + (i + 1) + "[" + (j + 1) + "]";
					String usageList = selectedInstanceSegment.getUsageList() + "-" + segment.getFields().get(i).getUsage().name();

					Field field = segment.getFields().get(i);
					
					Datatype fieldDT = m.getDatatypes().findOne(field.getDatatype());
					Table fieldTable = m.getTables().findOne(field.getTable());
					
					Predicate fieldPredicate = this.findPreficate(segment.getPredicates(), (i+1) + "[1]");
					List<ConformanceStatement> fieldConformanceStatements = this.findConformanceStatements(segment.getConformanceStatements(), (i+1) + "[1]");
					
					
					String fieldUsage = field.getUsage().name();
					
					if(fieldPredicate != null && fieldUsage.equals("C")){
						fieldUsage = fieldUsage + "(" + fieldPredicate.getTrueUsage().name() + "/" + fieldPredicate.getFalseUsage().name() +")";
					}
					
					if (segment.getName().equals("OBX") && field.getPosition() == 2) {
						obx5DTStr = fieldStr[j];
					}

					if (segment.getName().equals("OBX") && field.getPosition() == 5) {
						// TODO OBX Dynamic mapping needed
						Datatype dynamicFieldDT = m.getDatatypes().findOneDatatype(obx5DTStr+"_IZ");
						if(dynamicFieldDT == null){
							dynamicFieldDT = m.getDatatypes().findOneDatatypeByBase(obx5DTStr);
						}
						if(dynamicFieldDT != null){
							fieldDT = dynamicFieldDT;
						}
					}
					

					if (fieldDT.getComponents().size() > 0) {
						FieldModel fieldModel = new FieldModel( selectedInstanceSegment.getMessageName(), path, iPath, iPositionPath, usageList, field, fieldStr[j], m.findTCAMTConstraintByIPath(iPath), false, fieldDT, fieldTable, fieldPredicate, fieldConformanceStatements, fieldUsage);
						TreeNode fieldTreeNode = new DefaultTreeNode(fieldModel, segmentTreeRoot);

						String[] componentStr = fieldStr[j].split("\\^");

						for (int k = 0; k < fieldDT.getComponents().size(); k++) {
							String componentPath = path + "." + (k + 1);
							String componentIPath = iPath + "." + (k + 1) + "[1]";
							String componentIPositionPath = iPositionPath + "." + (k + 1) + "[1]";
							String componentUsageList = usageList + "-" + fieldDT.getComponents().get(k).getUsage().name();
							String componentUsage = fieldDT.getComponents().get(k).getUsage().name();

							Component component = fieldDT.getComponents().get(k);
							Datatype componentDT = m.getDatatypes().findOne(component.getDatatype());
							Table componentTable = m.getTables().findOne(component.getTable());
							Predicate componentPredicate = this.findPreficate(fieldDT.getPredicates(), (k+1) + "[1]");
							List<ConformanceStatement> componentConformanceStatements = this.findConformanceStatements(fieldDT.getConformanceStatements(), (k+1) + "[1]");
							if(componentPredicate != null && componentUsage.equals("C")){
								componentUsage = componentUsage + "(" + componentPredicate.getTrueUsage().name() + "/" + componentPredicate.getFalseUsage().name() +")";
							}

							TreeNode componentTreeNode;
							String[] subComponentStr;
							if (k >= componentStr.length) {
								if (componentDT.getComponents().size() > 0) {
									ComponentModel componentModel = new ComponentModel(
											selectedInstanceSegment.getMessageName(),
											componentPath,
											componentIPath,
											componentIPositionPath,
											componentUsageList,
											component,
											"",
											m.findTCAMTConstraintByIPath(componentIPath),
											false, componentDT, componentTable, componentPredicate, componentConformanceStatements, componentUsage);
									componentTreeNode = new DefaultTreeNode(componentModel, fieldTreeNode);
									subComponentStr = new String[] { "" };
								} else {
									ComponentModel componentModel = new ComponentModel(
											selectedInstanceSegment.getMessageName(),
											componentPath,
											componentIPath,
											componentIPositionPath,
											componentUsageList,
											component,
											"",
											m.findTCAMTConstraintByIPath(componentIPath),
											true, componentDT, componentTable, componentPredicate, componentConformanceStatements, componentUsage);
									componentTreeNode = new DefaultTreeNode(
											componentModel, fieldTreeNode);
									subComponentStr = new String[] { "" };
								}

							} else {
								if (componentDT.getComponents().size() > 0) {
									ComponentModel componentModel = new ComponentModel(
											selectedInstanceSegment
													.getMessageName(),
											componentPath,
											componentIPath,
											componentIPositionPath,
											componentUsageList,
											component,
											componentStr[k],
											m.findTCAMTConstraintByIPath(componentIPath),
											false, componentDT, componentTable, componentPredicate, componentConformanceStatements, componentUsage);
									componentTreeNode = new DefaultTreeNode(
											componentModel, fieldTreeNode);
									subComponentStr = componentStr[k]
											.split("\\&");
								} else {
									ComponentModel componentModel = new ComponentModel(
											selectedInstanceSegment
													.getMessageName(),
											componentPath,
											componentIPath,
											componentIPositionPath,
											componentUsageList,
											component,
											componentStr[k],
											m.findTCAMTConstraintByIPath(componentIPath),
											true, componentDT, componentTable, componentPredicate, componentConformanceStatements, componentUsage);
									componentTreeNode = new DefaultTreeNode(
											componentModel, fieldTreeNode);
									subComponentStr = componentStr[k]
											.split("\\&");
								}

							}

							for (int l = 0; l < componentDT.getComponents().size(); l++) {
								String subComponentPath = componentPath + "." + (l + 1);
								String subComponentIPath = componentIPath + "." + (l + 1) + "[1]";
								String subComponentIPositionPath = componentIPositionPath + "." + (l + 1) + "[1]";

								Component subComponent = componentDT.getComponents().get(l);
								
								String subComponentUsageList = componentUsageList + "-" + subComponent.getUsage().name();
								String subComponentUsage = subComponent.getUsage().name();
										
								Datatype subComponentDT = m.getDatatypes().findOne(subComponent.getDatatype());
								Table subComponentTable = m.getTables().findOne(subComponent.getTable());
								Predicate subComponentPredicate = this.findPreficate(componentDT.getPredicates(), (l+1) + "[1]");
								List<ConformanceStatement> subComponentConformanceStatements = this.findConformanceStatements(componentDT.getConformanceStatements(), (l+1) + "[1]");
								
								if(subComponentPredicate != null && subComponentUsage.equals("C")){
									subComponentUsage = subComponentUsage + "(" + subComponentPredicate.getTrueUsage().name() + "/" + subComponentPredicate.getFalseUsage().name() +")";
								}
								
								
								if (l >= subComponentStr.length) {
									ComponentModel subComponentModel = new ComponentModel(
											selectedInstanceSegment
													.getMessageName(),
											subComponentPath,
											subComponentIPath,
											subComponentIPositionPath,
											subComponentUsageList,
											subComponent,
											"",
											m.findTCAMTConstraintByIPath(subComponentIPath),
											true, subComponentDT,
											subComponentTable, subComponentPredicate, subComponentConformanceStatements, subComponentUsage);
									new DefaultTreeNode(subComponentModel,
											componentTreeNode);
								} else {
									ComponentModel subComponentModel = new ComponentModel(
											selectedInstanceSegment
													.getMessageName(),
											subComponentPath,
											subComponentIPath,
											subComponentIPositionPath,
											subComponentUsageList,
											subComponent,
											subComponentStr[l],
											m.findTCAMTConstraintByIPath(subComponentIPath),
											true, subComponentDT,
											subComponentTable, subComponentPredicate, subComponentConformanceStatements, subComponentUsage);
									new DefaultTreeNode(subComponentModel,
											componentTreeNode);
								}
							}
						}
					} else {
						if (path.equals("MSH.1")) {
							FieldModel fieldModel = new FieldModel(
									selectedInstanceSegment.getMessageName(),
									path, iPath, iPositionPath, usageList,
									segment.getFields().get(i), "|",
									m.findTCAMTConstraintByIPath(iPath), true,
									fieldDT, fieldTable, fieldPredicate, fieldConformanceStatements, fieldUsage);
							new DefaultTreeNode(fieldModel, segmentTreeRoot);
						} else if (path.equals("MSH.2")) {
							FieldModel fieldModel = new FieldModel(
									selectedInstanceSegment.getMessageName(),
									path, iPath, iPositionPath, usageList,
									segment.getFields().get(i), "^" + "~"
											+ "\\" + "&",
									m.findTCAMTConstraintByIPath(iPath), true,
									fieldDT, fieldTable, fieldPredicate, fieldConformanceStatements, fieldUsage);
							new DefaultTreeNode(fieldModel, segmentTreeRoot);
						} else {
							FieldModel fieldModel = new FieldModel(
									selectedInstanceSegment.getMessageName(),
									path, iPath, iPositionPath, usageList,
									segment.getFields().get(i), fieldStr[j],
									m.findTCAMTConstraintByIPath(iPath), true,
									fieldDT, fieldTable, fieldPredicate, fieldConformanceStatements, fieldUsage);
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
		for (TreeNode fieldTN : segmentTreeRoot.getChildren()) {
			FieldModel fieldModel = (FieldModel) fieldTN.getData();
			if (fieldModel.getPath().equals(previousPath)) {
				lineStr = lineStr + "~";
			} else {
				lineStr = lineStr + "|";
			}
			if (fieldModel.isLeafNode()) {
				lineStr = lineStr + fieldModel.getData();
			} else {
				for (int i = 0; i < fieldTN.getChildren().size(); i++) {
					TreeNode componentTN = fieldTN.getChildren().get(i);
					if (i == 0) {

					} else {
						lineStr = lineStr + "^";
					}

					ComponentModel componentModel = (ComponentModel) componentTN
							.getData();
					if (componentModel.isLeafNode()) {
						lineStr = lineStr + componentModel.getData();
					} else {
						String subComponentStr = "";
						for (int j = 0; j < componentTN.getChildren().size(); j++) {
							TreeNode subComponentTN = componentTN.getChildren()
									.get(j);
							if (j == 0) {

							} else {
								subComponentStr = subComponentStr + "&";
							}

							ComponentModel subComponentModel = (ComponentModel) subComponentTN
									.getData();
							subComponentStr = subComponentStr
									+ subComponentModel.getData();
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
		String[] lines = m.getHl7EndcodedMessage().split(
				System.getProperty("line.separator"));
		String hl7Message = "";
		for (int i = 0; i < lines.length; i++) {
			if (i == lineNum) {
				if (i == 0) {
					hl7Message = "MSH|" + "^" + "~" + "\\" + "&"
							+ generateLineStr.substring(7)
							+ System.getProperty("line.separator");
				} else {
					hl7Message = hl7Message + lines[i].substring(0, 3)
							+ generateLineStr
							+ System.getProperty("line.separator");
				}
			} else {
				hl7Message = hl7Message + lines[i]
						+ System.getProperty("line.separator");
			}
		}

		m.setHl7EndcodedMessage(hl7Message);

	}

	public void addRepeatedField(FieldModel fieldModel,
			TreeNode segmentTreeRoot, Message m) {
		String path = fieldModel.getPath();
		int count = 0;
		int position = 0;
		for (int i = 0; i < segmentTreeRoot.getChildren().size(); i++) {
			FieldModel fm = (FieldModel) segmentTreeRoot.getChildren().get(i).getData();
			if (fm.getPath().equals(path)) {
				count = count + 1;
				position = i;
			}
		}

		String iPath = fieldModel.getIpath().substring(0,fieldModel.getIpath().length() - 3) + "[" + (count + 1) + "]";
		String iPositionPath = fieldModel.getiPositionPath().substring(0, fieldModel.getiPositionPath().length() - 3) + "[" + (count + 1) + "]";

		FieldModel repeatedFieldModel = new FieldModel(
				fieldModel.getMessageName(), path, iPath, iPositionPath,
				fieldModel.getUsageList(), fieldModel.getNode(), "", null,
				fieldModel.isLeafNode(), fieldModel.getDatatype(),
				fieldModel.getTable(), fieldModel.getPredicate(), fieldModel.getConformanceStatements(), fieldModel.getUsage());
		TreeNode addedField = new DefaultTreeNode(repeatedFieldModel, segmentTreeRoot);

		if (!fieldModel.isLeafNode()) {
			for (int i = 0; i < fieldModel.getDatatype().getComponents().size(); i++) {
				Component c = fieldModel.getDatatype().getComponents().get(i);
				Datatype cDT = m.getDatatypes().findOne(c.getDatatype());
				Table cTable = m.getTables().findOne(c.getTable());
				Predicate cPredicate = this.findPreficate(fieldModel.getDatatype().getPredicates(), (i+1) + "[1]");
				List<ConformanceStatement> cConformanceStatements = this.findConformanceStatements(fieldModel.getDatatype().getConformanceStatements(), (i+1) + "[1]");
				String componentPath = path + "." + (i + 1);
				String componentIPath = iPath + "." + (i + 1) + "[1]";
				String componentIPositionPath = iPositionPath + "." + (i + 1) + "[1]";
				String componentUsageList = fieldModel.getUsageList() + "-" + c.getUsage().name();
				String componentUsage = c.getUsage().name();
				
				if(cPredicate != null && componentUsage.equals("C")){
					componentUsage = componentUsage + "(" + cPredicate.getTrueUsage().name() + "/" + cPredicate.getFalseUsage().name() +")";
				}

				if (cDT.getComponents().size() > 0) {
					ComponentModel repatedComponentModel = new ComponentModel(
							fieldModel.getMessageName(), componentPath,
							componentIPath, componentIPositionPath,
							componentUsageList, c, "", null, false, cDT, cTable, cPredicate, cConformanceStatements, componentUsage);
					TreeNode addedComponent = new DefaultTreeNode(repatedComponentModel, addedField);

					for (int j = 0; j < cDT.getComponents().size(); j++) {
						Component sc = cDT.getComponents().get(j);
						Datatype scDT = m.getDatatypes().findOne(sc.getDatatype());
						Table scTable = m.getTables().findOne(sc.getTable());
						Predicate scPredicate = this.findPreficate(cDT.getPredicates(), (j+1) + "[1]");
						List<ConformanceStatement> scConformanceStatements = this.findConformanceStatements(cDT.getConformanceStatements(), (j+1) + "[1]");
						String subComponentPath = componentPath + "." + (j + 1);
						String subComponentIPath = componentIPath + "." + (j + 1) + "[1]";
						String subComponentIPositionPath = componentIPositionPath + "." + (j + 1) + "[1]";
						String subComponentUsageList = componentUsageList + "-" + sc.getUsage().name();
						String subComponentUsage = sc.getUsage().name();
						
						if(scPredicate != null && subComponentUsage.equals("C")){
							subComponentUsage = subComponentUsage + "(" + scPredicate.getTrueUsage().name() + "/" + scPredicate.getFalseUsage().name() +")";
						}

						ComponentModel repatedSubComponentModel = new ComponentModel(
								fieldModel.getMessageName(), subComponentPath,
								subComponentIPath, subComponentIPositionPath,
								subComponentUsageList, sc, "", null, true,
								scDT, scTable, scPredicate, scConformanceStatements, subComponentUsage);
						new DefaultTreeNode(repatedSubComponentModel, addedComponent);
					}
				} else {
					ComponentModel repatedComponentModel = new ComponentModel(
							fieldModel.getMessageName(), componentPath,
							componentIPath, componentIPositionPath,
							componentUsageList, c, "", null, true, cDT, cTable, cPredicate, cConformanceStatements, componentUsage);
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

		for (String p : pathData) {
			Integer position = null;

			for (int i = 0; i < currentNode.getChildren().size(); i++) {
				MessageTreeModel model = (MessageTreeModel) currentNode
						.getChildren().get(i).getData();

				String[] currentPathData = model.getPath().split("\\.");

				if (p.equals(currentPathData[currentPathData.length - 1])) {
					position = i + 1;
					i = currentNode.getChildren().size();
				}
			}
			result.add(position);
			currentNode = currentNode.getChildren().get(position - 1);
		}

		return result;
	}

	public void populateTreeNode(TreeNode parent, Message m) {
		MessageTreeModel model = (MessageTreeModel) parent.getData();
		Object n = model.getNode();

		if (n instanceof SegmentRef) {
			return;
		} else {
			Group g = (Group) n;

			for (SegmentRefOrGroup child : g.getChildren()) {
				if (child instanceof SegmentRef) {
					SegmentRef segmentRef = (SegmentRef) child;
					String segmentName = m.getSegments()
							.findOne(segmentRef.getRef()).getName();
					String path = model.getPath()
							+ "."
							+ m.getSegments().findOne(segmentRef.getRef())
									.getName();

					MessageTreeModel newModel = new MessageTreeModel(
							model.getMessageId(), segmentName, segmentRef,
							path, 0);

					new DefaultTreeNode(segmentRef.getMax(), newModel, parent);
				} else {
					Group group = (Group) child;
					String path = group.getName();
					String messageId = path.split("\\.")[0];
					String messagePath = path.replace(messageId + ".", "");
					MessageTreeModel newModel = new MessageTreeModel(
							model.getMessageId(), group.getName(), group,
							messagePath, 0);
					TreeNode childNode = new DefaultTreeNode(group.getMax(),
							newModel, parent);
					this.populateTreeNode(childNode, m);
				}
			}
		}

	}

	public String generateHL7Message(TreeNode messageTreeRoot, Message m) {
		String message = "MSH|" + "^" + "~" + "\\" + "&" + "|"
				+ System.getProperty("line.separator");
		for (TreeNode child : messageTreeRoot.getChildren()) {
			message = this.travel(m, child, message);
		}

		return message;
	}

	private String travel(Message m, TreeNode node, String message) {
		MessageTreeModel model = (MessageTreeModel) node.getData();

		if (model.getNode() instanceof SegmentRef) {
			SegmentRef segmentRef = (SegmentRef) model.getNode();
			String segmentName = m.getSegments().findOne(segmentRef.getRef())
					.getName();
			if (!segmentName.equals("MSH")) {
				if (!segmentRef.getMax().equals("0"))
					message = message + segmentName + "|"
							+ System.getProperty("line.separator");
			}
			return message;
		} else {
			for (TreeNode child : node.getChildren()) {
				message = this.travel(m, child, message);
			}
		}

		return message;
	}

	public String generateConstraintDocument(Message m) throws Exception {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder;
		docBuilder = docFactory.newDocumentBuilder();

		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("ConformanceContext");

		rootElement.setAttribute("UUID", UUID.randomUUID().toString());
		doc.appendChild(rootElement);

		Element constraintsElement = doc.createElement("Constraints");
		Element groupElement = doc.createElement("Group");
		constraintsElement.appendChild(groupElement);

		Element elmByName = doc.createElement("ByName");
		String messageName = null;
		for (TCAMTConstraint c : m.getTcamtConstraints()) {
			messageName = c.getMessageName();
			String usageList = c.getUsageList();
			String iPositionPath = c.getiPosition();
			String iPath = c.getIpath();
			String tdc = c.getCategorization().getValue();

			if (c.getCategorization()
					.equals(TestDataCategorization.Indifferent)) {
			} else if (c.getCategorization().equals(
					TestDataCategorization.ContentIndifferent)) {
				this.createPresenceCheck(usageList, iPositionPath, iPath, tdc, elmByName);
			} else if (c.getCategorization().equals(
					TestDataCategorization.Configurable)) {
				this.createPresenceCheck(usageList, iPositionPath, iPath, tdc, elmByName);
			} else if (c.getCategorization().equals(
					TestDataCategorization.SystemGenerated)) {
				this.createPresenceCheck(usageList, iPositionPath, iPath, tdc,
						elmByName);
			} else if (c.getCategorization().equals(
					TestDataCategorization.TestCaseProper)) {
				this.createPresenceCheck(usageList, iPositionPath, iPath, tdc,
						elmByName);
			} else if (c.getCategorization().equals(
					TestDataCategorization.NotValued)) {
			} else if (c.getCategorization().equals(
					TestDataCategorization.TestCaseFixed)) {
				this.createPresenceCheck(usageList, iPositionPath, iPath, tdc,
						elmByName);
				this.createPlainTextCheck(c.getData(), iPositionPath, iPath,
						tdc, elmByName);
			} else if (c.getCategorization().equals(
					TestDataCategorization.TestCaseFixedList)) {
				this.createPresenceCheck(usageList, iPositionPath, iPath, tdc,
						elmByName);
				this.createStringListCheck(c.getData(), iPositionPath, iPath,
						tdc, elmByName);
			}
		}
		if (messageName != null) {
			elmByName.setAttribute("Name", messageName);
			rootElement.appendChild(constraintsElement);
		}
		groupElement.appendChild(elmByName);

		return XMLManager.docToString(doc);
	}

	private void createStringListCheck(String values, String iPositionPath, String iPath, String tdc, Element parent) {
		Element elmConstraint = parent.getOwnerDocument().createElement("Constraint");

		Element elmReference = parent.getOwnerDocument().createElement("Reference");
		elmReference.setAttribute("Source", "Test Case");
		elmReference.setAttribute("GeneratedBy", "TCAMT");
		elmReference.setAttribute("ReferencePath", iPath);
		elmReference.setAttribute("TestDataCategorization", tdc);
		elmConstraint.appendChild(elmReference);

		elmConstraint.setAttribute("ID", "TCAMT_" + iPath);
		elmConstraint.setAttribute("Target", iPositionPath);
		Element elmDescription = parent.getOwnerDocument().createElement("Description");
		elmDescription.appendChild(parent.getOwnerDocument().createTextNode( iPath + " SHALL be one of list values: " + values + "."));
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

	private void createPlainTextCheck(String value, String iPositionPath, String iPath, String tdc, Element parent) {
		Element elmConstraint = parent.getOwnerDocument().createElement("Constraint");
		Element elmReference = parent.getOwnerDocument().createElement("Reference");
		elmReference.setAttribute("Source", "Test Case");
		elmReference.setAttribute("GeneratedBy", "TCAMT");
		elmReference.setAttribute("ReferencePath", iPath);
		elmReference.setAttribute("TestDataCategorization", tdc);
		elmConstraint.appendChild(elmReference);

		elmConstraint.setAttribute("ID", "TCAMT_" + iPath);
		elmConstraint.setAttribute("Target", iPositionPath);
		Element elmDescription = parent.getOwnerDocument().createElement("Description");
		elmDescription.appendChild(parent.getOwnerDocument().createTextNode(iPath + " SHALL be valued '" + value + "."));
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

	private void createPresenceCheck(String usageList, String iPositionPath, String iPath, String tdc, Element parent) {
		String[] uList = usageList.split("-");
		String myIPositionPath = "";
		String myIPath = "";
		for (int i = 0; i < uList.length; i++) {
			if (i == 0) {
				myIPositionPath = iPositionPath.split("\\.")[i];
				myIPath = iPath.split("\\.")[i];
			} else {
				myIPositionPath = myIPositionPath + "." + iPositionPath.split("\\.")[i];
				myIPath = myIPath + "." + iPath.split("\\.")[i];
			}
			if (!uList[i].equals("R")) {
				Element elmConstraint = parent.getOwnerDocument().createElement("Constraint");
				Element elmReference = parent.getOwnerDocument().createElement("Reference");
				elmReference.setAttribute("Source", "Test Case");
				elmReference.setAttribute("GeneratedBy", "TCAMT");
				elmReference.setAttribute("ReferencePath", iPath);
				elmReference.setAttribute("TestDataCategorization", tdc);
				elmConstraint.appendChild(elmReference);

				elmConstraint.setAttribute("ID", "TCAMT_" + iPath + "_" + (i + 1));
				elmConstraint.setAttribute("Target", myIPositionPath);
				Element elmDescription = parent.getOwnerDocument().createElement("Description");
				elmDescription.appendChild(parent.getOwnerDocument().createTextNode(myIPath + " SHALL be valued."));
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

	private String getFieldStrFromSegment(String segmentName,
			InstanceSegment is, int position) {
		// &lt; (<), &amp; (&), &gt; (>), &quot; ("), and &apos; (').
		String segmentStr = is.getLineStr().replace("<", "&lt;")
				.replace(">", "&gt;").replace("\"", "&quot;")
				.replace("'", "&apos;");
		if (segmentName.equals("MSH")) {
			segmentStr = "MSH|FieldSeperator|Encoding|"
					+ segmentStr.substring(9);
		}
		String[] wholeFieldStr = segmentStr.split("\\|");

		if (position > wholeFieldStr.length - 1)
			return "";
		else
			return wholeFieldStr[position];
	}

	private String getComponentStrFromField(String fieldStr, int position) {
		String[] componentStr = fieldStr.split("\\^");

		if (position > componentStr.length)
			return "";
		else
			return componentStr[position - 1];

	}

	private String getSubComponentStrFromField(String componentStr, int position) {
		String[] subComponentStr = componentStr.split("\\&");

		if (position > subComponentStr.length)
			return "";
		else
			return subComponentStr[position - 1];

	}

	


	private void generateXMLFromMesageInstance(Message m, List<InstanceSegment> instanceSegments, boolean isSTD, String testCaseName) throws Exception {
		String messageName = m.getMessageObj().getStructID();
		org.w3c.dom.Document xmlHL7MessageInstanceDom = XMLManager.stringToDom("<" + messageName + "/>");
		Element rootElm = (Element) xmlHL7MessageInstanceDom.getElementsByTagName(messageName).item(0);
		if(testCaseName != null) rootElm.setAttribute("testcaseName", testCaseName);

		for (InstanceSegment instanceSegment : instanceSegments) {
			String[] iPathList = instanceSegment.getIpath().split("\\.");
			if (iPathList.length == 1) {
				Element segmentElm = xmlHL7MessageInstanceDom
						.createElement(iPathList[0].substring(0,
								iPathList[0].lastIndexOf("[")));
				if (isSTD)
					this.generateSegment(m, segmentElm, instanceSegment);
				else
					this.generateNISTSegment(m, segmentElm, instanceSegment);
				rootElm.appendChild(segmentElm);
			} else {
				Element parentElm = rootElm;

				for (int i = 0; i < iPathList.length; i++) {
					String iPath = iPathList[i];
					if (i == iPathList.length - 1) {
						Element segmentElm = xmlHL7MessageInstanceDom
								.createElement(iPath.substring(0,
										iPath.lastIndexOf("[")));
						if (isSTD)
							this.generateSegment(m, segmentElm, instanceSegment);
						else
							this.generateNISTSegment(m, segmentElm,
									instanceSegment);
						parentElm.appendChild(segmentElm);
					} else {
						String groupName = iPath.substring(0,
								iPath.lastIndexOf("["));
						int groupIndex = Integer.parseInt(iPath.substring(
								iPath.lastIndexOf("[") + 1,
								iPath.lastIndexOf("]")));

						NodeList groups = parentElm
								.getElementsByTagName(messageName + "."
										+ groupName);
						if (groups == null || groups.getLength() < groupIndex) {
							Element group = xmlHL7MessageInstanceDom
									.createElement(messageName + "."
											+ groupName);
							parentElm.appendChild(group);
							parentElm = group;

						} else {
							parentElm = (Element) groups.item(groupIndex - 1);
						}
					}
				}
			}

		}

		if (isSTD)
			m.setXmlEncodedSTDMessage(XMLManager
					.docToString(xmlHL7MessageInstanceDom));
		else
			m.setXmlEncodedNISTMessage(XMLManager
					.docToString(xmlHL7MessageInstanceDom));
	}

	private void generateNISTSegment(Message m, Element segmentElm,
			InstanceSegment instanceSegment) {
		String lineStr = instanceSegment.getLineStr();
		String segmentName = lineStr.substring(0, 3);
		Segment segment = m.getSegments().findOne(
				instanceSegment.getSegmentRef().getRef());

		if (lineStr.startsWith("MSH")) {
			lineStr = "MSH|%SEGMENTDVIDER%|%ENCODINGDVIDER%"
					+ lineStr.substring(8);
		}

		String[] fieldStrs = lineStr.substring(4).split("\\|");

		for (int i = 0; i < fieldStrs.length; i++) {
			String[] fieldStrRepeats = fieldStrs[i].split("\\~");
			for (String fieldStr : fieldStrRepeats) {
				if (fieldStr.equals("%SEGMENTDVIDER%")) {
					Element fieldElm = segmentElm.getOwnerDocument()
							.createElement("MSH.1");
					Text value = segmentElm.getOwnerDocument().createTextNode(
							"|");
					fieldElm.appendChild(value);
					segmentElm.appendChild(fieldElm);
				} else if (fieldStr.equals("%ENCODINGDVIDER%")) {
					Element fieldElm = segmentElm.getOwnerDocument()
							.createElement("MSH.2");
					Text value = segmentElm.getOwnerDocument().createTextNode(
							"^~\\&");
					fieldElm.appendChild(value);
					segmentElm.appendChild(fieldElm);
				} else {
					if (fieldStr != null && !fieldStr.equals("")) {
						if (i < segment.getFields().size()) {
							Field field = segment.getFields().get(i);
							Element fieldElm = segmentElm.getOwnerDocument()
									.createElement(
											segmentName + "."
													+ field.getPosition());
							if (m.getDatatypes().findOne(field.getDatatype())
									.getComponents() == null
									|| m.getDatatypes()
											.findOne(field.getDatatype())
											.getComponents().size() == 0) {
								if (lineStr.startsWith("OBX")) {
									if (field.getPosition().equals(2)) {
										Text value = segmentElm
												.getOwnerDocument()
												.createTextNode(fieldStr);
										fieldElm.appendChild(value);
									} else if (field.getPosition().equals(5)) {
										String[] componentStrs = fieldStr
												.split("\\^");

										for (int index = 0; index < componentStrs.length; index++) {
											String componentStr = componentStrs[index];
											Element componentElm = segmentElm
													.getOwnerDocument()
													.createElement(
															segmentName
																	+ "."
																	+ field.getPosition()
																	+ "."
																	+ (index + 1));
											Text value = segmentElm
													.getOwnerDocument()
													.createTextNode(
															componentStr);
											componentElm.appendChild(value);
											fieldElm.appendChild(componentElm);

										}
									}
								} else {
									Text value = segmentElm.getOwnerDocument()
											.createTextNode(fieldStr);
									fieldElm.appendChild(value);
								}
							} else {
								String[] componentStrs = fieldStr.split("\\^");
								for (int j = 0; j < componentStrs.length; j++) {
									if (j < m.getDatatypes()
											.findOne(field.getDatatype())
											.getComponents().size()) {
										Component component = m.getDatatypes()
												.findOne(field.getDatatype())
												.getComponents().get(j);
										String componentStr = componentStrs[j];
										if (componentStr != null
												&& !componentStr.equals("")) {
											Element componentElm = segmentElm
													.getOwnerDocument()
													.createElement(
															segmentName + "."
																	+ (i + 1)
																	+ "."
																	+ (j + 1));
											if (m.getDatatypes()
													.findOne(
															component
																	.getDatatype())
													.getComponents() == null
													|| m.getDatatypes()
															.findOne(
																	component
																			.getDatatype())
															.getComponents()
															.size() == 0) {
												Text value = segmentElm
														.getOwnerDocument()
														.createTextNode(
																componentStr);
												componentElm.appendChild(value);
											} else {
												String[] subComponentStrs = componentStr
														.split("\\&");
												for (int k = 0; k < subComponentStrs.length; k++) {
													String subComponentStr = subComponentStrs[k];
													if (subComponentStr != null
															&& !subComponentStr
																	.equals("")) {
														Element subComponentElm = segmentElm
																.getOwnerDocument()
																.createElement(
																		segmentName
																				+ "."
																				+ (i + 1)
																				+ "."
																				+ (j + 1)
																				+ "."
																				+ (k + 1));
														Text value = segmentElm
																.getOwnerDocument()
																.createTextNode(
																		subComponentStr);
														subComponentElm
																.appendChild(value);
														componentElm
																.appendChild(subComponentElm);
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

	private void generateSegment(Message m, Element segmentElm,
			InstanceSegment instanceSegment) {
		String lineStr = instanceSegment.getLineStr();
		String segmentName = lineStr.substring(0, 3);
		Segment segment = m.getSegments().findOne(
				instanceSegment.getSegmentRef().getRef());
		String variesDT = "";

		if (lineStr.startsWith("MSH")) {
			lineStr = "MSH|%SEGMENTDVIDER%|%ENCODINGDVIDER%"
					+ lineStr.substring(8);
		}

		String[] fieldStrs = lineStr.substring(4).split("\\|");

		for (int i = 0; i < fieldStrs.length; i++) {
			String[] fieldStrRepeats = fieldStrs[i].split("\\~");
			for (String fieldStr : fieldStrRepeats) {
				if (fieldStr.equals("%SEGMENTDVIDER%")) {
					Element fieldElm = segmentElm.getOwnerDocument()
							.createElement("MSH.1");
					Text value = segmentElm.getOwnerDocument().createTextNode(
							"|");
					fieldElm.appendChild(value);
					segmentElm.appendChild(fieldElm);
				} else if (fieldStr.equals("%ENCODINGDVIDER%")) {
					Element fieldElm = segmentElm.getOwnerDocument()
							.createElement("MSH.2");
					Text value = segmentElm.getOwnerDocument().createTextNode(
							"^~\\&");
					fieldElm.appendChild(value);
					segmentElm.appendChild(fieldElm);
				} else {
					if (fieldStr != null && !fieldStr.equals("")) {
						if (i < segment.getFields().size()) {
							Field field = segment.getFields().get(i);
							Element fieldElm = segmentElm.getOwnerDocument()
									.createElement(
											segmentName + "."
													+ field.getPosition());
							if (m.getDatatypes().findOne(field.getDatatype())
									.getComponents() == null
									|| m.getDatatypes()
											.findOne(field.getDatatype())
											.getComponents().size() == 0) {
								if (lineStr.startsWith("OBX")) {
									if (field.getPosition().equals(2)) {
										variesDT = fieldStr;
										Text value = segmentElm
												.getOwnerDocument()
												.createTextNode(fieldStr);
										fieldElm.appendChild(value);
									} else if (field.getPosition().equals(5)) {
										String[] componentStrs = fieldStr
												.split("\\^");

										for (int index = 0; index < componentStrs.length; index++) {
											String componentStr = componentStrs[index];
											Element componentElm = segmentElm
													.getOwnerDocument()
													.createElement(
															variesDT
																	+ "."
																	+ (index + 1));
											Text value = segmentElm
													.getOwnerDocument()
													.createTextNode(
															componentStr);
											componentElm.appendChild(value);
											fieldElm.appendChild(componentElm);

										}
									}
								} else {
									Text value = segmentElm.getOwnerDocument()
											.createTextNode(fieldStr);
									fieldElm.appendChild(value);
								}
							} else {
								String[] componentStrs = fieldStr.split("\\^");
								String componentDataTypeName = m.getDatatypes()
										.findOne(field.getDatatype()).getName();
								for (int j = 0; j < componentStrs.length; j++) {
									if (j < m.getDatatypes()
											.findOne(field.getDatatype())
											.getComponents().size()) {
										Component component = m.getDatatypes()
												.findOne(field.getDatatype())
												.getComponents().get(j);
										String componentStr = componentStrs[j];
										if (componentStr != null
												&& !componentStr.equals("")) {
											Element componentElm = segmentElm
													.getOwnerDocument()
													.createElement(
															componentDataTypeName
																	+ "."
																	+ (j + 1));
											if (m.getDatatypes()
													.findOne(
															component
																	.getDatatype())
													.getComponents() == null
													|| m.getDatatypes()
															.findOne(
																	component
																			.getDatatype())
															.getComponents()
															.size() == 0) {
												Text value = segmentElm
														.getOwnerDocument()
														.createTextNode(
																componentStr);
												componentElm.appendChild(value);
											} else {
												String[] subComponentStrs = componentStr
														.split("\\&");
												String subComponentDataTypeName = m
														.getDatatypes()
														.findOne(
																component
																		.getDatatype())
														.getName();

												for (int k = 0; k < subComponentStrs.length; k++) {
													String subComponentStr = subComponentStrs[k];
													if (subComponentStr != null
															&& !subComponentStr
																	.equals("")) {
														Element subComponentElm = segmentElm
																.getOwnerDocument()
																.createElement(
																		subComponentDataTypeName
																				+ "."
																				+ (k + 1));
														Text value = segmentElm
																.getOwnerDocument()
																.createTextNode(
																		subComponentStr);
														subComponentElm
																.appendChild(value);
														componentElm
																.appendChild(subComponentElm);
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

	private String findTestDataCategorization(Message m, String iPath) {
		for (TCAMTConstraint c : m.getTcamtConstraints()) {
			if (c.getIpath().equals(iPath))
				return c.getCategorization().getValue();
		}

		return "";
	}

	public TreeNode generateConstraintTree(Message m) {
		TreeNode root = new DefaultTreeNode("", null);
		for (TCAMTConstraint c : m.getTcamtConstraints()) {
			TreeNode cTreeNode = new DefaultTreeNode(c, root);
			cTreeNode.setExpanded(true);
			String usageList = c.getUsageList();
			String iPositionPath = c.getiPosition();
			String iPath = c.getIpath();
			if (c.getCategorization().equals(TestDataCategorization.Indifferent)) {
			} else if (c.getCategorization().equals(TestDataCategorization.ContentIndifferent)) {
				this.createPresenceTree(usageList, iPositionPath, iPath, cTreeNode);
			} else if (c.getCategorization().equals(TestDataCategorization.Configurable)) {
				this.createPresenceTree(usageList, iPositionPath, iPath, cTreeNode);
			} else if (c.getCategorization().equals(TestDataCategorization.SystemGenerated)) {
				this.createPresenceTree(usageList, iPositionPath, iPath, cTreeNode);
			} else if (c.getCategorization().equals( TestDataCategorization.TestCaseProper)) {
				this.createPresenceTree(usageList, iPositionPath, iPath, cTreeNode);
			} else if (c.getCategorization().equals(
					TestDataCategorization.NotValued)) {
			} else if (c.getCategorization().equals(
					TestDataCategorization.TestCaseFixed)) {
				this.createPresenceTree(usageList, iPositionPath, iPath,
						cTreeNode);
				this.createPlainTextTree(c.getData(), iPositionPath, iPath,
						cTreeNode);
			} else if (c.getCategorization().equals(
					TestDataCategorization.TestCaseFixedList)) {
				this.createPresenceTree(usageList, iPositionPath, iPath,
						cTreeNode);
				this.createStringListTree(c.getData(), iPositionPath, iPath,
						cTreeNode);
			}
		}

		return root;
	}

	private void createStringListTree(String data, String iPositionPath,
			String iPath, TreeNode parent) {
		Constraint c = new Constraint();
		c.setData(data);
		c.setDescription("The value of " + iPath
				+ " SHALL be one of List Values: " + data);
		c.setId("TCAMT");
		c.setIpath(iPath);
		c.setiPositionPath(iPositionPath);
		c.setType("StringList");
		new DefaultTreeNode(c, parent);

	}

	private void createPlainTextTree(String data, String iPositionPath,
			String iPath, TreeNode parent) {
		Constraint c = new Constraint();
		c.setData(data);
		c.setDescription("The value of " + iPath + " SHALL be '" + data + "'.");
		c.setId("TCAMT");
		c.setIpath(iPath);
		c.setiPositionPath(iPositionPath);
		c.setType("PlainText");
		new DefaultTreeNode(c, parent);

	}

	private void createPresenceTree(String usageList, String iPositionPath,
			String iPath, TreeNode parent) {
		String[] uList = usageList.split("-");
		String myIPositionPath = "";
		String myIPath = "";
		for (int i = 0; i < uList.length; i++) {
			if (i == 0) {
				myIPath = iPath.split("\\.")[i];
				myIPositionPath = iPositionPath.split("\\.")[i];
			} else {
				myIPath = myIPath + "." + iPath.split("\\.")[i];
				myIPositionPath = myIPositionPath + "."
						+ iPositionPath.split("\\.")[i];
			}
			if (!uList[i].equals("R")) {
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

	public TreeNode genRestrictedTree(TreeNode segmentTreeRoot) {
		TreeNode result = new DefaultTreeNode("root", null);

		for (TreeNode fieldTN : segmentTreeRoot.getChildren()) {
			FieldModel fm = (FieldModel) fieldTN.getData();

			String[] usageList = fm.getUsageList().split("-");
			boolean usageCheck = true;

			for (String u : usageList) {
				if (!u.equals("R") && !u.equals("RE") && !u.equals("C")) {
					usageCheck = false;
				}
			}

			if (usageCheck) {
				TreeNode fieldNode = new DefaultTreeNode(fm, result);

				for (TreeNode componentTN : fieldTN.getChildren()) {
					ComponentModel cm = (ComponentModel) componentTN.getData();
					if (cm.getNode().getUsage().value().equals("R")
							|| cm.getNode().getUsage().value().equals("RE")
							|| cm.getNode().getUsage().value().equals("C")) {
						TreeNode compomnentNode = new DefaultTreeNode(cm,
								fieldNode);
						for (TreeNode subComponentTN : componentTN
								.getChildren()) {
							ComponentModel scm = (ComponentModel) subComponentTN
									.getData();
							if (scm.getNode().getUsage().value().equals("R")
									|| scm.getNode().getUsage().value()
											.equals("RE")
									|| scm.getNode().getUsage().value()
											.equals("C")) {
								new DefaultTreeNode(scm, compomnentNode);
							}
						}

					}
				}
			}
		}

		return result;

	}
	
	private Predicate findPreficate(List<Predicate> predicates, String path){
		for(Predicate p:predicates){
			if(p.getConstraintTarget().equals(path)) return p;
		}
		return null;
	}
	
	private List<ConformanceStatement> findConformanceStatements(List<ConformanceStatement> conformanceStatements, String path) {
		List<ConformanceStatement> results = new ArrayList<ConformanceStatement>();
		for(ConformanceStatement c:conformanceStatements){
			if(c.getConstraintTarget().equals(path)) results.add(c);
		}
		if(results.size() == 0) return null;
		else return results;
	}
	
	
}
