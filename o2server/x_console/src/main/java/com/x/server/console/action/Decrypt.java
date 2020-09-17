package com.x.server.console.action;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.Crypto;

public class Decrypt {

	private static Logger logger = LoggerFactory.getLogger(Decrypt.class);

	public boolean execute(String text) throws Exception {
		logger.print("decrypt text:{}", Crypto.plainText(text));
		return true;
	}

}