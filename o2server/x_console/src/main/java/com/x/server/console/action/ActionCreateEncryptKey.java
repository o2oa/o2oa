package com.x.server.console.action;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;

public class ActionCreateEncryptKey extends ActionBase {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreateEncryptKey.class);

	private void init() throws Exception {
		// nothing
	}

	public boolean execute() throws Exception {
		this.init();
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		SecureRandom random = new SecureRandom();
		generator.initialize(1024, random);
		KeyPair pair = generator.generateKeyPair();
		File publicKeyFile = new File(Config.base(), "config/public.key");
		File privateKeyFile = new File(Config.base(), "config/private.key");
		FileUtils.write(publicKeyFile, Base64.encodeBase64URLSafeString(pair.getPublic().getEncoded()),
				DefaultCharset.charset, false);

		FileUtils.write(privateKeyFile, Base64.encodeBase64URLSafeString(pair.getPrivate().getEncoded()),
				DefaultCharset.charset, false);

		// 为前端提供publicKey,为密码加密
		this.writeConfigFile(new String(Base64.encodeBase64(pair.getPublic().getEncoded())));

		LOGGER.print(
				"public key: config/public.key, private key: config/private.key, create key success. Please  restart server!");

		return true;
	}

	public boolean writeConfigFile(String publicKey) {
		File dir;
		Gson gson = XGsonBuilder.instance();
		try {
			dir = new File(Config.base(), "servers/webServer/x_desktop/res/config");
			FileUtils.forceMkdir(dir);
			File fileConfig = new File(dir, "config.json");
			String json = FileUtils.readFileToString(fileConfig, StandardCharsets.UTF_8);
			JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
			jsonObject.addProperty("publicKey", publicKey);
			FileUtils.write(fileConfig, jsonObject.toString(), DefaultCharset.charset, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}