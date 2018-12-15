package com.x.base.core.project.test;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

public class CryptoTest {

	@Test
	public void test() throws Exception {
		System.out.println(CryptoClass.encrypt("password", "12345678"));
	}

	@Test
	public void test1() throws Exception {
		System.out.println(CryptoClass.decrypt("imXg6AUytfr+uVm31GQvyw==", "12345678"));
	}

	// 加密
	public static String EncryptTest(String sSrc, String sKey) throws Exception {
		if (sKey == null) {
			System.out.print("Key为空null");
			return null;
		}
		// // 判断Key是否为16位
		// if (sKey.length() != 16) {
		// System.out.print("Key长度不是16位");
		// return null;
		// }
		byte[] raw = sKey.getBytes("utf-8");
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");// "算法/模式/补码方式"
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
		return Base64.encodeBase64URLSafeString(encrypted);// 此处使用BASE64做转码功能，同时能起到2次加密的作用。
	}

}
