package com.x.jpush.assemble.control;

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

	public static void init() throws Exception {
		try {
			CacheManager.init(context.clazz().getSimpleName());
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
