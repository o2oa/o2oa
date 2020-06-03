package com.x.server.console.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
		
		//为前端提供publicKey,为密码加密
		this.writeConfigFile(new String(Base64.encodeBase64(pair.getPublic().getEncoded())));
		
		return true;
	}
	
	public static void main(String[] args) throws Exception {
		ActionCreateEncryptKey actionCreateEncryptKey = new ActionCreateEncryptKey();
		actionCreateEncryptKey.writeConfigFile("ssxx");
	}

	public  boolean writeConfigFile(String publicKey) {
	    File dir;
		StringBuffer stringBuffer = new StringBuffer();
		try {
			dir = new File(Config.base(), "servers/webServer/x_desktop/res/config");
			FileUtils.forceMkdir(dir);
			File fileConfig = new File(dir, "config.json");
			
			BufferedReader bufferedReader = 
					new BufferedReader(new InputStreamReader(new FileInputStream(fileConfig), "UTF-8"));
			String line;
			while((line=bufferedReader.readLine()) != null) {
				stringBuffer.append(line);
			}
			
			JsonObject jsonObject = (JsonObject) new JsonParser().parse(stringBuffer.toString());
			jsonObject.addProperty("publicKey", publicKey);

			 FileUtils.write(fileConfig, jsonObject.toString(),DefaultCharset.charset, false);
					
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	   return true;
  }
}