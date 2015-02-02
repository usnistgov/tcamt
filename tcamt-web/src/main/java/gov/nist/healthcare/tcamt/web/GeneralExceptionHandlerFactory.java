/*
 * NIST Lab Result Interface (LRI) Constants.java 03/20/2013 This code was produced by the National
 * Institute of Standards and Technology (NIST). See the "nist.disclaimer" file given in the
 * distribution for information on the use and redistribution of this software.
 */
package gov.nist.healthcare.tcamt.web;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

/**
 * 
 * @author Harold Affo (NIST)
 * 
 */
public class GeneralExceptionHandlerFactory extends ExceptionHandlerFactory {

	/**
	 *  
	 */
	private final ExceptionHandlerFactory	parent;

	/**
	 * 
	 * @param parent
	 */
	public GeneralExceptionHandlerFactory(ExceptionHandlerFactory parent) {

		this.parent = parent;
	}

	@Override
	/**
	 * 
	 */
	public ExceptionHandler getExceptionHandler() {

		return new GeneralExceptionHandler(this.parent.getExceptionHandler());
	}
}
