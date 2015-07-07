package gov.nist.healthcare.tcamt.service.converter;


import gov.nist.healthcare.hl7tools.v2.maker.core.ConversionException;
import gov.nist.healthcare.tcamt.domain.Metadata;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class JsonMetadataConverter extends JsonConverter implements MetadataConverter {

	public JsonMetadataConverter() {
		super();
	}

	
	
	
	/**
	 * 
	 */
	public String toString(Metadata metadata) throws ConversionException {
		try {
			String value = mapper.writeValueAsString(metadata);
			return value;
		} catch (JsonProcessingException e) {
			throw new ConversionException(e);
		}
	}

	
	/**
	 * 
	 */
	public Metadata fromString(String metadata) throws ConversionException {

		try {
			Metadata md = mapper.readValue(metadata, Metadata.class);
			return md;
		} catch (JsonParseException e) {
			throw new ConversionException(e);
		} catch (JsonMappingException e) {
			throw new ConversionException(e);
		} catch (IOException e) {
			throw new ConversionException(e);
		}
	}

}
