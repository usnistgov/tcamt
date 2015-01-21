package gov.nist.healthcare.tcamt.view;

import gov.nist.healthcare.hl7tools.domain.Component;
import gov.nist.healthcare.hl7tools.domain.Field;
import gov.nist.healthcare.hl7tools.domain.Message;
import gov.nist.healthcare.hl7tools.domain.MetaData;
import gov.nist.healthcare.hl7tools.domain.StatementDetails;
import gov.nist.healthcare.hl7tools.service.serializer.ProfileSchemaVersion;
import gov.nist.healthcare.hl7tools.service.serializer.XMLSerializer;
import gov.nist.healthcare.hl7tools.v2.maker.core.ConversionException;
import gov.nist.healthcare.hl7tools.v2.maker.core.domain.profile.MessageProfile;
import gov.nist.healthcare.tcamt.db.DBImpl;
import gov.nist.healthcare.tcamt.domain.Actor;
import gov.nist.healthcare.tcamt.domain.InstanceSegment;
import gov.nist.healthcare.tcamt.domain.SegmentTreeModel;
import gov.nist.healthcare.tcamt.domain.TestCase;
import gov.nist.healthcare.tcamt.domain.TestPlan;
import gov.nist.healthcare.tcamt.domain.Transaction;
import gov.nist.healthcare.tcamt.domain.TestStep;
import gov.nist.healthcare.tcamt.domain.TestStory;
import gov.nist.healthcare.tcamt.domain.ValidationContext;
import gov.nist.healthcare.tcamt.service.JsonTestCaseConverter;
import gov.nist.healthcare.tcamt.service.JsonTestPlanConverter;
import gov.nist.healthcare.tcamt.service.JsonTestStepConverter;
import gov.nist.healthcare.tcamt.service.JsonTestStoryConverter;
import gov.nist.healthcare.tcamt.service.ManageInstance;
import gov.nist.healthcare.tcamt.service.TestCaseConverter;
import gov.nist.healthcare.tcamt.service.TestPlanConverter;
import gov.nist.healthcare.tcamt.service.TestStepConverter;
import gov.nist.healthcare.tcamt.service.TestStoryConverter;
import gov.nist.healthcare.tcamt.service.XMLManager;
import gov.nist.healthcare.umld.domain.sequence.Line;
import gov.nist.healthcare.umld.domain.sequence.Participant;
import gov.nist.healthcare.umld.view.SequenceDiagramDraw;
import gov.nist.healthcare.vsm.view.VSLibManager;
import gov.nist.healthcare.tcamt.view.InteractionRequestBean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;

import net.sourceforge.plantuml.SourceStringReader;
import nu.xom.Serializer;

import org.apache.commons.io.IOUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

