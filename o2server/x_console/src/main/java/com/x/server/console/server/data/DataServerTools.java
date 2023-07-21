package com.x.server.console.server.data;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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

public class DataServerTools {

	private DataServerTools() {
		// nothing
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(DataServerTools.class);

	private static final String GLOB = "glob:";

	private static final String WRITE_FILENAME_DATABASE_GLOB_PATTERN = GLOB + H2Tools.FILENAME_DATABASE + ".*.mv.db";

	private static final String H2MIGRATIONTOOL_JAR_GLOB_PATTERN = GLOB + "H2MigrationTool.jar";

	public static DataTcpWebServer start() throws Exception {

		DataServer dataServer = Config.currentNode().getData();
		if (null == dataServer) {
			LOGGER.info("data server is not configured.");
			return null;
		} else if (!BooleanUtils.isTrue(dataServer.getEnable())) {
			LOGGER.info("data server is not enable.");
			return null;
		}
		upgradeIfNecessary();
		return startServer(dataServer);
	}

	private static DataTcpWebServer startServer(DataServer dataServer) throws Exception {
		Path dataBaseDir = Config.path_local_repository_data(true);
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

	/**
	 * 如果H2文件存在,jarVersion 和 localRepositoryDataH2Version 版本不一致,需要升级服务器.
	 * 
	 * @throws Exception
	 */
	private static void upgradeIfNecessary() throws Exception {
		Optional<String> localRepositoryDataH2Version = H2Tools.localRepositoryDataH2Version();
		Path path = Config.path_local_repository_data(true).resolve(H2Tools.FILENAME_DATABASE);
		Optional<String> jarVersion = H2Tools.jarVersion();
		if (Files.exists(path) && jarVersion.isPresent() && localRepositoryDataH2Version.isPresent()
				&& (!StringUtils.equals(jarVersion.get(), localRepositoryDataH2Version.get()))) {
			LOGGER.print("upgrade h2 database from {} to {}, file path:{}.", localRepositoryDataH2Version.get(),
					jarVersion.get(), path);
			upgrade(path, localRepositoryDataH2Version.get(), jarVersion.get());
		}
	}

	private static void upgrade(Path path, String fromVersion, String targetVesion) throws Exception {
		backup(path);// 先备份数据库
		clean();
		upgradeExecute(path, fromVersion, targetVesion);
		cover(path);
		H2Tools.localRepositoryDataH2Version(targetVesion);
	}

	/**
	 * 由于升级最终产生新数据库名为:X.mv.db.214null.mv.db,所以先将这些格式的数据全部删除.
	 * 
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private static void clean() throws IOException, URISyntaxException {
		PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher(WRITE_FILENAME_DATABASE_GLOB_PATTERN);
		try (Stream<Path> stream = Files.walk(Config.path_local_repository_data(true), 1)) {
			stream.filter(o -> pathMatcher.matches(o.getFileName())).forEach(o -> {
				try {
					Files.delete(o);
				} catch (IOException e) {
					LOGGER.error(e);
				}
			});
		}
	}

	/**
	 * 备份原有数据库
	 * 
	 * @param path
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private static void backup(Path path) throws IOException, URISyntaxException {
		Path backup = Config.path_local_repository_data(true)
				.resolve(H2Tools.FILENAME_DATABASE + "." + DateTools.now());
		Files.copy(path, backup);
	}

	private static void upgradeExecute(Path path, String fromVersion, String targetVersion) throws Exception {
		List<String> jarPaths = new ArrayList<>();
		PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher(H2MIGRATIONTOOL_JAR_GLOB_PATTERN);
		try (Stream<Path> stream = Files.walk(Config.path_commons(false), 1)) {
			stream.filter(o -> pathMatcher.matches(o.getFileName()))
					.forEach(o -> jarPaths.add(o.toAbsolutePath().toString()));
		}
		String command = Config.command_java_path().toString() + " -classpath \""
				+ StringUtils.join(jarPaths, File.pathSeparator) + "\" com.manticore.h2.H2MigrationTool -u "
				+ H2Tools.USER + " -p " + Config.token().getPassword() + " -f " + fromVersion + " -t " + targetVersion
				+ " -d " + path.toString() + " -c ZIP -o VARIABLE_BINARY --force";
		LOGGER.info("upgrade command:{}.", () -> command);
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
		LOGGER.info(inputMessage);
		if (exitCode != 0) {
			throw new RunningException(errorMessage);
		}
		p.destroy();
	}

	/**
	 * 将新升级完成名为X.mv.db.214null.mv.db的数据库重新改为X.mv.db
	 * 
	 * @param path
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws RunningException
	 */
	private static void cover(Path path) throws IOException, URISyntaxException, RunningException {
		PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher(WRITE_FILENAME_DATABASE_GLOB_PATTERN);
		try (Stream<Path> stream = Files.walk(Config.path_local_repository_data(true), 1)) {
			Optional<Path> opt = stream.filter(o -> pathMatcher.matches(o.getFileName())).findFirst();
			if (opt.isPresent()) {
				Files.copy(opt.get(), path, StandardCopyOption.REPLACE_EXISTING);
			} else {
				throw new RunningException("can not find new version db file:{}.",
						WRITE_FILENAME_DATABASE_GLOB_PATTERN);
			}
		}
	}

}
