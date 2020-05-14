package com.x.server.console.action;

import com.x.base.core.container.factory.PersistenceXmlHelper;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.server.console.server.Servers;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.jdbc.conf.JDBCConfiguration;
import org.apache.openjpa.jdbc.meta.MappingTool;
import org.apache.openjpa.lib.util.Options;
import org.apache.openjpa.persistence.EntityManagerImpl;
import org.apache.openjpa.persistence.OpenJPAEntityManagerFactory;
import org.apache.openjpa.persistence.OpenJPAPersistence;
import org.eclipse.jetty.deploy.App;
import org.eclipse.jetty.deploy.DeploymentManager;
import org.eclipse.jetty.quickstart.QuickStartWebApp;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RestatWar {

	private static Logger logger = LoggerFactory.getLogger(RestatWar.class);

	/* 初始化完成 */
	public boolean execute( String simpleName ) throws Exception {
		if (Servers.applicationServerIsRunning()) {
			try {
				GzipHandler gzipHandler = (GzipHandler) Servers.applicationServer.getHandler();
				HandlerList hanlderList = (HandlerList) gzipHandler.getHandler();
				Boolean appExists = false;
				for (Handler handler : hanlderList.getHandlers()) {
					if ( QuickStartWebApp.class.isAssignableFrom(handler.getClass())) {
						QuickStartWebApp app = (QuickStartWebApp) handler;
						if (StringUtils.equals("/" + simpleName, app.getContextPath())) {
							appExists = true;
							app.stop();
							app.start();
						}
					}
				}
				if( !appExists ){
					logger.print("application {} not exists or not start.", simpleName );
				}
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			logger.print("application server not start." );
		}
		logger.print("restart application command excute completed." );
		return true;
	}
}