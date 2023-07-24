package com.x.processplatform.service.processing;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.x.base.core.project.Context;

@WebListener
public class ApplicationServletContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		try {
			ThisApplication.context = Context.concrete(servletContextEvent);
			ThisApplication.init();
			ThisApplication.context().regist();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		try {
			ThisApplication.destroy();
			ThisApplication.context.destrory(servletContextEvent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}