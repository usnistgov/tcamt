package gov.nist.healthcare.tcamt.service.converter;

import gov.nist.healthcare.hl7tools.v2.maker.core.ConversionException;
import gov.nist.healthcare.tcamt.domain.Metadata;

public interface MetadataConverter {

	/**
	 * 
	 * @param ig
	 * @return
	 * @throws ConversionException
	 */
	public String toString(Metadata metadata) throws ConversionException;
	
	/**
	 * 
	 * @param ig
	 * @return
	 * @throws ConversionException
	 */
	public Metadata fromString(String metadata) throws ConversionException;
}
