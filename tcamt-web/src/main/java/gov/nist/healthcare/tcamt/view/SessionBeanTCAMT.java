package gov.nist.healthcare.tcamt.view;

import gov.nist.healthcare.tcamt.db.DBImpl;
import gov.nist.healthcare.tcamt.domain.Actor;
import gov.nist.healthcare.tcamt.domain.Interaction;
import gov.nist.healthcare.tcamt.domain.Message;
import gov.nist.healthcare.tcamt.domain.TestCase;
import gov.nist.healthcare.tcamt.domain.TestPlan;
import gov.nist.healthcare.tcamt.domain.Transaction;
import gov.nist.healthcare.tcamt.domain.TestStep;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.event.TabChangeEvent;

@ManagedBean
@SessionScoped
public class SessionBeanTCAMT implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 432938680402529031L;
	
	private DBImpl myDBManager = new DBImpl();
	
	private List<Actor> actors;
	private List<Message> messages;
	private List<Interaction> interactions;
	private List<TestStep> teststeps;
	private List<Transaction> transactions;
	private List<TestCase> testcases;
	private List<TestPlan> testplans;
	
	private String loggedId;

	/**
	 * 
	 */

	
	
	public void onTabChange(TabChangeEvent event) {
		String tabTitle = event.getTab().getTitle();
		if(tabTitle.equals("Actor")){
			this.updateActors();
		}else if(tabTitle.equals("Message")){
			this.updateMessages();
		}else if(tabTitle.equals("Interaction")){
			this.updateActors();
			this.updateMessages();
			this.updateInteractions();
		}else if(tabTitle.equals("Test Step")){
			this.updateInteractions();
			this.updateTestSteps();
		}else if(tabTitle.equals("Transaction")){
			this.updateTestSteps();
			this.updateTransactions();
		}else if(tabTitle.equals("Test Case")){
			this.updateTestCases();
		}else if(tabTitle.equals("Test Plan")){
			this.updateTestPlans();
		}
    }
	
	public SessionBeanTCAMT() {
		super();
		this.updateActors();
		this.updateMessages();
		this.updateInteractions();
		this.updateTestSteps();
		this.updateTransactions();
		this.updateTestCases();
		this.updateTestPlans();
	}

	public void updateActors(){
		this.actors = this.myDBManager.getAllActors(this.loggedId);
	}
	
	public void updateMessages(){
		this.messages = this.myDBManager.getAllMessages(this.loggedId);
	}
	
	public void updateInteractions(){
		this.interactions = this.myDBManager.getAllInteractions(this.loggedId);
	}
	
	public void updateTestSteps(){
		this.teststeps = this.myDBManager.getAllTestSteps(this.loggedId);
	}
	
	public void updateTransactions(){
		this.transactions = this.myDBManager.getAllTransactions(this.loggedId);
	}
	
	public void updateTestCases(){
		this.testcases = this.myDBManager.getAllTestCases(this.loggedId);
	}
	
	public void updateTestPlans(){
		this.testplans = this.myDBManager.getAllTestPlans(this.loggedId);
	}
	
	public boolean isLatestActor(Actor actor){
		if(actor != null){
			for(Actor a:this.actors){
				if(a.getId().equals(actor.getId())){
					if(a.getVersion().intValue() == actor.getVersion().intValue()) return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean isLatestMessage(Message message){
		if(message != null){
			for(Message m:this.messages){
				if(m.getId().equals(message.getId())){
					if(m.getVersion().intValue() == message.getVersion().intValue()) return true;
				}
			}
		}
		return false;
	}
	
	public boolean isLatestInteraction(Interaction interaction){
		if(interaction != null){
			for(Interaction i:this.interactions){
				if(i.getId().equals(interaction.getId())){
					if(i.getVersion().intValue() == interaction.getVersion().intValue()){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public List<TestPlan> getTestplans() {
		return testplans;
	}

	public void setTestplans(List<TestPlan> testplans) {
		this.testplans = testplans;
	}

	public List<Actor> getActors() {
		return actors;
	}

	public void setActors(List<Actor> actors) {
		this.actors = actors;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

	public List<Interaction> getInteractions() {
		return interactions;
	}

	public void setInteractions(List<Interaction> interactions) {
		this.interactions = interactions;
	}
	
	public List<TestStep> getTeststeps() {
		return teststeps;
	}

	public void setTeststeps(List<TestStep> teststeps) {
		this.teststeps = teststeps;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}

	public List<TestCase> getTestcases() {
		return testcases;
	}

	public void setTestcases(List<TestCase> testcases) {
		this.testcases = testcases;
	}

	public String getLoggedId() {
		return loggedId;
	}

	public void setLoggedId(String loggedId) {
		this.loggedId = loggedId;
	}

	public void retriveAllData() {
		this.updateActors();
		this.updateMessages();
		this.updateInteractions();
		this.updateTestSteps();
		this.updateTransactions();
		this.updateTestCases();
		this.updateTestPlans();
		
	}
	
	
}
