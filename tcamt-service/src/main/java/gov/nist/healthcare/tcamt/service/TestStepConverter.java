package gov.nist.healthcare.tcamt.service;

import gov.nist.healthcare.hl7tools.v2.maker.core.ConversionException;
import gov.nist.healthcare.tcamt.domain.TestStep;

public interface TestStepConverter {

	/**
	 * 
	 * @param ig
	 * @return
	 * @throws ConversionException
	 */
	public String toString(TestStep ts) throws ConversionException;
	
	/**
	 * 
	 * @param ig
	 * @return
	 * @throws ConversionException
	 */
	public TestStep fromString(String ts) throws ConversionException;
}
