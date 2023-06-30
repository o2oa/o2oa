package com.x.server.console.server.data;

import java.nio.file.Path;
import java.util.Optional;

import org.h2.tools.Server;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.DataServer;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.H2Tools;

public class DataServerTools {

	private DataServerTools() {
		// nothing
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(DataServerTools.class);

	public static DataTcpWebServer start(DataServer dataServer) throws Exception {
		// File dataBaseDir = new File(Config.base(), "local/repository/data");
		// FileUtils.forceMkdir(dataBaseDir);
		Path dataBaseDir = Config.path_local_repository_data(true);
		Optional<String> opt = H2Tools.jarVersion();
		if (opt.isPresent()) {
			H2Tools.localRepositoryDataH2Version(opt.get());
		} else {
			throw new IllegalStateException("can not get h2 jar version.");
		}
		Server tcpServer = null;
		Server webServer = null;
		String password = Config.token().getPassword();
		String[] tcps = new String[9];
		tcps[0] = "-tcp";
		tcps[1] = "-tcpAllowOthers";
		tcps[2] = "-tcpPort";
		tcps[3] = dataServer.getTcpPort().toString();
		tcps[4] = "-baseDir";
		tcps[5] = dataBaseDir.toAbsolutePath().toString();
		tcps[6] = "-tcpPassword";
		tcps[7] = password;
		tcps[8] = "-ifNotExists";
		tcpServer = Server.createTcpServer(tcps).start();
		Integer webPort = dataServer.getWebPort();
		if ((null != webPort) && (webPort > 0)) {
			String[] webs = new String[4];
			webs[0] = "-web";
			webs[1] = "-webAllowOthers";
			webs[2] = "-webPort";
			webs[3] = webPort.toString();
			webServer = Server.createWebServer(webs).start();
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("data server repository:{}.", dataBaseDir);
		}
		LOGGER.print("****************************************");
		LOGGER.print("* data server start completed.");
		LOGGER.print("* port: " + dataServer.getTcpPort() + ".");
		if ((null != webPort) && (webPort > 0)) {
			LOGGER.print("* web console port: " + dataServer.getWebPort() + ".");
		}
		LOGGER.print("****************************************");
		return new DataTcpWebServer(tcpServer, webServer);
	}
}
