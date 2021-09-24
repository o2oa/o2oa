package com.x.base.core.project.logger;

public class LoggerFactory {

	private LoggerFactory() {
	}

	public static Logger getLogger(Class<?> clz) {
		return new Logger(clz.getName());
	}

	@Deprecated
	/**
	 * 
	 * 此方法已经废弃没有任何用处,有部分custom模块编写时错误的使用了这个方法,作为兼容custom模块的空方法.
	 */
	public static void setLevel(Object o) {
		// nothing
	}

}