package com.x.report.assemble.control.service;
import java.util.Date;
import java.util.List;

import com.x.base.core.project.http.EffectivePerson;
import com.x.report.assemble.control.schedule.bean.ReportCreateFlag;

/**
 * 汇报时间周期和类别计划服务类
 * @author O2LEE
 *
 */
public class Report_Sv_ReportFlagWeek{
	
	/**
	 * 判断是否需要生成周汇报
	 * @param effectivePerson 
	 * @param reportCreateFlags
	 * @param date 
	 * @return
	 * @throws Exception
	 */
	public List<ReportCreateFlag> composeFlags(EffectivePerson effectivePerson, List<ReportCreateFlag> reportCreateFlags, Date date) throws Exception {
//		String WEEKEND_IGNORE = report_S_SettingServiceAdv.getValueByCode("WEEKEND_IGNORE");
//		String HOLIDAY_IGNORE = report_S_SettingServiceAdv.getValueByCode("HOLIDAY_IGNORE");
//		String REPORT_WEEK_DAYTYPE = report_S_SettingServiceAdv.getValueByCode("REPORT_WEEK_DAYTYPE");
//		String REPORT_WEEK_DAY = report_S_SettingServiceAdv.getValueByCode("REPORT_WEEK_DAY");
//		String REPORT_WEEK_TIME = report_S_SettingServiceAdv.getValueByCode("REPORT_WEEK_TIME");
//		String REPORT_WEEK_MODULE = report_S_SettingServiceAdv.getValueByCode("REPORT_WEEK_MODULE");
		return reportCreateFlags;
	}

}
