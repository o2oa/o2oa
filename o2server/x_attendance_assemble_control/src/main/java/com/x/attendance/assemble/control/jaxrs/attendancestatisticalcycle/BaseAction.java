package com.x.attendance.assemble.control.jaxrs.attendancestatisticalcycle;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.service.UserManagerService;
import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import net.sf.ehcache.Ehcache;

public class BaseAction extends StandardJaxrsAction{

	protected Ehcache cache = ApplicationCache.instance().getCache( AttendanceStatisticalCycle.class);
	protected UserManagerService userManagerService = new UserManagerService();
	protected DateOperation dateOperation = new DateOperation();

}
