package gov.nist.healthcare.tcamt.service.converter;


import gov.nist.healthcare.hl7tools.v2.maker.core.ConversionException;
import gov.nist.healthcare.tcamt.domain.DataInstanceTestCase;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class JsonDataInstanceTestCaseConverter extends JsonConverter implements DataInstanceTestCaseConverter {

	public JsonDataInstanceTestCaseConverter() {
		super();
	}

	
	
	
	/**
	 * 
	 */
	public String toString(DataInstanceTestCase tc) throws ConversionException {
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
	public DataInstanceTestCase fromString(String s) throws ConversionException {

		try {
			DataInstanceTestCase tc = mapper.readValue(s, DataInstanceTestCase.class);
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
