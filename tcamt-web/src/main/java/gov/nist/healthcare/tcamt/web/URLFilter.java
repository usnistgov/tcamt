/*
 * NIST Lab Result Interface (LRI) CachingDisableFilter.java 03/20/2013 This code was produced by
 * the National Institute of Standards and Technology (NIST). See the "nist.disclaimer" file given
 * in the distribution for information on the use and redistribution of this software.
 */
package gov.nist.healthcare.tcamt.web;

import java.io.IOException;

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
public class URLFilter implements Filter {

	private static final String PRIMEFACES = "primefaces";
	private static final String FACES = "javax.faces";
	private static final String JAVAX_RESOURCE = "javax.faces.resource";
	private static final String JAVAX_RESOURCES = "javax.faces.resources";
	private static final String URL_FACES = "/faces";
	private static final String URL_FACES_WITH_ENDSLASH = URL_FACES + "/";

	private static final String URL_FACES_RESOURCE = URL_FACES_WITH_ENDSLASH
			+ JAVAX_RESOURCE;
	private static final String URL_FACES_RESOURCE_WITH_ENDSLASH = URL_FACES_RESOURCE
			+ "/";

	private static final String URL_FACES_RESOURCES = URL_FACES_WITH_ENDSLASH
			+ JAVAX_RESOURCES;
	private static final String URL_FACES_RESOURCES_WITH_ENDSLASH = URL_FACES_RESOURCES
			+ "/";

	/**
     * 
     */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

		// TODO Auto-generated method stub
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		if (isExcluded(req)) {
			res.sendError(404);
			return;
		} else {
			chain.doFilter(request, response);
		}
	}

	private boolean isExcluded(HttpServletRequest req) {
		String ln = req.getParameter("ln");
		String path = req.getRequestURI().substring(
				req.getContextPath().length());
		return path.equals(URL_FACES) || path.equals(URL_FACES_WITH_ENDSLASH)
				|| path.equals(URL_FACES_RESOURCE)
				|| path.equals(URL_FACES_RESOURCE_WITH_ENDSLASH)
				|| path.equals(URL_FACES_RESOURCES)
				|| path.equals(URL_FACES_RESOURCES_WITH_ENDSLASH)
				|| (ln != null && !ln.startsWith(PRIMEFACES))
				|| (ln != null && ln.equalsIgnoreCase(FACES));
		/*
		 * || (path.endsWith(".xhtml") && !path.equals("/faces/index.xhtml") &&
		 * !path .equals("/index.xhtml"));
		 */
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
