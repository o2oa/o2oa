package com.x.pan.assemble.control;

import com.x.base.core.project.Context;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.message.MessageConnector;
import com.x.pan.assemble.control.factory.ApplicationForkJoinWorkerThreadFactory;
import com.x.pan.assemble.control.schedule.RecycleClean;
import com.x.pan.assemble.control.util.OfficeManagerInstance;

import java.util.concurrent.ForkJoinPool;

/**
 * 应用初始化及销毁业务处理
 * @author sword
 */
public class ThisApplication {

	private ThisApplication() {
		// nothing
	}

	private static final ForkJoinPool FORKJOINPOOL = new ForkJoinPool(Runtime.getRuntime().availableProcessors(),
			new ApplicationForkJoinWorkerThreadFactory(ThisApplication.class.getPackage()), null, false);

	public static ForkJoinPool threadPool() {
		return FORKJOINPOOL;
	}

	protected static Context context;

	public static Context context() {
		return context;
	}

	public static void init() throws Exception {
		try {
			CacheManager.init(context.clazz().getSimpleName());
			MessageConnector.start(context());
			context.schedule(RecycleClean.class, "0 10 5 * * ?");
			OfficeManagerInstance.startInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void destroy() {
		try {
			CacheManager.shutdown();
			OfficeManagerInstance.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
