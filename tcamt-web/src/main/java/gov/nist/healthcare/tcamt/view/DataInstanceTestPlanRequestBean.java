package gov.nist.healthcare.tcamt.view;

import gov.nist.healthcare.hl7tools.v2.maker.core.ConversionException;
import gov.nist.healthcare.tcamt.domain.DataInstanceTestCase;
import gov.nist.healthcare.tcamt.domain.DataInstanceTestCaseGroup;
import gov.nist.healthcare.tcamt.domain.DataInstanceTestPlan;
import gov.nist.healthcare.tcamt.domain.DataInstanceTestStep;
import gov.nist.healthcare.tcamt.domain.DefaultTestDataCategorization;
import gov.nist.healthcare.tcamt.domain.DefaultTestDataCategorizationSheet;
import gov.nist.healthcare.tcamt.domain.JurorDocument;
import gov.nist.healthcare.tcamt.domain.Log;
import gov.nist.healthcare.tcamt.domain.ManualTestStep;
import gov.nist.healthcare.tcamt.domain.Message;
import gov.nist.healthcare.tcamt.domain.Metadata;
import gov.nist.healthcare.tcamt.domain.ProfileContainer;
import gov.nist.healthcare.tcamt.domain.TCAMTConstraint;
import gov.nist.healthcare.tcamt.domain.TestStory;
import gov.nist.healthcare.tcamt.domain.data.ComponentModel;
import gov.nist.healthcare.tcamt.domain.data.FieldModel;
import gov.nist.healthcare.tcamt.domain.data.InstanceSegment;
import gov.nist.healthcare.tcamt.domain.data.TestDataCategorization;
import gov.nist.healthcare.tcamt.domain.report.HL7V2MessageValidationReport;
import gov.nist.healthcare.tcamt.service.ManageInstance;
import gov.nist.healthcare.tcamt.service.ValidationMessage;
import gov.nist.healthcare.tcamt.service.XMLManager;
import gov.nist.healthcare.tcamt.service.converter.DataInstanceTestCaseConverter;
import gov.nist.healthcare.tcamt.service.converter.DataInstanceTestGroupConverter;
import gov.nist.healthcare.tcamt.service.converter.DataInstanceTestPlanConverter;
import gov.nist.healthcare.tcamt.service.converter.DataInstanceTestStepConverter;
import gov.nist.healthcare.tcamt.service.converter.JsonDataInstanceTestCaseConverter;
import gov.nist.healthcare.tcamt.service.converter.JsonDataInstanceTestGroupConverter;
import gov.nist.healthcare.tcamt.service.converter.JsonDataInstanceTestPlanConverter;
import gov.nist.healthcare.tcamt.service.converter.JsonDataInstanceTestStepConverter;
import gov.nist.healthcare.tcamt.service.converter.JsonManualTestStepConverter;
import gov.nist.healthcare.tcamt.service.converter.JsonMetadataConverter;
import gov.nist.healthcare.tcamt.service.converter.JsonTestStoryConverter;
import gov.nist.healthcare.tcamt.service.converter.ManualTestStepConverter;
import gov.nist.healthcare.tcamt.service.converter.MetadataConverter;
import gov.nist.healthcare.tcamt.service.converter.TestStoryConverter;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatypes;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segments;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.json.XML;
import org.primefaces.event.ColumnResizeEvent;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.TreeDragDropEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;

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
	private Long defaultTDCId = null;
	private Long jurorDocumentId = null;
	private Long shareTo = null;
	private int activeIndexOfMessageInstancePanel = 0;
	
	private TreeNode testplanRoot = new DefaultTreeNode("root", null);
    private TreeNode selectedNode = null;
    
    private TreeNode segmentTreeRoot = new DefaultTreeNode("root", null);
    private TreeNode filtedSegmentTreeRoot = new DefaultTreeNode("root", null);
