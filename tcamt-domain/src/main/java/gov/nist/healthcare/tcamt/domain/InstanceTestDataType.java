package gov.nist.healthcare.tcamt.domain;

import gov.nist.healthcare.tcamt.domain.data.TestDataCategorization;

public class InstanceTestDataType  implements Cloneable {

	private String iPath;
	private TestDataCategorization type;
	
	public InstanceTestDataType(String iPath, TestDataCategorization type) {
		super();
		this.iPath = iPath;
		this.type = type;
	}
	
	

	public InstanceTestDataType() {
		super();
	}



	public String getiPath() {
		return iPath;
	}

	public void setiPath(String iPath) {
		this.iPath = iPath;
	}

	public TestDataCategorization getType() {
		return type;
	}

	public void setType(TestDataCategorization type) {
		this.type = type;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		InstanceTestDataType cloned = (InstanceTestDataType)super.clone();		
		return cloned;
	}
}
