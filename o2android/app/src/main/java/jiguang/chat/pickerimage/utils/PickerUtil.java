package jiguang.chat.pickerimage.utils;

import java.math.BigDecimal;

public class PickerUtil {

	public static String getFileSizeString(long size) {
    	if (size <= 0) {
    		return "0B";
		}
    	
    	// < 1K
    	if (size < 1024) {
            return size + "B";
        }
    	// 1K -- 1M
        else if (size >= 1024 && size < 1048576) {
        	double dout = (size * 1.0) / 1024;
        	BigDecimal bd = new BigDecimal(dout);
        	bd = bd.setScale(0, BigDecimal.ROUND_HALF_UP);
        	return (int)bd.doubleValue() + "K";
        }
    	// > 1M
        else if (size >= 1048576 && size < 1073741824){
        	double dout = (size * 1.0) / 1048576;
        	BigDecimal bd = new BigDecimal(dout);
        	bd = bd.setScale(1, BigDecimal.ROUND_HALF_UP);
        	return bd.doubleValue() + "M";
        }
        else {
        	double dout = (size * 1.0) / 1073741824;
        	BigDecimal bd = new BigDecimal(dout);
        	bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        	return bd.doubleValue() + "GB";
        }
    }
}

