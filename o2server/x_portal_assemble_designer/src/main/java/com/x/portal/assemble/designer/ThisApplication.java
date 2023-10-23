package com.x.portal.assemble.designer;

import java.util.concurrent.ForkJoinPool;

import com.x.base.core.project.ApplicationForkJoinWorkerThreadFactory;
import com.x.base.core.project.Context;
import com.x.base.core.project.cache.CacheManager;
import com.x.portal.assemble.designer.queue.PageVersionQueue;
import com.x.portal.assemble.designer.queue.ScriptVersionQueue;

public class ThisApplication {

	private ThisApplication() {
		// nothing
	}

	private static final ForkJoinPool FORKJOINPOOL = new ForkJoinPool(Runtime.getRuntime().availableProcessors(),
			new ApplicationForkJoinWorkerThreadFactory(ThisApplication.class.getPackage()), null, false);

	public static ForkJoinPool forkJoinPool() {
		return FORKJOINPOOL;
	}

	public static final PageVersionQueue pageVersionQueue = new PageVersionQueue();
	public static final ScriptVersionQueue scriptVersionQueue = new ScriptVersionQueue();

	protected static Context context;

	public static Context context() {
		return context;
	}

	public static void init() {
		try {
			CacheManager.init(context.clazz().getSimpleName());
			context().startQueue(pageVersionQueue);
			context().startQueue(scriptVersionQueue);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void destroy() {
		try {
			FORKJOINPOOL.shutdown();
			CacheManager.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
