/*
 * NIST Lab Result Interface (LRI) Constant.java 03/20/2013 This code was produced by the National
 * Institute of Standards and Technology (NIST). See the "nist.disclaimer" file given in the
 * distribution for information on the use and redistribution of this software.
 */
package gov.nist.healthcare.tcamt.web;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import javax.faces.FacesException;
import javax.faces.application.NavigationHandler;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;

/**
 * 
 * @author Harold Affo (NIST)
 * 
 */
public class GeneralExceptionHandler extends ExceptionHandlerWrapper {

	private final ExceptionHandler	wrapped;

	private static final Logger		LOGGER	= Logger.getLogger(GeneralExceptionHandler.class);

	public GeneralExceptionHandler(ExceptionHandler wrapped) {

		this.wrapped = wrapped;
	}

	@Override
	public ExceptionHandler getWrapped() {

		return this.wrapped;
	}

	@Override
	public void handle() throws FacesException {

		for (Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents().iterator(); i
				.hasNext();) {
			ExceptionQueuedEvent event = i.next();
			ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event.getSource();
			Throwable t = context.getException();
			if (t instanceof ViewExpiredException) {
				ViewExpiredException vee = (ViewExpiredException) t;
				FacesContext facesContext = FacesContext.getCurrentInstance();
				Map<String, Object> requestMap = facesContext.getExternalContext().getRequestMap();
				NavigationHandler navigationHandler = facesContext.getApplication()
						.getNavigationHandler();
				try {
					// Push some useful stuff to the request scope for use in
					// the page
					requestMap.put("currentViewId", vee.getViewId());
					navigationHandler.handleNavigation(facesContext, null, "/views/errors/expired");
					facesContext.renderResponse();

				} finally {
					i.remove();
				}
			} else if (t.getMessage() == null
					|| t.getMessage() != null
					&& !t.getMessage().contains("Connection reset by peer: socket write error")
					&& !t.getMessage().contains(
							"Software caused connection abort: socket write error")) {
				FacesContext facesContext = FacesContext.getCurrentInstance();
				NavigationHandler navigationHandler = facesContext.getApplication()
						.getNavigationHandler();
				final HttpServletRequest request = (HttpServletRequest) facesContext
						.getExternalContext().getRequest();
				try {
					// store debugging information
					request.setAttribute(Constant.ATTRIBUTE_ERROR_DATE, new SimpleDateFormat(
							"MM/dd/yyyy HH:mm:ss").format(new Date()));
					request.setAttribute(Constant.ATTRIBUTE_ERROR_EXCEPTION_TYPE, t.getClass());
					request.setAttribute(Constant.ATTRIBUTE_ERROR_MESSAGE, t.getMessage());
					request.setAttribute(Constant.ATTRIBUTE_ERROR_REQUEST_URI,
							request.getRequestURI());
					request.setAttribute(Constant.ATTRIBUTE_ERROR_STATUS_CODE,
							HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					String token = generateToken();
					request.setAttribute(Constant.ATTRIBUTE_ERROR_TOKEN, token);
					request.setAttribute(Constant.ATTRIBUTE_ERROR_AJAX_REQUEST, facesContext
							.getPartialViewContext().isAjaxRequest());

					request.setAttribute(Constant.ATTRIBUTE_ERROR_TRACE,
							ExceptionUtils.getStackTrace(t));

					String content = LoggerHelper.getInstance().getExceptionMessage(request,
							Constant.EXCEPTION_CONTENT);
					// get logging content
					content = content.replaceAll("<br />", "\n").replaceAll("<ul>", "\n")
							.replaceAll("</ul>", "\n").replaceAll("<li>", "-")
							.replaceAll("</li>", "\n").replaceAll("<pre>", "\n")
							.replaceAll("</pre>", "\n");
					LOGGER.error(content);
					t.printStackTrace();
					request.removeAttribute(Constant.ATTRIBUTE_ERROR_EXCEPTION);
					navigationHandler.handleNavigation(facesContext, null, "/views/errors/bug");
					facesContext.renderResponse();

				} finally {
					i.remove();
				}
			}
		}

		// Therefore, let the parent handle left exception.
		getWrapped().handle();
	}

	/**
	 * @return
	 */
	private String generateToken() {

		return "" + Calendar.getInstance().get(Calendar.YEAR) + "-"
				+ Calendar.getInstance().get(Calendar.MONTH) + "-" + new Random().nextInt(1000)
				+ "-" + Calendar.getInstance().get(Calendar.SECOND);
	}

}
