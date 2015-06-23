package gov.nist.healthcare.tcamt.view;

import gov.nist.healthcare.hl7tools.v2.maker.core.ConversionException;
import gov.nist.healthcare.tcamt.domain.DataInstanceTestCase;
import gov.nist.healthcare.tcamt.domain.DataInstanceTestCaseGroup;
import gov.nist.healthcare.tcamt.domain.DataInstanceTestPlan;
import gov.nist.healthcare.tcamt.domain.DataInstanceTestStep;
import gov.nist.healthcare.tcamt.domain.Message;
import gov.nist.healthcare.tcamt.domain.TCAMTConstraint;
import gov.nist.healthcare.tcamt.domain.TestStory;
import gov.nist.healthcare.tcamt.domain.data.ComponentModel;
import gov.nist.healthcare.tcamt.domain.data.FieldModel;
import gov.nist.healthcare.tcamt.domain.data.InstanceSegment;
import gov.nist.healthcare.tcamt.domain.data.MessageTreeModel;
import gov.nist.healthcare.tcamt.domain.data.TestDataCategorization;
import gov.nist.healthcare.tcamt.service.ManageInstance;
import gov.nist.healthcare.tcamt.service.XMLManager;
import gov.nist.healthcare.tcamt.service.converter.DataInstanceTestCaseConverter;
import gov.nist.healthcare.tcamt.service.converter.DataInstanceTestPlanConverter;
import gov.nist.healthcare.tcamt.service.converter.JsonDataInstanceTestCaseConverter;
import gov.nist.healthcare.tcamt.service.converter.JsonDataInstanceTestPlanConverter;
import gov.nist.healthcare.tcamt.service.converter.JsonTestStoryConverter;
import gov.nist.healthcare.tcamt.service.converter.TestStoryConverter;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TreeDragDropEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.xml.sax.SAXException;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

