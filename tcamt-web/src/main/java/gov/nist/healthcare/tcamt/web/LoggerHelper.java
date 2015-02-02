/*
 * Meaningful Use Core
 * MailHelper.java October 14, 2011
 *
 * This code was produced by the National Institute of Standards and
 * Technology (NIST). See the 'nist.disclaimer' file given in the distribution
 * for information on the use and redistribution of this software.
 */
package gov.nist.healthcare.tcamt.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

/**
 * @author Harold Affo (NIST)
 */
public class LoggerHelper {

	private static LoggerHelper	instance;

	public static LoggerHelper getInstance() {
		if (instance == null) {
			synchronized (LoggerHelper.class) {
				instance = new LoggerHelper();
			}
		}
		return instance;
	}

	public String getExceptionMessage(HttpServletRequest request, String content) {
		if (StringUtils.isNotEmpty(content)) {
			String tmp = new String(content);

			Object attribute = null;

			attribute = request.getAttribute(Constant.ATTRIBUTE_ERROR_DATE);
			if (attribute != null) {
				tmp = tmp.replace("%date%", attribute.toString());
			} else {
				tmp = tmp.replace("%date%", "Not Available");
			}

			attribute = request.getAttribute(Constant.ATTRIBUTE_ERROR_TOKEN);
			if (attribute != null) {
				tmp = tmp.replaceAll("%rmdNumber%", attribute.toString());
			} else {
				tmp = tmp.replaceAll("%rmdNumber%", "Not Available");
			}

			tmp = tmp.replace("%user-agent%", request.getHeader("user-agent"));

			tmp = tmp.replace("%user-ip-address%", getUserIpAddress(request));

			attribute = request.getAttribute(Constant.ATTRIBUTE_ERROR_REQUEST_URI);
			if (attribute != null) {
				tmp = tmp.replace("%request-uri%", attribute.toString());
			} else {
				tmp = tmp.replace("%request-uri%", "Not Available");
			}

			attribute = request.getAttribute(Constant.ATTRIBUTE_ERROR_AJAX_REQUEST);
			if (attribute != null) {
				tmp = tmp.replace("%ajax-request%", ((Boolean) attribute) ? "Yes" : "No");
			} else {
				tmp = tmp.replace("%ajax-request%", "Not Available");
			}

			attribute = request.getAttribute(Constant.ATTRIBUTE_ERROR_STATUS_CODE);
			if (attribute != null) {
				tmp = tmp.replace("%status-code%", attribute.toString());
			} else {
				tmp = tmp.replace("%status-code%", "Not Available");
			}

			attribute = request.getAttribute(Constant.ATTRIBUTE_ERROR_EXCEPTION_TYPE);
			if (attribute != null) {
				tmp = tmp.replace("%exception-code%", attribute.toString());
			} else {
				tmp = tmp.replace("%exception-code%", "Not Available");
			}

			attribute = request.getAttribute(Constant.ATTRIBUTE_ERROR_MESSAGE);
			if (attribute != null) {
				tmp = tmp.replace("%exception-message%", attribute.toString());
			} else {
				tmp = tmp.replace("%exception-message%", "Not Available");
			}

			attribute = request.getAttribute(Constant.ATTRIBUTE_ERROR_TRACE);
			if (attribute != null) {
				tmp = tmp.replace("%exception-stack-trace%", attribute.toString());
			} else {
				tmp = tmp.replace("%exception-stack-trace%", "Not Available");
			}

			return tmp;
		}
		return null;
	}

	private String getUserIpAddress(HttpServletRequest request) {
		String ipAddress = request.getHeader("X-FORWARDED-FOR");
		if (ipAddress == null) {
			ipAddress = request.getRemoteAddr();
		}
		return ipAddress;
	}
}
