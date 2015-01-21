package gov.nist.healthcare.tcamt.service;


import gov.nist.healthcare.hl7tools.v2.maker.core.ConversionException;
import gov.nist.healthcare.tcamt.domain.TestStory;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class JsonTestStoryConverter extends JsonConverter implements TestStoryConverter {

	public JsonTestStoryConverter() {
		super();
	}

	
	
	
	/**
	 * 
	 */
	public String toString(TestStory testStory) throws ConversionException {
		try {
			String value = mapper.writeValueAsString(testStory);
			return value;
		} catch (JsonProcessingException e) {
			throw new ConversionException(e);
		}
	}

	
	/**
	 * 
	 */
	public TestStory fromString(String testStory) throws ConversionException {

		try {
			TestStory ts = mapper.readValue(testStory, TestStory.class);
			return ts;
		} catch (JsonParseException e) {
			throw new ConversionException(e);
		} catch (JsonMappingException e) {
			throw new ConversionException(e);
		} catch (IOException e) {
			throw new ConversionException(e);
		}
	}

}
