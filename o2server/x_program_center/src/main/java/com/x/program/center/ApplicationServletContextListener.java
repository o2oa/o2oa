package com.x.program.center;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ApplicationServletContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		try {
			ThisApplication.context =Context.concrete(servletContextEvent);
			ThisApplication.init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		if (null != ThisApplication.context) {
			ThisApplication.destroy();
			ThisApplication.context.destrory(servletContextEvent);
		}
	}

}