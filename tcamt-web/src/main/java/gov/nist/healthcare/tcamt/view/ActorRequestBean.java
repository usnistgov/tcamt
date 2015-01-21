package gov.nist.healthcare.tcamt.view;

import gov.nist.healthcare.tcamt.db.DBImpl;
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
	
	private String shareTo;
	
	private DBImpl dbManager = new DBImpl();
	
	/**
	 * 
	 */

	public void delActor(ActionEvent event) {
		this.dbManager.actorDelete((Actor) event.getComponent().getAttributes().get("actor"));
		this.sessionBeanTCAMT.updateActors();
	}
	
	public void cloneActor(ActionEvent event) throws CloneNotSupportedException {	
		Actor a = (Actor)((Actor)event.getComponent().getAttributes().get("actor")).clone();
		a.setName("Copy_" + a.getName());
		a.setVersion(0);
		this.dbManager.actorInsert(a, this.sessionBeanTCAMT.getLoggedId());
		this.sessionBeanTCAMT.updateActors();
	}
	
	public void createActor() {
		this.newActor = new Actor();
	}
	
	public void addActor() {
		this.dbManager.actorInsert(this.newActor, this.sessionBeanTCAMT.getLoggedId());
		this.sessionBeanTCAMT.updateActors();
	}
	
	public void shareActor() {
		this.dbManager.actorInsert(this.editActor, this.shareTo);
		this.sessionBeanTCAMT.updateActors();
	}
	
	public void editActor() {
		this.editActor.setVersion(this.editActor.getVersion() + 1);
		this.dbManager.actorUpdate(this.editActor);
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
		this.setShareTo("");
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

	public String getShareTo() {
		return shareTo;
	}

	public void setShareTo(String shareTo) {
		this.shareTo = shareTo;
	}
	
}
