package com.x.component.assemble.control;

import com.x.base.core.project.Context;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.component.assemble.control.schedule.InitComponents;

public class ThisApplication {

	private ThisApplication() {
		// nothing
	}

	private static Context context;

	private static final Logger LOGGER = LoggerFactory.getLogger(ThisApplication.class);

	public static void context(Context context) {
		ThisApplication.context = context;
	}

	public static Context context() {
		return context;
	}

	public static void init() {
		try {
			CacheManager.init(context.clazz().getSimpleName());
			context.scheduleLocal(InitComponents.class, 1);
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	public static void destroy() {
		try {
			CacheManager.shutdown();
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

}
