package gov.nist.healthcare.tcamt.view;

import gov.nist.healthcare.tcamt.domain.User;
import gov.nist.healthcare.tcamt.web.LoginManager;

import java.io.Serializable;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;

@ManagedBean
@SessionScoped
public class UserControl implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5719566433011463506L;
	private User user = new User();
	private User newUser = new User();
	
	private String killUserId;

	@ManagedProperty("#{sessionBeanTCAMT}")
	private SessionBeanTCAMT sessionBeanTCAMT;
	
	public void loginAction(ActionEvent actionEvent) {
		LoginManager loginManager = LoginManager.getInstance();
		this.user = this.sessionBeanTCAMT.getDbManager().isValidUser(user);
		if (this.user != null) {
			
			if(loginManager.isUsing(this.user.getUserId())){
				this.killUserId = this.user.getUserId();
				this.user = new User();
				sessionBeanTCAMT.setLoggedUser(null);
				FacesContext.getCurrentInstance().addMessage("userMessage", new FacesMessage(FacesMessage.SEVERITY_ERROR, 
						"Login Error!", 
						"ID is currently used at IP_<a target='_blank' href='https://db-ip.com/"
						+ loginManager.getLoggedIP(killUserId) 
						+ "'>"+loginManager.getLoggedIP(killUserId) 
						+ "</a>."));				
			}else {
				sessionBeanTCAMT.setLoggedUser(user);
				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage("userMessage", new FacesMessage(FacesMessage.SEVERITY_INFO, "Hello, " + this.user.getUserId()  + ".", "You have susscessfully logged in."));
				loginManager.setSession((HttpSession)context.getExternalContext().getSession(true), this.user.getUserId());
				this.killUserId = null;
			}
			loginManager.printloginUsers();
		} else {
			this.user = new User();
			sessionBeanTCAMT.setLoggedUser(null);
			FacesContext.getCurrentInstance().addMessage("userMessage", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login Error!", "Invalid User ID or password."));
			this.killUserId = null;
		}
		
		sessionBeanTCAMT.retriveAllData();
	}

	public void logoutAction(ActionEvent actionEvent) {
		FacesContext.getCurrentInstance().addMessage("userMessage", new FacesMessage(FacesMessage.SEVERITY_INFO, "Bye, " + this.user.getUserId() + ".", "You have susscessfully logged out."));
		LoginManager loginManager = LoginManager.getInstance();
		loginManager.removeSession(this.user.getUserId());
		loginManager.printloginUsers();
		this.user = new User();
		sessionBeanTCAMT.setLoggedUser(null);
		sessionBeanTCAMT.retriveAllData();
		this.killUserId = null;
	}
	
	public void killUserIdAction(ActionEvent actionEvent) {
		LoginManager loginManager = LoginManager.getInstance();
		HttpSession session = loginManager.findSession(killUserId);
		if(session != null) session.invalidate();
		this.killUserId = null;
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

	public String getKillUserId() {
		return killUserId;
	}

	public void setKillUserId(String killUserId) {
		this.killUserId = killUserId;
	}
	
	
}
