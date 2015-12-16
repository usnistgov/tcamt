package gov.nist.healthcare.tcamt.domain.data;

import gov.nist.healthcare.tcamt.domain.TestCaseCodeList;

public class TDCGlobalSettingData {

	private String path;
	private TestDataCategorization tdc;
	private TestCaseCodeList codeList;
	
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
	public TestCaseCodeList getCodeList() {
		return codeList;
	}
	public void setCodeList(TestCaseCodeList codeList) {
		this.codeList = codeList;
	}
	@Override
	public String toString() {
		return "TDCGlobalSettingData [path=" + path + ", tdc=" + tdc
				+ ", codeList=" + codeList + "]";
	}
	
	
}
