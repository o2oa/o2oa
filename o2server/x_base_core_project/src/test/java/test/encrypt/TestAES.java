package test.encrypt;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestAES {

	public static final String CRYPTOGRAPHIC_ALGORITHM_AES = "AES";
	public static final String KEY = "123456789abcdefg";
	public static final String inputFile = "/data/Temp/1_9.txt";
	public static final String encryptedFile = "/data/Temp/1_9_aes.data";
	public static final String decryptedFile = "/data/Temp/1_9_aes.txt";

	@Test
	void encrypt() throws Exception {
		long length = 0;
		try (InputStream input = Files.newInputStream(Paths.get(inputFile));
				OutputStream output = Files.newOutputStream(Paths.get(encryptedFile))) {
			length = encryptAES(input, output);
		}
		Assertions.assertTrue(length > 0);
	}

	@Test
	void decrypt() throws Exception {
		long length = 0;
		try (InputStream input = Files.newInputStream(Paths.get(encryptedFile));
				OutputStream output = Files.newOutputStream(Paths.get(decryptedFile))) {
			length = decryptAES(input, output);
		}
		Assertions.assertTrue(length > 0);
	}

	private static Long encryptAES(InputStream inputStream, OutputStream outputStream) throws Exception {
		Cipher cipher = Cipher.getInstance(CRYPTOGRAPHIC_ALGORITHM_AES);
		cipher.init(Cipher.ENCRYPT_MODE, computeIfEncryptKeyAbsent());
		try (CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher)) {
			byte[] buffer = new byte[4096];
			int bytesRead;
			long length = 0L;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				cipherOutputStream.write(buffer, 0, bytesRead);
				length += bytesRead;
			}
			return length;
		}
	}

	private static Long decryptAES(InputStream inputStream, OutputStream outputStream) throws Exception {
		Cipher cipher = Cipher.getInstance(CRYPTOGRAPHIC_ALGORITHM_AES);
		cipher.init(Cipher.DECRYPT_MODE, computeIfEncryptKeyAbsent());
		try (CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher)) {
			byte[] buffer = new byte[4096];
			int bytesRead;
			long length = 0L;
			while ((bytesRead = cipherInputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
				length += bytesRead;
			}
			return length;
		}
	}

	private static SecretKey computeIfEncryptKeyAbsent() {
		return new SecretKeySpec(KEY.getBytes(), CRYPTOGRAPHIC_ALGORITHM_AES);
	}

}
