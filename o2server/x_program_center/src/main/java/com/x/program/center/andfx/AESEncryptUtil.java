package com.x.program.center.andfx;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class AESEncryptUtil {
    private static final String ALGORITHM = "AES";
    private static volatile Map<String, Cipher> encryptMap = new HashMap<>();
    private static volatile Map<String, Cipher> decryptMap = new HashMap<>();

    public static String encryptToBase64(String appSecret, String text){
        try {
            Cipher cipher = encryptMap.get(appSecret);
            if (cipher == null) {
                synchronized (appSecret) {
                    cipher = encryptMap.get(appSecret);
                    if (cipher == null) {
                        byte[] keyBytes = appSecret.getBytes(StandardCharsets.UTF_8);
                        SecretKey secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
                        cipher = Cipher.getInstance(ALGORITHM);
                        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                        encryptMap.put(appSecret, cipher);
                    }
                }
            }
            byte[] encryptedData = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedData);
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public static String decryptFromBase64(String appSecret, String base64){
        try {
            Cipher cipher = decryptMap.get(appSecret);
            if (cipher == null) {
                synchronized (appSecret) {
                    cipher = decryptMap.get(appSecret);
                    if (cipher == null) {
                        byte[] keyBytes = appSecret.getBytes(StandardCharsets.UTF_8);
                        SecretKey secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
                        cipher = Cipher.getInstance(ALGORITHM);
                        cipher.init(Cipher.DECRYPT_MODE, secretKey);
                        decryptMap.put(appSecret, cipher);
                    }
                }
            }
            byte[] data = cipher.doFinal(Base64.getDecoder().decode(base64));
            return new String(data, StandardCharsets.UTF_8);
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }
}
