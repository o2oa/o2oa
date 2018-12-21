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
public class Report_Sv_ReportFlagDay{

	/**
	 * 判断是否需要生成日汇报
	 * @param effectivePerson 
	 * @param reportCreateFlags
	 * @param date 
	 * @return
	 * @throws Exception
	 */
	public List<ReportCreateFlag> composeFlags(EffectivePerson effectivePerson, List<ReportCreateFlag> reportCreateFlags, Date date) throws Exception {
//		String WEEKEND_IGNORE = report_S_SettingServiceAdv.getValueByCode("WEEKEND_IGNORE");
//		String HOLIDAY_IGNORE = report_S_SettingServiceAdv.getValueByCode("HOLIDAY_IGNORE");
//		String REPORT_DAY_MODULE = report_S_SettingServiceAdv.getValueByCode("REPORT_DAY_MODULE");
//		String REPORT_DAY_DAYTYPE = report_S_SettingServiceAdv.getValueByCode("REPORT_DAY_DAYTYPE");
//		String REPORT_DAY_TIME = report_S_SettingServiceAdv.getValueByCode("REPORT_DAY_TIME");
		return reportCreateFlags;
	}

}
