package gov.nist.healthcare.tcamt.domain.data;

public class TestDataFromCS {
	private String path;
	private String data;
	private TestDataCategorization tdc;
	
	public TestDataFromCS() {
		super();
	}
	
	public TestDataFromCS(String path, String data, TestDataCategorization tdc) {
		super();
		this.path = path;
		this.data = data;
		this.tdc = tdc;
	}
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
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
	
	
}
