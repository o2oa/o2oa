package com.x.program.center.jaxrs.dingding.encrypt;

import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Created by fancyLou on 2020-10-26.
 * Copyright © 2020 O2. All rights reserved.
 */
public class PKCS7Padding {
    private static final Charset CHARSET = Charset.forName("utf-8");
    private static final int BLOCK_SIZE = 32;

    public PKCS7Padding() {
    }

    public static byte[] getPaddingBytes(int count) {
        int amountToPad = 32 - count % 32;
        if (amountToPad == 0) {
            amountToPad = 32;
        }

        char padChr = chr(amountToPad);
        String tmp = new String();

        for(int index = 0; index < amountToPad; ++index) {
            tmp = tmp + padChr;
        }

        return tmp.getBytes(CHARSET);
    }

    public static byte[] removePaddingBytes(byte[] decrypted) {
        int pad = decrypted[decrypted.length - 1];
        if (pad < 1 || pad > 32) {
            pad = 0;
        }

        return Arrays.copyOfRange(decrypted, 0, decrypted.length - pad);
    }

    private static char chr(int a) {
        byte target = (byte)(a & 255);
        return (char)target;
    }
}
