package jiguang.chat.pickerimage.utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

public class MD5 {
	
	public static String getStringMD5(String value) {
        if (value == null || value.trim().length() < 1) {
            return null;
        }
        try {
            return getMD5(value.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static String getMD5(byte[] source) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            return HexDump.toHex(md5.digest(source));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    public static String getStreamMD5(String filePath) {
    	String hash=null;
    	byte[] buffer = new byte[4096];
    	BufferedInputStream in=null;
    	try  
    	{  
    		MessageDigest md5 = MessageDigest.getInstance("MD5");
    		in = new BufferedInputStream(new FileInputStream(filePath));
    		int numRead = 0;    
    		while ((numRead = in.read(buffer)) > 0) {    
    			md5.update(buffer, 0, numRead);    
    		}    
    		in.close();   
    		hash = HexDump.toHex(md5.digest());    
    	} catch (Exception e) {
    		e.printStackTrace();  
    	} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
    	}
    	return hash;
    }  
}
