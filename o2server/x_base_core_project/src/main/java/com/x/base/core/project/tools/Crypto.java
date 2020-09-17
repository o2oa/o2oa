package com.x.base.core.project.tools;

import java.io.ByteArrayOutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;
import java.util.regex.Matcher;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.script.ScriptContext;
import javax.script.SimpleScriptContext;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import com.x.base.core.project.script.ScriptFactory;

public class Crypto {

	private Crypto() {
	}

	private static final String utf8 = "UTF-8";

	private static final String DES = "DES";

	private static final String RSA = "RSA";

	private static final String NEVERCHANGEKEY = "NEVERCHANGEKEY";

	public static String encrypt(String data, String key) throws Exception {
		byte[] bt = encrypt(data.getBytes(), key.getBytes());
		String str = Base64.encodeBase64URLSafeString(bt);
		return URLEncoder.encode(str, utf8);
	}

	public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
		// 生成一个可信任的随机数源
		SecureRandom sr = new SecureRandom();
		// 从原始密钥数据创建DESKeySpec对象
		DESKeySpec dks = new DESKeySpec(key);
		// 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey securekey = keyFactory.generateSecret(dks);
		// Cipher对象实际完成加密操作
		Cipher cipher = Cipher.getInstance(DES);
		// 用密钥初始化Cipher对象
		cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
		return cipher.doFinal(data);
	}

	public static String decrypt(String data, String key) throws Exception {
		if (StringUtils.isEmpty(data)) {
			return null;
		}
		String str = URLDecoder.decode(data, utf8);
		byte[] buf = Base64.decodeBase64(str);
		byte[] bt = decrypt(buf, key.getBytes());
		return new String(bt);
	}

	public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
		// 生成一个可信任的随机数源
		SecureRandom sr = new SecureRandom();
		// 从原始密钥数据创建DESKeySpec对象
		DESKeySpec dks = new DESKeySpec(key);
		// 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey securekey = keyFactory.generateSecret(dks);
		// Cipher对象实际完成解密操作
		Cipher cipher = Cipher.getInstance(DES);
		// 用密钥初始化Cipher对象
		cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
		return cipher.doFinal(data);
	}

	public static PublicKey rsaPublicKey(String publicKey) throws Exception {
		byte[] keyBytes = Base64.decodeBase64(publicKey);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(RSA);
		return keyFactory.generatePublic(keySpec);
	}

	public static PrivateKey rsaPrivateKey(String privateKey) throws Exception {
		byte[] keyBytes = Base64.decodeBase64(privateKey);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(RSA);
		return keyFactory.generatePrivate(keySpec);
	}

	public static String rsaEncrypt(String content, String publicKey) throws Exception {
		Cipher cipher = Cipher.getInstance(RSA);
		cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey(publicKey));
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			byte[] bytes = content.getBytes();
			for (int i = 0; i < bytes.length; i += 100) {
				baos.write(cipher.doFinal(ArrayUtils.subarray(bytes, i, i + 100)));
			}
			return Base64.encodeBase64URLSafeString(baos.toByteArray());
		}
	}

	public static String rsaDecrypt(String content, String privateKey) throws Exception {
		Cipher cipher = Cipher.getInstance(RSA);
		cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey(privateKey));
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			byte[] bytes = Base64.decodeBase64(content);
			for (int i = 0; i < bytes.length; i += 128) {
				baos.write(cipher.doFinal(ArrayUtils.subarray(bytes, i, i + 128)));
			}
			return new String(baos.toByteArray());
		}
	}

	public static final String TEST_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCWcVZIS57VeOUzi8c01WKvwJK9uRe6hrGTUYmF6J/pI6/UvCbdBWCoErbzsBZOElOH8Sqal3vsNMVLjPYClfoDyYDaUlakP3ldfnXJzAFJVVubF53KadG+fwnh9ZMvxdh7VXVqRL3IQBDwGgzX4rmSK+qkUJjc3OkrNJPB7LLD8QIDAQAB";
	public static final String TEST_PRIVATE_KEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJZxVkhLntV45TOLxzTVYq/Akr25F7qGsZNRiYXon+kjr9S8Jt0FYKgStvOwFk4SU4fxKpqXe+w0xUuM9gKV+gPJgNpSVqQ/eV1+dcnMAUlVW5sXncpp0b5/CeH1ky/F2HtVdWpEvchAEPAaDNfiuZIr6qRQmNzc6Ss0k8HsssPxAgMBAAECgYAWtRy05NUgm5Lc6Og0jVDL/mEnydxPBy2ectwzHh2k7wIHNi8XhUxFki2TMqzrM9Dv3/LySpMl4AE3mhs34LNPy6F+MwyF5X7j+2Y6MflJyeb9HNyT++viysQneoOEiOk3ghxF2/GPjpiEF79wSp+1YKTxRAyq7ypV3t35fGOOEQJBANLDPWl8b5c3lrcz/dTamMjHbVamEyX43yzQOphzkhYsz4pruATzTxU+z8/zPdEqHcWWV39CP3xu3EYNcAhxJW8CQQC2u7PF5Xb1xYRCsmIPssFxil64vvdUadSxl7GLAgjQ9ULyYWB24KObCEzLnPcT8Pf2Q0YQOixxa/78FuzmgbyfAkA7ZFFV/H7lugB6t+f7p24OhkRFep9CwBMD6dnZRBgSr6X8d8ZvfrD2Z7DgBMeSva+OEoOtlNmXExZ3lynO9zN5AkAVczEmIMp3DSl6XtAuAZC9kD2QODJ2QToLYsAfjiyUwsWKCC43piTuVOoW2KUUPSwOR1VZIEsJQWEcHGDQqhgHAkAeZ7a6dVRZFdBwKA0ADjYCufAW2cIYiVDQBJpgB+kiLQflusNOCBK0FT3lg8BdUSy2D253Ih6l3lbaM/4M7DFQ";

	public static String plainText(String text) {
		if (StringUtils.isEmpty(text)) {
			return text;
		}
		try {
			Matcher matcher = StringTools.SCRIPTTEXT_REGEX.matcher(text);
			if (matcher.matches()) {
				String value = StringEscapeUtils.unescapeJson(matcher.group(1));
				if (StringUtils.startsWithIgnoreCase(value, "ENCRYPT:")) {
					String de = StringUtils.substringAfter(value, ":");
					return decrypt(de, NEVERCHANGEKEY);
				} else {
					String eval = ScriptFactory.functionalization(StringEscapeUtils.unescapeJson(value));
					ScriptContext scriptContext = new SimpleScriptContext();
					return Objects.toString(ScriptFactory.scriptEngine.eval(eval, scriptContext));
				}
			} else {
				return text;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String defaultEncrypt(String data) throws Exception {
		byte[] bt = encrypt(data.getBytes(), NEVERCHANGEKEY.getBytes());
		String str = Base64.encodeBase64URLSafeString(bt);
		return URLEncoder.encode(str, utf8);
	}
}
