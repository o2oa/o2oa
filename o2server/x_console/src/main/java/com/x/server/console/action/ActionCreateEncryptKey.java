package com.x.server.console.action;

import java.io.File;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;

public class ActionCreateEncryptKey extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionCreateEncryptKey.class);

	private Date start;

	private void init() throws Exception {
		this.start = new Date();
	}

	public boolean execute(String password) throws Exception {
		this.init();
		if (!StringUtils.equals(Config.token().getPassword(), password)) {
			logger.print("password not match.");
			return false;
		}
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		generator.initialize(1024);
		KeyPair pair = generator.generateKeyPair();
		File publicKeyFile = new File(Config.base(), "config/public.key");
		File privateKeyFile = new File(Config.base(), "config/private.key");
		FileUtils.write(publicKeyFile, Base64.encodeBase64URLSafeString(pair.getPublic().getEncoded()),
				DefaultCharset.charset, false);
		FileUtils.write(privateKeyFile, Base64.encodeBase64URLSafeString(pair.getPrivate().getEncoded()),
				DefaultCharset.charset, false);
		System.out.println("public key: config/public.key, private key: config/private.key, create key success!");
		return true;
	}

}