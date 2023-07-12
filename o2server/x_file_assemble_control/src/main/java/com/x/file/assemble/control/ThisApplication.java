package com.x.file.assemble.control;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.x.base.core.project.Context;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.message.MessageConnector;
import com.x.file.assemble.control.jaxrs.file.FileRemoveQueue;
import com.x.file.assemble.control.schedule.RecycleClean;

/**
 * @author sword
 */
public class ThisApplication {

	private ThisApplication() {
		// nothing
	}

	private static ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(),
			new ThreadFactoryBuilder().setNameFormat(ThisApplication.class.getPackageName() + "-threadpool-%d")
					.build());

	public static ExecutorService threadPool() {
		return threadPool;
	}

	public static final FileRemoveQueue fileRemoveQueue = new FileRemoveQueue();

	protected static Context context;

	public static Context context() {
		return context;
	}

	public static void init() {
		try {
			CacheManager.init(context.clazz().getSimpleName());
			MessageConnector.start(context());
			context.schedule(RecycleClean.class, "0 20 2 * * ?");
			context().startQueue(fileRemoveQueue);
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
