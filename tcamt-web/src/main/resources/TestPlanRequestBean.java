package gov.nist.healthcare.tcamt.view;

import gov.nist.healthcare.tcamt.db.DBImpl;
import gov.nist.healthcare.tcamt.domain.TestCase;
import gov.nist.healthcare.tcamt.domain.TestPlan;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;

@ManagedBean
@SessionScoped
public class TestPlanRequestBean implements Serializable {


	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8314721636448203440L;
	
	private TestPlan newTestPlan;
	private TestPlan editTestPlan;
	private TestPlan existTestPlan;
	
	private Integer testCaseId;
	
	private DBImpl dbManager = new DBImpl();
	@ManagedProperty("#{sessionBeanTCAMT}")
	private SessionBeanTCAMT sessionBeanTCAMT;
	
	private String shareTo;

	/**
	 * 
	 */	
	private void init(){
		this.newTestPlan = new TestPlan();
		this.editTestPlan = new TestPlan();
		this.existTestPlan = new TestPlan();
		this.testCaseId = null;
	}

	public TestPlanRequestBean() {
		super();
		init();
	}

	public void addTestCase() throws CloneNotSupportedException {
		this.newTestPlan.getTestCases().add(this.testCaseId);
		this.testCaseId = null;
	}
	
	public void addTestCaseEdit() throws CloneNotSupportedException {
		this.editTestPlan.getTestCases().add(this.testCaseId);
		this.testCaseId = null;
	}
		
	public void delTestPlan(ActionEvent event) {
		this.dbManager.testPlanDelete((TestPlan)event.getComponent().getAttributes().get("testPlan"));
		this.sessionBeanTCAMT.updateTestPlans();
		init();

	}
	
	public void cloneTestPlan(ActionEvent event) throws CloneNotSupportedException {	
		TestPlan tp = (TestPlan)((TestPlan)event.getComponent().getAttributes().get("testPlan")).clone();
		tp.setName("Copy_" + tp.getName());
		tp.setVersion(0);
		this.dbManager.testPlanInsert(tp, this.sessionBeanTCAMT.getLoggedUser().getUserId());
		this.sessionBeanTCAMT.updateTestPlans();
		this.init();
	}
	
	
	public void createTestPlan() {
		init();
	}

	public void addTestPlan() throws CloneNotSupportedException {
		dbManager.testPlanInsert(this.newTestPlan, this.sessionBeanTCAMT.getLoggedUser().getUserId());
		this.sessionBeanTCAMT.updateTestPlans();
	}
	
	public void shareTestPlan(){
		dbManager.testPlanInsert(this.editTestPlan, this.shareTo);
		this.sessionBeanTCAMT.updateTestPlans();
	}
	
	public void updateTestPlan() throws CloneNotSupportedException {
		dbManager.testPlanUpdate(this.editTestPlan);
		this.sessionBeanTCAMT.updateTestPlans();
	}

	public void editTestPlan() {
		this.editTestPlan.setVersion(this.editTestPlan.getVersion() + 1);
		dbManager.testPlanUpdate(this.editTestPlan);
		this.sessionBeanTCAMT.updateTestPlans();
	}

	public void selectEditTestPlan(ActionEvent event) {
		init();
		this.existTestPlan = (TestPlan) event.getComponent().getAttributes().get("testPlan");
		this.editTestPlan.setId(existTestPlan.getId());
		this.editTestPlan.setName(existTestPlan.getName());
		this.editTestPlan.setDescription(existTestPlan.getDescription());
		this.editTestPlan.setVersion(existTestPlan.getVersion());
		this.editTestPlan.setTestCases(existTestPlan.getTestCases());
		this.shareTo = "";
	}
	
	public void delTestCase(ActionEvent event){
		int num = (Integer) event.getComponent().getAttributes().get("num");
		this.newTestPlan.getTestCases().remove(num);
	}
	
	public void delTestCaseEdit(ActionEvent event){
		int num = (Integer) event.getComponent().getAttributes().get("num");
		this.editTestPlan.getTestCases().remove(num);
	}
	
	public TestCase findTestCase(int id){
		return this.dbManager.getTestCaseById(id);
	}
	
	/**
	 * 
	 */

	public TestPlan getNewTestPlan() {
		return newTestPlan;
	}

	public void setNewTestPlan(TestPlan newTestPlan) {
		this.newTestPlan = newTestPlan;
	}

	public TestPlan getEditTestPlan() {
		return editTestPlan;
	}

	public void setEditTestPlan(TestPlan editTestPlan) {
		this.editTestPlan = editTestPlan;
	}

	public TestPlan getExistTestPlan() {
		return existTestPlan;
	}

	public void setExistTestPlan(TestPlan existTestPlan) {
		this.existTestPlan = existTestPlan;
	}

	public DBImpl getDbManager() {
		return dbManager;
	}

	public void setDbManager(DBImpl dbManager) {
		this.dbManager = dbManager;
	}

	public SessionBeanTCAMT getSessionBeanTCAMT() {
		return sessionBeanTCAMT;
	}

	public void setSessionBeanTCAMT(SessionBeanTCAMT sessionBeanTCAMT) {
		this.sessionBeanTCAMT = sessionBeanTCAMT;
	}

	public List<TestPlan> getTestPlans(){
		return this.sessionBeanTCAMT.getTestplans();
	}

	public Integer getTestCaseId() {
		return testCaseId;
	}

	public void setTestCaseId(Integer testCaseId) {
		this.testCaseId = testCaseId;
	}

	public String getShareTo() {
		return shareTo;
	}

	public void setShareTo(String shareTo) {
		this.shareTo = shareTo;
	}
}
