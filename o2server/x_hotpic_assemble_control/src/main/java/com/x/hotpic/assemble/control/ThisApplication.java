package com.x.hotpic.assemble.control;

import com.x.base.core.project.Context;
import com.x.base.core.project.cache.CacheManager;
import com.x.hotpic.assemble.control.queueTask.DocumentExistsCheckTask;
import com.x.hotpic.assemble.control.schedule.InfoExistsCheckTask;

public class ThisApplication {

	private ThisApplication() {
		// nothing
	}

	public static final DocumentExistsCheckTask queueLoginRecord = new DocumentExistsCheckTask();
	protected static Context context;

	public static Context context() {
		return context;
	}

	public static void init() {
		try {
			CacheManager.init(context.clazz().getSimpleName());
			context.startQueue(queueLoginRecord);
			context.schedule(InfoExistsCheckTask.class, "0 0/10 * * * ?");
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
