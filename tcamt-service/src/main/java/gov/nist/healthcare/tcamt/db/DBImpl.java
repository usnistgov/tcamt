package gov.nist.healthcare.tcamt.db;

import gov.nist.healthcare.tcamt.domain.Actor;
import gov.nist.healthcare.tcamt.domain.DataInstanceTestPlan;
import gov.nist.healthcare.tcamt.domain.IsolatedTestPlan;
import gov.nist.healthcare.tcamt.domain.Message;
import gov.nist.healthcare.tcamt.domain.User;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
	private SessionFactory sessionFactory;
	private Session currentSession;
	private Transaction currentTransaction;

	
	
	public DBImpl() {
		super();
		System.out.println("DBImpl Object Created!");
		sessionFactory = DBImpl.getSessionFactory();
		this.currentSession = sessionFactory.openSession();
	}
	
	public void finalize() {
		System.out.println("DBImpl Object Closed!");
		currentSession.close();
		this.sessionFactory.close();
	}

	public Session openCurrentSession() {
		currentSession = this.sessionFactory.getCurrentSession();
		this.currentTransaction = currentSession.beginTransaction();
		return currentSession;
	}

	public void closeCurrentSession() {
		currentTransaction.commit();
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
		this.openCurrentSession();
		this.currentSession.save(user);
		this.closeCurrentSession();
	}

	public void updateUser(User user) {
		this.openCurrentSession();
		this.currentSession.update(user);
		this.closeCurrentSession();
	}

	public void deleteUser(User user) {
		this.openCurrentSession();
		this.currentSession.delete(user);
		this.closeCurrentSession();
	}

	public void actorInsert(Actor a) {
		this.openCurrentSession();
		this.currentSession.save(a);
		this.closeCurrentSession();
	}

	public void actorUpdate(Actor a) {
		this.openCurrentSession();
		this.currentSession.update(a);
		this.closeCurrentSession();
	}

	public void actorDelete(Actor a) {
		this.openCurrentSession();
		this.currentSession.delete(a);
		this.closeCurrentSession();

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
		this.openCurrentSession();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		m.setLastUpdateDate(dateFormat.format(date));
		
		this.currentSession.save(m);
		this.closeCurrentSession();
	}

	public void messageUpdate(Message m) {
		this.openCurrentSession();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		m.setLastUpdateDate(dateFormat.format(date));
		this.currentSession.update(m);
		this.closeCurrentSession();
	}

	public void messageDelete(Message m) {
		this.openCurrentSession();
		this.currentSession.delete(m);
		this.closeCurrentSession();
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
		this.openCurrentSession();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		ditp.setLastUpdateDate(dateFormat.format(date));
		this.currentSession.save(ditp);
		this.closeCurrentSession();
	}

	public void dataInstanceTestPlanUpdate(DataInstanceTestPlan ditp) {
		this.openCurrentSession();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		ditp.setLastUpdateDate(dateFormat.format(date));
		this.currentSession.update(ditp);
		this.closeCurrentSession();
	}

	public void dataInstanceTestPlanDelete(DataInstanceTestPlan ditp) {
		this.openCurrentSession();
		this.currentSession.delete(ditp);
		this.closeCurrentSession();
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
		this.openCurrentSession();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		itp.setLastUpdateDate(dateFormat.format(date));
		this.currentSession.save(itp);
		this.closeCurrentSession();
	}

	public void isolatedTestPlanUpdate(IsolatedTestPlan itp) {
		this.openCurrentSession();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		itp.setLastUpdateDate(dateFormat.format(date));
		this.currentSession.update(itp);
		this.closeCurrentSession();
	}

	public void isolatedTestPlanDelete(IsolatedTestPlan itp) {
		this.openCurrentSession();
		this.currentSession.delete(itp);
		this.closeCurrentSession();
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
