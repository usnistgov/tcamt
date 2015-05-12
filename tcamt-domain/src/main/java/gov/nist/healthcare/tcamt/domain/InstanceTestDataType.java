package gov.nist.healthcare.tcamt.domain;

import gov.nist.healthcare.tcamt.domain.data.TestDataCategorization;

import java.io.Serializable;

public class InstanceTestDataType  implements Cloneable, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3251547205552538246L;
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
