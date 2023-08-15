package com.x.base.core.project.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class H2Tools {

	private H2Tools() {
		// nothing
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(H2Tools.class);

	private static final Pattern VERSION_FILENAME_PATTERN = Pattern.compile("^h2-(.+).jar$");

	public static final String DATABASE = "X";
	public static final String USER = "sa";
	public static final String FILENAME_H2_VERSION = "h2.version";
	public static final String FILENAME_DATABASE = "X.mv.db";
	public static final String DEFAULT_VERSION = "1.4.200";

	public static Optional<String> jarVersion() {
		try (Stream<Path> stream = Files.walk(Config.pathCommonsExt(true), 1)) {
			return stream.map(o -> VERSION_FILENAME_PATTERN.matcher(o.getFileName().toString()))
					.filter(Matcher::matches).map(o -> o.group(1)).findFirst();
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return Optional.empty();
	}

	public static Optional<String> localRepositoryDataH2Version() {
		try {
			Path path = Config.pathLocalRepositoryData(true).resolve(H2Tools.FILENAME_H2_VERSION);
			if (Files.exists(path)) {
				String version = Files.readString(path);
				return Optional.of(version);
			} else {
				return Optional.of(DEFAULT_VERSION);
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return Optional.empty();
	}

	public static void localRepositoryDataH2Version(String version) throws IOException {
		Path path = Config.pathLocalRepositoryData(true).resolve(H2Tools.FILENAME_H2_VERSION);
		if (!Files.exists(path)) {
			Files.createFile(path);
		}
		Files.writeString(path, version, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	}

}
