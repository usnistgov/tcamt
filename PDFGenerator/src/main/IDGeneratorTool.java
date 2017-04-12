package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class IDGeneratorTool {

	public static void main(String[] args) {
		IDGeneratorTool gen = new IDGeneratorTool();
		gen.subDirList("/Users/jungyubw/Downloads/CDC Immunization Related Requirements Test Plan (CNI) v5.2");
	}

	public void subDirList(String source) {
		long range = 1234567L;
		Random r = new Random();
		File dir = new File(source);
		File[] fileList = dir.listFiles();
		try {
			for (int i = 0; i < fileList.length; i++) {
				File file = fileList[i];
				if (file.isFile()) {
					if(file.getName().equals("TestPlan.json") || file.getName().equals("TestCaseGroup.json") || file.getName().equals("TestCase.json") || file.getName().equals("TestStep.json") || file.getName().equals("TestObject.json")){
						FileInputStream fisTargetFile = new FileInputStream(file);
						String targetFileStr = IOUtils.toString(fisTargetFile, "UTF-8");
						String[] lines = targetFileStr.split(System.getProperty("line.separator"));
						String newStr = "";
						long number = (long)(r.nextDouble()*range);
						newStr = "{" + System.getProperty("line.separator") + "  \"id\" : " +  number + "," + System.getProperty("line.separator");
						
						for(int k=1; k<lines.length; k++){
							newStr = newStr + lines[k] + System.getProperty("line.separator");
						}
						FileUtils.writeStringToFile(file, newStr);
						
					}
				} else if (file.isDirectory()) {
					subDirList(file.getCanonicalPath().toString());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
