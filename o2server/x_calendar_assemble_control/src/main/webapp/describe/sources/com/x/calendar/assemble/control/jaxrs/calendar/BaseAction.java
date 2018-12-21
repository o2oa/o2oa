package com.x.calendar.assemble.control.jaxrs.calendar;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.calendar.assemble.control.service.CalendarServiceAdv;
import com.x.calendar.assemble.control.service.UserManagerService;

public class BaseAction extends StandardJaxrsAction{
	
	protected CalendarServiceAdv calendarServiceAdv = new CalendarServiceAdv();
	protected UserManagerService userManagerService = new UserManagerService();

}
