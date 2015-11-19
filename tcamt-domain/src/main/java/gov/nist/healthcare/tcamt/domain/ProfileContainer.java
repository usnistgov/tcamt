package gov.nist.healthcare.tcamt.domain;

import java.io.Serializable;

public class ProfileContainer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4140673604136313536L;
	private String messageId;
	private String constraintId;
	private String valueSetLibraryId;
	
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public String getConstraintId() {
		return constraintId;
	}
	public void setConstraintId(String constraintId) {
		this.constraintId = constraintId;
	}
	public String getValueSetLibraryId() {
		return valueSetLibraryId;
	}
	public void setValueSetLibraryId(String valueSetLibraryId) {
		this.valueSetLibraryId = valueSetLibraryId;
	}
	
	
}
