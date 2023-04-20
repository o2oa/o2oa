package com.x.base.core.project.tools;

import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @author sword
 * @date 2023/04/06 13:50
 **/
public class ShaTools {

    private static final Logger logger = LoggerFactory.getLogger(ShaTools.class);

    public static String getToken(String key, String secret){
        long timestamp = System.currentTimeMillis();
        String header = Base64.getEncoder().encodeToString("{\"alg\": \"SHA256\"}".getBytes(StandardCharsets.UTF_8));

        String load = Base64.getEncoder().encodeToString(("{\"key\": \"" + key + "\", \"timestamp\": " + timestamp + "}").getBytes(StandardCharsets.UTF_8));

        String signature = SHA256(header + load + secret);
        return header + "." + load + "." + signature;
    }

    public static String SHA256(String str) {
        MessageDigest messageDigest;
        String encodeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes(StandardCharsets.UTF_8));
            encodeStr = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException ex) {
            logger.error(ex);
        }
        return encodeStr;
    }

    private static String byte2Hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        String temp;
        for (byte aByte : bytes) {
            temp = Integer.toHexString(aByte & 0xFF);
            if (temp.length() == 1) {
                // 1得到一位的进行补0操作
                sb.append("0");
            }
            sb.append(temp);
        }
        return sb.toString();
    }

    /**
     * HMACSHA256 加密算法
     *
     * @param data 加密字符串
     * @param key  密钥
     * @return 加密结果字符串
     */
    public static String HMACSHA256(String data, String key) {
        StringBuilder sb = new StringBuilder();
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] array = sha256_HMAC.doFinal(data.getBytes("UTF-8"));
            for (byte item : array) {
                sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
            }
        } catch (Exception ex) {
            logger.error(ex);
        }
        return sb.toString();
    }

    public static void main(String[] args) throws Exception{
        Map<String, Object> map = new HashMap<>();
        map.put("roomName", "测试会议0419001");
        map.put("verifyMode", "1");
        map.put("maxUserCount", 300);
        String url = "https://117.133.7.109:8443/api/v1/room/addRoomInfo";
        String key = "4QY08Kyh";
        String secret = "HpQi5csSMrufkM)b&#YWVlr7o*wWUG3G";
        String token = getToken(key, secret);
        System.out.println("认证token:"+token);
        List<NameValuePair> header = new ArrayList<>();
        header.add(new NameValuePair("Authorization", getToken(key, secret)));
        SslTools.ignoreSsl();
        String res = HttpConnection.postAsString(url, header, XGsonBuilder.toJson(map));
        System.out.println(res);
    }
}
