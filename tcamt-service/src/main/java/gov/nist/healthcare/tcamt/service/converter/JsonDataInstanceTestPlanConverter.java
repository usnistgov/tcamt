package gov.nist.healthcare.tcamt.service.converter;


import gov.nist.healthcare.hl7tools.v2.maker.core.ConversionException;
import gov.nist.healthcare.tcamt.domain.DataInstanceTestPlan;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class JsonDataInstanceTestPlanConverter extends JsonConverter implements DataInstanceTestPlanConverter {

	public JsonDataInstanceTestPlanConverter() {
		super();
	}

	
	
	
	/**
	 * 
	 */
	public String toString(DataInstanceTestPlan tp) throws ConversionException {
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
	public DataInstanceTestPlan fromString(String s) throws ConversionException {

		try {
			DataInstanceTestPlan tp = mapper.readValue(s, DataInstanceTestPlan.class);
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
