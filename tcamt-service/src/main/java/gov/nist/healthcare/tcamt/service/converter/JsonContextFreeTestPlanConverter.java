package gov.nist.healthcare.tcamt.service.converter;


import gov.nist.healthcare.hl7tools.v2.maker.core.ConversionException;
import gov.nist.healthcare.tcamt.domain.ContextFreeTestPlan;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class JsonContextFreeTestPlanConverter extends JsonConverter implements ContextFreeTestPlanConverter {

	public JsonContextFreeTestPlanConverter() {
		super();
	}

	
	
	
	/**
	 * 
	 */
	public String toString(ContextFreeTestPlan tp) throws ConversionException {
		try {
			String value = mapper.writeValueAsString(tp);
			return value;
		} catch (JsonProcessingException e) {
			throw new ConversionException(e);
		}
	}

	
	/**
	 * 
	 */
	public ContextFreeTestPlan fromString(String s) throws ConversionException {

		try {
			ContextFreeTestPlan tp = mapper.readValue(s, ContextFreeTestPlan.class);
			return tp;
		} catch (JsonParseException e) {
			throw new ConversionException(e);
		} catch (JsonMappingException e) {
			throw new ConversionException(e);
		} catch (IOException e) {
			throw new ConversionException(e);
		}
	}

}
