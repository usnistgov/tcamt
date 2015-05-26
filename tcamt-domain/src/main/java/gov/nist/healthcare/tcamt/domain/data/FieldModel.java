package gov.nist.healthcare.tcamt.domain.data;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;

import java.io.Serializable;

public class FieldModel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5288642864545218785L;
	private String messageName;
	private String path;
	private String ipath;
	private String iPositionPath;
	private String usageList;
	private Field node;
	private String data;
	private TestDataCategorization tdc;
	private boolean isLeafNode;
	
	
	
	public FieldModel(String messageName, String path, String ipath, String iPositionPath, String usageList, Field node, String data,
			TestDataCategorization tdc, boolean isLeafNode) {
		super();
		this.messageName = messageName;
		this.path = path;
		this.ipath = ipath;
		this.iPositionPath = iPositionPath;
		this.usageList = usageList;
		this.setNode(node);
		this.data = data;
		this.tdc = tdc;
		this.isLeafNode = isLeafNode;
	}
	
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getIpath() {
		return ipath;
	}
	public void setIpath(String ipath) {
		this.ipath = ipath;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public TestDataCategorization getTdc() {
		return tdc;
	}
	public void setTdc(TestDataCategorization tdc) {
		this.tdc = tdc;
	}


	public boolean isLeafNode() {
		return isLeafNode;
	}


	public void setLeafNode(boolean isLeafNode) {
		this.isLeafNode = isLeafNode;
	}


	public Field getNode() {
		return node;
	}


	public void setNode(Field node) {
		this.node = node;
	}


	public String getUsageList() {
		return usageList;
	}


	public void setUsageList(String usageList) {
		this.usageList = usageList;
	} 
	
	public boolean checkTestDataCategorizationAvaiablility(){
		String usage[] = this.usageList.split("-");
		for(String u:usage){
			if(!u.equals("R") && !u.equals("RE") && !u.equals("C")){
				return false;
			}
		}
		return true;
	}


	public String getiPositionPath() {
		return iPositionPath;
	}


	public void setiPositionPath(String iPositionPath) {
		this.iPositionPath = iPositionPath;
	}


	public String getMessageName() {
		return messageName;
	}


	public void setMessageName(String messageName) {
		this.messageName = messageName;
	}
	
	
}
