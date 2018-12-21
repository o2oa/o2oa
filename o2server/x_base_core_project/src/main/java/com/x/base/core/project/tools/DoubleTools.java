package com.x.base.core.project.tools;

import java.nio.ByteBuffer;

public class DoubleTools {

	public static byte[] doubleToByteArray(double[] doubleArray) {
		int times = Double.SIZE / Byte.SIZE;
		byte[] bytes = new byte[doubleArray.length * times];
		for (int i = 0; i < doubleArray.length; i++) {
			ByteBuffer.wrap(bytes, i * times, times).putDouble(doubleArray[i]);
		}
		return bytes;
	}

	public static double[] byteToDoubleArray(byte[] bytes) {
		int times = Double.SIZE / Byte.SIZE;
		double[] doubles = new double[bytes.length / times];
		for (int i = 0; i < doubles.length; i++) {
			doubles[i] = ByteBuffer.wrap(bytes, i * times, times).getDouble();
		}
		return doubles;
	}

}
