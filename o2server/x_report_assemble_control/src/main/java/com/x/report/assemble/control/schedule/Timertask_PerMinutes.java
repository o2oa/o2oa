package com.x.report.assemble.control.schedule;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.report.assemble.control.EnumReportTypes;
import com.x.report.assemble.control.service.Report_Caculater_MonthReportTime;
import com.x.report.assemble.control.service.Report_P_ProfileServiceAdv;
import com.x.report.assemble.control.service.Report_S_SettingServiceAdv;
import com.x.report.assemble.control.service.Report_Sv_ReportCreator;

/**
 * 定时代理: 定期根据系统配置生成汇报信息
 * 
 * @author O2LEE
 *
 */
public class Timertask_PerMinutes implements Job {

	private Logger logger = LoggerFactory.getLogger(Timertask_PerMinutes.class);
	private Report_P_ProfileServiceAdv report_P_ProfileServiceAdv = new Report_P_ProfileServiceAdv();
	private Report_Caculater_MonthReportTime report_Caculater_MonthReportTime = new Report_Caculater_MonthReportTime();
	private Report_S_SettingServiceAdv report_S_SettingServiceAdv = new Report_S_SettingServiceAdv();

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		tryToCreateReport();
	}

	/**
	 * 系统尝试判断汇报发起时间
	 */
	private void tryToCreateReport() {
		// 获取上一次成功发起汇报的时间
		Date lastReportTime = null;
		Date nextReportTime = null;
		String AUTOCREATE_TYPE = null;
		try {
			// NONE|EXPRESSION|CUSTOMDATELIST
			AUTOCREATE_TYPE = report_S_SettingServiceAdv.getValueByCode("AUTOCREATE_TYPE");
			if (!"EXPRESSION".equals(AUTOCREATE_TYPE) && !"AUTOCREATE_TYPE".equals(AUTOCREATE_TYPE)) {
				logger.info(">>>>>>>>>>>>未获取到合法的自动汇报时间计算类别。");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		try {
			lastReportTime = report_P_ProfileServiceAdv.getMaxCreateTime(EnumReportTypes.MONTHREPORT.toString());

			// 以最后一次发起时间为基准，去计算下一次发起时间
			nextReportTime = report_Caculater_MonthReportTime.getNextReportTime(EnumReportTypes.MONTHREPORT.toString(),
					lastReportTime);

			// logger.info( "每月汇报最后一次发起时间：" + lastReportTime + ", 下一次发起时间：" + nextReportTime
			// +", 当前时间：" + new Date() );
			// 判断当前时间是否已经超过了下一次发起时间，如果超过了，就尝试发起汇报
			if (nextReportTime != null && new Date().after(nextReportTime)) {
				new Report_Sv_ReportCreator().create(nextReportTime);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}