package com.x.program.center.jaxrs.collect;

import com.x.base.core.http.WrapOutBoolean;
import com.x.base.core.http.connection.HttpConnection;

public class ActionCheckConnect {

	public WrapOutBoolean execute() throws Exception {
		WrapOutBoolean wrap = new WrapOutBoolean();
		try {
			wrap.setValue(false);
			String url = "http://collect.xplatform.tech/o2_collect/jaxrs/echo";
			HttpConnection.getAsString(url, null);
			wrap.setValue(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return wrap;
	}

}
