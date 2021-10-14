package com.x.organization.assemble.authentication;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.Context;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.message.MessageConnector;
import com.x.organization.assemble.authentication.jaxrs.authentication.QueueLoginRecord;
import com.x.organization.assemble.authentication.schedule.CleanupBind;
import com.x.organization.assemble.authentication.schedule.CleanupOauthCode;
import com.x.organization.assemble.authentication.schedule.CleanupTokenThreshold;
import com.x.organization.assemble.authentication.schedule.UpdateTokenThresholds;

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
			context.startQueue(queueLoginRecord);
			context.schedule(CleanupBind.class, "0 */15 * * * ?");
			context.schedule(CleanupOauthCode.class, "0 */15 * * * ?");
			MessageConnector.start(context());
			// 如果启用了安全注销需要启动定时任务进行刷新
			if (BooleanUtils.isTrue(Config.person().getEnableSafeLogout())) {
				context.schedule(CleanupTokenThreshold.class, "50 50 6,12,18 * * ?");
				context.scheduleLocal(UpdateTokenThresholds.class, 45, 60 * 30);
			}
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
