package com.x.report.assemble.control.jaxrs.workplan;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.report.assemble.control.service.Report_C_WorkPlanNextServiceAdv;
import com.x.report.assemble.control.service.Report_C_WorkPlanServiceAdv;
import com.x.report.assemble.control.service.Report_I_ServiceAdv;

public class BaseAction extends StandardJaxrsAction{
	
	protected Report_I_ServiceAdv report_I_ServiceAdv = new Report_I_ServiceAdv();
	
	protected Report_C_WorkPlanServiceAdv report_C_WorkPlanServiceAdv = new Report_C_WorkPlanServiceAdv();
	
	protected Report_C_WorkPlanNextServiceAdv report_C_WorkPlanNextServiceAdv = new Report_C_WorkPlanNextServiceAdv();

}
