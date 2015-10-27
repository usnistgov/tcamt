package gov.nist.healthcare.tcamt.domain.data;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ComponentModel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5288642864545218785L;
	private String messageName;
	private String path;
	private String ipath;
	private String iPositionPath;
	private String usageList;
	private Component node;
	private String data;
	private Datatype datatype;
	private Table table;
	private TestDataCategorization tdc;
	private boolean isLeafNode;
	private Predicate predicate;
	private List<ConformanceStatement> conformanceStatements;
	private String usage;
	private List<String> listValues = new ArrayList<String>();
	private String newValue;
	
	
	public ComponentModel(String messageName, String path, String ipath, String iPositionPath, String usageList, Component node, String data,
			TestDataCategorization tdc, boolean isLeafNode, Datatype datatype, Table table, Predicate predicate, List<ConformanceStatement> conformanceStatements, String usage, List<String> listValues) {
		super();
		this.messageName = messageName;
		this.path = path;
		this.ipath = ipath;
		this.iPositionPath = iPositionPath;
		this.setUsageList(usageList);
		this.setNode(node);
		this.data = data;
		this.tdc = tdc;
		this.isLeafNode = isLeafNode;
		this.table = table;
		this.datatype = datatype;
		this.predicate = predicate;
		this.conformanceStatements = conformanceStatements;
		this.usage = usage;
		this.listValues = listValues;
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


	public Component getNode() {
		return node;
	}


	public void setNode(Component node) {
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
		if(this.data == null || this.data.equals("")) return false;
		
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


	public Datatype getDatatype() {
		return datatype;
	}


	public void setDatatype(Datatype datatype) {
		this.datatype = datatype;
	}


	public Table getTable() {
		return table;
	}


	public void setTable(Table table) {
		this.table = table;
	}


	public Predicate getPredicate() {
		return predicate;
	}


	public void setPredicate(Predicate predicate) {
		this.predicate = predicate;
	}


	public String getUsage() {
		return usage;
	}


	public void setUsage(String usage) {
		this.usage = usage;
	}


	public List<ConformanceStatement> getConformanceStatements() {
		return conformanceStatements;
	}


	public void setConformanceStatements(List<ConformanceStatement> conformanceStatements) {
		this.conformanceStatements = conformanceStatements;
	}


	public List<String> getListValues() {
		return listValues;
	}


	public void setListValues(List<String> listValues) {
		this.listValues = listValues;
	}


	public String getNewValue() {
		return newValue;
	}


	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}
	
	public void addValueList(){
		if(this.newValue != null && !this.newValue.equals("")) this.listValues.add(this.newValue);
		this.newValue = "";
	}
	
	public void delValue(String v){
		this.listValues.remove(v);
	}
	
}
