package gov.nist.healthcare.tcamt.view;

import gov.nist.healthcare.tcamt.db.DBImpl;
import gov.nist.healthcare.tcamt.domain.Actor;
import gov.nist.healthcare.tcamt.domain.ConformanceProfile;
import gov.nist.healthcare.tcamt.domain.DataInstanceTestPlan;
import gov.nist.healthcare.tcamt.domain.Message;
import gov.nist.healthcare.tcamt.domain.User;

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
	
	private DBImpl dbManager = new DBImpl();
	
	private List<ConformanceProfile> conformanceProfiles;
	private List<Actor> actors;
	private List<Message> messages;
	private List<DataInstanceTestPlan> dataInstanceTestPlans;
	
	private int mActiveIndex = 0;
	private int ditActiveIndex = 0;
	private int itActiveIndex = 0;
	
	private User loggedUser;

	/**
	 * 
	 */
	
	public void onTabChange(TabChangeEvent event) {
		String tabTitle = event.getTab().getTitle();
		if(tabTitle.equals("Profile")){
			this.updateConformanceProfiles();
		}else if(tabTitle.equals("Actor")){
			this.updateActors();
		}else if(tabTitle.equals("Message")){
			mActiveIndex = 0;
			this.updateMessages();
		}else if(tabTitle.equals("Test Plan")){
			this.updateDataInstanceTestPlans();
			ditActiveIndex = 0;
		}
    }
	
	public SessionBeanTCAMT() {
		super();
		this.updateConformanceProfiles();
		this.updateActors();
		this.updateMessages();
		this.updateDataInstanceTestPlans();
	}

	public void updateConformanceProfiles(){
		this.conformanceProfiles = this.dbManager.getAllConformanceProfiles();
	}
	
	public void updateActors(){
		this.actors = this.dbManager.getAllActors(this.loggedUser);
	}
	
	public void updateMessages(){
		this.messages = this.dbManager.getAllMessages(this.loggedUser);
	}
	
	public void updateDataInstanceTestPlans(){
		this.dataInstanceTestPlans = this.dbManager.getAllDataInstanceTestPlans(this.loggedUser);
	}
	
	public boolean isLatestActor(Actor actor){
		if(actor != null){
			for(Actor a:this.actors){
				if(a.getId() == actor.getId()){
					if(a.getVersion().intValue() == actor.getVersion().intValue()) return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean isLatestMessage(Message message){
		if(message != null){
			for(Message m:this.messages){
				if(m.getId() == message.getId()){
					if(m.getVersion().intValue() == message.getVersion().intValue()) return true;
				}
			}
		}
		return false;
	}
	
	public void retriveAllData() {
		this.updateConformanceProfiles();
		this.updateActors();
		this.updateMessages();
		this.updateDataInstanceTestPlans();
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

	public User getLoggedUser() {
		return loggedUser;
	}

	public void setLoggedUser(User loggedUser) {
		this.loggedUser = loggedUser;
	}

	public List<DataInstanceTestPlan> getDataInstanceTestPlans() {
		return dataInstanceTestPlans;
	}

	public void setDataInstanceTestPlans(List<DataInstanceTestPlan> dataInstanceTestPlans) {
		this.dataInstanceTestPlans = dataInstanceTestPlans;
	}
	
	public DBImpl getDbManager() {
		return dbManager;
	}

	public void setDbManager(DBImpl dbManager) {
		this.dbManager = dbManager;
	}

	public int getDitActiveIndex() {
		return ditActiveIndex;
	}

	public void setDitActiveIndex(int ditActiveIndex) {
		this.ditActiveIndex = ditActiveIndex;
	}

	public int getItActiveIndex() {
		return itActiveIndex;
	}

	public void setItActiveIndex(int itActiveIndex) {
		this.itActiveIndex = itActiveIndex;
	}

	public int getmActiveIndex() {
		return mActiveIndex;
	}

	public void setmActiveIndex(int mActiveIndex) {
		this.mActiveIndex = mActiveIndex;
	}

	public List<ConformanceProfile> getConformanceProfiles() {
		return conformanceProfiles;
	}

	public void setConformanceProfiles(List<ConformanceProfile> conformanceProfiles) {
		this.conformanceProfiles = conformanceProfiles;
	}
	
}
