package gov.nist.healthcare.tcamt.view;

import gov.nist.healthcare.tcamt.db.DBImpl;
import gov.nist.healthcare.tcamt.domain.Actor;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

@ManagedBean
@SessionScoped
public class UserControl {

	private String id;
	private String password;
	
	private String newId;
	private String newPassword;
	private String selectedUser;
	

	private DBImpl dbManager = new DBImpl();

	@ManagedProperty("#{sessionBeanTCAMT}")
	private SessionBeanTCAMT sessionBeanTCAMT;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public void loginAction(ActionEvent actionEvent) {
		boolean result = dbManager.getUserPasswordById(this.id, this.password);

		if (result) {
			addMessage("Hello " + this.id);
			sessionBeanTCAMT.setLoggedId(this.id);
		} else {
			this.id = null;
			sessionBeanTCAMT.setLoggedId(null);
			addMessage("Sorry but failed!");
		}
		
		sessionBeanTCAMT.retriveAllData();
	}

	public void logoutAction(ActionEvent actionEvent) {
		this.setId(null);
		sessionBeanTCAMT.setLoggedId(null);
		addMessage("Bye");
		
		sessionBeanTCAMT.retriveAllData();
	}
	
	public void editUser(){
		this.dbManager.modifyPassWord(this.selectedUser, this.newPassword);
		this.selectedUser = new String();
		this.newPassword = new String();
	}
	
	public void selectEditUser(ActionEvent event) {
		this.selectedUser = (String) event.getComponent().getAttributes().get("user");
	}
	
	
	public void delUser(ActionEvent event) {
		this.dbManager.deleteUset((String) event.getComponent().getAttributes().get("user"));
	}
	
	public void createUser() {
		this.newId = new String();
		this.newPassword = new String();
	}
	
	public void addUser() {
		this.dbManager.addUser(this.newId, this.newPassword);
		this.newPassword = new String();
		this.newId = new String();
	}

	public void addMessage(String summary) {
		FacesMessage msgs = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, null);
		FacesContext.getCurrentInstance().addMessage(null, msgs);
	}

	public SessionBeanTCAMT getSessionBeanTCAMT() {
		return sessionBeanTCAMT;
	}

	public void setSessionBeanTCAMT(SessionBeanTCAMT sessionBeanTCAMT) {
		this.sessionBeanTCAMT = sessionBeanTCAMT;
	}
	
	public List<String> getUsers(){
		return dbManager.getAllUsers();
	}

	public String getNewId() {
		return newId;
	}

	public void setNewId(String newId) {
		this.newId = newId;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getSelectedUser() {
		return selectedUser;
	}

	public void setSelectedUser(String selectedUser) {
		this.selectedUser = selectedUser;
	}
	
	
}
