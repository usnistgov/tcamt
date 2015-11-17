package gov.nist.healthcare.tcamt.domain.data;

import gov.nist.healthcare.hl7tools.domain.Field;

public class SegmentTreeModel extends MessageTreeModel implements Cloneable{

	private String data;
	private TestDataCategorization tdc;
	private String ca;
	private boolean isLeafNode;
	private String iPath;
	
	public SegmentTreeModel(String messageId, String name, Object node, String iPath, String path, String data, TestDataCategorization tdc, String ca, boolean isLeafNode, int occur) {
		super(messageId, name, node, path, 1);
		this.iPath = iPath;
		this.data = data;
		this.tdc = tdc;
		this.ca = ca;
		this.isLeafNode = isLeafNode;
		this.occurrence = occur;
		
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getCa() {
		return ca;
	}

	public void setCa(String ca) {
		this.ca = ca;
	}
	
	public boolean isLeafNode() {
		return isLeafNode;
	}

	public void setLeafNode(boolean isLeafNode) {
		this.isLeafNode = isLeafNode;
	}
	
	public boolean isFirstField(){
		boolean result = true;
		
		if(!(this.getNode() instanceof Field)){
			result = false;
		}else{
			if(!this.getiPath().substring(this.getiPath().lastIndexOf("[") + 1 , this.getiPath().lastIndexOf("]")).equals("1")){
				result = false;
			}
		}
		
		
		return result;
	}

	public String getiPath() {
		return iPath;
	}

	public void setiPath(String iPath) {
		this.iPath = iPath;
	}
	
	
	
	public TestDataCategorization getTdc() {
		return tdc;
	}

	public void setTdc(TestDataCategorization tdc) {
		this.tdc = tdc;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
