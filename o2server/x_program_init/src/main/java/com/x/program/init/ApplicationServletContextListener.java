package com.x.program.init;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ApplicationServletContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		ThisApplication.init();
		ThisApplication.path = servletContextEvent.getServletContext().getRealPath("");
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		ThisApplication.destroy();
	}

}