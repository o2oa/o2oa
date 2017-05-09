package com.x.organization.assemble.control;

import com.x.base.core.project.Context;
import com.x.organization.assemble.control.timer.CheckRoleTask;

public class ThisApplication {

	protected static Context context;

	public static Context context() {
		return context;
	}

	public static void init() {
		try {
			context().timer(new CheckRoleTask(context()), 5);
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
