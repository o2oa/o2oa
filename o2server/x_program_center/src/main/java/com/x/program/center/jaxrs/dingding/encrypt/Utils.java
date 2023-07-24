package com.x.program.center.jaxrs.dingding.encrypt;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Created by fancyLou on 2020-10-26. Copyright © 2020 O2. All rights reserved.
 */
public class Utils {

	private static final Random random = new SecureRandom();

	private Utils() {
		// nothing
	}

	public static String getRandomStr(int count) {
		String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < count; ++i) {
			sb.append(base.charAt(random.nextInt(base.length())));
		}

		return sb.toString();
	}

	public static byte[] int2Bytes(int count) {
		return new byte[] { (byte) (count >> 24 & 255), (byte) (count >> 16 & 255), (byte) (count >> 8 & 255),
				(byte) (count & 255) };
	}

	public static int bytes2int(byte[] byteArr) {
		int count = 0;

		for (int i = 0; i < 4; ++i) {
			count <<= 8;
			count |= byteArr[i] & 255;
		}

		return count;
	}
}
