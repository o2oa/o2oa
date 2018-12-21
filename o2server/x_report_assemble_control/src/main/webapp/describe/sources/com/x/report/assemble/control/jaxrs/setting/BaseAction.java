package com.x.report.assemble.control.jaxrs.setting;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.report.assemble.control.service.Report_S_SettingServiceAdv;
import com.x.report.assemble.control.service.UserManagerService;

public class BaseAction extends StandardJaxrsAction{
	
	protected Report_S_SettingServiceAdv report_S_SettingServiceAdv = new Report_S_SettingServiceAdv();
	protected UserManagerService userManagerService = new UserManagerService();

}
