package gov.nist.healthcare.tcamt.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Java_pdf {

   public static void main(String[] args) throws IOException {
        try
        {
            String htmlFilePath = "TestDataSpecification.html";
            String pdfFilePath = "out.pdf";
            ProcessBuilder pb = new ProcessBuilder("/usr/local/bin/wkhtmltopdf" , htmlFilePath , pdfFilePath);
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
        catch(Exception e)
        {
            System.out.println("coming-->"+e.getMessage() );
        }
    }

}
