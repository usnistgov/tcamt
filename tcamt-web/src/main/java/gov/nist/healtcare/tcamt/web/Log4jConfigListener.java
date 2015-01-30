/*
 * NIST IGAMT
 * Log4jConfigListener.java Mar 5, 2013
 *
 * This code was produced by the National Institute of Standards and
 * Technology (NIST). See the "nist.disclaimer" file given in the distribution
 * for information on the use and redistribution of this software.
 */
package gov.nist.healtcare.tcamt.web;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.PropertyConfigurator;

/**
 * @author Harold Affo (NIST)
 * 
 */
public class Log4jConfigListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
	try {
	    Properties p = new Properties();
	    InputStream log4jFile = Log4jConfigListener.class
		    .getResourceAsStream("/tcamt-log4j.properties");
	    p.load(log4jFile);
	    String logDir = sce.getServletContext().getRealPath("/WEB-INF/logs");

	    File f = new File(logDir);
	    if (!f.exists()) {
		f.mkdir();
	    }

	    p.put("log.dir", logDir);
	    PropertyConfigurator.configure(p);
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
	// TODO Auto-generated method stub

    }

}
