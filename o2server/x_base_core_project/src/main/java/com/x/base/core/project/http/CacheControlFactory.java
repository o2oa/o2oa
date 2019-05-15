package com.x.base.core.project.http;

import javax.ws.rs.core.CacheControl;

public class CacheControlFactory {
	public static CacheControl getDefault() {
		CacheControl cc = new CacheControl();
		cc.setNoCache(true);
		return cc;
	}

	public static CacheControl getMaxAge(Integer max) {
		CacheControl cc = new CacheControl();
		cc.setMaxAge(max);
		return cc;
	}

}
