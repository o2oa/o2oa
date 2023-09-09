package test.encrypt;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class TestAESGCMNoPadding {

	public static final String CRYPTOGRAPHIC_ALGORITHM_AES = "AES";
	public static final String CRYPTOGRAPHIC_ALGORITHM_AES_GCM_NOPADDING = "AES/GCM/NoPadding";
	public static final String KEY = "123456789abcdefg";

	public static void main(String[] args) throws Exception {

		String inputFile = "/data/Temp/o2server-8.1.1-linux-x64.zip";
		String encryptedFile = "/data/Temp/en.zip";
		String decryptedFile = "/data/Temp/de.zip";

		try (InputStream input = Files.newInputStream(Paths.get(inputFile));
				OutputStream output = Files.newOutputStream(Paths.get(encryptedFile))) {
			encryptAESGCMNoPadding(input, output);
		}

		try (InputStream input = Files.newInputStream(Paths.get(encryptedFile));
				OutputStream output = Files.newOutputStream(Paths.get(decryptedFile))) {
			decryptAESGCMNoPadding(input, output);
		}

	}

	private static Long encryptAESGCMNoPadding(InputStream inputStream, OutputStream outputStream) throws Exception {
		Cipher cipher = Cipher.getInstance(CRYPTOGRAPHIC_ALGORITHM_AES_GCM_NOPADDING);
		cipher.init(Cipher.ENCRYPT_MODE, computeIfEncryptKeyAbsent());
		byte[] iv = cipher.getIV();
		outputStream.write(iv);
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

	private static Long decryptAESGCMNoPadding(InputStream inputStream, OutputStream outputStream) throws Exception {
		byte[] iv = new byte[12]; // Read the IV from the encrypted stream
		if (12 != inputStream.read(iv)) {
			throw new IllegalStateException("Read the IV from the encrypted stream error.");
		}
		Cipher cipher = Cipher.getInstance(CRYPTOGRAPHIC_ALGORITHM_AES_GCM_NOPADDING);
		GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, iv);
		cipher.init(Cipher.DECRYPT_MODE, computeIfEncryptKeyAbsent(), gcmParameterSpec);
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
