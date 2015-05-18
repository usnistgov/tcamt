package gov.nist.healthcare.tcamt.db;

import gov.nist.healthcare.tcamt.domain.Actor;
import gov.nist.healthcare.tcamt.domain.DataInstanceTestPlan;
import gov.nist.healthcare.tcamt.domain.IsolatedTestPlan;
import gov.nist.healthcare.tcamt.domain.Message;
import gov.nist.healthcare.tcamt.domain.User;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;

public class DBImpl implements DBInterface, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7820251230674334331L;
	private Session currentSession;
	private Transaction currentTransaction;

	public Session openCurrentSession() {
		currentSession = getSessionFactory().openSession();
		return currentSession;
	}

	public Session openCurrentSessionwithTransaction() {
		currentSession = getSessionFactory().openSession();
		currentTransaction = currentSession.beginTransaction();
		return currentSession;
	}

	public void closeCurrentSession() {
		currentSession.close();
	}

	public void closeCurrentSessionwithTransaction() {
		currentTransaction.commit();
		currentSession.close();
	}

	@SuppressWarnings("deprecation")
	private static SessionFactory getSessionFactory() {
		try {
			Configuration configuration = new Configuration().configure();
			return configuration.buildSessionFactory();
		} catch (Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public User isValidUser(User user) {
		this.openCurrentSession();
		Criteria criteria = this.currentSession.createCriteria(User.class);
		criteria.add(Restrictions.eq("userId", user.getUserId()));

		@SuppressWarnings("unchecked")
		List<User> users = criteria.list();
		this.closeCurrentSession();
		if (users == null || users.size() == 0)
			return null;
		if (users.get(0).getPassword().equals(user.getPassword()))
			return users.get(0);
		return null;
	}

	public User getUserById(long id) {
		this.openCurrentSession();
		User u = (User)this.currentSession.get(User.class, id);
		this.closeCurrentSession();
		return u;
	}

	@SuppressWarnings("unchecked")
	public List<User> getAllUsers() {
		this.openCurrentSession();
		Criteria criteria = this.currentSession.createCriteria(User.class);
		List<User> results = criteria.list();
		this.closeCurrentSession();
		return results;
	}

	public void addUser(User user) {
		this.openCurrentSessionwithTransaction();
		this.currentSession.save(user);
		this.closeCurrentSessionwithTransaction();
	}

	public void updateUser(User user) {
		this.openCurrentSessionwithTransaction();
		this.currentSession.update(user);
		this.closeCurrentSessionwithTransaction();
	}

	public void deleteUser(User user) {
		this.openCurrentSessionwithTransaction();
		this.currentSession.delete(user);
		this.closeCurrentSessionwithTransaction();
	}

	public void actorInsert(Actor a) {
		this.openCurrentSessionwithTransaction();
		this.currentSession.save(a);
		this.closeCurrentSessionwithTransaction();
	}

	public void actorUpdate(Actor a) {
		this.openCurrentSessionwithTransaction();
		this.currentSession.update(a);
		this.closeCurrentSessionwithTransaction();
	}

	public void actorDelete(Actor a) {
		this.openCurrentSessionwithTransaction();
		this.currentSession.delete(a);
		this.closeCurrentSessionwithTransaction();

	}

	@SuppressWarnings("unchecked")
	public List<Actor> getAllActors(User author) {
		if (author == null) return null;
		this.openCurrentSession();
		Criteria criteria = this.currentSession.createCriteria(Actor.class);
		criteria.add(Restrictions.eq("author", author));
		List<Actor> results = (List<Actor>) criteria.list();
		this.closeCurrentSession();
		return results;
	}

	public Actor getActorById(long id) {
		this.openCurrentSession();
		Actor a = (Actor)this.currentSession.get(Actor.class, id);
		this.closeCurrentSession();
		return a;
	}

	public void messageInsert(Message m) {
		this.openCurrentSessionwithTransaction();
		this.currentSession.save(m);
		this.closeCurrentSessionwithTransaction();
	}

	public void messageUpdate(Message m) {
		this.openCurrentSessionwithTransaction();
		this.currentSession.update(m);
		this.closeCurrentSessionwithTransaction();
	}

	public void messageDelete(Message m) {
		this.openCurrentSessionwithTransaction();
		this.currentSession.delete(m);
		this.closeCurrentSessionwithTransaction();
	}

	@SuppressWarnings("unchecked")
	public List<Message> getAllMessages(User author) {
		if (author == null) return null;
		this.openCurrentSession();
		Criteria criteria = this.currentSession.createCriteria(Message.class);
		criteria.add(Restrictions.eq("author", author));
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		List<Message> results = (List<Message>) criteria.list();
		this.closeCurrentSession();
		return results;
	}

	public Message getMessageById(long id) {
		this.openCurrentSession();
		Message m = (Message)this.currentSession.get(Message.class, id);
		this.closeCurrentSession();
		return m;
	}

	public void dataInstanceTestPlanInsert(DataInstanceTestPlan ditp) {
		this.openCurrentSessionwithTransaction();
		this.currentSession.save(ditp);
		this.closeCurrentSessionwithTransaction();
	}

	public void dataInstanceTestPlanUpdate(DataInstanceTestPlan ditp) {
		this.openCurrentSessionwithTransaction();
		this.currentSession.update(ditp);
		this.closeCurrentSessionwithTransaction();
	}

	public void dataInstanceTestPlanDelete(DataInstanceTestPlan ditp) {
		this.openCurrentSessionwithTransaction();
		this.currentSession.delete(ditp);
		this.closeCurrentSessionwithTransaction();
	}

	public List<DataInstanceTestPlan> getAllDataInstanceTestPlans(User author) {
		if (author == null) return null;
		this.openCurrentSession();
		Criteria criteria = this.currentSession.createCriteria(DataInstanceTestPlan.class);
		criteria.add(Restrictions.eq("author", author));
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		@SuppressWarnings("unchecked")
		List<DataInstanceTestPlan> results = (List<DataInstanceTestPlan>) criteria.list();
		this.closeCurrentSession();
		return results;
	}

	public DataInstanceTestPlan getDataInstanceTestPlanById(long id) {
		this.openCurrentSession();
		Criteria criteria = this.currentSession.createCriteria(DataInstanceTestPlan.class);
		criteria.add(Restrictions.eq("id", id));
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		@SuppressWarnings("unchecked")
		List<DataInstanceTestPlan> results = (List<DataInstanceTestPlan>) criteria.list();
		this.closeCurrentSession();
		
		if(results == null || results.size() == 0){
			return null;
		}else {
			return results.get(0);
		}
	}

	public void isolatedTestPlanInsert(IsolatedTestPlan itp) {
		this.openCurrentSessionwithTransaction();
		this.currentSession.save(itp);
		this.closeCurrentSessionwithTransaction();
	}

	public void isolatedTestPlanUpdate(IsolatedTestPlan itp) {
		this.openCurrentSessionwithTransaction();
		this.currentSession.update(itp);
		this.closeCurrentSessionwithTransaction();
	}

	public void isolatedTestPlanDelete(IsolatedTestPlan itp) {
		this.openCurrentSessionwithTransaction();
		this.currentSession.delete(itp);
		this.closeCurrentSessionwithTransaction();
	}

	public List<IsolatedTestPlan> getAllIsolatedTestPlans(User author) {
		if (author == null) return null;
		this.openCurrentSession();
		Criteria criteria = this.currentSession.createCriteria(IsolatedTestPlan.class);
		criteria.add(Restrictions.eq("author", author));
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		@SuppressWarnings("unchecked")
		List<IsolatedTestPlan> results = (List<IsolatedTestPlan>) criteria.list();
		this.closeCurrentSession();
		return results;
	}

	public IsolatedTestPlan getIsolatedTestPlanById(long id) {
		this.openCurrentSession();
		IsolatedTestPlan plan = (IsolatedTestPlan)this.currentSession.get(IsolatedTestPlan.class, id);
		this.closeCurrentSession();
		return plan;
	}
}
