package gov.nist.healthcare.tcamt.service;


import gov.nist.healthcare.hl7tools.v2.maker.core.ConversionException;
import gov.nist.healthcare.tcamt.domain.TestStep;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class JsonTestStepConverter extends JsonConverter implements TestStepConverter {

	public JsonTestStepConverter() {
		super();
	}

	
	
	
	/**
	 * 
	 */
	public String toString(TestStep ts) throws ConversionException {
		try {
			String value = mapper.writeValueAsString(ts);
			return value;
		} catch (JsonProcessingException e) {
			throw new ConversionException(e);
		}
	}

	
	/**
	 * 
	 */
	public TestStep fromString(String ts) throws ConversionException {

		try {
			return mapper.readValue(ts, TestStep.class);
		} catch (JsonParseException e) {
			throw new ConversionException(e);
		} catch (JsonMappingException e) {
			throw new ConversionException(e);
		} catch (IOException e) {
			throw new ConversionException(e);
		}
	}

}
