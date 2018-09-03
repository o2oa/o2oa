package com.x.cms.assemble.search;

import com.x.base.core.project.Context;

public class ThisApplication {

	protected static Context context;
	public static final String ROLE_CMSManager = "CMSManager@CMSManagerSystemRole@R";
	
	public static Context context() {
		return context;
	}
	
	public static void init() throws Exception {
	}

	public static void destroy() {
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
