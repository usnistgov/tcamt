package gov.nist.healthcare.tcamt.db;

import gov.nist.healthcare.tcamt.domain.Actor;
import gov.nist.healthcare.tcamt.domain.ConformanceProfile;
import gov.nist.healthcare.tcamt.domain.ContextFreeTestPlan;
import gov.nist.healthcare.tcamt.domain.DataInstanceTestPlan;
import gov.nist.healthcare.tcamt.domain.IntegratedProfile;
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
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
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
	
	public void integratedProfileInsert(IntegratedProfile ip) {
		this.openCurrentSession();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		ip.setLastUpdateDate(dateFormat.format(date));
		this.currentSession.save(ip);
		this.closeCurrentSession();
	}

	public void integratedProfileUpdate(IntegratedProfile ip) {
		this.openCurrentSession();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		ip.setLastUpdateDate(dateFormat.format(date));
		this.currentSession.update(ip);
		this.closeCurrentSession();
	}

	public void integratedProfileDelete(IntegratedProfile ip) {
		this.openCurrentSession();
		this.currentSession.delete(ip);
		this.closeCurrentSession();

	}

	@SuppressWarnings("unchecked")
	public List<IntegratedProfile> getAllIntegratedProfiles() {
		this.openCurrentSession();
		Criteria criteria = this.currentSession.createCriteria(IntegratedProfile.class);
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		List<IntegratedProfile> results = criteria.list();
		this.closeCurrentSession();
		return results;
	}

	public IntegratedProfile getIntegratedProfileById(long id) {
		this.openCurrentSession();
		IntegratedProfile ip = (IntegratedProfile)this.currentSession.get(IntegratedProfile.class, id);
		this.closeCurrentSession();
		return ip;
	}
	
	public void conformanceProfileInsert(ConformanceProfile cp) {
		this.openCurrentSession();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		cp.setLastUpdateDate(dateFormat.format(date));
		this.currentSession.save(cp);
		this.closeCurrentSession();
	}

	public void conformanceProfileUpdate(ConformanceProfile cp) {
		this.openCurrentSession();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		cp.setLastUpdateDate(dateFormat.format(date));
		this.currentSession.update(cp);
		this.closeCurrentSession();
	}

	public void conformanceProfileDelete(ConformanceProfile cp) {
		this.openCurrentSession();
		this.currentSession.delete(cp);
		this.closeCurrentSession();

	}

	@SuppressWarnings("unchecked")
	public List<ConformanceProfile> getAllConformanceProfiles() {
		this.openCurrentSession();
		Criteria criteria = this.currentSession.createCriteria(ConformanceProfile.class);
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		List<ConformanceProfile> results = criteria.list();
		this.closeCurrentSession();
		return results;
	}

	public ConformanceProfile getConformanceProfileById(long id) {
		this.openCurrentSession();
		ConformanceProfile cp = (ConformanceProfile)this.currentSession.get(ConformanceProfile.class, id);
		this.closeCurrentSession();
		return cp;
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
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
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

	public void contextFreeTestPlanInsert(ContextFreeTestPlan cftp) {
		this.openCurrentSession();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		cftp.setLastUpdateDate(dateFormat.format(date));
		this.currentSession.save(cftp);
		this.closeCurrentSession();
		
	}

	public void contextFreeTestPlanUpdate(ContextFreeTestPlan cftp) {
		this.openCurrentSession();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		cftp.setLastUpdateDate(dateFormat.format(date));
		this.currentSession.update(cftp);
		this.closeCurrentSession();
		
	}

	public void contextFreeTestPlanDelete(ContextFreeTestPlan cftp) {
		this.openCurrentSession();
		this.currentSession.delete(cftp);
		this.closeCurrentSession();
		
	}

	public List<ContextFreeTestPlan> getAllContextFreeTestPlans() {
		this.openCurrentSession();
		Criteria criteria = this.currentSession.createCriteria(ContextFreeTestPlan.class);
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		@SuppressWarnings("unchecked")
		List<ContextFreeTestPlan> results = criteria.list();
		this.closeCurrentSession();
		return results;
	}

	public ContextFreeTestPlan getContextFreeTestPlanById(long id) {
		this.openCurrentSession();
		ContextFreeTestPlan cftp = (ContextFreeTestPlan)this.currentSession.get(ContextFreeTestPlan.class, id);
		this.closeCurrentSession();
		return cftp;
	}
}
