package com.x.report.assemble.control.jaxrs.workprog;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.report.assemble.control.service.Report_C_WorkProgServiceAdv;
import com.x.report.assemble.control.service.Report_I_ServiceAdv;
import com.x.report.assemble.control.service.Report_I_WorkInfoServiceAdv;
import com.x.report.assemble.control.service.Report_P_ProfileServiceAdv;

public class BaseAction extends StandardJaxrsAction{
	
	protected Report_I_ServiceAdv report_I_ServiceAdv = new Report_I_ServiceAdv();
	
	protected Report_C_WorkProgServiceAdv report_C_WorkProgServiceAdv = new Report_C_WorkProgServiceAdv();
	
	protected Report_P_ProfileServiceAdv report_P_ProfileServiceAdv = new Report_P_ProfileServiceAdv();
	
	protected Report_I_WorkInfoServiceAdv report_I_WorkInfoServiceAdv = new Report_I_WorkInfoServiceAdv();
}
