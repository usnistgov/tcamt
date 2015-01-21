package gov.nist.healthcare.tcamt.service;


import gov.nist.healthcare.hl7tools.v2.maker.core.ConversionException;
import gov.nist.healthcare.tcamt.domain.TestPlan;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class JsonTestPlanConverter extends JsonConverter implements TestPlanConverter {

	public JsonTestPlanConverter() {
		super();
	}

	
	
	
	/**
	 * 
	 */
	public String toString(TestPlan tp) throws ConversionException {
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
	public TestPlan fromString(String s) throws ConversionException {

		try {
			TestPlan tp = mapper.readValue(s, TestPlan.class);
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
