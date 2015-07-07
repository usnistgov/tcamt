package gov.nist.healthcare.tcamt.service.converter;


import gov.nist.healthcare.hl7tools.v2.maker.core.ConversionException;
import gov.nist.healthcare.tcamt.domain.DataInstanceTestStep;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class JsonDataInstanceTestStepConverter extends JsonConverter implements DataInstanceTestStepConverter {

	public JsonDataInstanceTestStepConverter() {
		super();
	}

	
	
	
	/**
	 * 
	 */
	public String toString(DataInstanceTestStep ts) throws ConversionException {
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
	public DataInstanceTestStep fromString(String s) throws ConversionException {

		try {
			DataInstanceTestStep ts = mapper.readValue(s, DataInstanceTestStep.class);
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
