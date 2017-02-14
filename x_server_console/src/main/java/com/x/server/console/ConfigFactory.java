package com.x.server.console;

import java.io.File;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.http.WrapOutString;
import com.x.base.core.project.AbstractThisApplication;
import com.x.base.core.project.server.Config;
import com.x.base.core.project.server.Node;
import com.x.base.core.utils.JarTools;

public class ConfigFactory {

	private static Logger logger = LoggerFactory.getLogger(ConfigFactory.class);

	public static void sync() throws Exception {
		Node node = Config.currentNode();
		if (BooleanUtils.isNotTrue(node.getIsPrimaryCenter())) {
			logger.info("{} is not primary center. sync config from primary center {}.", Config.node(),
					Config.nodes().primaryCenterNode());
			syncFromPrimaryCenter();
			Config.flush();
		}
	}

	private static void syncFromPrimaryCenter() throws Exception {
		WrapOutString wrap = AbstractThisApplication.getFromCenter("config", WrapOutString.class);
		byte[] bytes = Base64.decodeBase64(wrap.getValue());
		File dist = new File(Config.base(), "config");
		JarTools.unjar(bytes, "", dist, true);
	}

}
