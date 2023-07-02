package com.x.attendance.assemble.control.jaxrs.attendancesetting;

import com.x.attendance.assemble.control.service.AttendanceSettingServiceAdv;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;

public class BaseAction extends StandardJaxrsAction{
	
	protected AttendanceSettingServiceAdv attendanceSettingServiceAdv = new AttendanceSettingServiceAdv();

}
