package o2.collect.console.data;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import o2.base.core.project.config.Config;

public class DataServerTools {

	private static Logger logger = LoggerFactory.getLogger(DataServerTools.class);

	public static void start() throws Exception {
		File dataBaseDir = new File(Config.base(), "local/repository/data");
		FileUtils.forceMkdir(dataBaseDir);
		String[] tcps = new String[8];
		tcps[0] = "-tcp";
		tcps[1] = "-tcpAllowOthers";
		tcps[2] = "-tcpPort";
		tcps[3] = Config.dataServer().getTcpPort().toString();
		tcps[4] = "-tcpPassword";
		tcps[5] = Config.token().getPassword();
		tcps[6] = "-baseDir";
		tcps[7] = dataBaseDir.getAbsolutePath();
		Server.createTcpServer(tcps).start();
		logger.info("data server start completed.");
	}
}
