package com.x.mind.assemble.control;

import com.x.base.core.project.Context;

public class ThisApplication{
	protected static Context context;
	
	public static Context context() {
		return context;
	}
	
	public static void init() {
	}

	public static void destroy() {
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
