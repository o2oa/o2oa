package com.x.report.assemble.control.service;
import java.util.Date;
import java.util.List;

import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.report.assemble.control.EnumReportTypes;
import com.x.report.assemble.control.schedule.bean.ReportCreateFlag;
import com.x.report.assemble.control.schedule.exception.ExceptionConfigGetValue;
import com.x.report.common.date.DateOperation;
import com.x.report.core.entity.Report_P_Profile;

/**
 * 汇报时间周期和类别计划服务类
 * @author O2LEE
 *
 */
public class Report_Sv_ReportFlagMonth{

	private Report_S_SettingServiceAdv report_S_SettingServiceAdv = new Report_S_SettingServiceAdv();
	private Report_P_ProfileServiceAdv report_P_ProfileServiceAdv = new Report_P_ProfileServiceAdv();
	private DateOperation dateOperation = new DateOperation();
	private Logger logger = LoggerFactory.getLogger(Report_Sv_ReportFlagMonth.class);	
	
	/**
	 * 判断是否需要生成月汇报
	 * @param effectivePerson
	 * @param reportCreateFlags
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public List<ReportCreateFlag> composeFlags( EffectivePerson effectivePerson, List<ReportCreateFlag> reportCreateFlags, Date nextMonthReportTime ) throws Exception {
		String REPORT_MONTH_DAYTYPE = null;
		String REPORT_MONTH_MODULE = null;
		String next_reportYear = null;
		String next_reportMonth = null;
		List<Report_P_Profile> createRecordList = null;
		
		try {
			REPORT_MONTH_DAYTYPE = report_S_SettingServiceAdv.getValueByCode("REPORT_MONTH_DAYTYPE");
		} catch (Exception e) {
			Exception exception = new ExceptionConfigGetValue( e, "REPORT_MONTH_DAYTYPE" );
			logger.error( exception );
			throw e;
		}
		
		try {
			REPORT_MONTH_MODULE = report_S_SettingServiceAdv.getValueByCode( "REPORT_MONTH_MODULE" );
		} catch (Exception e) {
			Exception exception = new ExceptionConfigGetValue( e, "REPORT_MONTH_MODULE" );
			logger.error( exception );
			throw e;
		}
		
		if( REPORT_MONTH_MODULE == null || REPORT_MONTH_MODULE.trim().isEmpty() || "NONE".equalsIgnoreCase( REPORT_MONTH_MODULE.trim() ) ) {
			logger.info( ">>>>>>>>>>>没有模块开启月度汇报功能。");
			return reportCreateFlags;
		}
		
		//根据配置判断月报发起时间
		//===================================================================================
		//1、判断月报是否已经发起，如果已经成功发起，不做操作，并且更新下一次月报发起时间
		//2、如果月报未发起，那么组织一个创建信息对象添加到reportCreateFlags
		//===================================================================================		
		//发起时间已经确定了，根据配置判断需要发起的汇报的年份和月份
		if( "THIS_MONTH".equalsIgnoreCase( REPORT_MONTH_DAYTYPE )) {
			//汇报的是当月的工作
			next_reportYear = dateOperation.getYear( nextMonthReportTime );
			next_reportMonth = dateOperation.getMonth( nextMonthReportTime );
		}else if( "NEXT_MONTH".equalsIgnoreCase( REPORT_MONTH_DAYTYPE )) {
			//汇报是上个月的工作
			nextMonthReportTime = dateOperation.getFirstDayInMonth( nextMonthReportTime );
			nextMonthReportTime = dateOperation.getMonthAddDate( nextMonthReportTime, -1 );
			next_reportYear = dateOperation.getYear( nextMonthReportTime );
			next_reportMonth = dateOperation.getMonth( nextMonthReportTime );
		}
		
		//查询当年当月，月汇报是否已经发起过了
		logger.info( ">>>>>>>>>>>查询"+next_reportYear+"年"+next_reportMonth+"月，月汇报是否已经发起过了......");
		
		createRecordList = report_P_ProfileServiceAdv.listWithCondition( EnumReportTypes.MONTHREPORT.toString(), next_reportYear, next_reportMonth, null, null );
		if( createRecordList == null || createRecordList.isEmpty() ) {//未发起过，需要发起一次
			logger.info( ">>>>>>>>>>>系统尝试发起[" +next_reportYear+ "年" +next_reportMonth+ "月]汇报......");
			ReportCreateFlag reportCreateFlag = new ReportCreateFlag();
			reportCreateFlag.setReportType( EnumReportTypes.MONTHREPORT );
			reportCreateFlag.setReportYear( next_reportYear );
			reportCreateFlag.setReportMonth( next_reportMonth );
			reportCreateFlag.setReport_modules( REPORT_MONTH_MODULE );
			reportCreateFlag.setSendDate( nextMonthReportTime );
			reportCreateFlags.add( reportCreateFlag );
		}else {
			logger.info( ">>>>>>>>>>>" +next_reportYear+ "年" +next_reportMonth+ "月已经存在汇报，不需要再次发起汇报。");
		}
		return reportCreateFlags;
	}
}
