package gov.nist.healthcare.tcamt.service;


import gov.nist.healthcare.hl7tools.v2.maker.core.ConversionException;
import gov.nist.healthcare.tcamt.domain.TestCase;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class JsonTestCaseConverter extends JsonConverter implements TestCaseConverter {

	public JsonTestCaseConverter() {
		super();
	}

	
	
	
	/**
	 * 
	 */
	public String toString(TestCase tc) throws ConversionException {
		try {
			String value = mapper.writeValueAsString(tc);
			return value;
		} catch (JsonProcessingException e) {
			throw new ConversionException(e);
		}
	}

	
	/**
	 * 
	 */
	public TestCase fromString(String s) throws ConversionException {

		try {
			TestCase tc = mapper.readValue(s, TestCase.class);
			return tc;
		} catch (JsonParseException e) {
			throw new ConversionException(e);
		} catch (JsonMappingException e) {
			throw new ConversionException(e);
		} catch (IOException e) {
			throw new ConversionException(e);
		}
	}

}
