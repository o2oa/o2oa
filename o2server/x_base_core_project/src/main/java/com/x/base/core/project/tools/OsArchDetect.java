package com.x.base.core.project.tools;

import java.nio.file.Paths;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.project.schedule.JobReportListener;

public class OsArchDetect {

	private OsArchDetect() {
		// nothing
	}

	private static Logger logger = LoggerFactory.getLogger(OsArchDetect.class);

	public static final String OS_WINDOWS_X64 = "windows-x64";
	public static final String OS_LINUX_X64 = "linux-x64";
	public static final String OS_LINUX_AARCH64 = "linux-aarch64";
	public static final String OS_OTHER = "other";

	public static String os() {
		String osName = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
		String osArch = System.getProperty("os.arch", "").toLowerCase(Locale.ROOT);

		boolean isWindows = osName.contains("win");
		boolean isLinux = osName.contains("linux");

		// 归一化架构：x64 / aarch64 / 其它
		boolean isX64 = osArch.equals("amd64") || osArch.equals("x86_64");
		boolean isAArch64 = osArch.equals("aarch64") || osArch.equals("arm64");

		// 你定义的三个目标
		if (isWindows && (isX64)) {
			return OS_WINDOWS_X64;
		}
		if (isLinux && isX64) {
			return OS_LINUX_X64;
		}
		if (isLinux && isAArch64) {
			return OS_LINUX_AARCH64;
		}
		logger.warn("unknown platform, os.name: {}, os.arch: {}", osName, osArch);
		return OS_OTHER;
	}

	public static boolean isWindows() {
		return os().startsWith("windows");
	}

	public static String javaHome() {
		return System.getProperty("java.home");
	}

	public static String javaCmd() {
		var home = javaHome();
		var dir = Paths.get(home);
		if (isWindows()) {
			return dir.resolve("bin").resolve("java.exe").toString();
		} else {
			return dir.resolve("bin").resolve("java").toString();
		}
	}

}
