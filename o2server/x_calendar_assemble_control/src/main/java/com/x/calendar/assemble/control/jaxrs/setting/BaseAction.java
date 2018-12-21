package com.x.calendar.assemble.control.jaxrs.setting;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.calendar.assemble.control.service.Calendar_SettingServiceAdv;
import com.x.calendar.assemble.control.service.UserManagerService;

public class BaseAction extends StandardJaxrsAction{
	
	protected Calendar_SettingServiceAdv calendar_SettingServiceAdv = new Calendar_SettingServiceAdv();
	protected UserManagerService userManagerService = new UserManagerService();

}
