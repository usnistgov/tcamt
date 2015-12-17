package gov.nist.healthcare.tcamt.domain.data;

public class TDCGlobalSettingData {

	private String path;
	private TestDataCategorization tdc;
	private long codeListId;
	
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public TestDataCategorization getTdc() {
		return tdc;
	}
	public void setTdc(TestDataCategorization tdc) {
		this.tdc = tdc;
	}
	public long getCodeListId() {
		return codeListId;
	}
	public void setCodeListId(long codeListId) {
		this.codeListId = codeListId;
	}
	@Override
	public String toString() {
		return "TDCGlobalSettingData [path=" + path + ", tdc=" + tdc + ", codeListId=" + codeListId + "]";
	}
}
