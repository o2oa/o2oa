package com.x.base.core.project.tools;

import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.apache.commons.codec.binary.Base64;

public class ByteTools {

	public static String compressBase64String(byte[] data) throws Exception {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream(data.length)) {
			Deflater compressor = new Deflater();
			compressor.setLevel(Deflater.BEST_COMPRESSION);
			compressor.setInput(data);
			compressor.finish();
			byte[] buf = new byte[1024];
			while (!compressor.finished()) {
				int count = compressor.deflate(buf);
				baos.write(buf, 0, count);
			}
			return Base64.encodeBase64String(baos.toByteArray());
		}
	}

	public static byte[] decompressBase64String(String base64String) throws Exception {
		byte[] data = Base64.decodeBase64(base64String);
		Inflater inflater = new Inflater();
		inflater.setInput(data);
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length)) {
			byte[] buffer = new byte[1024];
			while (!inflater.finished()) {
				int count = inflater.inflate(buffer);
				outputStream.write(buffer, 0, count);
			}
			byte[] output = outputStream.toByteArray();
			return output;
		}
	}

}
