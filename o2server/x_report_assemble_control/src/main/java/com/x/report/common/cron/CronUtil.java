package com.x.report.common.cron;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.quartz.CronExpression;

import com.x.report.common.date.DateOperation;

/**
 * 时间表过式工具类
 * @author O2LEE
 *
 */
public class CronUtil {
	
	private static DateOperation dateOperation =  new DateOperation();
	
    /**
     * 根据时间表达式，列示最近的maxCount个时间
     * @param beginDate
     * @param maxCount
     * @param expression
     * @param style
     * @return
     * @throws Exception
     */
    public static List<String> listDateWithCron( Date beginDate, Integer maxCount, String expression, String style ) throws Exception {
    	CronExpression cron = new CronExpression(expression);
    	if ( !CronExpression.isValidExpression(expression) ) {
    		throw new Exception("expression is valid!expression:" + expression);
    	}
    	List<String> dateList = new ArrayList<>();
    	Date lastRun = new Date();
    	for ( int i = 0; i < 10; i++ ) {
	    	lastRun = cron.getNextValidTimeAfter(lastRun);
	    	dateList.add(dateOperation.getDateFromDate(lastRun, style));
    	}
    	return dateList;
    }
    
    /**
     * 根据时间表达式，列示最近的maxCount个时间
     * @param beginDate
     * @param maxCount
     * @param expression
     * @param style
     * @return
     * @throws Exception
     */
    public static List<Date> listDateWithCron( Date beginDate, Integer maxCount, String expression ) throws Exception {
    	CronExpression cron = new CronExpression(expression);
    	if ( !CronExpression.isValidExpression(expression) ) {
    		throw new Exception("expression is valid!expression:" + expression);
    	}
    	List<Date> dateList = new ArrayList<>();
    	Date lastRun = new Date();
    	for (int i = 0; i < 10; i++) {
	    	lastRun = cron.getNextValidTimeAfter(lastRun);
	    	dateList.add(lastRun);
    	}
    	return dateList;
    }
    
    /**
     * 根据开始时间，获取下一个满足表达式的时间
     * @param beginDate
     * @param expression
     * @param style
     * @return
     * @throws Exception
     */
    public static String nextDate( Date beginDate, String expression, String style ) throws Exception {
    	CronExpression cron = new CronExpression(expression);
    	if ( !CronExpression.isValidExpression(expression) ) {
    		throw new Exception("expression is valid!expression:" + expression);
    	}
    	return dateOperation.getDateFromDate(cron.getNextValidTimeAfter(beginDate), style);
    }
    
    /**
     * 根据开始时间，获取下一个满足表达式的时间
     * @param beginDate
     * @param expression
     * @param style
     * @return
     * @throws Exception
     */
    public static Date nextDate( Date beginDate, String expression ) throws Exception {
    	CronExpression cron = new CronExpression(expression);
    	if ( !CronExpression.isValidExpression(expression) ) {
    		throw new Exception("expression is valid!expression:" + expression);
    	}
    	if( beginDate == null ) {
    		beginDate = new Date();
    	}
    	return cron.getNextValidTimeAfter(beginDate);
    }
    
}
