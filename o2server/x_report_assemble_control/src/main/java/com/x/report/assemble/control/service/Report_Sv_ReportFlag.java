package com.x.report.assemble.control.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.report.assemble.control.schedule.bean.ReportCreateFlag;
import com.x.report.assemble.control.schedule.exception.ExceptionComposeReportCreateFlag;
import com.x.report.assemble.control.schedule.exception.ExceptionConfigGetValue;

/**
 * 汇报时间周期和类别计划服务类
 * @author O2LEE
 *
 */
public class Report_Sv_ReportFlag{

	private Report_S_SettingServiceAdv report_S_SettingServiceAdv = new Report_S_SettingServiceAdv();
	private Report_Sv_ReportFlagMonth report_Sv_ReportFlagMonth = new Report_Sv_ReportFlagMonth();
	private Logger logger = LoggerFactory.getLogger(Report_Sv_ReportFlag.class);	
	
	/**
	 * 查询需要汇报的信息列表：汇报类别，汇报周期信息<br/>
	 * 1、获取汇报的相关配置信息<br/>
	 * 2、判断系统是否开启了各种汇报功能<br/>
	 * @param effectivePerson
	 * @param date
	 * @return
	 */
	public List<ReportCreateFlag> getFlags( EffectivePerson effectivePerson, Date date ) {
		
		List<ReportCreateFlag> reportCreateFlags = new ArrayList<>();;
		//获取汇报的相关配置信息
		String MONTHREPORT_ENABLE = null;
		try {
			MONTHREPORT_ENABLE = report_S_SettingServiceAdv.getValueByCode("MONTHREPORT_ENABLE");
		} catch (Exception e) {
			Exception exception = new ExceptionConfigGetValue( e, "MONTHREPORT_ENABLE" );
			logger.error( exception );
			e.printStackTrace();
		}
		//1、判断系统是否开启了月度汇报汇报功能
		if( MONTHREPORT_ENABLE != null && MONTHREPORT_ENABLE.trim().toUpperCase().equals( "TRUE" )) {
			try {
				reportCreateFlags = report_Sv_ReportFlagMonth.composeFlags( effectivePerson, reportCreateFlags, date );
			} catch (Exception e) {
				Exception exception = new ExceptionComposeReportCreateFlag( e, "组织月度汇报的时间点信息时发生异常" );
				logger.error( exception );
				e.printStackTrace();
			}
		}else {
			logger.info( ">>>>>>>>>>>>月度汇报汇报功能未开启");
		}
		return reportCreateFlags;
	}
}