//	private TreeNode messageTreeRoot = new DefaultTreeNode("root", null);
	private TreeNode constraintTreeRoot = new DefaultTreeNode("root", null);
    
    private InstanceSegment selectedInstanceSegment= null;
	private List<InstanceSegment> instanceSegments = new ArrayList<InstanceSegment>();
	
	
	private String usageViewOption = "partial";
	private String usageViewOption2 = "partial";
	private List<InstanceSegment> filteredInstanceSegments =  new ArrayList<InstanceSegment>();
	
	
	private ManageInstance manageInstanceService = new ManageInstance();
	
	private transient StreamedContent zipResourceBundleFile;
	
	private TestStoryConverter testStoryConverter;
	private MetadataConverter metadataConverter;
	private ManualTestStepConverter mtsConverter;
	private DataInstanceTestStepConverter tsConverter;
	private DataInstanceTestCaseConverter tcConverter;
	private DataInstanceTestGroupConverter tgConverter;
	private DataInstanceTestPlanConverter tpConverter;
	private HL7V2MessageValidationReport hL7V2MessageValidationReport;
	
	private String testDataSpecificationHTML;
	private String jurorDocumentHTML;
	private String jurorDocumentPDFFileName;
	
	private String messageContentHTML;
	
	private String selectedTestCaseName;
	
	
	private DataInstanceTestCaseGroup parentTestGroup;
	private DataInstanceTestCase parentTestCase;
	
	private String[] selectedDisplayColumns;   
    private List<String> displayColumns;
    
	public DataInstanceTestPlanRequestBean() {
		super();
		
		displayColumns = new ArrayList<String>();
		displayColumns.add("DT");
		displayColumns.add("Usage");
		displayColumns.add("Cardi.");
		displayColumns.add("Length");
		displayColumns.add("Value Set");
		displayColumns.add("Predicate");
		displayColumns.add("Conf.Statements");
		
		selectedDisplayColumns = new String[]{"DT", "Usage", "Cardi.", "Value Set"};
	}
		
	public void shareInit(ActionEvent event){
		try{
			this.shareTo = null;
			this.selectedTestPlan = (DataInstanceTestPlan) event.getComponent().getAttributes().get("testplan");
		}catch(Exception e){
			FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage( FacesMessage.SEVERITY_FATAL, "FATAL Error", e.toString()));
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}
	
	public void createTestPlan() {
		try{
			init();
			this.selectedTestPlan.setName("New TestPlan");
			this.selectedTestPlan.setSelected(true);
			this.createTestPlanTree(this.selectedTestPlan);
			this.sessionBeanTCAMT.setDitActiveIndex(1);
			
			FacesContext context = FacesContext.getCurrentInstance();
	        context.addMessage(null, new FacesMessage("New TestPlan",  "New TestPlan has been created.") );
		}catch(Exception e){
			FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage( FacesMessage.SEVERITY_FATAL, "FATAL Error", e.toString()));
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}
	
	public void cloneTestPlan(ActionEvent event)  {
		try{
			DataInstanceTestPlan testplan = ((DataInstanceTestPlan) event.getComponent().getAttributes().get("testplan")).clone();
			testplan.setName("(Copy)" + testplan.getName());
			this.sessionBeanTCAMT.getDbManager().dataInstanceTestPlanInsert(testplan);
			this.sessionBeanTCAMT.updateDataInstanceTestPlans();
			
			this.selectedTestCase = null;
			this.selectedTestCaseGroup = null;
			this.selectedTestStep = null;
			
			this.selectedTestPlan = testplan;
			this.selectedTestPlan.setSelected(true);
			this.createTestPlanTree(this.selectedTestPlan);
			this.sessionBeanTCAMT.setDitActiveIndex(1);
			
			FacesContext context = FacesContext.getCurrentInstance();
	        context.addMessage(null, new FacesMessage("Clone TestPlan",  "TestPlan: " + this.selectedTestPlan.getName() + " has been clonned.") );
		}catch(Exception e){
			FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage( FacesMessage.SEVERITY_FATAL, "FATAL Error", e.toString()));
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}
	
	public void selectTestPlan(ActionEvent event) {
		try{
			this.selectedTestPlan = (DataInstanceTestPlan) event.getComponent().getAttributes().get("testplan");
			this.selectedTestPlan = this.sessionBeanTCAMT.getDbManager().getDataInstanceTestPlanById(this.selectedTestPlan.getId());
			if(this.selectedTestPlan.getMetadata() == null) this.selectedTestPlan.setMetadata(new Metadata());
			
			this.selectedTestCase = null;
			this.selectedTestCaseGroup = null;
			this.selectedTestStep = null;
			
			this.selectedTestPlan.setSelected(true);
			this.createTestPlanTree(this.selectedTestPlan);
			this.sessionBeanTCAMT.setDitActiveIndex(1);
			
			if(this.selectedTestPlan.getSpecificJurorDocument() == null){	
				jurorDocumentId = null;
				
				if(!this.selectedTestPlan.isJurorDocumentEnable()){
					jurorDocumentId = (long)-1;
				}
				
			}else {
				jurorDocumentId = this.selectedTestPlan.getSpecificJurorDocument().getId();
			}
			
			this.selectedTestPlan.setChanged(false);
			
			for(DataInstanceTestCaseGroup group:this.selectedTestPlan.getTestcasegroups()){
				group.setChanged(false);
				for(DataInstanceTestCase testcase:group.getTestcases()){
					testcase.setChanged(false);
					for(DataInstanceTestStep teststep:testcase.getTeststeps()){
						teststep.setChanged(false);
					}
				}
			}
			
			for(DataInstanceTestCase testcase:this.selectedTestPlan.getTestcases()){
				testcase.setChanged(false);
				for(DataInstanceTestStep teststep:testcase.getTeststeps()){
					teststep.setChanged(false);
				}
			}
			
			
		}catch(Exception e){
			FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage( FacesMessage.SEVERITY_FATAL, "FATAL Error", e.toString()));
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}
	
	public void deleteTestPlan(ActionEvent event) {
		try{
			String deletedTestPlanName = ((DataInstanceTestPlan) event.getComponent().getAttributes().get("testplan")).getName();
			this.sessionBeanTCAMT.getDbManager().dataInstanceTestPlanDelete((DataInstanceTestPlan) event.getComponent().getAttributes().get("testplan"));
			this.sessionBeanTCAMT.updateDataInstanceTestPlans();
		
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Delete TestPlan",  "TestPlan: " + deletedTestPlanName + " has been created.") );
		}catch(Exception e){
			FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage( FacesMessage.SEVERITY_FATAL, "FATAL Error", e.toString()));
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}
	
	public void shareTestPlan() {
		try{
			this.selectedTestPlan.setName("(Copy)" + selectedTestPlan.getName());
			this.selectedTestPlan.setAuthor(this.sessionBeanTCAMT.getDbManager().getUserById(this.shareTo));
			this.sessionBeanTCAMT.getDbManager().dataInstanceTestPlanInsert(selectedTestPlan.clone());
			this.sessionBeanTCAMT.updateDataInstanceTestPlans();
			
			FacesContext context = FacesContext.getCurrentInstance();
	        context.addMessage(null, new FacesMessage("Share TestPlan",  "TestPlan: " + this.selectedTestPlan.getName() + " has been sented to " + this.selectedTestPlan.getAuthor().getUserId()) );
	        
	        this.init();
	        
	        this.sessionBeanTCAMT.setDitActiveIndex(0);
		}catch(Exception e){
			FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage( FacesMessage.SEVERITY_FATAL, "FATAL Error", e.toString()));
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}
	
	public void closeTestPlan()  {
		this.init();
		
	}
	
	public void saveTestPlan()  {
		try{
			if(this.selectedTestCaseGroup != null) this.selectedTestCaseGroup.setSelected(true);
			if(this.selectedTestCase != null) this.selectedTestCase.setSelected(true);
			if(this.selectedTestStep != null) this.selectedTestStep.setSelected(true);
			
			if(this.selectedTestPlan.getId() <= 0){
				this.selectedTestPlan.setVersion(1);
				this.sessionBeanTCAMT.getDbManager().dataInstanceTestPlanInsert(this.selectedTestPlan);
				this.sessionBeanTCAMT.updateDataInstanceTestPlans();
			}else{
				DataInstanceTestPlan testplan = this.selectedTestPlan.clone();
				this.sessionBeanTCAMT.getDbManager().dataInstanceTestPlanInsert(testplan);
				this.sessionBeanTCAMT.updateDataInstanceTestPlans();
				this.sessionBeanTCAMT.getDbManager().dataInstanceTestPlanDelete(this.sessionBeanTCAMT.getDbManager().getDataInstanceTestPlanById(this.selectedTestPlan.getId()));
				this.selectedTestPlan = testplan;
				this.sessionBeanTCAMT.updateDataInstanceTestPlans();
			}
			
			this.createTestPlanTree(this.selectedTestPlan);
			this.selectedTestPlan.setChanged(false);
			
			for(DataInstanceTestCaseGroup group:this.selectedTestPlan.getTestcasegroups()){
				group.setChanged(false);
				for(DataInstanceTestCase testcase:group.getTestcases()){
					testcase.setChanged(false);
					for(DataInstanceTestStep teststep:testcase.getTeststeps()){
						teststep.setChanged(false);
					}
				}
			}
			
			for(DataInstanceTestCase testcase:this.selectedTestPlan.getTestcases()){
				testcase.setChanged(false);
				for(DataInstanceTestStep teststep:testcase.getTeststeps()){
					teststep.setChanged(false);
				}
			}
			
			FacesContext context = FacesContext.getCurrentInstance();
	        context.addMessage(null, new FacesMessage("Save TestPlan",  "TestPlan: " + this.selectedTestPlan.getName() + " has been saved.") );
		}catch(Exception e){
			FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage( FacesMessage.SEVERITY_FATAL, "FATAL Error", e.toString()));
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}
	
	public void addTestCaseGroup() {
		try{
			if(this.selectedNode != null){
				DataInstanceTestCaseGroup newDataInstacneTestCaseGroup = new DataInstanceTestCaseGroup();
				newDataInstacneTestCaseGroup.setVersion(1);
				newDataInstacneTestCaseGroup.setName("New TestCaseGroup");
				newDataInstacneTestCaseGroup.setPosition(this.selectedTestPlan.getTestcasegroups().size() + 1);
				newDataInstacneTestCaseGroup.setSelected(true);
				this.selectedTestPlan.addTestCaseGroup(newDataInstacneTestCaseGroup);
				this.selectedTestPlan.setExpanded(true);
				this.createTestPlanTree(this.selectedTestPlan);
				
				this.selectedTestCase = null;
				this.selectedTestStep = null;
				this.selectedTestCaseGroup = newDataInstacneTestCaseGroup;
				this.modifyTestCaseGroup();
			}else{
				FacesContext context = FacesContext.getCurrentInstance();
                context.addMessage(null, new FacesMessage( FacesMessage.SEVERITY_ERROR, "Error", "SelectedNode is null."));
			}
		}catch(Exception e){
			FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage( FacesMessage.SEVERITY_FATAL, "FATAL Error", e.toString()));
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}
	
	public void cloneTestCaseGroup(ActionEvent event) throws CloneNotSupportedException {
		try{
			if(this.selectedNode != null){
				DataInstanceTestCaseGroup clonedGroup = ((DataInstanceTestCaseGroup)this.selectedNode.getData()).clone();
				clonedGroup.setName("(Copy)" + clonedGroup.getName());
				clonedGroup.setPosition(this.selectedTestPlan.getTestcasegroups().size()+1);
				clonedGroup.setSelected(true);
				this.selectedTestPlan.addTestCaseGroup(clonedGroup);
				this.createTestPlanTree(this.selectedTestPlan);
				
				this.selectedTestCase = null;
				this.selectedTestCaseGroup = clonedGroup;
				this.selectedTestStep = null;
				this.modifyTestCaseGroup();
			}else{
				FacesContext context = FacesContext.getCurrentInstance();
                context.addMessage(null, new FacesMessage( FacesMessage.SEVERITY_ERROR, "Error", "SelectedNode is null."));
			}
		}catch(Exception e){
			FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage( FacesMessage.SEVERITY_FATAL, "FATAL Error", e.toString()));
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}
	
	public void deleteTestCaseGroup(ActionEvent event) {
		try{
			if(this.selectedNode != null){
				this.selectedTestPlan.getTestcasegroups().remove(this.selectedNode.getData());
				this.selectedTestCase = null;
				this.selectedTestCaseGroup = null;
				this.selectedTestStep = null;
				
				this.selectedTestPlan.setSelected(true);
				
				this.createTestPlanTree(this.selectedTestPlan);
				this.updatePositionForPlanAndTree();
				this.modifyTestPlan();
			}else{
				FacesContext context = FacesContext.getCurrentInstance();
                context.addMessage(null, new FacesMessage( FacesMessage.SEVERITY_ERROR, "Error", "SelectedNode is null."));
			}
		}catch(Exception e){
			FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage( FacesMessage.SEVERITY_FATAL, "FATAL Error", e.toString()));
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}		
	}
	
	public void addTestCase() {
		try{
			if(this.selectedNode != null){
				DataInstanceTestCase newDataInstacneTestCase = new DataInstanceTestCase();
				newDataInstacneTestCase.setVersion(1);
				newDataInstacneTestCase.setName("New TestCase");
				
				if(this.selectedNode.getData() instanceof  DataInstanceTestPlan){
					newDataInstacneTestCase.setPosition(this.selectedTestPlan.getTestcases().size() + 1);
					newDataInstacneTestCase.setSelected(true);
					this.selectedTestPlan.setExpanded(true);
					((DataInstanceTestPlan)this.selectedNode.getData()).addTestCase(newDataInstacneTestCase);
					this.parentTestGroup = null;
				}else if(this.selectedNode.getData() instanceof  DataInstanceTestCaseGroup){
					newDataInstacneTestCase.setPosition(((DataInstanceTestCaseGroup)this.selectedNode.getData()).getTestcases().size() + 1);
					newDataInstacneTestCase.setSelected(true);
					((DataInstanceTestCaseGroup)this.selectedNode.getData()).setExpanded(true);
					((DataInstanceTestCaseGroup)this.selectedNode.getData()).addTestCase(newDataInstacneTestCase);
					this.parentTestGroup = (DataInstanceTestCaseGroup)this.selectedNode.getData();
				}
				
				this.createTestPlanTree(this.selectedTestPlan);
				
				this.selectedTestCase = newDataInstacneTestCase;
				this.selectedTestStep = null;
				this.selectedTestCaseGroup = null;
				this.modifyTestCase();
			}else{
				FacesContext context = FacesContext.getCurrentInstance();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "SelectedNode is null."));
			}
		}catch(Exception e){
			FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "FATAL Error", e.toString()));
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}
	
	public void cloneTestCase(ActionEvent event) {
		try{
			if(this.selectedNode != null){
				DataInstanceTestCase clonedTestcase = ((DataInstanceTestCase)this.selectedNode.getData()).clone();
				clonedTestcase.setName("(Copy)" + clonedTestcase.getName());
				clonedTestcase.setSelected(true);
				
				if(this.selectedNode.getParent().getData() instanceof DataInstanceTestCaseGroup){
					DataInstanceTestCaseGroup group = (DataInstanceTestCaseGroup)this.selectedNode.getParent().getData();
					group.setExpanded(true);
					clonedTestcase.setPosition(group.getTestcases().size()+1);
					group.addTestCase(clonedTestcase);
					this.parentTestGroup = (DataInstanceTestCaseGroup)this.selectedNode.getParent().getData();
				}else if(this.selectedNode.getParent().getData() instanceof DataInstanceTestPlan){
					DataInstanceTestPlan plan = (DataInstanceTestPlan)this.selectedNode.getParent().getData();
					plan.setExpanded(true);
					clonedTestcase.setPosition(plan.getTestcases().size()+1);
					plan.addTestCase(clonedTestcase);
					this.parentTestGroup = null;
				}
				
				this.createTestPlanTree(this.selectedTestPlan);
				
				this.selectedTestCase = clonedTestcase;
				this.selectedTestCaseGroup = null;
				this.selectedTestStep = null;
				this.modifyTestCase();
			}else{
				FacesContext context = FacesContext.getCurrentInstance();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "SelectedNode is null."));
			}		
		}catch(Exception e){
			FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "FATAL Error", e.toString()));
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}
	
	public void deleteTestCase(ActionEvent event) {
		try{
			if(this.selectedNode != null){
				this.selectedTestCase = null;
				this.selectedTestCaseGroup = null;
				this.selectedTestStep = null;
				
				TreeNode parentNode = this.selectedNode.getParent();
				if(parentNode.getData() instanceof DataInstanceTestCaseGroup){
					this.selectedTestCaseGroup = (DataInstanceTestCaseGroup)parentNode.getData();
					this.selectedTestCaseGroup.getTestcases().remove(this.selectedNode.getData());
					this.selectedTestCaseGroup.setSelected(true);
					this.modifyTestCaseGroup();
				}else if(parentNode.getData() instanceof DataInstanceTestPlan){
					this.selectedTestPlan.getTestcases().remove(this.selectedNode.getData());
					this.selectedTestPlan.setSelected(true);
					this.modifyTestPlan();
				}
				
				this.createTestPlanTree(this.selectedTestPlan);
				this.updatePositionForPlanAndTree();
			}else{
				FacesContext context = FacesContext.getCurrentInstance();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "SelectedNode is null."));
			}		
		}catch(Exception e){
			FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "FATAL Error", e.toString()));
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}
	
	public void addTestStep(ActionEvent event) {
		try{
			if(this.selectedNode != null){
				this.selectedTestCase = (DataInstanceTestCase)this.selectedNode.getData();
				this.parentTestCase =  (DataInstanceTestCase)this.selectedNode.getData();
				if(this.selectedNode.getParent().getData() instanceof DataInstanceTestCaseGroup){
					this.parentTestGroup = (DataInstanceTestCaseGroup)this.selectedNode.getParent().getData();
				}
				DataInstanceTestStep newTestStep = new DataInstanceTestStep();
				newTestStep.setName("New Test Step");
				newTestStep.setPosition(this.selectedTestCase.getTeststeps().size() + 1);
				newTestStep.setSelected(true);
				this.selectedTestCase.addTestStep(newTestStep);
				this.selectedTestCase.setExpanded(true);
				
				this.createTestPlanTree(this.selectedTestPlan);
				this.selectedTestCaseGroup = null;
				this.selectedTestCase = null;
				this.selectedTestStep = newTestStep;
				
				this.selectTestStep();
				this.modifyTestStep();
			}else{
				FacesContext context = FacesContext.getCurrentInstance();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "SelectedNode is null."));
			}
		}catch(Exception e){
			FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "FATAL Error", e.toString()));
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}
	
	public void cloneTestStep() {
		try{
			if(this.selectedNode != null){
				DataInstanceTestCase testcase = (DataInstanceTestCase)this.selectedNode.getParent().getData();
				DataInstanceTestStep clonedStep = ((DataInstanceTestStep)this.selectedNode.getData()).clone();
				
				this.parentTestCase =  (DataInstanceTestCase)this.selectedNode.getParent().getData();
				if(this.selectedNode.getParent().getParent().getParent().getData() instanceof DataInstanceTestCaseGroup){
					this.parentTestGroup = (DataInstanceTestCaseGroup)this.selectedNode.getParent().getParent().getData();
				}
				
				clonedStep.setName("(Copy)" + clonedStep.getName());
				clonedStep.setPosition(testcase.getTeststeps().size()+1);
				clonedStep.setSelected(true);
				testcase.addTestStep(clonedStep);
				testcase.setExpanded(true);
				this.createTestPlanTree(this.selectedTestPlan);
				
				this.selectedTestCaseGroup = null;
				this.selectedTestCase = null;
				this.selectedTestStep = clonedStep;
				
				this.selectTestStep();
				this.modifyTestStep();
			}else{
				FacesContext context = FacesContext.getCurrentInstance();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "SelectedNode is null."));
			}		
		}catch(Exception e){
			FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "FATAL Error", e.toString()));
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}
	
	public void deleteTestStep(ActionEvent event) {
		try{
			if(this.selectedNode != null){
				TreeNode parentNode = this.selectedNode.getParent();
				if(parentNode.getData() instanceof DataInstanceTestCase){
					this.selectedTestCase = (DataInstanceTestCase)parentNode.getData();
					this.selectedTestCase.getTeststeps().remove(this.selectedNode.getData());
					this.selectedTestCase.setExpanded(true);
					this.selectedTestCase.setSelected(true);
					
					if(parentNode.getParent().getData() instanceof DataInstanceTestCaseGroup){
						this.parentTestGroup = (DataInstanceTestCaseGroup)parentNode.getParent().getData();
					}
					
				}
				
				this.createTestPlanTree(this.selectedTestPlan);
				this.updatePositionForPlanAndTree();
				
				this.selectedTestCaseGroup = null;
				this.selectedTestStep = null;
				
				this.modifyTestCase();
			}else{
				FacesContext context = FacesContext.getCurrentInstance();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "SelectedNode is null."));
			}			
		}catch(Exception e){
			FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "FATAL Error", e.toString()));
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}
	
	public void onNodeSelect(NodeSelectEvent event) {
		try{
			if(event.getTreeNode().getData() instanceof DataInstanceTestPlan){
				this.selectedTestCase = null;
				this.selectedTestCaseGroup = null;
				this.selectedTestStep = null;
			}else if(event.getTreeNode().getData() instanceof DataInstanceTestCase){
				this.selectedTestCase = (DataInstanceTestCase)event.getTreeNode().getData();
				this.selectedTestCaseGroup = null;
				this.selectedTestStep = null;
				
				if(event.getTreeNode().getParent().getData() instanceof DataInstanceTestPlan){
					this.parentTestGroup = null;
				}else if(event.getTreeNode().getParent().getData() instanceof DataInstanceTestCaseGroup){
					this.parentTestGroup = (DataInstanceTestCaseGroup)event.getTreeNode().getParent().getData();
				}
			}else if(event.getTreeNode().getData() instanceof DataInstanceTestCaseGroup){
				this.selectedTestCase = null;
				this.selectedTestStep = null;
				this.selectedTestCaseGroup = (DataInstanceTestCaseGroup)event.getTreeNode().getData();
			}else if(event.getTreeNode().getData() instanceof DataInstanceTestStep){
				this.selectedTestStep = (DataInstanceTestStep)event.getTreeNode().getData();
				this.selectedTestCaseName = ((DataInstanceTestCase)event.getTreeNode().getParent().getData()).getName();
				this.selectedTestCase = null;
				this.selectedTestCaseGroup = null;
				this.selectTestStep();
				this.activeIndexOfMessageInstancePanel = 0;
				
				this.parentTestCase = (DataInstanceTestCase)event.getTreeNode().getParent().getData();
				
				if(event.getTreeNode().getParent().getParent().getData() instanceof DataInstanceTestPlan){
					this.parentTestGroup = null;
				}else if(event.getTreeNode().getParent().getParent().getData() instanceof DataInstanceTestCaseGroup){
					this.parentTestGroup = (DataInstanceTestCaseGroup)event.getTreeNode().getParent().getParent().getData();
				}
			}
			this.clearSelectNode(this.testplanRoot);
			event.getTreeNode().setSelected(true);			
		}catch(Exception e){
			FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "FATAL Error", e.toString()));
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
    }
	
	public void onNodeExpand(NodeExpandEvent event) {
        try{
			if(event.getTreeNode().getData() instanceof DataInstanceTestPlan){
				this.selectedTestPlan.setExpanded(true);
			}else if(event.getTreeNode().getData() instanceof DataInstanceTestCase){
				DataInstanceTestCase dataInstanceTestCase = (DataInstanceTestCase)event.getTreeNode().getData();
				dataInstanceTestCase.setExpanded(true);
			}else if(event.getTreeNode().getData() instanceof DataInstanceTestCaseGroup){
				DataInstanceTestCaseGroup dataInstanceTestCaseGroup = (DataInstanceTestCaseGroup)event.getTreeNode().getData();
				dataInstanceTestCaseGroup.setExpanded(true);
			}	
		}catch(Exception e){
			FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "FATAL Error", e.toString()));
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
    }
	
	public void onNodeCollapse(NodeCollapseEvent event) {
        try{
			if(event.getTreeNode().getData() instanceof DataInstanceTestPlan){
				this.selectedTestPlan.setExpanded(false);
			}else if(event.getTreeNode().getData() instanceof DataInstanceTestCase){
				DataInstanceTestCase dataInstanceTestCase = (DataInstanceTestCase)event.getTreeNode().getData();
				dataInstanceTestCase.setExpanded(false);
			}else if(event.getTreeNode().getData() instanceof DataInstanceTestCaseGroup){
				DataInstanceTestCaseGroup dataInstanceTestCaseGroup = (DataInstanceTestCaseGroup)event.getTreeNode().getData();
				dataInstanceTestCaseGroup.setExpanded(false);
			}	
		}catch(Exception e){
			FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "FATAL Error", e.toString()));
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
    }
	
	public void saveHL7Message() {
		this.readHL7Message();
		this.segmentTreeRoot = new DefaultTreeNode("root", null);
		this.filtedSegmentTreeRoot = new DefaultTreeNode("root", null);
	}
	
	public void readHL7Message() {
		try{
			this.instanceSegments = new ArrayList<InstanceSegment>();
			if(this.selectedTestStep.getMessage().getHl7EndcodedMessage() != null && !this.selectedTestStep.getMessage().getHl7EndcodedMessage().equals("")){
				this.manageInstanceService.loadMessageInstance(this.selectedTestStep.getMessage(), this.instanceSegments, selectedTestCaseName);
				this.selectedInstanceSegment = null;
				this.generateMessageContentHTML();				
				this.generateTestDataSpecificationHTML();
				this.generateJurorDocumentHTML();
			}
			this.updateFilteredInstanceSegments();
		}catch(Exception e){
			FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "FATAL Error", e.toString()));
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}
	
	public void loadHL7Message(){
		try{
			this.selectedTestStep.getMessage().setHl7EndcodedMessage(this.selectedTestStep.getMessage().getConformanceProfile().getSampleER7Message());
			this.readHL7Message();
			
			this.modifyTestStep();
		}catch(Exception e){
			FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "FATAL Error", e.toString()));
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}
	
	public void onInstanceSegmentSelect(SelectEvent event){
		try{
			this.segmentTreeRoot = new DefaultTreeNode("root", null);
			this.manageInstanceService.genSegmentTree(this.segmentTreeRoot, this.selectedInstanceSegment, this.selectedTestStep.getMessage());
			this.activeIndexOfMessageInstancePanel = 4;
			this.updateFilteredSegmentTree();
		}catch(Exception e){
			FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "FATAL Error", e.toString()));
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}
	
