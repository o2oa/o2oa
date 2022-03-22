package com.x.query.assemble.designer;

import java.net.URL;
import java.net.URLClassLoader;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.eclipse.jetty.webapp.WebAppClassLoader;

import com.x.base.core.project.Context;

/**
 * web应用初始化
 * 
 * @author sword
 */
@WebListener
public class ApplicationServletContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		try {
			ThisApplication.setContext(Context.concrete(servletContextEvent, Business.getDynamicEntityClassLoader()));
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
