package com.x.processplatform.assemble.designer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.x.base.core.project.Context;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.message.MessageConnector;

public class ThisApplication {

	private ThisApplication() {
		// nothing
	}

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

	protected static Context context;

	public static final ProjectionExecuteQueue projectionExecuteQueue = new ProjectionExecuteQueue();
	public static final MappingExecuteQueue mappingExecuteQueue = new MappingExecuteQueue();
	public static final FormVersionQueue formVersionQueue = new FormVersionQueue();
	public static final ProcessVersionQueue processVersionQueue = new ProcessVersionQueue();
	public static final ScriptVersionQueue scriptVersionQueue = new ScriptVersionQueue();

	public static Context context() {
		return context;
	}

	public static void init() {
		try {
			CacheManager.init(context.clazz().getSimpleName());
			MessageConnector.start(context());
			initThreadPool();
			context().startQueue(projectionExecuteQueue);
			context().startQueue(mappingExecuteQueue);
			context().startQueue(formVersionQueue);
			context().startQueue(processVersionQueue);
			context().startQueue(scriptVersionQueue);
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
