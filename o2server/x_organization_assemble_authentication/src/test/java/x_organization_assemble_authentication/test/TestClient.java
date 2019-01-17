package x_organization_assemble_authentication.test;

import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

public class TestClient {

	public static String encrypt(String data, String key) throws Exception {
		byte[] bt = encrypt(data.getBytes(), key.getBytes());
		String str = Base64.encodeBase64URLSafeString(bt);
		return URLEncoder.encode(str, "UTF-8");
	}

	public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
		// 生成一个可信任的随机数源
		SecureRandom sr = new SecureRandom();
		// 从原始密钥数据创建DESKeySpec对象
		DESKeySpec dks = new DESKeySpec(key);
		// 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey securekey = keyFactory.generateSecret(dks);
		// Cipher对象实际完成加密操作
		Cipher cipher = Cipher.getInstance("DES");
		// 用密钥初始化Cipher对象
		cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
		return cipher.doFinal(data);
	}

	@Test
	public void test() throws Exception {
		String text = "张三" + "#" + (new Date()).getTime();
		String key = "12345678";
		System.out.println(encrypt(text, key));
	}

}
