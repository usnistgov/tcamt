package gov.nist.healthcare.tcamt.service.converter;

import gov.nist.healthcare.hl7tools.v2.maker.core.ConversionException;
import gov.nist.healthcare.tcamt.domain.DataInstanceTestStep;

public interface DataInstanceTestStepConverter {

	/**
	 * 
	 * @param ig
	 * @return
	 * @throws ConversionException
	 */
	public String toString(DataInstanceTestStep ts) throws ConversionException;
	
	/**
	 * 
	 * @param ig
	 * @return
	 * @throws ConversionException
	 */
	public DataInstanceTestStep fromString(String ts) throws ConversionException;
}
