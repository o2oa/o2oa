package com.x.query.assemble.designer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

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
			ThisApplication
					.setContext(Context.concrete(servletContextEvent, true, Business.getDynamicEntityClassLoader()));
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
