package com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Double2DateUtility {
	    public final static SimpleDateFormat yyyyMdHHmmss = new SimpleDateFormat("yyyy-M-d HH:mm:ss");
	    public final static SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    public final static Date date = new Date();
	    public final static long localOffset = date.getTimezoneOffset() * 60000;
	    public final static DecimalFormat df = new DecimalFormat("#.0000000000");

	    public static String getDateByDouble(SimpleDateFormat dateFormat, double num) {
	        Date date = new Date();
	        date.setTime((long) ((num - 25569) * 24 * 3600 * 1000 + localOffset));
	        return dateFormat.format(date);
	    }

	    public static String getDateByString(SimpleDateFormat dateFormat, String num) {
	        double parseDouble = Double.parseDouble(num);
	        return getDateByDouble(dateFormat, parseDouble);
	    }

	    public static double getDoubleByDate(SimpleDateFormat dateFormat, String time) {
	        Date date = null;
	        try {
	            date = dateFormat.parse(time);
	        } catch (ParseException e) {
	            e.printStackTrace();
	        }
	        return getDoubleByDate(date);
	    }

	    public static double getDoubleByDate(Date date) {
	        double dd = (double) (date.getTime() - localOffset) / 24 / 3600 / 1000 + 25569.0000000;
	        return Double.valueOf(df.format(dd));
	    }

	    public static void main(String[] args) {
	        Date now = new Date();
	        double value = getDoubleByDate(now);
	        System.out.println(value);
	        System.out.println(yyyyMMddHHmmss.format(now));
	        System.out.println(getDateByDouble(yyyyMMddHHmmss, value));
	    }
}
