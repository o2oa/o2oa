package com.x.base.core.project.logger;

public class LoggerFactory {

	private LoggerFactory() {
	}

	public static Logger getLogger(Class<?> clz) {
		return new Logger(clz.getName());
	}

}