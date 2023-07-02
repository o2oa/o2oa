package com.x.mind.assemble.control;

import com.x.base.core.project.Context;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.message.MessageConnector;
import com.x.mind.assemble.control.queue.QueueShareNotify;

public class ThisApplication {

	private ThisApplication() {
		// nothing
	}

	protected static Context context;
	public static final QueueShareNotify queueShareNotify = new QueueShareNotify();

	public static Context context() {
		return context;
	}

	public static void init() {
		try {
			CacheManager.init(context.clazz().getSimpleName());
			MessageConnector.start(context());
			context().startQueue(queueShareNotify);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void destroy() {
		try {
			CacheManager.shutdown();
			queueShareNotify.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
