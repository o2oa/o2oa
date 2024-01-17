package com.x.base.core.project.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.graalvm.polyglot.Source;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.scripting.GraalvmScriptingFactory;

public class Crypto {

	private Crypto() {
	}

	private static final String DES = "DES";

	private static final String RSA = "RSA";

	private static final String NEVERCHANGEKEY = "NEVERCHANGEKEY";

	private static Class<?> classSm4 = null;

	private static final String TYPE_AES = "AES";
	private static final String TYPE_SM4 = "SM4";

	private static final Pattern PLAINTEXT_TRANSFORM_REGEX = Pattern.compile("^\\((ENCRYPT:|SCRIPT:)(.+?)\\)$");

	private static final String ENCRYPT_PREFIX = "ENCRYPT:";
	private static final String SCRIPT_PREFIX = "SCRIPT:";

	public static String encrypt(String data, String key) throws Exception {
		return encrypt(data, key, Config.person().getEncryptType());
	}

	public static String encrypt(String data, String key, String type)
			throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, NoSuchMethodException,
			IllegalAccessException, InvocationTargetException, ClassNotFoundException {
		byte[] bt = null;
		if (StringUtils.equalsIgnoreCase(type, TYPE_SM4)) {
			bt = encryptSm4(data.getBytes(StandardCharsets.UTF_8), key);
		} else if (StringUtils.equalsIgnoreCase(type, TYPE_AES)) {
			bt = encryptAes(data.getBytes(), DigestUtils.md5(key));
		} else {
			bt = encrypt(data.getBytes(), key.getBytes());
		}
		String str = Base64.encodeBase64URLSafeString(bt);
		return URLEncoder.encode(str, StandardCharsets.UTF_8.name());
	}

	private static byte[] encrypt(byte[] data, byte[] key) throws InvalidKeyException, NoSuchAlgorithmException,
			InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
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

	private static byte[] encryptAes(byte[] text, byte[] key) throws NoSuchPaddingException, NoSuchAlgorithmException,
			InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

		SecretKeySpec aesKey = new SecretKeySpec(key, TYPE_AES);

		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

		cipher.init(Cipher.ENCRYPT_MODE, aesKey);

		return cipher.doFinal(text);

	}

	private static byte[] decryptAes(byte[] text, byte[] key) throws NoSuchPaddingException, NoSuchAlgorithmException,
			InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

		SecretKeySpec aesKey = new SecretKeySpec(key, TYPE_AES);

		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

		cipher.init(Cipher.DECRYPT_MODE, aesKey);

		return cipher.doFinal(text);

	}

	private static byte[] encryptSm4(byte[] data, String password)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
		return (byte[]) MethodUtils.invokeStaticMethod(getSm4Class(), "encryptMessageBySM4", data, password);
	}

	public static synchronized Class<?> getSm4Class() throws ClassNotFoundException {
		if (null == classSm4) {
			classSm4 = Class.forName("cfca.sadk.util.EncryptUtil");
		}
		return classSm4;
	}

	public static String decrypt(String data, String key) throws Exception {
		return decrypt(data, key, Config.person().getEncryptType());
	}

	public static String decrypt(String data, String key, String type)
			throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, NoSuchMethodException,
			IllegalAccessException, InvocationTargetException, ClassNotFoundException {
		if (StringUtils.isEmpty(data)) {
			return null;
		}
		String str = URLDecoder.decode(data, StandardCharsets.UTF_8.name());
		byte[] buf = Base64.decodeBase64(str);
		byte[] bt = null;
		if (StringUtils.equalsIgnoreCase(type, TYPE_SM4)) {
			bt = decryptSm4(buf, key);
			return new String(bt, StandardCharsets.UTF_8);
		} else if (StringUtils.equalsIgnoreCase(type, TYPE_AES)) {
			bt = decryptAes(buf, DigestUtils.md5(key));
			return new String(bt, StandardCharsets.UTF_8);
		} else {
			bt = decrypt(buf, key.getBytes());
			return new String(bt, StandardCharsets.UTF_8);
		}
	}

	private static byte[] decrypt(byte[] data, byte[] key) throws InvalidKeyException, NoSuchAlgorithmException,
			InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
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

	private static byte[] decryptSm4(byte[] data, String password)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
		return (byte[]) MethodUtils.invokeStaticMethod(getSm4Class(), "decryptMessageBySM4", data, password);
	}

	public static PublicKey rsaPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] keyBytes = Base64.decodeBase64(publicKey);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(RSA);
		return keyFactory.generatePublic(keySpec);
	}

	public static PrivateKey rsaPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] keyBytes = Base64.decodeBase64(privateKey);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(RSA);
		return keyFactory.generatePrivate(keySpec);
	}

	public static String rsaEncrypt(String content, String publicKey)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException,
			IllegalBlockSizeException, BadPaddingException, IOException {
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

	public static String rsaDecrypt(String content, String privateKey)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException,
			IllegalBlockSizeException, BadPaddingException, IOException {
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
		Matcher matcher = PLAINTEXT_TRANSFORM_REGEX.matcher(text);
		if (matcher.matches()) {
			try {
				if (StringUtils.startsWithIgnoreCase(matcher.group(1), ENCRYPT_PREFIX)) {
					return decrypt(matcher.group(2), NEVERCHANGEKEY, null);
				} else if (StringUtils.startsWithIgnoreCase(matcher.group(1), SCRIPT_PREFIX)) {
					Source source = GraalvmScriptingFactory
							.functionalization(StringEscapeUtils.unescapeJson(matcher.group(2)));
					Optional<String> opt = GraalvmScriptingFactory.evalAsString(source, null);
					if (opt.isPresent()) {
						return opt.get();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			return text;
		}
		return null;
	}

	public static String defaultEncrypt(String data)
			throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		byte[] bt = encrypt(data.getBytes(), NEVERCHANGEKEY.getBytes());
		String str = Base64.encodeBase64URLSafeString(bt);
		return URLEncoder.encode(str, StandardCharsets.UTF_8.name());
	}

	public static String formattedDefaultEncrypt(String data)
			throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		return "(" + ENCRYPT_PREFIX + defaultEncrypt(data) + ")";
	}

	public static String base64Encode(String value) {
		return Base64.encodeBase64URLSafeString(value.getBytes(StandardCharsets.UTF_8));
	}

	public static String base64Decode(String value) {
		return new String(Base64.decodeBase64(value), StandardCharsets.UTF_8);
	}

	/**
	 * AES加密
	 *
	 * @param data 明文
	 * @param key  秘钥
	 * @return
	 * @throws Exception
	 */
	public static String encodeAES(String data, String key) throws Exception {

		byte[] keyBytes = DigestUtils.md5(key);

		byte[] passwordBytes = data.getBytes();

		byte[] aesBytes = encryptAes(passwordBytes, keyBytes);

		return new String(Base64.encodeBase64(aesBytes));

	}

	/**
	 * AES解密
	 *
	 * @param data 密文
	 * @param key  秘钥
	 * @return
	 * @throws Exception
	 */
	public static String decodeAES(String data, String key) throws Exception {
		if (StringUtils.isEmpty(data) && StringUtils.isEmpty(key)) {
			return null;
		}

		byte[] keyBytes = DigestUtils.md5(key);

		byte[] debase64Bytes = Base64.decodeBase64(data.getBytes());

		return new String(decryptAes(debase64Bytes, keyBytes));

	}
}
