package com.x.organization.assemble.express;

import com.x.base.core.project.Context;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.LoggerFactory;

public class ThisApplication {

	private ThisApplication() {
		// nothing
	}

	protected static Context context;

	public static Context context() {
		return context;
	}

	public static void init() {
		try {
			CacheManager.init(context.clazz().getSimpleName());
			LoggerFactory.setLevel(Config.logLevel().x_organization_assemble_express());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void destroy() {
		try {
			CacheManager.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
