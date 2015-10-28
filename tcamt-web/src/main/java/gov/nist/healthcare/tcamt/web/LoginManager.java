package gov.nist.healthcare.tcamt.web;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

public class LoginManager implements HttpSessionBindingListener {

	private static LoginManager loginManager = null;

	// Current Logged Users' Containers
	private static Hashtable<HttpSession, String> loginUsers = new Hashtable<HttpSession, String>();

	// Singleton
	public static synchronized LoginManager getInstance() {
		if (loginManager == null) {
			loginManager = new LoginManager();
		}
		return loginManager;
	}

	/*
	 * When session is connected, this method will be
	 * called.(session.setAttribute("login", this)) Hashtable session and User
	 * Id will be stored.
	 */
	public void valueBound(HttpSessionBindingEvent event) {
		loginUsers.put(event.getSession(), event.getName());
		System.out.println(event.getName() + " is logged.");
		System.out.println("The number of logged users : "
				+ this.getUserCount());
	}

	/*
	 * When the session is disabled, this method will be called. (invalidate) It
	 * will remove logged user on the Hashtable.
	 */
	public void valueUnbound(HttpSessionBindingEvent event) {
		// session값을 찾아서 없애준다.
		loginUsers.remove(event.getSession());
		System.out.println(" " + event.getName() + " is logout");
		System.out.println("The number of logged users : "
				+ this.getUserCount());
	}

	public void removeSession(String userId) {
		Enumeration<HttpSession> e = loginUsers.keys();
		HttpSession session = null;
		while (e.hasMoreElements()) {
			session = (HttpSession) e.nextElement();
			if (loginUsers.get(session).equals(userId)) {
				session.invalidate();
			}
		}
	}

	public boolean isUsing(String userID) {
		return loginUsers.containsValue(userID);
	}

	public void setSession(HttpSession session, String userId) {
		session.setAttribute(userId, this);
	}

	public String getUserID(HttpSession session) {
		return (String) loginUsers.get(session);
	}

	public int getUserCount() {
		return loginUsers.size();
	}

	public void printloginUsers() {
		Enumeration<HttpSession> e = loginUsers.keys();
		HttpSession session = null;
		System.out.println("===========================================");
		int i = 0;
		while (e.hasMoreElements()) {
			session = (HttpSession) e.nextElement();
			System.out.println((++i) + ". User : " + loginUsers.get(session));
		}
		System.out.println("===========================================");
	}

	public Collection<String> getUsers() {
		Collection<String> collection = loginUsers.values();
		return collection;
	}

}
