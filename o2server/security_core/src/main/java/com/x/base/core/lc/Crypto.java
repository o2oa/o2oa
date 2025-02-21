package com.x.base.core.lc;

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
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

public class Crypto {

	private Crypto() {
	}

	private static final String RSA = "RSA";
	private static final String TYPE_AES = "AES";
	private static final String AES_TRANSFORMATION = "AES/GCM/NoPadding";
	private static final int AES_TAG_LENGTH = 128;
	private static final int AES_IV_LENGTH = 12;

	public static PrivateKey rsaPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] keyBytes = Base64.decodeBase64(privateKey);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(RSA);
		return keyFactory.generatePrivate(keySpec);
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

	public static String decodeAES(String data, String key)
			throws NoSuchPaddingException, NoSuchAlgorithmException,
			InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException  {
		if (StringUtils.isEmpty(data) && StringUtils.isEmpty(key)) {
			return null;
		}

		byte[] keyBytes = DigestUtils.md5(key);

		byte[] debase64Bytes = Base64.decodeBase64(data);

		return new String(decryptAes(debase64Bytes, keyBytes));

	}
}
