package com.x.server.console.action;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.quickstart.QuickStartWebApp;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.util.resource.JarResource;
import org.eclipse.jetty.util.resource.Resource;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.server.console.server.Servers;

public class RestatWar {

	private static Logger logger = LoggerFactory.getLogger(RestatWar.class);

	/* 初始化完成 */
	public boolean execute(String simpleName) throws Exception {
		if (Servers.applicationServerIsRunning()) {
			try {
				GzipHandler gzipHandler = (GzipHandler) Servers.getApplicationServer().getHandler();
				HandlerList hanlderList = (HandlerList) gzipHandler.getHandler();
				File dir = null;
				String warFilePath = null;
				Boolean appExists = false;
				for (Handler handler : hanlderList.getHandlers()) {
					if (QuickStartWebApp.class.isAssignableFrom(handler.getClass())) {
						QuickStartWebApp app = (QuickStartWebApp) handler;
						if (StringUtils.equals("/" + simpleName, app.getContextPath())) {
							appExists = true;
							if (StringUtils.equals(type(simpleName), "storeWar")) {
								warFilePath = Config.dir_store(true) + "/" + simpleName + ".war";
								dir = new File(Config.dir_servers_applicationServer_work(), simpleName);
								logger.print("stoping offical application {} ...", simpleName);
							} else if (StringUtils.equals(type(simpleName), "customWar")) {
								warFilePath = Config.dir_custom(true) + "/" + simpleName + ".war";
								dir = new File(Config.dir_servers_applicationServer_work(), simpleName);
								logger.print("stoping custom application {} ...", simpleName);
							}
							app.stop();
							Thread.sleep(2000);
							if (dir != null && dir.exists()) {
								FileUtils.forceDelete(dir);
							}
							Resource base = Resource.newResource(warFilePath);
							dir.mkdirs();
							logger.print("redeploy application {} to work dir...", simpleName);
							JarResource.newJarResource(base).copyTo(dir);
							logger.print("starting application {} ...", simpleName);
							app.start();
						}
					}
				}
				if (!appExists) {
					logger.print("application {} not exists or not start.", simpleName);
				}
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			logger.print("application server not start.");
		}
		logger.print("restart application {} command excute completed.", simpleName);
		return true;
	}

	private String type(String simpleName) throws Exception {
		if ((new File(Config.dir_store(), simpleName + ".war")).exists()) {
			return "storeWar";
		}
		if ((new File(Config.dir_store_jars(), simpleName + ".jar")).exists()) {
			return "storeJar";
		}
		if ((new File(Config.dir_custom(), simpleName + ".war")).exists()) {
			return "customWar";
		}
		if ((new File(Config.dir_custom_jars(), simpleName + ".jar")).exists()) {
			return "customJar";
		}
		return null;
	}
}