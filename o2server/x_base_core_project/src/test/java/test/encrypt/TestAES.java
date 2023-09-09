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

public class TestAES {

	public static final String CRYPTOGRAPHIC_ALGORITHM_AES = "AES";
	public static final String KEY = "123456789abcdefg";

	public static void main(String[] args) throws Exception {

		String inputFile = "/data/Temp/o2server-8.1.1-linux-x64.zip";
		String encryptedFile = "/data/Temp/en.zip";
		String decryptedFile = "/data/Temp/de.zip";

		try (InputStream input = Files.newInputStream(Paths.get(inputFile));
				OutputStream output = Files.newOutputStream(Paths.get(encryptedFile))) {
			encryptAES(input, output);
		}

		try (InputStream input = Files.newInputStream(Paths.get(encryptedFile));
				OutputStream output = Files.newOutputStream(Paths.get(decryptedFile))) {
			decryptAES(input, output);
		}

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
