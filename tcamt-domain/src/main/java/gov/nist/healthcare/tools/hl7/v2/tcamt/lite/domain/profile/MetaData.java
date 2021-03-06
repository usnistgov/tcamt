package gov.nist.healthcare.tools.hl7.v2.tcamt.lite.domain.profile;


public class MetaData implements java.io.Serializable, Cloneable {

	private static final long serialVersionUID = 1L;
	
	public MetaData() {
		super();
	}

	/* XSD Attributes */
	
	private String name; 						//ConformanceProfile/MetaData/@Name
	
	private String orgName = ""; 				//ConformanceProfile/MetaData/@OrgName
	
	private String version = ""; 				//ConformanceProfile/MetaData/@Version
	
	private String date = ""; 					//ConformanceProfile/MetaData/@Date
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
	public MetaData clone() throws CloneNotSupportedException {
		MetaData clonedProfileMetaData = new MetaData();

		clonedProfileMetaData.setName(name);
		clonedProfileMetaData.setOrgName(orgName);
		clonedProfileMetaData.setDate(date);
		clonedProfileMetaData.setVersion(version);
		return clonedProfileMetaData;
	}
}
