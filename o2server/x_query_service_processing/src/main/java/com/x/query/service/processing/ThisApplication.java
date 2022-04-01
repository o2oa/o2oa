package com.x.query.service.processing;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.BooleanUtils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.x.base.core.project.Context;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.query.service.processing.schedule.CrawlCms;
import com.x.query.service.processing.schedule.CrawlWork;
import com.x.query.service.processing.schedule.CrawlWorkCompleted;

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

	public static Context context() {
		return context;
	}

	protected static void setContext(Context context) {
		ThisApplication.context = context;
	}

	public static void init() {
		try {
			initThreadPool();
			CacheManager.init(context.clazz().getSimpleName());
			if (BooleanUtils.isTrue(Config.query().getCrawlWork().getEnable())) {
				context.schedule(CrawlWork.class, Config.query().getCrawlWork().getCron());
			}
			if (BooleanUtils.isTrue(Config.query().getCrawlWorkCompleted().getEnable())) {
				context.schedule(CrawlWorkCompleted.class, Config.query().getCrawlWorkCompleted().getCron());
			}
			if (BooleanUtils.isTrue(Config.query().getCrawlCms().getEnable())) {
				context.schedule(CrawlCms.class, Config.query().getCrawlCms().getCron());
			}
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
