package gov.nist.healthcare.hl7tools.service.util.valueset;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class PDFGenerator {

	public static void main(String[] args) {
		PDFGenerator gen = new PDFGenerator();
//		gen.subDirList("/Users/jungyubw/Documents/Works/Projects/hit-iz-tool/hit-iz-resource/src/main/resources/Isolated");
//		gen.subDirList("/Users/jungyubw/Documents/Works/Projects/hit-iz-tool/hit-iz-resource/src/main/resources/Contextbased");
		
		gen.subDirList("/Users/jungyubw/Downloads/TESTPLAN");
		
		
	}

	public void subDirList(String source) {
		File dir = new File(source);
		File[] fileList = dir.listFiles();
		try {
			for (int i = 0; i < fileList.length; i++) {
				File file = fileList[i];
				if (file.isFile()) {
					if(file.getName().endsWith(".html")){
						
						if(file.getName().equals("TestStory.html")){
							
						}else if(file.getName().equals("TestStoryPDF.html")){
							String htmlFileName = file.getAbsolutePath();
							String pdfFileName = file.getAbsolutePath().replace("PDF.html", ".pdf");
							genPDF(htmlFileName, pdfFileName);
							file.delete();
						}else if(file.getName().equals("MessageContentPDF.html")){
							String htmlFileName = file.getAbsolutePath();
							String pdfFileName = file.getAbsolutePath().replace("PDF.html", ".pdf");
							genPDF(htmlFileName, pdfFileName);
							file.delete();
						}else if(file.getName().equals("TestDataSpecificationPDF.html")){
							String htmlFileName = file.getAbsolutePath();
							String pdfFileName = file.getAbsolutePath().replace("PDF.html", ".pdf");
							genPDF(htmlFileName, pdfFileName);
							file.delete();
						}else if(file.getName().equals("TestPackage.html")){
							String htmlFileName = file.getAbsolutePath();
							String pdfFileName = file.getAbsolutePath().replace(".html", ".pdf");
							genTOCPDF(htmlFileName, pdfFileName);
						}else{
							String htmlFileName = file.getAbsolutePath();
							String pdfFileName = file.getAbsolutePath().replace(".html", ".pdf");
							genPDF(htmlFileName, pdfFileName);
						}
						
					}
				} else if (file.isDirectory()) {
					subDirList(file.getCanonicalPath().toString());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void genPDF(String htmlFileName, String pdfFileName) throws IOException{
		ProcessBuilder pb = new ProcessBuilder("/usr/local/bin/wkhtmltopdf" , htmlFileName , pdfFileName);
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
	
	private void genTOCPDF(String htmlFileName, String pdfFileName) throws IOException{
		ProcessBuilder pb = new ProcessBuilder("/usr/local/bin/wkhtmltopdf" , "toc" , htmlFileName , pdfFileName);
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
