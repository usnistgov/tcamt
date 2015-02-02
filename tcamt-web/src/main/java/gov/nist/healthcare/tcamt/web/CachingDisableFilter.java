/*
 * NIST Lab Result Interface (LRI) CachingDisableFilter.java 03/20/2013 This code was produced by
 * the National Institute of Standards and Technology (NIST). See the "nist.disclaimer" file given
 * in the distribution for information on the use and redistribution of this software.
 */
package gov.nist.healthcare.tcamt.web;

import java.io.IOException;

import javax.faces.application.ResourceHandler;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author Harold Affo (NIST)
 * 
 */
public class CachingDisableFilter implements Filter {

	/**
	 * 
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

		// TODO Auto-generated method stub
	}

	/**
	 * 
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletResponse resp = (HttpServletResponse) response;
		HttpServletRequest req = (HttpServletRequest) request;
		if (req.getRequestURI().startsWith(
				req.getContextPath() + ResourceHandler.RESOURCE_IDENTIFIER)) {
			resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP
			resp.setHeader("Pragma", "no-cache"); // HTTP 1.0.
			resp.setDateHeader("Expires", 0); // Proxies.
			resp.setHeader("X-UA-Compatible", "IE=9");
		}
		chain.doFilter(request, response);
	}

	/**
	 * 
	 */
	@Override
	public void destroy() {

		// TODO Auto-generated method stub

	}

}
