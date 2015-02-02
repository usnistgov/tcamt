/*
 * NIST IGAMT
 * UploadException.java Apr 24, 2013
 * 
 * This code was produced by the National Institute of Standards and
 * Technology (NIST). See the "nist.disclaimer" file given in the distribution
 * for information on the use and redistribution of this software.
 */
package gov.nist.healthcare.tcamt.web;

/**
 * @author Harold Affo
 * 
 */
public class UploadException extends Exception {

	private static final long serialVersionUID = 1134812461230576220L;

	public UploadException(String error) {
		super(error);
	}

}