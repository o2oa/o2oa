package com.x.attendance.assemble.control.jaxrs.attendancestatisticalcycle;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.service.UserManagerService;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;

public class BaseAction extends StandardJaxrsAction{
	
	protected UserManagerService userManagerService = new UserManagerService();
	protected DateOperation dateOperation = new DateOperation();

}
