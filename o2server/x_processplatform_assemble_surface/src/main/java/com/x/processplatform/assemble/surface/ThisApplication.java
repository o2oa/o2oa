package com.x.processplatform.assemble.surface;

import com.x.base.core.project.Context;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.schedule.CleanKeyLock;

public class ThisApplication {

	protected static Context context;

	public static Context context() {
		return context;
	}

	public static void init() {
		try {
			context.schedule(CleanKeyLock.class, "2 0/2 * * * ?");
			LoggerFactory.setLevel(Config.logLevel().x_processplatform_assemble_surface());
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