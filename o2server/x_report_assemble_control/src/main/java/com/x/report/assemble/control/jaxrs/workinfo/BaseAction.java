package com.x.report.assemble.control.jaxrs.workinfo;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.report.assemble.control.service.Report_I_ServiceAdv;
import com.x.report.assemble.control.service.Report_I_WorkInfoServiceAdv;
import com.x.report.assemble.control.service.Report_P_ProfileServiceAdv;
import com.x.report.assemble.control.service.WorkConfigUtilService;

public class BaseAction extends StandardJaxrsAction{
	
	protected Report_I_ServiceAdv report_I_ServiceAdv = new Report_I_ServiceAdv();
	
	protected Report_P_ProfileServiceAdv report_P_ProfileServiceAdv = new Report_P_ProfileServiceAdv();
	
	protected WorkConfigUtilService workConfigServiceAdv = new WorkConfigUtilService();

	protected Report_I_WorkInfoServiceAdv report_I_WorkInfoServiceAdv = new Report_I_WorkInfoServiceAdv();

}
