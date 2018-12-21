package com.x.report.assemble.control.service;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;
import com.x.report.common.cron.CronUtil;
import com.x.report.common.date.DateOperation;

/**
 * 月度汇报时间周期和类别计划服务类
 * @author O2LEE
 *
 */
public class Report_Caculater_MonthReportTime{
	private static Logger logger = LoggerFactory.getLogger(Report_Caculater_MonthReportTime.class);
	private Report_S_SettingServiceAdv report_S_SettingServiceAdv = new Report_S_SettingServiceAdv();
	private DateOperation dateOperation = new DateOperation();
	
	/**
	 * 根据当前日期，计算下一次汇报生成的日期。
	 * 有可能是当月，也有可能是次月，根据配置计算时间。
	 * @param effectivePerson
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public Date caculateNextMonthReportTime( EffectivePerson effectivePerson, Date date ) throws Exception {
		//WEEKEND_IGNORE, 是否自动忽略周末：可选值为true|false，单值。此属性控制系统是否生成汇报时自动避开周末，生成时间顺延到下一个工作日.
		//String WEEKEND_IGNORE = report_S_SettingServiceAdv.getValueByCode("WEEKEND_IGNORE");
		//HOLIDAY_IGNORE, 是否忽略法定节假日：可选值为true|false，单值。此属性控制系统是否生成汇报时自动避开周末法定节假日，生成时间顺延到下一个工作日.
		//String HOLIDAY_IGNORE = report_S_SettingServiceAdv.getValueByCode("HOLIDAY_IGNORE");
		//REPORT_MONTH_DAYTYPE, 月度汇报发起周期类别：可选值：THIS_MONTH|NEXT_MONTH。此配置控制月度汇报发起的时机，是当月发起，还是次月发起。
		String REPORT_MONTH_DAYTYPE = report_S_SettingServiceAdv.getValueByCode("REPORT_MONTH_DAYTYPE");
		//REPORT_MONTH_DAY, 每月汇报发起日期：可选值：月份中的第几日。此配置控制月度汇报发起的时间。
		String REPORT_MONTH_DAY = report_S_SettingServiceAdv.getValueByCode("REPORT_MONTH_DAY");
		//REPORT_MONTH_TIME, 每月汇报发起时间：可选值为一天中的任何时间。该配置控制月度汇报发起的具体时间点。
		String REPORT_MONTH_TIME = report_S_SettingServiceAdv.getValueByCode("REPORT_MONTH_TIME");
		
		if( REPORT_MONTH_DAYTYPE == null || REPORT_MONTH_DAYTYPE.isEmpty() || "NONE".equals( REPORT_MONTH_DAYTYPE ) ) {
			throw new Exception("未正确获取月度汇报发起周期类别：REPORT_MONTH_DAYTYPE:" + REPORT_MONTH_DAYTYPE);
		}
		if( REPORT_MONTH_DAY == null || REPORT_MONTH_DAY.isEmpty() || "NONE".equals( REPORT_MONTH_DAY ) ) {
			throw new Exception("未正确获取月度汇报发起日期：REPORT_MONTH_DAY:" + REPORT_MONTH_DAY );
		}
		if( REPORT_MONTH_TIME == null || REPORT_MONTH_TIME.isEmpty() || "NONE".equals( REPORT_MONTH_TIME )) {
			throw new Exception("未正确获取月度汇报发起时间：REPORT_MONTH_TIME:" + REPORT_MONTH_TIME );
		}
		
		if( date == null ) {
			date = new Date();
		}
		String reportDateString = null;
		Calendar cal = Calendar.getInstance();
		cal.setTime( date );
		
		if( "THIS_MONTH".equalsIgnoreCase( REPORT_MONTH_DAYTYPE )) {
			//当月发起
			int reportYear = dateOperation.getYearNumber( cal.getTime() );
			int reportMonth = dateOperation.getMonthNumber( cal.getTime() );
			
			int reportDay = Integer.parseInt( REPORT_MONTH_DAY );
			
			if( reportDay > dateOperation.getMaxDayByYearMonth( reportYear, reportMonth )) {
				reportDay = dateOperation.getMaxDayByYearMonth( reportYear, reportMonth );
			}
			
			cal.set( reportYear, reportMonth - 1, reportDay );
			reportDateString = dateOperation.getDateFromDate( cal.getTime(), "yyyy-MM-dd") + " " + REPORT_MONTH_TIME.trim();
			return dateOperation.getDateFromString( reportDateString );
		}else if( "NEXT_MONTH".equalsIgnoreCase( REPORT_MONTH_DAYTYPE )) {
			//下月发起
			cal.add( Calendar.MONTH, 1 );
			int reportYear = dateOperation.getYearNumber( cal.getTime() );
			int reportMonth = dateOperation.getMonthNumber( cal.getTime() );
			int reportDay = Integer.parseInt( REPORT_MONTH_DAY );
			
			if( reportDay > dateOperation.getMaxDayByYearMonth( reportYear, reportMonth )) {
				reportDay = dateOperation.getMaxDayByYearMonth( reportYear, reportMonth );
			}
			
			cal.set( reportYear, reportMonth - 1, reportDay );
			reportDateString = dateOperation.getDateFromDate( cal.getTime(), "yyyy-MM-dd") + " " + REPORT_MONTH_TIME.trim();
			return dateOperation.getDateFromString( reportDateString );
		}
		return null;
	}

	/**
	 * 根据上次发起时间，计算下一次发起时间
	 * 1、判断配置，是按表达式计算时间，还是自定义时间
	 * 2、推算下一次发起时间并且返回
	 * @param string
	 * @param lastReportTime
	 * @return
	 * @throws Exception 
	 */
	public Date getNextReportTime(String reportType, Date lastReportTime) throws Exception {
		//定时生成类别。此属性控制系统定时生成组织汇报的时间控制方式，不自动启动、依据时间表达式或者自定义启动时间列表
		String AUTOCREATE_TYPE =null;
		//时间表达式。此属性控制系统定时生成启动时间，时间表达式。
		String CRON_EXPRESSION = null;
		//自定义时间列表。此属性控制系统定时生成启动时间，自定义。
		String CUSTOM_DATELIST = null;
		try {
			//NONE|EXPRESSION|CUSTOMDATELIST
			AUTOCREATE_TYPE = report_S_SettingServiceAdv.getValueByCode("AUTOCREATE_TYPE");
			if( !"EXPRESSION".equals( AUTOCREATE_TYPE ) && !"AUTOCREATE_TYPE".equals( AUTOCREATE_TYPE ) ) {
				//定时类别不合法
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("未正确获取配置信息：AUTOCREATE_TYPE");
		}
		if( "EXPRESSION".equals( AUTOCREATE_TYPE )) {
			try {
				CRON_EXPRESSION = report_S_SettingServiceAdv.getValueByCode("CRON_EXPRESSION");
				logger.info("配置的发起时间表达示：CRON_EXPRESSION=" + CRON_EXPRESSION );
				return CronUtil.nextDate( lastReportTime, CRON_EXPRESSION );
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception("未正确获取配置信息：CRON_EXPRESSION");
			}
		}else if("CUSTOMDATELIST".equals( AUTOCREATE_TYPE )){
			try {
				CUSTOM_DATELIST = report_S_SettingServiceAdv.getValueByCode("CUSTOM_DATELIST");
				return caculateNextReportTimeWithCustomDateList( lastReportTime, CUSTOM_DATELIST );
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception("未正确获取配置信息：CUSTOM_DATELIST");
			}
		}
		return null;
	}

	/**
	 * 根据CUSTOM_DATELIST，获取晚于lastReportTime最近的一个日期，有可能没有
	 * @param lastReportTime
	 * @param CUSTOM_DATELIST: 多个时间，以逗号(",")分隔
	 * @return
	 * @throws Exception 
	 */
	private Date caculateNextReportTimeWithCustomDateList( Date lastReportTime, String CUSTOM_DATELIST ) throws Exception {
		if( StringUtils.isEmpty( CUSTOM_DATELIST )) {
			return null;
		}
		List<Date> dateList = new ArrayList<>();
		String[] dateStringArray = CUSTOM_DATELIST.split( "," );
		for( String str : dateStringArray ) {
			try {
				dateList.add( dateOperation.getDateFromString(str));
			} catch (Exception e) {
				throw new Exception("date string is valid! date:"+str );
			}
		}
		if( ListTools.isNotEmpty( dateList )) {
			SortTools.asc( dateList );
			for( Date date : dateList ) {
				if( date.after( lastReportTime )) {
					return date;
				}
			}
		}
		return null;
	}
}
