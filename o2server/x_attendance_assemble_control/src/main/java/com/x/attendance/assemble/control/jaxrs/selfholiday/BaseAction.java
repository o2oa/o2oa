package com.x.attendance.assemble.control.jaxrs.selfholiday;

import com.x.attendance.assemble.control.service.AttendanceDetailAnalyseServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceSelfHolidayServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceStatisticalCycleServiceAdv;
import com.x.attendance.assemble.control.service.UserManagerService;
import com.x.attendance.entity.AttendanceSelfHoliday;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;

public class BaseAction extends StandardJaxrsAction{
	protected Cache.CacheCategory cache = new Cache.CacheCategory( AttendanceSelfHoliday.class);
	protected UserManagerService userManagerService = new UserManagerService();
	protected AttendanceStatisticalCycleServiceAdv attendanceStatisticCycleServiceAdv = new AttendanceStatisticalCycleServiceAdv();
	protected AttendanceDetailAnalyseServiceAdv attendanceDetailAnalyseServiceAdv = new AttendanceDetailAnalyseServiceAdv();
	protected AttendanceSelfHolidayServiceAdv attendanceSelfHolidayServiceAdv = new AttendanceSelfHolidayServiceAdv();
}
