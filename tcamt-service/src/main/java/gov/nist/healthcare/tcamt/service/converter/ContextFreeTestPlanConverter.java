package gov.nist.healthcare.tcamt.service.converter;

import gov.nist.healthcare.hl7tools.v2.maker.core.ConversionException;
import gov.nist.healthcare.tcamt.domain.ContextFreeTestPlan;

public interface ContextFreeTestPlanConverter {

	/**
	 * 
	 * @param ig
	 * @return
	 * @throws ConversionException
	 */
	public String toString(ContextFreeTestPlan tp) throws ConversionException;
	
	/**
	 * 
	 * @param ig
	 * @return
	 * @throws ConversionException
	 */
	public ContextFreeTestPlan fromString(String tp) throws ConversionException;
}