@ManagedBean
@SessionScoped
public class TestCaseRequestBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 965282772653484407L;
	
	private TestCase newTestCase;
	private TestCase editTestCase;
	private TestCase existTestCase;
	private Integer newTestStepId;
	private Integer newTestStepIdForEdit;
	private Integer newTransactionId;
	private Integer newTransactionIdForEdit;
	
	private DBImpl dbManager = new DBImpl();
	@ManagedProperty("#{sessionBeanTCAMT}")
	private SessionBeanTCAMT sessionBeanTCAMT;
	
	private transient StreamedContent zipResourceBundleFile;
	
	private TestStoryConverter testStoryConverter;
	private TestCaseConverter tcConverter;
	private TestStepConverter tsConverter;
	private TestPlanConverter tpConverter;
	
	private String shareTo;
	
	private TestCase toBeDeletedTestCase;
	private boolean canDelete;

	/**
	 * 
	 */
	public void checkTestCaseToDelete(ActionEvent event){
		this.toBeDeletedTestCase = (TestCase) event.getComponent().getAttributes().get("testCase");
		this.setCanDelete(this.dbManager.checkAvailabilityDeleteTestCase(this.toBeDeletedTestCase.getId()));
	}
	
	public void delTestCase(ActionEvent event) {
		this.dbManager.testCaseDelete(this.toBeDeletedTestCase);
		this.sessionBeanTCAMT.updateTestCases();
		this.init();
	}
	
	public void cloneTestCase(ActionEvent event) throws CloneNotSupportedException {	
		TestCase tsc = (TestCase)((TestCase)event.getComponent().getAttributes().get("testCase")).clone();
		tsc.setName("Copy_" + tsc.getName());
		tsc.setVersion(0);
		this.dbManager.testCaseInsert(tsc, this.sessionBeanTCAMT.getLoggedId());
		this.sessionBeanTCAMT.updateTestCases();
		this.init();
	}

	public TestCaseRequestBean() {
		super();
		this.init();
	}
	
	private void init(){
		this.newTestCase = new TestCase();
		this.editTestCase = new TestCase();
		this.existTestCase = new TestCase();
		this.newTestStepId = null;
		this.newTestStepIdForEdit = null;
		this.newTransactionId = null;
		this.newTransactionIdForEdit = null;
	}

	public void createTestCase() {
		this.init();
	}

	public void addTestCase() throws CloneNotSupportedException, IOException {
		SequenceDiagramDraw sequenceDiagramDraw = new SequenceDiagramDraw();		
		this.drawSequenceDiagram(this.newTestCase, sequenceDiagramDraw);
		sequenceDiagramDraw.setTitle(this.newTestCase.getName());
		sequenceDiagramDraw.setHideFoot(true);
		this.newTestCase.setUmld(sequenceDiagramDraw.getUmlDiagram().getCode());
		
		
		this.dbManager.testCaseInsert(this.newTestCase, this.sessionBeanTCAMT.getLoggedId());
		this.sessionBeanTCAMT.updateTestCases();
	}
	
	public void shareTestCase(){
		this.dbManager.testCaseInsert(this.editTestCase, this.shareTo);
		this.sessionBeanTCAMT.updateTestCases();
	}
	
	public void updateTestCase() throws CloneNotSupportedException, IOException {
		SequenceDiagramDraw sequenceDiagramDraw = new SequenceDiagramDraw();		
		this.drawSequenceDiagram(this.editTestCase, sequenceDiagramDraw);
		sequenceDiagramDraw.setTitle(this.editTestCase.getName());
		sequenceDiagramDraw.setHideFoot(true);	
		this.editTestCase.setUmld(sequenceDiagramDraw.getUmlDiagram().getCode());
		this.editTestCase.setVersion(this.editTestCase.getVersion() + 1);
		
		this.dbManager.testCaseUpdate(this.editTestCase);
		this.sessionBeanTCAMT.updateTestCases();
	}
	
	public DefaultStreamedContent loadUMLD(TestCase tsc) throws IOException{
		FacesContext context = FacesContext.getCurrentInstance();
	    HttpServletRequest myRequest = (HttpServletRequest) context.getExternalContext().getRequest();
	    String source =  myRequest.getParameter("code");
		if(tsc != null){
			source = tsc.getUmld();
			
		}
		
		ByteArrayOutputStream outputStream = null;
		byte[] bytes;
		outputStream = new ByteArrayOutputStream();
		SourceStringReader reader = new SourceStringReader(source);
		reader.generateImage(outputStream);
		bytes = outputStream.toByteArray();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        return new DefaultStreamedContent(inputStream, "image/png");
	}
	
	private void drawSequenceDiagram(TestCase testCase, SequenceDiagramDraw sequenceDiagramDraw){
		List<Participant> participants = new ArrayList<Participant>();
		List<Line> lines = new ArrayList<Line>();
		
		
		if(testCase.getType().equals("Data Instance Test Case")){
		}else {
			for(Actor actor:testCase.getListActors()){
				if(testCase.getSutActorId().equals(actor.getId())){
					participants.add(new Participant(actor.getName(), "SUT"));
				}else {
					participants.add(new Participant(actor.getName(), ""));
				}
			}
		}
		
		
		participants.add(new Participant("TestBed", ""));
		participants.add(new Participant("User", ""));
		
		for(Integer tsId:testCase.getTestSteps()){
			
			TestStep ts = this.dbManager.getTestStepById(tsId);
			
			if(ts.getType().equals("Interaction")){
				Line line = new Line();
				
				line.setArrow("->");
				line.setMessage(ts.getInteraction().getMessage().getName());
				if(testCase.getType().equals("Data Instance Test Case")){
					line.setSender(this.findParticipant("User", participants));
					line.setReceiver(this.findParticipant("TestBed", participants));
				}else{
					line.setSender(this.findParticipant(ts.getInteraction().getsActor().getName(), participants));
					line.setReceiver(this.findParticipant(ts.getInteraction().getrActor().getName(), participants));
				}
				line.setType("sequence");

				lines.add(line);
			}else if(ts.getType().equals("Manual")){
				Line line = new Line();
				
				line.setArrow("->");
				line.setMessage(ts.getDescription());
				if(testCase.getType().equals("Data Instance Test Case")){
					line.setSender(this.findParticipant("TestBed", participants));
					line.setReceiver(this.findParticipant("TestBed", participants));
				}else {
					line.setSender(this.findParticipant(ts.getTargetActor().getName(), participants));
					line.setReceiver(this.findParticipant(ts.getTargetActor().getName(), participants));
				}
				line.setType("sequence");

				lines.add(line);
			}
			else if(ts.getType().equals("Profile") || ts.getType().equals("Message")){
				Line line = new Line();
				
				line.setArrow("->");
				line.setMessage(ts.getInteraction().getMessage().getName());
				line.setSender(this.findParticipant("User", participants));
				line.setReceiver(this.findParticipant("TestBed", participants));
				line.setType("sequence");

				lines.add(line);
			}
		}
		
		sequenceDiagramDraw.setParticipants(participants);
		sequenceDiagramDraw.setLines(lines);
	}
	
	private Participant findParticipant(String name, List<Participant> participants){
		for(Participant p : participants){
			if(p!=null && p.getName() != null)
				if(p.getName().equals(name)) return p;
		}
		return null;
	}
	
	public void saveTestCase() {
		this.editTestCase.setVersion(this.editTestCase.getVersion() + 1);
		this.dbManager.testCaseUpdate(this.editTestCase);
		this.sessionBeanTCAMT.updateTestCases();
	}

	public void selectEditTestCase(ActionEvent event) {
		this.init();
		this.existTestCase = (TestCase) event.getComponent().getAttributes().get("testCase");
		this.editTestCase.setId(existTestCase.getId());
		this.editTestCase.setName(existTestCase.getName());
		this.editTestCase.setDescription(existTestCase.getDescription());
		this.editTestCase.setVersion(existTestCase.getVersion());
		this.editTestCase.setTestSteps(existTestCase.getTestSteps());
		this.editTestCase.setTransactions(existTestCase.getTransactions());
		this.editTestCase.setTestCaseStory(this.existTestCase.getTestCaseStory());
		this.editTestCase.setUmld(existTestCase.getUmld());
		this.editTestCase.setType(this.existTestCase.getType());
		this.editTestCase.setListActors(this.existTestCase.getListActors());
		this.editTestCase.setSutActorId(this.existTestCase.getSutActorId());
		this.editTestCase.setTestScenario(this.existTestCase.getTestScenario());
		this.shareTo = "";
	}
	
	public void addTestStep(){
		this.newTestCase.getTestSteps().add(newTestStepId);
		
		TestStep ts = this.dbManager.getTestStepById(this.newTestStepId);
		if(ts.getType().equals("Interaction")){
			this.newTestCase.addActor(ts.getInteraction().getsActor());
			this.newTestCase.addActor(ts.getInteraction().getrActor());
		}else if(ts.getType().equals("Manual")){
			this.newTestCase.addActor(ts.getTargetActor());
		}
			
		this.newTestCase.getTransactions().add(ts.getType());
		this.newTestStepId = null;
	}
	
	public void addTransaction(){
		Transaction tsc = this.dbManager.getTransactionById(this.newTransactionId);
		for(Integer id: tsc.getTestSteps()){
			this.newTestCase.getTestSteps().add(id);
			
			TestStep ts = this.dbManager.getTestStepById(id);
			if(ts.getType().equals("Interaction")){
				this.newTestCase.addActor(ts.getInteraction().getsActor());
				this.newTestCase.addActor(ts.getInteraction().getrActor());
			}else if(ts.getType().equals("Manual")){
				this.newTestCase.addActor(ts.getTargetActor());
			}
			
			this.newTestCase.getTransactions().add("TestStep Group: " + tsc.getName());
		}
		
		this.newTransactionId = null;
	}
	
	public void addTestStepForEdit(){
		this.editTestCase.getTestSteps().add(newTestStepIdForEdit);
		
		TestStep ts = this.dbManager.getTestStepById(newTestStepIdForEdit);
		
		if(ts.getType().equals("Interaction")){
			this.editTestCase.addActor(ts.getInteraction().getsActor());
			this.editTestCase.addActor(ts.getInteraction().getrActor());
		}else if(ts.getType().equals("Manual")){
			this.newTestCase.addActor(ts.getTargetActor());
		}
		
		this.editTestCase.getTransactions().add(ts.getType());
		this.newTestStepIdForEdit = null;
	}
	
	public void addTransactionForEdit(){
		Transaction tsc = this.dbManager.getTransactionById(this.newTransactionIdForEdit);
		for(Integer id: tsc.getTestSteps()){
			this.editTestCase.getTestSteps().add(id);
			
			TestStep ts = this.dbManager.getTestStepById(id);
			
			if(ts.getType().equals("Interaction")){
				this.editTestCase.addActor(ts.getInteraction().getsActor());
				this.editTestCase.addActor(ts.getInteraction().getrActor());
			}else if(ts.getType().equals("Manual")){
				this.newTestCase.addActor(ts.getTargetActor());
			}
			
			this.editTestCase.getTransactions().add("TestStep Group: " + tsc.getName());
		}
		
		this.newTransactionIdForEdit = null;
	}
	
	public void delTestStep(ActionEvent event){
		int num = (Integer) event.getComponent().getAttributes().get("num");
		this.newTestCase.getTestSteps().remove(num);
		this.newTestCase.getTransactions().remove(num);
	}
	
	public void moveUpTestStep(ActionEvent event){
		int num = (Integer) event.getComponent().getAttributes().get("num");
	    if(num != 0)	Collections.swap(this.newTestCase.getTestSteps(), num, num - 1);
	    if(num != 0)	Collections.swap(this.newTestCase.getTransactions(), num, num - 1);
	}
	
	public void moveDownTestStep(ActionEvent event){
		int num = (Integer) event.getComponent().getAttributes().get("num");
		if(num != this.newTestCase.getTestSteps().size() -1)	  Collections.swap(this.newTestCase.getTestSteps(), num, num + 1);
		if(num != this.newTestCase.getTransactions().size() -1)	  Collections.swap(this.newTestCase.getTransactions(), num, num + 1);
	}
	
	public void delTestStepForEdit(ActionEvent event){
		int num = (Integer) event.getComponent().getAttributes().get("num");
		this.editTestCase.getTestSteps().remove(num);
		this.editTestCase.getTransactions().remove(num);
	}
	
	public void moveUpTestStepForEdit(ActionEvent event){
		int num = (Integer) event.getComponent().getAttributes().get("num");
	    if(num != 0)	Collections.swap(this.editTestCase.getTestSteps(), num, num - 1);
	    if(num != 0)	Collections.swap(this.editTestCase.getTransactions(), num, num - 1);
	}
	
	public void moveDownTestStepForEdit(ActionEvent event){
		int num = (Integer) event.getComponent().getAttributes().get("num");
		if(num != this.editTestCase.getTestSteps().size() -1)	  Collections.swap(this.editTestCase.getTestSteps(), num, num + 1);
		if(num != this.editTestCase.getTransactions().size() -1)	  Collections.swap(this.editTestCase.getTransactions(), num, num + 1);
	}
	
	public TestStep findTestStep(int id){
		return this.dbManager.getTestStepById(id);
	}
	
	public String findTransactionName(int num){
		return this.newTestCase.getTransactions().get(num);
	}
	
	public String findTransactionNameForEdit(int num){
		return this.editTestCase.getTransactions().get(num);
	}
	public void downloadResourceBundleForTestPlan(TestPlan tp) throws IOException, DocumentException, ConversionException{
		this.setTestStoryConverter(new JsonTestStoryConverter());
		this.setTpConverter(new JsonTestPlanConverter());
		this.setTcConverter(new JsonTestCaseConverter());
		this.setTsConverter(new JsonTestStepConverter());
		
		
		String outFilename = "ResourceBundle_TestPlan_" + tp.getName() + ".zip";
		ByteArrayOutputStream outputStream = null;
		byte[] bytes;
		outputStream = new ByteArrayOutputStream();
		ZipOutputStream out = new ZipOutputStream(outputStream);
		
		
		String testPlanPath = tp.getName();
		this.generateTestPlanJsonRB(out, tp, testPlanPath);
		
		for(Integer key:tp.getTestCases()){
			TestCase tc = this.dbManager.getTestCaseById(key);
			String testCasePath = testPlanPath + File.separator + tc.getName();
			this.generateTestStoryRB(out, tc.getTestCaseStory(), testCasePath);
			this.generateTestCaseJsonRB(out, tc, testCasePath);
			
			int tsNum = 0;
			for(Integer id: tc.getTestSteps()){
				TestStep ts = this.dbManager.getTestStepById(id);
				String testStepPath = testCasePath + File.separator + "["+ (tsNum + 1) + "]" + tc.getTransactions().get(tsNum) + "_" +ts.getName() ;
				this.generateTestStoryRB(out, ts.getTestStepStory(), testStepPath);
				this.generateTestStepJsonRB(out, ts, testStepPath);
				
				if(!ts.getType().equals("Manual")){
					this.generateDataSheetRB(out, ts, testStepPath);
					this.generateMessageRB(out, ts.getInteraction().getMessage().getHl7EndcodedMessage(), testStepPath);
					this.generateValidationContextRB(out, ts, testStepPath);
					this.generateTableLibraryRB(out, ts.getInteraction().getMessage().getMessageProfile(), testStepPath);
					this.generateProfileRB(out, ts.getInteraction().getMessage().getMessageProfile(), testStepPath);
				}
				
	        	tsNum = tsNum + 1;
			}
			
		}	
		out.close();
		bytes = outputStream.toByteArray();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
		this.setZipResourceBundleFile(new DefaultStreamedContent(inputStream, "application/zip", outFilename));
	}
	
	public void downloadResourceBundleForTestCase(TestCase tc) throws IOException, DocumentException, ConversionException{
		this.setTestStoryConverter(new JsonTestStoryConverter());
		this.setTcConverter(new JsonTestCaseConverter());
		this.setTsConverter(new JsonTestStepConverter());
		
		
		String outFilename = "ResourceBundle_TestCase_" + tc.getName() + ".zip";
		ByteArrayOutputStream outputStream = null;
		byte[] bytes;
		outputStream = new ByteArrayOutputStream();
		ZipOutputStream out = new ZipOutputStream(outputStream);
		String testCasePath = tc.getName();
		
		this.generateTestStoryRB(out, tc.getTestCaseStory(), testCasePath);
		this.generateTestCaseJsonRB(out, tc, testCasePath);
		
		int tsNum = 0;
		for(Integer id: tc.getTestSteps()){
			TestStep ts = this.dbManager.getTestStepById(id);
			String testStepPath = testCasePath + File.separator + "["+ (tsNum + 1) + "]" + tc.getTransactions().get(tsNum) + "_" +ts.getName() ;
			this.generateTestStoryRB(out, ts.getTestStepStory(), testStepPath);
			this.generateTestStepJsonRB(out, ts, testStepPath);
			
			if(!ts.getType().equals("Manual")){
				this.generateDataSheetRB(out, ts, testStepPath);
				this.generateMessageRB(out, ts.getInteraction().getMessage().getHl7EndcodedMessage(), testStepPath);
				this.generateValidationContextRB(out, ts, testStepPath);
				this.generateTableLibraryRB(out, ts.getInteraction().getMessage().getMessageProfile(), testStepPath);
				this.generateProfileRB(out, ts.getInteraction().getMessage().getMessageProfile(), testStepPath);
			}
			
        	tsNum = tsNum + 1;
		}
		
		out.close();
		bytes = outputStream.toByteArray();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
		this.setZipResourceBundleFile(new DefaultStreamedContent(inputStream, "application/zip", outFilename));
	}
	
	private void generateTestPlanJsonRB(ZipOutputStream out, TestPlan tp, String path) throws IOException, ConversionException {
		byte[] buf = new byte[1024];
		out.putNextEntry(new ZipEntry(path + File.separator + "TestPlan.json"));
		InputStream inTP = IOUtils.toInputStream(this.tpConverter.toString(tp));
		int lenTP;
        while ((lenTP = inTP.read(buf)) > 0) {
            out.write(buf, 0, lenTP);
        }
        out.closeEntry();
        inTP.close();
		
	}
	
	private void generateTestCaseJsonRB(ZipOutputStream out, TestCase tc, String path) throws IOException, ConversionException {
		byte[] buf = new byte[1024];
		out.putNextEntry(new ZipEntry(path + File.separator + "TestCase.json"));
		InputStream inTC = IOUtils.toInputStream(this.tcConverter.toString(tc));
		int lenTC;
        while ((lenTC = inTC.read(buf)) > 0) {
            out.write(buf, 0, lenTC);
        }
        out.closeEntry();
        inTC.close();
		
	}
	
	private void generateTestStepJsonRB(ZipOutputStream out, TestStep ts, String path) throws IOException, ConversionException {
		byte[] buf = new byte[1024];
		out.putNextEntry(new ZipEntry(path + File.separator + "TestStep.json"));
		InputStream inTS = IOUtils.toInputStream(this.tsConverter.toString(ts));
		int lenTS;
        while ((lenTS = inTS.read(buf)) > 0) {
            out.write(buf, 0, lenTS);
        }
        out.closeEntry();
        inTS.close();
		
	}
	
	private void generateTestStoryRB(ZipOutputStream out, TestStory testStory, String path) throws IOException, DocumentException, ConversionException {
		byte[] buf = new byte[1024];
		out.putNextEntry(new ZipEntry(path + File.separator + "TestStory.json"));
        InputStream inTS = IOUtils.toInputStream(this.testStoryConverter.toString(testStory));
        int lenTS;
        while ((lenTS = inTS.read(buf)) > 0) {
            out.write(buf, 0, lenTS);
        }
        out.closeEntry();
        inTS.close();
		
		out.putNextEntry(new ZipEntry(path + File.separator + "TestStory.html"));
        String testCaseStoryStr = this.generateTestStory(testStory);
        InputStream inTestStory = IOUtils.toInputStream(testCaseStoryStr);
        int lenTestStory;
        while ((lenTestStory = inTestStory.read(buf)) > 0) {
            out.write(buf, 0, lenTestStory);
        }
        inTestStory.close();
        out.closeEntry();
        
        out.putNextEntry(new ZipEntry(path + File.separator + "TestStory.pdf"));
        
        Document document = new Document(PageSize.LETTER);
        ByteArrayOutputStream outputStream = null;
		byte[] bytes;
		outputStream = new ByteArrayOutputStream();
		
        PdfWriter pdfWriter = PdfWriter.getInstance(document, outputStream);
        document.open();
        document.addAuthor("SSD ITL NIST");
        document.addCreator("TCAMT");
        document.addSubject("Test Story");
        document.addCreationDate();
        document.addTitle("Test Story");
        
        
        XMLWorkerHelper worker = XMLWorkerHelper.getInstance();
        
        worker.parseXHtml(pdfWriter, document, new StringReader(testCaseStoryStr));
        document.close();
        
        bytes = outputStream.toByteArray();
		ByteArrayInputStream inTestStoryPDF = new ByteArrayInputStream(bytes);
        int lenTestStoryPDF;
        while ((lenTestStoryPDF = inTestStoryPDF.read(buf)) > 0) {
            out.write(buf, 0, lenTestStoryPDF);
        }
        inTestStoryPDF.close();
        out.closeEntry();
	}
	
	private String generateTestStory(TestStory testStory) {
		if(testStory == null){
			testStory = new TestStory();
		}

		String relativeWebPath = "resources" + File.separator + "templates" + File.separator + "TestStory.html";
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ServletContext servletContext = (ServletContext) facesContext.getExternalContext().getContext();
        String absoluteDiskPath = servletContext.getRealPath(relativeWebPath);
        
		byte[] encoded;
		try {
			encoded = Files.readAllBytes(Paths.get(absoluteDiskPath));
			String testStoryStr = new String(encoded, StandardCharsets.UTF_8);
			testStoryStr = testStoryStr.replace("?Description?", testStory.getDescription());
			testStoryStr = testStoryStr.replace("?Comments?", testStory.getComments());
			testStoryStr = testStoryStr.replace("?PreCondition?", testStory.getPreCondition());
			testStoryStr = testStoryStr.replace("?PostCondition?", testStory.getPostCondition());
			testStoryStr = testStoryStr.replace("?TestObjectives?", testStory.getTestObjectives());
			testStoryStr = testStoryStr.replace("?Notes?", testStory.getNotes());
			testStoryStr = testStoryStr.replace("<br>", " ");
			testStoryStr = testStoryStr.replace("</br>", " ");
			return testStoryStr;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private void generateMessageRB(ZipOutputStream out, String messageStr, String path) throws IOException {
		byte[] buf = new byte[1024];
		out.putNextEntry(new ZipEntry(path + File.separator + "Message.txt"));
		if(messageStr != null){
			InputStream inMessage = IOUtils.toInputStream(messageStr);
			int lenMessage;
            while ((lenMessage = inMessage.read(buf)) > 0) {
                out.write(buf, 0, lenMessage);
            }
            inMessage.close();
		}
        out.closeEntry();
		
	}
	
	private void generateTableLibraryRB(ZipOutputStream out, MessageProfile mp, String path) throws IOException {
		byte[] buf = new byte[1024];
		out.putNextEntry(new ZipEntry(path + File.separator + "TableLibrary.xml"));
		if(mp != null){
			VSLibManager vsManager = new VSLibManager();
			InputStream inMessage = vsManager.getStreamCodeTable(mp.getMessage());
			int lenMessage;
            while ((lenMessage = inMessage.read(buf)) > 0) {
                out.write(buf, 0, lenMessage);
            }
            inMessage.close();
		}
        out.closeEntry();
		
	}
	
	private void generateProfileRB(ZipOutputStream out, MessageProfile mp, String path) throws IOException {
		byte[] buf = new byte[1024];
		FileOutputStream os = null;
		out.putNextEntry(new ZipEntry(path + File.separator + "ConformanceProfile.xml"));
		if(mp != null){


			MetaData metaData = mp.getMetaData();
			XMLSerializer xmlSerializer = new XMLSerializer();
			Message m = mp.getMessage();
			String schemaV = mp.getMetaData().getHl7RulesSchema();
			schemaV = schemaV.replace(".", "");
			ProfileSchemaVersion schemaVersion = ProfileSchemaVersion.valueOf("V" + schemaV);
			nu.xom.Element result = xmlSerializer.serialize(m, metaData.getHl7Version(), schemaVersion);

			File f = File.createTempFile("ConformanceProfile", ".xml");
			os = new FileOutputStream(f);
			Serializer serializer = new Serializer(os, "UTF-8");
			serializer.setIndent(4);
			serializer.setMaxLength(400);
			serializer.write(new nu.xom.Document(result));
			os.flush();
			os.close();
			
			InputStream inMessage = new FileInputStream(f);
			int lenMessage;
            while ((lenMessage = inMessage.read(buf)) > 0) {
                out.write(buf, 0, lenMessage);
            }
            inMessage.close();
		}
        out.closeEntry();
		
	}
	
	private void generateValidationContextRB(ZipOutputStream out, TestStep ts, String path) throws IOException {
		byte[] buf = new byte[1024];
		out.putNextEntry(new ZipEntry(path + File.separator + "ValidationContext.xml"));
		String validationContextsStr = this.generateValidationContexts(ts.getInteraction().getMessage().getValidationContexts());
		InputStream inValidationContexts = IOUtils.toInputStream(validationContextsStr);
        int len;
        while ((len = inValidationContexts.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        inValidationContexts.close();
		out.closeEntry();
	}
	
	private String generateValidationContexts(List<ValidationContext> validationContexts) {
		String relativeWebPath = "resources" + File.separator + "templates" + File.separator + "ValidationContextTemplate.xml";
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ServletContext servletContext = (ServletContext) facesContext.getExternalContext().getContext();
        String absoluteDiskPath = servletContext.getRealPath(relativeWebPath);
        
        try {
        	org.w3c.dom.Document doc = XMLManager.readFileAsDocument(absoluteDiskPath);
        	
        	for(ValidationContext vc:validationContexts){
        		StatementDetails sd = vc.getStatementDetails();
        		
        		if(sd.getConstructionType().equals("Single")){
        			if(sd.getPattern().equals("Constant Value Check")){
        				if(sd.getSubPattern().equals("Single Value")){
        					Element root = (Element)(doc.getElementsByTagNameNS("http://www.nist.gov/healthcare/validation/message/hl7/v2/context", "HL7V2MessageValidationContextDefinition").item(0));

        					Element pluginCheckElm = doc.createElement("con:PluginCheck");
        					pluginCheckElm.setAttribute("Name", "gov.nist.healthcare.core.validation.message.plugin.value.ValueCheckPlugin");
        					Element valElm = doc.createElement("val:Params");
        					valElm.setAttribute("xmlns:val", "http://www.nist.gov/healthcare/validation");
        					valElm.setTextContent("{\"location\":\"" + sd.getTargetNodeLocation() + "\",\"values\":[{\"type\":\"PLAIN\",\"text\":\""+ sd.getLiteralValue() +"\",\"options\":{\"required\":true}}],\"matchAll\":false,\"minMatch\":0,\"maxMatch\":null}");
        					pluginCheckElm.appendChild(valElm);
        					root.appendChild(pluginCheckElm);
        				}
        			}else if(sd.getPattern().equals("Presence Check")){
        				Element root = (Element)(doc.getElementsByTagNameNS("http://www.nist.gov/healthcare/validation/message/hl7/v2/context", "HL7V2MessageValidationContextDefinition").item(0));

    					Element pluginCheckElm = doc.createElement("con:PluginCheck");
    					pluginCheckElm.setAttribute("Name", "gov.nist.healthcare.core.validation.message.plugin.value.ValueCheckPlugin");
    					Element valElm = doc.createElement("val:Params");
    					valElm.setAttribute("xmlns:val", "http://www.nist.gov/healthcare/validation");
    					valElm.setTextContent("{\"location\":\"" + sd.getAnotherNodeLocation() + "\",\"values\":[{\"type\":\"PRESENT\",\"text\":null,\"options\":null}],\"matchAll\":false,\"minMatch\":0,\"maxMatch\":null}");
    					pluginCheckElm.appendChild(valElm);
    					root.appendChild(pluginCheckElm);
        			}
        		}
        	}
        	
        	
        	String vcStr = XMLManager.docToString(doc);
        	return vcStr;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
        
		return null;
	}
	
	
	
	private void generateDataSheetRB(ZipOutputStream out, TestStep ts, String outPath) throws IOException, DocumentException {
		ManageInstance manageInstanceService = new ManageInstance();
		
		String relativeWebPath = "resources" + File.separator + "templates" + File.separator + "DataSheetTemplate.html";
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ServletContext servletContext = (ServletContext) facesContext.getExternalContext().getContext();
        String absoluteDiskPath = servletContext.getRealPath(relativeWebPath);
        
        String dataSheetStr = "";
        byte[] encoded;
		try {
			encoded = Files.readAllBytes(Paths.get(absoluteDiskPath));
			dataSheetStr = new String(encoded, StandardCharsets.UTF_8);
			
			InteractionRequestBean irb = new InteractionRequestBean();
			irb.setEditInteraction(ts.getInteraction());
			irb.setMessageTreeRoot(manageInstanceService.generateMessageTreeForElementOccur(ts.getInteraction().getMessage()));
			irb.readHL7Message();
			
			String htmlStr = "";
			
			for(InstanceSegment is: irb.getInstanceSegments()){
				
				int startIndex = is.getPath().lastIndexOf(".")  + 1;
				int endIndex = is.getPath().lastIndexOf("[");
				
				String segmentName =  is.getPath().substring(startIndex, endIndex);
				
				
				htmlStr = htmlStr + "<p>" + segmentName + "</p><table><thead><tr><th>Location</th><th>Data Element</th><th>Data</th><th>Categorization</th></tr></thead><tbody>";
				
				for(TreeNode field: is.getSegmentTreeNode().getChildren()){
					SegmentTreeModel stm = (SegmentTreeModel)field.getData();
					Field fieldObj = (Field)stm.getNode();
					if(stm.getOccurrence() > 0) {
						String path = stm.getPath().substring(stm.getPath().indexOf(segmentName));			
						String data = "";
						String categorization = "";
						
						if(stm.getData() != null){
							data = stm.getData().replace("^~\\&", "^~\\&amp;");
						}
						
						if(stm.getType() != null){
							categorization = stm.getType().getValue();
						}

						htmlStr = htmlStr + "<tr><td>" + path + "</td><td>" + fieldObj.getDescription() + "</td><td>" + data + "</td><td>" + categorization + "</td></tr>";
						
						
						for(TreeNode component:field.getChildren()){
							SegmentTreeModel componentStm = (SegmentTreeModel)component.getData();
							Component componentObj = (Component)componentStm.getNode();
							
							String path2 = componentStm.getPath().substring(componentStm.getPath().indexOf(segmentName));		
							String data2 = "";
							String categorization2 = "";
							
							if(componentStm.getData() != null){
								data2 = componentStm.getData();
							}
							
							if(componentStm.getType() != null){
								categorization2 = componentStm.getType().getValue();
							}

							htmlStr = htmlStr + "<tr><td  class=\"embSpace\">" + path2 + "</td><td class=\"embSpace\">" + componentObj.getDescription() + "</td><td class=\"childField\">" + data2 + "</td><td class=\"childField\">" + categorization2 + "</td></tr>";
							
							for(TreeNode subcomponent:component.getChildren()){
								SegmentTreeModel subcomponentStm = (SegmentTreeModel)subcomponent.getData();
								Component subcomponentObj = (Component)subcomponentStm.getNode();
								
								String path3 = subcomponentStm.getPath().substring(subcomponentStm.getPath().indexOf(segmentName));	
								String data3 = "";
								String categorization3 = "";
								
								if(subcomponentStm.getData() != null){
									data3 = subcomponentStm.getData();
								}
								
								if(subcomponentStm.getType() != null){
									categorization3 = subcomponentStm.getType().getValue();
								}

								htmlStr = htmlStr + "<tr><td class=\"embSubSpace\">" + path3 + "</td><td class=\"embSubSpace\">" + subcomponentObj.getDescription() + "</td><td class=\"childField\">" + data3 + "</td><td class=\"childField\">" + categorization3 + "</td></tr>";
								
							}
						}
					}
				}
				
				htmlStr = htmlStr + "</tbody></table><br/>";
			}

			dataSheetStr = dataSheetStr.replace("?TSDESC?", ts.getName());
			dataSheetStr = dataSheetStr.replace("?TSNAME?", ts.getDescription());
			dataSheetStr = dataSheetStr.replace("?Sheets?", htmlStr);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		byte[] buf = new byte[1024];
		out.putNextEntry(new ZipEntry(outPath + File.separator + "DataSheet.html"));
		InputStream inDataSheets = IOUtils.toInputStream(dataSheetStr);
        int len;
        while ((len = inDataSheets.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        inDataSheets.close();
        out.closeEntry();
        out.putNextEntry(new ZipEntry(outPath + File.separator + "DataSheet.pdf"));
        
        
        ByteArrayOutputStream outputStream = null;
		byte[] bytes;
		outputStream = new ByteArrayOutputStream();
        
        
        Document document = new Document(PageSize.LETTER);
        PdfWriter pdfWriter = PdfWriter.getInstance(document, outputStream);
        document.open();
        document.addAuthor("SSD ITL NIST");
        document.addCreator("TCAMT");
        document.addSubject("Test Case Information");
        document.addCreationDate();
        document.addTitle("Test Case Information");
        
        
        XMLWorkerHelper worker = XMLWorkerHelper.getInstance();
        
        worker.parseXHtml(pdfWriter, document, new StringReader(dataSheetStr));
        document.close();
        
        bytes = outputStream.toByteArray();
        
        ByteArrayInputStream  inDataSheetsPDF = new ByteArrayInputStream(bytes);
        int lenDataSheetsPDF;
        while ((lenDataSheetsPDF = inDataSheetsPDF.read(buf)) > 0) {
            out.write(buf, 0, lenDataSheetsPDF);
        }
        inDataSheetsPDF.close();
        
        out.closeEntry();
		
	}

	/**
	 * 
	 */

	public TestCase getNewTestCase() {
		return newTestCase;
	}

	public void setNewTestCase(TestCase newTestCase) {
		this.newTestCase = newTestCase;
	}

	public TestCase getEditTestCase() {
		return editTestCase;
	}

	public void setEditTestCase(TestCase editTestCase) {
		this.editTestCase = editTestCase;
	}

	public TestCase getExistTestCase() {
		return existTestCase;
	}

	public void setExistTestCase(TestCase existTestCase) {
		this.existTestCase = existTestCase;
	}

	public DBImpl getDbManager() {
		return dbManager;
	}

	public void setDbManager(DBImpl dbManager) {
		this.dbManager = dbManager;
	}
	
	public SessionBeanTCAMT getSessionBeanTCAMT() {
		return sessionBeanTCAMT;
	}

	public void setSessionBeanTCAMT(SessionBeanTCAMT sessionBeanTCAMT) {
		this.sessionBeanTCAMT = sessionBeanTCAMT;
	}

	public Integer getNewTestStepId() {
		return newTestStepId;
	}

	public void setNewTestStepId(Integer newTestStepId) {
		this.newTestStepId = newTestStepId;
	}

	public Integer getNewTestStepIdForEdit() {
		return newTestStepIdForEdit;
	}

	public void setNewTestStepIdForEdit(Integer newTestStepIdForEdit) {
		this.newTestStepIdForEdit = newTestStepIdForEdit;
	}
	
	public List<TestCase> getTestCases(){
		return this.sessionBeanTCAMT.getTestcases();
	}

	public Integer getNewTransactionId() {
		return newTransactionId;
	}

	public void setNewTransactionId(Integer newTransactionId) {
		this.newTransactionId = newTransactionId;
	}

	public Integer getNewTransactionIdForEdit() {
		return newTransactionIdForEdit;
	}

	public void setNewTransactionIdForEdit(Integer newTransactionIdForEdit) {
		this.newTransactionIdForEdit = newTransactionIdForEdit;
	}

	public StreamedContent getZipResourceBundleFile() {
		return zipResourceBundleFile;
	}

	public void setZipResourceBundleFile(StreamedContent zipResourceBundleFile) {
		this.zipResourceBundleFile = zipResourceBundleFile;
	}

	public TestStoryConverter getTestStoryConverter() {
		return testStoryConverter;
	}

	public void setTestStoryConverter(TestStoryConverter testStoryConverter) {
		this.testStoryConverter = testStoryConverter;
	}

	public TestCaseConverter getTcConverter() {
		return tcConverter;
	}

	public void setTcConverter(TestCaseConverter tcConverter) {
		this.tcConverter = tcConverter;
	}

	public TestStepConverter getTsConverter() {
		return tsConverter;
	}

	public void setTsConverter(TestStepConverter tsConverter) {
		this.tsConverter = tsConverter;
	}

	public TestPlanConverter getTpConverter() {
		return tpConverter;
	}

	public void setTpConverter(TestPlanConverter tpConverter) {
		this.tpConverter = tpConverter;
	}

	public String getShareTo() {
		return shareTo;
	}

	public void setShareTo(String shareTo) {
		this.shareTo = shareTo;
	}

	public TestCase getToBeDeletedTestCase() {
		return toBeDeletedTestCase;
	}

	public void setToBeDeletedTestCase(TestCase toBeDeletedTestCase) {
		this.toBeDeletedTestCase = toBeDeletedTestCase;
	}

	public boolean isCanDelete() {
		return canDelete;
	}

	public void setCanDelete(boolean canDelete) {
		this.canDelete = canDelete;
	}
	
	
	
}
