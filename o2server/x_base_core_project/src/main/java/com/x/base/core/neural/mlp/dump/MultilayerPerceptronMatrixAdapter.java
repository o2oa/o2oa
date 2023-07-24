package com.x.base.core.neural.mlp.dump;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.apache.commons.codec.binary.Base64;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.x.base.core.neural.mlp.matrix.MultilayerPerceptronMatrix;
import com.x.base.core.neural.tools.FP16;

public class MultilayerPerceptronMatrixAdapter
		implements JsonSerializer<MultilayerPerceptronMatrix>, JsonDeserializer<MultilayerPerceptronMatrix> {

	private static final String PROPERTY_ROW = "row";
	private static final String PROPERTY_COLUMN = "column";
	private static final String PROPERTY_DATA = "data";

	@Override
	public JsonElement serialize(MultilayerPerceptronMatrix matrix, Type typeOfSrc, JsonSerializationContext context) {
		float[] arr = new float[matrix.column() * matrix.row()];
		final JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(PROPERTY_ROW, matrix.row());
		jsonObject.addProperty(PROPERTY_COLUMN, matrix.column());
		matrix.visit((r, c, v) -> arr[r * matrix.column() + c] = v);
		// byte[] bs = floatToFloatBytes(arr);
		byte[] bs = floatToHalfPrecisionBytes(arr);
		try {
			bs = compress(bs, Deflater.BEST_COMPRESSION, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		jsonObject.addProperty(PROPERTY_DATA, Base64.encodeBase64URLSafeString(bs));
		return jsonObject;
	}

	@Override
	public MultilayerPerceptronMatrix deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject jsonObject = json.getAsJsonObject();
		int row = jsonObject.get(PROPERTY_ROW).getAsInt();
		int column = jsonObject.get(PROPERTY_COLUMN).getAsInt();
		MultilayerPerceptronMatrix matrix = new MultilayerPerceptronMatrix(row, column);
		byte[] bs = Base64.decodeBase64(jsonObject.get(PROPERTY_DATA).getAsString());
		try {
			bs = decompress(bs, true);
		} catch (DataFormatException e) {
			e.printStackTrace();
		}
		float[] arr = floatFromHalfPrecisionBytes(bs);
		matrix.visit((r, c, v) -> matrix.set(r, c, arr[r * matrix.column() + c]));
		return matrix;
	}

	private byte[] floatToHalfPrecisionBytes(float[] arr) {
		ByteBuffer buffer = ByteBuffer.allocate(arr.length * 2);
		for (int i = 0; i < arr.length; i++) {
			buffer.putShort(i * 2, FP16.toHalf(arr[i]));
		}
		return buffer.array();
	}

	private float[] floatFromHalfPrecisionBytes(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		int size = bytes.length / 2;
		float[] values = new float[size];
		for (int i = 0; i < size; i++) {
			values[i] = FP16.toFloat(buffer.getShort(i * 2));
		}
		return values;
	}

	private byte[] floatToFloatBytes(float[] arr) {
		ByteBuffer buffer = ByteBuffer.allocate(arr.length * 4);
		for (int i = 0; i < arr.length; i++) {
			buffer.putFloat(i * 4, arr[i]);
		}
		return buffer.array();
	}

	private float[] floatFormFloatBytes(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		int size = bytes.length / 4;
		float[] values = new float[size];
		for (int i = 0; i < size; i++) {
			values[i] = buffer.getFloat(i * 4);
		}
		return values;
	}

	public static byte[] compress(byte[] input, int compressionLevel, boolean gzip) throws IOException {
		Deflater compressor = new Deflater(compressionLevel, gzip);
		compressor.setInput(input);
		compressor.finish();
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		byte[] readBuffer = new byte[1024];
		int readCount = 0;
		while (!compressor.finished()) {
			readCount = compressor.deflate(readBuffer);
			if (readCount > 0) {
				bao.write(readBuffer, 0, readCount);
			}
		}
		compressor.end();
		return bao.toByteArray();
	}

	public static byte[] decompress(byte[] input, boolean gzip) throws DataFormatException {
		Inflater decompressor = new Inflater(gzip);
		decompressor.setInput(input);
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		byte[] readBuffer = new byte[1024];
		int readCount = 0;
		while (!decompressor.finished()) {
			readCount = decompressor.inflate(readBuffer);
			if (readCount > 0) {
				bao.write(readBuffer, 0, readCount);
			}
		}
		decompressor.end();
		return bao.toByteArray();
	}
}
