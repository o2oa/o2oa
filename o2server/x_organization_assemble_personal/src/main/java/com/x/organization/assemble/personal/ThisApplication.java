package com.x.organization.assemble.personal;

import com.x.base.core.project.Context;
import com.x.base.core.project.cache.CacheManager;
import com.x.organization.assemble.personal.jaxrs.exmail.QueueUpdateExmail;
import com.x.organization.assemble.personal.schedule.DisableExpiredEmpower;

public class ThisApplication {

	private ThisApplication() {
		// nothing
	}

	protected static Context context;

	public static final QueueUpdateExmail queueUpdateExmail = new QueueUpdateExmail();

	public static Context context() {
		return context;
	}

	public static void init() {
		try {
			CacheManager.init(context.clazz().getSimpleName());
			context.schedule(DisableExpiredEmpower.class, "0 0/20 * * * ?");
			context.startQueue(queueUpdateExmail);
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
