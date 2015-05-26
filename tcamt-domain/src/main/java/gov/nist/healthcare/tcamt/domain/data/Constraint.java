package gov.nist.healthcare.tcamt.domain.data;

public class Constraint {
	private String id;
	private String type;
	private String description;
	private String ipath;
	private String iPositionPath;
	private String data;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getIpath() {
		return ipath;
	}
	public void setIpath(String ipath) {
		this.ipath = ipath;
	}
	public String getiPositionPath() {
		return iPositionPath;
	}
	public void setiPositionPath(String iPositionPath) {
		this.iPositionPath = iPositionPath;
	}
	
	
}
