/*
 * NIST IGAMT
 * Constant.java Mar 7, 2013
 *
 * This code was produced by the National Institute of Standards and
 * Technology (NIST). See the "nist.disclaimer" file given in the distribution
 * for information on the use and redistribution of this software.
 */
package gov.nist.healthcare.tcamt.web;

import java.util.TimeZone;

/**
 * 
 * 
 * @author Harold Affo (NIST)
 * 
 */
public class Constant {

	private Constant() {
	}

	public final static int			INT_PROPERTY_NOT_DEFINED			= 1;
	public final static String		STRING_PROPERTY_NOT_DEFINED			= "";
	public final static Object		PROPERTY_NOT_DEFINED				= null;

	public static final String		ORGANIZATION_NAME					= "NIST";
	// name for file that holds base IG template
	public static final String		IG_TEMPLATE_FILE_NAME				= "ig.template.file.name";

	public static final String		USER_CREATED_SECTION				= "userCreatedSection";

	/*
	 * public static final String DATA_TYPES = "Datatypes"; public static final
	 * String MESSAGES = "Messages"; public static final String MESSSAGE_TYPES =
	 * "Message Types"; public static final String SEG_FIELD_DESCRIP =
	 * "Segment and Field Descriptions";
	 */
	public static final String		USAGE_NOTE							= "Usage Notes";
	public static final String		CONFORMANCE_STATEMENTS				= "Conformance Statements";
	public static final String		CONFORMANCE_IN_SPECIAL_CASES		= "Conformance in Special Cases and Additional Information";
	public static final String		DATATYPE_LEVEL_CONF_STATEMENTS		= "Datatype level conformance statements";

	public static final String		NAME_OF_SEGMENT_SECTION				= "segment";
	public static final String		NAME_OF_MESSAGES_SECTION			= "messages";
	public static final String		NAME_OF_DATA_TYPE_SECTION			= "datatype";

	public static final String		EMPTY_STRING						= "";

	public static final String		HTML_SINGLE_SPACE					= " ";
	public static final String		HTML_DOUBLE_QUOTE					= "&quot;";
	public static final String		HTML_LINE_BREAK						= "<br/>";
	public static final String		HTML_COLUMN_TAG						= "<td>";

	// values for pre-defined section ids;
	public static final String		TITLE_SECTION_ID					= "title";
	public static final String		TABLE_OF_CONTENTS_SECTION_ID		= "toc";
	// public static final String MESSAGES_SECTION_ID = "messages";
	// public static final String MESSAGE_TYPES_SECTION_ID = "messageTypes";
	// public static final String DATA_TYPES_SECTION_ID = "dataTypes";
	// public static final String SEGMENT_FIELD_DESC_SECTION_ID =
	// "segmentAndFieldDesc";

	// section recommendations
	public static final String		REQUIRED							= "Required";
	public static final String		OPTIONAL							= "Optional";
	public static final String		RECOMMENDED							= "Recommended";

	public static final String		TOP_LEVEL_SECTION					= "section";
	public static final String		SUB_SECTION							= "subsection";

	// Datatype Library
	public static final String		DT_CATEGORIES_FILE_NAME				= "dt.categories.file.name";

	public static final String		MIN_LENGTH							= "minLength";
	public static final String		TRUNCATED_ALLOWED					= "truncationAllowed";
	public static final String		CONF_LENGTH							= "confLength";
	public static final String		CONSTRAINABLE						= "constrainable";
	public static final String		IMPLEMENTATION						= "implementation";

	// Conforamnce Statement
	public static final String		SEGMENT_LEVEL						= "Segment";
	public static final String		USER_CONTEXT_ID						= "igContext";

	public static final String		DEFAULT_DT_LIB_NAME					= "default";
	public static final String		DEFAULT_DT_LIB_DESC					= "My default DT Library";

	public static final String		ATTRIBUTE_ERROR_EXCEPTION			= "javax.servlet.error.exception";

	public static final String		ATTRIBUTE_ERROR_EXCEPTION_TYPE		= "javax.servlet.error.exception_type";

	public static final String		ATTRIBUTE_ERROR_MESSAGE				= "javax.servlet.error.message";

	public static final String		ATTRIBUTE_ERROR_REQUEST_URI			= "javax.servlet.error.request_uri";

	public static final String		ATTRIBUTE_ERROR_STATUS_CODE			= "javax.servlet.error.status_code";

	public static final String		ATTRIBUTE_ERROR_TOKEN				= "error.token";

	public static final String		ATTRIBUTE_ERROR_AJAX_REQUEST		= "ajaxRequest";

	public static final String		ATTRIBUTE_ERROR_TRACE				= "javax.servlet.error.trace";

	public static final String		ATTRIBUTE_ERROR_DATE				= "error.date";

	public static final String		ATTRIBUTE_MESSAGE_VALIDATION_RESULT	= "MESSAGE_VALIDATION_RESULT";

	public static final String		SESSION_TC_CONFIG					= "tcConfig";

	public static final String		ATTRIBUTE_VALIDATION_MODEL			= "VALIDATION_MODEL";

	public static final TimeZone	TIME_ZONE							= TimeZone
																				.getTimeZone("EST");

	public static final String		EXCEPTION_CONTENT					= "******************** A New Exception Occured %rmdNumber% *************************<br />"
																				+ "<ul>"
																				+ "		                <li> Date: %date% </li>"
																				+ "		                <li> Token: %rmdNumber% </li>"
																				+ "		                <li> User agent: %user-agent% </li>"
																				+ "		                <li> User IP: %user-ip-address% </li>"
																				+ "		                <li> Request URI: %request-uri% </li>"
																				+ "		                <li> Ajax request:%ajax-request% </li>"
																				+ "		                <li> Status code: %status-code% </li>"
																				+ "		                <li> Exception type: %exception-code% </li>"
																				+ "		                <li> Exception message: %exception-message% </li>"
																				+ "		                <li> Exception stack trace: <br />"
																				+ "		                <pre> %exception-stack-trace% </pre>"
																				+ "		                </li>"
																				+ "		</ul>"
																				+ "		************************************** END %rmdNumber% ***************************<br />";

}
