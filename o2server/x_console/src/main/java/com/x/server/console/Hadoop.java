package com.x.server.console;

import com.x.base.core.project.config.Config;

public class Hadoop {

	private Hadoop() {
		// nothing
	}

	public static void init() throws Exception {
		setHomeDir();
	}

	private static void setHomeDir() throws Exception {
		if (Config.isWindowsJava11()) {
			System.setProperty("hadoop.home.dir", Config.path_commons_hadoop_windows(false).toString());
			// [qtp680227777-226] WARN org.apache.hadoop.util.NativeCodeLoader - Unable to
			// load native-hadoop library for your platform... using builtin-java classes
			// where applicable
			// System.setProperty("log4j.logger.org.apache.hadoop.util.NativeCodeLoader",
			// "ERROR");
			System.setProperty("java.library.path", Config.path_commons_hadoop_windows(false).resolve("bin").toString()
					+ System.getProperty("path.separator") + System.getProperty("java.library.path"));
		}
	}

}
