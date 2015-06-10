package gov.nist.healthcare.tcamt.view;

import gov.nist.healthcare.core.hl7.v2.parser.ParserException;
import gov.nist.healthcare.hl7tools.v2.maker.core.ConversionException;
import gov.nist.healthcare.tcamt.domain.DataInstanceTestPlan;
import gov.nist.healthcare.tcamt.domain.IsolatedTestCase;
import gov.nist.healthcare.tcamt.domain.IsolatedTestCaseGroup;
import gov.nist.healthcare.tcamt.domain.IsolatedTestPlan;
import gov.nist.healthcare.tcamt.domain.IsolatedTestStep;
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
import gov.nist.healthcare.tcamt.service.converter.IsolatedTestPlanConverter;
import gov.nist.healthcare.tcamt.service.converter.JsonIsolatedTestPlanConverter;
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
public class IsolatedTestPlanRequestBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6592759972860009656L;

	@ManagedProperty("#{sessionBeanTCAMT}")
	private SessionBeanTCAMT sessionBeanTCAMT;
	private IsolatedTestPlan selectedTestPlan = new IsolatedTestPlan();
	private IsolatedTestCase selectedTestCase = null;
	private IsolatedTestCaseGroup selectedTestCaseGroup = null;
	private IsolatedTestStep selectedTestStep = null;
	private int activeIndexOfMessageInstancePanel = 0;
	
	
	private TreeNode testplanRoot = new DefaultTreeNode("root", null);
    private TreeNode selectedNode = null;
  
    private TreeNode segmentTreeRoot = new DefaultTreeNode("root", null);
	private TreeNode messageTreeRoot = new DefaultTreeNode("root", null);
	private TreeNode constraintTreeRoot = new DefaultTreeNode("root", null);
    
    private InstanceSegment selectedInstanceSegment= null;
	private List<InstanceSegment> instanceSegments = new ArrayList<InstanceSegment>();
	private ManageInstance manageInstanceService  = new ManageInstance();
	
	private transient StreamedContent zipResourceBundleFile;
	
	private IsolatedTestStep newTestStep = new IsolatedTestStep();
	private Long sActorId = null;
	private Long rActorId = null;
	private Long messageId = null;
	private Long shareTo = null;
	
	private TestStoryConverter testStoryConverter;
	private IsolatedTestPlanConverter tpConverter;
	
	private String usageViewOption ="partial";
	private String usageViewOption2 ="partial";
	private List<InstanceSegment> filteredInstanceSegments = new ArrayList<InstanceSegment>();
	private TreeNode filtedSegmentTreeRoot = new DefaultTreeNode("root", null);
	
	
	private void init(){
		this.selectedTestPlan = new IsolatedTestPlan();
		this.selectedTestPlan.setVersion(1);
		this.selectedTestPlan.setAuthor(this.sessionBeanTCAMT.getLoggedUser());
		this.selectedTestCase = null;
		this.selectedTestStep = null;
		this.selectedTestCaseGroup = null;
		
		this.selectedInstanceSegment = null;
		this.segmentTreeRoot = new DefaultTreeNode("root", null);
		this.messageTreeRoot = new DefaultTreeNode("root", null);
		this.setConstraintTreeRoot(new DefaultTreeNode("root", null));
		this.instanceSegments = new ArrayList<InstanceSegment>();
		this.manageInstanceService = new ManageInstance();
		
		setNewTestStep(new IsolatedTestStep());
		setsActorId(null);
		setrActorId(null);
		setMessageId(null);
		this.shareTo = null;
		this.selectedNode = null;
		
		
		this.sessionBeanTCAMT.setItActiveIndex(0);
		this.setActiveIndexOfMessageInstancePanel(2);
		
		this.usageViewOption = "partial";
		this.usageViewOption2 = "partial";
		this.filteredInstanceSegments = new ArrayList<InstanceSegment>();
		this.filtedSegmentTreeRoot = new DefaultTreeNode("root", null);
		
	}
	


	public void shareInit(ActionEvent event){
		this.shareTo = null;
		this.selectedTestPlan = (IsolatedTestPlan) event.getComponent().getAttributes().get("testplan");
	}
	
	public void createTestPlan() {
		init();
		this.selectedTestPlan.setName("New TestPlan");
		this.createTestPlanTree(this.selectedTestPlan);
		this.sessionBeanTCAMT.setItActiveIndex(1);
	}
	
	public void deleteTestPlan(ActionEvent event) {
		String deletedTestPlanName = ((IsolatedTestPlan) event.getComponent().getAttributes().get("testplan")).getName();
		this.sessionBeanTCAMT.getDbManager().isolatedTestPlanDelete((IsolatedTestPlan) event.getComponent().getAttributes().get("testplan"));
		this.sessionBeanTCAMT.updateIsolatedTestPlans();
		
		FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage("TestPlan Deleted.",  "TestPlan: " + deletedTestPlanName + " has been created.") );
	}
	
	public void shareTestPlan() throws CloneNotSupportedException{
		this.selectedTestPlan.setName("Copyed_" + selectedTestPlan.getName());
		this.selectedTestPlan.setAuthor(this.sessionBeanTCAMT.getDbManager().getUserById(this.shareTo));
		this.sessionBeanTCAMT.getDbManager().isolatedTestPlanInsert(selectedTestPlan.clone());
		this.sessionBeanTCAMT.updateIsolatedTestPlans();

		FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage("TestPlan sharing.",  "TestPlan: " + this.selectedTestPlan.getName() + " has been sented to " + this.selectedTestPlan.getAuthor().getUserId()) );
        
        this.init();
        
        this.sessionBeanTCAMT.setDitActiveIndex(0);
	}
	
	public void cloneTestPlan(ActionEvent event) throws CloneNotSupportedException {
		IsolatedTestPlan testplan = (IsolatedTestPlan) event.getComponent().getAttributes().get("testplan");
		testplan.setName("Copyed_" + testplan.getName());
		this.sessionBeanTCAMT.getDbManager().isolatedTestPlanInsert(testplan.clone());
		this.sessionBeanTCAMT.updateIsolatedTestPlans();
		
		this.selectedTestPlan = testplan;
		this.selectedTestCase = null;
		this.selectedTestCaseGroup = null;
		this.selectedTestStep = null;
		this.createTestPlanTree(this.selectedTestPlan);
		this.sessionBeanTCAMT.setDitActiveIndex(1);
		
		FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage("Message Cloned.",  "TestPlan: " + this.selectedTestPlan.getName() + " has been created.") );
	}
	
	public void selectTestPlan(ActionEvent event) {
		this.selectedTestPlan = (IsolatedTestPlan) event.getComponent().getAttributes().get("testplan");
		this.selectedTestPlan = this.sessionBeanTCAMT.getDbManager().getIsolatedTestPlanById(this.selectedTestPlan.getId());
		this.selectedTestCase = null;
		this.selectedTestStep = null;
		this.selectedTestCaseGroup = null;
		this.createTestPlanTree(this.selectedTestPlan);
		this.sessionBeanTCAMT.setItActiveIndex(1);
	}
	
	
	private void createTestPlanTree(IsolatedTestPlan tp) {
		this.testplanRoot = new DefaultTreeNode("", null);
		TreeNode testplanNode = new DefaultTreeNode("plan", tp, this.testplanRoot);
		testplanNode.setExpanded(true);
		for(IsolatedTestCaseGroup itcg:tp.getTestcasegroups()){
			TreeNode groupNode = new DefaultTreeNode("group", itcg, testplanNode);
			groupNode.setExpanded(true);
			for(IsolatedTestCase itc:itcg.getTestcases()){
				new DefaultTreeNode("case", itc, groupNode);
			}
		}
		for(IsolatedTestCase itc:tp.getTestcases()){
			new DefaultTreeNode("case", itc, testplanNode);
		}
	}
	

	public void saveTestPlan() {
		if(this.selectedTestPlan.getId() <= 0){
			this.selectedTestPlan.setVersion(1);
			this.sessionBeanTCAMT.getDbManager().isolatedTestPlanInsert(this.selectedTestPlan);
			this.sessionBeanTCAMT.updateIsolatedTestPlans();
		}else{
			this.selectedTestPlan.setVersion(this.selectedTestPlan.getVersion() + 1);
			this.sessionBeanTCAMT.getDbManager().isolatedTestPlanUpdate(this.selectedTestPlan);
			this.sessionBeanTCAMT.updateIsolatedTestPlans();
		}
		
		FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage("TestPlan Saved.",  "TestPlan: " + this.selectedTestPlan.getName() + " has been saved.") );
	}
	
	public void addTestCaseGroup() {
		if(this.selectedNode != null){
			IsolatedTestCaseGroup newIsolatedTestCaseGroup = new IsolatedTestCaseGroup();
			newIsolatedTestCaseGroup.setVersion(1);
			newIsolatedTestCaseGroup.setName("New TestCaseGroup");
			this.selectedTestPlan.addTestCaseGroup(newIsolatedTestCaseGroup);
			
			this.selectedNode.setSelected(false);
			this.selectedNode.setExpanded(true);
			this.selectedNode = new DefaultTreeNode("group", newIsolatedTestCaseGroup, this.selectedNode);
			this.selectedNode.setSelected(true);
			
			this.selectedTestCaseGroup = newIsolatedTestCaseGroup;
			this.selectedTestCase = null;
			this.selectedTestStep = null;	
		}
	}
	
	public void addTestCase() {
		if(this.selectedNode != null){
			IsolatedTestCase newIsolatedTestCase = new IsolatedTestCase();
			newIsolatedTestCase.setVersion(1);
			newIsolatedTestCase.setName("New TestCase");
			
			if(this.selectedNode.getData() instanceof IsolatedTestPlan){
				((IsolatedTestPlan)this.selectedNode.getData()).addTestCase(newIsolatedTestCase);	
			}else if(this.selectedNode.getData() instanceof IsolatedTestCaseGroup){
				((IsolatedTestCaseGroup)this.selectedNode.getData()).addTestCase(newIsolatedTestCase);
			}
			
			
			this.selectedNode.setSelected(false);
			this.selectedNode.setExpanded(true);
			this.selectedNode = new DefaultTreeNode("case", newIsolatedTestCase, this.selectedNode);
			this.selectedNode.setSelected(true);
			this.selectedTestCase = newIsolatedTestCase;
			this.selectedTestCaseGroup = null;
			this.selectedTestStep = null;	
		}
	}
	
	public void onNodeSelect(NodeSelectEvent event) throws CloneNotSupportedException, IOException, ParserException {
		if(event.getTreeNode().getData() instanceof IsolatedTestPlan){
			this.selectedTestCase = null;
			this.selectedTestCaseGroup = null;
			this.selectedTestStep = null;
		}else if(event.getTreeNode().getData() instanceof IsolatedTestCase){
			this.selectedTestCase = (IsolatedTestCase)event.getTreeNode().getData();
			this.selectedTestCaseGroup = null;
			this.selectedTestStep = null;	
			
			this.messageTreeRoot = new DefaultTreeNode("root", null);
			this.setConstraintTreeRoot(new DefaultTreeNode("root", null));
			this.segmentTreeRoot = new DefaultTreeNode("root", null);
		}else if(event.getTreeNode().getData() instanceof IsolatedTestCaseGroup){
			this.selectedTestCase = null;
			this.selectedTestCaseGroup = (IsolatedTestCaseGroup)event.getTreeNode().getData();
			this.selectedTestStep = null;
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
			}else if(parentNode.getData() instanceof IsolatedTestCase){
				this.selectedTestCase = (IsolatedTestCase)parentNode.getData();
				this.selectedTestCaseGroup = null;
				this.selectedTestStep = null;
			}else if(parentNode.getData() instanceof IsolatedTestCaseGroup){
				this.selectedTestCase = null;
				this.selectedTestCaseGroup = (IsolatedTestCaseGroup)parentNode.getData();
				this.selectedTestStep = null;
			}
			this.selectedNode = parentNode;
			this.selectedNode.setSelected(true);	
		}
	}
	
	public void deleteTestCaseGroup(ActionEvent event) {
		if(this.selectedNode != null){
			TreeNode parentNode = this.selectedNode.getParent();
			this.selectedTestPlan.getTestcasegroups().remove(this.selectedNode.getData());
			this.selectedTestCaseGroup = null;
			
			selectedNode.getChildren().clear();
	        selectedNode.getParent().getChildren().remove(selectedNode);
	        
			if(parentNode.getData() instanceof IsolatedTestPlan){
				this.selectedTestCase = null;
				this.selectedTestCaseGroup = null;
				this.selectedTestStep = null;
			}else if(parentNode.getData() instanceof IsolatedTestCase){
				this.selectedTestCase = (IsolatedTestCase)parentNode.getData();
				this.selectedTestCaseGroup = null;
				this.selectedTestStep = null;
			}else if(parentNode.getData() instanceof IsolatedTestCaseGroup){
				this.selectedTestCase = null;
				this.selectedTestCaseGroup = (IsolatedTestCaseGroup)parentNode.getData();
				this.selectedTestStep = null;
			}
			this.selectedNode = parentNode;
			this.selectedNode.setSelected(true);	
		}
	}
	
	public void selectTestCase(ActionEvent event){
		this.selectedTestCase = (IsolatedTestCase) event.getComponent().getAttributes().get("testcase");	
	}
	
	public void createTestStep() {
		this.setNewTestStep(new IsolatedTestStep());
		this.setsActorId(null);
		this.setrActorId(null);
		this.setMessageId(null);
	}
	
	public void addTestStep(){
		this.newTestStep.setrActor(this.sessionBeanTCAMT.getDbManager().getActorById(this.rActorId));
		this.newTestStep.setsActor(this.sessionBeanTCAMT.getDbManager().getActorById(this.sActorId));
		Message m = this.sessionBeanTCAMT.getDbManager().getMessageById(this.messageId);
		m.setId(0);
		m.setAuthor(null);
		
		this.newTestStep.setMessage(m);
		
		this.selectedTestCase.addTestStep(this.newTestStep);
	}
	
	public void deleteTestStep(ActionEvent event) {
		this.selectedTestCase.getTeststeps().remove((IsolatedTestStep)event.getComponent().getAttributes().get("teststep"));
		this.selectedTestStep = null;
	}
	
	public void selectTestStep(ActionEvent event) throws CloneNotSupportedException, IOException, ParserException {
		this.selectedTestStep = (IsolatedTestStep)event.getComponent().getAttributes().get("teststep");
		this.selectedTestCaseGroup = null;
		
		this.selectedInstanceSegment = new InstanceSegment();
		this.messageTreeRoot = new DefaultTreeNode("root", null);
		this.setConstraintTreeRoot(new DefaultTreeNode("root", null));
		this.segmentTreeRoot = new DefaultTreeNode("root", null);
		this.instanceSegments = new ArrayList<InstanceSegment>();
		this.manageInstanceService = new ManageInstance();
		
		if(this.selectedTestStep.getMessage() != null){
			this.messageTreeRoot = this.manageInstanceService.loadMessage(this.selectedTestStep.getMessage());
			this.constraintTreeRoot = this.manageInstanceService.generateConstraintTree(this.selectedTestStep.getMessage());
			this.readHL7Message();	
		}
	}
	
	public void readHL7Message() throws CloneNotSupportedException, IOException{
		this.instanceSegments = new ArrayList<InstanceSegment>();
		if(this.selectedTestStep.getMessage().getHl7EndcodedMessage() != null && !this.selectedTestStep.getMessage().getHl7EndcodedMessage().equals("")){
			this.manageInstanceService.loadMessageInstance(this.selectedTestStep.getMessage(), this.instanceSegments);
			this.selectedInstanceSegment = null;
		}
		this.activeIndexOfMessageInstancePanel = 3;
		this.updateFilteredInstanceSegments();
	}
	
	public void onInstanceSegmentSelect(SelectEvent event){
		this.segmentTreeRoot = new DefaultTreeNode("root", null);
		this.manageInstanceService.genSegmentTree(this.segmentTreeRoot, this.selectedInstanceSegment, this.selectedTestStep.getMessage());
		this.activeIndexOfMessageInstancePanel = 4;
		this.updateFilteredSegmentTree();
	}
	
	public void genrateHL7Message() throws CloneNotSupportedException, IOException{
		this.selectedTestStep.getMessage().setHl7EndcodedMessage(this.manageInstanceService.generateHL7Message(this.messageTreeRoot,  this.selectedTestStep.getMessage()));
		this.readHL7Message();
	}
	
	public void updateInstanceData(Object model) throws CloneNotSupportedException, IOException {
		int lineNum = this.instanceSegments.indexOf(this.selectedInstanceSegment);
		this.manageInstanceService.updateHL7Message(lineNum, this.manageInstanceService.generateLineStr(this.segmentTreeRoot), this.selectedTestStep.getMessage());
		this.readHL7Message();
		this.selectedInstanceSegment = this.instanceSegments.get(lineNum);
		this.activeIndexOfMessageInstancePanel = 4;
	}
	
	public void createTCAMTConstraint(Object model) {
		String ipath = null;
		String data = null;
		String level = "IsolatedTestCase";
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
	
	public void downloadResourceBundleForTestPlan(IsolatedTestPlan tp) throws Exception{
		this.setTestStoryConverter(new JsonTestStoryConverter());
		this.setTpConverter(new JsonIsolatedTestPlanConverter());
		
		
		String outFilename = "TestPlan_" + tp.getName() + ".zip";
		ByteArrayOutputStream outputStream = null;
		byte[] bytes;
		outputStream = new ByteArrayOutputStream();
		ZipOutputStream out = new ZipOutputStream(outputStream);
		
		this.generateTestPlanJsonRB(out, tp);
		
		for(IsolatedTestCaseGroup itg:tp.getTestcasegroups()){
			String groupPath = itg.getName();
			for(IsolatedTestCase itc:itg.getTestcases()){
				String testcasePath = groupPath + File.separator + itc.getName();
				this.generateTestStoryRB(out, itc.getTestCaseStory(), testcasePath);
				for(IsolatedTestStep its:itc.getTeststeps()){
					String teststepPath = testcasePath + File.separator + its.getName();
					this.generateMessageRB(out, its.getMessage().getHl7EndcodedMessage(), teststepPath);
					this.generateProfileRB(out, its.getMessage().getProfile(), teststepPath);
					this.generateValueSetRB(out, its.getMessage().getValueSet(), teststepPath);
					this.generateConstraintRB(out, its.getMessage(), teststepPath);
					this.generateTestStoryRB(out, its.getTestStepStory(), teststepPath);
				}
			}
		}
		
		for(IsolatedTestCase itc:tp.getTestcases()){
			String testcasePath = itc.getName();
			this.generateTestStoryRB(out, itc.getTestCaseStory(), testcasePath);
			for(IsolatedTestStep its:itc.getTeststeps()){
				String teststepPath = testcasePath + File.separator + its.getName();
				this.generateMessageRB(out, its.getMessage().getHl7EndcodedMessage(), teststepPath);
				this.generateProfileRB(out, its.getMessage().getProfile(), teststepPath);
				this.generateValueSetRB(out, its.getMessage().getValueSet(), teststepPath);
				this.generateConstraintRB(out, its.getMessage(), teststepPath);
				this.generateTestStoryRB(out, its.getTestStepStory(), teststepPath);
			}
		}
		out.close();
		bytes = outputStream.toByteArray();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
		this.setZipResourceBundleFile(new DefaultStreamedContent(inputStream, "application/zip", outFilename));
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
	
	private void generateTestPlanJsonRB(ZipOutputStream out, IsolatedTestPlan tp) throws IOException, ConversionException {
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
	
	public void cloneTestCaseGroup(ActionEvent event) throws CloneNotSupportedException {
		if(this.selectedNode != null){
			IsolatedTestCaseGroup group = ((IsolatedTestCaseGroup)this.selectedNode.getData()).clone();
			group.setName("Copyed_" + group.getName());
			this.selectedNode.setSelected(false);
			this.selectedNode = new DefaultTreeNode("group", group, this.selectedNode.getParent());
			
			this.selectedTestCaseGroup = group;
			this.selectedTestCase = null;
			this.selectedTestStep = null;
			
			for(IsolatedTestCase testcase:group.getTestcases()){
				testcase.setName("Copyed_" + testcase.getName());
				new DefaultTreeNode("case", testcase, this.selectedNode);
			}
			
			this.selectedNode.setExpanded(true);
			this.selectedTestPlan.addTestCaseGroup(group);
			
		}
	}
	
	public void cloneTestCase(ActionEvent event) throws CloneNotSupportedException {
		if(this.selectedNode != null){
			IsolatedTestCase testcase = ((IsolatedTestCase)this.selectedNode.getData()).clone();
			testcase.setName("Copyed_" + testcase.getName());
			this.selectedNode.setSelected(false);
			this.selectedNode = new DefaultTreeNode("case", testcase, this.selectedNode.getParent());
			
			this.selectedTestCase = testcase;
			this.selectedTestCaseGroup = null;
			this.selectedTestStep = null;
			
			if(this.selectedNode.getParent().getData() instanceof IsolatedTestCaseGroup){
				IsolatedTestCaseGroup group = (IsolatedTestCaseGroup)this.selectedNode.getParent().getData();
				group.addTestCase(testcase);
			}else if(this.selectedNode.getParent().getData() instanceof IsolatedTestPlan){
				IsolatedTestPlan plan = (IsolatedTestPlan)this.selectedNode.getParent().getData();
				plan.addTestCase(testcase);
			}
		}
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

	public List<IsolatedTestPlan> getTestPlans() {
		return this.sessionBeanTCAMT.getIsolatedTestPlans();
	}

	public SessionBeanTCAMT getSessionBeanTCAMT() {
		return sessionBeanTCAMT;
	}


	public void setSessionBeanTCAMT(SessionBeanTCAMT sessionBeanTCAMT) {
		this.sessionBeanTCAMT = sessionBeanTCAMT;
	}


	public IsolatedTestPlan getSelectedTestPlan() {
		return selectedTestPlan;
	}


	public void setSelectedTestPlan(IsolatedTestPlan selectedTestPlan) {
		this.selectedTestPlan = selectedTestPlan;
	}


	public IsolatedTestCase getSelectedTestCase() {
		return selectedTestCase;
	}


	public void setSelectedTestCase(IsolatedTestCase selectedTestCase) {
		this.selectedTestCase = selectedTestCase;
	}


	public IsolatedTestCaseGroup getSelectedTestCaseGroup() {
		return selectedTestCaseGroup;
	}


	public void setSelectedTestCaseGroup(IsolatedTestCaseGroup selectedTestCaseGroup) {
		this.selectedTestCaseGroup = selectedTestCaseGroup;
	}


	public IsolatedTestStep getSelectedTestStep() {
		return selectedTestStep;
	}


	public void setSelectedTestStep(IsolatedTestStep selectedTestStep) {
		this.selectedTestStep = selectedTestStep;
	}

	public TreeNode getTestplanRoot() {
		return testplanRoot;
	}


	public void setTestplanRoot(TreeNode testplanRoot) {
		this.testplanRoot = testplanRoot;
	}


	public TreeNode getSelectedNode() {
		return selectedNode;
	}


	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
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


	public IsolatedTestStep getNewTestStep() {
		return newTestStep;
	}


	public void setNewTestStep(IsolatedTestStep newTestStep) {
		this.newTestStep = newTestStep;
	}


	public Long getsActorId() {
		return sActorId;
	}


	public void setsActorId(Long sActorId) {
		this.sActorId = sActorId;
	}


	public Long getrActorId() {
		return rActorId;
	}


	public void setrActorId(Long rActorId) {
		this.rActorId = rActorId;
	}


	public Long getMessageId() {
		return messageId;
	}


	public void setMessageId(Long messageId) {
		this.messageId = messageId;
	}


	public int getActiveIndexOfMessageInstancePanel() {
		return activeIndexOfMessageInstancePanel;
	}


	public void setActiveIndexOfMessageInstancePanel(
			int activeIndexOfMessageInstancePanel) {
		this.activeIndexOfMessageInstancePanel = activeIndexOfMessageInstancePanel;
	}


	public TreeNode getSegmentTreeRoot() {
		return segmentTreeRoot;
	}


	public void setSegmentTreeRoot(TreeNode segmentTreeRoot) {
		this.segmentTreeRoot = segmentTreeRoot;
	}


	public TestStoryConverter getTestStoryConverter() {
		return testStoryConverter;
	}


	public void setTestStoryConverter(TestStoryConverter testStoryConverter) {
		this.testStoryConverter = testStoryConverter;
	}


	public IsolatedTestPlanConverter getTpConverter() {
		return tpConverter;
	}


	public void setTpConverter(IsolatedTestPlanConverter tpConverter) {
		this.tpConverter = tpConverter;
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
	
	
	
}
