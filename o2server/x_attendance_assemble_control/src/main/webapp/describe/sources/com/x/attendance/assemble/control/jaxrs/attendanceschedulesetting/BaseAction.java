package com.x.attendance.assemble.control.jaxrs.attendanceschedulesetting;

import com.x.attendance.assemble.control.service.AttendanceScheduleSettingServiceAdv;
import com.x.attendance.assemble.control.service.UserManagerService;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;

public class BaseAction extends StandardJaxrsAction{
	
	protected UserManagerService userManagerService = new UserManagerService();
	protected AttendanceScheduleSettingServiceAdv attendanceScheduleSettingServiceAdv = new AttendanceScheduleSettingServiceAdv();
	

}
