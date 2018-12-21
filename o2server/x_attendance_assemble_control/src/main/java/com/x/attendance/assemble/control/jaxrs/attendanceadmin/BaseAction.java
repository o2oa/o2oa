package com.x.attendance.assemble.control.jaxrs.attendanceadmin;

import com.x.attendance.assemble.control.service.AttendanceAdminServiceAdv;
import com.x.attendance.assemble.control.service.UserManagerService;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;

public class BaseAction extends StandardJaxrsAction{
	
	protected AttendanceAdminServiceAdv attendanceAdminServiceAdv = new AttendanceAdminServiceAdv();
	protected UserManagerService userManagerService = new UserManagerService();
	
}
