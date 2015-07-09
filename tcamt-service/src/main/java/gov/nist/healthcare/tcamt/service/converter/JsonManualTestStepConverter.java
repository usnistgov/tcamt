package gov.nist.healthcare.tcamt.service.converter;


import gov.nist.healthcare.hl7tools.v2.maker.core.ConversionException;
import gov.nist.healthcare.tcamt.domain.ManualTestStep;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class JsonManualTestStepConverter extends JsonConverter implements ManualTestStepConverter {

	public JsonManualTestStepConverter() {
		super();
	}

	
	
	
	/**
	 * 
	 */
	public String toString(ManualTestStep ts) throws ConversionException {
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
	public ManualTestStep fromString(String s) throws ConversionException {

		try {
			ManualTestStep ts = mapper.readValue(s, ManualTestStep.class);
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
