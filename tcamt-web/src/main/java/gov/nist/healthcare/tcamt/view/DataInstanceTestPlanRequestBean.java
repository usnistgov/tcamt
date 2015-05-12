package gov.nist.healthcare.tcamt.view;

import gov.nist.healthcare.hl7tools.v2.maker.core.ConversionException;
import gov.nist.healthcare.tcamt.domain.DataInstanceTestCase;
import gov.nist.healthcare.tcamt.domain.DataInstanceTestCaseGroup;
import gov.nist.healthcare.tcamt.domain.DataInstanceTestPlan;
import gov.nist.healthcare.tcamt.domain.data.InstanceSegment;
import gov.nist.healthcare.tcamt.domain.data.SegmentTreeModel;
import gov.nist.healthcare.tcamt.service.ManageInstance;
import gov.nist.healthcare.tcamt.service.converter.DataInstanceTestCaseConverter;
import gov.nist.healthcare.tcamt.service.converter.DataInstanceTestPlanConverter;
import gov.nist.healthcare.tcamt.service.converter.JsonDataInstanceTestCaseConverter;
import gov.nist.healthcare.tcamt.service.converter.JsonDataInstanceTestPlanConverter;
import gov.nist.healthcare.tcamt.service.converter.JsonTestStoryConverter;
import gov.nist.healthcare.tcamt.service.converter.TestStoryConverter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;

import org.apache.commons.io.IOUtils;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;

