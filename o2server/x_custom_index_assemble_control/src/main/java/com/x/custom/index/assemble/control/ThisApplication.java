package com.x.custom.index.assemble.control;

import com.x.base.core.project.Context;
import com.x.base.core.project.cache.CacheManager;

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
			// context.scheduleLocal(CheckConfigFile.class, 5);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void destroy() {
		try {
			// nothing
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
