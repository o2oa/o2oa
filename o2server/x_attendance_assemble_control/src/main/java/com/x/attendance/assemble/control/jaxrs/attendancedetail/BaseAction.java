package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.attendance.assemble.control.service.AttendanceDetailAnalyseServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceDetailMobileAnalyseServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceDetailServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceEmployeeConfigServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceScheduleSettingServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceSelfHolidayServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceStatisticalCycleServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceWorkDayConfigServiceAdv;
import com.x.attendance.assemble.control.service.UserManagerService;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;

public class BaseAction extends StandardJaxrsAction{
	
	protected UserManagerService userManagerService = new UserManagerService();
	protected AttendanceDetailAnalyseServiceAdv attendanceDetailAnalyseServiceAdv = new AttendanceDetailAnalyseServiceAdv();
	protected AttendanceWorkDayConfigServiceAdv attendanceWorkDayConfigServiceAdv = new AttendanceWorkDayConfigServiceAdv();
	protected AttendanceStatisticalCycleServiceAdv attendanceStatisticCycleServiceAdv = new AttendanceStatisticalCycleServiceAdv();
	protected AttendanceDetailServiceAdv attendanceDetailServiceAdv = new AttendanceDetailServiceAdv();
	protected AttendanceEmployeeConfigServiceAdv attendanceEmployeeConfigServiceAdv = new AttendanceEmployeeConfigServiceAdv();
	protected AttendanceSelfHolidayServiceAdv attendanceSelfHolidayServiceAdv = new AttendanceSelfHolidayServiceAdv();
	protected AttendanceScheduleSettingServiceAdv attendanceScheduleSettingServiceAdv = new AttendanceScheduleSettingServiceAdv();
	protected AttendanceDetailMobileAnalyseServiceAdv attendanceDetailMobileAnalyseServiceAdv = new AttendanceDetailMobileAnalyseServiceAdv();
}
