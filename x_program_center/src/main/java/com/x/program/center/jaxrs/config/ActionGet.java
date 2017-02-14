package com.x.program.center.jaxrs.config;

import org.apache.commons.codec.binary.Base64;

import com.x.base.core.http.WrapOutString;
import com.x.base.core.project.server.Config;
import com.x.base.core.utils.JarTools;

public class ActionGet {
	public WrapOutString execute() throws Exception {
		byte[] bytes = JarTools.jar(Config.base() + "/config");
		WrapOutString wrap = new WrapOutString();
		wrap.setValue(Base64.encodeBase64String(bytes));
		return wrap;
	}
}