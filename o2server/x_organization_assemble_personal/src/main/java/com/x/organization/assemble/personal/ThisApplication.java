package com.x.organization.assemble.personal;

import com.x.base.core.project.Context;
import com.x.organization.assemble.personal.schedule.DisableExpiredEmpower;

public class ThisApplication {

	protected static Context context;
	public static final int passwordStrengthLevel = 4;

	public static Context context() {
		return context;
	}

	public static void init() {
		try {
			context.schedule(DisableExpiredEmpower.class, "0 */20 * * * ?");
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
