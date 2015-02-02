/**
 * 
 */
package gov.nist.healthcare.tcamt.web;

/**
 * @author haffo
 * 
 */
public class ValidationException extends Exception {

	private static final long serialVersionUID = 8794081620377180040L;

	public ValidationException(String error) {
		super(error);
	}

}
