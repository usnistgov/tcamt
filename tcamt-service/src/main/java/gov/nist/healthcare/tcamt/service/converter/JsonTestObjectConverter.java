package gov.nist.healthcare.tcamt.service.converter;


import gov.nist.healthcare.hl7tools.v2.maker.core.ConversionException;
import gov.nist.healthcare.tcamt.domain.TestObject;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class JsonTestObjectConverter extends JsonConverter implements TestObjectConverter {

	public JsonTestObjectConverter() {
		super();
	}

	
	
	
	/**
	 * 
	 */
	public String toString(TestObject to) throws ConversionException {
		try {
			String value = mapper.writeValueAsString(to);
			return value;
		} catch (JsonProcessingException e) {
			throw new ConversionException(e);
		}
	}

	
	/**
	 * 
	 */
	public TestObject fromString(String s) throws ConversionException {

		try {
			TestObject to = mapper.readValue(s, TestObject.class);
			return to;
		} catch (JsonParseException e) {
			throw new ConversionException(e);
		} catch (JsonMappingException e) {
			throw new ConversionException(e);
		} catch (IOException e) {
			throw new ConversionException(e);
		}
	}

}
