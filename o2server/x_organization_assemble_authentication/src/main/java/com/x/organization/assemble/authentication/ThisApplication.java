package com.x.organization.assemble.authentication;

import com.x.base.core.project.Context;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.authentication.jaxrs.authentication.QueueLoginRecord;
import com.x.organization.assemble.authentication.schedule.CleanupBind;
import com.x.organization.assemble.authentication.schedule.CleanupOauthCode;

public class ThisApplication {

	private ThisApplication() {
		// nothing
	}

	public static final QueueLoginRecord queueLoginRecord = new QueueLoginRecord();

	protected static Context context;

	public static Context context() {
		return context;
	}

	public static void init() {
		try {
			CacheManager.init(context.clazz().getSimpleName());
			LoggerFactory.setLevel(Config.logLevel().x_organization_assemble_authentication());
			context.startQueue(queueLoginRecord);
			context.schedule(CleanupBind.class, "0 */15 * * * ?");
			context.schedule(CleanupOauthCode.class, "0 */15 * * * ?");
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
