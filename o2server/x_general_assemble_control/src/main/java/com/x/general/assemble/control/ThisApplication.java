package com.x.general.assemble.control;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.x.base.core.project.Context;
import com.x.base.core.project.cache.CacheManager;
import com.x.general.assemble.control.schedule.Clean;

public class ThisApplication {

	private ThisApplication() {
		// nothing
	}

	protected static Context context;

	private static ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(),
			new ThreadFactoryBuilder().setNameFormat(ThisApplication.class.getPackageName() + "-threadpool-%d")
					.build());

	public static ExecutorService threadPool() {
		return threadPool;
	}

	public static Context context() {
		return context;
	}

	public static void init() {
		try {
			CacheManager.init(context.clazz().getSimpleName());
			context().schedule(Clean.class, "0 0 2 * * ?");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void destroy() {
		try {
			threadPool.shutdown();
			CacheManager.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
