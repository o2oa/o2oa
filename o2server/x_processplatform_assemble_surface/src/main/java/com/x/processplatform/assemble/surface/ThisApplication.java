package com.x.processplatform.assemble.surface;

import java.util.concurrent.ForkJoinPool;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.ApplicationForkJoinWorkerThreadFactory;
import com.x.base.core.project.Context;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.message.MessageConnector;
import com.x.processplatform.assemble.surface.schedule.CleanKeyLock;
import com.x.processplatform.assemble.surface.schedule.Expire;
import com.x.processplatform.assemble.surface.schedule.PassExpired;
import com.x.processplatform.assemble.surface.schedule.TouchDetained;

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
			if (BooleanUtils.isTrue(Config.processPlatform().getTouchDetained().getEnable())) {
				context.schedule(TouchDetained.class, Config.processPlatform().getTouchDetained().getCron());
			}
			if (BooleanUtils.isTrue(Config.processPlatform().getExpire().getEnable())) {
				context.schedule(Expire.class, Config.processPlatform().getExpire().getCron());
			}
			if (BooleanUtils.isTrue(Config.processPlatform().getPassExpired().getEnable())) {
				context.schedule(PassExpired.class, Config.processPlatform().getPassExpired().getCron());
			}
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
