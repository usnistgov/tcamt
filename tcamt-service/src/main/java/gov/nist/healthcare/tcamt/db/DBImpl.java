package gov.nist.healthcare.tcamt.db;

import gov.nist.healthcare.tcamt.domain.Actor;
import gov.nist.healthcare.tcamt.domain.ConformanceProfile;
import gov.nist.healthcare.tcamt.domain.ContextFreeTestPlan;
import gov.nist.healthcare.tcamt.domain.DataInstanceTestPlan;
import gov.nist.healthcare.tcamt.domain.DefaultTestDataCategorizationSheet;
import gov.nist.healthcare.tcamt.domain.IntegratedProfile;
import gov.nist.healthcare.tcamt.domain.JurorDocument;
import gov.nist.healthcare.tcamt.domain.Log;
import gov.nist.healthcare.tcamt.domain.Message;
import gov.nist.healthcare.tcamt.domain.SimpleDataInstanceTestPlan;
import gov.nist.healthcare.tcamt.domain.SimpleMessage;
import gov.nist.healthcare.tcamt.domain.TCAMTConstraint;
import gov.nist.healthcare.tcamt.domain.TestCaseCodeList;
import gov.nist.healthcare.tcamt.domain.User;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
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
		sessionFactory = DBImpl.getSessionFactory();
		this.currentSession = sessionFactory.openSession();
	}

	private void init() {
		sessionFactory = DBImpl.getSessionFactory();
		if (!currentSession.isOpen())
			this.currentSession = sessionFactory.openSession();
	}

	public void finalize() {
		if (currentSession.isOpen())
			this.currentSession.close();
		if (!this.sessionFactory.isClosed())
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
		Configuration configuration = new Configuration().configure();
		return configuration.buildSessionFactory();
	}

	public User isValidUser(User user) {
		try {
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
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
		return null;
	}

	public User getUserById(long id) {
		try {
			this.openCurrentSession();
			User u = (User) this.currentSession.get(User.class, id);
			this.closeCurrentSession();
			return u;
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<User> getAllUsers() {
		try {
			this.openCurrentSession();
			Criteria criteria = this.currentSession.createCriteria(User.class);
			criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
			List<User> results = criteria.list();
			this.closeCurrentSession();
			return results;
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
		return null;
	}

	public void addUser(User user) {
		try {
			this.openCurrentSession();
			this.currentSession.save(user);
			this.closeCurrentSession();
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
	}

	public void updateUser(User user) {
		try {
			this.openCurrentSession();
			this.currentSession.update(user);
			this.closeCurrentSession();
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
	}

	public void deleteUser(User user) {
		try {
			this.openCurrentSession();
			this.currentSession.delete(user);
			this.closeCurrentSession();
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
	}

	public void integratedProfileInsert(IntegratedProfile ip) {
		try {
			this.openCurrentSession();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			ip.setLastUpdateDate(dateFormat.format(date));
			this.currentSession.save(ip);
			this.closeCurrentSession();
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
	}

	public void integratedProfileUpdate(IntegratedProfile ip) {
		try {
			this.openCurrentSession();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			ip.setLastUpdateDate(dateFormat.format(date));
			this.currentSession.update(ip);
			this.closeCurrentSession();
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
	}

	public void integratedProfileDelete(IntegratedProfile ip) {
		try {
			this.openCurrentSession();
			this.currentSession.delete(ip);
			this.closeCurrentSession();
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
	}

	@SuppressWarnings("unchecked")
	public List<IntegratedProfile> getAllIntegratedProfiles() {
		try {
			this.openCurrentSession();
			Criteria criteria = this.currentSession
					.createCriteria(IntegratedProfile.class);
			criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
			List<IntegratedProfile> results = criteria.list();
			this.closeCurrentSession();
			return results;
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
		return null;
	}

	public IntegratedProfile getIntegratedProfileById(long id) {
		try {
			this.openCurrentSession();
			IntegratedProfile ip = (IntegratedProfile) this.currentSession.get(
					IntegratedProfile.class, id);
			this.closeCurrentSession();
			return ip;
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
		return null;
	}

	public void conformanceProfileInsert(ConformanceProfile cp) {
		try {
			this.openCurrentSession();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			cp.setLastUpdateDate(dateFormat.format(date));
			this.currentSession.save(cp);
			this.closeCurrentSession();
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
	}

	public void conformanceProfileUpdate(ConformanceProfile cp) {
		try {
			this.openCurrentSession();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			cp.setLastUpdateDate(dateFormat.format(date));
			this.currentSession.update(cp);
			this.closeCurrentSession();
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
	}

	public void conformanceProfileDelete(ConformanceProfile cp) {
		try {
			this.openCurrentSession();
			this.currentSession.delete(cp);
			this.closeCurrentSession();
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
	}

	@SuppressWarnings("unchecked")
	public List<ConformanceProfile> getAllConformanceProfiles() {
		try {
			this.openCurrentSession();
			Criteria criteria = this.currentSession
					.createCriteria(ConformanceProfile.class);
			criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
			List<ConformanceProfile> results = criteria.list();
			this.closeCurrentSession();
			return results;
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
		return null;
	}

	public ConformanceProfile getConformanceProfileById(long id) {
		try {
			this.openCurrentSession();
			ConformanceProfile cp = (ConformanceProfile) this.currentSession
					.get(ConformanceProfile.class, id);
			this.closeCurrentSession();
			return cp;
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
		return null;
	}

	public void actorInsert(Actor a) {
		try {
			this.openCurrentSession();
			this.currentSession.save(a);
			this.closeCurrentSession();
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
	}

	public void actorUpdate(Actor a) {
		try {
			this.openCurrentSession();
			this.currentSession.update(a);
			this.closeCurrentSession();
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
	}

	public void actorDelete(Actor a) {
		try {
			this.openCurrentSession();
			this.currentSession.delete(a);
			this.closeCurrentSession();
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Actor> getAllActors(User author) {
		try {
			if (author == null)
				return null;
			this.openCurrentSession();
			Criteria criteria = this.currentSession.createCriteria(Actor.class);
			criteria.add(Restrictions.eq("author", author));
			criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
			List<Actor> results = (List<Actor>) criteria.list();
			this.closeCurrentSession();
			return results;
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
		return null;
	}

	public Actor getActorById(long id) {
		try {
			this.openCurrentSession();
			Actor a = (Actor) this.currentSession.get(Actor.class, id);
			this.closeCurrentSession();
			return a;
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
		return null;
	}

	public void messageInsert(Message m) {
		try {
			this.openCurrentSession();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			m.setLastUpdateDate(dateFormat.format(date));

			this.currentSession.save(m);
			this.closeCurrentSession();
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
	}

	public void messageUpdate(Message m) {
		try {
			this.openCurrentSession();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			m.setLastUpdateDate(dateFormat.format(date));
			this.currentSession.update(m);
			this.closeCurrentSession();
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
	}

	public void messageDelete(Message m) {
		try {
			this.openCurrentSession();
			this.currentSession.delete(m);
			this.closeCurrentSession();
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Message> getAllMessages(User author) {
		try {
			if (author == null)
				return null;
			this.openCurrentSession();
			Criteria criteria = this.currentSession
					.createCriteria(Message.class);
			criteria.add(Restrictions.eq("author", author));
			criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
			List<Message> results = (List<Message>) criteria.list();
			this.closeCurrentSession();
			return results;
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
		return null;
	}

	public Message getMessageById(long id) {
		try {
			this.openCurrentSession();
			Message m = (Message) this.currentSession.get(Message.class, id);
			this.closeCurrentSession();
			return m;
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
		return null;
	}

	public void dataInstanceTestPlanInsert(DataInstanceTestPlan ditp) {
		try {
			this.openCurrentSession();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			ditp.setLastUpdateDate(dateFormat.format(date));
			this.currentSession.save(ditp);
			this.closeCurrentSession();
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
	}

	public void dataInstanceTestPlanUpdate(DataInstanceTestPlan ditp) {
		try {
			this.openCurrentSession();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			ditp.setLastUpdateDate(dateFormat.format(date));
			this.currentSession.update(ditp);
			this.closeCurrentSession();
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
	}

	public void dataInstanceTestPlanDelete(DataInstanceTestPlan ditp) {
		try {
			this.openCurrentSession();
			this.currentSession.delete(ditp);
			this.closeCurrentSession();
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
	}

	public List<DataInstanceTestPlan> getAllDataInstanceTestPlans(User author) {
		try {
			if (author == null)
				return null;
			this.openCurrentSession();
			Criteria criteria = this.currentSession
					.createCriteria(DataInstanceTestPlan.class);
			criteria.add(Restrictions.eq("author", author));
			criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
			@SuppressWarnings("unchecked")
			List<DataInstanceTestPlan> results = (List<DataInstanceTestPlan>) criteria
					.list();
			this.closeCurrentSession();
			return results;
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
		return null;
	}

	public List<DataInstanceTestPlan> getAllDataInstanceTestPlans() {
		try {
			this.openCurrentSession();
			Criteria criteria = this.currentSession
					.createCriteria(DataInstanceTestPlan.class);
			criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
			@SuppressWarnings("unchecked")
			List<DataInstanceTestPlan> results = criteria.list();
			this.closeCurrentSession();
			return results;
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
		return null;
	}

	public DataInstanceTestPlan getDataInstanceTestPlanById(long id) {
		try {
			this.openCurrentSession();
			Criteria criteria = this.currentSession
					.createCriteria(DataInstanceTestPlan.class);
			criteria.add(Restrictions.eq("id", id));
			criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
			@SuppressWarnings("unchecked")
			List<DataInstanceTestPlan> results = (List<DataInstanceTestPlan>) criteria
					.list();
			this.closeCurrentSession();

			if (results == null || results.size() == 0) {
				return null;
			} else {
				return results.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
		return null;
	}

	public void contextFreeTestPlanInsert(ContextFreeTestPlan cftp) {
		try {
			this.openCurrentSession();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			cftp.setLastUpdateDate(dateFormat.format(date));
			this.currentSession.save(cftp);
			this.closeCurrentSession();
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
	}

	public void contextFreeTestPlanUpdate(ContextFreeTestPlan cftp) {
		try {
			this.openCurrentSession();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			cftp.setLastUpdateDate(dateFormat.format(date));
			this.currentSession.update(cftp);
			this.closeCurrentSession();
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
	}

	public void contextFreeTestPlanDelete(ContextFreeTestPlan cftp) {
		try {
			this.openCurrentSession();
			this.currentSession.delete(cftp);
			this.closeCurrentSession();
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
	}

	public List<ContextFreeTestPlan> getAllContextFreeTestPlans() {
		try {
			this.openCurrentSession();
			Criteria criteria = this.currentSession
					.createCriteria(ContextFreeTestPlan.class);
			criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
			@SuppressWarnings("unchecked")
			List<ContextFreeTestPlan> results = criteria.list();
			this.closeCurrentSession();
			return results;
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
		return null;
	}

	public ContextFreeTestPlan getContextFreeTestPlanById(long id) {
		try {
			this.openCurrentSession();
			ContextFreeTestPlan cftp = (ContextFreeTestPlan) this.currentSession
					.get(ContextFreeTestPlan.class, id);
			this.closeCurrentSession();
			return cftp;
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
		return null;
	}

	public void logInsert(Log l) {
		this.finalize();
		this.init();

		this.openCurrentSession();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		l.setLastUpdateDate(dateFormat.format(date));
		this.currentSession.save(l);
		this.closeCurrentSession();
	}

	public void allLogsDelete() {
		this.openCurrentSession();
		Criteria criteria = this.currentSession.createCriteria(Log.class);
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		@SuppressWarnings("unchecked")
		List<Log> results = criteria.list();

		for (Log l : results) {
			this.currentSession.delete(l);
		}

		this.closeCurrentSession();

	}

	public List<Log> getAllLogs() {
		this.openCurrentSession();
		Criteria criteria = this.currentSession.createCriteria(Log.class);
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		@SuppressWarnings("unchecked")
		List<Log> results = criteria.list();
		this.closeCurrentSession();
		return results;
	}

	private String getStackTrace(final Throwable throwable) {
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw, true);
		throwable.printStackTrace(pw);
		return sw.getBuffer().toString();
	}

	public void logDelete(Log l) {
		try {
			this.openCurrentSession();
			this.currentSession.delete(l);
			this.closeCurrentSession();
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
	}

	public void jurorDocumentInsert(JurorDocument jd) {
		try {
			this.openCurrentSession();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			jd.setLastUpdateDate(dateFormat.format(date));
			this.currentSession.save(jd);
			this.closeCurrentSession();
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
	}

	public void jurorDocumentUpdate(JurorDocument jd) {
		try {
			this.openCurrentSession();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			jd.setLastUpdateDate(dateFormat.format(date));
			this.currentSession.update(jd);
			this.closeCurrentSession();
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}

	}

	public void jurorDocumentDelete(JurorDocument jd) {
		try {
			this.openCurrentSession();
			this.currentSession.delete(jd);
			this.closeCurrentSession();
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}

	}

	public List<JurorDocument> getAllJurorDocuments() {
		try {
			this.openCurrentSession();
			Criteria criteria = this.currentSession
					.createCriteria(JurorDocument.class);
			criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
			@SuppressWarnings("unchecked")
			List<JurorDocument> results = criteria.list();
			this.closeCurrentSession();
			return results;
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
		return null;
	}

	public JurorDocument getJurorDocumentById(long id) {
		try {
			this.openCurrentSession();
			JurorDocument jd = (JurorDocument) this.currentSession.get(
					JurorDocument.class, id);
			this.closeCurrentSession();
			return jd;
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
		return null;
	}

	public void defaultTestDataCategorizationSheetInsert(
			DefaultTestDataCategorizationSheet sheet) {
		try {
			this.openCurrentSession();
			this.currentSession.save(sheet);
			this.closeCurrentSession();
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
	}

	public void defaultTestDataCategorizationSheetAllDelete() {

		List<DefaultTestDataCategorizationSheet> sets = this
				.getAllDefaultTestDataCategorizationSheets();
		if (sets != null && !sets.isEmpty()) {
			try {
				this.openCurrentSession();

				for (DefaultTestDataCategorizationSheet sheet : sets) {
					this.currentSession.delete(sheet);
				}
				this.closeCurrentSession();
			} catch (Exception e) {
				e.printStackTrace();
				Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
				this.logInsert(log);
			}
		}

	}

	public List<DefaultTestDataCategorizationSheet> getAllDefaultTestDataCategorizationSheets() {
		try {
			this.openCurrentSession();
			Criteria criteria = this.currentSession
					.createCriteria(DefaultTestDataCategorizationSheet.class);
			criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
			@SuppressWarnings("unchecked")
			List<DefaultTestDataCategorizationSheet> results = criteria.list();
			this.closeCurrentSession();
			return results;
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
		return null;
	}

	public DefaultTestDataCategorizationSheet getDefaultTestDataCategorizationSheetById(
			long id) {
		try {
			this.openCurrentSession();
			DefaultTestDataCategorizationSheet sheet = (DefaultTestDataCategorizationSheet) this.currentSession
					.get(DefaultTestDataCategorizationSheet.class, id);
			this.closeCurrentSession();
			return sheet;
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
		return null;
	}

	public List<TCAMTConstraint> getAllTCAMTConstraints() {
		try {
			this.openCurrentSession();
			Criteria criteria = this.currentSession
					.createCriteria(TCAMTConstraint.class);
			criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
			@SuppressWarnings("unchecked")
			List<TCAMTConstraint> results = criteria.list();
			this.closeCurrentSession();
			return results;
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
		return null;
	}

	public void tcamtConstraintDelete(TCAMTConstraint constraint) {
		try {
			this.openCurrentSession();
			this.currentSession.delete(constraint);
			this.closeCurrentSession();
		} catch (Exception e) {
			System.out.println(constraint.getId() + " is using.");
		}

	}

	public void testCaseCodeListInsert(TestCaseCodeList t) {
		try {
			this.openCurrentSession();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			t.setLastUpdateDate(dateFormat.format(date));

			this.currentSession.save(t);
			this.closeCurrentSession();
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
	}

	public void testCaseCodeListUpdate(TestCaseCodeList t) {
		try {
			this.openCurrentSession();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			t.setLastUpdateDate(dateFormat.format(date));
			this.currentSession.update(t);
			this.closeCurrentSession();
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}

	}

	public void testCaseCodeListDelete(TestCaseCodeList t) {
		try {
			this.openCurrentSession();
			this.currentSession.delete(t);
			this.closeCurrentSession();
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}

	}

	@SuppressWarnings("unchecked")
	public List<TestCaseCodeList> getAllTestCaseCodeLists(User author) {
		try {
			if (author == null)
				return null;
			this.openCurrentSession();
			Criteria criteria = this.currentSession
					.createCriteria(TestCaseCodeList.class);
			criteria.add(Restrictions.eq("author", author));
			criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
			List<TestCaseCodeList> results = (List<TestCaseCodeList>) criteria
					.list();
			this.closeCurrentSession();
			return results;
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
		return null;
	}

	public TestCaseCodeList getTestCaseCodeListById(long id) {
		try {
			this.openCurrentSession();
			TestCaseCodeList codelist = (TestCaseCodeList) this.currentSession
					.get(TestCaseCodeList.class, id);
			this.closeCurrentSession();
			return codelist;
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
		return null;
	}

	public List<SimpleMessage> getAllSimpleMessages(User author) {
		try {
			if (author == null)
				return null;
			this.openCurrentSession();
			
			
			
			
			Query q1 = this.currentSession.createQuery("Select id, lastUpdateDate, longDescription, name, version from Message where author_id = '" + author.getId() +  "'");
			
//			Criteria criteria = this.currentSession
//					.createCriteria(Message.class);
//			criteria.add(Restrictions.eq("author", author));
//			criteria.setProjection(Projections
//					.projectionList()
//					.add(Projections.property("id"), "id")
//					.add(Projections.property("lastUpdateDate"),
//							"lastUpdateDate")
//					.add(Projections.property("longDescription"),
//							"longDescription")
//					.add(Projections.property("name"), "name")
//					.add(Projections.property("version"), "version"));
//
//			criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
//			List<Message> results = (List<Message>) criteria.list();
			
			List results;
			List<SimpleMessage> simpleResults = new ArrayList<SimpleMessage>();
			
			results = q1.list();
			
			
			for (Iterator it = results.iterator(); it.hasNext(); ) {
	               Object[] myResult = (Object[]) it.next();
	               Long id = (Long) myResult[0];
	               String lastUpdateDate = (String) myResult[1];
	               String longDescription = (String) myResult[2];
	               String name = (String) myResult[3];
	               Integer version = (Integer) myResult[4];
	               
	               System.out.println(id + "::" + lastUpdateDate + "::" + longDescription + "::" + name + "::" +version);
	               
	               SimpleMessage sm = new SimpleMessage();
					sm.setId(id);
					sm.setLastUpdateDate(lastUpdateDate);
					sm.setLongDescription(longDescription);
					sm.setName(name);
					sm.setVersion(version);

					simpleResults.add(sm);
	            }
			
			this.closeCurrentSession();

//			List<SimpleMessage> simpleResults = new ArrayList<SimpleMessage>();
//			for (Message m : results) {
//				SimpleMessage sm = new SimpleMessage();
//				sm.setId(m.getId());
//				sm.setLastUpdateDate(m.getLastUpdateDate());
//				sm.setLongDescription(m.getLongDescription());
//				sm.setName(m.getName());
//				sm.setVersion(m.getVersion());
//
//				simpleResults.add(sm);
//			}

			return simpleResults;
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
		return null;
	}

	public List<SimpleDataInstanceTestPlan> getAllSimpleDataInstanceTestPlans(
			User author) {
		try {
			if (author == null)
				return null;
			this.openCurrentSession();
			Criteria criteria = this.currentSession
					.createCriteria(DataInstanceTestPlan.class);
			criteria.add(Restrictions.eq("author", author));
			criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
			@SuppressWarnings("unchecked")
			List<DataInstanceTestPlan> results = (List<DataInstanceTestPlan>) criteria
					.list();
			this.closeCurrentSession();

			List<SimpleDataInstanceTestPlan> simpleResults = new ArrayList<SimpleDataInstanceTestPlan>();
			for (DataInstanceTestPlan m : results) {
				SimpleDataInstanceTestPlan sm = new SimpleDataInstanceTestPlan();
				sm.setId(m.getId());
				sm.setLastUpdateDate(m.getLastUpdateDate());
				sm.setLongDescription(m.getLongDescription());
				sm.setName(m.getName());
				sm.setType(m.getType());

				simpleResults.add(sm);
			}

			return simpleResults;
		} catch (Exception e) {
			e.printStackTrace();
			Log log = new Log(e.toString(), "Error", this.getStackTrace(e));
			this.logInsert(log);
		}
		return null;
	}

}
