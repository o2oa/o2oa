package com.x.processplatform.assemble.surface;

import java.util.concurrent.ForkJoinPool;

import com.x.base.core.project.ApplicationForkJoinWorkerThreadFactory;
import com.x.base.core.project.Context;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.message.MessageConnector;
import com.x.processplatform.assemble.surface.schedule.CleanKeyLock;

public class ThisApplication {

	private ThisApplication() {
		// nothing
	}

	protected static Context context;

	private static final ForkJoinPool FORKJOINPOOL = new ForkJoinPool(Runtime.getRuntime().availableProcessors(),
			new ApplicationForkJoinWorkerThreadFactory(ThisApplication.class.getPackage()), null, false);

	public static ForkJoinPool forkJoinPool() {
		return FORKJOINPOOL;
	}

	public static Context context() {
		return context;
	}

	public static void init() {
		try {
			CacheManager.init(context.clazz().getSimpleName());
			context.schedule(CleanKeyLock.class, "2 0/2 * * * ?");
			MessageConnector.start(context());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void destroy() {
		try {
			FORKJOINPOOL.shutdown();
			CacheManager.shutdown();
			MessageConnector.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
