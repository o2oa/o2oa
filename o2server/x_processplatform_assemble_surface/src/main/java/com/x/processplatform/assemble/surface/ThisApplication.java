package com.x.processplatform.assemble.surface;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.x.base.core.project.Context;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.message.MessageConnector;
import com.x.processplatform.assemble.surface.schedule.CleanKeyLock;

public class ThisApplication {

	private ThisApplication() {
		// nothing
	}

	protected static Context context;

	private static ExecutorService threadPool;

	public static ExecutorService threadPool() {
		return threadPool;
	}

	private static void initThreadPool() {
		int maximumPoolSize = Runtime.getRuntime().availableProcessors() + 1;
		ThreadFactory threadFactory = new ThreadFactoryBuilder()
				.setNameFormat(ThisApplication.class.getPackageName() + "-threadpool-%d").build();
		threadPool = new ThreadPoolExecutor(0, maximumPoolSize, 120, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000),
				threadFactory);
	}

	public static Context context() {
		return context;
	}

	public static void init() {
		try {
			CacheManager.init(context.clazz().getSimpleName());
			context.schedule(CleanKeyLock.class, "2 0/2 * * * ?");
			initThreadPool();
			MessageConnector.start(context());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void destroy() {
		try {
			CacheManager.shutdown();
			MessageConnector.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
