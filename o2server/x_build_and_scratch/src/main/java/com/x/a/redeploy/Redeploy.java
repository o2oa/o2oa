package com.x.a.redeploy;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.x.base.core.project.AssembleA;
import com.x.base.core.project.CoreA;
import com.x.base.core.project.ServiceA;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.Crypto;
import com.x.base.core.project.tools.DateTools;

public class Redeploy {

	public static final String DEFAULT_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCWcVZIS57VeOUzi8c01WKvwJK9uRe6hrGTUYmF6J/pI6/UvCbdBWCoErbzsBZOElOH8Sqal3vsNMVLjPYClfoDyYDaUlakP3ldfnXJzAFJVVubF53KadG+fwnh9ZMvxdh7VXVqRL3IQBDwGgzX4rmSK+qkUJjc3OkrNJPB7LLD8QIDAQAB";
	public static final String DEFAULT_PRIVATE_KEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJZxVkhLntV45TOLxzTVYq/Akr25F7qGsZNRiYXon+kjr9S8Jt0FYKgStvOwFk4SU4fxKpqXe+w0xUuM9gKV+gPJgNpSVqQ/eV1+dcnMAUlVW5sXncpp0b5/CeH1ky/F2HtVdWpEvchAEPAaDNfiuZIr6qRQmNzc6Ss0k8HsssPxAgMBAAECgYAWtRy05NUgm5Lc6Og0jVDL/mEnydxPBy2ectwzHh2k7wIHNi8XhUxFki2TMqzrM9Dv3/LySpMl4AE3mhs34LNPy6F+MwyF5X7j+2Y6MflJyeb9HNyT++viysQneoOEiOk3ghxF2/GPjpiEF79wSp+1YKTxRAyq7ypV3t35fGOOEQJBANLDPWl8b5c3lrcz/dTamMjHbVamEyX43yzQOphzkhYsz4pruATzTxU+z8/zPdEqHcWWV39CP3xu3EYNcAhxJW8CQQC2u7PF5Xb1xYRCsmIPssFxil64vvdUadSxl7GLAgjQ9ULyYWB24KObCEzLnPcT8Pf2Q0YQOixxa/78FuzmgbyfAkA7ZFFV/H7lugB6t+f7p24OhkRFep9CwBMD6dnZRBgSr6X8d8ZvfrD2Z7DgBMeSva+OEoOtlNmXExZ3lynO9zN5AkAVczEmIMp3DSl6XtAuAZC9kD2QODJ2QToLYsAfjiyUwsWKCC43piTuVOoW2KUUPSwOR1VZIEsJQWEcHGDQqhgHAkAeZ7a6dVRZFdBwKA0ADjYCufAW2cIYiVDQBJpgB+kiLQflusNOCBK0FT3lg8BdUSy2D253Ih6l3lbaM/4M7DFQ";

	public static String redeploy(String server, Integer port, Class<?> cls, String publicKey) {
		String result = "";
		try {
			File file = null;
			if (AssembleA.class.isAssignableFrom(cls)) {
				file = new File("D:/O2/code/store", cls.getSimpleName() + ".war");
			} else if (CoreA.class.isAssignableFrom(cls)) {
				file = new File("D:/O2/code/store/jars", cls.getSimpleName() + ".jar");
			} else if (ServiceA.class.isAssignableFrom(cls)) {
				file = new File("D:/O2/code/store", cls.getSimpleName() + ".war");
			} else {
				throw new Exception("not defined class.");
			}
			byte[] bytes = FileUtils.readFileToByteArray(file);
			CommandObject cmd = new CommandObject();
			cmd.setCommand("redeploy:" + cls.getSimpleName());
			cmd.setBody(Base64.encodeBase64String(bytes));
			if (StringUtils.isNotEmpty(publicKey)) {
				cmd.setCredential(Crypto.rsaEncrypt("o2@" + DateTools.format(new Date()), publicKey));
			}
			String json = XGsonBuilder.toJson(cmd);
			try (Socket client = new Socket(server, port)) {
				try (OutputStream outputStream = client.getOutputStream();
						InputStream inputStream = client.getInputStream()) {
					IOUtils.write(json, outputStream);
					client.shutdownOutput();
					result = IOUtils.toString(inputStream);
					client.shutdownInput();
				}
			}
		} catch (Exception e) {
			result = e.getMessage();
			e.printStackTrace();
		}
		return result;
	}

	public static class CommandObject {

		private String command;

		private String body;

		private String credential;

		public String getCommand() {
			return command;
		}

		public void setCommand(String command) {
			this.command = command;
		}

		public String getBody() {
			return body;
		}

		public void setBody(String body) {
			this.body = body;
		}

		public String getCredential() {
			return credential;
		}

		public void setCredential(String credential) {
			this.credential = credential;
		}

	}

	@Test
	public void testRsaEncrypt() throws Exception {
		File file = new File("D:/O2/code/store/x_query_assemble_surface.war");
		byte[] bytes = FileUtils.readFileToByteArray(file);
		CommandObject cmd = new CommandObject();
		cmd.setCommand("redeploy:x_query_assemble_surface");
		cmd.setBody(Base64.encodeBase64URLSafeString(bytes));
		String json = XGsonBuilder.toJson(cmd);
		System.out.println(json);
		String en = Crypto.rsaEncrypt(json, DEFAULT_PUBLIC_KEY);
		System.out.println(en);
		String de = Crypto.rsaDecrypt(en, DEFAULT_PRIVATE_KEY);
		System.out.println(de);
	}

}
