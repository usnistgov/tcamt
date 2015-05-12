package gov.nist.healthcare.tcamt.view;

import gov.nist.healthcare.tcamt.domain.Actor;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;

@ManagedBean
@SessionScoped
public class ActorRequestBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 432938680402529031L;
	
	@ManagedProperty("#{sessionBeanTCAMT}")
	private SessionBeanTCAMT sessionBeanTCAMT;
	
	private Actor newActor = new Actor();
	private Actor editActor = new Actor();
	private Actor existActor;
	
	private Long shareTo;
	
	/**
	 * 
	 */

	public void delActor(ActionEvent event) {
		this.sessionBeanTCAMT.getDbManager().actorDelete((Actor) event.getComponent().getAttributes().get("actor"));
		this.sessionBeanTCAMT.updateActors();
	}
	
	public void cloneActor(ActionEvent event) throws CloneNotSupportedException {	
		Actor a = (Actor)((Actor)event.getComponent().getAttributes().get("actor")).clone();
		a.setName("Copy_" + a.getName());
		a.setVersion(0);
		a.setAuthor(this.sessionBeanTCAMT.getLoggedUser());
		
		this.sessionBeanTCAMT.getDbManager().actorInsert(a);
		this.sessionBeanTCAMT.updateActors();
	}
	
	public void createActor() {
		this.newActor = new Actor();
	}
	
	public void addActor() {
		newActor.setAuthor(this.sessionBeanTCAMT.getLoggedUser());
		newActor.setVersion(1);
		this.sessionBeanTCAMT.getDbManager().actorInsert(this.newActor);
		this.sessionBeanTCAMT.updateActors();
	}
	
	public void shareActor() {
		System.out.println("SAHRE!!!!");
		
		
		
		this.editActor.setAuthor(this.sessionBeanTCAMT.getDbManager().getUserById(this.shareTo));
		this.sessionBeanTCAMT.getDbManager().actorInsert(this.editActor);
		this.sessionBeanTCAMT.updateActors();
	}
	
	public void editActor() {
		this.editActor.setVersion(this.editActor.getVersion() + 1);
		this.sessionBeanTCAMT.getDbManager().actorUpdate(this.editActor);
		this.sessionBeanTCAMT.updateActors();
	}
	
	public void selectEditActor(ActionEvent event) {
		this.editActor = new Actor();
		
		this.existActor = (Actor)event.getComponent().getAttributes().get("actor");
		this.editActor.setId(existActor.getId());
		this.editActor.setName(existActor.getName());
		this.editActor.setReference(existActor.getReference());
		this.editActor.setRole(existActor.getRole());
		this.editActor.setVersion(existActor.getVersion());
		this.editActor.setAuthor(existActor.getAuthor());
		this.setShareTo(null);
	}

	/**
	 * 
	 */

	public Actor getNewActor() {
		return newActor;
	}

	public void setNewActor(Actor newActor) {
		this.newActor = newActor;
	}

	public Actor getEditActor() {
		return editActor;
	}

	public void setEditActor(Actor editActor) {
		this.editActor = editActor;
	}
	
	public List<Actor> getActors(){
		return this.sessionBeanTCAMT.getActors();
	}
	
	public SessionBeanTCAMT getSessionBeanTCAMT() {
		return sessionBeanTCAMT;
	}

	public void setSessionBeanTCAMT(SessionBeanTCAMT sessionBeanTCAMT) {
		this.sessionBeanTCAMT = sessionBeanTCAMT;
	}

	public Long getShareTo() {
		return shareTo;
	}

	public void setShareTo(Long shareTo) {
		this.shareTo = shareTo;
	}
	
}
