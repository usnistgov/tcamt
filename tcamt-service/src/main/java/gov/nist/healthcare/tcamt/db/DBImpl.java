package gov.nist.healthcare.tcamt.db;

import gov.nist.healthcare.hl7tools.domain.ConformanceStatement;
import gov.nist.healthcare.hl7tools.domain.Predicate;
import gov.nist.healthcare.hl7tools.v2.maker.core.domain.profile.MessageProfile;
import gov.nist.healthcare.tcamt.domain.Actor;
import gov.nist.healthcare.tcamt.domain.InstanceTestDataType;
import gov.nist.healthcare.tcamt.domain.Interaction;
import gov.nist.healthcare.tcamt.domain.Message;
import gov.nist.healthcare.tcamt.domain.ProfilePathOccurIGData;
import gov.nist.healthcare.tcamt.domain.TestCase;
import gov.nist.healthcare.tcamt.domain.TestPlan;
import gov.nist.healthcare.tcamt.domain.Transaction;
import gov.nist.healthcare.tcamt.domain.TestStep;
import gov.nist.healthcare.tcamt.domain.TestStory;
import gov.nist.healthcare.tcamt.domain.ValidationContext;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBImpl implements DBInterface {

	public int testPlanInsert(TestPlan tp, String author) {
		Connection conn = null;
		PreparedStatement stmt = null;
		DBCon db = new DBCon();
		int result = 0;
		try {
			conn = db.connect();
			String sql = "insert into testplans (name, description, testcases, author) values(?,?,?,?)";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, tp.getName());
			stmt.setString(2, tp.getDescription());
			stmt.setObject(3, tp.getTestCases());
			stmt.setString(4, author);
			result = stmt.executeUpdate();
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close(stmt, conn);
		}
		return result;
	}

	public int testPlanUpdate(TestPlan tp) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		DBCon db = new DBCon();
		int result = 0;
		try {
			conn = db.connect();
			String sql = "update testplans set name=?, description=?, version=?, testcases=? where id=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, tp.getName());
			pstmt.setString(2, tp.getDescription());
			pstmt.setInt(3, tp.getVersion());
			pstmt.setObject(4, tp.getTestCases());
			pstmt.setInt(5, tp.getId());
			result = pstmt.executeUpdate();
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close(pstmt, conn);
		}
		return result;
	}

	public int testPlanDelete(TestPlan tp) {		
		Connection conn = null;
		PreparedStatement pstmt = null;
		DBCon db = new DBCon();
		int result = 0;
		try {
			conn = db.connect();
			String sql = "delete from testplans where id=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, tp.getId());
			result = pstmt.executeUpdate();
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close(pstmt, conn);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<TestPlan> getAllTestPlans(String author) {
		List<TestPlan> tps = new ArrayList<TestPlan>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DBCon db = new DBCon();
		try {
			conn = db.connect();
			String sql = "select * from testplans where author=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, author);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				TestPlan tp = new TestPlan();
				tp.setId(rs.getInt("id"));
				tp.setName(rs.getString("name"));
				tp.setVersion(rs.getInt("version"));
				tp.setDescription(rs.getString("description"));
				
				ByteArrayInputStream bais1;
				ObjectInputStream ins1;
				bais1 = new ByteArrayInputStream(rs.getBytes("testcases"));
				ins1 = new ObjectInputStream(bais1);
				tp.setTestCases((List<Integer>)ins1.readObject());
				ins1.close();
				
				
				tps.add(tp);
			}
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close(rs, pstmt, conn);
		}
		return tps;
	}

	@SuppressWarnings("unchecked")
	public TestPlan getTestPlanById(int id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DBCon db = new DBCon();
		try {
			conn = db.connect();
			String sql = "select * from testplans WHERE id=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				TestPlan tp = new TestPlan();
				tp.setId(rs.getInt("id"));
				tp.setName(rs.getString("name"));
				tp.setVersion(rs.getInt("version"));
				tp.setDescription(rs.getString("description"));
				
				ByteArrayInputStream bais1;
				ObjectInputStream ins1;
				bais1 = new ByteArrayInputStream(rs.getBytes("testcases"));
				ins1 = new ObjectInputStream(bais1);
				tp.setTestCases((List<Integer>)ins1.readObject());
				ins1.close();
				
				return tp;
			}
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close(rs, pstmt, conn);
		}
		return null;
	}

	public int testCaseInsert(TestCase tc, String author) {
		Connection conn = null;
		PreparedStatement stmt = null;
		DBCon db = new DBCon();
		int result = 0;
		try {
			conn = db.connect();
			String sql = "insert into testcases (name, description, type, umld, teststorydesc, teststorycomment, teststoryprecondition, teststorypostcondition, teststorytestobjectives, teststorynotes, teststepobj, listactorobj, transactionobj, sutactorid, testscenario, author) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, tc.getName());
			stmt.setString(2, tc.getDescription());
			stmt.setString(3, tc.getType());
			stmt.setString(4, tc.getUmld());
			stmt.setString(5, tc.getTestCaseStory().getDescription());
			stmt.setString(6, tc.getTestCaseStory().getComments());
			stmt.setString(7, tc.getTestCaseStory().getPreCondition());
			stmt.setString(8, tc.getTestCaseStory().getPostCondition());
			stmt.setString(9, tc.getTestCaseStory().getTestObjectives());
			stmt.setString(10, tc.getTestCaseStory().getNotes());
			stmt.setObject(11, tc.getTestSteps());
			stmt.setObject(12, tc.getListActors());
			stmt.setObject(13, tc.getTransactions());
			stmt.setObject(14, tc.getSutActorId());
			stmt.setString(15, tc.getTestScenario());
			stmt.setString(16, author);
			result = stmt.executeUpdate();
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close(stmt, conn);
		}
		return result;
	}

	public int testCaseUpdate(TestCase tc) {
		Connection conn = null;
		PreparedStatement stmt = null;
		DBCon db = new DBCon();
		int result = 0;
		try {
			conn = db.connect();
			String sql = "update testcases set name=?, description=?, type=?, version=?, umld=?, teststorydesc=?, teststorycomment=?, teststoryprecondition=?, teststorypostcondition=?, teststorytestobjectives=?, teststorynotes=?, teststepobj=?, listactorobj=?, transactionobj=?, sutactorid=?, testscenario=?"
					+ " where id=?";
			stmt = conn.prepareStatement(sql);
			
			stmt.setString(1, tc.getName());
			stmt.setString(2, tc.getDescription());
			stmt.setString(3, tc.getType());
			stmt.setInt(4, tc.getVersion());
			stmt.setString(5, tc.getUmld());
			stmt.setString(6, tc.getTestCaseStory().getDescription());
			stmt.setString(7, tc.getTestCaseStory().getComments());
			stmt.setString(8, tc.getTestCaseStory().getPreCondition());
			stmt.setString(9, tc.getTestCaseStory().getPostCondition());
			stmt.setString(10, tc.getTestCaseStory().getTestObjectives());
			stmt.setString(11, tc.getTestCaseStory().getNotes());
			stmt.setObject(12, tc.getTestSteps());
			stmt.setObject(13, tc.getListActors());
			stmt.setObject(14, tc.getTransactions());
			stmt.setObject(15, tc.getSutActorId());
			stmt.setString(16, tc.getTestScenario());
			stmt.setInt(17, tc.getId());
			result = stmt.executeUpdate();
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close(stmt, conn);
		}
		return result;
	}

	public int testCaseDelete(TestCase tc) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		DBCon db = new DBCon();
		int result = 0;
		try {
			conn = db.connect();
			String sql = "delete from testcases where id=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, tc.getId());
			result = pstmt.executeUpdate();
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close(pstmt, conn);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<TestCase> getAllTestCases(String author) {
		List<TestCase> tcs = new ArrayList<TestCase>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DBCon db = new DBCon();
		try {
			conn = db.connect();
			String sql = "select * from testcases where author=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, author);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				TestCase tc = new TestCase();
				tc.setId(rs.getInt("id"));
				tc.setName(rs.getString("name"));
				tc.setDescription(rs.getString("description"));
				tc.setType(rs.getString("type"));
				tc.setVersion(rs.getInt("version"));
				tc.setUmld(rs.getString("umld"));
				tc.setTestScenario(rs.getString("testscenario"));
				tc.setTestCaseStory(new TestStory(rs.getString("teststorydesc"), rs.getString("teststorycomment"), rs.getString("teststoryprecondition"), rs.getString("teststorypostcondition"), rs.getString("teststorytestobjectives"), rs.getString("teststorynotes")));
				
				ByteArrayInputStream bais1;
	            ObjectInputStream ins1;
	            bais1 = new ByteArrayInputStream(rs.getBytes("teststepobj"));
	            ins1 = new ObjectInputStream(bais1);
				tc.setTestSteps((List<Integer>)ins1.readObject());
				ins1.close();
				
				ByteArrayInputStream bais2;
	            ObjectInputStream ins2;
	            bais2 = new ByteArrayInputStream(rs.getBytes("listactorobj"));
	            ins2 = new ObjectInputStream(bais2);
				tc.setListActors((List<Actor>)ins2.readObject());
				ins2.close();
				
				ByteArrayInputStream bais3;
	            ObjectInputStream ins3;
	            bais3 = new ByteArrayInputStream(rs.getBytes("transactionobj"));
	            ins3 = new ObjectInputStream(bais3);
				tc.setTransactions((List<String>)ins3.readObject());
				ins3.close();
				
				tc.setSutActorId(rs.getInt("sutactorid"));
				
				tcs.add(tc);
			}
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close(rs, pstmt, conn);
		}
		return tcs;
	}

	@SuppressWarnings("unchecked")
	public TestCase getTestCaseById(int id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DBCon db = new DBCon();
		try {
			conn = db.connect();
			String sql = "select * from testcases WHERE id=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				TestCase tc = new TestCase();
				tc.setId(rs.getInt("id"));
				tc.setName(rs.getString("name"));
				tc.setDescription(rs.getString("description"));
				tc.setType(rs.getString("type"));
				tc.setVersion(rs.getInt("version"));
				tc.setUmld(rs.getString("umld"));
				tc.setTestScenario(rs.getString("testscenario"));
				tc.setTestCaseStory(new TestStory(rs.getString("teststorydesc"), rs.getString("teststorycomment"), rs.getString("teststoryprecondition"), rs.getString("teststorypostcondition"), rs.getString("teststorytestobjectives"), rs.getString("teststorynotes")));
				
				ByteArrayInputStream bais1;
	            ObjectInputStream ins1;
	            bais1 = new ByteArrayInputStream(rs.getBytes("teststepobj"));
	            ins1 = new ObjectInputStream(bais1);
				tc.setTestSteps((List<Integer>)ins1.readObject());
				ins1.close();
				
				ByteArrayInputStream bais2;
	            ObjectInputStream ins2;
	            bais2 = new ByteArrayInputStream(rs.getBytes("listactorobj"));
	            ins2 = new ObjectInputStream(bais2);
				tc.setListActors((List<Actor>)ins2.readObject());
				ins2.close();
				
				ByteArrayInputStream bais3;
	            ObjectInputStream ins3;
	            bais3 = new ByteArrayInputStream(rs.getBytes("transactionobj"));
	            ins3 = new ObjectInputStream(bais3);
				tc.setTransactions((List<String>)ins3.readObject());
				ins3.close();
				
				tc.setSutActorId(rs.getInt("sutactorid"));
				
				return tc;
			}
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close(rs, pstmt, conn);
		}
		return null;
	}

	public int actorInsert(Actor a, String author) {
		Connection conn = null;
		PreparedStatement stmt = null;
		DBCon db = new DBCon();
		int result = 0;
		try {
			conn = db.connect();
			String sql = "insert into actors (name, role, reference, author) values(?,?,?,?)";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, a.getName());
			stmt.setString(2, a.getRole());
			stmt.setString(3, a.getReference());
			stmt.setString(4, author);
			result = stmt.executeUpdate();
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close(stmt, conn);
		}
		return result;
	}

	public int actorUpdate(Actor a) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		DBCon db = new DBCon();
		int result = 0;
		try {
			conn = db.connect();
			String sql = "update actors set name=?, role=?, reference=?, version=? where id=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, a.getName());
			pstmt.setString(2, a.getRole());
			pstmt.setString(3, a.getReference());
			pstmt.setInt(4, a.getVersion());
			pstmt.setInt(5, a.getId());
			result = pstmt.executeUpdate();
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close(pstmt, conn);
		}
		return result;
	}

	public int actorDelete(Actor a) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		DBCon db = new DBCon();
		int result = 0;
		try {
			conn = db.connect();
			String sql = "delete from actors where id=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, a.getId());
			result = pstmt.executeUpdate();
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close(pstmt, conn);
		}
		return result;
	}

	public List<Actor> getAllActors(String author) {
		List<Actor> actors = new ArrayList<Actor>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DBCon db = new DBCon();
		try {
			conn = db.connect();
			String sql = "select * from actors where author=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, author);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Actor a = new Actor();
				a.setId(rs.getInt("id"));
				a.setName(rs.getString("name"));
				a.setRole(rs.getString("role"));
				a.setReference(rs.getString("reference"));
				a.setVersion(rs.getInt("version"));
				
				actors.add(a);
			}
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close(rs, pstmt, conn);
		}
		return actors;
	}

	public Actor getActorById(int id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DBCon db = new DBCon();
		try {
			conn = db.connect();
			String sql = "select * from actors WHERE id=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Actor a = new Actor();
				a.setId(rs.getInt("id"));
				a.setName(rs.getString("name"));
				a.setRole(rs.getString("role"));
				a.setReference(rs.getString("reference"));
				a.setVersion(rs.getInt("version"));
				return a;
			}
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close(rs, pstmt, conn);
		}
		return null;
	}

	public int messageInsert(Message m, String author) {
		Connection conn = null;
		PreparedStatement stmt = null;
		DBCon db = new DBCon();
		int result = 0;
		try {
			conn = db.connect();
			String sql = "insert into messages (name, description, messageProfile, listCSs, listCPs, profilePathOccurIGData, instanceTestDataTypes, validationContexts, hl7EndcodedMessage, author) values(?,?,?,?,?,?,?,?,?,?)";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, m.getName());
			stmt.setString(2, m.getDescription());
			stmt.setObject(3, m.getMessageProfile());
			stmt.setObject(4, m.getListCSs());
			stmt.setObject(5, m.getListCPs());
			stmt.setObject(6, m.getProfilePathOccurIGData());
			stmt.setObject(7, m.getInstanceTestDataTypes());
			stmt.setObject(8, m.getValidationContexts());
			stmt.setString(9, m.getHl7EndcodedMessage());
			stmt.setString(10, author);
			result = stmt.executeUpdate();
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close(stmt, conn);
		}
		return result;
	}

	public int messageUpdate(Message m) {
		Connection conn = null;
		PreparedStatement stmt = null;
		DBCon db = new DBCon();
		int result = 0;
		try {
			conn = db.connect();
			String sql = "update messages set name=?, description=?, version=?, messageProfile=?, listCSs=?, listCPs=?, profilePathOccurIGData=?, instanceTestDataTypes=?, validationContexts=?, hl7EndcodedMessage=? where id=?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, m.getName());
			stmt.setString(2, m.getDescription());
			stmt.setInt(3, m.getVersion());
			stmt.setObject(4, m.getMessageProfile());
			stmt.setObject(5, m.getListCSs());
			stmt.setObject(6, m.getListCPs());
			stmt.setObject(7, m.getProfilePathOccurIGData());
			stmt.setObject(8, m.getInstanceTestDataTypes());
			stmt.setObject(9, m.getValidationContexts());
			stmt.setString(10, m.getHl7EndcodedMessage());
			stmt.setInt(11, m.getId());
			result = stmt.executeUpdate();
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close(stmt, conn);
		}
		return result;
	}

	public int messageDelete(Message m) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		DBCon db = new DBCon();
		int result = 0;
		try {
			conn = db.connect();
			String sql = "delete from messages where id=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, m.getId());
			result = pstmt.executeUpdate();
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close(pstmt, conn);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Message> getAllMessages(String author) {
		List<Message> messages = new ArrayList<Message>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DBCon db = new DBCon();
		try {
			conn = db.connect();
			String sql = "select * from messages where author=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, author);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Message m = new Message();
				m.setId(rs.getInt("id"));
				m.setName(rs.getString("name"));
				m.setDescription(rs.getString("description"));
				m.setVersion(rs.getInt("version"));
				m.setHl7EndcodedMessage(rs.getString("hl7EndcodedMessage"));
				
				
				ByteArrayInputStream bais1;
	            ObjectInputStream ins1;
	            bais1 = new ByteArrayInputStream(rs.getBytes("messageProfile"));
	            ins1 = new ObjectInputStream(bais1);
				m.setMessageProfile((MessageProfile)ins1.readObject());
				ins1.close();
				
				ByteArrayInputStream bais2;
	            ObjectInputStream ins2;
	            bais2 = new ByteArrayInputStream(rs.getBytes("listCSs"));
	            ins2 = new ObjectInputStream(bais2);
				m.setListCSs((List<ConformanceStatement>)ins2.readObject());
				ins2.close();
				
				ByteArrayInputStream bais3;
	            ObjectInputStream ins3;
	            bais3 = new ByteArrayInputStream(rs.getBytes("listCPs"));
	            ins3 = new ObjectInputStream(bais3);
				m.setListCPs((List<Predicate>) ins3.readObject());
				ins3.close();
				
				ByteArrayInputStream bais4;
	            ObjectInputStream ins4;
	            bais4 = new ByteArrayInputStream(rs.getBytes("profilePathOccurIGData"));
	            ins4 = new ObjectInputStream(bais4);
				m.setProfilePathOccurIGData((List<ProfilePathOccurIGData>) ins4.readObject());
				ins4.close();
				
				ByteArrayInputStream bais5;
	            ObjectInputStream ins5;
	            bais5 = new ByteArrayInputStream(rs.getBytes("instanceTestDataTypes"));
	            ins5 = new ObjectInputStream(bais5);
				m.setInstanceTestDataTypes((List<InstanceTestDataType>) ins5.readObject());
				ins5.close();
				
				ByteArrayInputStream bais6;
	            ObjectInputStream ins6;
	            bais6 = new ByteArrayInputStream(rs.getBytes("validationContexts"));
	            ins6 = new ObjectInputStream(bais6);
				m.setValidationContexts((List<ValidationContext>) ins6.readObject());
				ins6.close();
				
				messages.add(m);
			}
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close(rs, pstmt, conn);
		}
		return messages;
	}

	@SuppressWarnings("unchecked")
	public Message getMessageById(int id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DBCon db = new DBCon();
		try {
			conn = db.connect();
			String sql = "select * from messages WHERE id=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Message m = new Message();
				m.setId(rs.getInt("id"));
				m.setName(rs.getString("name"));
				m.setDescription(rs.getString("description"));
				m.setVersion(rs.getInt("version"));
				m.setHl7EndcodedMessage(rs.getString("hl7EndcodedMessage"));
				
				
				ByteArrayInputStream bais1;
	            ObjectInputStream ins1;
	            bais1 = new ByteArrayInputStream(rs.getBytes("messageProfile"));
	            ins1 = new ObjectInputStream(bais1);
				m.setMessageProfile((MessageProfile)ins1.readObject());
				ins1.close();
				
				ByteArrayInputStream bais2;
	            ObjectInputStream ins2;
	            bais2 = new ByteArrayInputStream(rs.getBytes("listCSs"));
	            ins2 = new ObjectInputStream(bais2);
				m.setListCSs((List<ConformanceStatement>)ins2.readObject());
				ins2.close();
				
				ByteArrayInputStream bais3;
	            ObjectInputStream ins3;
	            bais3 = new ByteArrayInputStream(rs.getBytes("listCPs"));
	            ins3 = new ObjectInputStream(bais3);
				m.setListCPs((List<Predicate>) ins3.readObject());
				ins3.close();
				
				ByteArrayInputStream bais4;
	            ObjectInputStream ins4;
	            bais4 = new ByteArrayInputStream(rs.getBytes("profilePathOccurIGData"));
	            ins4 = new ObjectInputStream(bais4);
				m.setProfilePathOccurIGData((List<ProfilePathOccurIGData>) ins4.readObject());
				ins4.close();
				
				ByteArrayInputStream bais5;
	            ObjectInputStream ins5;
	            bais5 = new ByteArrayInputStream(rs.getBytes("instanceTestDataTypes"));
	            ins5 = new ObjectInputStream(bais5);
				m.setInstanceTestDataTypes((List<InstanceTestDataType>) ins5.readObject());
				ins5.close();
				
				ByteArrayInputStream bais6;
	            ObjectInputStream ins6;
	            bais6 = new ByteArrayInputStream(rs.getBytes("validationContexts"));
	            ins6 = new ObjectInputStream(bais6);
				m.setValidationContexts((List<ValidationContext>) ins6.readObject());
				ins6.close();
				
				return m;
			}
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close(rs, pstmt, conn);
		}
		return null;
	}

	public int interactionInsert(Interaction i, String author) {
		Connection conn = null;
		PreparedStatement stmt = null;
		DBCon db = new DBCon();
		int result = 0;
		try {
			conn = db.connect();
			String sql = "insert into interactions (name, description, sActor, rActor, message, author) values(?,?,?,?,?,?)";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, i.getName());
			stmt.setString(2, i.getDescription());
			stmt.setObject(3, i.getsActor());
			stmt.setObject(4, i.getrActor());
			stmt.setObject(5, i.getMessage());
			stmt.setString(6, author);
			result = stmt.executeUpdate();
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close(stmt, conn);
		}
		return result;
	}

	public int interactionUpdate(Interaction i) {
		Connection conn = null;
		PreparedStatement stmt = null;
		DBCon db = new DBCon();
		int result = 0;
		try {
			conn = db.connect();
			String sql = "update interactions set name=?, description=?, sActor=?, rActor=?, message=?, version=? where id=?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, i.getName());
			stmt.setString(2, i.getDescription());
			stmt.setObject(3, i.getsActor());
			stmt.setObject(4, i.getrActor());
			stmt.setObject(5, i.getMessage());
			stmt.setInt(6, i.getVersion());
			stmt.setInt(7, i.getId());
			result = stmt.executeUpdate();
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close(stmt, conn);
		}
		return result;
	}

	public int interactionDelete(Interaction i) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		DBCon db = new DBCon();
		int result = 0;
		try {
			conn = db.connect();
			String sql = "delete from interactions where id=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, i.getId());
			result = pstmt.executeUpdate();
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close(pstmt, conn);
		}
		return result;
	}

	public List<Interaction> getAllInteractions(String author) {
		List<Interaction> interactions = new ArrayList<Interaction>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DBCon db = new DBCon();
		try {
			conn = db.connect();
			String sql = "select * from interactions where author=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, author);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Interaction i = new Interaction();
				i.setId(rs.getInt("id"));
				i.setName(rs.getString("name"));
				i.setDescription(rs.getString("description"));
				i.setVersion(rs.getInt("version"));
				
				ByteArrayInputStream bais1;
	            ObjectInputStream ins1;
	            bais1 = new ByteArrayInputStream(rs.getBytes("sActor"));
	            ins1 = new ObjectInputStream(bais1);
				i.setsActor((Actor) ins1.readObject());
				ins1.close();
				
				ByteArrayInputStream bais2;
	            ObjectInputStream ins2;
	            bais2 = new ByteArrayInputStream(rs.getBytes("rActor"));
	            ins2 = new ObjectInputStream(bais2);
				i.setrActor((Actor) ins2.readObject());
				ins2.close();
				
				ByteArrayInputStream bais3;
	            ObjectInputStream ins3;
	            bais3 = new ByteArrayInputStream(rs.getBytes("message"));
	            ins3 = new ObjectInputStream(bais3);
				i.setMessage((Message) ins3.readObject());
				ins3.close();
				
				interactions.add(i);
			}
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close(rs, pstmt, conn);
		}
		return interactions;
	}

	public Interaction getInteractionById(int id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DBCon db = new DBCon();
		try {
			conn = db.connect();
			String sql = "select * from interactions WHERE id=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Interaction i = new Interaction();
				i.setId(rs.getInt("id"));
				i.setName(rs.getString("name"));
				i.setDescription(rs.getString("description"));
				i.setVersion(rs.getInt("version"));
				
				ByteArrayInputStream bais1;
	            ObjectInputStream ins1;
	            bais1 = new ByteArrayInputStream(rs.getBytes("sActor"));
	            ins1 = new ObjectInputStream(bais1);
				i.setsActor((Actor) ins1.readObject());
				ins1.close();
				
				ByteArrayInputStream bais2;
	            ObjectInputStream ins2;
	            bais2 = new ByteArrayInputStream(rs.getBytes("rActor"));
	            ins2 = new ObjectInputStream(bais2);
				i.setrActor((Actor) ins2.readObject());
				ins2.close();
				
				ByteArrayInputStream bais3;
	            ObjectInputStream ins3;
	            bais3 = new ByteArrayInputStream(rs.getBytes("message"));
	            ins3 = new ObjectInputStream(bais3);
				i.setMessage((Message) ins3.readObject());
				ins3.close();
				
				return i;
			}
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close(rs, pstmt, conn);
		}
		return null;
	}

	
	public int transactionInsert(Transaction tsc, String author) {
		Connection conn = null;
		PreparedStatement stmt = null;
		DBCon db = new DBCon();
		int result = 0;
		try {
			conn = db.connect();
			String sql = "insert into transactions (name, description, umld, teststeps, author) values(?,?,?,?,?)";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, tsc.getName());
			stmt.setString(2, tsc.getDescription());
			stmt.setString(3, tsc.getUmld());
			stmt.setObject(4, tsc.getTestSteps());
			stmt.setString(5, author);
			result = stmt.executeUpdate();
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close(stmt, conn);
		}
		return result;
	}

	public int transactionUpdate(Transaction tsc) {
		Connection conn = null;
		PreparedStatement stmt = null;
		DBCon db = new DBCon();
		int result = 0;
		try {
			conn = db.connect();
			String sql = "update transactions set name=?, description=?, version=?, teststeps=?, umld=? where id=?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, tsc.getName());
			stmt.setString(2, tsc.getDescription());
			stmt.setInt(3, tsc.getVersion());
			stmt.setObject(4, tsc.getTestSteps());
			stmt.setString(5, tsc.getUmld());
			stmt.setInt(6, tsc.getId());
			result = stmt.executeUpdate();
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close(stmt, conn);
		}
		return result;
	}

	public int transactionDelete(Transaction tsc) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		DBCon db = new DBCon();
		int result = 0;
		try {
			conn = db.connect();
			String sql = "delete from transactions where id=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, tsc.getId());
			result = pstmt.executeUpdate();
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close(pstmt, conn);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Transaction> getAllTransactions(String author) {
		List<Transaction> transactions = new ArrayList<Transaction>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DBCon db = new DBCon();
		try {
			conn = db.connect();
			String sql = "select * from transactions where author=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, author);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Transaction tsc = new Transaction();
				tsc.setId(rs.getInt("id"));
				tsc.setName(rs.getString("name"));
				tsc.setDescription(rs.getString("description"));
				tsc.setVersion(rs.getInt("version"));
				tsc.setUmld(rs.getString("umld"));
				
				ByteArrayInputStream bais1;
	            ObjectInputStream ins1;
	            bais1 = new ByteArrayInputStream(rs.getBytes("teststeps"));
	            ins1 = new ObjectInputStream(bais1);
	            tsc.setTestSteps((List<Integer>) ins1.readObject());
				ins1.close();
				
				transactions.add(tsc);
			}
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close(rs, pstmt, conn);
		}
		return transactions;
	}

	@SuppressWarnings("unchecked")
	public Transaction getTransactionById(int id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DBCon db = new DBCon();
		try {
			conn = db.connect();
			String sql = "select * from transactions WHERE id=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Transaction tsc = new Transaction();
				tsc.setId(rs.getInt("id"));
				tsc.setName(rs.getString("name"));
				tsc.setDescription(rs.getString("description"));
				tsc.setVersion(rs.getInt("version"));
				tsc.setUmld(rs.getString("umld"));
				
				ByteArrayInputStream bais1;
	            ObjectInputStream ins1;
	            bais1 = new ByteArrayInputStream(rs.getBytes("teststeps"));
	            ins1 = new ObjectInputStream(bais1);
	            tsc.setTestSteps((List<Integer>) ins1.readObject());
				ins1.close();
				
				return tsc;
			}
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close(rs, pstmt, conn);
		}
		return null;
	}

	public int testStepInsert(TestStep ts, String author) {
		Connection conn = null;
		PreparedStatement stmt = null;
		DBCon db = new DBCon();
		int result = 0;
		try {
			conn = db.connect();
			String sql = "insert into teststeps (name, description, type, teststorydesc, teststorycomment, teststoryprecondition, teststorypostcondition, teststorytestobjectives, teststorynotes, interactionobj, targetactorobj, author) values(?,?,?,?,?,?,?,?,?,?,?,?)";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, ts.getName());
			stmt.setString(2, ts.getDescription());
			stmt.setString(3, ts.getType());
			stmt.setString(4, ts.getTestStepStory().getDescription());
			stmt.setString(5, ts.getTestStepStory().getComments());
			stmt.setString(6, ts.getTestStepStory().getPreCondition());
			stmt.setString(7, ts.getTestStepStory().getPostCondition());
			stmt.setString(8, ts.getTestStepStory().getTestObjectives());
			stmt.setString(9, ts.getTestStepStory().getNotes());
			stmt.setObject(10, ts.getInteraction());
			stmt.setObject(11, ts.getTargetActor());
			stmt.setString(12, author);
			result = stmt.executeUpdate();
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close(stmt, conn);
		}
		return result;
	}
	public int testStepUpdate(TestStep ts) {
		Connection conn = null;
		PreparedStatement stmt = null;
		DBCon db = new DBCon();
		int result = 0;
		try {
			conn = db.connect();
			String sql = "update teststeps set name=?, description=?, type=?, version=?, teststorydesc=?, teststorycomment=?, teststoryprecondition=?, teststorypostcondition=?, teststorytestobjectives=?, teststorynotes=?, interactionobj=?, targetactorobj=?"
					+ " where id=?";
			stmt = conn.prepareStatement(sql);
			
			stmt.setString(1, ts.getName());
			stmt.setString(2, ts.getDescription());
			stmt.setString(3, ts.getType());
			stmt.setInt(4, ts.getVersion());
			stmt.setString(5, ts.getTestStepStory().getDescription());
			stmt.setString(6, ts.getTestStepStory().getComments());
			stmt.setString(7, ts.getTestStepStory().getPreCondition());
			stmt.setString(8, ts.getTestStepStory().getPostCondition());
			stmt.setString(9, ts.getTestStepStory().getTestObjectives());
			stmt.setString(10, ts.getTestStepStory().getNotes());
			stmt.setObject(11, ts.getInteraction());
			stmt.setObject(12, ts.getTargetActor());
			stmt.setInt(13, ts.getId());
			result = stmt.executeUpdate();
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close(stmt, conn);
		}
		return result;
	}

	public int testStepDelete(TestStep ts) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		DBCon db = new DBCon();
		int result = 0;
		try {
			conn = db.connect();
			String sql = "delete from teststeps where id=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, ts.getId());
			result = pstmt.executeUpdate();
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close(pstmt, conn);
		}
		return result;
	}

	public List<TestStep> getAllTestSteps(String author) {
		List<TestStep> tss = new ArrayList<TestStep>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DBCon db = new DBCon();
		try {
			conn = db.connect();
			String sql = "select * from teststeps where author=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, author);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				TestStep ts = new TestStep();
				ts.setId(rs.getInt("id"));
				ts.setName(rs.getString("name"));
				ts.setDescription(rs.getString("description"));
				ts.setType(rs.getString("type"));
				ts.setVersion(rs.getInt("version"));
				ts.setTestStepStory(new TestStory(rs.getString("teststorydesc"), rs.getString("teststorycomment"), rs.getString("teststoryprecondition"), rs.getString("teststorypostcondition"), rs.getString("teststorytestobjectives"), rs.getString("teststorynotes")));
				
				ByteArrayInputStream bais1;
	            ObjectInputStream ins1;
	            bais1 = new ByteArrayInputStream(rs.getBytes("interactionobj"));
	            ins1 = new ObjectInputStream(bais1);
				ts.setInteraction((Interaction)ins1.readObject());
				ins1.close();
				
				ByteArrayInputStream bais2;
	            ObjectInputStream ins2;
	            bais2 = new ByteArrayInputStream(rs.getBytes("targetactorobj"));
	            ins2 = new ObjectInputStream(bais2);
				ts.setTargetActor((Actor)ins2.readObject());
				ins2.close();
				
				tss.add(ts);
			}
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close(rs, pstmt, conn);
		}
		return tss;
	}

	public TestStep getTestStepById(int id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DBCon db = new DBCon();
		try {
			conn = db.connect();
			String sql = "select * from teststeps WHERE id=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				TestStep ts = new TestStep();
				ts.setId(rs.getInt("id"));
				ts.setName(rs.getString("name"));
				ts.setDescription(rs.getString("description"));
				ts.setType(rs.getString("type"));
				ts.setVersion(rs.getInt("version"));
				ts.setTestStepStory(new TestStory(rs.getString("teststorydesc"), rs.getString("teststorycomment"), rs.getString("teststoryprecondition"), rs.getString("teststorypostcondition"), rs.getString("teststorytestobjectives"), rs.getString("teststorynotes")));
				
				ByteArrayInputStream bais1;
	            ObjectInputStream ins1;
	            bais1 = new ByteArrayInputStream(rs.getBytes("interactionobj"));
	            ins1 = new ObjectInputStream(bais1);
				ts.setInteraction((Interaction)ins1.readObject());
				ins1.close();
				
				ByteArrayInputStream bais2;
	            ObjectInputStream ins2;
	            bais2 = new ByteArrayInputStream(rs.getBytes("targetactorobj"));
	            ins2 = new ObjectInputStream(bais2);
				ts.setTargetActor((Actor)ins2.readObject());
				ins2.close();
				
				return ts;
			}
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close(rs, pstmt, conn);
		}
		return null;
	}
	
	@SuppressWarnings("resource")
	public boolean getUserPasswordById(String id, String password) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DBCon db = new DBCon();
		
		try {
			conn = db.connect();
			String sql = "select * from users";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				String dbId = rs.getString("id");
				String dbPassword = rs.getString("password");
				if(dbId.equals(id) && dbPassword.equals(password)){
					db.close(rs, pstmt, conn);
					return true;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close(rs, pstmt, conn);
		}
		return false;
	}

	public List<String> getAllUsers() {
		List<String> users = new ArrayList<String>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DBCon db = new DBCon();
		try {
			conn = db.connect();
			String sql = "select * from users";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				users.add(rs.getString("id"));
			}
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close(rs, pstmt, conn);
		}
		return users;
	}
	
	public boolean checkAvailabilityDeleteTestStep(Integer id) {		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DBCon db = new DBCon();
		try {
			conn = db.connect();
			String sql = "select * from testcases";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				List<Integer> testStepIdList;
				ByteArrayInputStream bais1;
				ObjectInputStream ins1;
				bais1 = new ByteArrayInputStream(rs.getBytes("teststepobj"));
				ins1 = new ObjectInputStream(bais1);
				testStepIdList = (List<Integer>)ins1.readObject();
				ins1.close();
				
				for(Integer tsId:testStepIdList){
					if(tsId.equals(id)){
						db.close(rs, pstmt, conn);
						return false;
					}
				}
			}
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close(rs, pstmt, conn);
		}
		
		return true;
	}

	public boolean checkAvailabilityDeleteTestCase(Integer id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DBCon db = new DBCon();
		try {
			conn = db.connect();
			String sql = "select * from testplans";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				List<Integer> testCaseIdList;
				
				ByteArrayInputStream bais1;
				ObjectInputStream ins1;
				bais1 = new ByteArrayInputStream(rs.getBytes("testcases"));
				ins1 = new ObjectInputStream(bais1);
				testCaseIdList = (List<Integer>)ins1.readObject();
				ins1.close();
				
				for(Integer tcId:testCaseIdList){
					if(tcId.equals(id)){
						db.close(rs, pstmt, conn);
						return false;
					}
				}
			}
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close(rs, pstmt, conn);
		}
		
		return true;
	}
}
