package com.x.teamwork.assemble.control;

import com.x.base.core.project.Context;
import com.x.teamview.assemble.control.service.SystemConfigPersistService;

public class ThisApplication {
	
	protected static Context context;

	public static Context context() {
		return context;
	}

	public static void init() {
		try {
			new SystemConfigPersistService().initSystemConfig();
			System.out.println("x_teamwork_assemble_control restart completed!");
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
