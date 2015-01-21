package gov.nist.healthcare.tcamt.domain;

import java.io.Serializable;

import gov.nist.healthcare.hl7tools.domain.StatementDetails;

public class ValidationContext implements Cloneable, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3926211483462954685L;
	private String id;
	private String path;
	private String description;
	private String level;
	private String script;
	private StatementDetails statementDetails;
	
	
	
	public ValidationContext(String id, String path, String description,
			String level, String script, StatementDetails statementDetails) {
		super();
		this.id = id;
		this.path = path;
		this.description = description;
		this.level = level;
		this.script = script;
		this.statementDetails = statementDetails;
	}
	
	public ValidationContext(){
		super();
	}
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getScript() {
		return script;
	}
	public void setScript(String script) {
		this.script = script;
	}
	public StatementDetails getStatementDetails() {
		return statementDetails;
	}
	public void setStatementDetails(StatementDetails statementDetails) {
		this.statementDetails = statementDetails;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		ValidationContext cloned = (ValidationContext)super.clone();		
		return cloned;
	}
	
	
}
