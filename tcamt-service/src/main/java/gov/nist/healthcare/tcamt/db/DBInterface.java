package gov.nist.healthcare.tcamt.db;

import gov.nist.healthcare.tcamt.domain.Actor;
import gov.nist.healthcare.tcamt.domain.ConformanceProfile;
import gov.nist.healthcare.tcamt.domain.DataInstanceTestPlan;
import gov.nist.healthcare.tcamt.domain.IntegratedProfile;
import gov.nist.healthcare.tcamt.domain.Message;
import gov.nist.healthcare.tcamt.domain.User;

import java.util.List;

public interface DBInterface {
	public User isValidUser(User user);
	public User getUserById(long id);
	public List<User> getAllUsers();
	public void addUser(User user);
	public void updateUser(User user);
	public void deleteUser(User user);
	
	public void integratedProfileInsert(IntegratedProfile ip);
	public void integratedProfileUpdate(IntegratedProfile ip);
	public void integratedProfileDelete(IntegratedProfile ip);
	public List<IntegratedProfile> getAllIntegratedProfiles();
	public IntegratedProfile getIntegratedProfileById(long id);
	
	public void conformanceProfileInsert(ConformanceProfile cp);
	public void conformanceProfileUpdate(ConformanceProfile cp);
	public void conformanceProfileDelete(ConformanceProfile cp);
	public List<ConformanceProfile> getAllConformanceProfiles();
	public ConformanceProfile getConformanceProfileById(long id);
	
	public void actorInsert(Actor a);
	public void actorUpdate(Actor a);
	public void actorDelete(Actor a);
	public List<Actor> getAllActors(User author);
	public Actor getActorById(long id);
	
	public void messageInsert(Message m);
	public void messageUpdate(Message m);
	public void messageDelete(Message m);
	public List<Message> getAllMessages(User author);
	public Message getMessageById(long id);
	
	public void dataInstanceTestPlanInsert(DataInstanceTestPlan ditp);
	public void dataInstanceTestPlanUpdate(DataInstanceTestPlan ditp);
	public void dataInstanceTestPlanDelete(DataInstanceTestPlan ditp);
	public List<DataInstanceTestPlan> getAllDataInstanceTestPlans(User author);
	public DataInstanceTestPlan getDataInstanceTestPlanById(long id);
}
