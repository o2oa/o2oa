package com.x.organization.assemble.authentication;

import com.x.base.core.project.Context;
import com.x.organization.assemble.authentication.jaxrs.authentication.QueueLoginRecord;
import com.x.organization.assemble.authentication.schedule.CleanupBind;

public class ThisApplication {

	public static QueueLoginRecord queueLoginRecord;

	protected static Context context;

	public static Context context() {
		return context;
	}

	public static void init() {
		try {
			queueLoginRecord = new QueueLoginRecord();
			context().startQueue(queueLoginRecord);
			context().schedule(CleanupBind.class, "0 */30 * * * ?");
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