//	public void genrateHL7Message() {
//		try{
//			this.selectedTestStep.getMessage().setHl7EndcodedMessage(this.manageInstanceService.generateHL7Message(this.messageTreeRoot,  this.selectedTestStep.getMessage()));
//			this.readHL7Message();			
//		}catch(Exception e){
//			FacesContext context = FacesContext.getCurrentInstance();
//            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "FATAL Error", e.toString()));
//			e.printStackTrace();
//			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
//			this.sessionBeanTCAMT.getDbManager().logInsert(log);
//		}
//	}
	
	private int findLineNum(String ipath){
		int result = 0;
		for(InstanceSegment is: this.instanceSegments){
			if(is.getIpath().equals(ipath)){
				return result;
			}
			result = result + 1;
		}
		return -1;
	}
	
	public void updateInstanceData(Object model) {
		try{
			int lineNum = this.findLineNum(this.selectedInstanceSegment.getIpath());
			if(lineNum >= 0){
				this.manageInstanceService.updateHL7Message(lineNum, this.manageInstanceService.generateLineStr(this.segmentTreeRoot), this.selectedTestStep.getMessage());
				this.readHL7Message();
				this.selectedInstanceSegment = this.instanceSegments.get(lineNum);
				this.activeIndexOfMessageInstancePanel = 4;
				
				this.createTCAMTConstraint(model);
				
				this.modifyTestStep();
			}else {
				throw new Exception("NOT FOUND Line Num");
			}
		}catch(Exception e){
			FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "FATAL Error", e.toString()));
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}
	
	public void delInstanceSegment(ActionEvent event) {
		try{
			InstanceSegment toBeDeletedInstanceSegment = (InstanceSegment)event.getComponent().getAttributes().get("instanceSegment");
			
			int rowIndex = this.instanceSegments.indexOf(toBeDeletedInstanceSegment);
			String[] lines = this.selectedTestStep.getMessage().getHl7EndcodedMessage().split(System.getProperty("line.separator"));
			String editedHl7EndcodedMessage = "";
			for(int i=0; i<lines.length;i++){
				if(i != rowIndex){
					editedHl7EndcodedMessage = editedHl7EndcodedMessage + lines[i] + System.getProperty("line.separator");
				}
			}
			this.selectedTestStep.getMessage().setHl7EndcodedMessage(editedHl7EndcodedMessage);
			this.readHL7Message();
			
			this.selectedInstanceSegment = null;
			this.segmentTreeRoot = new DefaultTreeNode("root", null);
		    this.filtedSegmentTreeRoot = new DefaultTreeNode("root", null);
		    
		    this.modifyTestStep();
		}catch(Exception e){
			FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "FATAL Error", e.toString()));
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}
	
	public void downloadResourceBundleForTestPlan(DataInstanceTestPlan tp, boolean needPDF) {
		try{
			this.setTestStoryConverter(new JsonTestStoryConverter());
			this.setTpConverter(new JsonDataInstanceTestPlanConverter());
			this.setTgConverter(new JsonDataInstanceTestGroupConverter());
			this.setTcConverter(new JsonDataInstanceTestCaseConverter());
			this.setTsConverter(new JsonDataInstanceTestStepConverter());
			this.setMtsConverter(new JsonManualTestStepConverter());
			this.setMetadataConverter(new JsonMetadataConverter());
			
			String outFilename = tp.getName() + ".zip";
			ByteArrayOutputStream outputStream = null;
			byte[] bytes;
			outputStream = new ByteArrayOutputStream();
			ZipOutputStream out = new ZipOutputStream(outputStream);
			
//			this.generateMetaDataRB(out, tp.getMetadata());
			this.generateTestPackage(out, tp, needPDF);
			
			if(tp.getType() != null && tp.getType().equals("Isolated")){
				this.generateIsolatedRB(out, tp, needPDF);
			}else {
				this.generateContextBasedRB(out, tp, needPDF);
			}
//			this.generateDocumentationRB(out, tp);
//			this.generateProfilesConstraintsValueSetsRB(out, tp);

			out.close();
			bytes = outputStream.toByteArray();
			ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
			this.setZipResourceBundleFile(new DefaultStreamedContent(inputStream, "application/zip", outFilename));
			
//			FacesContext context = FacesContext.getCurrentInstance();
//	        context.addMessage(null, new FacesMessage("Resource Bundle",  "Resource bundel has been populated."));
		}catch(Exception e){
			FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "FATAL Error", e.toString()));
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}
	
	private void generateTestPackage(ZipOutputStream out, DataInstanceTestPlan tp, boolean needPDF) throws Exception {
		String packageBodyHTML= "";
		String tocHTML = "<ol>" + System.getProperty("line.separator");
		
		packageBodyHTML = packageBodyHTML + tp.getMetadata().getTestSuiteHeader() + System.getProperty("line.separator");
		packageBodyHTML = packageBodyHTML + tp.getMetadata().getTestSuiteHomeTitle() + System.getProperty("line.separator");
		packageBodyHTML = packageBodyHTML + "<h2>" + tp.getMetadata().getTestSuiteDomain() + "</h2>" + System.getProperty("line.separator");
		packageBodyHTML = packageBodyHTML + "<h2>" + tp.getMetadata().getTestSuiteVersion() + "<h2>" + System.getProperty("line.separator");
		packageBodyHTML = packageBodyHTML + "<h2>" + tp.getMetadata().getTestSuiteAdminEmail() + "<h2>" + System.getProperty("line.separator");
		packageBodyHTML = packageBodyHTML + "<h2>" + tp.getLastUpdateDate() + "<h2>" + System.getProperty("line.separator");
		packageBodyHTML = packageBodyHTML + "<p style=\"page-break-after:always;\"></p>";
		packageBodyHTML = packageBodyHTML + tp.getMetadata().getTestSuiteHomeContent() + System.getProperty("line.separator");
		packageBodyHTML = packageBodyHTML + "<p style=\"page-break-after:always;\"></p>";
		packageBodyHTML = packageBodyHTML + "?TOC?"+ System.getProperty("line.separator");
		packageBodyHTML = packageBodyHTML + "<p style=\"page-break-after:always;\"></p>";
		
		HashMap<Integer, Object>  testPlanMap = new HashMap<Integer, Object>();
		for(DataInstanceTestCaseGroup tcg:tp.getTestcasegroups()){
			testPlanMap.put(tcg.getPosition(), tcg);
		}
		for(DataInstanceTestCase tc:tp.getTestcases()){
			testPlanMap.put(tc.getPosition(), tc);
		}
		
		for(int i=0; i< testPlanMap.keySet().size(); i++){
			Object child = testPlanMap.get(i+1);
			
			if(child instanceof DataInstanceTestCaseGroup){
				DataInstanceTestCaseGroup group = (DataInstanceTestCaseGroup)child;
				packageBodyHTML = packageBodyHTML + "<A NAME=\"" + (i+1) + "\">" + "<h2>" + (i+1) + ". " + group.getName() + "</h2>" + System.getProperty("line.separator");
				tocHTML = tocHTML + "<li><a href=\"#"+ (i+1) + "\">"+ (i+1) + ". " + group.getName() +"</a></li>" +  System.getProperty("line.separator");
				
				packageBodyHTML = packageBodyHTML + "<span>" + group.getLongDescription() + "</span>" + System.getProperty("line.separator");
				packageBodyHTML = packageBodyHTML + "<p style=\"page-break-after:always;\"></p>";
				
				HashMap<Integer, DataInstanceTestCase>  testCaseMap = new HashMap<Integer, DataInstanceTestCase>();
				for(DataInstanceTestCase tc:group.getTestcases()){
					testCaseMap.put(tc.getPosition(), tc);
				}
				tocHTML = tocHTML + "<ol>" + System.getProperty("line.separator");
				for(int j=0; j<testCaseMap.keySet().size(); j++){
					DataInstanceTestCase tc = testCaseMap.get(j+1);
					
					packageBodyHTML = packageBodyHTML + "<A NAME=\"" + (i+1) + "." + (j+1) + "\">" + "<h2>" + (i+1) + "." + (j+1) + ". " + tc.getName() + "</h2>" + System.getProperty("line.separator");
					tocHTML = tocHTML + "<li><a href=\"#"+ (i+1) + "." + (j+1) + "\">"+ (i+1) + "." + (j+1) + ". " + tc.getName() +"</a></li>" +  System.getProperty("line.separator");
					
					packageBodyHTML = packageBodyHTML + "<span>" + tc.getLongDescription() + "</span>" + System.getProperty("line.separator");
					packageBodyHTML = packageBodyHTML + "<h3>" + "Test Story" + "</h3>" + System.getProperty("line.separator");
					packageBodyHTML = packageBodyHTML + this.retrieveBodyContent(this.generateTestStory(tc.getTestCaseStory(), "plain"));
					packageBodyHTML = packageBodyHTML + "<p style=\"page-break-after:always;\"></p>";
					
					HashMap<Integer, DataInstanceTestStep>  testStepMap = new HashMap<Integer, DataInstanceTestStep>();
					for(DataInstanceTestStep ts:tc.getTeststeps()){
						testStepMap.put(ts.getPosition(), ts);
					}
					
					tocHTML = tocHTML + "<ol>" + System.getProperty("line.separator");
					for(int k=0; k < testStepMap.keySet().size(); k++){
						DataInstanceTestStep ts = testStepMap.get(k+1);
						packageBodyHTML = packageBodyHTML + "<A NAME=\"" + (i+1) + "." + (j+1) + "." + (k+1) + "\">" + "<h2>" + (i+1) + "." + (j+1) + "." + (k+1) + ". " + ts.getName() + "</h2>" + System.getProperty("line.separator");
						tocHTML = tocHTML + "<li><a href=\"#" + (i+1) + "." + (j+1) + "." + (k+1) + "\">"+ (i+1) + "." + (j+1) + "." + (k+1) + ". " + ts.getName() +"</a></li>" +  System.getProperty("line.separator");
						
						if(tp.getType().equals("Isolated")){
							packageBodyHTML = packageBodyHTML + "<span>Test Step Type: " + ts.getType() + "</span><br/>" + System.getProperty("line.separator");
						}
						packageBodyHTML = packageBodyHTML + "<span>" + ts.getLongDescription() + "</span>" + System.getProperty("line.separator");
						packageBodyHTML = packageBodyHTML + "<h3>" + "Test Story" + "</h3>" + System.getProperty("line.separator");
						packageBodyHTML = packageBodyHTML + this.retrieveBodyContent(this.generateTestStory(ts.getTestStepStory(), "plain"));
						
						if(ts != null && ts.getMessage() != null && ts.getMessage().getConformanceProfile() != null){
							this.instanceSegments = new ArrayList<InstanceSegment>();
							if(ts.getMessage().getHl7EndcodedMessage() != null && !ts.getMessage().getHl7EndcodedMessage().equals("")){
								this.manageInstanceService.loadMessage(ts.getMessage());
								this.manageInstanceService.loadMessageInstance(ts.getMessage(), this.instanceSegments, tc.getName());
								
								this.manageInstanceService.generateXMLFromMesageInstance(ts.getMessage(), instanceSegments, true, tc.getName());
								this.manageInstanceService.generateXMLFromMesageInstance(ts.getMessage(), instanceSegments, false, tc.getName());
								this.manageInstanceService.generateXMLforMessageContentAndUpdateTestData(ts.getMessage(), instanceSegments);
							}

							if(ts.getMessage().getConformanceProfile().getMessageContentXSLT()!= null){
								String mcXSL = ts.getMessage().getConformanceProfile().getMessageContentXSLT().replaceAll("<xsl:param name=\"output\" select=\"'ng-tab-html'\"/>", "<xsl:param name=\"output\" select=\"'plain-html'\"/>");
								InputStream xsltInputStream = new ByteArrayInputStream(mcXSL.getBytes());
								InputStream sourceInputStream = new ByteArrayInputStream(ts.getMessage().getXmlEncodedMessageContent().getBytes());
								Reader xsltReader =  new InputStreamReader(xsltInputStream, "UTF-8");
								Reader sourceReader = new InputStreamReader(sourceInputStream, "UTF-8");
								String xsltStr = IOUtils.toString(xsltReader);
								String sourceStr = IOUtils.toString(sourceReader);
								
								String messageContentHTMLStr = XMLManager.parseXmlByXSLT(sourceStr, xsltStr);
								packageBodyHTML = packageBodyHTML + "<h3>" + "Message Contents" + "</h3>" + System.getProperty("line.separator");
//								packageBodyHTML = packageBodyHTML + this.retrieveBodyContent(messageContentHTMLStr);
								packageBodyHTML = packageBodyHTML + messageContentHTMLStr;
							}
							
							if(ts.getMessage().getConformanceProfile().getTestDataSpecificationXSLT() != null){
								String tdXSL = ts.getMessage().getConformanceProfile().getTestDataSpecificationXSLT().replaceAll("<xsl:param name=\"output\" select=\"'ng-tab-html'\"/>", "<xsl:param name=\"output\" select=\"'plain-html'\"/>");
								InputStream xsltInputStream = new ByteArrayInputStream(tdXSL.getBytes());
								InputStream sourceInputStream = new ByteArrayInputStream(ts.getMessage().getXmlEncodedNISTMessage().getBytes());
								Reader xsltReader =  new InputStreamReader(xsltInputStream, "UTF-8");
								Reader sourceReader = new InputStreamReader(sourceInputStream, "UTF-8");
								String xsltStr = IOUtils.toString(xsltReader);
								String sourceStr = IOUtils.toString(sourceReader);
								
								String testDataSpecificationHTMLStr = XMLManager.parseXmlByXSLT(sourceStr, xsltStr);
								packageBodyHTML = packageBodyHTML + "<h3>" + "Test Data Specification" + "</h3>" + System.getProperty("line.separator");
//								packageBodyHTML = packageBodyHTML + this.retrieveBodyContent(testDataSpecificationHTMLStr);
								packageBodyHTML = packageBodyHTML + testDataSpecificationHTMLStr;
							}
							if(tp.isJurorDocumentEnable()){
								if(ts.getMessage().getConformanceProfile().getJurorDocumentXSLT() != null){
									InputStream xsltInputStream = null;
									if(tp.getSpecificJurorDocument() == null){
										xsltInputStream = new ByteArrayInputStream(ts.getMessage().getConformanceProfile().getJurorDocumentXSLT().getBytes());
									}else {
										xsltInputStream = new ByteArrayInputStream(tp.getSpecificJurorDocument().getJurorDocumentHTML().getBytes());
									}
									
									
									InputStream sourceInputStream = new ByteArrayInputStream(ts.getMessage().getXmlEncodedNISTMessage().getBytes());
									
									Reader xsltReader =  new InputStreamReader(xsltInputStream, "UTF-8");
									Reader sourceReader = new InputStreamReader(sourceInputStream, "UTF-8");
									String xsltStr = IOUtils.toString(xsltReader);
									String sourceStr = IOUtils.toString(sourceReader);
									String jurorDocumentHTMLStr = XMLManager.parseXmlByXSLT(sourceStr, xsltStr);
									packageBodyHTML = packageBodyHTML + "<h3>" + "Juror Document" + "</h3>" + System.getProperty("line.separator");
									packageBodyHTML = packageBodyHTML + this.retrieveBodyContent(jurorDocumentHTMLStr);
								}
							}
						}
						
						packageBodyHTML = packageBodyHTML + "<p style=\"page-break-after:always;\"></p>";
					}
					tocHTML = tocHTML + "</ol>" + System.getProperty("line.separator");
					
				}
				tocHTML = tocHTML + "</ol>" + System.getProperty("line.separator");
				
			}else if(child instanceof DataInstanceTestCase){
				DataInstanceTestCase tc = (DataInstanceTestCase)child;
				packageBodyHTML = packageBodyHTML + "<A NAME=\"" + (i+1) + "\">" + "<h2>" + (i+1) + ". " + tc.getName() + "</h2>" + System.getProperty("line.separator");
				tocHTML = tocHTML + "<li><a href=\"#" + (i+1) + "\">"+ (i+1) + ". " + tc.getName() +"</a></li>" +  System.getProperty("line.separator");
				packageBodyHTML = packageBodyHTML + "<span>" + tc.getLongDescription() + "</span>" + System.getProperty("line.separator");
				packageBodyHTML = packageBodyHTML + "<h3>" + "Test Story" + "</h3>" + System.getProperty("line.separator");
				packageBodyHTML = packageBodyHTML + this.retrieveBodyContent(this.generateTestStory(tc.getTestCaseStory(), "plain"));
				packageBodyHTML = packageBodyHTML + "<p style=\"page-break-after:always;\"></p>";
				
				HashMap<Integer, DataInstanceTestStep>  testStepMap = new HashMap<Integer, DataInstanceTestStep>();
				for(DataInstanceTestStep ts:tc.getTeststeps()){
					testStepMap.put(ts.getPosition(), ts);
				}
				
				tocHTML = tocHTML + "<ol>" + System.getProperty("line.separator");
				for(int j=0; j<testStepMap.keySet().size(); j++){
					DataInstanceTestStep ts = testStepMap.get(j+1);
					packageBodyHTML = packageBodyHTML + "<A NAME=\"" + (i+1) + "." + (j+1) + "\">" + "<h2>" + (i+1) + "." + (j+1) + ". " + ts.getName() + "</h2>" + System.getProperty("line.separator");
					tocHTML = tocHTML + "<li><a href=\"#" + (i+1) + "." + (j+1) + "\">"+ (i+1) + "." + (j+1) + ". " + ts.getName() +"</a></li>" +  System.getProperty("line.separator");
					if(tp.getType().equals("Isolated")){
						packageBodyHTML = packageBodyHTML + "<span>Test Step Type: " + ts.getType() + "</span><br/>" + System.getProperty("line.separator");
					}
					packageBodyHTML = packageBodyHTML + "<span>" + ts.getLongDescription() + "</span>" + System.getProperty("line.separator");
					packageBodyHTML = packageBodyHTML + "<h3>" + "Test Story" + "</h3>" + System.getProperty("line.separator");
					packageBodyHTML = packageBodyHTML + this.retrieveBodyContent(this.generateTestStory(ts.getTestStepStory(), "plain"));
					if(ts != null && ts.getMessage() != null && ts.getMessage().getConformanceProfile() != null){
						this.instanceSegments = new ArrayList<InstanceSegment>();
						if(ts.getMessage().getHl7EndcodedMessage() != null && !ts.getMessage().getHl7EndcodedMessage().equals("")){
							this.manageInstanceService.loadMessage(ts.getMessage());
							this.manageInstanceService.loadMessageInstance(ts.getMessage(), this.instanceSegments, tc.getName());
						}
						
						if(ts.getMessage().getConformanceProfile().getMessageContentXSLT()!= null){
							String mcXSL = ts.getMessage().getConformanceProfile().getMessageContentXSLT().replaceAll("<xsl:param name=\"output\" select=\"'ng-tab-html'\"/>", "<xsl:param name=\"output\" select=\"'plain-html'\"/>");
							InputStream xsltInputStream = new ByteArrayInputStream(mcXSL.getBytes());
							InputStream sourceInputStream = new ByteArrayInputStream(ts.getMessage().getXmlEncodedMessageContent().getBytes());
							Reader xsltReader =  new InputStreamReader(xsltInputStream, "UTF-8");
							Reader sourceReader = new InputStreamReader(sourceInputStream, "UTF-8");
							String xsltStr = IOUtils.toString(xsltReader);
							String sourceStr = IOUtils.toString(sourceReader);
							
							String messageContentHTMLStr = XMLManager.parseXmlByXSLT(sourceStr, xsltStr);
							packageBodyHTML = packageBodyHTML + "<h3>" + "Message Contents" + "</h3>" + System.getProperty("line.separator");
//							packageBodyHTML = packageBodyHTML + this.retrieveBodyContent(messageContentHTMLStr);
							packageBodyHTML = packageBodyHTML + messageContentHTMLStr;
						}
						
						if(ts.getMessage().getConformanceProfile().getTestDataSpecificationXSLT() != null){
							String tdXSL = ts.getMessage().getConformanceProfile().getTestDataSpecificationXSLT().replaceAll("<xsl:param name=\"output\" select=\"'ng-tab-html'\"/>", "<xsl:param name=\"output\" select=\"'plain-html'\"/>");
							InputStream xsltInputStream = new ByteArrayInputStream(tdXSL.getBytes());
							InputStream sourceInputStream = new ByteArrayInputStream(ts.getMessage().getXmlEncodedNISTMessage().getBytes());
							Reader xsltReader =  new InputStreamReader(xsltInputStream, "UTF-8");
							Reader sourceReader = new InputStreamReader(sourceInputStream, "UTF-8");
							String xsltStr = IOUtils.toString(xsltReader);
							String sourceStr = IOUtils.toString(sourceReader);
							
							String testDataSpecificationHTMLStr = XMLManager.parseXmlByXSLT(sourceStr, xsltStr);
							packageBodyHTML = packageBodyHTML + "<h3>" + "Test Data Specification" + "</h3>" + System.getProperty("line.separator");
//							packageBodyHTML = packageBodyHTML + this.retrieveBodyContent(testDataSpecificationHTMLStr);
							packageBodyHTML = packageBodyHTML + testDataSpecificationHTMLStr;
						}
						if(tp.isJurorDocumentEnable()){
							if(ts.getMessage().getConformanceProfile().getJurorDocumentXSLT() != null){
								InputStream xsltInputStream = null;
								if(tp.getSpecificJurorDocument() == null){
									xsltInputStream = new ByteArrayInputStream(ts.getMessage().getConformanceProfile().getJurorDocumentXSLT().getBytes());
								}else {
									xsltInputStream = new ByteArrayInputStream(tp.getSpecificJurorDocument().getJurorDocumentHTML().getBytes());
								}
								
								
								InputStream sourceInputStream = new ByteArrayInputStream(ts.getMessage().getXmlEncodedNISTMessage().getBytes());
								
								Reader xsltReader =  new InputStreamReader(xsltInputStream, "UTF-8");
								Reader sourceReader = new InputStreamReader(sourceInputStream, "UTF-8");
								String xsltStr = IOUtils.toString(xsltReader);
								String sourceStr = IOUtils.toString(sourceReader);
								String jurorDocumentHTMLStr = XMLManager.parseXmlByXSLT(sourceStr, xsltStr);
								packageBodyHTML = packageBodyHTML + "<h3>" + "Juror Document" + "</h3>" + System.getProperty("line.separator");
								packageBodyHTML = packageBodyHTML + this.retrieveBodyContent(jurorDocumentHTMLStr);
							}
						}
					}
					packageBodyHTML = packageBodyHTML + "<p style=\"page-break-after:always;\"></p>";
				}
				tocHTML = tocHTML + "</ol>" + System.getProperty("line.separator");
			}
		}
		
		tocHTML = tocHTML + "</ol>" + System.getProperty("line.separator");
		
		ClassLoader classLoader = getClass().getClassLoader();
		String testPackageStr = IOUtils.toString(classLoader.getResourceAsStream("TestPackage.html"));
		testPackageStr = testPackageStr.replace("?bodyContent?", packageBodyHTML);
		testPackageStr = testPackageStr.replace("?TOC?", tocHTML);
		
		byte[] buf = new byte[1024];
		out.putNextEntry(new ZipEntry("TestPackage.html"));
		InputStream inTestPackage = IOUtils.toInputStream(testPackageStr);
		int lenTestPackage;
        while ((lenTestPackage = inTestPackage.read(buf)) > 0) {
            out.write(buf, 0, lenTestPackage);
        }
        out.closeEntry();
        inTestPackage.close();
        
        if(needPDF){
        	out.putNextEntry(new ZipEntry("TestPackage.pdf"));
            String tempFileName = this.htmlStringToPDF(testPackageStr);
            File zipFile = new File(tempFileName + ".pdf");	        
            FileInputStream inTestPackagePDF = new FileInputStream(zipFile);
            
            int lenTestPackagePDF;
            while ((lenTestPackagePDF = inTestPackagePDF.read(buf)) > 0) {
                out.write(buf, 0, lenTestPackagePDF);
            }
            inTestPackagePDF.close();
            out.closeEntry();
            this.fileDelete(tempFileName + ".html");
            this.fileDelete(tempFileName + ".pdf");
        }
        
	}

	private String retrieveBodyContent(String generateTestStory) {
		
		
		int beginIndex = generateTestStory.indexOf("<body>");
		int endIndex = generateTestStory.indexOf("</body>");
		
		return "" + generateTestStory.subSequence(beginIndex + "<body>".length() , endIndex);
	}

	public void createTCAMTConstraint(Object model) {
		try{
			String ipath = null;
			String data = null;
			String level = "TestCase";
			String iPosition = null;
			String messageName = null;
			String usageList = null;
			TestDataCategorization tdc = null;
			List<String> listValue = null;
			
			if(model instanceof FieldModel){
				FieldModel fModel = (FieldModel)model;
				ipath = fModel.getIpath();
				data = fModel.getData();
				tdc = fModel.getTdc();
				iPosition = fModel.getiPositionPath();
				messageName = fModel.getMessageName();
				usageList = fModel.getUsageList();
				listValue = fModel.getListValues();
			}else if(model instanceof ComponentModel){
				ComponentModel cModel = (ComponentModel)model;
				ipath = cModel.getIpath();
				data = cModel.getData();
				tdc = cModel.getTdc();
				iPosition = cModel.getiPositionPath();
				messageName = cModel.getMessageName();
				usageList = cModel.getUsageList();
				listValue = cModel.getListValues();
			}
			this.selectedTestStep.getMessage().deleteTCAMTConstraintByIPath(ipath);

			if(tdc != null && !tdc.getValue().equals("")){
				TCAMTConstraint tcamtConstraint = new TCAMTConstraint();
				tcamtConstraint.setCategorization(tdc);
				if(tdc.equals(TestDataCategorization.Value_TestCaseFixedList)){
					String listValueStr = "";
					
					for(String v:listValue){
						if(listValueStr.equals("")){
							listValueStr = listValueStr + "'" + v + "'";
						}else{
							listValueStr = listValueStr + ",'" + v + "'";
						}
					}
					tcamtConstraint.setData(listValueStr);
					
				}else {
					tcamtConstraint.setData(data);
				}
				tcamtConstraint.setIpath(ipath);
				tcamtConstraint.setLevel(level);
				tcamtConstraint.setiPosition(iPosition);
				tcamtConstraint.setMessageName(messageName);
				tcamtConstraint.setUsageList(usageList);
				this.selectedTestStep.getMessage().addTCAMTConstraint(tcamtConstraint);
			}
			this.constraintTreeRoot = this.manageInstanceService.generateConstraintTree(this.selectedTestStep.getMessage(), this.instanceSegments);
			this.generateMessageContentHTML();	
			
			this.activeIndexOfMessageInstancePanel = 4;
			
			this.modifyTestStep();
		}catch(Exception e){
			FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "FATAL Error", e.toString()));
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
		
	}
	
	public void updateFilteredInstanceSegments() {
		try{
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
		}catch(Exception e){
			FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "FATAL Error", e.toString()));
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
    }
	
	public void updateFilteredSegmentTree(){	
		try{
			if(this.usageViewOption2.equals("all")){
				this.filtedSegmentTreeRoot = this.segmentTreeRoot;
			}else {
				this.filtedSegmentTreeRoot = this.manageInstanceService.genRestrictedTree(this.segmentTreeRoot);
			}
		}catch(Exception e){
			FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "FATAL Error", e.toString()));
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}
	
	public void addRepeatedField(FieldModel fieldModel){
		try{
			this.manageInstanceService.addRepeatedField(fieldModel, this.segmentTreeRoot, this.selectedTestStep.getMessage());
			this.updateFilteredSegmentTree();
			this.modifyTestStep();
		}catch(Exception e){
			FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "FATAL Error", e.toString()));
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}
	
	public void updateMessageTestStep() {
		try{
			Message m = this.sessionBeanTCAMT.getDbManager().getMessageById(this.messageId);
			m.setId(this.selectedTestStep.getMessage().getId());
			m.setAuthor(null);
			this.selectedTestStep.setMessage(m);
			this.messageId = null;
			
			selectTestStep();
			
			this.modifyTestStep();
	        
		}catch(Exception e){
			FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "FATAL Error", e.toString()));
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}
	
	public void updateSpecificJurorDocument(){
		try{
			if(this.jurorDocumentId == 0){
				this.selectedTestPlan.setSpecificJurorDocument(null);
				this.selectedTestPlan.setJurorDocumentEnable(true);
			}else {
				JurorDocument jd = this.sessionBeanTCAMT.getDbManager().getJurorDocumentById(this.jurorDocumentId);
				this.selectedTestPlan.setSpecificJurorDocument(jd);	
				if(jd == null){
					this.selectedTestPlan.setJurorDocumentEnable(false);
				}else {
					this.selectedTestPlan.setJurorDocumentEnable(true);
				}
			}
			
			this.modifyTestPlan();
		}catch(Exception e){
			FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "FATAL Error", e.toString()));
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}
	
	public void onDragDrop(TreeDragDropEvent event) {
		try{
			TreeNode dragNode = event.getDragNode();
	        TreeNode dropNode = event.getDropNode();
	        int dropIndex = event.getDropIndex();
	        
	        if(dragNode.getData() instanceof DataInstanceTestPlan){
	        	if(dropNode.getData() instanceof DataInstanceTestPlan){
	        		FacesContext context = FacesContext.getCurrentInstance();
	                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Test Plan cannot have another Test Plan. Have been reverted."));
	        		this.createTestPlanTree(this.selectedTestPlan);
	        	}else if(dropNode.getData() instanceof DataInstanceTestCaseGroup){
	        		FacesContext context = FacesContext.getCurrentInstance();
	                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Test Group cannot have Test Plan. Have been reverted."));
	        		this.createTestPlanTree(this.selectedTestPlan);
	        	}else if(dropNode.getData() instanceof DataInstanceTestCase){
	        		FacesContext context = FacesContext.getCurrentInstance();
	                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Test Case cannot have Test Plan. Have been reverted."));
	        		this.createTestPlanTree(this.selectedTestPlan);
	        	}else if(dropNode.getData() instanceof DataInstanceTestStep){
	        		FacesContext context = FacesContext.getCurrentInstance();
	                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Test Step cannot have Test Plan. Have been reverted."));
	        		this.createTestPlanTree(this.selectedTestPlan);
	        	}
	        }else if(dragNode.getData() instanceof DataInstanceTestCaseGroup){
	        	if(dropNode.getData() instanceof DataInstanceTestPlan){
	        		this.updateTestPlanByTree(dragNode, dropNode, dropIndex);
	        	}else if(dropNode.getData() instanceof DataInstanceTestCaseGroup){
	        		FacesContext context = FacesContext.getCurrentInstance();
	                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Test Group cannot have another Test Group. Have been reverted."));
	        		this.createTestPlanTree(this.selectedTestPlan);
	        	}else if(dropNode.getData() instanceof DataInstanceTestCase){
	        		FacesContext context = FacesContext.getCurrentInstance();
	                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Test Case cannot have Test Group. Have been reverted."));
	        		this.createTestPlanTree(this.selectedTestPlan);
	        	}else if(dropNode.getData() instanceof DataInstanceTestStep){
	        		FacesContext context = FacesContext.getCurrentInstance();
	                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Test Step cannot have Test Group. Have been reverted."));
	        		this.createTestPlanTree(this.selectedTestPlan);
	        	}
	        }else if(dragNode.getData() instanceof DataInstanceTestCase){
	        	if(dropNode.getData() instanceof DataInstanceTestPlan){
	        		this.updateTestPlanByTree(dragNode, dropNode, dropIndex);
	        	}else if(dropNode.getData() instanceof DataInstanceTestCaseGroup){
	        		this.updateTestPlanByTree(dragNode, dropNode, dropIndex);
	        	}else if(dropNode.getData() instanceof DataInstanceTestCase){
	        		FacesContext context = FacesContext.getCurrentInstance();
	                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Test Case cannot have another Test Case. Have been reverted."));
	        		this.createTestPlanTree(this.selectedTestPlan);
	        	}else if(dropNode.getData() instanceof DataInstanceTestStep){
	        		FacesContext context = FacesContext.getCurrentInstance();
	                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Test Step cannot have Test Case. Have been reverted."));
	        		this.createTestPlanTree(this.selectedTestPlan);
	        	}
	        }else if(dragNode.getData() instanceof DataInstanceTestStep){
	        	if(dropNode.getData() instanceof DataInstanceTestPlan){
	        		FacesContext context = FacesContext.getCurrentInstance();
	                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Test Plan cannot have Test Step. Have been reverted."));
	        		this.createTestPlanTree(this.selectedTestPlan);
	        	}else if(dropNode.getData() instanceof DataInstanceTestCaseGroup){
	        		FacesContext context = FacesContext.getCurrentInstance();
	                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Test Group cannot have Test Step. Have been reverted."));
	        		this.createTestPlanTree(this.selectedTestPlan);
	        	}else if(dropNode.getData() instanceof DataInstanceTestCase){
	        		this.updateTestPlanByTree(dragNode, dropNode, dropIndex);
	        	}else if(dropNode.getData() instanceof DataInstanceTestStep){
	        		FacesContext context = FacesContext.getCurrentInstance();
	                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Test Step cannot have another Test Step. Have been reverted."));
	        		this.createTestPlanTree(this.selectedTestPlan);
	        	}
	        }
	        
	        this.modifyTestPlan();
		}catch(Exception e){
			FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "FATAL Error", e.toString()));
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
    }
	
	public void resetMessage(){
		try{
			this.selectedTestStep.setMessage(new Message());
			this.jurorDocumentHTML = null;
			this.jurorDocumentPDFFileName = null;
			this.messageContentHTML = null;
			this.testDataSpecificationHTML = null;
			this.modifyTestStep();
		}catch(Exception e){
			FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "FATAL Error", e.toString()));
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}
	
	public boolean checkUsageList(String usageList){
		String[] usageArray = usageList.split("-");
		for(String u:usageArray){
    		if(u.equals("X")){
    			return false;
    		}
    	}
		return true;
	}
	
	public void resetAllTestDataCategorizations() throws Exception{
		this.selectedTestStep.getMessage().setTcamtConstraints(new HashSet<TCAMTConstraint>());
		
		this.constraintTreeRoot = this.manageInstanceService.generateConstraintTree(this.selectedTestStep.getMessage(), this.instanceSegments);
	}
	
	public void removeTCAMTConstraint(Object model){
		if(model instanceof FieldModel){
			FieldModel fModel = (FieldModel)model;
			this.selectedTestStep.getMessage().deleteTCAMTConstraintByIPath(fModel.getIpath());
			fModel.setTdc(null);
		}else if(model instanceof ComponentModel){
			ComponentModel cModel = (ComponentModel)model;
			this.selectedTestStep.getMessage().deleteTCAMTConstraintByIPath(cModel.getIpath());
			cModel.setTdc(null);
		}
	}
	
	public void updateTDC() throws Exception {
		this.resetAllTestDataCategorizations();
		if(this.defaultTDCId != null){
			DefaultTestDataCategorizationSheet sheet = this.sessionBeanTCAMT.getDbManager().getDefaultTestDataCategorizationSheetById(this.defaultTDCId);
			for(InstanceSegment is:this.instanceSegments){
				this.segmentTreeRoot = new DefaultTreeNode("root", null);
				this.manageInstanceService.genSegmentTree(this.segmentTreeRoot, is, this.selectedTestStep.getMessage());
				
				for(TreeNode fNode:this.segmentTreeRoot.getChildren()){
					FieldModel fModel = (FieldModel)fNode.getData();
					String[] paths = fModel.getPath().split("\\.");
					
					String segmentName = paths[paths.length-2];
					Integer fieldPosition = Integer.parseInt(paths[paths.length-1]);
					Integer componentPosition = null;
					Integer subComponentPosition = null;
					
					TestDataCategorization testDataCategorization = this.findTestDataCategorizationFromSheet(sheet, segmentName, fieldPosition, componentPosition, subComponentPosition);
					
					if(testDataCategorization != null){
						fModel.setTdc(testDataCategorization);
						this.createTCAMTConstraint(fModel);
					}
					
					for(TreeNode cNode:fNode.getChildren()){
						ComponentModel cModel = (ComponentModel)cNode.getData();
						
						paths = cModel.getPath().split("\\.");
						
						segmentName = paths[paths.length-3];
						fieldPosition = Integer.parseInt(paths[paths.length-2]);
						componentPosition = Integer.parseInt(paths[paths.length-1]);
						subComponentPosition = null;
						
						testDataCategorization = this.findTestDataCategorizationFromSheet(sheet, segmentName, fieldPosition, componentPosition, subComponentPosition);
						
						if(testDataCategorization != null){
							cModel.setTdc(testDataCategorization);
							this.createTCAMTConstraint(cModel);
						}
						
						for(TreeNode scNode:cNode.getChildren()){
							ComponentModel scModel = (ComponentModel)scNode.getData();
							
							paths = scModel.getPath().split("\\.");
							
							segmentName = paths[paths.length-4];
							fieldPosition = Integer.parseInt(paths[paths.length-3]);
							componentPosition = Integer.parseInt(paths[paths.length-2]);
							subComponentPosition = Integer.parseInt(paths[paths.length-1]);
							
							testDataCategorization = this.findTestDataCategorizationFromSheet(sheet, segmentName, fieldPosition, componentPosition, subComponentPosition);
							
							if(testDataCategorization != null){
								scModel.setTdc(testDataCategorization);
								this.createTCAMTConstraint(scModel);
							}
						}
						
					}
				}
				
				
				
				
			}	
		}
		this.defaultTDCId = null;
		this.activeIndexOfMessageInstancePanel = 3;
		
		this.modifyTestStep();
	}
	
	private TestDataCategorization findTestDataCategorizationFromSheet(DefaultTestDataCategorizationSheet sheet, String segmentName, Integer fieldPosition, Integer componentPosition, Integer subComponentPosition){
		for(DefaultTestDataCategorization dtdc:sheet.getDefaultTestDataCategorizations()){
			if(dtdc.getSegmentName().equals(segmentName)){
				if(dtdc.getFieldPosition() != null && dtdc.getFieldPosition().equals(fieldPosition)){
					if(dtdc.getComponentPosition() != null && dtdc.getComponentPosition().equals(componentPosition)){
						if(dtdc.getSubComponentPosition() != null && dtdc.getSubComponentPosition().equals(subComponentPosition)){
							return dtdc.getCategorization();
						}else if(dtdc.getSubComponentPosition() == null && subComponentPosition == null){
							return dtdc.getCategorization();
						}
					}else if(dtdc.getComponentPosition() == null && dtdc.getSubComponentPosition() == null && componentPosition == null && subComponentPosition == null){
						return dtdc.getCategorization();
					}
				}
			}
		}
		return null;
	}
	
	public void detectChangeEr7Message(AjaxBehaviorEvent event){
		this.selectedInstanceSegment = null;
		this.segmentTreeRoot = new DefaultTreeNode("root", null);
	    this.filtedSegmentTreeRoot = new DefaultTreeNode("root", null);
	    this.modifyTestStep();
	}
	
	public void onTabChange(TabChangeEvent event) throws Exception {
		if(event.getTab().getTitle().equals("Test Step MetaData")){
		
		}else if(event.getTab().getTitle().equals("Test Step Story")){
			
		}else if(event.getTab().getTitle().equals("HL7 Encoded Message")){
			
		}else if(event.getTab().getTitle().equals("Segments List")){
			this.instanceSegments = new ArrayList<InstanceSegment>();
			if(this.selectedTestStep.getMessage().getHl7EndcodedMessage() != null && !this.selectedTestStep.getMessage().getHl7EndcodedMessage().equals("")){
				this.manageInstanceService.loadMessageInstance(this.selectedTestStep.getMessage(), this.instanceSegments, selectedTestCaseName);
			}
			this.updateFilteredInstanceSegments();
		}else if(event.getTab().getTitle().equals("TestData of Selected Segment")){
		
		}else if(event.getTab().getTitle().equals("List Constraints")){
			this.instanceSegments = new ArrayList<InstanceSegment>();
			if(this.selectedTestStep.getMessage().getHl7EndcodedMessage() != null && !this.selectedTestStep.getMessage().getHl7EndcodedMessage().equals("")){
				this.manageInstanceService.loadMessageInstance(this.selectedTestStep.getMessage(), this.instanceSegments, selectedTestCaseName);
			}
			this.constraintTreeRoot = this.manageInstanceService.generateConstraintTree(this.selectedTestStep.getMessage(), this.instanceSegments);
		}else if(event.getTab().getTitle().equals("STD XML Encoded Message")){
			this.instanceSegments = new ArrayList<InstanceSegment>();
			if(this.selectedTestStep.getMessage().getHl7EndcodedMessage() != null && !this.selectedTestStep.getMessage().getHl7EndcodedMessage().equals("")){
				this.manageInstanceService.loadMessageInstance(this.selectedTestStep.getMessage(), this.instanceSegments, selectedTestCaseName);
			}
			this.manageInstanceService.generateXMLFromMesageInstance(this.selectedTestStep.getMessage(), instanceSegments, true, selectedTestCaseName);
		}else if(event.getTab().getTitle().equals("NIST XML Encoded Message")){
			this.instanceSegments = new ArrayList<InstanceSegment>();
			if(this.selectedTestStep.getMessage().getHl7EndcodedMessage() != null && !this.selectedTestStep.getMessage().getHl7EndcodedMessage().equals("")){
				this.manageInstanceService.loadMessageInstance(this.selectedTestStep.getMessage(), this.instanceSegments, selectedTestCaseName);
			}
			this.manageInstanceService.generateXMLFromMesageInstance(this.selectedTestStep.getMessage(), instanceSegments, false, selectedTestCaseName);
		}else if(event.getTab().getTitle().equals("Message Contents")){
			this.instanceSegments = new ArrayList<InstanceSegment>();
			if(this.selectedTestStep.getMessage().getHl7EndcodedMessage() != null && !this.selectedTestStep.getMessage().getHl7EndcodedMessage().equals("")){
				this.manageInstanceService.loadMessageInstance(this.selectedTestStep.getMessage(), this.instanceSegments, selectedTestCaseName);
			}
			this.generateMessageContentHTML();
		}else if(event.getTab().getTitle().equals("Test Data Specification")){
			this.instanceSegments = new ArrayList<InstanceSegment>();
			if(this.selectedTestStep.getMessage().getHl7EndcodedMessage() != null && !this.selectedTestStep.getMessage().getHl7EndcodedMessage().equals("")){
				this.manageInstanceService.loadMessageInstance(this.selectedTestStep.getMessage(), this.instanceSegments, selectedTestCaseName);
			}
			this.manageInstanceService.generateXMLFromMesageInstance(this.selectedTestStep.getMessage(), instanceSegments, false, selectedTestCaseName);
			this.generateTestDataSpecificationHTML();
		}else if(event.getTab().getTitle().equals("Juror Document")){
			this.instanceSegments = new ArrayList<InstanceSegment>();
			if(this.selectedTestStep.getMessage().getHl7EndcodedMessage() != null && !this.selectedTestStep.getMessage().getHl7EndcodedMessage().equals("")){
				this.manageInstanceService.loadMessageInstance(this.selectedTestStep.getMessage(), this.instanceSegments, selectedTestCaseName);
			}
			this.manageInstanceService.generateXMLFromMesageInstance(this.selectedTestStep.getMessage(), instanceSegments, false, selectedTestCaseName);
			this.generateJurorDocumentHTML();
		}else {
		}
    }
	
	public void modifyTestPlan(){
		this.selectedTestPlan.setChanged(true);
	}
	
	public void modifyTestCaseGroup(){
		this.selectedTestPlan.setChanged(true);
		this.selectedTestCaseGroup.setChanged(true);
	}
	
	public void modifyTestCase(){
		this.selectedTestPlan.setChanged(true);
		this.selectedTestCase.setChanged(true);
		if(this.parentTestGroup != null) this.parentTestGroup.setChanged(true);
	}
	
	public void modifyTestStep(){
		this.selectedTestPlan.setChanged(true);
		this.selectedTestStep.setChanged(true);
		this.parentTestCase.setChanged(true);
		if(this.parentTestGroup != null) this.parentTestGroup.setChanged(true);
	}
	
	public void deleteAllTestDataCategorizationForNonLeafNode(){
		List<DataInstanceTestPlan> plans = this.getSessionBeanTCAMT().getDbManager().getAllDataInstanceTestPlans();
		
		for(DataInstanceTestPlan plan : plans){
			for(DataInstanceTestCaseGroup tcg:plan.getTestcasegroups()){
				for(DataInstanceTestCase tc:tcg.getTestcases()){
					for(DataInstanceTestStep ts:tc.getTeststeps()){
						Message m = ts.getMessage();
						this.manageInstanceService.loadMessage(m);
						
						List<TCAMTConstraint> toBeDeletedTCAMTConstraints = new ArrayList<TCAMTConstraint>();
						
						for(TCAMTConstraint c:m.getTcamtConstraints()){
							if(!this.isLeafNode(this.makePathFromIPath(c.getiPosition()), m.getMessageObj(), m.getSegments(), m.getDatatypes())){
								toBeDeletedTCAMTConstraints.add(c);
							}
						}
						
						for(TCAMTConstraint c:toBeDeletedTCAMTConstraints){
							m.getTcamtConstraints().remove(c);
						}
					}
				}
				
				
			}
			for(DataInstanceTestCase tc:plan.getTestcases()){
				for(DataInstanceTestStep ts:tc.getTeststeps()){
					Message m = ts.getMessage();
					this.manageInstanceService.loadMessage(m);
					
					List<TCAMTConstraint> toBeDeletedTCAMTConstraints = new ArrayList<TCAMTConstraint>();
					
					for(TCAMTConstraint c:m.getTcamtConstraints()){
						if(!this.isLeafNode(this.makePathFromIPath(c.getiPosition()), m.getMessageObj(), m.getSegments(), m.getDatatypes())){
							toBeDeletedTCAMTConstraints.add(c);
						}
					}
					
					for(TCAMTConstraint c:toBeDeletedTCAMTConstraints){
						m.getTcamtConstraints().remove(c);
					}
				}
			}
			this.getSessionBeanTCAMT().getDbManager().dataInstanceTestPlanUpdate(plan);
		}
	}
	
	public void deleteAllTestDataCategorizationForNonLeafNodeForTestPlan(){
			for(DataInstanceTestCaseGroup tcg:this.selectedTestPlan.getTestcasegroups()){
				for(DataInstanceTestCase tc:tcg.getTestcases()){
					for(DataInstanceTestStep ts:tc.getTeststeps()){
						Message m = ts.getMessage();
						this.manageInstanceService.loadMessage(m);
						
						List<TCAMTConstraint> toBeDeletedTCAMTConstraints = new ArrayList<TCAMTConstraint>();
						
						for(TCAMTConstraint c:m.getTcamtConstraints()){
							if(!this.isLeafNode(this.makePathFromIPath(c.getiPosition()), m.getMessageObj(), m.getSegments(), m.getDatatypes())){
								toBeDeletedTCAMTConstraints.add(c);
							}
						}
						
						for(TCAMTConstraint c:toBeDeletedTCAMTConstraints){
							m.getTcamtConstraints().remove(c);
						}
					}
				}
				
				
			}
			for(DataInstanceTestCase tc:this.selectedTestPlan.getTestcases()){
				for(DataInstanceTestStep ts:tc.getTeststeps()){
					Message m = ts.getMessage();
					this.manageInstanceService.loadMessage(m);
					
					List<TCAMTConstraint> toBeDeletedTCAMTConstraints = new ArrayList<TCAMTConstraint>();
					
					for(TCAMTConstraint c:m.getTcamtConstraints()){
						if(!this.isLeafNode(this.makePathFromIPath(c.getiPosition()), m.getMessageObj(), m.getSegments(), m.getDatatypes())){
							toBeDeletedTCAMTConstraints.add(c);
						}
					}
					
					for(TCAMTConstraint c:toBeDeletedTCAMTConstraints){
						m.getTcamtConstraints().remove(c);
					}
				}
			}		
	}
	
	public void deleteAllMissingTestDataCategorization(){
		List<TCAMTConstraint> tcamtConstraints = this.getSessionBeanTCAMT().getDbManager().getAllTCAMTConstraints();
		for(TCAMTConstraint c:tcamtConstraints){
			this.getSessionBeanTCAMT().getDbManager().tcamtConstraintDelete(c);
		}
	}
	
	
	private boolean isLeafNode(String path,
			gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message messageObj, Segments segments,
			Datatypes datatypes) {
		
		Object currentNode = messageObj;
		String[] pathList = path.split("\\.");
		for(String location:pathList){
			if(currentNode != null){
				currentNode = this.findChild(location, currentNode, segments, datatypes);
			}
		}
		
		if(currentNode == null){
			return false;
			
		}else if (currentNode instanceof Datatype){
			Datatype dt = (Datatype)currentNode;
			if(dt.getComponents() == null || dt.getComponents().size() == 0){
				return true;
			}
		}
		
		return false;
	}
	
	private Object findChild(String location, Object parentObj, Segments segments, Datatypes datatypes){
		int position = Integer.parseInt(location);
		
		if(parentObj instanceof gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message){
			gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message messageObj = (gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message)parentObj;
			
			for(SegmentRefOrGroup child : messageObj.getChildren()){
				if(child.getPosition() == position){
					if(child instanceof Group){
						return child;
					}else if (child instanceof SegmentRef){
						SegmentRef segmentRef = (SegmentRef)child;
						return segments.findOneSegmentById(segmentRef.getRef());
					}
				}
			}
		}else if(parentObj instanceof Group){
			Group group = (Group)parentObj;
			
			for(SegmentRefOrGroup child : group.getChildren()){
				if(child.getPosition() == position){
					if(child instanceof Group){
						return child;
					}else if (child instanceof SegmentRef){
						SegmentRef segmentRef = (SegmentRef)child;
						return segments.findOneSegmentById(segmentRef.getRef());
					}
				}
			}
		}else if(parentObj instanceof Segment){
			Segment segment = (Segment)parentObj;
			for(Field child:segment.getFields()){
				if(child.getPosition() == position){
					return datatypes.findOne(child.getDatatype());
				}
			}
		}else if(parentObj instanceof Datatype){
			Datatype dt = (Datatype)parentObj;
			for(Component child:dt.getComponents()){
				if(child.getPosition() == position){
					return datatypes.findOne(child.getDatatype());
				}
			}
		}
		
		return null;
	}

	private String makePathFromIPath(String ipath) {
		String[] pathList = ipath.split("\\.");
		String result = "";
		for(String path:pathList){
			result = result + "." + path.substring(0, path.indexOf("["));
		}
		return result.substring(1);
	}
	
	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */

	
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
//		this.messageTreeRoot = new DefaultTreeNode("root", null);
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
		this.jurorDocumentPDFFileName = null;
		this.setMessageContentHTML(null);
		
	}
	
	private void createTestPlanTree(DataInstanceTestPlan tp) {
		this.testplanRoot = new DefaultTreeNode("", null);
		TreeNode testplanNode = new DefaultTreeNode("plan", tp, this.testplanRoot);
		
		testplanNode.setSelected(tp.isSelected());
		tp.setSelected(false);
		testplanNode.setExpanded(tp.isExpanded());
		
		List<DataInstanceTestCaseGroup> sortedDataInstanceTestCaseGroups = new ArrayList<DataInstanceTestCaseGroup>(tp.getTestcasegroups());
		Collections.sort(sortedDataInstanceTestCaseGroups);
		for(DataInstanceTestCaseGroup ditcg:sortedDataInstanceTestCaseGroups){
			TreeNode groupNode = new DefaultTreeNode("group", ditcg, testplanNode);
			
			groupNode.setExpanded(ditcg.isExpanded());
			groupNode.setSelected(ditcg.isSelected());
			ditcg.setSelected(false);
			if(groupNode.isSelected()) this.selectedTestCaseGroup = (DataInstanceTestCaseGroup)groupNode.getData();
			
			List<DataInstanceTestCase> sortedDataInstanceTestCases = new ArrayList<DataInstanceTestCase>(ditcg.getTestcases());
			Collections.sort(sortedDataInstanceTestCases);
			for(DataInstanceTestCase ditc:sortedDataInstanceTestCases){
				TreeNode caseNode = new DefaultTreeNode("case", ditc, groupNode);
				caseNode.setExpanded(ditc.isExpanded());
				caseNode.setSelected(ditc.isSelected());
				ditc.setSelected(false);
				if(caseNode.isSelected()) this.selectedTestCase = (DataInstanceTestCase)caseNode.getData();
				List<DataInstanceTestStep> sortedDataInstanceTestStep = new ArrayList<DataInstanceTestStep>(ditc.getTeststeps());
				Collections.sort(sortedDataInstanceTestStep);
				for(DataInstanceTestStep dits:sortedDataInstanceTestStep){
					TreeNode stepNode = new DefaultTreeNode("step", dits, caseNode);
					stepNode.setSelected(dits.isSelected());
					dits.setSelected(false);
					if(stepNode.isSelected()) this.selectedTestStep = (DataInstanceTestStep)stepNode.getData();
				}
			}
		}
		List<DataInstanceTestCase> sortedDataInstanceTestCases = new ArrayList<DataInstanceTestCase>(tp.getTestcases());
		Collections.sort(sortedDataInstanceTestCases);
		for(DataInstanceTestCase ditc:sortedDataInstanceTestCases){
			TreeNode caseNode = new DefaultTreeNode("case", ditc, testplanNode);
			caseNode.setExpanded(ditc.isExpanded());
			caseNode.setSelected(ditc.isSelected());
			ditc.setSelected(false);
			if(caseNode.isSelected()) this.selectedTestCase = (DataInstanceTestCase)caseNode.getData();
			List<DataInstanceTestStep> sortedDataInstanceTestStep = new ArrayList<DataInstanceTestStep>(ditc.getTeststeps());
			Collections.sort(sortedDataInstanceTestStep);
			for(DataInstanceTestStep dits:sortedDataInstanceTestStep){
				TreeNode stepNode = new DefaultTreeNode("step", dits, caseNode);
				stepNode.setSelected(dits.isSelected());
				dits.setSelected(false);
				if(stepNode.isSelected()) this.selectedTestStep = (DataInstanceTestStep)stepNode.getData();
			}
		}
	}
	
	private String getStackTrace(final Throwable throwable) {
	     final StringWriter sw = new StringWriter();
	     final PrintWriter pw = new PrintWriter(sw, true);
	     throwable.printStackTrace(pw);
	     return sw.getBuffer().toString();
	}

	private void clearSelectNode(TreeNode node){
		node.setSelected(false);
		
		for(TreeNode child:node.getChildren()){
			clearSelectNode(child);
		}	
	}
	
	public void er7MessageValidation() throws Exception {
		
		ValidationMessage vm = new ValidationMessage();
		
		this.hL7V2MessageValidationReport = vm.er7MessageValidation(this.selectedTestStep.getMessage().getHl7EndcodedMessage(),
								this.selectedTestStep.getMessage().getConformanceProfile().getIntegratedProfile().getProfile(),
								this.selectedTestStep.getMessage().getConformanceProfile().getIntegratedProfile().getConstraints(),
								this.selectedTestStep.getMessage().getConformanceProfile().getIntegratedProfile().getValueSet(),
								this.selectedTestStep.getMessage().getConformanceProfile().getConformanceProfileId());
	}
	
	private void selectTestStep() throws Exception {
		this.selectedTestCaseGroup = null;
		this.testDataSpecificationHTML = null;
		this.jurorDocumentHTML = null;
		this.jurorDocumentPDFFileName = null;
		this.setMessageContentHTML(null);
		
		this.selectedInstanceSegment = null;
//		this.messageTreeRoot = new DefaultTreeNode("root", null);
		this.segmentTreeRoot = new DefaultTreeNode("root", null);
		this.filtedSegmentTreeRoot = new DefaultTreeNode("root", null);
		this.setConstraintTreeRoot(new DefaultTreeNode("root", null));
		this.instanceSegments = new ArrayList<InstanceSegment>();
		this.filteredInstanceSegments =  new ArrayList<InstanceSegment>();
		this.manageInstanceService = new ManageInstance();
		this.hL7V2MessageValidationReport = null;
		
		if(this.selectedTestStep.getMessage() != null && this.selectedTestStep.getMessage().getConformanceProfile() != null && this.selectedTestStep.getMessage().getName() != null){
//			this.messageTreeRoot = this.manageInstanceService.loadMessage(this.selectedTestStep.getMessage());
			this.manageInstanceService.loadMessage(this.selectedTestStep.getMessage());
		}
	}
	
	private void generateMessageContentHTML() throws Exception{
		if(this.selectedTestStep.getMessage().getConformanceProfile().getMessageContentXSLT() != null){
			this.manageInstanceService.generateXMLforMessageContentAndUpdateTestData(this.selectedTestStep.getMessage(), this.instanceSegments);
			
			String mcXSL = this.selectedTestStep.getMessage().getConformanceProfile().getMessageContentXSLT().replaceAll("<xsl:param name=\"output\" select=\"'ng-tab-html'\"/>", "<xsl:param name=\"output\" select=\"'plain-html'\"/>");
			InputStream xsltInputStream = new ByteArrayInputStream(mcXSL.getBytes());
			InputStream sourceInputStream = new ByteArrayInputStream(this.selectedTestStep.getMessage().getXmlEncodedMessageContent().getBytes());
			Reader xsltReader =  new InputStreamReader(xsltInputStream, "UTF-8");
			Reader sourceReader = new InputStreamReader(sourceInputStream, "UTF-8");
			String xsltStr = IOUtils.toString(xsltReader);
			String sourceStr = IOUtils.toString(sourceReader);
			
			
			this.messageContentHTML = XMLManager.parseXmlByXSLT(sourceStr, xsltStr);	
		}	
		
		
	}
	
	private void generateJurorDocumentHTML() throws IOException {
		if(this.selectedTestPlan.isJurorDocumentEnable()){
			if(this.selectedTestStep.getMessage().getConformanceProfile().getJurorDocumentXSLT() != null){
				InputStream xsltInputStream = null;
				if(this.selectedTestPlan.getSpecificJurorDocument() == null){
					xsltInputStream = new ByteArrayInputStream(this.selectedTestStep.getMessage().getConformanceProfile().getJurorDocumentXSLT().getBytes());
				}else {
					xsltInputStream = new ByteArrayInputStream(this.selectedTestPlan.getSpecificJurorDocument().getJurorDocumentHTML().getBytes());
				}
				
				InputStream sourceInputStream = new ByteArrayInputStream(this.selectedTestStep.getMessage().getXmlEncodedNISTMessage().getBytes());
				Reader xsltReader =  new InputStreamReader(xsltInputStream, "UTF-8");
				Reader sourceReader = new InputStreamReader(sourceInputStream, "UTF-8");
				String xsltStr = IOUtils.toString(xsltReader);
				String sourceStr = IOUtils.toString(sourceReader);
				
				this.jurorDocumentHTML = XMLManager.parseXmlByXSLT(sourceStr, xsltStr);
				
				
				this.jurorDocumentPDFFileName = this.htmlStringToPDF2(this.jurorDocumentHTML) + ".pdf";
			}
		}
	}
	
	private void generateTestDataSpecificationHTML() throws IOException {
		if(this.selectedTestStep.getMessage().getConformanceProfile().getTestDataSpecificationXSLT() != null){
			String tdXSL = this.selectedTestStep.getMessage().getConformanceProfile().getTestDataSpecificationXSLT().replaceAll("<xsl:param name=\"output\" select=\"'ng-tab-html'\"/>", "<xsl:param name=\"output\" select=\"'plain-html'\"/>");
			InputStream xsltInputStream = new ByteArrayInputStream(tdXSL.getBytes());
			InputStream sourceInputStream = new ByteArrayInputStream(this.selectedTestStep.getMessage().getXmlEncodedNISTMessage().getBytes());
			Reader xsltReader =  new InputStreamReader(xsltInputStream, "UTF-8");
			Reader sourceReader = new InputStreamReader(sourceInputStream, "UTF-8");
			String xsltStr = IOUtils.toString(xsltReader);
			String sourceStr = IOUtils.toString(sourceReader);
			this.testDataSpecificationHTML = XMLManager.parseXmlByXSLT(sourceStr, xsltStr);	
		}			
	}
	
	private void generateIsolatedRB(ZipOutputStream out, DataInstanceTestPlan tp, boolean needPDF) throws Exception {
		this.generateTestPlanJsonRB(out, tp);
		for(DataInstanceTestCaseGroup ditg:tp.getTestcasegroups()){
			String groupPath = "TestGroup_" + ditg.getPosition();
			this.generateTestGroupJsonRB(out, ditg, groupPath);
			for(DataInstanceTestCase ditc:ditg.getTestcases()){
				String testcasePath = groupPath + File.separator + "TestCase_" + ditc.getPosition();
				this.generateTestCaseJsonRB(out, ditc, testcasePath);
				this.generateTestStoryRB(out, ditc.getTestCaseStory(), testcasePath, needPDF);
				for(DataInstanceTestStep dits:ditc.getTeststeps()){
					String teststepPath = testcasePath + File.separator + "TestStep_" + dits.getPosition();
					this.generateTestStoryRB(out, dits.getTestStepStory(), teststepPath, needPDF);
					this.generateTestStepJsonRB(out, dits, teststepPath);
					this.instanceSegments = new ArrayList<InstanceSegment>();
					if(dits != null && dits.getMessage() != null && dits.getMessage().getConformanceProfile() != null){
						if(dits.getMessage().getHl7EndcodedMessage() != null && !dits.getMessage().getHl7EndcodedMessage().equals("")){
							this.manageInstanceService.loadMessage(dits.getMessage());
							this.manageInstanceService.loadMessageInstance(dits.getMessage(), this.instanceSegments, ditc.getName());
						}
						
						this.generateMessageRB(out, dits.getMessage().getHl7EndcodedMessage(), teststepPath);
						this.generateMessageContentRB(out, dits.getMessage(), teststepPath, needPDF);
						this.generateMessageContentJSONRB(out, dits.getMessage(), teststepPath);
						this.generateTestDataSpecificationRB(out, dits.getMessage(), teststepPath, needPDF);
						this.generateTestDataSpecificationJSONRB(out, dits.getMessage(), teststepPath);
						this.generateJurorDocumentRB(out, dits.getMessage(), teststepPath, tp, needPDF);
						this.generateJurorDocumentJSONRB(out, dits.getMessage(), teststepPath, tp);
						this.generateTestStepConstraintsRB(out, dits.getMessage(), teststepPath);
					}
				}
			}
		}
		
		for(DataInstanceTestCase ditc:tp.getTestcases()){
			String testcasePath = "TestCase_" +  ditc.getPosition();
			this.generateTestStoryRB(out, ditc.getTestCaseStory(), testcasePath, needPDF);
			this.generateTestCaseJsonRB(out, ditc, testcasePath);
			for(DataInstanceTestStep dits:ditc.getTeststeps()){
				String teststepPath = testcasePath + File.separator + "TestStep_" + dits.getPosition();
				this.generateTestStoryRB(out, dits.getTestStepStory(), teststepPath, needPDF);
				this.generateTestStepJsonRB(out, dits, teststepPath);
				this.instanceSegments = new ArrayList<InstanceSegment>();
				if(dits != null && dits.getMessage() != null && dits.getMessage().getConformanceProfile() != null){
					if(dits.getMessage().getHl7EndcodedMessage() != null && !dits.getMessage().getHl7EndcodedMessage().equals("")){
						this.manageInstanceService.loadMessage(dits.getMessage());
						this.manageInstanceService.loadMessageInstance(dits.getMessage(), this.instanceSegments, ditc.getName());
					}
					
					this.generateMessageRB(out, dits.getMessage().getHl7EndcodedMessage(), teststepPath);
					this.generateMessageContentRB(out, dits.getMessage(), teststepPath, needPDF);
					this.generateMessageContentJSONRB(out, dits.getMessage(), teststepPath);
					this.generateTestDataSpecificationRB(out, dits.getMessage(), teststepPath, needPDF);
					this.generateTestDataSpecificationJSONRB(out, dits.getMessage(), teststepPath);
					this.generateJurorDocumentRB(out, dits.getMessage(), teststepPath, tp, needPDF);
					this.generateJurorDocumentJSONRB(out, dits.getMessage(), teststepPath, tp);
					this.generateTestStepConstraintsRB(out, dits.getMessage(), teststepPath);
				}
			}
		}
	}
	
	private void generateContextBasedRB(ZipOutputStream out, DataInstanceTestPlan tp, boolean needPDF) throws Exception {
		this.generateTestPlanJsonRB(out, tp);
		
		for(DataInstanceTestCaseGroup ditg:tp.getTestcasegroups()){
			String groupPath = "TestGroup_" + ditg.getPosition();
			this.generateTestGroupJsonRB(out, ditg, groupPath);
			for(DataInstanceTestCase ditc:ditg.getTestcases()){
				String testcasePath = groupPath + File.separator + "TestCase_" + ditc.getPosition();
				this.generateTestCaseJsonRB(out, ditc, testcasePath);
				this.generateTestStoryRB(out, ditc.getTestCaseStory(), testcasePath, needPDF);
				for(DataInstanceTestStep dits:ditc.getTeststeps()){
					String teststepPath = testcasePath + File.separator + "TestStep_" + dits.getPosition();
					this.generateTestStoryRB(out, dits.getTestStepStory(), teststepPath, needPDF);
					this.generateTestStepJsonRB(out, dits, teststepPath);
					this.instanceSegments = new ArrayList<InstanceSegment>();
					if(dits != null && dits.getMessage() != null && dits.getMessage().getConformanceProfile() != null){
						if(dits.getMessage().getHl7EndcodedMessage() != null && !dits.getMessage().getHl7EndcodedMessage().equals("")){
							this.manageInstanceService.loadMessage(dits.getMessage());
							this.manageInstanceService.loadMessageInstance(dits.getMessage(), this.instanceSegments, ditc.getName());
						}
						
						this.generateMessageRB(out, dits.getMessage().getHl7EndcodedMessage(), teststepPath);
						this.generateMessageContentRB(out, dits.getMessage(), teststepPath, needPDF);
						this.generateMessageContentJSONRB(out, dits.getMessage(), teststepPath);
						this.generateTestDataSpecificationRB(out, dits.getMessage(), teststepPath, needPDF);
						this.generateTestDataSpecificationJSONRB(out, dits.getMessage(), teststepPath);
						this.generateJurorDocumentRB(out, dits.getMessage(), teststepPath, tp, needPDF);
						this.generateJurorDocumentJSONRB(out, dits.getMessage(), teststepPath, tp);
						this.generateTestStepConstraintsRB(out, dits.getMessage(), teststepPath);
					}
				}
			}
		}
		
		for(DataInstanceTestCase ditc:tp.getTestcases()){
			String testcasePath = "TestCase_" +  ditc.getPosition();
			this.generateTestStoryRB(out, ditc.getTestCaseStory(), testcasePath, needPDF);
			this.generateTestCaseJsonRB(out, ditc, testcasePath);
			for(DataInstanceTestStep dits:ditc.getTeststeps()){
				String teststepPath = testcasePath + File.separator + "TestStep_" + dits.getPosition();
				this.generateTestStoryRB(out, dits.getTestStepStory(), teststepPath, needPDF);
				this.generateTestStepJsonRB(out, dits, teststepPath);
				this.instanceSegments = new ArrayList<InstanceSegment>();
				if(dits != null && dits.getMessage() != null && dits.getMessage().getConformanceProfile() != null){
					if(dits.getMessage().getHl7EndcodedMessage() != null && !dits.getMessage().getHl7EndcodedMessage().equals("")){
						this.manageInstanceService.loadMessage(dits.getMessage());
						this.manageInstanceService.loadMessageInstance(dits.getMessage(), this.instanceSegments, ditc.getName());
					}
					
					this.generateMessageRB(out, dits.getMessage().getHl7EndcodedMessage(), teststepPath);
					this.generateMessageContentRB(out, dits.getMessage(), teststepPath, needPDF);
					this.generateMessageContentJSONRB(out, dits.getMessage(), teststepPath);
					this.generateTestDataSpecificationRB(out, dits.getMessage(), teststepPath, needPDF);
					this.generateTestDataSpecificationJSONRB(out, dits.getMessage(), teststepPath);
					this.generateJurorDocumentRB(out, dits.getMessage(), teststepPath, tp, needPDF);
					this.generateJurorDocumentJSONRB(out, dits.getMessage(), teststepPath, tp);
					this.generateTestStepConstraintsRB(out, dits.getMessage(), teststepPath);
				}
			}
		}
	}
	
	private void generateTestStepJsonRB(ZipOutputStream out, DataInstanceTestStep dits, String teststepPath) throws IOException, ConversionException {
		byte[] buf = new byte[1024];
		out.putNextEntry(new ZipEntry(teststepPath + File.separator + "TestStep.json"));
		
		if(dits != null && dits.getMessage() != null && dits.getMessage().getConformanceProfile() != null){
			ProfileContainer hl7v2 = new ProfileContainer();
			hl7v2.setMessageId(dits.getMessage().getConformanceProfile().getConformanceProfileId());
			hl7v2.setConstraintId(dits.getMessage().getConformanceProfile().getConstraintId());
			hl7v2.setValueSetLibraryId(dits.getMessage().getConformanceProfile().getValueSetLibraryId());
			dits.setHl7v2(hl7v2);
		}
		
		InputStream inTP = null;
		
		if(dits.getType() != null && dits.getType().contains("MANUAL")){
			ManualTestStep mts = new ManualTestStep();
			mts.setDescription(dits.getLongDescription());
			mts.setName(dits.getName());
			mts.setType(dits.getType());
			mts.setPosition(dits.getPosition());
			inTP = IOUtils.toInputStream(this.mtsConverter.toString(mts));
		}else {
			inTP = IOUtils.toInputStream(this.tsConverter.toString(dits));
		}
		int lenTP;
        while ((lenTP = inTP.read(buf)) > 0) {
            out.write(buf, 0, lenTP);
        }
        out.closeEntry();
        inTP.close();
	}

	private void generateTestCaseJsonRB(ZipOutputStream out, DataInstanceTestCase ditc, String testcasePath) throws IOException, ConversionException {
		byte[] buf = new byte[1024];
		out.putNextEntry(new ZipEntry(testcasePath + File.separator + "TestCase.json"));

		InputStream inTP = IOUtils.toInputStream(this.tcConverter.toString(ditc));
		int lenTP;
        while ((lenTP = inTP.read(buf)) > 0) {
            out.write(buf, 0, lenTP);
        }
        out.closeEntry();
        inTP.close();
	}

	private void generateTestGroupJsonRB(ZipOutputStream out, DataInstanceTestCaseGroup ditg, String groupPath) throws IOException, ConversionException {
		byte[] buf = new byte[1024];
		out.putNextEntry(new ZipEntry(groupPath + File.separator + "TestCaseGroup.json"));
		InputStream inTP = IOUtils.toInputStream(this.tgConverter.toString(ditg));
		int lenTP;
        while ((lenTP = inTP.read(buf)) > 0) {
            out.write(buf, 0, lenTP);
        }
        out.closeEntry();
        inTP.close();
	}

	private void generateJurorDocumentJSONRB(ZipOutputStream out, Message m, String path, DataInstanceTestPlan tp) throws IOException{
		if(tp.isJurorDocumentEnable()){
			if(m.getConformanceProfile().getJurorDocumentJSONXSLT() != null){
				InputStream xsltInputStream = null;
				if(tp.getSpecificJurorDocument() == null){
					xsltInputStream = new ByteArrayInputStream(m.getConformanceProfile().getJurorDocumentJSONXSLT().getBytes());
				}else {
					xsltInputStream = new ByteArrayInputStream(tp.getSpecificJurorDocument().getJurorDocumentJSON().getBytes());
				}
				InputStream sourceInputStream = new ByteArrayInputStream(m.getXmlEncodedNISTMessage().getBytes());
				Reader xsltReader =  new InputStreamReader(xsltInputStream, "UTF-8");
				Reader sourceReader = new InputStreamReader(sourceInputStream, "UTF-8");

				String xsltStr = IOUtils.toString(xsltReader);
				String sourceStr = IOUtils.toString(sourceReader);
				String jurorDocumentJSONStr = XMLManager.parseXmlByXSLT(sourceStr, xsltStr);
				if(jurorDocumentJSONStr != null && !jurorDocumentJSONStr.equals("")){
					byte[] buf = new byte[1024];
					out.putNextEntry(new ZipEntry(path + File.separator + "JurorDocument.json"));
			        JSONObject xmlJSONObj = XML.toJSONObject(jurorDocumentJSONStr);
			        InputStream inJurorDocument = IOUtils.toInputStream(xmlJSONObj.toString(), "UTF-8");
			        int lenJurorDocument;
			        while ((lenJurorDocument = inJurorDocument.read(buf)) > 0) {
			            out.write(buf, 0, lenJurorDocument);
			        }
			        inJurorDocument.close();
			        out.closeEntry();
				}
			}
		}
	}

	private void generateJurorDocumentRB(ZipOutputStream out, Message m, String path, DataInstanceTestPlan tp, boolean needPDF) throws IOException{
		if(tp.isJurorDocumentEnable()){
			if(m.getConformanceProfile().getJurorDocumentXSLT() != null){
				InputStream xsltInputStream = null;
				if(tp.getSpecificJurorDocument() == null){
					xsltInputStream = new ByteArrayInputStream(m.getConformanceProfile().getJurorDocumentXSLT().getBytes());
				}else {
					xsltInputStream = new ByteArrayInputStream(tp.getSpecificJurorDocument().getJurorDocumentHTML().getBytes());
				}
				InputStream sourceInputStream = new ByteArrayInputStream(m.getXmlEncodedNISTMessage().getBytes());
				Reader xsltReader =  new InputStreamReader(xsltInputStream, "UTF-8");
				Reader sourceReader = new InputStreamReader(sourceInputStream, "UTF-8");
				String xsltStr = IOUtils.toString(xsltReader);
				String sourceStr = IOUtils.toString(sourceReader);
				String jurorDocumentHTMLStr = XMLManager.parseXmlByXSLT(sourceStr, xsltStr);
				
				if(jurorDocumentHTMLStr != null && !jurorDocumentHTMLStr.equals("")){
					byte[] buf = new byte[1024];
					out.putNextEntry(new ZipEntry(path + File.separator + "JurorDocument.html"));
			        InputStream inJurorDocument = IOUtils.toInputStream(jurorDocumentHTMLStr, "UTF-8");
			        int lenJurorDocument;
			        while ((lenJurorDocument = inJurorDocument.read(buf)) > 0) {
			            out.write(buf, 0, lenJurorDocument);
			        }
			        inJurorDocument.close();
			        out.closeEntry();
			        if(needPDF){
			        	out.putNextEntry(new ZipEntry(path + File.separator + "JurorDocument.pdf"));
				        
				        String tempFileName = this.htmlStringToPDF(jurorDocumentHTMLStr);
				        File zipFile = new File(tempFileName + ".pdf");	        
				        FileInputStream inJurorDocumentPDF = new FileInputStream(zipFile);

				        int lenJurorDocumentPDF;
				        while ((lenJurorDocumentPDF = inJurorDocumentPDF.read(buf)) > 0) {
				            out.write(buf, 0, lenJurorDocumentPDF);
				        }
				        inJurorDocumentPDF.close();
				        out.closeEntry();
				        this.fileDelete(tempFileName + ".html");
				        this.fileDelete(tempFileName + ".pdf");
			        }
				}
			}
		}
	}
	
	private void generateTestDataSpecificationJSONRB(ZipOutputStream out, Message m, String path) throws IOException{
		if(m.getConformanceProfile().getTestDataSpecificationJSONXSLT() != null){
			InputStream xsltInputStream = new ByteArrayInputStream(m.getConformanceProfile().getTestDataSpecificationJSONXSLT().getBytes());
			InputStream sourceInputStream = new ByteArrayInputStream(m.getXmlEncodedNISTMessage().getBytes());
			Reader xsltReader =  new InputStreamReader(xsltInputStream, "UTF-8");
			Reader sourceReader = new InputStreamReader(sourceInputStream, "UTF-8");
			String xsltStr = IOUtils.toString(xsltReader);
			String sourceStr = IOUtils.toString(sourceReader);
			
			String testDataSpecificationJSONStr = XMLManager.parseXmlByXSLT(sourceStr, xsltStr);
			
			if(testDataSpecificationJSONStr != null && !testDataSpecificationJSONStr.equals("")){
				byte[] buf = new byte[1024];
				
				out.putNextEntry(new ZipEntry(path + File.separator + "TestDataSpecification.json"));
				
		        InputStream inTestDataSpecification = IOUtils.toInputStream(testDataSpecificationJSONStr, "UTF-8");
		        int lenTestDataSpecification;
		        while ((lenTestDataSpecification = inTestDataSpecification.read(buf)) > 0) {
		            out.write(buf, 0, lenTestDataSpecification);
		        }
		        inTestDataSpecification.close();
		        out.closeEntry();
			}
			
		}
	}
	
	private void generateTestDataSpecificationRB(ZipOutputStream out, Message m, String path, boolean needPDF) throws IOException{
		if(m.getConformanceProfile().getTestDataSpecificationXSLT() != null){
			InputStream xsltInputStream = new ByteArrayInputStream(m.getConformanceProfile().getTestDataSpecificationXSLT().getBytes());
			InputStream sourceInputStream = new ByteArrayInputStream(m.getXmlEncodedNISTMessage().getBytes());
			Reader xsltReader =  new InputStreamReader(xsltInputStream, "UTF-8");
			Reader sourceReader = new InputStreamReader(sourceInputStream, "UTF-8");
			String xsltStr = IOUtils.toString(xsltReader);
			String sourceStr = IOUtils.toString(sourceReader);
			
			String testDataSpecificationHTMLStr = XMLManager.parseXmlByXSLT(sourceStr, xsltStr);
			
			if(testDataSpecificationHTMLStr != null && !testDataSpecificationHTMLStr.equals("")){
				byte[] buf = new byte[1024];
				
				out.putNextEntry(new ZipEntry(path + File.separator + "TestDataSpecification.html"));
				
		        InputStream inTestDataSpecification = IOUtils.toInputStream(testDataSpecificationHTMLStr, "UTF-8");
		        int lenTestDataSpecification;
		        while ((lenTestDataSpecification = inTestDataSpecification.read(buf)) > 0) {
		            out.write(buf, 0, lenTestDataSpecification);
		        }
		        inTestDataSpecification.close();
		        out.closeEntry();
		        
		        if(needPDF){
		        	out.putNextEntry(new ZipEntry(path + File.separator + "TestDataSpecification.pdf"));
			        
			        String tempFileName = this.htmlStringToPDF(testDataSpecificationHTMLStr);
			        File zipFile = new File(tempFileName + ".pdf");	        
			        FileInputStream inTestDataSpecificationPDF = new FileInputStream(zipFile);
			        
			        int lenTestDataSpecificationPDF;
			        while ((lenTestDataSpecificationPDF = inTestDataSpecificationPDF.read(buf)) > 0) {
			            out.write(buf, 0, lenTestDataSpecificationPDF);
			        }
			        inTestDataSpecificationPDF.close();
			        out.closeEntry();
			        
			        this.fileDelete(tempFileName + ".html");
			        this.fileDelete(tempFileName + ".pdf");
		        }
			}
		}
	}
	
	private void generateMessageContentRB(ZipOutputStream out, Message m, String path, boolean needPDF) throws Exception{
		if(m.getConformanceProfile().getMessageContentXSLT() != null){
			this.manageInstanceService.generateXMLforMessageContentAndUpdateTestData(m, this.instanceSegments);
			InputStream xsltInputStream = new ByteArrayInputStream(m.getConformanceProfile().getMessageContentXSLT().getBytes());
			InputStream sourceInputStream = new ByteArrayInputStream(m.getXmlEncodedMessageContent().getBytes());
			Reader xsltReader =  new InputStreamReader(xsltInputStream, "UTF-8");
			Reader sourceReader = new InputStreamReader(sourceInputStream, "UTF-8");
			String xsltStr = IOUtils.toString(xsltReader);
			String sourceStr = IOUtils.toString(sourceReader);
			
			String messageContentHTMLStr = XMLManager.parseXmlByXSLT(sourceStr, xsltStr);
			
			if(messageContentHTMLStr != null && !messageContentHTMLStr.equals("")){
				byte[] buf = new byte[1024];
				
				out.putNextEntry(new ZipEntry(path + File.separator + "MessageContent.html"));
				
		        InputStream inMessageContent = IOUtils.toInputStream(messageContentHTMLStr, "UTF-8");
		        int lenMessageContent;
		        while ((lenMessageContent = inMessageContent.read(buf)) > 0) {
		            out.write(buf, 0, lenMessageContent);
		        }
		        inMessageContent.close();
		        out.closeEntry();
		        
		        if(needPDF){
		        	out.putNextEntry(new ZipEntry(path + File.separator + "MessageContent.pdf"));
			        
			        String tempFileName = this.htmlStringToPDF(messageContentHTMLStr);
			        File zipFile = new File(tempFileName + ".pdf");	        
			        FileInputStream inMessageContentPDF = new FileInputStream(zipFile);
			        
			        int lenMessageContentPDF;
			        while ((lenMessageContentPDF = inMessageContentPDF.read(buf)) > 0) {
			            out.write(buf, 0, lenMessageContentPDF);
			        }
			        inMessageContentPDF.close();
			        out.closeEntry();
			        
			        this.fileDelete(tempFileName + ".html");
			        this.fileDelete(tempFileName + ".pdf");
		        }
			}
		}
	}
	
	private void generateMessageContentJSONRB(ZipOutputStream out, Message m, String path) throws Exception{
		if(m.getConformanceProfile().getMessageContentJSONXSLT() != null){
			this.manageInstanceService.generateXMLforMessageContentAndUpdateTestData(m, this.instanceSegments);
			InputStream xsltInputStream = new ByteArrayInputStream(m.getConformanceProfile().getMessageContentJSONXSLT().getBytes());
			InputStream sourceInputStream = new ByteArrayInputStream(m.getXmlEncodedMessageContent().getBytes());
			Reader xsltReader =  new InputStreamReader(xsltInputStream, "UTF-8");
			Reader sourceReader = new InputStreamReader(sourceInputStream, "UTF-8");
			String xsltStr = IOUtils.toString(xsltReader);
			String sourceStr = IOUtils.toString(sourceReader);
			
			String messageContentJSONStr = XMLManager.parseXmlByXSLT(sourceStr, xsltStr);
			
			if(messageContentJSONStr != null && !messageContentJSONStr.equals("")){
				byte[] buf = new byte[1024];
				
				out.putNextEntry(new ZipEntry(path + File.separator + "MessageContent.json"));
				
		        InputStream inMessageContent = IOUtils.toInputStream(messageContentJSONStr, "UTF-8");
		        int lenMessageContent;
		        while ((lenMessageContent = inMessageContent.read(buf)) > 0) {
		            out.write(buf, 0, lenMessageContent);
		        }
		        inMessageContent.close();
		        out.closeEntry();
			}
			
		}
	}

