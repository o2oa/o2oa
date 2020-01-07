/**
 * zoneland.net Inc.
 * Copyright (c) 2014 All Rights Reserved.
 */
package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;


/**
 * DES加密解密
 * @author FancyLou
 * @version $Id: CryptDES.java, v 0.1 2014年9月5日 上午11:10:02 Exp $
 */
public class CryptDES {
    
    private Cipher encryptCipher = null;
    private Cipher decryptCipher = null;
    
    private static CryptDES des = null;

    public static CryptDES getInstance(String des_key) {
        try {
            DESKeySpec key = new DESKeySpec(des_key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            des = new CryptDES(keyFactory.generateSecret(key));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return des;
    }

    /**
     * Construct a new object which can be utilized to encrypt
     * and decrypt strings using the specified key
     * with a DES encryption algorithm.
     *
     * @param key The secret key used in the crypto operations.
     * @throws Exception If an error occurs.
     *
     */
    private CryptDES(SecretKey key) throws Exception {
        encryptCipher = Cipher.getInstance("DES");
        decryptCipher = Cipher.getInstance("DES");
        encryptCipher.init(Cipher.ENCRYPT_MODE, key);
        decryptCipher.init(Cipher.DECRYPT_MODE, key);
    }    
 
    /**
     * Encrypt a string using DES encryption, and return the encrypted
     * string as a base64 encoded string.
     * @param unencryptedString The string to encrypt.
     * @return String The DES encrypted and base 64 encoded string.
     * @throws Exception If an error occurs.
     */
    public String encryptBase64 (String unencryptedString) throws Exception {
        // Encode the string into bytes using utf-8
        byte[] unencryptedByteArray = unencryptedString.getBytes("UTF8");
        // Encrypt
        byte[] encryptedBytes = encryptCipher.doFinal(unencryptedByteArray);
        // Encode bytes to base64 to get a string
        byte [] encodedBytes = Base64.encode(encryptedBytes, Base64.DEFAULT);
        return new String(encodedBytes);
    }
 
    /**
     * Decrypt a base64 encoded, DES encrypted string and return
     * the unencrypted string.
     * @param encryptedString The base64 encoded string to decrypt.
     * @return String The decrypted string.
     * @throws Exception If an error occurs.
     */
    public String decryptBase64 (String encryptedString) throws Exception {
        // Encode bytes to base64 to get a string
        byte [] decodedBytes = Base64.encode(encryptedString.getBytes(), Base64.DEFAULT);
        // Decrypt
        byte[] unencryptedByteArray = decryptCipher.doFinal(decodedBytes);
        // Decode using utf-8
        return new String(unencryptedByteArray, "UTF8");
    }  



}
