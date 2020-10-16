package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils;


import android.util.Base64;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * Created by fancyLou on 2020-09-27.
 * Copyright © 2020 O2. All rights reserved.
 */
public class CryptRSA {
    /**RSA算法*/
    private static final String RSA = "RSA";

    private static KeyFactory keyFactory;
    static {
        //实例化密钥工厂
        try {
            keyFactory = KeyFactory.getInstance(RSA);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * 公钥加密
     *
     * @param content   待加密数据
     * @param publicKey 密钥
     * @return string 加密后数据
     */
    public static String rsaEncryptByPublicKey(String content, String publicKey) throws Exception {
        byte[] keyBytes = Base64.decode(publicKey, Base64.NO_WRAP);
        //初始化公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        //产生公钥
        PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
        //数据加密
        //PKCS1Padding
        Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return new String(Base64.encode(cipher.doFinal(content.trim().getBytes("utf-8")), Base64.NO_WRAP));

    }


}
