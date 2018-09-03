package com.o2platform.common.date;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateOperation {
	private static SimpleDateFormat format_year = new SimpleDateFormat( "yyyy" );
	private static SimpleDateFormat format_month = new SimpleDateFormat( "MM" );
	private static SimpleDateFormat format_day = new SimpleDateFormat( "dd" );
	
	public static String getYear( Date date ) {
		if( date == null ){
			date = new Date();
		}
		return format_year.format( date );
	}
	
	public static String getMonth( Date date ) {
		if( date == null ){
			date = new Date();
		}
		return format_month.format( date );
	}
	
	public static String getDay( Date date ) {
		if( date == null ){
			date = new Date();
		}
		return format_day.format( date );
	}
}
