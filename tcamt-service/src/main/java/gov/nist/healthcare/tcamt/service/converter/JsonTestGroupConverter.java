package gov.nist.healthcare.tcamt.service.converter;


import gov.nist.healthcare.hl7tools.v2.maker.core.ConversionException;
import gov.nist.healthcare.tcamt.domain.TestCaseGroup;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class JsonTestGroupConverter extends JsonConverter implements TestGroupConverter {

	public JsonTestGroupConverter() {
		super();
	}

	
	
	
	/**
	 * 
	 */
	public String toString(TestCaseGroup group) throws ConversionException {
		try {
			String value = mapper.writeValueAsString(group);
			return value;
		} catch (JsonProcessingException e) {
			throw new ConversionException(e);
		}
	}

	
	/**
	 * 
	 */
	public TestCaseGroup fromString(String s) throws ConversionException {

		try {
			TestCaseGroup group = mapper.readValue(s, TestCaseGroup.class);
			return group;
		} catch (JsonParseException e) {
			throw new ConversionException(e);
		} catch (JsonMappingException e) {
			throw new ConversionException(e);
		} catch (IOException e) {
			throw new ConversionException(e);
		}
	}

}
