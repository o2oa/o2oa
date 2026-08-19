package com.x.base.core.project.tools;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.scripting.GraalvmScriptingFactory;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
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
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.graalvm.polyglot.Source;

public class Crypto {

	private Crypto() {
	}

	private static final String DES = "DES";

	private static final String RSA = "RSA";

	private static final String NEVERCHANGEKEY = "NEVERCHANGEKEY";

	private static Class<?> classSm4 = null;

	private static final String TYPE_AES = "AES";
	private static final String AES_TRANSFORMATION = "AES/GCM/NoPadding";
	private static final int AES_TAG_LENGTH = 128;
	private static final int AES_IV_LENGTH = 12;
	public static final String DESCRIBE_AES_KEY = "98b25ee736a4db745e9b66fe46274fd8";

	private static final String TYPE_SM4 = "SM4";

	private static final Pattern PLAINTEXT_TRANSFORM_REGEX = Pattern.compile("^\\((ENCRYPT:|SCRIPT:)(.+?)\\)$");

	private static final String ENCRYPT_PREFIX = "ENCRYPT:";
	private static final String SCRIPT_PREFIX = "SCRIPT:";

	public static final String NODE_PUBLIC_KEY = "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAk0E6jkkPoYCW6ORjvP+V8GICc9865mCwW89Rc7/sfB9B3WdYaQL1y5AI2GBJzrSGUfBXbJIGJ1ulDNnzSsPaCP/3ijY9BJWv8drbBjKB/z4IqTNaM5qQKVmcafKQXp+6OTciu8m9uscKZzD2a76gRlUmcsCAMeR5YqJaIdN8ahYaeYq4mPfaN7pbEId1+aD/g6YSNzOIrb5ub4odJAIP0TPiJyF5srjbgzmUSy5rs6UUJX+tdxm05BGXBVbQ2lwllYe2gbOKTkV7QVhApXrLfCCBu0RmurlN7jRjBnqsKRl45fSYaCrSoaxxNOy83Ud0NEsRBVICrAY6tpMCsHlizWDdSsE8ELzYxF80Cfpplf9d/OSMzXGSIgtUQ+xlLGLXEJ9w0rUr3jchXLx0mzVsBFa4BdpSSvkPoXr1g/dVrk1MN1IlxZGxb0NgsUnA3TRz0UuKvXm1psQSx6NEidr6PBJC0JAYGmda91v4j0k8mj9yzAqKhmN8jauR0RmtkefXGzj6QzXEbpg9tkH2NCmksdqkErAHn+NNHRvOy1lH7FRFeQS1H0530HC4aZzW6IDatYBv7vOSmhDeEZPEjKOf5iBrPxMLhcX0SbKVqwFiSXMIYpT4cTGl3uNG9nng4fxF/E3c+0ZjpNWJlvT+Rsp5mEkJMA4cC/GnjsNs2BeirSECAwEAAQ==";
	public static final String NODE_PRIVATE_KEY = "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";

	public static String encrypt(String data, String key) throws Exception {
		return encrypt(data, key, Config.person().getEncryptType());
	}

