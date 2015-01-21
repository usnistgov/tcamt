package gov.nist.healthcare.tcamt.domain;

import java.io.Serializable;



public class ProfilePathOccurIGData implements Cloneable, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4225517713853089642L;
	private String path;
	private int occur;
	private String igData;
	
	public ProfilePathOccurIGData(String path, int occur, String igData) {
		super();
		this.path = path;
		this.occur = occur;
		this.igData = igData;
	}
	
	public ProfilePathOccurIGData(){
		super();
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}

	public int getOccur() {
		return occur;
	}

	public void setOccur(int occur) {
		this.occur = occur;
	}

	public String getIgData() {
		return igData;
	}

	public void setIgData(String igData) {
		this.igData = igData;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		ProfilePathOccurIGData cloned = (ProfilePathOccurIGData)super.clone();		
		return cloned;
	}
}
