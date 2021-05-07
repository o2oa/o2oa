package com.x.base.core.project.tools;

import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.lang3.StringUtils;

public class PathTools {

	public static final String WEB_INF = "WEB-INF";
	public static final String WEB_INF_WEB_XML = "WEB-INF/web.xml";
	public static final String WEB_INF_CLASSES = "WEB-INF/classes";
	public static final String WEB_INF_CLASSES_LANGUAGE = "WEB-INF/classes/language";
	public static final String WEB_INF_LASTMODIFIED = "WEB-INF/lastModified";
	public static final String META_INF = "META";
	public static final String DOT_WAR = ".war";
	public static final String DOT_JAR = ".jar";
	public static final String DOT_ZIP = ".zip";
	public static final String DOT_BAT = ".bat";
	public static final String DOT_SH = ".sh";

	public static boolean shOrBat(Path path) {
		if (Files.exists(path) && (!Files.isDirectory(path))) {
			return StringUtils.endsWithAny(path.getFileName().toString().toLowerCase(), DOT_BAT, DOT_SH);
		}
		return false;
	}

	public static boolean jarOrZip(Path path) {
		if (Files.exists(path) && (!Files.isDirectory(path))) {
			return StringUtils.endsWithAny(path.getFileName().toString().toLowerCase(), DOT_JAR, DOT_ZIP);
		}
		return false;
	}

	public static boolean jar(Path path) {
		if (Files.exists(path) && (!Files.isDirectory(path))) {
			return StringUtils.endsWith(path.getFileName().toString().toLowerCase(), DOT_JAR);
		}
		return false;
	}

	public static boolean war(Path path) {
		if (Files.exists(path) && (!Files.isDirectory(path))) {
			return StringUtils.endsWith(path.getFileName().toString().toLowerCase(), DOT_WAR);
		}
		return false;
	}

}
