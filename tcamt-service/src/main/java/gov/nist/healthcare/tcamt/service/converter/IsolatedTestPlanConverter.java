package gov.nist.healthcare.tcamt.service.converter;

import gov.nist.healthcare.hl7tools.v2.maker.core.ConversionException;
import gov.nist.healthcare.tcamt.domain.IsolatedTestPlan;

public interface IsolatedTestPlanConverter {

	/**
	 * 
	 * @param ig
	 * @return
	 * @throws ConversionException
	 */
	public String toString(IsolatedTestPlan tp) throws ConversionException;
	
	/**
	 * 
	 * @param ig
	 * @return
	 * @throws ConversionException
	 */
	public IsolatedTestPlan fromString(String tp) throws ConversionException;
}
