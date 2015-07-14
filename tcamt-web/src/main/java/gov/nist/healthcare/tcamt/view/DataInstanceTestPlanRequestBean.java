package gov.nist.healthcare.tcamt.view;

import gov.nist.healthcare.hl7tools.v2.maker.core.ConversionException;
import gov.nist.healthcare.tcamt.domain.DataInstanceTestCase;
import gov.nist.healthcare.tcamt.domain.DataInstanceTestCaseGroup;
import gov.nist.healthcare.tcamt.domain.DataInstanceTestPlan;
import gov.nist.healthcare.tcamt.domain.DataInstanceTestStep;
import gov.nist.healthcare.tcamt.domain.IntegratedProfile;
import gov.nist.healthcare.tcamt.domain.Log;
import gov.nist.healthcare.tcamt.domain.ManualTestStep;
import gov.nist.healthcare.tcamt.domain.Message;
import gov.nist.healthcare.tcamt.domain.Metadata;
import gov.nist.healthcare.tcamt.domain.TCAMTConstraint;
import gov.nist.healthcare.tcamt.domain.TestStory;
import gov.nist.healthcare.tcamt.domain.data.ComponentModel;
import gov.nist.healthcare.tcamt.domain.data.FieldModel;
import gov.nist.healthcare.tcamt.domain.data.InstanceSegment;
import gov.nist.healthcare.tcamt.domain.data.TestDataCategorization;
import gov.nist.healthcare.tcamt.service.ManageInstance;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.json.XML;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TreeDragDropEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.xml.sax.SAXException;

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
	private MetadataConverter metadataConverter;
	private ManualTestStepConverter mtsConverter;
	private DataInstanceTestStepConverter tsConverter;
	private DataInstanceTestCaseConverter tcConverter;
	private DataInstanceTestGroupConverter tgConverter;
	private DataInstanceTestPlanConverter tpConverter;
	
	
	private String testDataSpecificationHTML;
	private String jurorDocumentHTML;
	private String jurorDocumentPDFFileName;
	
	private String messageContentHTML;
	
	
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
			testplan.setName("Copyed_" + testplan.getName());
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
			
