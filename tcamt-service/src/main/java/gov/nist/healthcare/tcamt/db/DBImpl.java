package gov.nist.healthcare.tcamt.db;

import gov.nist.healthcare.tcamt.domain.Actor;
import gov.nist.healthcare.tcamt.domain.DataInstanceTestCase;
import gov.nist.healthcare.tcamt.domain.DataInstanceTestCaseGroup;
import gov.nist.healthcare.tcamt.domain.DataInstanceTestPlan;
import gov.nist.healthcare.tcamt.domain.IsolatedTestCase;
import gov.nist.healthcare.tcamt.domain.IsolatedTestCaseGroup;
import gov.nist.healthcare.tcamt.domain.IsolatedTestPlan;
import gov.nist.healthcare.tcamt.domain.IsolatedTestStep;
import gov.nist.healthcare.tcamt.domain.Message;
import gov.nist.healthcare.tcamt.domain.User;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;

public class DBImpl implements DBInterface, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7820251230674334331L;

	public User isValidUser(User user) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(User.class);
		criteria.add(Restrictions.eq("userId", user.getUserId()));

		@SuppressWarnings("unchecked")
		List<User> users = criteria.list();
		session.getTransaction().commit();
		if (users == null || users.size() == 0)
			return null;
		if (users.get(0).getPassword().equals(user.getPassword()))
			return users.get(0);
		return null;
	}
	
	
	public User getUserById(long id){
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(User.class);
		criteria.add(Restrictions.eq("id", id));

		@SuppressWarnings("unchecked")
		List<User> users = criteria.list();
		session.getTransaction().commit();
		if (users == null || users.size() == 0)
			return null;
		else
			return users.get(0);
	}

	@SuppressWarnings("unchecked")
	public List<User> getAllUsers() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(User.class);
		List<User> results = criteria.list();
		session.getTransaction().commit();
		return results;
	}

	public void addUser(User user) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		
		session.beginTransaction();
		session.save(user);
		session.getTransaction().commit();
		
	}

	public void updateUser(User user) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		
		session.beginTransaction();
		session.update(user);
		session.getTransaction().commit();
		
	}

	public void deleteUser(User user) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		
		session.beginTransaction();
		session.delete(user);
		session.getTransaction().commit();
		
	}
	
	public void actorInsert(Actor a) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		
		session.beginTransaction();
		session.save(a);
		session.getTransaction().commit();
		
	}

	public void actorUpdate(Actor a) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		
		session.beginTransaction();
		session.update(a);
		session.getTransaction().commit();
		
	}

	public void actorDelete(Actor a) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		
		session.beginTransaction();
		session.delete(a);
		session.getTransaction().commit();
		
	}

	@SuppressWarnings("unchecked")
	public List<Actor> getAllActors(User author) {
		if(author == null) return null;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(Actor.class);
		criteria.add(Restrictions.eq("author", author));
		List<Actor> results = (List<Actor>) criteria.list();
		session.getTransaction().commit();
		return results;
	}

	public Actor getActorById(long id) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(Actor.class);
		criteria.add(Restrictions.eq("id", id));
		@SuppressWarnings("unchecked")
		List<Actor> results = criteria.list();
		session.getTransaction().commit();
		if (results == null || results.size() == 0) {
			return null;
		} else {
			return results.get(0);
		}
	}

	public void messageInsert(Message m) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		session.save(m);
		session.getTransaction().commit();
		
	}

	public void messageUpdate(Message m) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		session.update(m);
		session.getTransaction().commit();
		
	}

	public void messageDelete(Message m) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		session.delete(m);
		session.getTransaction().commit();
		
	}

	@SuppressWarnings("unchecked")
	public List<Message> getAllMessages(User author) {
		if(author == null) return null;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(Message.class);
		criteria.add(Restrictions.eq("author", author));
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		List<Message> results = (List<Message>) criteria.list();
		session.getTransaction().commit();
		return results;
	}

	@SuppressWarnings("unchecked")
	public Message getMessageById(long id) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(Message.class);
		criteria.add(Restrictions.eq("id", id));
		List<Message> results = criteria.list();
		session.getTransaction().commit();
		if (results == null || results.size() == 0) {
			return null;
		} else {
			return results.get(0);
		}
	}

	public void dataInstanceTestPlanInsert(DataInstanceTestPlan ditp) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		
		for(DataInstanceTestCaseGroup group:ditp.getTestcasegroups()){
			for(DataInstanceTestCase c:group.getTestcases()){
				Message m = c.getMessage();
				if(m != null){
					m.setAuthor(null);
					m.setId(0);
					session.save(m);
				}
			}
		}
		
		for(DataInstanceTestCase c:ditp.getTestcases()){
			Message m = c.getMessage();
			if(m != null){
				m.setAuthor(null);
				m.setId(0);
				session.save(m);	
			}
		}
		
		session.save(ditp);
		session.getTransaction().commit();
		
	}

	public void dataInstanceTestPlanUpdate(DataInstanceTestPlan ditp) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		for(DataInstanceTestCaseGroup group:ditp.getTestcasegroups()){
			for(DataInstanceTestCase c:group.getTestcases()){
				if(c.getMessage().getAuthor() == null){
					session.update(c.getMessage());	
				}else{
					Message m = c.getMessage();
					if(m != null){
						m.setAuthor(null);
						m.setId(0);
						session.save(m);
					}
				}
			}
		}
		
		for(DataInstanceTestCase c:ditp.getTestcases()){
			if(c.getMessage().getAuthor() == null){
				session.update(c.getMessage());	
			}else{
				Message m = c.getMessage();
				if(m != null){
					m.setAuthor(null);
					m.setId(0);
					session.save(m);
				}
			}
		}
		session.update(ditp);
		session.getTransaction().commit();
	}

	public void dataInstanceTestPlanDelete(DataInstanceTestPlan ditp) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		session.delete(ditp);
		session.getTransaction().commit();
	}

	public List<DataInstanceTestPlan> getAllDataInstanceTestPlans(User author) {
		if(author == null) return null;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(DataInstanceTestPlan.class);
		criteria.add(Restrictions.eq("author", author));
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		@SuppressWarnings("unchecked")
		List<DataInstanceTestPlan> results = (List<DataInstanceTestPlan>) criteria.list();
		session.getTransaction().commit();
		return results;
	}

	public DataInstanceTestPlan getDataInstanceTestPlanById(long id) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(DataInstanceTestPlan.class);
		criteria.add(Restrictions.eq("id", id));
		@SuppressWarnings("unchecked")
		List<DataInstanceTestPlan> results = criteria.list();
		session.getTransaction().commit();
		if (results == null || results.size() == 0) {
			return null;
		} else {
			return results.get(0);
		}
	}


	public void isolatedTestPlanInsert(IsolatedTestPlan itp) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		
		for(IsolatedTestCaseGroup group:itp.getTestcasegroups()){
			for(IsolatedTestCase c:group.getTestcases()){
				for(IsolatedTestStep s:c.getTeststeps()){
					Message m = s.getMessage();
					if(m != null){
						m.setAuthor(null);
						m.setId(0);
						session.save(m);
					}	
				}
			}
		}
		
		for(IsolatedTestCase c:itp.getTestcases()){
			for(IsolatedTestStep s:c.getTeststeps()){
				Message m = s.getMessage();
				if(m != null){
					m.setAuthor(null);
					m.setId(0);
					session.save(m);
				}	
			}
		}
		
		session.save(itp);
		session.getTransaction().commit();
		
	}


	public void isolatedTestPlanUpdate(IsolatedTestPlan itp) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		for(IsolatedTestCaseGroup group:itp.getTestcasegroups()){
			for(IsolatedTestCase c:group.getTestcases()){
				for(IsolatedTestStep s:c.getTeststeps()){
					if(s.getMessage().getAuthor() == null){
						session.update(s.getMessage());	
					}else{
						Message m = s.getMessage();
						if(m != null){
							m.setAuthor(null);
							m.setId(0);
							session.save(m);
						}
					}	
				}
			}
		}
		
		for(IsolatedTestCase c:itp.getTestcases()){
			for(IsolatedTestStep s:c.getTeststeps()){
				if(s.getMessage().getAuthor() == null){
					session.update(s.getMessage());	
				}else{
					Message m = s.getMessage();
					if(m != null){
						m.setAuthor(null);
						m.setId(0);
						session.save(m);
					}
				}
			}
		}
		session.update(itp);
		session.getTransaction().commit();
		
	}


	public void isolatedTestPlanDelete(IsolatedTestPlan itp) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		session.delete(itp);
		session.getTransaction().commit();
	}


	public List<IsolatedTestPlan> getAllIsolatedTestPlans(User author) {
		if(author == null) return null;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(IsolatedTestPlan.class);
		criteria.add(Restrictions.eq("author", author));
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		@SuppressWarnings("unchecked")
		List<IsolatedTestPlan> results = (List<IsolatedTestPlan>) criteria.list();
		session.getTransaction().commit();
		return results;
	}


	public IsolatedTestPlan getIsolatedTestPlanById(long id) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(IsolatedTestPlan.class);
		criteria.add(Restrictions.eq("id", id));
		@SuppressWarnings("unchecked")
		List<IsolatedTestPlan> results = criteria.list();
		session.getTransaction().commit();
		if (results == null || results.size() == 0) {
			return null;
		} else {
			return results.get(0);
		}
	}
}
