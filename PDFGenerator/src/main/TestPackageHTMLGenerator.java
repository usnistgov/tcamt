package main;

import gov.nist.healthcare.tcamt.jsonDomain.TestCase;
import gov.nist.healthcare.tcamt.jsonDomain.TestCaseGroup;
import gov.nist.healthcare.tcamt.jsonDomain.TestPlan;
import gov.nist.healthcare.tcamt.jsonDomain.TestStep;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;

public class TestPackageHTMLGenerator {
	private String rbPath = "/Users/jungyubw/Downloads/ONC_Certification";
	private String packageBodyHTML = "";

	public static void main(String[] args) throws Exception {
		TestPackageHTMLGenerator gen = new TestPackageHTMLGenerator();
		TestPlan tp = gen.loadResouceBundle(gen.rbPath, null);
		gen.generateTestPackage(tp);
		gen.genTOCoverPagePDF(gen.rbPath + "/TestPackage.html" , gen.rbPath  + "/TestPackage.pdf", gen.rbPath + "/CoverPage.html" );
	}

	
	public TestPlan loadResouceBundle(String path, Object parent) throws IOException {
		Object child = this.findTypeFolder(path);
		
		if(child instanceof TestPlan){
			File dir = new File(path);
			File[] fileList = dir.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				File file = fileList[i];
				if (file.isDirectory()) {
					loadResouceBundle(file.getCanonicalPath().toString(), child);
				}
			}
			return (TestPlan)child;
		}else if(child instanceof TestCaseGroup){
			if(parent instanceof TestPlan){
				TestPlan tp = (TestPlan)parent;
				tp.addChild((TestCaseGroup)child);
				File dir = new File(path);
				File[] fileList = dir.listFiles();
				for (int i = 0; i < fileList.length; i++) {
					File file = fileList[i];
					if (file.isDirectory()) {
						loadResouceBundle(file.getCanonicalPath().toString(), child);
					}
				}
			}else {
				TestCaseGroup tcg = (TestCaseGroup)parent;
				tcg.addChild((TestCaseGroup)child);
				File dir = new File(path);
				File[] fileList = dir.listFiles();
				for (int i = 0; i < fileList.length; i++) {
					File file = fileList[i];
					if (file.isDirectory()) {
						loadResouceBundle(file.getCanonicalPath().toString(), child);
					}
				}
			}
			
			
		}else if(child instanceof TestCase){
			TestCase tc = (TestCase)child;
			TestCaseGroup tcg = (TestCaseGroup)parent;
			tcg.addChild(tc);
			
			File fileTestStory = new File(path + "/TestStory.html");
			if(fileTestStory.exists()) {
				String stringTestStory = FileUtils.readFileToString(fileTestStory);
				stringTestStory = stringTestStory.substring(stringTestStory.lastIndexOf("<body>") + 6, stringTestStory.lastIndexOf("</body>"));
				tc.setTestStory(stringTestStory);
			}
			
			File dir = new File(path);
			File[] fileList = dir.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				File file = fileList[i];
				if (file.isDirectory()) {
					loadResouceBundle(file.getCanonicalPath().toString(), tc);
				}
			}
			
		}else if(child instanceof TestStep){
			TestStep ts = (TestStep)child;
			TestCase tc = (TestCase)parent;
			tc.addChild(ts);
			
			File fileTestStory = new File(path + "/TestStory.html");
			if(fileTestStory.exists()) {
				String stringTestStory = FileUtils.readFileToString(fileTestStory);
				stringTestStory = stringTestStory.substring(stringTestStory.lastIndexOf("<body>") + 6, stringTestStory.lastIndexOf("</body>"));
				ts.setTestStory(stringTestStory);
			}
			
			File fileMessage = new File(path + "/Message.txt");
			if(fileMessage.exists()) {
				String stringMessage = FileUtils.readFileToString(fileMessage);
				ts.setMessage(stringMessage);
			}
			
			File fileMessageXML = new File(path + "/Message.xml");
			if(fileMessageXML.exists()) {
				String stringMessageXML = FileUtils.readFileToString(fileMessageXML);
				ts.setMessageXML(stringMessageXML);
			}
			
			File fileTestDataSpecification = new File(path + "/TestDataSpecification.html");
			if(fileTestDataSpecification.exists()) {
				String stringTestDataSpecification = FileUtils.readFileToString(fileTestDataSpecification);
				stringTestDataSpecification = stringTestDataSpecification.substring(stringTestDataSpecification.lastIndexOf("</style>") + 8);
				ts.setTestDataSpecification(stringTestDataSpecification);
			}
			
			File fileMessageContent = new File(path + "/MessageContent.html");
			if(fileMessageContent.exists()) {
				String stringMessageContent = FileUtils.readFileToString(fileMessageContent);
				stringMessageContent = stringMessageContent.substring(stringMessageContent.lastIndexOf("</style>") + 8);
				ts.setMessageContent(stringMessageContent);
			}
			
			File fileJurorDocument = new File(path + "/JurorDocument.html");
			if(fileJurorDocument.exists()) {
				String stringJurorDocument = FileUtils.readFileToString(fileJurorDocument);
				stringJurorDocument = stringJurorDocument.substring(stringJurorDocument.lastIndexOf("<body>") + 6, stringJurorDocument.lastIndexOf("</body>"));
				ts.setJurorDocument(stringJurorDocument);
			}
		}
		
		
		return null;
	}
	
	
	private Object findTypeFolder(String path) throws IOException{
		File dir = new File(path);
		File[] fileList = dir.listFiles();
		
		for (int i = 0; i < fileList.length; i++) {
			File file = fileList[i];
			if (file.isFile()) {
				if(file.getName().equals("TestPlan.json")){
					String jsonString = FileUtils.readFileToString(file);
					Gson gson = new Gson();
					TestPlan tp = gson.fromJson(jsonString,TestPlan.class);
					
					return tp;
				}else if(file.getName().equals("TestCaseGroup.json")){
					String jsonString = FileUtils.readFileToString(file);
					Gson gson = new Gson();
					TestCaseGroup tcg = gson.fromJson(jsonString,TestCaseGroup.class);
					
					return tcg;
				}else if(file.getName().equals("TestCase.json")){
					String jsonString = FileUtils.readFileToString(file);
					Gson gson = new Gson();
//					System.out.println(file.getAbsolutePath());
					
					TestCase tc = gson.fromJson(jsonString,TestCase.class);
					
					return tc;
				}else if(file.getName().equals("TestStep.json")){
					String jsonString = FileUtils.readFileToString(file);
					Gson gson = new Gson();
					TestStep ts = gson.fromJson(jsonString,TestStep.class);

					return ts;
				}
			}
		}
		
		return "";
	}
	
	
	private void generateTestGroup(TestCaseGroup group, String path){
		packageBodyHTML = packageBodyHTML + "<A NAME=\"" + path + "\">" + "<h2>" + path + ". " + group.getName() + "</h2>" + System.getProperty("line.separator");
		packageBodyHTML = packageBodyHTML + "<span>" + group.getDescription()+ "</span>" + System.getProperty("line.separator");
		packageBodyHTML = packageBodyHTML + "<p style=\"page-break-after:always;\"></p>";
		
		HashMap<Integer, Object>  childMap = new HashMap<Integer, Object>();
		for(Object o:group.getChildren()){
			if(o instanceof TestCaseGroup){
				TestCaseGroup childGroup = (TestCaseGroup)o;
				childMap.put(childGroup.getPosition(), childGroup);
			}else if(o instanceof TestCase){
				TestCase childCase = (TestCase)o;
				childMap.put(childCase.getPosition(), childCase);
			}
		}
		
		for(int i=0; i< childMap.keySet().size(); i++){
			Object o = childMap.get(i+1);
			if(o instanceof TestCaseGroup){
				this.generateTestGroup((TestCaseGroup)o, path + "." + (i+1));
			}else if(o instanceof TestCase){
				this.generateTestCase((TestCase)o, path + "." + (i+1));
			}
			
		}
		
		
	}
	
	private void generateTestCase(TestCase testcase, String path) {
		packageBodyHTML = packageBodyHTML + "<A NAME=\"" + path + "\">" + "<h2>" + path + ". " + testcase.getName() + "</h2>" + System.getProperty("line.separator");
		packageBodyHTML = packageBodyHTML + "<span>" + testcase.getDescription() + "</span>" + System.getProperty("line.separator");
		packageBodyHTML = packageBodyHTML + "<h3>" + "Test Story" + "</h3>" + System.getProperty("line.separator");
		packageBodyHTML = packageBodyHTML + testcase.getTestStory();
		packageBodyHTML = packageBodyHTML + "<p style=\"page-break-after:always;\"></p>";
		
		HashMap<Integer, TestStep>  testStepMap = new HashMap<Integer, TestStep>();
		for(TestStep ts:testcase.getChildren()){
			testStepMap.put(ts.getPosition(), ts);
		}
		
		for(int i=0; i < testStepMap.keySet().size(); i++){
			TestStep ts = testStepMap.get(i+1);
			this.generateTestStep(ts, path + "." + (i+1));
		}
		
	}


	private void generateTestStep(TestStep teststep, String path) {
		packageBodyHTML = packageBodyHTML + "<A NAME=\"" + path + "\">" + "<h2>" + path + ". " + teststep.getName() + "</h2>" + System.getProperty("line.separator");
		packageBodyHTML = packageBodyHTML + "<span>" + teststep.getDescription() + "</span>" + System.getProperty("line.separator");
		packageBodyHTML = packageBodyHTML + "<h3>" + "Test Story" + "</h3>" + System.getProperty("line.separator");
		packageBodyHTML = packageBodyHTML + teststep.getTestStory() + System.getProperty("line.separator"); 
		
		if(teststep.getMessageContent() != null && !teststep.getMessageContent().equals("")){
			packageBodyHTML = packageBodyHTML + "<h3>" + "Message Contents" + "</h3>" + System.getProperty("line.separator");
			packageBodyHTML = packageBodyHTML + teststep.getMessageContent() + System.getProperty("line.separator");
		}
		
		if(teststep.getTestDataSpecification() != null && !teststep.getTestDataSpecification().equals("")){
			packageBodyHTML = packageBodyHTML + "<h3>" + "TestData Specification" + "</h3>" + System.getProperty("line.separator");
			packageBodyHTML = packageBodyHTML + teststep.getTestDataSpecification() + System.getProperty("line.separator");
		}
		
		if(teststep.getJurorDocument() != null && !teststep.getJurorDocument().equals("")){
			packageBodyHTML = packageBodyHTML + "<h3>" + "Juror Document" + "</h3>" + System.getProperty("line.separator");
			packageBodyHTML = packageBodyHTML + teststep.getJurorDocument() + System.getProperty("line.separator");
		}
	}


	private void generateTestPackage(TestPlan tp) throws Exception {
		packageBodyHTML = packageBodyHTML + "<h1>" + tp.getName() + "</h1>" + System.getProperty("line.separator");
		packageBodyHTML = packageBodyHTML + tp.getDescription() + System.getProperty("line.separator");
		packageBodyHTML = packageBodyHTML + "<p style=\"page-break-after:always;\"></p>";
		
		HashMap<Integer, TestCaseGroup>  testPlanMap = new HashMap<Integer, TestCaseGroup>();
		for(TestCaseGroup tcg:tp.getChildren()){
			testPlanMap.put(tcg.getPosition(), tcg);
		}
		
		for(int i=0; i< testPlanMap.keySet().size(); i++){
			this.generateTestGroup(testPlanMap.get(i+1), ""+(i+1));
			
		}
			
		File fileTestPackageTemplate= new File("/Users/jungyubw/Documents/Works/Projects/tcamt/tcamt-web/src/main/resources/TestPackage.html");
		File fileCoverPageTemplate= new File("/Users/jungyubw/Documents/Works/Projects/tcamt/tcamt-web/src/main/resources/CoverPage.html");
		
		String stringTestPackage = FileUtils.readFileToString(fileTestPackageTemplate);
		String stringCoverPage = FileUtils.readFileToString(fileCoverPageTemplate);
				
		stringTestPackage = stringTestPackage.replace("?bodyContent?", packageBodyHTML);
		stringCoverPage = stringCoverPage.replace("?title?", "ONC Certification Test Plan");
		stringCoverPage = stringCoverPage.replace("?subtitle?", "NIST HealthCare Test");
		stringCoverPage = stringCoverPage.replace("?version?", "1.0.0");
		stringCoverPage = stringCoverPage.replace("?date?", "March 10nd, 2016");
		
		
		File testPackagefile = new File(this.rbPath + "/TestPackage.html");
		FileWriter fileWriter = new FileWriter(testPackagefile);
		fileWriter.write(stringTestPackage);
		fileWriter.flush();
		fileWriter.close();
		
		File coverPagefile = new File(this.rbPath + "/CoverPage.html");
		FileWriter fileWriter2 = new FileWriter(coverPagefile);
		fileWriter2.write(stringCoverPage);
		fileWriter2.flush();
		fileWriter2.close();

	}
	
	private void genTOCoverPagePDF(String htmlFileName, String pdfFileName, String coverpageFileName) throws IOException{
		ProcessBuilder pb = new ProcessBuilder("/usr/local/bin/wkhtmltopdf" , "cover", coverpageFileName, "toc" , htmlFileName , pdfFileName);
        pb.redirectErrorStream(true);
        Process process = pb.start();
        BufferedReader inStreamReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
       
        String line = inStreamReader.readLine();
       
        while(line != null)
        {
        	System.out.println(line);
            line = inStreamReader.readLine();
        }
	}
}