//			FacesContext context = FacesContext.getCurrentInstance();
//	        context.addMessage(null, new FacesMessage("Select TestPlan",  "TestPlan: " + this.selectedTestPlan.getName() + " has been selected."));
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
			this.selectedTestPlan.setName("Copyed_" + selectedTestPlan.getName());
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
	
	public void saveTestPlan()  {
		try{
			if(this.selectedTestPlan.getId() <= 0){
				this.selectedTestPlan.setVersion(1);
				this.sessionBeanTCAMT.getDbManager().dataInstanceTestPlanInsert(this.selectedTestPlan);
				this.sessionBeanTCAMT.updateDataInstanceTestPlans();
			}else{
				DataInstanceTestPlan testplan = this.selectedTestPlan.clone();
				this.sessionBeanTCAMT.getDbManager().dataInstanceTestPlanInsert(testplan);
				this.sessionBeanTCAMT.updateDataInstanceTestPlans();
				this.sessionBeanTCAMT.getDbManager().dataInstanceTestPlanDelete(this.selectedTestPlan);
				this.selectedTestPlan = this.sessionBeanTCAMT.getDbManager().getDataInstanceTestPlanById(testplan.getId());
				this.sessionBeanTCAMT.updateDataInstanceTestPlans();
			}
			this.selectedTestPlan.setSelected(true);
			this.createTestPlanTree(this.selectedTestPlan);
			
			this.selectedTestCase = null;
			this.selectedTestStep = null;
			this.selectedTestCaseGroup = null;
			
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
				
//				FacesContext context = FacesContext.getCurrentInstance();
//		        context.addMessage(null, new FacesMessage("New TestCaseGroup",  "New TestCaseGroup has been created."));
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
				clonedGroup.setName("Copyed_" + clonedGroup.getName());
				clonedGroup.setPosition(this.selectedTestPlan.getTestcasegroups().size()+1);
				clonedGroup.setSelected(true);
				this.selectedTestPlan.addTestCaseGroup(clonedGroup);
				this.createTestPlanTree(this.selectedTestPlan);
				
				this.selectedTestCase = null;
				this.selectedTestCaseGroup = clonedGroup;
				this.selectedTestStep = null;
				
//		        FacesContext context = FacesContext.getCurrentInstance();
//		        context.addMessage(null, new FacesMessage("Clone TestCaseGroup",  "TestCaseGroup: " + this.selectedTestCaseGroup.getName() + " has been clonned.") );
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
				
//				FacesContext context = FacesContext.getCurrentInstance();
//		        context.addMessage(null, new FacesMessage("Delete TestCaseGroup",  "TestCaseGroup has been deleted.") );
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
				}else if(this.selectedNode.getData() instanceof  DataInstanceTestCaseGroup){
					newDataInstacneTestCase.setPosition(((DataInstanceTestCaseGroup)this.selectedNode.getData()).getTestcases().size() + 1);
					newDataInstacneTestCase.setSelected(true);
					((DataInstanceTestCaseGroup)this.selectedNode.getData()).setExpanded(true);
					((DataInstanceTestCaseGroup)this.selectedNode.getData()).addTestCase(newDataInstacneTestCase);
				}
				
				this.createTestPlanTree(this.selectedTestPlan);
				
				this.selectedTestCase = newDataInstacneTestCase;
				this.selectedTestStep = null;
				this.selectedTestCaseGroup = null;
//				FacesContext context = FacesContext.getCurrentInstance();
//		        context.addMessage(null, new FacesMessage("New TestCase",  "New TestCase has been created."));
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
				clonedTestcase.setName("Copyed_" + clonedTestcase.getName());
				clonedTestcase.setSelected(true);
				
				if(this.selectedNode.getParent().getData() instanceof DataInstanceTestCaseGroup){
					DataInstanceTestCaseGroup group = (DataInstanceTestCaseGroup)this.selectedNode.getParent().getData();
					group.setExpanded(true);
					clonedTestcase.setPosition(group.getTestcases().size()+1);
					group.addTestCase(clonedTestcase);
				}else if(this.selectedNode.getParent().getData() instanceof DataInstanceTestPlan){
					DataInstanceTestPlan plan = (DataInstanceTestPlan)this.selectedNode.getParent().getData();
					plan.setExpanded(true);
					clonedTestcase.setPosition(plan.getTestcases().size()+1);
					plan.addTestCase(clonedTestcase);
				}
				
				this.createTestPlanTree(this.selectedTestPlan);
				
				this.selectedTestCase = clonedTestcase;
				this.selectedTestCaseGroup = null;
				this.selectedTestStep = null;
//				FacesContext context = FacesContext.getCurrentInstance();
//		        context.addMessage(null, new FacesMessage("Clone TestCase",  "TestCase: " + this.selectedTestCase.getName() + " has been clonned.") );
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
				}else if(parentNode.getData() instanceof DataInstanceTestPlan){
					this.selectedTestPlan.getTestcases().remove(this.selectedNode.getData());
					this.selectedTestPlan.setSelected(true);
				}
				
				this.createTestPlanTree(this.selectedTestPlan);
				this.updatePositionForPlanAndTree();
//				FacesContext context = FacesContext.getCurrentInstance();
//		        context.addMessage(null, new FacesMessage("Delete TestCase",  "TestCase has been deleted."));
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
//				FacesContext context = FacesContext.getCurrentInstance();
//		        context.addMessage(null, new FacesMessage("New TestStep",  "New TestStep has been created."));
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
				clonedStep.setName("Copyed_" + clonedStep.getName());
				clonedStep.setPosition(testcase.getTeststeps().size()+1);
				clonedStep.setSelected(true);
				testcase.addTestStep(clonedStep);
				testcase.setExpanded(true);
				this.createTestPlanTree(this.selectedTestPlan);
				
				this.selectedTestCaseGroup = null;
				this.selectedTestCase = null;
				this.selectedTestStep = clonedStep;
//				FacesContext context = FacesContext.getCurrentInstance();
//		        context.addMessage(null, new FacesMessage("Clone TestStep",  "TestStep has been cloned."));
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
				}
				
				this.createTestPlanTree(this.selectedTestPlan);
				this.updatePositionForPlanAndTree();
				
				this.selectedTestCaseGroup = null;
				this.selectedTestStep = null;