	public static String encrypt(String data, String key, String type)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, ClassNotFoundException, InvalidAlgorithmParameterException {
		byte[] bt = null;
		if (Strings.CI.equals(type, TYPE_SM4)) {
			bt = encryptSm4(data.getBytes(StandardCharsets.UTF_8), key);
		} else {
			bt = encryptAes(data.getBytes(StandardCharsets.UTF_8), DigestUtils.md5(key));
		}
		String str = Base64.encodeBase64URLSafeString(bt);
		return URLEncoder.encode(str, StandardCharsets.UTF_8);
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

	private static byte[] encryptAes(byte[] text, byte[] key)
			throws NoSuchPaddingException, NoSuchAlgorithmException,
			InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {

		SecretKeySpec aesKey = new SecretKeySpec(key, TYPE_AES);

		Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
		byte[] iv = new byte[AES_IV_LENGTH];
		new SecureRandom().nextBytes(iv);
		GCMParameterSpec gcmSpec = new GCMParameterSpec(AES_TAG_LENGTH, iv);

		cipher.init(Cipher.ENCRYPT_MODE, aesKey, gcmSpec);
		byte[] encryptedData = cipher.doFinal(text);

		// 将IV与密文拼接在一起，以便在解密时使用
		byte[] encryptedDataWithIv = new byte[iv.length + encryptedData.length];
		System.arraycopy(iv, 0, encryptedDataWithIv, 0, iv.length);
		System.arraycopy(encryptedData, 0, encryptedDataWithIv, iv.length, encryptedData.length);

		return encryptedDataWithIv;
	}

	private static byte[] decryptAes(byte[] text, byte[] key)
			throws NoSuchPaddingException, NoSuchAlgorithmException,
			InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
		SecretKeySpec aesKey = new SecretKeySpec(key, TYPE_AES);

		Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
		byte[] iv = new byte[AES_IV_LENGTH];
		byte[] encryptedData = new byte[text.length - AES_IV_LENGTH];
		System.arraycopy(text, 0, iv, 0, iv.length);
		System.arraycopy(text, iv.length, encryptedData, 0, encryptedData.length);
		GCMParameterSpec gcmSpec = new GCMParameterSpec(AES_TAG_LENGTH, iv);

		cipher.init(Cipher.DECRYPT_MODE, aesKey, gcmSpec);

		return cipher.doFinal(encryptedData);
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
            throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, ClassNotFoundException, InvalidAlgorithmParameterException {
		if (StringUtils.isEmpty(data)) {
			return null;
		}
		String str = URLDecoder.decode(data, StandardCharsets.UTF_8);
		byte[] buf = Base64.decodeBase64(str);
		byte[] bt;
		if (Strings.CI.equals(type, TYPE_SM4)) {
			bt = decryptSm4(buf, key);
        } else {
			bt = decryptAes(buf, DigestUtils.md5(key));
        }
        return new String(bt, StandardCharsets.UTF_8);
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
			IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance(RSA);
		cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey(publicKey));
		byte[] encryptedBytes = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
		return Base64.encodeBase64URLSafeString(encryptedBytes);
	}

	public static String rsaDecrypt(String content, String privateKey)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException,
			IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance(RSA);
		cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey(privateKey));
		byte[] decodedBytes = Base64.decodeBase64(content);
		byte[] decryptedBytes = cipher.doFinal(decodedBytes);
		return new String(decryptedBytes, StandardCharsets.UTF_8);
	}

	public static String plainText(String text) {
		if (StringUtils.isEmpty(text)) {
			return text;
		}
		Matcher matcher = PLAINTEXT_TRANSFORM_REGEX.matcher(text);
		if (matcher.matches()) {
			try {
				if (Strings.CI.equals(matcher.group(1), ENCRYPT_PREFIX)) {
					return decodeDES(matcher.group(2), NEVERCHANGEKEY);
				} else if (Strings.CI.equals(matcher.group(1), SCRIPT_PREFIX)) {
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
			IllegalBlockSizeException, BadPaddingException {
		byte[] bt = encrypt(data.getBytes(), NEVERCHANGEKEY.getBytes());
		String str = Base64.encodeBase64URLSafeString(bt);
		return URLEncoder.encode(str, StandardCharsets.UTF_8);
	}

	public static String formattedDefaultEncrypt(String data)
			throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException {
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

		byte[] keyBytes = key.length()<16 ? DigestUtils.md5(key) : key.getBytes(StandardCharsets.UTF_8);

		byte[] passwordBytes = data.getBytes(StandardCharsets.UTF_8);

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

		byte[] keyBytes = key.length()<16 ? DigestUtils.md5(key) : key.getBytes(StandardCharsets.UTF_8);

		byte[] debase64Bytes = Base64.decodeBase64(data.getBytes(StandardCharsets.UTF_8));

		return new String(decryptAes(debase64Bytes, keyBytes));

	}

	public static String encodeDES(String data, String key) throws Exception {
		byte[] bt = encrypt(data.getBytes(StandardCharsets.UTF_8), key.getBytes());
		String str = Base64.encodeBase64URLSafeString(bt);
		return URLEncoder.encode(str, StandardCharsets.UTF_8);
	}

	public static String decodeDES(String data, String key) {
		try {
			String str = URLDecoder.decode(data, StandardCharsets.UTF_8);
			byte[] bt = decrypt(Base64.decodeBase64(str), key.getBytes());
			return new String(bt, StandardCharsets.UTF_8);
		} catch (Exception e) {
			return "";
		}
	}
}
