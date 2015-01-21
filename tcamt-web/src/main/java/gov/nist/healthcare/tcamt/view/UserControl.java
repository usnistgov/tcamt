package gov.nist.healthcare.tcamt.view;

import gov.nist.healthcare.tcamt.db.DBImpl;

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
	
	
}
