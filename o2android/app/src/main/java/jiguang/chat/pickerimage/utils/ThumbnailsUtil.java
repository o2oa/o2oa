package jiguang.chat.pickerimage.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.io.File;
import java.util.HashMap;

public class ThumbnailsUtil {
	
	@SuppressLint("UseSparseArrays")
	private static HashMap<Integer,String> hash = new HashMap<Integer, String>();
	
	public static String getThumbnailWithImageID(int key, String defalt){
		if(hash == null || !hash.containsKey(key)) {
			return defalt;
		}
		
		try{
			String thumbFilePath = hash.get(key);
			if(TextUtils.isEmpty(thumbFilePath)){
				return defalt;
			}
			String thumbPath = thumbFilePath.substring("file://".length());
			File file = new File(thumbPath);
			if(file.exists()){
				return thumbFilePath;
			}
		}catch(Exception e){}

		return defalt;
	}
	
	public static void put(Integer key, String value){
		hash.put(key, value);
	}
	
	public static void clear(){
		hash.clear();
	}
}
