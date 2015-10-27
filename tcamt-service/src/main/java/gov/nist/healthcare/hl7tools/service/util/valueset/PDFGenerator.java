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
		
		gen.subDirList("/Users/jungyubw/Downloads/ONC 2015 Test Plan");
		
		
	}

	public void subDirList(String source) {
		File dir = new File(source);
		File[] fileList = dir.listFiles();
		try {
			for (int i = 0; i < fileList.length; i++) {
				File file = fileList[i];
				if (file.isFile()) {
					if(file.getName().endsWith(".html")){
						
						String htmlFileName = file.getAbsolutePath();
						String pdfFileName = file.getAbsolutePath().replace(".html", ".pdf");
						genPDF(htmlFileName, pdfFileName);
					}
				} else if (file.isDirectory()) {
					subDirList(file.getCanonicalPath().toString());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void genPDF(String htmlFileName, String pdfFileName) throws IOException{
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
}
