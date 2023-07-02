package com.x.attendance.assemble.control.jaxrs.attendanceemployeeconfig;

import com.x.attendance.assemble.control.service.AttendanceEmployeeConfigServiceAdv;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;

public class BaseAction extends StandardJaxrsAction{
	
	protected AttendanceEmployeeConfigServiceAdv attendanceEmployeeConfigServiceAdv = new AttendanceEmployeeConfigServiceAdv();
	
}
