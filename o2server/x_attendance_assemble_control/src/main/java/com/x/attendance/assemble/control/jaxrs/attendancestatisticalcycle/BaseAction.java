package com.x.attendance.assemble.control.jaxrs.attendancestatisticalcycle;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.service.UserManagerService;
import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;

public class BaseAction extends StandardJaxrsAction{
	protected Cache.CacheCategory cache = new Cache.CacheCategory( AttendanceStatisticalCycle.class);
	protected UserManagerService userManagerService = new UserManagerService();
	protected DateOperation dateOperation = new DateOperation();

}
