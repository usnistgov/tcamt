package gov.nist.healthcare.tcamt.domain.data;

import java.io.Serializable;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;

public class InstanceSegment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 632441462717290129L;
	private String ipath;
	private String path;
	private String messageName;
	private String iPositionPath;
	private String usageList;
	
	private String lineStr;
	private boolean isAnchor;
	private SegmentRef segmentRef;

	public InstanceSegment(String ipath, String path, String messageName, String iPositionPath, String lineStr, boolean isAnchor ,SegmentRef segmentRef, String usageList) {
		this.ipath = ipath;
		this.path = path;
		this.messageName = messageName;
		this.iPositionPath = iPositionPath;
		this.lineStr = lineStr;
		this.setAnchor(isAnchor);
		this.segmentRef = segmentRef;
		this.usageList = usageList;
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

	public String getUsageList() {
		return usageList;
	}

	public void setUsageList(String usageList) {
		this.usageList = usageList;
	}

	public String getMessageName() {
		return messageName;
	}

	public void setMessageName(String messageName) {
		this.messageName = messageName;
	}

	public String getiPositionPath() {
		return iPositionPath;
	}

	public void setiPositionPath(String iPositionPath) {
		this.iPositionPath = iPositionPath;
	}
	
	
}
