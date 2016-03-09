package main;

import org.apache.commons.lang3.StringUtils;


public class TestUtil {

	public static void main(String[] args) {
		System.out.println(modifyFormIPath("ORDER[12].OBSERVATION[2].OBX[2].5[1].2[1]"));

	}
	
	
	private static String modifyFormIPath(String iPath){
		String result = "";
		
		String[] pathList = iPath.split("\\.");

		String currentType = "GroupOrSegment";
		String previousType = "GroupOrSegment";
		
		for(String p:pathList){
			String path = p.substring(0,p.indexOf("["));
			int instanceNum =Integer.parseInt(p.substring(p.indexOf("[") + 1 , p.indexOf("]")));
			
			if(StringUtils.isNumeric(path)){
				currentType = "FieldOrComponent";
			}else {
				currentType = "GroupOrSegment";
			}
			
			if(instanceNum == 1){
				if(currentType.equals("FieldOrComponent") && previousType.equals("GroupOrSegment")){
					result = result + "-" + path;
				}else{
					result = result + "." + path;
				}
			}else {
				if(currentType.equals("FieldOrComponent") && previousType.equals("GroupOrSegment")){
					result = result + "-" + path + "[" + instanceNum + "]";
				}else{
					result = result + "." + path + "[" + instanceNum + "]";
				}
			}
			
			previousType = currentType;
		}
		return result.substring(1);
	}
}
