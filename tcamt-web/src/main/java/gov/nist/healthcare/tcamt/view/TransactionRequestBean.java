package gov.nist.healthcare.tcamt.view;

import gov.nist.healthcare.tcamt.db.DBImpl;
import gov.nist.healthcare.tcamt.domain.Transaction;
import gov.nist.healthcare.tcamt.domain.TestStep;
import gov.nist.healthcare.umld.domain.sequence.Line;
import gov.nist.healthcare.umld.domain.sequence.Participant;
import gov.nist.healthcare.umld.view.SequenceDiagramDraw;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;

import net.sourceforge.plantuml.SourceStringReader;

import org.primefaces.model.DefaultStreamedContent;

@ManagedBean
@SessionScoped
public class TransactionRequestBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 965282772653484407L;
	
	private Transaction newTransaction;
	private Transaction editTransaction;
	private Transaction existTransaction;
	private Integer newTestStepId;
	private Integer newTestStepIdForEdit;
	
	private DBImpl dbManager = new DBImpl();
	@ManagedProperty("#{sessionBeanTCAMT}")
	private SessionBeanTCAMT sessionBeanTCAMT;

	private String shareTo;
	/**
	 * 
	 */
	public void delTransaction(ActionEvent event) {
		this.dbManager.transactionDelete((Transaction) event.getComponent().getAttributes().get("transaction"));
		this.sessionBeanTCAMT.updateTransactions();
		this.init();
	}
	
	public void cloneTransaction(ActionEvent event) throws CloneNotSupportedException {	
		Transaction tsc = (Transaction)((Transaction)event.getComponent().getAttributes().get("transaction")).clone();
		tsc.setName("Copy_" + tsc.getName());
		tsc.setVersion(0);
		this.dbManager.transactionInsert(tsc, this.sessionBeanTCAMT.getLoggedId());
		this.sessionBeanTCAMT.updateTransactions();
		this.init();
	}

	public TransactionRequestBean() {
		super();
		this.init();
	}
	
	private void init(){
		this.newTransaction = new Transaction();
		this.editTransaction = new Transaction();
		this.existTransaction = new Transaction();
		this.newTestStepId = null;
		this.newTestStepIdForEdit = null;
	}

	public void createTransaction() {
		this.init();
	}

	public void addTransaction() throws CloneNotSupportedException, IOException {
		SequenceDiagramDraw sequenceDiagramDraw = new SequenceDiagramDraw();		
		this.drawSequenceDiagram(this.newTransaction, sequenceDiagramDraw);
		sequenceDiagramDraw.setTitle(this.newTransaction.getName());
		sequenceDiagramDraw.setHideFoot(true);
		this.newTransaction.setUmld(sequenceDiagramDraw.getUmlDiagram().getCode());
		
		this.dbManager.transactionInsert(this.newTransaction, this.sessionBeanTCAMT.getLoggedId());
		this.sessionBeanTCAMT.updateTransactions();
	}
	
	public void shareTransaction(){
		this.dbManager.transactionInsert(this.editTransaction, this.shareTo);
		this.sessionBeanTCAMT.updateTransactions();
	}
	
	public void updateTransaction() throws CloneNotSupportedException, IOException {
		SequenceDiagramDraw sequenceDiagramDraw = new SequenceDiagramDraw();		
		this.drawSequenceDiagram(this.editTransaction, sequenceDiagramDraw);
		sequenceDiagramDraw.setTitle(this.editTransaction.getName());
		sequenceDiagramDraw.setHideFoot(true);	
		this.editTransaction.setUmld(sequenceDiagramDraw.getUmlDiagram().getCode());
		this.editTransaction.setVersion(this.editTransaction.getVersion() + 1);
		
		this.dbManager.transactionUpdate(this.editTransaction);
		this.sessionBeanTCAMT.updateTransactions();
	}
	
	public DefaultStreamedContent loadUMLD(Transaction tsc) throws IOException{
		FacesContext context = FacesContext.getCurrentInstance();
	    HttpServletRequest myRequest = (HttpServletRequest) context.getExternalContext().getRequest();
	    String source =  myRequest.getParameter("code");
		if(tsc != null){
			source = tsc.getUmld();
			
		}
		
		ByteArrayOutputStream outputStream = null;
		byte[] bytes;
		outputStream = new ByteArrayOutputStream();
		SourceStringReader reader = new SourceStringReader(source);
		reader.generateImage(outputStream);
		bytes = outputStream.toByteArray();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        return new DefaultStreamedContent(inputStream, "image/png");
	}
	
	private void drawSequenceDiagram(Transaction transaction, SequenceDiagramDraw sequenceDiagramDraw){
		Set<String> actorSet = new HashSet<String>();
		
		for(Integer tsId:transaction.getTestSteps()){
			TestStep ts = this.dbManager.getTestStepById(tsId);
			
			if(ts.getType().equals("Interaction")){
				actorSet.add(ts.getInteraction().getsActor().getName());
				actorSet.add(ts.getInteraction().getrActor().getName());
			}else if(ts.getType().equals("Manual")){
				actorSet.add(ts.getTargetActor().getName());
			}else if(ts.getType().equals("Profile") || ts.getType().equals("Message")){
				actorSet.add("User");
				actorSet.add("TestBed");
			}
		}
		
		List<Participant> participants = new ArrayList<Participant>();
		List<Line> lines = new ArrayList<Line>();
		
		for(String s:actorSet){
			participants.add(new Participant(s, ""));
		}
		
		participants.add(new Participant("TestBed", ""));
		
		for(Integer tsId:transaction.getTestSteps()){
			
			TestStep ts = this.dbManager.getTestStepById(tsId);
			
			if(ts.getType().equals("Interaction")){
				Line line = new Line();
				
				line.setArrow("->");
				line.setMessage(ts.getInteraction().getMessage().getName());
				line.setSender(this.findParticipant(ts.getInteraction().getsActor().getName(), participants));
				line.setReceiver(this.findParticipant(ts.getInteraction().getrActor().getName(), participants));
				line.setType("sequence");

				lines.add(line);
			}else if(ts.getType().equals("Manual")){
				Line line = new Line();
				
				line.setArrow("->");
				line.setMessage(ts.getDescription());
				line.setSender(this.findParticipant(ts.getTargetActor().getName(), participants));
				line.setReceiver(this.findParticipant(ts.getTargetActor().getName(), participants));
				line.setType("sequence");

				lines.add(line);
			}else if(ts.getType().equals("Profile") || ts.getType().equals("Message")){
				Line line = new Line();
				
				line.setArrow("->");
				line.setMessage(ts.getInteraction().getMessage().getName());
				line.setSender(this.findParticipant("User", participants));
				line.setReceiver(this.findParticipant("TestBed", participants));
				line.setType("sequence");

				lines.add(line);
			}
		}
		
		sequenceDiagramDraw.setParticipants(participants);
		sequenceDiagramDraw.setLines(lines);
	}
	
	private Participant findParticipant(String name, List<Participant> participants){
		for(Participant p : participants){
			if(p.getName().equals(name)) return p;
		}
		return null;
	}
	
	public void saveTransaction() {
		this.editTransaction.setVersion(this.editTransaction.getVersion() + 1);
		this.dbManager.transactionUpdate(this.editTransaction);
		this.sessionBeanTCAMT.updateTransactions();
	}

	public void selectEditTransaction(ActionEvent event) {
		this.init();
		this.existTransaction = (Transaction) event.getComponent().getAttributes().get("transaction");
		this.editTransaction.setId(existTransaction.getId());
		this.editTransaction.setName(existTransaction.getName());
		this.editTransaction.setDescription(existTransaction.getDescription());
		this.editTransaction.setVersion(existTransaction.getVersion());
		this.editTransaction.setTestSteps(existTransaction.getTestSteps());
		this.editTransaction.setUmld(existTransaction.getUmld());
		
		this.shareTo = "";
	}
	
	public void addTestStep() throws CloneNotSupportedException{
		this.newTransaction.getTestSteps().add(newTestStepId);
		this.newTestStepId = null;
	}
	
	public void addTestStepForEdit() throws CloneNotSupportedException{
		this.editTransaction.getTestSteps().add(newTestStepIdForEdit);
		this.newTestStepIdForEdit = null;
	}
	
	public void delTestStep(ActionEvent event){
		int num = (Integer) event.getComponent().getAttributes().get("num");
		this.newTransaction.getTestSteps().remove(num);
	}
	
	public void moveUpTestStep(ActionEvent event){
		int num = (Integer) event.getComponent().getAttributes().get("num");
	    if(num != 0)	Collections.swap(this.newTransaction.getTestSteps(), num, num - 1);
	}
	
	public void moveDownTestStep(ActionEvent event){
		int num = (Integer) event.getComponent().getAttributes().get("num");
		if(num != this.newTransaction.getTestSteps().size() -1)	  Collections.swap(this.newTransaction.getTestSteps(), num, num + 1);
	}
	
	public void delTestStepForEdit(ActionEvent event){
		int num = (Integer) event.getComponent().getAttributes().get("num");
		this.editTransaction.getTestSteps().remove(num);
	}
	
	public void moveUpTestStepForEdit(ActionEvent event){
		int num = (Integer) event.getComponent().getAttributes().get("num");
	    if(num != 0)	Collections.swap(this.editTransaction.getTestSteps(), num, num - 1);
	}
	
	public void moveDownTestStepForEdit(ActionEvent event){
		int num = (Integer) event.getComponent().getAttributes().get("num");
		if(num != this.editTransaction.getTestSteps().size() -1)	  Collections.swap(this.editTransaction.getTestSteps(), num, num + 1);
	}
	
	public TestStep findTestStep(int id){
		return this.dbManager.getTestStepById(id);
	}
	

	/**
	 * 
	 */

	public Transaction getNewTransaction() {
		return newTransaction;
	}

	public void setNewTransaction(Transaction newTransaction) {
		this.newTransaction = newTransaction;
	}

	public Transaction getEditTransaction() {
		return editTransaction;
	}

	public void setEditTransaction(Transaction editTransaction) {
		this.editTransaction = editTransaction;
	}

	public Transaction getExistTransaction() {
		return existTransaction;
	}

	public void setExistTransaction(Transaction existTransaction) {
		this.existTransaction = existTransaction;
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

	public Integer getNewTestStepId() {
		return newTestStepId;
	}

	public void setNewTestStepId(Integer newTestStepId) {
		this.newTestStepId = newTestStepId;
	}

	public Integer getNewTestStepIdForEdit() {
		return newTestStepIdForEdit;
	}

	public void setNewTestStepIdForEdit(Integer newTestStepIdForEdit) {
		this.newTestStepIdForEdit = newTestStepIdForEdit;
	}
	
	public List<Transaction> getTransactions(){
		return this.sessionBeanTCAMT.getTransactions();
	}

	public String getShareTo() {
		return shareTo;
	}

	public void setShareTo(String shareTo) {
		this.shareTo = shareTo;
	}
	
}
