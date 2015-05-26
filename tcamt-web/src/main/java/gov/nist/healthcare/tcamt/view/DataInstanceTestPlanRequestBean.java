package gov.nist.healthcare.tcamt.view;

import gov.nist.healthcare.core.hl7.v2.parser.ParserException;
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
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
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
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
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
	private DataInstanceTestStep newTestStep = new DataInstanceTestStep();
	private Long messageId = null;
	private Long shareTo;
	private int activeIndexOfMessageInstancePanel = 0;
	
	private TreeNode testplanRoot;
    private TreeNode selectedNode;
    
    private TreeNode segmentTreeRoot;
	private TreeNode messageTreeRoot;
	private TreeNode constraintTreeRoot;
    
    private InstanceSegment selectedInstanceSegment= null;
	private List<InstanceSegment> instanceSegments;
	private ManageInstance manageInstanceService;
	
	private transient StreamedContent zipResourceBundleFile;
	
	private TestStoryConverter testStoryConverter;
	private DataInstanceTestCaseConverter tcConverter;
	private DataInstanceTestPlanConverter tpConverter;

	private void init(){
		this.selectedTestPlan = new DataInstanceTestPlan();
		this.selectedTestPlan.setVersion(1);
		this.selectedTestPlan.setAuthor(this.sessionBeanTCAMT.getLoggedUser());
		this.selectedTestCase = null;
		this.selectedTestStep = null;
		this.selectedTestCaseGroup = null;
		this.messageId = null;
		this.shareTo = null;
		
		this.selectedInstanceSegment = null;
		this.segmentTreeRoot = new DefaultTreeNode("root", null);
		this.messageTreeRoot = new DefaultTreeNode("root", null);
		this.setConstraintTreeRoot(new DefaultTreeNode("root", null));
		this.instanceSegments = new ArrayList<InstanceSegment>();
		this.manageInstanceService = new ManageInstance();
		this.sessionBeanTCAMT.setDitActiveIndex(0);
		this.setActiveIndexOfMessageInstancePanel(2);
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
		for(DataInstanceTestCaseGroup ditcg:tp.getTestcasegroups()){
			TreeNode groupNode = new DefaultTreeNode("group", ditcg, testplanNode);
			groupNode.setExpanded(true);
			for(DataInstanceTestCase ditc:ditcg.getTestcases()){
				new DefaultTreeNode("case", ditc, groupNode);
			}
		}
		for(DataInstanceTestCase ditc:tp.getTestcases()){
			new DefaultTreeNode("case", ditc, testplanNode);
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
			this.selectedTestPlan.addTestCaseGroup(newDataInstacneTestCaseGroup);
			
			this.selectedNode.setSelected(false);
			this.selectedNode.setExpanded(true);
			this.selectedNode = new DefaultTreeNode("group", newDataInstacneTestCaseGroup, this.selectedNode);
			this.selectedNode.setSelected(true);
			
			this.selectedTestCaseGroup = newDataInstacneTestCaseGroup;
			this.selectedTestCase = null;
			this.selectedTestStep = null;	
		}
	}
	
	public void addTestCase() {
		if(this.selectedNode != null){
			DataInstanceTestCase newDataInstacneTestCase = new DataInstanceTestCase();
			newDataInstacneTestCase.setVersion(1);
			newDataInstacneTestCase.setName("New TestCase");
			
			if(this.selectedNode.getData() instanceof  DataInstanceTestPlan){
				((DataInstanceTestPlan)this.selectedNode.getData()).addTestCase(newDataInstacneTestCase);	
			}else if(this.selectedNode.getData() instanceof  DataInstanceTestCaseGroup){
				((DataInstanceTestCaseGroup)this.selectedNode.getData()).addTestCase(newDataInstacneTestCase);
			}
			
			this.selectedNode.setSelected(false);
			this.selectedNode.setExpanded(true);
			this.selectedNode = new DefaultTreeNode("case", newDataInstacneTestCase, this.selectedNode);
			this.selectedNode.setSelected(true);
			this.selectedTestCase = newDataInstacneTestCase;
			this.selectedTestCaseGroup = null;
			this.selectedTestStep = null;	
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
			
			this.messageTreeRoot = new DefaultTreeNode("root", null);
			this.segmentTreeRoot = new DefaultTreeNode("root", null);
			this.setConstraintTreeRoot(new DefaultTreeNode("root", null));
			
		}else if(event.getTreeNode().getData() instanceof DataInstanceTestCaseGroup){
			this.selectedTestCase = null;
			this.selectedTestStep = null;
			this.selectedTestCaseGroup = (DataInstanceTestCaseGroup)event.getTreeNode().getData();
		}
    }
	
	public void deleteTestCase(ActionEvent event) {
		if(this.selectedNode != null){
			TreeNode parentNode = this.selectedNode.getParent();
			this.selectedTestPlan.getTestcases().remove(this.selectedNode.getData());
			this.selectedTestCase = null;
			
			selectedNode.getChildren().clear();
	        selectedNode.getParent().getChildren().remove(selectedNode);
			
			if(parentNode.getData() instanceof DataInstanceTestPlan){
				this.selectedTestCase = null;
				this.selectedTestCaseGroup = null;
				this.selectedTestStep = null;
			}else if(parentNode.getData() instanceof DataInstanceTestCase){
				this.selectedTestCase = (DataInstanceTestCase)parentNode.getData();
				this.selectedTestCaseGroup = null;
				this.selectedTestStep = null;
			}else if(parentNode.getData() instanceof DataInstanceTestCaseGroup){
				this.selectedTestCase = null;
				this.selectedTestCaseGroup = (DataInstanceTestCaseGroup)parentNode.getData();
				this.selectedTestStep = null;
			}
			this.selectedNode = parentNode;
			this.selectedNode.setSelected(true);	
		}
	}
	
	public void cloneTestCaseGroup(ActionEvent event) throws CloneNotSupportedException {
		if(this.selectedNode != null){
			DataInstanceTestCaseGroup group = ((DataInstanceTestCaseGroup)this.selectedNode.getData()).clone();
			group.setName("Copyed_" + group.getName());
			this.selectedNode.setSelected(false);
			this.selectedNode = new DefaultTreeNode("group", group, this.selectedNode.getParent());
			
			this.selectedTestCaseGroup = group;
			this.selectedTestCase = null;
			this.selectedTestStep = null;
			
			for(DataInstanceTestCase testcase:group.getTestcases()){
				testcase.setName("Copyed_" + testcase.getName());
				new DefaultTreeNode("case", testcase, this.selectedNode);
			}
			
			this.selectedNode.setExpanded(true);
			this.selectedTestPlan.addTestCaseGroup(group);
			
		}
	}
	
	public void cloneTestCase(ActionEvent event) throws CloneNotSupportedException {
		if(this.selectedNode != null){
			DataInstanceTestCase testcase = ((DataInstanceTestCase)this.selectedNode.getData()).clone();
			testcase.setName("Copyed_" + testcase.getName());
			this.selectedNode.setSelected(false);
			this.selectedNode = new DefaultTreeNode("case", testcase, this.selectedNode.getParent());
			
			this.selectedTestCase = testcase;
			this.selectedTestCaseGroup = null;
			this.selectedTestStep = null;
			
			if(this.selectedNode.getParent().getData() instanceof DataInstanceTestCaseGroup){
				DataInstanceTestCaseGroup group = (DataInstanceTestCaseGroup)this.selectedNode.getParent().getData();
				group.addTestCase(testcase);
			}else if(this.selectedNode.getParent().getData() instanceof DataInstanceTestPlan){
				DataInstanceTestPlan plan = (DataInstanceTestPlan)this.selectedNode.getParent().getData();
				plan.addTestCase(testcase);
			}
		}
	}
	
	
	public void deleteTestCaseGroup(ActionEvent event) {
		if(this.selectedNode != null){
			TreeNode parentNode = this.selectedNode.getParent();
			this.selectedTestPlan.getTestcasegroups().remove(this.selectedNode.getData());
			this.selectedTestCaseGroup = null;
			
			selectedNode.getChildren().clear();
	        selectedNode.getParent().getChildren().remove(selectedNode);
	        
			if(parentNode.getData() instanceof DataInstanceTestPlan){
				this.selectedTestCase = null;
				this.selectedTestCaseGroup = null;
				this.selectedTestStep = null;
			}else if(parentNode.getData() instanceof DataInstanceTestCase){
				this.selectedTestCase = (DataInstanceTestCase)parentNode.getData();
				this.selectedTestCaseGroup = null;
				this.selectedTestStep = null;
			}else if(parentNode.getData() instanceof DataInstanceTestCaseGroup){
				this.selectedTestCase = null;
				this.selectedTestCaseGroup = (DataInstanceTestCaseGroup)parentNode.getData();
				this.selectedTestStep = null;
			}
			this.selectedNode = parentNode;
			this.selectedNode.setSelected(true);	
		}		
	}
	
	public void readHL7Message() throws CloneNotSupportedException, IOException{
		this.instanceSegments = new ArrayList<InstanceSegment>();
		if(this.selectedTestStep.getMessage().getHl7EndcodedMessage() != null && !this.selectedTestStep.getMessage().getHl7EndcodedMessage().equals("")){
			this.manageInstanceService.loadMessageInstance(this.selectedTestStep.getMessage(), this.instanceSegments);
			this.selectedInstanceSegment = null;
		}
		this.activeIndexOfMessageInstancePanel = 3;
	}
	
	public void onInstanceSegmentSelect(SelectEvent event){
		this.segmentTreeRoot = new DefaultTreeNode("root", null);
		this.manageInstanceService.genSegmentTree(this.segmentTreeRoot, this.selectedInstanceSegment, this.selectedTestStep.getMessage());
		this.activeIndexOfMessageInstancePanel = 4;
	}
	
	public void genrateHL7Message() throws CloneNotSupportedException, IOException{
		this.selectedTestStep.getMessage().setHl7EndcodedMessage(this.manageInstanceService.generateHL7Message(this.messageTreeRoot));
		this.readHL7Message();
	}
	
	public void createTestStep() {
		this.setNewTestStep(new DataInstanceTestStep());
		this.setMessageId(null);
	}
	
	public void addTestStep() throws CloneNotSupportedException{
		Message m = this.sessionBeanTCAMT.getDbManager().getMessageById(this.messageId).clone();
		m.setAuthor(null);
		this.newTestStep.setMessage(m);
		this.selectedTestCase.addTestStep(this.newTestStep);
	}
	
	public void deleteTestStep(ActionEvent event) {
		this.selectedTestCase.getTeststeps().remove((DataInstanceTestStep)event.getComponent().getAttributes().get("teststep"));
		this.selectedTestStep = null;
	}
	
	public void selectTestStep(ActionEvent event) throws CloneNotSupportedException, IOException, ParserException {
		this.selectedTestStep = (DataInstanceTestStep)event.getComponent().getAttributes().get("teststep");
		this.selectedTestCaseGroup = null;
		
		this.selectedInstanceSegment = new InstanceSegment();
		this.messageTreeRoot = new DefaultTreeNode("root", null);
		this.segmentTreeRoot = new DefaultTreeNode("root", null);
		this.setConstraintTreeRoot(new DefaultTreeNode("root", null));
		this.instanceSegments = new ArrayList<InstanceSegment>();
		this.manageInstanceService = new ManageInstance();
		
		if(this.selectedTestStep.getMessage() != null){
			this.messageTreeRoot = this.manageInstanceService.loadMessage(this.selectedTestStep.getMessage());
			this.constraintTreeRoot = this.manageInstanceService.generateConstraintTree(this.selectedTestStep.getMessage());
			this.readHL7Message();	
		}
	}
	
	public void updateInstanceData(Object model) throws CloneNotSupportedException, IOException {
		int lineNum = this.instanceSegments.indexOf(this.selectedInstanceSegment);
		this.manageInstanceService.updateHL7Message(lineNum, this.manageInstanceService.generateLineStr(this.segmentTreeRoot), this.selectedTestStep.getMessage());
		this.readHL7Message();
		this.selectedInstanceSegment = this.instanceSegments.get(lineNum);
		this.activeIndexOfMessageInstancePanel = 4;
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
					this.generateProfileRB(out, dits.getMessage().getProfile(), teststepPath);
					this.generateValueSetRB(out, dits.getMessage().getValueSet(), teststepPath);
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
				this.generateProfileRB(out, dits.getMessage().getProfile(), teststepPath);
				this.generateValueSetRB(out, dits.getMessage().getValueSet(), teststepPath);
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
		org.w3c.dom.Document constraintDom = XMLManager.stringToDom(m.getConstraints());
		
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
	
	public void updateProfile(FileUploadEvent event) throws IOException, CloneNotSupportedException {
		this.selectedTestStep.getMessage().setProfile(IOUtils.toString(event.getFile().getInputstream(), "UTF-8"));
		this.readProfile(this.selectedTestStep.getMessage());
	}
	
	public void updateConstraints(FileUploadEvent event) throws IOException, CloneNotSupportedException {
		this.selectedTestStep.getMessage().setConstraints(IOUtils.toString(event.getFile().getInputstream(), "UTF-8"));
		this.readProfile(this.selectedTestStep.getMessage());
	}
	
	public void updateValueSet(FileUploadEvent event) throws IOException, CloneNotSupportedException {
		this.selectedTestStep.getMessage().setValueSet(IOUtils.toString(event.getFile().getInputstream(), "UTF-8"));
		this.readProfile(this.selectedTestStep.getMessage());
	}
	
	private void readProfile(Message m) throws CloneNotSupportedException{
		if(m.getProfile() != null && m.getProfile().equals("") ){
			if(m.getValueSet() != null && m.getValueSet().equals("") ){
				if(m.getConstraints() != null && m.getConstraints().equals("") ){
					this.manageInstanceService.loadMessage(m);
				}
			}
		}
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


	public DataInstanceTestStep getNewTestStep() {
		return newTestStep;
	}


	public void setNewTestStep(DataInstanceTestStep newTestStep) {
		this.newTestStep = newTestStep;
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
	
	

}
