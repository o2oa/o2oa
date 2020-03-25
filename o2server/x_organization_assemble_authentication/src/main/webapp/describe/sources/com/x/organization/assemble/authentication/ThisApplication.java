package com.x.organization.assemble.authentication;

import com.x.base.core.project.Context;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.authentication.jaxrs.authentication.QueueLoginRecord;
import com.x.organization.assemble.authentication.schedule.CleanupBind;
import com.x.organization.assemble.authentication.schedule.CleanupOauthCode;

public class ThisApplication {

	public static QueueLoginRecord queueLoginRecord;

	protected static Context context;

	public static Context context() {
		return context;
	}

	public static void init() {
		try {
			LoggerFactory.setLevel(Config.logLevel().x_organization_assemble_authentication());
			queueLoginRecord = new QueueLoginRecord();
			context.startQueue(queueLoginRecord);
			context.schedule(CleanupBind.class, "0 */15 * * * ?");
			context.schedule(CleanupOauthCode.class, "0 */15 * * * ?");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void destroy() {
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
