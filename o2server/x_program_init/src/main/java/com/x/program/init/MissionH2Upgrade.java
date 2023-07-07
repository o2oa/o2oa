package com.x.program.init;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import com.manticore.h2.H2MigrationTool;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.H2Tools;
import com.x.base.core.project.tools.StringTools;
import com.x.program.init.Missions.Messages;
import com.x.program.init.Missions.Mission;

public class MissionH2Upgrade implements Mission {

	private static final Logger LOGGER = LoggerFactory.getLogger(MissionH2Upgrade.class);

	private static final String GLOB = "glob:";

	private static final String WRITE_FILENAME_DATABASE_GLOB_PATTERN = GLOB + H2Tools.FILENAME_DATABASE + ".*.mv.db";

	private static final String JAR_GLOB_PATTERN = GLOB + "*.jar";

	private String fromVersion;

	private String targetVersion;

	public String getFromVersion() {
		return fromVersion;
	}

	public void setFromVersion(String fromVersion) {
		this.fromVersion = fromVersion;
	}

	public String getTargetVersion() {
		return targetVersion;
	}

	public void setTargetVersion(String targetVersion) {
		this.targetVersion = targetVersion;
	}

	@Override
	public void execute(Missions.Messages messages) {
		messages.head(MissionH2Upgrade.class.getSimpleName());
		try {
			messages.msg("executing");
			Optional<String> jarVersion = H2Tools.jarVersion();
			if (jarVersion.isEmpty()) {
				throw new ExceptionMissionExecute("can not get h2 jar version.");
			}
			Optional<String> localRepositoryDataH2Version = H2Tools.localRepositoryDataH2Version();
			if (localRepositoryDataH2Version.isEmpty()) {
				throw new ExceptionMissionExecute("can not get h2 localRepositoryDataH2Version version.");
			}
			Path path = Config.path_local_repository_data(true).resolve(H2Tools.FILENAME_DATABASE);
			clean();
			backup(path);
			if (upgrade(path) == 0) {
				cover(path);
				H2Tools.localRepositoryDataH2Version(jarVersion.get());
			}
			messages.msg("success");
		} catch (Exception e) {
			messages.err(e.getMessage());
			throw new ExceptionMissionExecute(e);
		}
	}

	private void clean() throws IOException, URISyntaxException {
		PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher(WRITE_FILENAME_DATABASE_GLOB_PATTERN);
		try (Stream<Path> stream = Files.walk(Config.path_local_repository_data(true), 1)) {
			stream.filter(o -> pathMatcher.matches(o.getFileName())).forEach(o -> {
				try {
					Files.delete(o);
				} catch (IOException e) {
					throw new ExceptionMissionExecute(e);
				}
			});
		}
	}

	private void cover(Path path) throws IOException, URISyntaxException {
		PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher(WRITE_FILENAME_DATABASE_GLOB_PATTERN);
		try (Stream<Path> stream = Files.walk(Config.path_local_repository_data(true), 1)) {
			Optional<Path> opt = stream.filter(o -> pathMatcher.matches(o.getFileName())).findFirst();
			if (opt.isPresent()) {
				Files.copy(opt.get(), path, StandardCopyOption.REPLACE_EXISTING);
			} else {
				throw new ExceptionMissionExecute(
						"can not find new version db file:" + WRITE_FILENAME_DATABASE_GLOB_PATTERN + ".");
			}
		}
	}

	private void backup(Path path) throws IOException, URISyntaxException {
		Path backup = Config.path_local_repository_data(true)
				.resolve(H2Tools.FILENAME_DATABASE + "." + DateTools.now());
		Files.copy(path, backup);
	}

	private int upgrade(Path path) throws Exception {
		List<String> jarPaths = new ArrayList<>();
		PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher(JAR_GLOB_PATTERN);
		try (Stream<Path> stream = Files
				.walk(Paths.get(ThisApplication.getPath()).resolve("WEB-INF").resolve("ext").toAbsolutePath(), 1)) {
			stream.filter(o -> pathMatcher.matches(o.getFileName()))
					.forEach(o -> jarPaths.add(o.toAbsolutePath().toString()));
		}
		String command = Config.command_java_path().toString() + " -classpath \""
				+ StringUtils.join(jarPaths, File.pathSeparator) + "\" " + H2MigrationTool.class.getName() + " -u "
				+ H2Tools.USER + " -p " + Config.token().getPassword() + " -f " + fromVersion + " -t "
				+ this.getTargetVersion() + " -d " + path.toString() + " -c ZIP -o VARIABLE_BINARY --force";

		LOGGER.debug("upgrade command:{}.", () -> command);
		int exitCode = exec(command);
		LOGGER.info("upgrade result:{}.", () -> exitCode);
		return exitCode;
	}

	private int exec(String command) throws IOException, InterruptedException {
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
			LOGGER.error(new ExceptionMissionExecute(errorMessage));
		}
		p.destroy();
		return exitCode;
	}

	public static CheckResult check() throws IOException, URISyntaxException {
		CheckResult result = new CheckResult();
		Optional<String> jarVersion = H2Tools.jarVersion();
		Optional<String> localRepositoryDataH2Version = H2Tools.localRepositoryDataH2Version();
		Path path = Config.path_local_repository_data(true).resolve(H2Tools.FILENAME_DATABASE);
		result.setDataBaseFileExists(Files.exists(path));
		if (jarVersion.isPresent()) {
			result.setJarVersion(jarVersion.get());
		}
		if (localRepositoryDataH2Version.isPresent()) {
			result.setLocalRepositoryDataH2Version(localRepositoryDataH2Version.get());
		}
		if (Files.exists(path) && jarVersion.isPresent() && localRepositoryDataH2Version.isPresent()) {
			result.setNeedUpgrade(!StringUtils.equals(jarVersion.get(), localRepositoryDataH2Version.get()));
		}
		return result;
	}

	public static class CheckResult extends GsonPropertyObject {

		private static final long serialVersionUID = -4492633937635601307L;

		private String jarVersion;

		private String localRepositoryDataH2Version;

		private Boolean dataBaseFileExists;

		private Boolean needUpgrade;

		public Boolean getDataBaseFileExists() {
			return dataBaseFileExists;
		}

		public void setDataBaseFileExists(Boolean dataBaseFileExists) {
			this.dataBaseFileExists = dataBaseFileExists;
		}

		public String getJarVersion() {
			return jarVersion;
		}

		public void setJarVersion(String jarVersion) {
			this.jarVersion = jarVersion;
		}

		public String getLocalRepositoryDataH2Version() {
			return localRepositoryDataH2Version;
		}

		public void setLocalRepositoryDataH2Version(String localRepositoryDataH2Version) {
			this.localRepositoryDataH2Version = localRepositoryDataH2Version;
		}

		public Boolean getNeedUpgrade() {
			return needUpgrade;
		}

		public void setNeedUpgrade(Boolean needUpgrade) {
			this.needUpgrade = needUpgrade;
		}

	}

}
