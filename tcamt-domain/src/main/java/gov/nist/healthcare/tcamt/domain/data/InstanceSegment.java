package gov.nist.healthcare.tcamt.domain.data;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;

import java.io.Serializable;

public class InstanceSegment implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3923342579339897927L;
	private String ipath;
	private String path;
	private String lineStr;
	private boolean isAnchor;
	private SegmentRef segmentRef; 

	public InstanceSegment(String ipath, String path, String lineStr, boolean isAnchor ,SegmentRef segmentRef) {
		this.ipath = ipath;
		this.path = path;
		this.lineStr = lineStr;
		this.setAnchor(isAnchor);
		this.segmentRef = segmentRef;
	}
	
	public InstanceSegment(){
		super();
	}

	public String getIpath() {
		return ipath;
	}

	public void setIpath(String ipath) {
		this.ipath = ipath;
	}

	public SegmentRef getSegmentRef() {
		return segmentRef;
	}

	public void setSegmentRef(SegmentRef segmentRef) {
		this.segmentRef = segmentRef;
	}

	public boolean isAnchor() {
		return isAnchor;
	}

	public void setAnchor(boolean isAnchor) {
		this.isAnchor = isAnchor;
	}

	public String getLineStr() {
		return lineStr;
	}

	public void setLineStr(String lineStr) {
		this.lineStr = lineStr;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	

}