import com.itextpdf.text.DocumentException;

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
	private DataInstanceTestCaseGroup selectedTestCaseGroup = null;
	private Long message_id = null; 
	private int activeIndex = 0;
	private int activeIndexOfMessageInstancePanel = 0;
	
	private TreeNode testplanRoot;
    private TreeNode selectedNode;
    
    private TreeNode segmentTreeRoot;
	private TreeNode messageTreeRoot;
    
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
		this.selectedTestCaseGroup = null;
		this.message_id = null;
		
		this.selectedInstanceSegment = null;
		this.segmentTreeRoot = new DefaultTreeNode("root", null);
		this.messageTreeRoot = new DefaultTreeNode("root", null);
		this.instanceSegments = new ArrayList<InstanceSegment>();
		this.manageInstanceService = new ManageInstance();
		
		this.activeIndex = 0;
		this.setActiveIndexOfMessageInstancePanel(2);
	}
	
	
	public void createTestPlan() {
		init();
		this.selectedTestPlan.setName("New TestPlan");
		
		this.createTestPlanTree(this.selectedTestPlan);
		
		this.setActiveIndexOfMessageInstancePanel(2);
		this.activeIndex = 1;
	}
	
	public void deleteTestPlan(ActionEvent event) {
		this.sessionBeanTCAMT.getDbManager().dataInstanceTestPlanDelete((DataInstanceTestPlan) event.getComponent().getAttributes().get("testplan"));
		this.sessionBeanTCAMT.updateDataInstanceTestPlans();
	}
	
	public void selectTestPlan(ActionEvent event) {
		this.selectedTestPlan = (DataInstanceTestPlan) event.getComponent().getAttributes().get("testplan");
		this.selectedTestCase = null;
		this.selectedTestCaseGroup = null;
		this.createTestPlanTree(this.selectedTestPlan);
		this.activeIndex = 1;
	}
	
	public void messageSelected() throws CloneNotSupportedException, IOException{
		this.selectedTestCase.setMessage(this.sessionBeanTCAMT.getDbManager().getMessageById(this.message_id));
		this.messageTreeRoot = this.manageInstanceService.loadMessage(this.selectedTestCase.getMessage());
		this.readHL7Message();	
	}
	
	private void createTestPlanTree(DataInstanceTestPlan tp) {
		this.testplanRoot = new DefaultTreeNode("", null);
		TreeNode testplanNode = new DefaultTreeNode("plan", tp, this.testplanRoot);
		for(DataInstanceTestCaseGroup ditcg:tp.getTestcasegroups()){
			TreeNode groupNode = new DefaultTreeNode("group", ditcg, testplanNode);
			for(DataInstanceTestCase ditc:ditcg.getTestcases()){
				new DefaultTreeNode("case", ditc, groupNode);
			}
		}
		for(DataInstanceTestCase ditc:tp.getTestcases()){
			new DefaultTreeNode("case", ditc, testplanNode);
		}
	}
	
	public void createValidationContext(SegmentTreeModel stm) {
//		this.manageInstanceService.createVC(stm, this.selectedTestCase.getMessage(), "TestCase");
	}

	public void saveTestPlan() {
		if(this.selectedTestPlan.getId() <= 0){
			this.sessionBeanTCAMT.getDbManager().dataInstanceTestPlanInsert(this.selectedTestPlan);
			this.sessionBeanTCAMT.updateDataInstanceTestPlans();
		}else{
			this.sessionBeanTCAMT.getDbManager().dataInstanceTestPlanUpdate(this.selectedTestPlan);
			this.sessionBeanTCAMT.updateDataInstanceTestPlans();
		}
		
		init();
	}
	
	public void addTestCaseGroup() {
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
	}
	
	public void addTestCase() {
		DataInstanceTestCase newDataInstacneTestCase = new DataInstanceTestCase();
		newDataInstacneTestCase.setVersion(1);
		newDataInstacneTestCase.setName("New TestCase");
		
		if(this.selectedTestCaseGroup == null){
			this.selectedTestPlan.addTestCase(newDataInstacneTestCase);	
		}else{
			this.selectedTestCaseGroup.addTestCase(newDataInstacneTestCase);
		}
		
		this.selectedNode.setSelected(false);
		this.selectedNode.setExpanded(true);
		this.selectedNode = new DefaultTreeNode("case", newDataInstacneTestCase, this.selectedNode);
		this.selectedNode.setSelected(true);
		this.selectedTestCase = newDataInstacneTestCase;
		this.selectedTestCaseGroup = null;
	}
	
	public void onNodeSelect(NodeSelectEvent event) throws CloneNotSupportedException, IOException {
		if(event.getTreeNode().getData() instanceof DataInstanceTestPlan){
			this.selectedTestCase = null;
			this.selectedTestCaseGroup = null;
		}else if(event.getTreeNode().getData() instanceof DataInstanceTestCase){
			this.selectedTestCase = (DataInstanceTestCase)event.getTreeNode().getData();
			this.selectedTestCaseGroup = null;
			
			this.selectedInstanceSegment = new InstanceSegment();
			this.messageTreeRoot = new DefaultTreeNode("root", null);
			this.segmentTreeRoot = new DefaultTreeNode("root", null);
			this.instanceSegments = new ArrayList<InstanceSegment>();
			this.manageInstanceService = new ManageInstance();
			
			if(this.selectedTestCase.getMessage() != null){
				this.messageTreeRoot = this.manageInstanceService.loadMessage(this.selectedTestCase.getMessage());
				this.readHL7Message();	
			}
			
		}else if(event.getTreeNode().getData() instanceof DataInstanceTestCaseGroup){
			this.selectedTestCase = null;
			this.selectedTestCaseGroup = (DataInstanceTestCaseGroup)event.getTreeNode().getData();
		}
		this.message_id = null;
    }
	
	public void deleteTestCase(ActionEvent event) {
		TreeNode parentNode = this.selectedNode.getParent();
		this.selectedTestPlan.getTestcases().remove(this.selectedNode.getData());
		this.selectedTestCase = null;
		
		selectedNode.getChildren().clear();
        selectedNode.getParent().getChildren().remove(selectedNode);
		
		if(parentNode.getData() instanceof DataInstanceTestPlan){
			this.selectedTestCase = null;
			this.selectedTestCaseGroup = null;
		}else if(parentNode.getData() instanceof DataInstanceTestCase){
			this.selectedTestCase = (DataInstanceTestCase)parentNode.getData();
			this.selectedTestCaseGroup = null;
		}else if(parentNode.getData() instanceof DataInstanceTestCaseGroup){
			this.selectedTestCase = null;
			this.selectedTestCaseGroup = (DataInstanceTestCaseGroup)parentNode.getData();
		}
		this.selectedNode = parentNode;
		this.selectedNode.setSelected(true);
	}
	
	public void deleteTestCaseGroup(ActionEvent event) {
		TreeNode parentNode = this.selectedNode.getParent();
		this.selectedTestPlan.getTestcasegroups().remove(this.selectedNode.getData());
		this.selectedTestCaseGroup = null;
		
		selectedNode.getChildren().clear();
        selectedNode.getParent().getChildren().remove(selectedNode);
        
		if(parentNode.getData() instanceof DataInstanceTestPlan){
			this.selectedTestCase = null;
			this.selectedTestCaseGroup = null;
		}else if(parentNode.getData() instanceof DataInstanceTestCase){
			this.selectedTestCase = (DataInstanceTestCase)parentNode.getData();
			this.selectedTestCaseGroup = null;
		}else if(parentNode.getData() instanceof DataInstanceTestCaseGroup){
			this.selectedTestCase = null;
			this.selectedTestCaseGroup = (DataInstanceTestCaseGroup)parentNode.getData();
		}
		this.selectedNode = parentNode;
		this.selectedNode.setSelected(true);
		
	}
	
	public void readHL7Message() throws CloneNotSupportedException, IOException{
		this.instanceSegments = new ArrayList<InstanceSegment>();
		if(this.selectedTestCase.getMessage().getHl7EndcodedMessage() != null && !this.selectedTestCase.getMessage().getHl7EndcodedMessage().equals("")){
			this.manageInstanceService.loadMessageInstance(this.selectedTestCase.getMessage(), this.instanceSegments);
			this.selectedInstanceSegment = null;
		}
		this.activeIndexOfMessageInstancePanel = 2;
	}
	
	public void onInstanceSegmentSelect(SelectEvent event){
		this.segmentTreeRoot = new DefaultTreeNode("root", null);
		this.manageInstanceService.genSegmentTree(this.segmentTreeRoot, this.selectedInstanceSegment);
		this.activeIndexOfMessageInstancePanel = 3;
	}
	
	public void genrateHL7Message() throws CloneNotSupportedException, IOException{
		this.selectedTestCase.getMessage().setHl7EndcodedMessage(this.manageInstanceService.generateHL7Message(this.messageTreeRoot));
		this.readHL7Message();
	}
	
	public void updateInstanceData(Object model) throws CloneNotSupportedException, IOException {
		int lineNum = this.instanceSegments.indexOf(this.selectedInstanceSegment);
		this.manageInstanceService.updateHL7Message(lineNum, this.manageInstanceService.generateLineStr(this.segmentTreeRoot), this.selectedTestCase.getMessage());
		this.readHL7Message();
		this.selectedInstanceSegment = this.instanceSegments.get(lineNum);
		this.activeIndexOfMessageInstancePanel = 3;
	}
	
	public void downloadResourceBundleForTestPlan(DataInstanceTestPlan tp) throws IOException, DocumentException, ConversionException, CloneNotSupportedException{
		this.setTestStoryConverter(new JsonTestStoryConverter());
		this.setTpConverter(new JsonDataInstanceTestPlanConverter());
		this.setTcConverter(new JsonDataInstanceTestCaseConverter());
		
		
		String outFilename = "ResourceBundle_TestPlan_" + tp.getName() + ".zip";
		ByteArrayOutputStream outputStream = null;
		byte[] bytes;
		outputStream = new ByteArrayOutputStream();
		ZipOutputStream out = new ZipOutputStream(outputStream);
		
		
		String testPlanPath = tp.getName();
		this.generateTestPlanJsonRB(out, tp, testPlanPath);
		
		
//		for(Integer key:tp.getTestCases()){
//			TestCase tc = this.dbManager.getTestCaseById(key);
//			String testCasePath = testPlanPath + File.separator + tc.getName();
//			this.generateTestStoryRB(out, tc.getTestCaseStory(), testCasePath);
//			this.generateTestCaseJsonRB(out, tc, testCasePath);
//			
//			int tsNum = 0;
//			for(Integer id: tc.getTestSteps()){
//				TestStep ts = this.dbManager.getTestStepById(id);
//				String testStepPath = testCasePath + File.separator + "["+ (tsNum + 1) + "]" + tc.getTransactions().get(tsNum) + "_" +ts.getName() ;
//				this.generateTestStoryRB(out, ts.getTestStepStory(), testStepPath);
//				this.generateTestStepJsonRB(out, ts, testStepPath);
//				
//				if(!ts.getType().equals("Manual")){
//					MessageProfile mp = this.manageInstanceService.genMessageProfile(ts.getInteraction().getMessage().getMessageProfile());
//					this.generateDataSheetRB(out, ts, testStepPath);
//					this.generateMessageRB(out, ts.getInteraction().getMessage().getHl7EndcodedMessage(), testStepPath);
//					this.generateValidationContextRB(out, ts, testStepPath);
//					this.generateTableLibraryRB(out, mp, testStepPath);
//					this.generateProfileRB(out, mp, testStepPath);
//				}
//				
//	        	tsNum = tsNum + 1;
//			}
//			
//		}	
		out.close();
		bytes = outputStream.toByteArray();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
		this.setZipResourceBundleFile(new DefaultStreamedContent(inputStream, "application/zip", outFilename));
	}
	
	private void generateTestPlanJsonRB(ZipOutputStream out, DataInstanceTestPlan tp, String path) throws IOException, ConversionException {
		byte[] buf = new byte[1024];
		out.putNextEntry(new ZipEntry(path + File.separator + "TestPlan.json"));
		InputStream inTP = IOUtils.toInputStream(this.tpConverter.toString(tp));
		int lenTP;
        while ((lenTP = inTP.read(buf)) > 0) {
            out.write(buf, 0, lenTP);
        }
        out.closeEntry();
        inTP.close();
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


	public int getActiveIndex() {
		return activeIndex;
	}


	public void setActiveIndex(int activeIndex) {
		this.activeIndex = activeIndex;
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


	public Long getMessage_id() {
		return message_id;
	}


	public void setMessage_id(Long message_id) {
		this.message_id = message_id;
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
	
	

}