//	private void generateMetaDataRB(ZipOutputStream out, Metadata md) throws IOException, ConversionException {
//		byte[] buf = new byte[1024];
//		out.putNextEntry(new ZipEntry("About" + File.separator + "Metadata.json"));
//		InputStream inMetadata = IOUtils.toInputStream(this.metadataConverter.toString(md));
//		int lenMeta;
//        while ((lenMeta = inMetadata.read(buf)) > 0) {
//            out.write(buf, 0, lenMeta);
//        }
//        out.closeEntry();
//        inMetadata.close();
//	}
	
	private void generateTestStepConstraintsRB(ZipOutputStream out, Message m, String path) throws Exception {
		byte[] buf = new byte[1024];
		out.putNextEntry(new ZipEntry(path + File.separator + "Constraints.xml"));
		String constraintsStr = this.manageInstanceService.generateConstraintDocument(m);
		if(constraintsStr != null){
			InputStream inMessage = IOUtils.toInputStream(constraintsStr,"UTF-8");
			int lenMessage;
            while ((lenMessage = inMessage.read(buf)) > 0) {
                out.write(buf, 0, lenMessage);
            }
            inMessage.close();
		}
        out.closeEntry();
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
	
	private String htmlStringToPDF(String htmlStr) throws IOException{
		ClassLoader classLoader = this.getClass().getClassLoader();
		File file = new File(classLoader.getResource("TestStory.html").getFile());
		
		String fileName =file.getParent() + File.separator + UUID.randomUUID().toString();
		FileUtils.writeStringToFile(new File(fileName + ".html"), htmlStr);
        ProcessBuilder pb = new ProcessBuilder("/usr/local/bin/wkhtmltopdf" , fileName + ".html" , fileName + ".pdf");
        pb.redirectErrorStream(true);
        Process process = pb.start();
        BufferedReader inStreamReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
       
        String line = inStreamReader.readLine();
       
        while(line != null)
        {
            line = inStreamReader.readLine();
        }
        return fileName;
	}
	
	private String htmlStringToPDF2(String htmlStr) throws IOException{
		ClassLoader classLoader = this.getClass().getClassLoader();
		File file = new File(classLoader.getResource("TestStory.html").getFile());
		File parentFile = new File(file.getParent());
		File grandParentFile = new File(parentFile.getParent());
		String fileName = UUID.randomUUID().toString();
		String fileFullName = grandParentFile.getParent() + File.separator + "temp" + File.separator + fileName;
		FileUtils.writeStringToFile(new File(fileFullName + ".html"), htmlStr);
        ProcessBuilder pb = new ProcessBuilder("/usr/local/bin/wkhtmltopdf" , fileFullName + ".html" , fileFullName + ".pdf");
        pb.redirectErrorStream(true);
        Process process = pb.start();
        BufferedReader inStreamReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
       
        String line = inStreamReader.readLine();
       
        while(line != null)
        {
            line = inStreamReader.readLine();
        }
        
        return fileName;
	}
	
	private void fileDelete(String fileName){
		File file = new File(fileName); 
		file.delete();
	}
	
	private void generateTestStoryRB(ZipOutputStream out, TestStory testStory, String path, boolean needPDF) throws Exception{
		byte[] buf = new byte[1024];
		
		out.putNextEntry(new ZipEntry(path + File.separator + "TestStory.json"));
		InputStream inTS = IOUtils.toInputStream(this.testStoryConverter.toString(testStory));
		int lenTS;
        while ((lenTS = inTS.read(buf)) > 0) {
            out.write(buf, 0, lenTS);
        }
        out.closeEntry();
        inTS.close();
		
		out.putNextEntry(new ZipEntry(path + File.separator + "TestStory.html"));
		
		String testCaseStoryStr = this.generateTestStory(testStory, "ng-tab-html");
        InputStream inTestStory = IOUtils.toInputStream(testCaseStoryStr, "UTF-8");
        int lenTestStory;
        while ((lenTestStory = inTestStory.read(buf)) > 0) {
            out.write(buf, 0, lenTestStory);
        }
        inTestStory.close();
        out.closeEntry();
        
        if(needPDF){
        	out.putNextEntry(new ZipEntry(path + File.separator + "TestStory.pdf"));
        	String testCaseStoryPDFStr = this.generateTestStory(testStory, "plain");
            String tempFileName = this.htmlStringToPDF(testCaseStoryPDFStr);
            File zipFile = new File(tempFileName + ".pdf");	        
            FileInputStream inTestStoryPDF = new FileInputStream(zipFile);
            int lenTestStoryPDF;
            while ((lenTestStoryPDF = inTestStoryPDF.read(buf)) > 0) {
                out.write(buf, 0, lenTestStoryPDF);
            }
            inTestStoryPDF.close();
            out.closeEntry();
            
            this.fileDelete(tempFileName + ".html");
            this.fileDelete(tempFileName + ".pdf");        	
        }else{
        	out.putNextEntry(new ZipEntry(path + File.separator + "TestStoryPDF.html"));
    		
    		String testCaseStoryPDFStr = this.generateTestStory(testStory, "plain");
            InputStream inTestStoryPDFHTML = IOUtils.toInputStream(testCaseStoryPDFStr, "UTF-8");
            int lenTestStoryPDFHTML;
            while ((lenTestStoryPDFHTML = inTestStoryPDFHTML.read(buf)) > 0) {
                out.write(buf, 0, lenTestStoryPDFHTML);
            }
            inTestStoryPDFHTML.close();
            out.closeEntry();
        }
	}
	
	private String generateTestStory(TestStory testStory, String option) throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		String xsltStr;
		
		if(option.equals("ng-tab-html")){
			xsltStr = IOUtils.toString(classLoader.getResourceAsStream("TestStory_ng-tab-html.xsl"));
		}else{
			xsltStr = IOUtils.toString(classLoader.getResourceAsStream("TestStory_plain-html.xsl"));
		}
		
		String sourceStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + 
						"<TestStory>" + 
							"<comments><![CDATA["+ testStory.getComments().replace("&amp;", "&").replace("&lt;", "<").replace("&gt;", ">").replace("&nbsp;", " ") + "]]></comments>" +
							"<postCondition><![CDATA[" + testStory.getPostCondition().replace("&amp;", "&").replace("&lt;", "<").replace("&gt;", ">").replace("&nbsp;", " ") + "]]></postCondition>" +
							"<notes><![CDATA[" + testStory.getNotes().replace("&amp;", "&").replace("&lt;", "<").replace("&gt;", ">").replace("&nbsp;", " ") + "]]></notes>" + 
							"<teststorydesc><![CDATA[" + testStory.getTeststorydesc().replace("&amp;", "&").replace("&lt;", "<").replace("&gt;", ">").replace("&nbsp;", " ") + "]]></teststorydesc>" + 
							"<evaluationCriteria><![CDATA[" + testStory.getEvaluationCriteria().replace("&amp;", "&").replace("&lt;", "<").replace("&gt;", ">").replace("&nbsp;", " ") + "]]></evaluationCriteria>" + 
							"<preCondition><![CDATA[" + testStory.getPreCondition().replace("&amp;", "&").replace("&lt;", "<").replace("&gt;", ">").replace("&nbsp;", " ") + "]]></preCondition>" + 
							"<testObjectives><![CDATA[" + testStory.getTestObjectives().replace("&amp;", "&").replace("&lt;", "<").replace("&gt;", ">").replace("&nbsp;", " ") + "]]></testObjectives>" + 
						"</TestStory>";
		sourceStr = XMLManager.docToString(XMLManager.stringToDom(sourceStr));
	
		return XMLManager.parseXmlByXSLT(sourceStr, xsltStr);
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
	
	private void updatePositionForPlanAndTree(){
		int groupIndex = 1;
		int caseInPlanIndex = this.selectedTestPlan.getTestcasegroups().size() + 1;
		
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
	
	public boolean isSelectedColumn(String columnName){
		return Arrays.asList(this.selectedDisplayColumns).contains(columnName);
	}
	
	public String cutSegmentPath(String ipath){
		if(this.selectedInstanceSegment == null){
			return ipath;
		}
		
		return ipath.replace(this.selectedInstanceSegment.getIpath() + ".", "");
	}
	
	
	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	
	
	
	
	
	
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

//	public TreeNode getMessageTreeRoot() {
//		return messageTreeRoot;
//	}
//
//	public void setMessageTreeRoot(TreeNode messageTreeRoot) {
//		this.messageTreeRoot = messageTreeRoot;
//	}

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

	public void setActiveIndexOfMessageInstancePanel(int activeIndexOfMessageInstancePanel) {
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

	public MetadataConverter getMetadataConverter() {
		return metadataConverter;
	}

	public void setMetadataConverter(MetadataConverter metadataConverter) {
		this.metadataConverter = metadataConverter;
	}

	public DataInstanceTestGroupConverter getTgConverter() {
		return tgConverter;
	}

	public void setTgConverter(DataInstanceTestGroupConverter tgConverter) {
		this.tgConverter = tgConverter;
	}

	public DataInstanceTestStepConverter getTsConverter() {
		return tsConverter;
	}

	public void setTsConverter(DataInstanceTestStepConverter tsConverter) {
		this.tsConverter = tsConverter;
	}

	public ManualTestStepConverter getMtsConverter() {
		return mtsConverter;
	}

	public void setMtsConverter(ManualTestStepConverter mtsConverter) {
		this.mtsConverter = mtsConverter;
	}

	public String getJurorDocumentPDFFileName() {
		return jurorDocumentPDFFileName;
	}

	public void setJurorDocumentPDFFileName(String jurorDocumentPDFFileName) {
		this.jurorDocumentPDFFileName = jurorDocumentPDFFileName;
	}

	public Long getJurorDocumentId() {
		return jurorDocumentId;
	}

	public void setJurorDocumentId(Long jurorDocumentId) {
		this.jurorDocumentId = jurorDocumentId;
	}

	public String getSelectedTestCaseName() {
		return selectedTestCaseName;
	}

	public void setSelectedTestCaseName(String selectedTestCaseName) {
		this.selectedTestCaseName = selectedTestCaseName;
	}

	public Long getDefaultTDCId() {
		return defaultTDCId;
	}

	public void setDefaultTDCId(Long defaultTDCId) {
		this.defaultTDCId = defaultTDCId;
	}

	public HL7V2MessageValidationReport gethL7V2MessageValidationReport() {
		return hL7V2MessageValidationReport;
	}

	public void sethL7V2MessageValidationReport(
			HL7V2MessageValidationReport hL7V2MessageValidationReport) {
		this.hL7V2MessageValidationReport = hL7V2MessageValidationReport;
	}

	public DataInstanceTestCaseGroup getParentTestGroup() {
		return parentTestGroup;
	}

	public void setParentTestGroup(DataInstanceTestCaseGroup parentTestGroup) {
		this.parentTestGroup = parentTestGroup;
	}

	public DataInstanceTestCase getParentTestCase() {
		return parentTestCase;
	}

	public void setParentTestCase(DataInstanceTestCase parentTestCase) {
		this.parentTestCase = parentTestCase;
	}

	public String[] getSelectedDisplayColumns() {
		return selectedDisplayColumns;
	}

	public void setSelectedDisplayColumns(String[] selectedDisplayColumns) {
		this.selectedDisplayColumns = selectedDisplayColumns;
	}

	public List<String> getDisplayColumns() {
		return displayColumns;
	}

	public void setDisplayColumns(List<String> displayColumns) {
		this.displayColumns = displayColumns;
	}	
}