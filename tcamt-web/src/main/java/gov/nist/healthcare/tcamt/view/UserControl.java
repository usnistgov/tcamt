package gov.nist.healthcare.tcamt.view;

import gov.nist.healthcare.tcamt.domain.User;

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

	private User user = new User();
	private User newUser = new User();

	@ManagedProperty("#{sessionBeanTCAMT}")
	private SessionBeanTCAMT sessionBeanTCAMT;
	
	public void loginAction(ActionEvent actionEvent) {
		this.user = this.sessionBeanTCAMT.getDbManager().isValidUser(user);

		if (this.user != null) {
			addMessage("Hello " + this.user.getUserId());
			sessionBeanTCAMT.setLoggedUser(user);
		} else {
			this.user = new User();
			sessionBeanTCAMT.setLoggedUser(null);
			addMessage("Sorry but failed!");
		}
		
		sessionBeanTCAMT.retriveAllData();
	}

	public void logoutAction(ActionEvent actionEvent) {
		this.user = new User();
		sessionBeanTCAMT.setLoggedUser(null);
		addMessage("Bye");
		
		sessionBeanTCAMT.retriveAllData();
	}
	
	public void editUser(){
		this.sessionBeanTCAMT.getDbManager().updateUser(newUser);
		this.newUser = new User();
	}
	
	public void selectEditUser(ActionEvent event) {
		this.newUser = (User) event.getComponent().getAttributes().get("user");
	}
	
	
	public void delUser(ActionEvent event) {
		this.sessionBeanTCAMT.getDbManager().deleteUser((User) event.getComponent().getAttributes().get("user"));
	}
	
	public void createUser() {
		this.newUser = new User();
	}
	
	public void addUser() {
		this.sessionBeanTCAMT.getDbManager().addUser(this.newUser);
		this.newUser = new User();
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
	
	public List<User> getUsers(){
		return this.sessionBeanTCAMT.getDbManager().getAllUsers();
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getNewUser() {
		return newUser;
	}

	public void setNewUser(User newUser) {
		this.newUser = newUser;
	}
	
	
}
