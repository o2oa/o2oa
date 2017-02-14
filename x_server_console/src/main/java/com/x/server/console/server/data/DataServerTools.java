package com.x.server.console.server.data;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.project.server.Config;
import com.x.base.core.project.server.DataServer;

public class DataServerTools {

	private static Logger logger = LoggerFactory.getLogger(DataServerTools.class);

	public static DataTcpWebServer start(DataServer dataServer) throws Exception {
		File dataBaseDir = new File(Config.base(), "local/repository/data");
		FileUtils.forceMkdir(dataBaseDir);
		Server tcpServer = null;
		Server webServer = null;
		String password = dataServer.getPassword();
		if (StringUtils.isEmpty(password)) {
			password = Config.token().getPassword();
		}
		String[] tcps = new String[8];
		tcps[0] = "-tcp";
		tcps[1] = "-tcpAllowOthers";
		tcps[2] = "-tcpPort";
		tcps[3] = dataServer.getTcpPort().toString();
		tcps[4] = "-tcpPassword";
		tcps[5] = password;
		tcps[6] = "-baseDir";
		tcps[7] = dataBaseDir.getAbsolutePath();
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
		logger.info("data server start on port:{}, web console on port:{}.", dataServer.getTcpPort(),
				dataServer.getWebPort());
		return new DataTcpWebServer(tcpServer, webServer);
	}
}
