package gov.nist.healthcare.tcamt.domain.data;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;

import java.io.Serializable;

public class ComponentModel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5288642864545218785L;
	private String path;
	private String ipath;
	private Component node;
	private String data;
	private TestDataCategorization tdc;
	private boolean isLeafNode;
	
	
	
	public ComponentModel(String path, String ipath, Component node, String data,
			TestDataCategorization tdc, boolean isLeafNode) {
		super();
		this.path = path;
		this.ipath = ipath;
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


	public Component getNode() {
		return node;
	}


	public void setNode(Component node) {
		this.node = node;
	}


	
	
	
	
}
