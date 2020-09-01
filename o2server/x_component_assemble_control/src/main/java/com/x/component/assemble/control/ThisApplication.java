package com.x.component.assemble.control;

import com.x.base.core.project.Context;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.component.assemble.control.schedule.InitComponents;

public class ThisApplication {

	private ThisApplication() {
		// nothing
	}

	protected static Context context;

	private static Logger logger = LoggerFactory.getLogger(ThisApplication.class);

	public static Context context() {
		return context;
	}

	public static void init() {
		try {
			CacheManager.init(context.clazz().getSimpleName());
			LoggerFactory.setLevel(Config.logLevel().x_component_assemble_control());
			context.scheduleLocal(InitComponents.class, 1);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public static void destroy() {
		try {
			CacheManager.shutdown();
		} catch (Exception e) {
			logger.error(e);
		}
	}

}
