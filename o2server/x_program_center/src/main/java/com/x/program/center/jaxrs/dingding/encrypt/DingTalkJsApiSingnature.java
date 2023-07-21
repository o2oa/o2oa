package com.x.program.center.jaxrs.dingding.encrypt;

import java.security.MessageDigest;
import java.util.Formatter;

/**
 * Created by fancyLou on 2020-10-26.
 * Copyright © 2020 O2. All rights reserved.
 */
public class DingTalkJsApiSingnature {
    public DingTalkJsApiSingnature() {
    }

    public static String getJsApiSingnature(String url, String nonce, Long timeStamp, String jsTicket) throws DingTalkEncryptException {
        String plainTex = "jsapi_ticket=" + jsTicket + "&noncestr=" + nonce + "&timestamp=" + timeStamp + "&url=" + url;
        System.out.println(plainTex);
        String signature = "";

        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(plainTex.getBytes("UTF-8"));
            signature = byteToHex(crypt.digest());
            return signature;
        } catch (Exception var7) {
            throw new DingTalkEncryptException(900006);
        }
    }

    private static String byteToHex(byte[] hash) {
        Formatter formatter = new Formatter();
        byte[] var2 = hash;
        int var3 = hash.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            byte b = var2[var4];
            formatter.format("%02x", b);
        }

        String result = formatter.toString();
        formatter.close();
        return result;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=" + getJsApiSingnature("http://test4.weixin.wtoip.com/wtoip/dingding/dzmp", "abcde12345", 1463132072L, "M95mXz9E4Wy9LjWBIYZdkXOhM7KRvXr5Cq2Yz521gi7d3rnqEY4JO2FFsjLgJC8b5G7ajnJARnidJVYl4hjaXD"));
        String url = "http://test4.weixin.wtoip.com:80/wtoip/dingding/dzmp";
        String nonce = "abcde12345";
        Long timeStamp = 1463125744L;
        String tikcet = "gUsHOoPPzLVZKVkClnESg88m7qMV4c0Ys9VGsMigqzZU7gA8PeoNzHODmYPZ85TYuoZryXuqEUFlXLN1OPEixm";
        String jsApiSingnature = getJsApiSingnature(url, nonce, timeStamp, tikcet);
        System.err.println(jsApiSingnature + ", equals = " + jsApiSingnature.equals("d14dfc1d0d98cad2438e664723e8a9d8633b443f"));
    }
}
