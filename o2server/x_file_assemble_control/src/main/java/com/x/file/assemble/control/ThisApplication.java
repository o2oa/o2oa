package com.x.file.assemble.control;

import com.x.base.core.project.Context;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.file.assemble.control.jaxrs.file.FileRemoveQueue;

public class ThisApplication {

	public static FileRemoveQueue fileRemoveQueue;

	protected static Context context;

	public static Context context() {
		return context;
	}

	public static void init() {
		try {
			LoggerFactory.setLevel(Config.logLevel().x_file_assemble_control());
			fileRemoveQueue = new FileRemoveQueue();
			context().startQueue(fileRemoveQueue);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void destroy() {
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
