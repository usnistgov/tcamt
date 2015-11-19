package gov.nist.healthcare.tcamt.domain;

import java.io.Serializable;

import gov.nist.healthcare.tcamt.domain.data.TestDataCategorization;

public class InstanceTestDataType  implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1267318785285693607L;
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
