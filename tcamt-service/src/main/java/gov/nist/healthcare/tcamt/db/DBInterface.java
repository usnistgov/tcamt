package gov.nist.healthcare.tcamt.db;

import gov.nist.healthcare.tcamt.domain.Actor;
import gov.nist.healthcare.tcamt.domain.Interaction;
import gov.nist.healthcare.tcamt.domain.Message;
import gov.nist.healthcare.tcamt.domain.TestCase;
import gov.nist.healthcare.tcamt.domain.TestPlan;
import gov.nist.healthcare.tcamt.domain.Transaction;
import gov.nist.healthcare.tcamt.domain.TestStep;

import java.util.List;

public interface DBInterface {
	public int actorInsert(Actor a, String author);
	public int actorUpdate(Actor a);
	public int actorDelete(Actor a);
	public List<Actor> getAllActors(String author);
	public Actor getActorById(int id);
	
	public int messageInsert(Message m, String author);
	public int messageUpdate(Message m);
	public int messageDelete(Message m);
	public List<Message> getAllMessages(String author);
	public Message getMessageById(int id);
	
	public int interactionInsert(Interaction i, String author);
	public int interactionUpdate(Interaction i);
	public int interactionDelete(Interaction i);
	public List<Interaction> getAllInteractions(String author);
	public Interaction getInteractionById(int id);
	
	public int transactionInsert(Transaction tsc, String author);
	public int transactionUpdate(Transaction tsc);
	public int transactionDelete(Transaction tsc);
	public List<Transaction> getAllTransactions(String author);
	public Transaction getTransactionById(int id);

	public int testPlanInsert(TestPlan tp, String author);
	public int testPlanUpdate(TestPlan tp);
	public int testPlanDelete(TestPlan tp);
	public List<TestPlan> getAllTestPlans(String author);
	public TestPlan getTestPlanById(int id);

	public int testCaseInsert(TestCase tc, String author);
	public int testCaseUpdate(TestCase tc);
	public int testCaseDelete(TestCase tc);
	public List<TestCase> getAllTestCases(String author);
	public TestCase getTestCaseById(int id);
	
	public int testStepInsert(TestStep ts, String author);
	public int testStepUpdate(TestStep ts);
	public int testStepDelete(TestStep ts);
	public List<TestStep> getAllTestSteps(String author);
	public TestStep getTestStepById(int id);
	
	public boolean getUserPasswordById(String id, String password);
	public List<String> getAllUsers();
	
	public boolean checkAvailabilityDeleteTestStep(Integer id);
	public boolean checkAvailabilityDeleteTestCase(Integer id);
}
