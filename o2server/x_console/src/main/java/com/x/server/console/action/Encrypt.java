package com.x.server.console.action;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.Crypto;

public class Encrypt {

	private static Logger logger = LoggerFactory.getLogger(Encrypt.class);

	public boolean execute(String text) throws Exception {
		logger.print("encrypt text:(ENCRYPT:{})", Crypto.defaultEncrypt(text));
		return true;
	}

}