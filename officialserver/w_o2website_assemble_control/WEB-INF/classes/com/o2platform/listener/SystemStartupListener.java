package com.o2platform.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;


public class SystemStartupListener extends HttpServlet implements ServletContextListener {
	
	private static final long serialVersionUID = 1L;
	
	public static Logger logger = Logger.getLogger(SystemStartupListener.class);
	 
	public void contextDestroyed(ServletContextEvent arg0) {
		//logger.info("服务已经成功停止。");
	}
	public void contextInitialized(ServletContextEvent arg0) {
		//logger.info(">>>>>>>>>>>应用服务已经成功启动。");
	}
}