@ManagedBean
@SessionScoped
public class DataInstanceTestPlanRequestBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6592759972860009656L;

	@ManagedProperty("#{sessionBeanTCAMT}")
	private SessionBeanTCAMT sessionBeanTCAMT;
	private DataInstanceTestPlan selectedTestPlan = new DataInstanceTestPlan();
	private DataInstanceTestCase selectedTestCase = null;
	private DataInstanceTestStep selectedTestStep = null;
	private DataInstanceTestCaseGroup selectedTestCaseGroup = null;
	private Long messageId = null;
	private Long conformanceProfileId = null;
	private Long shareTo = null;
	private int activeIndexOfMessageInstancePanel = 0;
	
	private TreeNode testplanRoot = new DefaultTreeNode("root", null);
    private TreeNode selectedNode = null;
    
    private TreeNode segmentTreeRoot = new DefaultTreeNode("root", null);
	private TreeNode messageTreeRoot = new DefaultTreeNode("root", null);
	private TreeNode constraintTreeRoot = new DefaultTreeNode("root", null);
    
    private InstanceSegment selectedInstanceSegment= null;
	private List<InstanceSegment> instanceSegments = new ArrayList<InstanceSegment>();
	
	
	private String usageViewOption = "partial";
	private String usageViewOption2 = "partial";
	private List<InstanceSegment> filteredInstanceSegments =  new ArrayList<InstanceSegment>();
	private TreeNode filtedSegmentTreeRoot = new DefaultTreeNode("root", null);
	
	
	private ManageInstance manageInstanceService = new ManageInstance();
	
	private transient StreamedContent zipResourceBundleFile;
	
	private TestStoryConverter testStoryConverter;
	private DataInstanceTestCaseConverter tcConverter;
	private DataInstanceTestPlanConverter tpConverter;
	
	
	private String testDataSpecificationHTML;
	private String jurorDocumentHTML;
	private String messageContentHTML;

	private void init(){
		this.selectedTestPlan = new DataInstanceTestPlan();
		this.selectedTestPlan.setVersion(1);
		this.selectedTestPlan.setAuthor(this.sessionBeanTCAMT.getLoggedUser());
		this.selectedTestCase = null;
		this.selectedTestStep = null;
		this.selectedTestCaseGroup = null;
		this.messageId = null;
		this.shareTo = null;
		this.selectedNode = null;
		
		this.selectedInstanceSegment = null;
		this.segmentTreeRoot = new DefaultTreeNode("root", null);
		this.messageTreeRoot = new DefaultTreeNode("root", null);
		this.setConstraintTreeRoot(new DefaultTreeNode("root", null));
		this.instanceSegments = new ArrayList<InstanceSegment>();
		this.manageInstanceService = new ManageInstance();
		this.sessionBeanTCAMT.setDitActiveIndex(0);
		this.setActiveIndexOfMessageInstancePanel(2);
		
		this.usageViewOption = "partial";
		this.usageViewOption2 = "partial";
		this.filteredInstanceSegments = new ArrayList<InstanceSegment>();
		this.filtedSegmentTreeRoot = new DefaultTreeNode("root", null);
		
		this.testDataSpecificationHTML = null;
		this.jurorDocumentHTML = null;
		this.setMessageContentHTML(null);
		
	}
	
	public void shareInit(ActionEvent event){
		this.shareTo = null;
		this.selectedTestPlan = (DataInstanceTestPlan) event.getComponent().getAttributes().get("testplan");
	}
	
	
	public void createTestPlan() {
		init();
		this.selectedTestPlan.setName("New TestPlan");
		this.createTestPlanTree(this.selectedTestPlan);
		this.setActiveIndexOfMessageInstancePanel(2);
		this.sessionBeanTCAMT.setDitActiveIndex(1);
	}
	
	public void deleteTestPlan(ActionEvent event) {
		String deletedTestPlanName = ((DataInstanceTestPlan) event.getComponent().getAttributes().get("testplan")).getName();
		this.sessionBeanTCAMT.getDbManager().dataInstanceTestPlanDelete((DataInstanceTestPlan) event.getComponent().getAttributes().get("testplan"));
		this.sessionBeanTCAMT.updateDataInstanceTestPlans();
		
		FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage("TestPlan Deleted.",  "TestPlan: " + deletedTestPlanName + " has been created.") );
	}
	
	
	public void cloneTestPlan(ActionEvent event) throws CloneNotSupportedException {
		DataInstanceTestPlan testplan = ((DataInstanceTestPlan) event.getComponent().getAttributes().get("testplan")).clone();
		testplan.setName("Copyed_" + testplan.getName());
		this.sessionBeanTCAMT.getDbManager().dataInstanceTestPlanInsert(testplan);
		this.sessionBeanTCAMT.updateDataInstanceTestPlans();
		
		this.selectedTestPlan = testplan;
		this.selectedTestCase = null;
		this.selectedTestCaseGroup = null;
		this.selectedTestStep = null;
		this.createTestPlanTree(this.selectedTestPlan);
		this.sessionBeanTCAMT.setDitActiveIndex(1);
		
		FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage("Message Cloned.",  "TestPlan: " + this.selectedTestPlan.getName() + " has been created.") );
	}
	
	public void shareTestPlan() throws CloneNotSupportedException{
		this.selectedTestPlan.setName("Copyed_" + selectedTestPlan.getName());
		this.selectedTestPlan.setAuthor(this.sessionBeanTCAMT.getDbManager().getUserById(this.shareTo));
		this.sessionBeanTCAMT.getDbManager().dataInstanceTestPlanInsert(selectedTestPlan.clone());
		this.sessionBeanTCAMT.updateDataInstanceTestPlans();
		
		FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage("TestPlan sharing.",  "TestPlan: " + this.selectedTestPlan.getName() + " has been sented to " + this.selectedTestPlan.getAuthor().getUserId()) );
        
        this.init();
        
        this.sessionBeanTCAMT.setDitActiveIndex(0);
	}
	
	public void selectTestPlan(ActionEvent event) {
		this.selectedTestPlan = (DataInstanceTestPlan) event.getComponent().getAttributes().get("testplan");
		this.selectedTestPlan = this.sessionBeanTCAMT.getDbManager().getDataInstanceTestPlanById(this.selectedTestPlan.getId());
		this.selectedTestCase = null;
		this.selectedTestCaseGroup = null;
		this.selectedTestStep = null;
		this.createTestPlanTree(this.selectedTestPlan);
		this.sessionBeanTCAMT.setDitActiveIndex(1);
	}
	
	private void createTestPlanTree(DataInstanceTestPlan tp) {
		this.testplanRoot = new DefaultTreeNode("", null);
		TreeNode testplanNode = new DefaultTreeNode("plan", tp, this.testplanRoot);
		testplanNode.setExpanded(true);
		List<DataInstanceTestCaseGroup> sortedDataInstanceTestCaseGroups = new ArrayList<DataInstanceTestCaseGroup>(tp.getTestcasegroups());
		Collections.sort(sortedDataInstanceTestCaseGroups);
		for(DataInstanceTestCaseGroup ditcg:sortedDataInstanceTestCaseGroups){
			TreeNode groupNode = new DefaultTreeNode("group", ditcg, testplanNode);
			groupNode.setExpanded(true);
			List<DataInstanceTestCase> sortedDataInstanceTestCases = new ArrayList<DataInstanceTestCase>(ditcg.getTestcases());
			Collections.sort(sortedDataInstanceTestCases);
			for(DataInstanceTestCase ditc:sortedDataInstanceTestCases){
				TreeNode caseNode = new DefaultTreeNode("case", ditc, groupNode);
				caseNode.setExpanded(true);
				List<DataInstanceTestStep> sortedDataInstanceTestStep = new ArrayList<DataInstanceTestStep>(ditc.getTeststeps());
				Collections.sort(sortedDataInstanceTestStep);
				for(DataInstanceTestStep dits:sortedDataInstanceTestStep){
					new DefaultTreeNode("step", dits, caseNode);
				}
			}
		}
		List<DataInstanceTestCase> sortedDataInstanceTestCases = new ArrayList<DataInstanceTestCase>(tp.getTestcases());
		Collections.sort(sortedDataInstanceTestCases);
		for(DataInstanceTestCase ditc:sortedDataInstanceTestCases){
			TreeNode caseNode = new DefaultTreeNode("case", ditc, testplanNode);
			caseNode.setExpanded(true);
			List<DataInstanceTestStep> sortedDataInstanceTestStep = new ArrayList<DataInstanceTestStep>(ditc.getTeststeps());
			Collections.sort(sortedDataInstanceTestStep);
			for(DataInstanceTestStep dits:sortedDataInstanceTestStep){
				new DefaultTreeNode("step", dits, caseNode);
			}
		}
	}

	public void saveTestPlan() {
		if(this.selectedTestPlan.getId() <= 0){
			this.selectedTestPlan.setVersion(1);
			this.sessionBeanTCAMT.getDbManager().dataInstanceTestPlanInsert(this.selectedTestPlan);
			this.sessionBeanTCAMT.updateDataInstanceTestPlans();
		}else{
			this.selectedTestPlan.setVersion(this.selectedTestPlan.getVersion() + 1);
			this.sessionBeanTCAMT.getDbManager().dataInstanceTestPlanUpdate(this.selectedTestPlan);
			this.sessionBeanTCAMT.updateDataInstanceTestPlans();
		}
		
		FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage("TestPlan Saved.",  "TestPlan: " + this.selectedTestPlan.getName() + " has been saved.") );
	}
	
	public void addTestCaseGroup() {
		if(this.selectedNode != null){
			DataInstanceTestCaseGroup newDataInstacneTestCaseGroup = new DataInstanceTestCaseGroup();
			newDataInstacneTestCaseGroup.setVersion(1);
			newDataInstacneTestCaseGroup.setName("New TestCaseGroup");
			newDataInstacneTestCaseGroup.setPosition(this.selectedTestPlan.getTestcasegroups().size() + 1);
			this.selectedTestPlan.addTestCaseGroup(newDataInstacneTestCaseGroup);
			this.createTestPlanTree(this.selectedTestPlan);
		}
	}
	
	public void addTestCase() {
		if(this.selectedNode != null){
			DataInstanceTestCase newDataInstacneTestCase = new DataInstanceTestCase();
			newDataInstacneTestCase.setVersion(1);
			newDataInstacneTestCase.setName("New TestCase");
			
			if(this.selectedNode.getData() instanceof  DataInstanceTestPlan){
				newDataInstacneTestCase.setPosition(this.selectedTestPlan.getTestcases().size() + 1);
				((DataInstanceTestPlan)this.selectedNode.getData()).addTestCase(newDataInstacneTestCase);	
			}else if(this.selectedNode.getData() instanceof  DataInstanceTestCaseGroup){
				newDataInstacneTestCase.setPosition(((DataInstanceTestCaseGroup)this.selectedNode.getData()).getTestcases().size() + 1);
				((DataInstanceTestCaseGroup)this.selectedNode.getData()).addTestCase(newDataInstacneTestCase);
			}
			
			this.createTestPlanTree(this.selectedTestPlan);
		}
	}
	
	public void onNodeSelect(NodeSelectEvent event) throws CloneNotSupportedException, IOException {
		if(event.getTreeNode().getData() instanceof DataInstanceTestPlan){
			this.selectedTestCase = null;
			this.selectedTestCaseGroup = null;
			this.selectedTestStep = null;
		}else if(event.getTreeNode().getData() instanceof DataInstanceTestCase){
			this.selectedTestCase = (DataInstanceTestCase)event.getTreeNode().getData();
			this.selectedTestCaseGroup = null;
			this.selectedTestStep = null;	
		}else if(event.getTreeNode().getData() instanceof DataInstanceTestCaseGroup){
			this.selectedTestCase = null;
			this.selectedTestStep = null;
			this.selectedTestCaseGroup = (DataInstanceTestCaseGroup)event.getTreeNode().getData();
		}else if(event.getTreeNode().getData() instanceof DataInstanceTestStep){
			this.selectedTestStep = (DataInstanceTestStep)event.getTreeNode().getData();
			this.selectedTestCase = null;
			this.selectedTestCaseGroup = null;
			this.selectTestStep();
			this.activeIndexOfMessageInstancePanel = 0;
		}
		this.clearSelectNode(this.testplanRoot);
		event.getTreeNode().setSelected(true);
    }
	
	private void clearSelectNode(TreeNode node){
		node.setSelected(false);
		
		for(TreeNode child:node.getChildren()){
			clearSelectNode(child);
		}
	}
	
	public void deleteTestCase(ActionEvent event) {
		if(this.selectedNode != null){
			TreeNode parentNode = this.selectedNode.getParent();
			if(parentNode.getData() instanceof DataInstanceTestCaseGroup){
				this.selectedTestCaseGroup = (DataInstanceTestCaseGroup)parentNode.getData();
				this.selectedTestCaseGroup.getTestcases().remove(this.selectedNode.getData());
			}else if(parentNode.getData() instanceof DataInstanceTestPlan){
				this.selectedTestPlan.getTestcases().remove(this.selectedNode.getData());				
			}
			
			this.createTestPlanTree(this.selectedTestPlan);
			this.updatePositionForPlanAndTree();
		}
	}
	
	public void cloneTestCaseGroup(ActionEvent event) throws CloneNotSupportedException {
		if(this.selectedNode != null){
			DataInstanceTestCaseGroup group = ((DataInstanceTestCaseGroup)this.selectedNode.getData()).clone();
			group.setName("Copyed_" + group.getName());
			group.setPosition(this.selectedTestPlan.getTestcasegroups().size()+1);
			this.selectedTestPlan.addTestCaseGroup(group);
			this.createTestPlanTree(this.selectedTestPlan);
		}
	}
	
	public void cloneTestStep() throws CloneNotSupportedException {
		if(this.selectedNode != null){
			DataInstanceTestCase testcase = (DataInstanceTestCase)this.selectedNode.getParent().getData();
			DataInstanceTestStep step = ((DataInstanceTestStep)this.selectedNode.getData()).clone();
			step.setName("Copyed_" + step.getName());
			step.setPosition(testcase.getTeststeps().size()+1);
			testcase.addTestStep(step);
			this.createTestPlanTree(this.selectedTestPlan);
		}
	}
	
	public void cloneTestCase(ActionEvent event) throws CloneNotSupportedException {
		if(this.selectedNode != null){
			DataInstanceTestCase testcase = ((DataInstanceTestCase)this.selectedNode.getData()).clone();
			testcase.setName("Copyed_" + testcase.getName());
			
			if(this.selectedNode.getParent().getData() instanceof DataInstanceTestCaseGroup){
				DataInstanceTestCaseGroup group = (DataInstanceTestCaseGroup)this.selectedNode.getParent().getData();
				testcase.setPosition(group.getTestcases().size()+1);
				group.addTestCase(testcase);
			}else if(this.selectedNode.getParent().getData() instanceof DataInstanceTestPlan){
				DataInstanceTestPlan plan = (DataInstanceTestPlan)this.selectedNode.getParent().getData();
				testcase.setPosition(plan.getTestcases().size()+1);
				plan.addTestCase(testcase);
			}
			
			this.createTestPlanTree(this.selectedTestPlan);
		}
	}
	
	
	public void deleteTestCaseGroup(ActionEvent event) {
		if(this.selectedNode != null){
			this.selectedTestPlan.getTestcasegroups().remove(this.selectedNode.getData());
			this.createTestPlanTree(this.selectedTestPlan);
			this.updatePositionForPlanAndTree();
		}		
	}
	
	public void readHL7Message() throws CloneNotSupportedException, IOException{
		this.instanceSegments = new ArrayList<InstanceSegment>();
		if(this.selectedTestStep.getMessage().getHl7EndcodedMessage() != null && !this.selectedTestStep.getMessage().getHl7EndcodedMessage().equals("")){
			this.manageInstanceService.loadMessageInstance(this.selectedTestStep.getMessage(), this.instanceSegments);
			this.selectedInstanceSegment = null;
		}
		this.activeIndexOfMessageInstancePanel = 4;
		this.updateFilteredInstanceSegments();	
	}
	
	public void onInstanceSegmentSelect(SelectEvent event){
		this.segmentTreeRoot = new DefaultTreeNode("root", null);
		this.manageInstanceService.genSegmentTree(this.segmentTreeRoot, this.selectedInstanceSegment, this.selectedTestStep.getMessage());
		this.activeIndexOfMessageInstancePanel = 5;
		this.updateFilteredSegmentTree();
	}
	
	public void genrateHL7Message() throws CloneNotSupportedException, IOException{
		this.selectedTestStep.getMessage().setHl7EndcodedMessage(this.manageInstanceService.generateHL7Message(this.messageTreeRoot,  this.selectedTestStep.getMessage()));
		this.readHL7Message();
	}
	
	public void addTestStep(ActionEvent event) throws CloneNotSupportedException, IOException{
		if(this.selectedNode != null){
			this.selectedTestCase = (DataInstanceTestCase)this.selectedNode.getData();
			DataInstanceTestStep newTestStep = new DataInstanceTestStep();
			newTestStep.setName("New Test Step");
			newTestStep.setPosition(this.selectedTestCase.getTeststeps().size() + 1);
			this.selectedTestCase.addTestStep(newTestStep);
		
			this.createTestPlanTree(this.selectedTestPlan);
		}
		
	}
	
	public void deleteTestStep(ActionEvent event) {
		if(this.selectedNode != null){
			TreeNode parentNode = this.selectedNode.getParent();
			if(parentNode.getData() instanceof DataInstanceTestCase){
				this.selectedTestCase = (DataInstanceTestCase)parentNode.getData();
				this.selectedTestCase.getTeststeps().remove(this.selectedNode.getData());
			}
			
			this.createTestPlanTree(this.selectedTestPlan);
			this.updatePositionForPlanAndTree();
		}
	}
	
	private void selectTestStep() throws CloneNotSupportedException, IOException {
		this.selectedTestCaseGroup = null;
		this.testDataSpecificationHTML = null;
		this.jurorDocumentHTML = null;
		this.setMessageContentHTML(null);
		
		this.selectedInstanceSegment = new InstanceSegment();
		this.messageTreeRoot = new DefaultTreeNode("root", null);
		this.segmentTreeRoot = new DefaultTreeNode("root", null);
		this.setConstraintTreeRoot(new DefaultTreeNode("root", null));
		this.instanceSegments = new ArrayList<InstanceSegment>();
		this.filteredInstanceSegments =  new ArrayList<InstanceSegment>();
		this.manageInstanceService = new ManageInstance();
		
		if(this.selectedTestStep.getMessage().getConformanceProfile() == null) this.conformanceProfileId = null;
		else this.conformanceProfileId = this.selectedTestStep.getMessage().getConformanceProfile().getId();
		
		if(this.selectedTestStep.getMessage() != null && this.selectedTestStep.getMessage().getConformanceProfile() != null && this.selectedTestStep.getMessage().getName() != null){
			this.messageTreeRoot = this.manageInstanceService.loadMessage(this.selectedTestStep.getMessage());
			this.constraintTreeRoot = this.manageInstanceService.generateConstraintTree(this.selectedTestStep.getMessage());
			this.readHL7Message();
			
			this.generateTestDataSpecificationHTML();
			this.generateJurorDocumentHTML();
			this.generateMessageContentHTML();
		}
	}
	
	
	private void generateMessageContentHTML(){
		this.setMessageContentHTML(this.manageInstanceService.generateMessageContentHTML(this.selectedTestStep.getMessage(), instanceSegments));
	}
	
	private void generateJurorDocumentHTML() throws IOException{
		if(this.selectedTestStep.getMessage().getConformanceProfile().getJurorDocumentXSLT() != null){
			InputStream xsltInputStream = new ByteArrayInputStream(this.selectedTestStep.getMessage().getConformanceProfile().getJurorDocumentXSLT().getBytes());
			InputStream sourceInputStream = new ByteArrayInputStream(this.selectedTestStep.getMessage().getXmlEncodedNISTMessage().getBytes());
			Reader xsltReader =  new InputStreamReader(xsltInputStream, "UTF-8");
			Reader sourceReader = new InputStreamReader(sourceInputStream, "UTF-8");
			String xsltStr = IOUtils.toString(xsltReader);
			String sourceStr = IOUtils.toString(sourceReader);
			try {
				this.jurorDocumentHTML = XMLManager.parseXmlByXSLT(sourceStr, xsltStr);
			} catch (Exception e) {
				e.printStackTrace();
			}		
		}
	}
	
	private void generateTestDataSpecificationHTML() throws IOException{
		if(this.selectedTestStep.getMessage().getConformanceProfile().getTestDataSpecificationXSLT() != null){
			InputStream xsltInputStream = new ByteArrayInputStream(this.selectedTestStep.getMessage().getConformanceProfile().getTestDataSpecificationXSLT().getBytes());
			InputStream sourceInputStream = new ByteArrayInputStream(this.selectedTestStep.getMessage().getXmlEncodedNISTMessage().getBytes());
			Reader xsltReader =  new InputStreamReader(xsltInputStream, "UTF-8");
			Reader sourceReader = new InputStreamReader(sourceInputStream, "UTF-8");
			String xsltStr = IOUtils.toString(xsltReader);
			String sourceStr = IOUtils.toString(sourceReader);
			try {
				this.testDataSpecificationHTML = XMLManager.parseXmlByXSLT(sourceStr, xsltStr);
			} catch (Exception e) {
				e.printStackTrace();
			}		
		}
	}
	
	public void updateInstanceData(Object model) throws CloneNotSupportedException, IOException {
		int lineNum = this.instanceSegments.indexOf(this.selectedInstanceSegment);
		this.manageInstanceService.updateHL7Message(lineNum, this.manageInstanceService.generateLineStr(this.segmentTreeRoot), this.selectedTestStep.getMessage());
		this.readHL7Message();
		this.selectedInstanceSegment = this.instanceSegments.get(lineNum);
		this.activeIndexOfMessageInstancePanel = 5;
	}
	
	public void addNode(){
		
		TreeNode parent = this.selectedNode.getParent();
		
		int position = parent.getChildren().indexOf(this.selectedNode);
		
		MessageTreeModel model = (MessageTreeModel)this.selectedNode.getData();
		MessageTreeModel newModel = new MessageTreeModel(model.getMessageId(),model.getName(), model.getNode(), model.getPath(), model.getOccurrence());	
		TreeNode newNode = new DefaultTreeNode(((SegmentRefOrGroup)newModel.getNode()).getMax(), newModel, parent);
		
		this.manageInstanceService.populateTreeNode(newNode,  this.selectedTestStep.getMessage());
		
		parent.getChildren().add(position + 1, newNode);
	}
	
	public void downloadResourceBundleForTestPlan(DataInstanceTestPlan tp) throws Exception{
		this.setTestStoryConverter(new JsonTestStoryConverter());
		this.setTpConverter(new JsonDataInstanceTestPlanConverter());
		this.setTcConverter(new JsonDataInstanceTestCaseConverter());
		
		
		String outFilename = "TestPlan_" + tp.getName() + ".zip";
		ByteArrayOutputStream outputStream = null;
		byte[] bytes;
		outputStream = new ByteArrayOutputStream();
		ZipOutputStream out = new ZipOutputStream(outputStream);
		
		this.generateTestPlanJsonRB(out, tp);
		
		for(DataInstanceTestCaseGroup ditg:tp.getTestcasegroups()){
			String groupPath = ditg.getName();
			for(DataInstanceTestCase ditc:ditg.getTestcases()){
				String testcasePath = groupPath + File.separator + ditc.getName();
				this.generateTestStoryRB(out, ditc.getTestCaseStory(), testcasePath);
				for(DataInstanceTestStep dits:ditc.getTeststeps()){
					String teststepPath = testcasePath + File.separator + dits.getName();
					this.generateMessageRB(out, dits.getMessage().getHl7EndcodedMessage(), teststepPath);
					this.generateProfileRB(out, dits.getMessage().getConformanceProfile().getIntegratedProfile().getProfile(), teststepPath);
					this.generateValueSetRB(out, dits.getMessage().getConformanceProfile().getIntegratedProfile().getValueSet(), teststepPath);
					this.generateConstraintRB(out, dits.getMessage(), teststepPath);
					this.generateTestStoryRB(out, dits.getTestStepStory(), teststepPath);
				}
			}
		}
		
		for(DataInstanceTestCase ditc:tp.getTestcases()){
			String testcasePath = ditc.getName();
			this.generateTestStoryRB(out, ditc.getTestCaseStory(), testcasePath);
			for(DataInstanceTestStep dits:ditc.getTeststeps()){
				String teststepPath = testcasePath + File.separator + dits.getName();
				this.generateMessageRB(out, dits.getMessage().getHl7EndcodedMessage(), teststepPath);
				this.generateProfileRB(out, dits.getMessage().getConformanceProfile().getIntegratedProfile().getProfile(), teststepPath);
				this.generateValueSetRB(out, dits.getMessage().getConformanceProfile().getIntegratedProfile().getValueSet(), teststepPath);
				this.generateConstraintRB(out, dits.getMessage(), teststepPath);
				this.generateTestStoryRB(out, dits.getTestStepStory(), teststepPath);
			}
		}
		out.close();
		bytes = outputStream.toByteArray();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
		this.setZipResourceBundleFile(new DefaultStreamedContent(inputStream, "application/zip", outFilename));
	}
	
	private void generateMessageRB(ZipOutputStream out, String messageStr, String path) throws IOException {
		byte[] buf = new byte[1024];
		out.putNextEntry(new ZipEntry(path + File.separator + "Message.txt"));
		if(messageStr != null){
			InputStream inMessage = IOUtils.toInputStream(messageStr,"UTF-8");
			int lenMessage;
            while ((lenMessage = inMessage.read(buf)) > 0) {
                out.write(buf, 0, lenMessage);
            }
            inMessage.close();
		}
        out.closeEntry();
	}
	
	private void generateProfileRB(ZipOutputStream out, String profileStr, String path) throws SAXException, ParserConfigurationException, Exception {
		byte[] buf = new byte[1024];
		out.putNextEntry(new ZipEntry(path + File.separator + "Profile.xml"));
		if(profileStr != null){
			InputStream inMessage = IOUtils.toInputStream(XMLManager.docToString(XMLManager.stringToDom(profileStr)),"UTF-8");
			int lenMessage;
            while ((lenMessage = inMessage.read(buf)) > 0) {
                out.write(buf, 0, lenMessage);
            }
            inMessage.close();
		}
        out.closeEntry();
	}
	
	private void generateConstraintRB(ZipOutputStream out, Message m, String path) throws Exception {
		byte[] buf = new byte[1024];
		out.putNextEntry(new ZipEntry(path + File.separator + "Constraints.xml"));		
		org.w3c.dom.Document constraintDom = XMLManager.stringToDom(m.getConformanceProfile().getIntegratedProfile().getConstraints());
		
		this.manageInstanceService = new ManageInstance();
		this.manageInstanceService.createConstraintDocument(constraintDom, m);
		
		String constraintStr = XMLManager.docToString(constraintDom);
		
		
		
		if(constraintStr != null){
			InputStream inMessage = IOUtils.toInputStream(constraintStr, "UTF-8");
			int lenMessage;
            while ((lenMessage = inMessage.read(buf)) > 0) {
                out.write(buf, 0, lenMessage);
            }
            inMessage.close();
		}
        out.closeEntry();
	}
	
	private void generateValueSetRB(ZipOutputStream out, String valueSetStr, String path) throws SAXException, ParserConfigurationException, Exception {
		byte[] buf = new byte[1024];
		out.putNextEntry(new ZipEntry(path + File.separator + "ValueSets.xml"));
		if(valueSetStr != null){
			InputStream inMessage = IOUtils.toInputStream(XMLManager.docToString(XMLManager.stringToDom(valueSetStr)), "UTF-8");
			int lenMessage;
            while ((lenMessage = inMessage.read(buf)) > 0) {
                out.write(buf, 0, lenMessage);
            }
            inMessage.close();
		}
        out.closeEntry();
	}
	
	private void generateTestStoryRB(ZipOutputStream out, TestStory testStory, String path) throws IOException, DocumentException, ConversionException {
		byte[] buf = new byte[1024];
		
		out.putNextEntry(new ZipEntry(path + File.separator + "TestStory.html"));
        String testCaseStoryStr = this.generateTestStory(testStory);
        InputStream inTestStory = IOUtils.toInputStream(testCaseStoryStr, "UTF-8");
        int lenTestStory;
        while ((lenTestStory = inTestStory.read(buf)) > 0) {
            out.write(buf, 0, lenTestStory);
        }
        inTestStory.close();
        out.closeEntry();
        
        out.putNextEntry(new ZipEntry(path + File.separator + "TestStory.pdf"));
        
        Document document = new Document(PageSize.LETTER);
        ByteArrayOutputStream outputStream = null;
		byte[] bytes;
		outputStream = new ByteArrayOutputStream();
		
        PdfWriter pdfWriter = PdfWriter.getInstance(document, outputStream);
        document.open();
        document.addAuthor("SSD ITL NIST");
        document.addCreator("TCAMT");
        document.addSubject("Test Story");
        document.addCreationDate();
        document.addTitle("Test Story");
        
        
        XMLWorkerHelper worker = XMLWorkerHelper.getInstance();
        
        worker.parseXHtml(pdfWriter, document, new StringReader(testCaseStoryStr));
        document.close();
        
        bytes = outputStream.toByteArray();
		ByteArrayInputStream inTestStoryPDF = new ByteArrayInputStream(bytes);
        int lenTestStoryPDF;
        while ((lenTestStoryPDF = inTestStoryPDF.read(buf)) > 0) {
            out.write(buf, 0, lenTestStoryPDF);
        }
        inTestStoryPDF.close();
        out.closeEntry();
	}
	
	private String generateTestStory(TestStory testStory) throws IOException {
		if(testStory == null){
			testStory = new TestStory();
		}
		
		ClassLoader classLoader = getClass().getClassLoader();
		String testStoryStr = IOUtils.toString(classLoader.getResourceAsStream("TestStory.html"));
		testStoryStr = testStoryStr.replace("?Description?", testStory.getTeststorydesc());
		testStoryStr = testStoryStr.replace("?Comments?", testStory.getComments());
		testStoryStr = testStoryStr.replace("?PreCondition?", testStory.getPreCondition());
		testStoryStr = testStoryStr.replace("?PostCondition?", testStory.getPostCondition());
		testStoryStr = testStoryStr.replace("?TestObjectives?", testStory.getTestObjectives());
		testStoryStr = testStoryStr.replace("?EvaluationCriteria?", testStory.getEvaluationCriteria());
		testStoryStr = testStoryStr.replace("?Notes?", testStory.getNotes());
		testStoryStr = testStoryStr.replace("<br>", " ");
		testStoryStr = testStoryStr.replace("</br>", " ");
		return testStoryStr;
	}
	
	private void generateTestPlanJsonRB(ZipOutputStream out, DataInstanceTestPlan tp) throws IOException, ConversionException {
		byte[] buf = new byte[1024];
		out.putNextEntry(new ZipEntry("TestPlan.json"));
		InputStream inTP = IOUtils.toInputStream(this.tpConverter.toString(tp));
		int lenTP;
        while ((lenTP = inTP.read(buf)) > 0) {
            out.write(buf, 0, lenTP);
        }
        out.closeEntry();
        inTP.close();
	}
	
	public void createTCAMTConstraint(Object model) {
		String ipath = null;
		String data = null;
		String level = "DataInstanceTestCase";
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
		this.selectedTestStep.getMessage().deleteTCAMTConstraintByIPath(ipath);

		if(tdc != null && !tdc.getValue().equals("")){
			TCAMTConstraint tcamtConstraint = new TCAMTConstraint();
			tcamtConstraint.setCategorization(tdc);
			tcamtConstraint.setData(data);
			tcamtConstraint.setIpath(ipath);
			tcamtConstraint.setLevel(level);
			tcamtConstraint.setiPosition(iPosition);
			tcamtConstraint.setMessageName(messageName);
			tcamtConstraint.setUsageList(usageList);
			this.selectedTestStep.getMessage().addTCAMTConstraint(tcamtConstraint);
		}
		this.constraintTreeRoot = this.manageInstanceService.generateConstraintTree(this.selectedTestStep.getMessage());
	}
	
	public void profileUpdateMessage() throws CloneNotSupportedException, IOException{
		this.setActiveIndexOfMessageInstancePanel(1);
		this.sessionBeanTCAMT.setmActiveIndex(2);
		this.messageTreeRoot = this.manageInstanceService.loadMessage(this.selectedTestStep.getMessage());
		this.instanceSegments = new ArrayList<InstanceSegment>();
		this.readHL7Message();
		
	}
	
	public void deleteConstraint(String ipath){
		this.selectedTestStep.getMessage().deleteTCAMTConstraintByIPath(ipath);
		this.selectedInstanceSegment = null;
		this.segmentTreeRoot = new DefaultTreeNode("root", null);
		this.constraintTreeRoot = this.manageInstanceService.generateConstraintTree(this.selectedTestStep.getMessage());
	}
	
	public void updateFilteredInstanceSegments() {
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
    }
	
	public void updateFilteredSegmentTree(){	
		if(this.usageViewOption2.equals("all")){
			this.filtedSegmentTreeRoot = this.segmentTreeRoot;
		}else {
			this.filtedSegmentTreeRoot = this.manageInstanceService.genRestrictedTree(this.segmentTreeRoot);
		}
		
	}
	
	public void addRepeatedField(FieldModel fieldModel){
		this.manageInstanceService.addRepeatedField(fieldModel, this.segmentTreeRoot, this.selectedTestStep.getMessage());
		this.updateFilteredSegmentTree();
	}
	
	private void updatePositionForPlanAndTree(){
		int groupIndex = 1;
		int caseInPlanIndex = 1;
		
		for(TreeNode child:this.testplanRoot.getChildren().get(0).getChildren()){
			if(child.getData() instanceof DataInstanceTestCaseGroup){
				DataInstanceTestCaseGroup group = (DataInstanceTestCaseGroup)child.getData();
				group.setPosition(groupIndex);
				groupIndex = groupIndex + 1;
				
				int caseInGroupIndex = 1;
				
				for(TreeNode grandChild:child.getChildren()){
					DataInstanceTestCase testcase = (DataInstanceTestCase)grandChild.getData();
					testcase.setPosition(caseInGroupIndex);
					caseInGroupIndex = caseInGroupIndex + 1;
					
					int stepIndex = 1;
					
					for(TreeNode grandGrandChild:grandChild.getChildren()){
						DataInstanceTestStep teststep = (DataInstanceTestStep)grandGrandChild.getData();
						teststep.setPosition(stepIndex);
						stepIndex = stepIndex + 1;
					}
				}
				
			}else if(child.getData() instanceof DataInstanceTestCase){
				DataInstanceTestCase testcase = (DataInstanceTestCase)child.getData();
				testcase.setPosition(caseInPlanIndex);
				caseInPlanIndex = caseInPlanIndex + 1;
				
				int stepIndex = 1;
				
				for(TreeNode grandChild:child.getChildren()){
					DataInstanceTestStep teststep = (DataInstanceTestStep)grandChild.getData();
					teststep.setPosition(stepIndex);
					stepIndex = stepIndex + 1;
				}
			}
		}
	}
	
	private void addMovedNode(TreeNode dragNode, TreeNode dropNode, int dropIndex){
		if(selectedTestPlan.equals(dropNode.getData())){
			if(dragNode.getData() instanceof DataInstanceTestCaseGroup){
				DataInstanceTestCaseGroup testGroup = (DataInstanceTestCaseGroup)dragNode.getData();
				testGroup.setPosition(dropIndex + 1);
				selectedTestPlan.addTestCaseGroup(testGroup);
				return;
			}else if (dragNode.getData() instanceof DataInstanceTestCase){
				DataInstanceTestCase testCase = (DataInstanceTestCase)dragNode.getData();
				testCase.setPosition(dropIndex - selectedTestPlan.getTestcasegroups().size() + 1);
				selectedTestPlan.addTestCase(testCase);
				return;
			}
		}
		
		for(DataInstanceTestCaseGroup group:this.selectedTestPlan.getTestcasegroups()){
			if(group.equals(dropNode.getData())){
				DataInstanceTestCase testCase = (DataInstanceTestCase)dragNode.getData();
				testCase.setPosition(dropIndex + 1);
				group.getTestcases().add(testCase);
				return;
			}
			for(DataInstanceTestCase testcase:group.getTestcases()){
				if(testcase.equals(dropNode.getData())){
					DataInstanceTestStep testStep = (DataInstanceTestStep)dragNode.getData();
					testStep.setPosition(dropIndex + 1);
					testcase.getTeststeps().add(testStep);
					return;
				}
			}
		}
		for(DataInstanceTestCase testcase:this.selectedTestPlan.getTestcases()){
			if(testcase.equals(dropNode.getData())){
				DataInstanceTestStep testStep = (DataInstanceTestStep)dragNode.getData();
				testStep.setPosition(dropIndex + 1);
				testcase.getTeststeps().add(testStep);
				return;
			}
		}
	}
	
	private void updateTestPlanByTree(TreeNode dragNode, TreeNode dropNode, int dropIndex){
		this.selectedTestPlan.getTestcasegroups().remove(dragNode.getData());
		this.selectedTestPlan.getTestcases().remove(dragNode.getData());
		for(DataInstanceTestCaseGroup group:this.selectedTestPlan.getTestcasegroups()){
			group.getTestcases().remove(dragNode.getData());
			for(DataInstanceTestCase testcase:group.getTestcases()){
				testcase.getTeststeps().remove(dragNode.getData());
			}
		}
		for(DataInstanceTestCase testcase:this.selectedTestPlan.getTestcases()){
			testcase.getTeststeps().remove(dragNode.getData());
		}
		
		this.addMovedNode(dragNode, dropNode, dropIndex);
		
		this.updatePositionForPlanAndTree();
		this.createTestPlanTree(this.selectedTestPlan);
		this.updatePositionForPlanAndTree();
		
	}
	
	public void updateMessageTestStep() throws CloneNotSupportedException, IOException{
		Message m = this.sessionBeanTCAMT.getDbManager().getMessageById(this.messageId);
		m.setId(this.selectedTestStep.getMessage().getId());
		m.setAuthor(null);
		this.selectedTestStep.setMessage(m);
		this.messageId = null;
		
		selectTestStep();
	}
	
	public void updateConformanceProfileTestStep() throws CloneNotSupportedException, IOException{
		this.selectedTestStep.getMessage().setConformanceProfile(this.sessionBeanTCAMT.getDbManager().getConformanceProfileById(this.conformanceProfileId));
		selectTestStep();
	}
	
	public void onDragDrop(TreeDragDropEvent event) {
        TreeNode dragNode = event.getDragNode();
        TreeNode dropNode = event.getDropNode();
        int dropIndex = event.getDropIndex();
        
        if(dragNode.getData() instanceof DataInstanceTestPlan){
        	if(dropNode.getData() instanceof DataInstanceTestPlan){
        		FacesContext context = FacesContext.getCurrentInstance();
                context.addMessage(null, new FacesMessage("Invalid", "Test Plan cannot have another Test Plan. Have been reverted."));
        		this.createTestPlanTree(this.selectedTestPlan);
        	}else if(dropNode.getData() instanceof DataInstanceTestCaseGroup){
        		FacesContext context = FacesContext.getCurrentInstance();
                context.addMessage(null, new FacesMessage("Invalid", "Test Group cannot have Test Plan. Have been reverted."));
        		this.createTestPlanTree(this.selectedTestPlan);
        	}else if(dropNode.getData() instanceof DataInstanceTestCase){
        		FacesContext context = FacesContext.getCurrentInstance();
                context.addMessage(null, new FacesMessage("Invalid", "Test Case cannot have Test Plan. Have been reverted."));
        		this.createTestPlanTree(this.selectedTestPlan);
        	}else if(dropNode.getData() instanceof DataInstanceTestStep){
        		FacesContext context = FacesContext.getCurrentInstance();
                context.addMessage(null, new FacesMessage("Invalid", "Test Step cannot have Test Plan. Have been reverted."));
        		this.createTestPlanTree(this.selectedTestPlan);
        	}
        }else if(dragNode.getData() instanceof DataInstanceTestCaseGroup){
        	if(dropNode.getData() instanceof DataInstanceTestPlan){
        		this.updateTestPlanByTree(dragNode, dropNode, dropIndex);
        	}else if(dropNode.getData() instanceof DataInstanceTestCaseGroup){
        		FacesContext context = FacesContext.getCurrentInstance();
                context.addMessage(null, new FacesMessage("Invalid", "Test Group cannot have another Test Group. Have been reverted."));
        		this.createTestPlanTree(this.selectedTestPlan);
        	}else if(dropNode.getData() instanceof DataInstanceTestCase){
        		FacesContext context = FacesContext.getCurrentInstance();
                context.addMessage(null, new FacesMessage("Invalid", "Test Case cannot have Test Group. Have been reverted."));
        		this.createTestPlanTree(this.selectedTestPlan);
        	}else if(dropNode.getData() instanceof DataInstanceTestStep){
        		FacesContext context = FacesContext.getCurrentInstance();
                context.addMessage(null, new FacesMessage("Invalid", "Test Step cannot have Test Group. Have been reverted."));
        		this.createTestPlanTree(this.selectedTestPlan);
        	}
        }else if(dragNode.getData() instanceof DataInstanceTestCase){
        	if(dropNode.getData() instanceof DataInstanceTestPlan){
        		this.updateTestPlanByTree(dragNode, dropNode, dropIndex);
        	}else if(dropNode.getData() instanceof DataInstanceTestCaseGroup){
        		this.updateTestPlanByTree(dragNode, dropNode, dropIndex);
        	}else if(dropNode.getData() instanceof DataInstanceTestCase){
        		FacesContext context = FacesContext.getCurrentInstance();
                context.addMessage(null, new FacesMessage("Invalid", "Test Case cannot have another Test Case. Have been reverted."));
        		this.createTestPlanTree(this.selectedTestPlan);
        	}else if(dropNode.getData() instanceof DataInstanceTestStep){
        		FacesContext context = FacesContext.getCurrentInstance();
                context.addMessage(null, new FacesMessage("Invalid", "Test Step cannot have Test Case. Have been reverted."));
        		this.createTestPlanTree(this.selectedTestPlan);
        	}
        }else if(dragNode.getData() instanceof DataInstanceTestStep){
        	if(dropNode.getData() instanceof DataInstanceTestPlan){
        		FacesContext context = FacesContext.getCurrentInstance();
                context.addMessage(null, new FacesMessage("Invalid", "Test Plan cannot have Test Step. Have been reverted."));
        		this.createTestPlanTree(this.selectedTestPlan);
        	}else if(dropNode.getData() instanceof DataInstanceTestCaseGroup){
        		FacesContext context = FacesContext.getCurrentInstance();
                context.addMessage(null, new FacesMessage("Invalid", "Test Group cannot have Test Step. Have been reverted."));
        		this.createTestPlanTree(this.selectedTestPlan);
        	}else if(dropNode.getData() instanceof DataInstanceTestCase){
        		this.updateTestPlanByTree(dragNode, dropNode, dropIndex);
        	}else if(dropNode.getData() instanceof DataInstanceTestStep){
        		FacesContext context = FacesContext.getCurrentInstance();
                context.addMessage(null, new FacesMessage("Invalid", "Test Step cannot have another Test Step. Have been reverted."));
        		this.createTestPlanTree(this.selectedTestPlan);
        	}
        }
        
    }
	

	public List<DataInstanceTestPlan> getTestPlans() {
		return this.sessionBeanTCAMT.getDataInstanceTestPlans();
	}

	public SessionBeanTCAMT getSessionBeanTCAMT() {
		return sessionBeanTCAMT;
	}

	public void setSessionBeanTCAMT(SessionBeanTCAMT sessionBeanTCAMT) {
		this.sessionBeanTCAMT = sessionBeanTCAMT;
	}

	public DataInstanceTestPlan getSelectedTestPlan() {
		return selectedTestPlan;
	}

	public void setSelectedTestPlan(DataInstanceTestPlan selectedTestPlan) {
		this.selectedTestPlan = selectedTestPlan;
	}


	public DataInstanceTestCase getSelectedTestCase() {
		return selectedTestCase;
	}


	public void setSelectedTestCase(DataInstanceTestCase selectedTestCase) {
		this.selectedTestCase = selectedTestCase;
	}
	
	public TreeNode getTestplanRoot() {
        return testplanRoot;
    }
 
    public TreeNode getSelectedNode() {
        return selectedNode;
    }
 
    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }


	public DataInstanceTestCaseGroup getSelectedTestCaseGroup() {
		return selectedTestCaseGroup;
	}


	public void setSelectedTestCaseGroup(DataInstanceTestCaseGroup selectedTestCaseGroup) {
		this.selectedTestCaseGroup = selectedTestCaseGroup;
	}
	
	


	public Long getMessageId() {
		return messageId;
	}


	public void setMessageId(Long messageId) {
		this.messageId = messageId;
	}


	public InstanceSegment getSelectedInstanceSegment() {
		return selectedInstanceSegment;
	}


	public void setSelectedInstanceSegment(InstanceSegment selectedInstanceSegment) {
		this.selectedInstanceSegment = selectedInstanceSegment;
	}


	public TreeNode getMessageTreeRoot() {
		return messageTreeRoot;
	}


	public void setMessageTreeRoot(TreeNode messageTreeRoot) {
		this.messageTreeRoot = messageTreeRoot;
	}


	public List<InstanceSegment> getInstanceSegments() {
		return instanceSegments;
	}


	public void setInstanceSegments(List<InstanceSegment> instanceSegments) {
		this.instanceSegments = instanceSegments;
	}


	public ManageInstance getManageInstanceService() {
		return manageInstanceService;
	}


	public void setManageInstanceService(ManageInstance manageInstanceService) {
		this.manageInstanceService = manageInstanceService;
	}

	public StreamedContent getZipResourceBundleFile() {
		return zipResourceBundleFile;
	}


	public void setZipResourceBundleFile(StreamedContent zipResourceBundleFile) {
		this.zipResourceBundleFile = zipResourceBundleFile;
	}


	public TestStoryConverter getTestStoryConverter() {
		return testStoryConverter;
	}


	public void setTestStoryConverter(TestStoryConverter testStoryConverter) {
		this.testStoryConverter = testStoryConverter;
	}


	public DataInstanceTestCaseConverter getTcConverter() {
		return tcConverter;
	}


	public void setTcConverter(DataInstanceTestCaseConverter tcConverter) {
		this.tcConverter = tcConverter;
	}


	public DataInstanceTestPlanConverter getTpConverter() {
		return tpConverter;
	}


	public void setTpConverter(DataInstanceTestPlanConverter tpConverter) {
		this.tpConverter = tpConverter;
	}


	public void setTestplanRoot(TreeNode testplanRoot) {
		this.testplanRoot = testplanRoot;
	}


	public TreeNode getSegmentTreeRoot() {
		return segmentTreeRoot;
	}


	public void setSegmentTreeRoot(TreeNode segmentTreeRoot) {
		this.segmentTreeRoot = segmentTreeRoot;
	}


	public int getActiveIndexOfMessageInstancePanel() {
		return activeIndexOfMessageInstancePanel;
	}


	public void setActiveIndexOfMessageInstancePanel(
			int activeIndexOfMessageInstancePanel) {
		this.activeIndexOfMessageInstancePanel = activeIndexOfMessageInstancePanel;
	}


	public DataInstanceTestStep getSelectedTestStep() {
		return selectedTestStep;
	}


	public void setSelectedTestStep(DataInstanceTestStep selectedTestStep) {
		this.selectedTestStep = selectedTestStep;
	}

	public Long getShareTo() {
		return shareTo;
	}


	public void setShareTo(Long shareTo) {
		this.shareTo = shareTo;
	}

	public TreeNode getConstraintTreeRoot() {
		return constraintTreeRoot;
	}

	public void setConstraintTreeRoot(TreeNode constraintTreeRoot) {
		this.constraintTreeRoot = constraintTreeRoot;
	}

	public List<InstanceSegment> getFilteredInstanceSegments() {
		return filteredInstanceSegments;
	}

	public void setFilteredInstanceSegments(List<InstanceSegment> filteredInstanceSegments) {
		this.filteredInstanceSegments = filteredInstanceSegments;
	}

	public String getUsageViewOption() {
		return usageViewOption;
	}

	public void setUsageViewOption(String usageViewOption) {
		this.usageViewOption = usageViewOption;
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

	public String getTestDataSpecificationHTML() {
		return testDataSpecificationHTML;
	}

	public void setTestDataSpecificationHTML(String testDataSpecificationHTML) {
		this.testDataSpecificationHTML = testDataSpecificationHTML;
	}

	public String getJurorDocumentHTML() {
		return jurorDocumentHTML;
	}

	public void setJurorDocumentHTML(String jurorDocumentHTML) {
		this.jurorDocumentHTML = jurorDocumentHTML;
	}

	public String getMessageContentHTML() {
		return messageContentHTML;
	}

	public void setMessageContentHTML(String messageContentHTML) {
		this.messageContentHTML = messageContentHTML;
	}
	
	

}
