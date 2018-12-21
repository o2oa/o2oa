package com.x.organization.assemble.personal;

import com.x.base.core.project.Context;

public class ThisApplication {

	protected static Context context;
	public static final int passwordStrengthLevel = 4;

	public static Context context() {
		return context;
	}

	public static void init() {
		try {
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
