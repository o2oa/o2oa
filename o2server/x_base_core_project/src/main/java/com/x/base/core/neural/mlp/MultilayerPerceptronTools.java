package com.x.base.core.neural.mlp;

import java.security.SecureRandom;
import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class MultilayerPerceptronTools {

	private static final Random random = new SecureRandom();

	public static final float DELTA = 1e-10f;

	private MultilayerPerceptronTools() {
		// nothing
	}

	public static int argmax(float[] arr) {
		float max = arr[0];
		int idx = 0;
		for (int i = 1; i < arr.length; i++) {
			if (arr[i] > max) {
				max = arr[i];
				idx = i;
			}
		}
		return idx;
	}

	public static float[] normalize0to1(final float[] sample) {
		// (bla - min(bla)) / ( max(bla) - min(bla) )
		float max = NumberUtils.max(sample);
		float min = NumberUtils.min(sample);
		float total = max - min;
		float[] result = new float[sample.length];
		if (total <= 0) {
			for (int i = 0; i < sample.length; i++) {
				result[i] = 1f;
			}
		} else {
			for (int i = 0; i < sample.length; i++) {
				result[i] = (sample[i] - min) / total;
			}
		}
		return result;
	}

	public static int[] uniqueRandomInts(int count, int randomNumberOrigin, int randomNumberBound) {
		int s = randomNumberBound - randomNumberOrigin;
		int[] arr = new int[s];
		for (int i = 0; i < s; i++) {
			arr[i] = randomNumberOrigin + i;
		}
		ArrayUtils.shuffle(arr, random);
		return ArrayUtils.subarray(arr, 0, count);
	}

	public static float sigmoid(float v) {
		return 1f / (1f + (float) Math.exp(-v));
	}

}