//				FacesContext context = FacesContext.getCurrentInstance();
//		        context.addMessage(null, new FacesMessage("Delete TestStep",  "TestStep has been deleted."));
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
//				FacesContext context = FacesContext.getCurrentInstance();
//		        context.addMessage(null, new FacesMessage("Select TestPlan",  "TestPlan has been selected."));
			}else if(event.getTreeNode().getData() instanceof DataInstanceTestCase){
				this.selectedTestCase = (DataInstanceTestCase)event.getTreeNode().getData();
				this.selectedTestCaseGroup = null;
				this.selectedTestStep = null;
//				FacesContext context = FacesContext.getCurrentInstance();
//		        context.addMessage(null, new FacesMessage("Select TestCase",  "TestCase has been selected."));
			}else if(event.getTreeNode().getData() instanceof DataInstanceTestCaseGroup){
				this.selectedTestCase = null;
				this.selectedTestStep = null;
				this.selectedTestCaseGroup = (DataInstanceTestCaseGroup)event.getTreeNode().getData();
//				FacesContext context = FacesContext.getCurrentInstance();
//		        context.addMessage(null, new FacesMessage("Select TestCaseGroup",  "TestCaseGroup has been selected."));
			}else if(event.getTreeNode().getData() instanceof DataInstanceTestStep){
				this.selectedTestStep = (DataInstanceTestStep)event.getTreeNode().getData();
				this.selectedTestCase = null;
				this.selectedTestCaseGroup = null;
				this.selectTestStep();
				this.activeIndexOfMessageInstancePanel = 0;
//				FacesContext context = FacesContext.getCurrentInstance();
//		        context.addMessage(null, new FacesMessage("Select TestStep",  "TestStep has been selected."));
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
	
	public void readHL7Message() {
		try{
			this.instanceSegments = new ArrayList<InstanceSegment>();
			if(this.selectedTestStep.getMessage().getHl7EndcodedMessage() != null && !this.selectedTestStep.getMessage().getHl7EndcodedMessage().equals("")){
				this.manageInstanceService.loadMessageInstance(this.selectedTestStep.getMessage(), this.instanceSegments);
				this.selectedInstanceSegment = null;
				
				this.generateTestDataSpecificationHTML();
				this.generateJurorDocumentHTML();
				this.generateMessageContentHTML();
			}
			this.updateFilteredInstanceSegments();
//			FacesContext context = FacesContext.getCurrentInstance();
//	        context.addMessage(null, new FacesMessage("TestStep Loaded",  "HL7Message of TestStep has been readed."));
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
//			FacesContext context = FacesContext.getCurrentInstance();
//	        context.addMessage(null, new FacesMessage("Select Segment",  "Instance Segement has been selected."));
		}catch(Exception e){
			FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "FATAL Error", e.toString()));
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}
	
	public void genrateHL7Message() {
		try{
			this.selectedTestStep.getMessage().setHl7EndcodedMessage(this.manageInstanceService.generateHL7Message(this.messageTreeRoot,  this.selectedTestStep.getMessage()));
			
//			FacesContext context = FacesContext.getCurrentInstance();
//	        context.addMessage(null, new FacesMessage("Generate Message",  "HL7 Message of TestStep has been generated."));
	        
			this.readHL7Message();			
		}catch(Exception e){
			FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "FATAL Error", e.toString()));
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}
	
	public void updateInstanceData(Object model) {
		try{
			int lineNum = this.instanceSegments.indexOf(this.selectedInstanceSegment);
			this.manageInstanceService.updateHL7Message(lineNum, this.manageInstanceService.generateLineStr(this.segmentTreeRoot), this.selectedTestStep.getMessage());
			this.readHL7Message();
			this.selectedInstanceSegment = this.instanceSegments.get(lineNum);
			this.activeIndexOfMessageInstancePanel = 4;
//			FacesContext context = FacesContext.getCurrentInstance();
//	        context.addMessage(null, new FacesMessage("Update TestData",  "TestData has been updated."));
		}catch(Exception e){
			FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "FATAL Error", e.toString()));
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}
	
	public void downloadResourceBundleForTestPlan(DataInstanceTestPlan tp) {
		try{
			this.setTestStoryConverter(new JsonTestStoryConverter());
			this.setTpConverter(new JsonDataInstanceTestPlanConverter());
			this.setTgConverter(new JsonDataInstanceTestGroupConverter());
			this.setTcConverter(new JsonDataInstanceTestCaseConverter());
			this.setTsConverter(new JsonDataInstanceTestStepConverter());
			this.setMtsConverter(new JsonManualTestStepConverter());
			this.setMetadataConverter(new JsonMetadataConverter());
			
			String outFilename = "TestPlan_" + tp.getName() + ".zip";
			ByteArrayOutputStream outputStream = null;
			byte[] bytes;
			outputStream = new ByteArrayOutputStream();
			ZipOutputStream out = new ZipOutputStream(outputStream);
			
			this.generateMetaDataRB(out, tp.getMetadata());
			
			if(tp.getType() != null && tp.getType().equals("Isolated")){
				this.generateIsolatedRB(out, tp);
			}else {
				this.generateContextBasedRB(out, tp);
			}
			this.generateDocumentationRB(out, tp);
			this.generateProfilesConstraintsValueSetsRB(out, tp);
			
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
	
	public void createTCAMTConstraint(Object model) {
		try{
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
			this.generateMessageContentHTML();	
			
			this.activeIndexOfMessageInstancePanel = 4;
			
//			FacesContext context = FacesContext.getCurrentInstance();
//	        context.addMessage(null, new FacesMessage("Update Constraints",  "Constraints has been updated."));
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
//				FacesContext context = FacesContext.getCurrentInstance();
//		        context.addMessage(null, new FacesMessage("Chagne View Option",  "All of Instacne Segments are shown."));
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
//				FacesContext context = FacesContext.getCurrentInstance();
//		        context.addMessage(null, new FacesMessage("Chagne View Option",  "R/RE/C of Instacne Segments are shown."));
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
//				FacesContext context = FacesContext.getCurrentInstance();
//		        context.addMessage(null, new FacesMessage("Chagne View Option",  "All of node are shown in the Segment Tree."));
			}else {
				this.filtedSegmentTreeRoot = this.manageInstanceService.genRestrictedTree(this.segmentTreeRoot);
//				FacesContext context = FacesContext.getCurrentInstance();
//		        context.addMessage(null, new FacesMessage("Chagne View Option",  "R/RE/C of node are shown in the Segment Tree."));
			}
		}catch(Exception e){
			FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "FATAL Error", e.toString()));
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
	}
	
	public void updateTestDataFromCS(){
		try{
			this.manageInstanceService.updateTestDataFromCS(this.filtedSegmentTreeRoot);
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
//			FacesContext context = FacesContext.getCurrentInstance();
//	        context.addMessage(null, new FacesMessage("Repeat Field",  "Repeatable Field has been added."));
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
			
//			FacesContext context = FacesContext.getCurrentInstance();
//	        context.addMessage(null, new FacesMessage("Update Message",  "Message of Test Step has been updated."));
	        
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
//	        		FacesContext context = FacesContext.getCurrentInstance();
//	    	        context.addMessage(null, new FacesMessage("Drop TestCaseGroup",  "TestCaseGroup has been droped in TestPlan."));
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
//	        		FacesContext context = FacesContext.getCurrentInstance();
//	    	        context.addMessage(null, new FacesMessage("Drop TestCase",  "TestCase has been droped in TestPlan."));
	        	}else if(dropNode.getData() instanceof DataInstanceTestCaseGroup){
	        		this.updateTestPlanByTree(dragNode, dropNode, dropIndex);
//	        		FacesContext context = FacesContext.getCurrentInstance();
//	    	        context.addMessage(null, new FacesMessage("Drop TestCase",  "TestCase has been droped in TestCaseGroup."));
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
//	        		FacesContext context = FacesContext.getCurrentInstance();
//	    	        context.addMessage(null, new FacesMessage("Drop TestStep",  "TestStep has been droped in TestCase."));
	        	}else if(dropNode.getData() instanceof DataInstanceTestStep){
	        		FacesContext context = FacesContext.getCurrentInstance();
	                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Test Step cannot have another Test Step. Have been reverted."));
	        		this.createTestPlanTree(this.selectedTestPlan);
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
	
	public void resetMessage(){
		try{
			this.selectedTestStep.setMessage(new Message());
			this.jurorDocumentHTML = null;
			this.jurorDocumentPDFFileName = null;
			this.messageContentHTML = null;
			this.testDataSpecificationHTML = null;		
			
//			FacesContext context = FacesContext.getCurrentInstance();
//	        context.addMessage(null, new FacesMessage("Reset Message",  "Message of Test Step has been reset."));
		}catch(Exception e){
			FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "FATAL Error", e.toString()));
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.sessionBeanTCAMT.getDbManager().logInsert(log);
		}
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
			
			List<DataInstanceTestCase> sortedDataInstanceTestCases = new ArrayList<DataInstanceTestCase>(ditcg.getTestcases());
			Collections.sort(sortedDataInstanceTestCases);
			for(DataInstanceTestCase ditc:sortedDataInstanceTestCases){
				TreeNode caseNode = new DefaultTreeNode("case", ditc, groupNode);
				caseNode.setExpanded(ditc.isExpanded());
				caseNode.setSelected(ditc.isSelected());
				ditc.setSelected(false);
				List<DataInstanceTestStep> sortedDataInstanceTestStep = new ArrayList<DataInstanceTestStep>(ditc.getTeststeps());
				Collections.sort(sortedDataInstanceTestStep);
				for(DataInstanceTestStep dits:sortedDataInstanceTestStep){
					TreeNode stepNode = new DefaultTreeNode("step", dits, caseNode);
					stepNode.setSelected(dits.isSelected());
					dits.setSelected(false);
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
			List<DataInstanceTestStep> sortedDataInstanceTestStep = new ArrayList<DataInstanceTestStep>(ditc.getTeststeps());
			Collections.sort(sortedDataInstanceTestStep);
			for(DataInstanceTestStep dits:sortedDataInstanceTestStep){
				TreeNode stepNode = new DefaultTreeNode("step", dits, caseNode);
				stepNode.setSelected(dits.isSelected());
				dits.setSelected(false);
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
	
	private void selectTestStep() throws CloneNotSupportedException {
		this.selectedTestCaseGroup = null;
		this.testDataSpecificationHTML = null;
		this.jurorDocumentHTML = null;
		this.jurorDocumentPDFFileName = null;
		this.setMessageContentHTML(null);
		
		this.selectedInstanceSegment = new InstanceSegment();
		this.messageTreeRoot = new DefaultTreeNode("root", null);
		this.segmentTreeRoot = new DefaultTreeNode("root", null);
		this.setConstraintTreeRoot(new DefaultTreeNode("root", null));
		this.instanceSegments = new ArrayList<InstanceSegment>();
		this.filteredInstanceSegments =  new ArrayList<InstanceSegment>();
		this.manageInstanceService = new ManageInstance();
		
		if(this.selectedTestStep.getMessage() != null && this.selectedTestStep.getMessage().getConformanceProfile() != null && this.selectedTestStep.getMessage().getName() != null){
			this.messageTreeRoot = this.manageInstanceService.loadMessage(this.selectedTestStep.getMessage());
			this.constraintTreeRoot = this.manageInstanceService.generateConstraintTree(this.selectedTestStep.getMessage());
			this.readHL7Message();
		}
	}
	
	private void generateMessageContentHTML(){
		this.setMessageContentHTML(this.manageInstanceService.generateMessageContentHTML(this.selectedTestStep.getMessage(), instanceSegments));
	}
	
	private void generateJurorDocumentHTML() throws IOException {
		if(this.selectedTestStep.getMessage().getConformanceProfile().getJurorDocumentXSLT() != null){
			InputStream xsltInputStream = new ByteArrayInputStream(this.selectedTestStep.getMessage().getConformanceProfile().getJurorDocumentXSLT().getBytes());
			InputStream sourceInputStream = new ByteArrayInputStream(this.selectedTestStep.getMessage().getXmlEncodedNISTMessage().getBytes());
			Reader xsltReader =  new InputStreamReader(xsltInputStream, "UTF-8");
			Reader sourceReader = new InputStreamReader(sourceInputStream, "UTF-8");
			String xsltStr = IOUtils.toString(xsltReader);
			String sourceStr = IOUtils.toString(sourceReader);
			
			this.jurorDocumentHTML = XMLManager.parseXmlByXSLT(sourceStr, xsltStr);
			this.jurorDocumentPDFFileName = this.htmlStringToPDF2(this.jurorDocumentHTML) + ".pdf";
		}	
	}
	
	private void generateTestDataSpecificationHTML() throws IOException {
		if(this.selectedTestStep.getMessage().getConformanceProfile().getTestDataSpecificationXSLT() != null){
			InputStream xsltInputStream = new ByteArrayInputStream(this.selectedTestStep.getMessage().getConformanceProfile().getTestDataSpecificationXSLT().getBytes());
			InputStream sourceInputStream = new ByteArrayInputStream(this.selectedTestStep.getMessage().getXmlEncodedNISTMessage().getBytes());
			Reader xsltReader =  new InputStreamReader(xsltInputStream, "UTF-8");
			Reader sourceReader = new InputStreamReader(sourceInputStream, "UTF-8");
			String xsltStr = IOUtils.toString(xsltReader);
			String sourceStr = IOUtils.toString(sourceReader);
			this.testDataSpecificationHTML = XMLManager.parseXmlByXSLT(sourceStr, xsltStr);	
		}			
	}
	
	private void generateIsolatedRB(ZipOutputStream out, DataInstanceTestPlan tp) throws Exception {
		this.generateTestPlanJsonRB("Isolated" + File.separator + "TestPlan", out, tp);
		for(DataInstanceTestCaseGroup ditg:tp.getTestcasegroups()){
			String groupPath = "Isolated" + File.separator + "TestPlan" + File.separator + "TestGroup_" + ditg.getPosition();
			this.generateTestGroupJsonRB(out, ditg, groupPath);
			for(DataInstanceTestCase ditc:ditg.getTestcases()){
				String testcasePath = groupPath + File.separator + "TestCase_" + ditc.getPosition();
				this.generateTestCaseJsonRB(out, ditc, testcasePath);
				this.generateTestStoryRB(out, ditc.getTestCaseStory(), testcasePath);
				for(DataInstanceTestStep dits:ditc.getTeststeps()){
					String teststepPath = testcasePath + File.separator + "TestStep_" + dits.getPosition();
					this.generateTestStoryRB(out, dits.getTestStepStory(), teststepPath);
					this.generateTestStepJsonRB(out, dits, teststepPath);
					if(dits != null && dits.getMessage() != null && dits.getMessage().getConformanceProfile() != null){
						this.generateMessageRB(out, dits.getMessage().getHl7EndcodedMessage(), teststepPath);
						this.generateMessageContentRB(out, dits.getMessage(), teststepPath);
						this.generateTestDataSpecificationRB(out, dits.getMessage(), teststepPath);
						this.generateTestDataSpecificationJSONRB(out, dits.getMessage(), teststepPath);
						this.generateJurorDocumentRB(out, dits.getMessage(), teststepPath);
						this.generateJurorDocumentJSONRB(out, dits.getMessage(), teststepPath);
						this.generateTestStepConstraintsRB(out, dits.getMessage(), teststepPath);
					}
				}
			}
		}
		
		for(DataInstanceTestCase ditc:tp.getTestcases()){
			String testcasePath = "Isolated" + File.separator + "TestPlan" + File.separator  + "TestCase_" +  ditc.getPosition();
			this.generateTestStoryRB(out, ditc.getTestCaseStory(), testcasePath);
			this.generateTestCaseJsonRB(out, ditc, testcasePath);
			for(DataInstanceTestStep dits:ditc.getTeststeps()){
				String teststepPath = testcasePath + File.separator + "TestStep_" + dits.getPosition();
				this.generateTestStoryRB(out, dits.getTestStepStory(), teststepPath);
				this.generateTestStepJsonRB(out, dits, teststepPath);
				if(dits != null && dits.getMessage() != null && dits.getMessage().getConformanceProfile() != null){
					this.generateMessageRB(out, dits.getMessage().getHl7EndcodedMessage(), teststepPath);
					this.generateMessageContentRB(out, dits.getMessage(), teststepPath);
					this.generateTestDataSpecificationRB(out, dits.getMessage(), teststepPath);
					this.generateTestDataSpecificationJSONRB(out, dits.getMessage(), teststepPath);
					this.generateJurorDocumentRB(out, dits.getMessage(), teststepPath);
					this.generateJurorDocumentJSONRB(out, dits.getMessage(), teststepPath);
					this.generateTestStepConstraintsRB(out, dits.getMessage(), teststepPath);
				}
			}
		}
	}
	
	private void generateContextBasedRB(ZipOutputStream out, DataInstanceTestPlan tp) throws Exception {
		this.generateTestPlanJsonRB("Contextbased" + File.separator + "TestPlan", out, tp);
		
		for(DataInstanceTestCaseGroup ditg:tp.getTestcasegroups()){
			String groupPath = "Contextbased" + File.separator + "TestPlan" + File.separator + "TestGroup_" + ditg.getPosition();
			this.generateTestGroupJsonRB(out, ditg, groupPath);
			for(DataInstanceTestCase ditc:ditg.getTestcases()){
				String testcasePath = groupPath + File.separator + "TestCase_" + ditc.getPosition();
				this.generateTestCaseJsonRB(out, ditc, testcasePath);
				this.generateTestStoryRB(out, ditc.getTestCaseStory(), testcasePath);
				for(DataInstanceTestStep dits:ditc.getTeststeps()){
					String teststepPath = testcasePath + File.separator + "TestStep_" + dits.getPosition();
					this.generateTestStoryRB(out, dits.getTestStepStory(), teststepPath);
					this.generateTestStepJsonRB(out, dits, teststepPath);
					if(dits != null && dits.getMessage() != null && dits.getMessage().getConformanceProfile() != null){
						this.generateMessageRB(out, dits.getMessage().getHl7EndcodedMessage(), teststepPath);
						this.generateMessageContentRB(out, dits.getMessage(), teststepPath);
						this.generateTestDataSpecificationRB(out, dits.getMessage(), teststepPath);
						this.generateTestDataSpecificationJSONRB(out, dits.getMessage(), teststepPath);
						this.generateJurorDocumentRB(out, dits.getMessage(), teststepPath);
						this.generateJurorDocumentJSONRB(out, dits.getMessage(), teststepPath);
						this.generateTestStepConstraintsRB(out, dits.getMessage(), teststepPath);
					}
				}
			}
		}
		
		for(DataInstanceTestCase ditc:tp.getTestcases()){
			String testcasePath = "Contextbased" + File.separator + "TestPlan" + File.separator  + "TestCase_" +  ditc.getPosition();
			this.generateTestStoryRB(out, ditc.getTestCaseStory(), testcasePath);
			this.generateTestCaseJsonRB(out, ditc, testcasePath);
			for(DataInstanceTestStep dits:ditc.getTeststeps()){
				String teststepPath = testcasePath + File.separator + "TestStep_" + dits.getPosition();
				this.generateTestStoryRB(out, dits.getTestStepStory(), teststepPath);
				this.generateTestStepJsonRB(out, dits, teststepPath);
				if(dits != null && dits.getMessage() != null && dits.getMessage().getConformanceProfile() != null){
					this.generateMessageRB(out, dits.getMessage().getHl7EndcodedMessage(), teststepPath);
					this.generateMessageContentRB(out, dits.getMessage(), teststepPath);
					this.generateTestDataSpecificationRB(out, dits.getMessage(), teststepPath);
					this.generateTestDataSpecificationJSONRB(out, dits.getMessage(), teststepPath);
					this.generateJurorDocumentRB(out, dits.getMessage(), teststepPath);
					this.generateJurorDocumentJSONRB(out, dits.getMessage(), teststepPath);
					this.generateTestStepConstraintsRB(out, dits.getMessage(), teststepPath);
				}
			}
		}
	}
	
	private void generateTestStepJsonRB(ZipOutputStream out, DataInstanceTestStep dits, String teststepPath) throws IOException, ConversionException {
		byte[] buf = new byte[1024];
		out.putNextEntry(new ZipEntry(teststepPath + File.separator + "TestStep.json"));
		
		if(dits != null && dits.getMessage() != null && dits.getMessage().getConformanceProfile() != null){
			dits.setMessageId(dits.getMessage().getConformanceProfile().getConformanceProfileId());
			dits.setConstraintId(dits.getMessage().getConformanceProfile().getConstraintId());
			dits.setValueSetLibraryId(dits.getMessage().getConformanceProfile().getValueSetLibraryId());
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

	private void generateJurorDocumentJSONRB(ZipOutputStream out, Message m, String path) throws IOException{
		if(m.getConformanceProfile().getJurorDocumentJSONXSLT() != null){
			InputStream xsltInputStream = new ByteArrayInputStream(m.getConformanceProfile().getJurorDocumentJSONXSLT().getBytes());
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

	private void generateJurorDocumentRB(ZipOutputStream out, Message m, String path) throws IOException{
		if(m.getConformanceProfile().getJurorDocumentXSLT() != null){
			InputStream xsltInputStream = new ByteArrayInputStream(m.getConformanceProfile().getJurorDocumentXSLT().getBytes());
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
	
	private void generateTestDataSpecificationRB(ZipOutputStream out, Message m, String path) throws IOException{
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
	
	private void generateMessageContentRB(ZipOutputStream out, Message m, String path) throws Exception{		
		byte[] buf = new byte[1024];
		out.putNextEntry(new ZipEntry(path + File.separator + "MessageContent.html"));
		this.instanceSegments = new ArrayList<InstanceSegment>();
		if(m.getHl7EndcodedMessage() != null && !m.getHl7EndcodedMessage().equals("")){
			this.manageInstanceService.loadMessage(m);
			this.manageInstanceService.loadMessageInstance(m, this.instanceSegments);
		}
		
        String messageContentHTMLStr = this.manageInstanceService.generateMessageContentHTML(m, instanceSegments);
        
        InputStream inMessageContent = IOUtils.toInputStream(messageContentHTMLStr, "UTF-8");
        int lenMessageContent;
        while ((lenMessageContent = inMessageContent.read(buf)) > 0) {
            out.write(buf, 0, lenMessageContent);
        }
        inMessageContent.close();
        out.closeEntry();
        
        out.putNextEntry(new ZipEntry(path + File.separator + "MessageContent.json"));			
        String messageContentJSONStr = this.manageInstanceService.generateMessageContentJSON(m, instanceSegments);
        
        JSONObject xmlJSONObj = XML.toJSONObject(messageContentJSONStr);
        InputStream inMessageContentJSON = IOUtils.toInputStream(xmlJSONObj.toString(), "UTF-8");
        int lenMessageContentJSON;
        while ((lenMessageContentJSON = inMessageContentJSON.read(buf)) > 0) {
            out.write(buf, 0, lenMessageContentJSON);
        }
        inMessageContentJSON.close();
        out.closeEntry();
        
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
	
	private void generateProfilesConstraintsValueSetsRB(ZipOutputStream out, DataInstanceTestPlan tp) throws SAXException, ParserConfigurationException, Exception{
		Map<Long, IntegratedProfile> integratedProfiles = new HashMap<Long, IntegratedProfile>();
		
		
		for(DataInstanceTestCaseGroup ditg:tp.getTestcasegroups()){
			for(DataInstanceTestCase ditc:ditg.getTestcases()){
				for(DataInstanceTestStep dits:ditc.getTeststeps()){
					if(dits != null && dits.getMessage() != null && dits.getMessage().getConformanceProfile() != null){
						integratedProfiles.put(dits.getMessage().getConformanceProfile().getIntegratedProfile().getId(), dits.getMessage().getConformanceProfile().getIntegratedProfile());
					}
				}
			}
		}
		
		for(DataInstanceTestCase ditc:tp.getTestcases()){
			for(DataInstanceTestStep dits:ditc.getTeststeps()){
				if(dits != null && dits.getMessage() != null && dits.getMessage().getConformanceProfile() != null){
					integratedProfiles.put(dits.getMessage().getConformanceProfile().getIntegratedProfile().getId(), dits.getMessage().getConformanceProfile().getIntegratedProfile());
				}
			}
		}
		
		for(Long id:integratedProfiles.keySet()){
			this.generateProfileRB(out, integratedProfiles.get(id).getProfile(), integratedProfiles.get(id).getName() + "_Profile.xml");
			this.generateValueSetRB(out, integratedProfiles.get(id).getValueSet(), integratedProfiles.get(id).getName() + "_ValueSetLibrary.xml");
			this.generateConstraintRB(out, integratedProfiles.get(id).getConstraints(), integratedProfiles.get(id).getName() + "_Constraints.xml");	
		}
	}
	
	private void generateDocumentationRB(ZipOutputStream out, DataInstanceTestPlan tp) throws IOException{
		out.putNextEntry(new ZipEntry("Documentation" + File.separator));
		out.closeEntry();
	}
	
	private void generateMetaDataRB(ZipOutputStream out, Metadata md) throws IOException, ConversionException {
		byte[] buf = new byte[1024];
		out.putNextEntry(new ZipEntry("About" + File.separator + "Metadata.json"));
		InputStream inMetadata = IOUtils.toInputStream(this.metadataConverter.toString(md));
		int lenMeta;
        while ((lenMeta = inMetadata.read(buf)) > 0) {
            out.write(buf, 0, lenMeta);
        }
        out.closeEntry();
        inMetadata.close();
	}
	
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
		out.putNextEntry(new ZipEntry(path + File.separator + "Message.text"));
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
	
	private void generateProfileRB(ZipOutputStream out, String profileStr, String fileName) throws SAXException, ParserConfigurationException, Exception {
		byte[] buf = new byte[1024];
		out.putNextEntry(new ZipEntry("Global" + File.separator + "Profiles" + File.separator + fileName));
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
	
	private void generateConstraintRB(ZipOutputStream out, String constraintStr, String fileName) throws IOException {
		byte[] buf = new byte[1024];
		out.putNextEntry(new ZipEntry("Global" + File.separator + "Constraints" + File.separator + fileName));		
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
	
	private void generateValueSetRB(ZipOutputStream out, String valueSetStr, String fileName) throws SAXException, ParserConfigurationException, Exception {
		byte[] buf = new byte[1024];
		out.putNextEntry(new ZipEntry("Global" + File.separator + "ValueSetLibrary" + File.separator + fileName));
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
		String fileFullName =grandParentFile.getParent() + File.separator + "temp" + File.separator + fileName;
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
	
	private void generateTestStoryRB(ZipOutputStream out, TestStory testStory, String path) throws IOException, ConversionException{
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
		
		String testCaseStoryStr = this.generateTestStory(testStory);
        InputStream inTestStory = IOUtils.toInputStream(testCaseStoryStr, "UTF-8");
        int lenTestStory;
        while ((lenTestStory = inTestStory.read(buf)) > 0) {
            out.write(buf, 0, lenTestStory);
        }
        inTestStory.close();
        out.closeEntry();
        
        out.putNextEntry(new ZipEntry(path + File.separator + "TestStory.pdf"));
        String tempFileName = this.htmlStringToPDF(testCaseStoryStr);
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
	
	private void generateTestPlanJsonRB(String path, ZipOutputStream out, DataInstanceTestPlan tp) throws IOException, ConversionException {
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
	
}