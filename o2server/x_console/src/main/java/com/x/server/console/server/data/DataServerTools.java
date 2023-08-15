package com.x.server.console.server.data;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.h2.tools.Server;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.DataServer;
import com.x.base.core.project.exception.RunningException;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.H2Tools;
import com.x.base.core.project.tools.StringTools;

public class DataServerTools {

	private DataServerTools() {
		// nothing
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(DataServerTools.class);

	private static final String SCRIPT_TEMPLATE = " -classpath {0} org.h2.tools.Script -url {1} -user {2} -password {3} -script {4} -options compression zip";
	private static final String RUNSCRIPT_TEMPLATE = " -classpath {0} org.h2.tools.RunScript -url {1} -user {2} -password {3} -script {4} -options compression zip";

	public static DataTcpWebServer start() throws Exception {

		DataServer dataServer = Config.currentNode().getData();
		if (null == dataServer) {
			LOGGER.info("data server is not configured.");
			return null;
		} else if (!BooleanUtils.isTrue(dataServer.getEnable())) {
			LOGGER.info("data server is not enable.");
			return null;
		}
		migrateIfNecessary();
		return startServer(dataServer);
	}

	private static DataTcpWebServer startServer(DataServer dataServer) throws Exception {
		Path dataBaseDir = Config.pathLocalRepositoryData(true);
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
		writeVersion();
		return new DataTcpWebServer(tcpServer, webServer);
	}

	private static void writeVersion() throws IOException, URISyntaxException {
		Optional<String> opt = H2Tools.jarVersion();
		if (opt.isPresent()) {
			H2Tools.localRepositoryDataH2Version(opt.get());
		}
	}

	/**
	 * 如果H2文件存在,jarVersion 和 localRepositoryDataH2Version 版本不一致,需要升级服务器.
	 * 
	 * @throws Exception
	 */
	private static void migrateIfNecessary() throws Exception {
		Optional<String> localRepositoryDataH2Version = H2Tools.localRepositoryDataH2Version();
		Path path = Config.pathLocalRepositoryData(true).resolve(H2Tools.FILENAME_DATABASE);
		Optional<String> jarVersion = H2Tools.jarVersion();
		if (Files.exists(path) && jarVersion.isPresent() && localRepositoryDataH2Version.isPresent()
				&& (!StringUtils.equals(jarVersion.get(), localRepositoryDataH2Version.get()))) {
			LOGGER.print("upgrade h2 database from {} to {}, file path:{}.", localRepositoryDataH2Version.get(),
					jarVersion.get(), path);
			migrate(path, localRepositoryDataH2Version.get(), jarVersion.get());
		}
	}

	private static void migrate(Path path, String fromVersion, String targetVesion) throws Exception {
		String url = "jdbc:h2:file:"
				+ Config.pathLocalRepositoryData(true).resolve(H2Tools.DATABASE).toAbsolutePath().toString();
		String file = Config.pathLocalRepositoryData(true).resolve("script.zip").toAbsolutePath().toString();
		script(fromVersion, url, file);
		mv(path);
		runScript(fromVersion, targetVesion, url, file);
	}

	/**
	 * 移动原有数据库
	 * 
	 * @param path
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private static void mv(Path path) throws IOException, URISyntaxException {
		Path backup = Config.pathLocalRepositoryData(true)
				.resolve(H2Tools.FILENAME_DATABASE + "." + DateTools.compact(new Date()));
		LOGGER.info("backup h2 database to:{}.", backup.toAbsolutePath().toString());
		Files.move(path, backup);
	}

	private static void script(String fromVersion, String url, String file) throws Exception {
		String classpath = Config.path_commons_h2(false).resolve(fromVersion).toString();
		String command = Config.command_java_path().toString()
				+ StringTools.format(SCRIPT_TEMPLATE, classpath, url, H2Tools.USER, Config.token().getPassword(), file);
		LOGGER.info("migrate h2 script command:{}.", command);
		exec(command);
	}

	private static void runScript(String fromVersion, String targetVersion, String url, String file) throws Exception {
		String classpath = Config.path_commons_h2(false).resolve(targetVersion).toString();
		String command = Config.command_java_path().toString() + StringTools.format(RUNSCRIPT_TEMPLATE, classpath, url,
				H2Tools.USER, Config.token().getPassword(), file);
		if (StringUtils.equals(H2Tools.DEFAULT_VERSION, fromVersion)) {
			command += " FROM_1X";
		}
		LOGGER.info("migrate h2 runScript command:{}.", command);
		exec(command);
	}

	private static void exec(String command) throws IOException, InterruptedException, RunningException {
		java.lang.ProcessBuilder processBuilder = new java.lang.ProcessBuilder();
		if (SystemUtils.IS_OS_WINDOWS) {
			processBuilder.command("cmd", "/c", command);
		} else {
			processBuilder.command("sh", "-c", command);
		}
		Process p = processBuilder.start();
		InputStream inputStream = p.getInputStream();
		InputStream errorStream = p.getErrorStream();
		int exitCode = p.waitFor();
		String errorMessage = IOUtils.toString(errorStream, StandardCharsets.UTF_8);
		String inputMessage = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
		if (StringUtils.isNotEmpty(inputMessage)) {
			LOGGER.info(inputMessage);
		}
		if (exitCode != 0) {
			throw new RunningException(errorMessage);
		}
		p.destroy();
	}

}
