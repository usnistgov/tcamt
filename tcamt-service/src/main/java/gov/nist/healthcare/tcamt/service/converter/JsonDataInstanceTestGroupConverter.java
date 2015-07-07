package gov.nist.healthcare.tcamt.service.converter;


import gov.nist.healthcare.hl7tools.v2.maker.core.ConversionException;
import gov.nist.healthcare.tcamt.domain.DataInstanceTestCaseGroup;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class JsonDataInstanceTestGroupConverter extends JsonConverter implements DataInstanceTestGroupConverter {

	public JsonDataInstanceTestGroupConverter() {
		super();
	}

	
	
	
	/**
	 * 
	 */
	public String toString(DataInstanceTestCaseGroup group) throws ConversionException {
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
	public DataInstanceTestCaseGroup fromString(String s) throws ConversionException {

		try {
			DataInstanceTestCaseGroup group = mapper.readValue(s, DataInstanceTestCaseGroup.class);
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
