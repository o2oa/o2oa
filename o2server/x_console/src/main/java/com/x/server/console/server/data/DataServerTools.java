package com.x.server.console.server.data;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.h2.tools.Server;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.DataServer;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class DataServerTools {

	private static Logger logger = LoggerFactory.getLogger(DataServerTools.class);

	public static DataTcpWebServer start(DataServer dataServer) throws Exception {
		File dataBaseDir = new File(Config.base(), "local/repository/data");
		FileUtils.forceMkdir(dataBaseDir);
		Server tcpServer = null;
		Server webServer = null;
		String password = Config.token().getPassword();
		String[] tcps = new String[9];
		tcps[0] = "-tcp";
		tcps[1] = "-tcpAllowOthers";
		tcps[2] = "-tcpPort";
		tcps[3] = dataServer.getTcpPort().toString();
		tcps[4] = "-baseDir";
		tcps[5] = dataBaseDir.getAbsolutePath();
		tcps[6] = "-tcpPassword";
		tcps[7] = password;
		tcps[8] = "-ifNotExists";
		tcpServer = Server.createTcpServer(tcps).start();
		Integer webPort = dataServer.getWebPort();
		if ((null != webPort) && (webPort > 0)) {
			String webs[] = new String[4];
			webs[0] = "-web";
			webs[1] = "-webAllowOthers";
			webs[2] = "-webPort";
			webs[3] = webPort.toString();
			webServer = Server.createWebServer(webs).start();
		}
		System.out.println("****************************************");
		System.out.println("* data server start completed.");
		System.out.println("* port: " + dataServer.getTcpPort() + ".");
		System.out.println("* web console port: " + dataServer.getWebPort() + ".");
		System.out.println("****************************************");
		return new DataTcpWebServer(tcpServer, webServer);
	}
}
