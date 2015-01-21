package gov.nist.healthcare.tcamt.domain;

import java.io.Serializable;

import org.primefaces.model.TreeNode;

public class InstanceSegment implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3923342579339897927L;
	private String path;
	private TreeNode segmentTreeNode;

	public InstanceSegment(String path, TreeNode segmentTreeNode) {
		this.path = path;
		this.segmentTreeNode = segmentTreeNode;
	}
	
	public InstanceSegment(){
		super();
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public TreeNode getSegmentTreeNode() {
		return segmentTreeNode;
	}

	public void setSegmentTreeNode(TreeNode segmentTreeNode) {
		this.segmentTreeNode = segmentTreeNode;
	}

}
