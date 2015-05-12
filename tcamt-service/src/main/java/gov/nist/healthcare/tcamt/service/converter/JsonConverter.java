/*
 * NIST IGAMT ModelConverter.java Mar 26, 2013
 * 
 * This code was produced by the National Institute of Standards and Technology (NIST). See the
 * "nist.disclaimer" file given in the distribution for information on the use and redistribution of
 * this software.
 */
package gov.nist.healthcare.tcamt.service.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * 
 * 
 */
public abstract class JsonConverter {

	protected final ObjectMapper	mapper	= new ObjectMapper();

	/**
   * 
   */
	public JsonConverter() {
		mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT);
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, true);
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
		mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		mapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.nist.healthcare.igamt.web.service.SegmentConverter#getContentType()
	 */
	public String getContentType() {
		return "application/json";
	}

}
