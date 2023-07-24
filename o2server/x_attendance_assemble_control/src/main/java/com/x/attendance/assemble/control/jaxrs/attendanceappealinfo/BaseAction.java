package com.x.attendance.assemble.control.jaxrs.attendanceappealinfo;

import com.x.attendance.assemble.control.service.AttendanceAppealInfoServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceDetailServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceNoticeService;
import com.x.attendance.assemble.control.service.AttendanceSettingServiceAdv;
import com.x.attendance.assemble.control.service.UserManagerService;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;

public class BaseAction extends StandardJaxrsAction{
	
	protected AttendanceAppealInfoServiceAdv attendanceAppealInfoServiceAdv = new AttendanceAppealInfoServiceAdv();
	protected AttendanceDetailServiceAdv attendanceDetailServiceAdv = new AttendanceDetailServiceAdv();
	protected UserManagerService userManagerService = new UserManagerService();
	protected AttendanceNoticeService attendanceNoticeService = new AttendanceNoticeService();
	protected AttendanceSettingServiceAdv attendanceSettingServiceAdv = new AttendanceSettingServiceAdv();

}
