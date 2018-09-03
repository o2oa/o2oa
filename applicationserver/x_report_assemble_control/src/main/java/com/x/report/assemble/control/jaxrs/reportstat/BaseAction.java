package com.x.report.assemble.control.jaxrs.reportstat;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.report.assemble.control.service.Report_C_WorkProgServiceAdv;
import com.x.report.assemble.control.service.Report_I_ServiceAdv;
import com.x.report.assemble.control.service.Report_I_WorkInfoServiceAdv;
import com.x.report.assemble.control.service.Report_P_MeasureInfoServiceAdv;
import com.x.report.assemble.control.service.UserManagerService;
import com.x.report.common.date.DateOperation;

public class BaseAction extends StandardJaxrsAction{
	
	protected DateOperation dateOperation = new DateOperation();
	
	protected UserManagerService userManagerService = new UserManagerService();
	
	protected Report_I_ServiceAdv report_I_ServiceAdv = new Report_I_ServiceAdv();
	
	protected Report_C_WorkProgServiceAdv report_C_WorkProgServiceAdv = new Report_C_WorkProgServiceAdv();
	
	protected Report_I_WorkInfoServiceAdv report_I_WorkInfoServiceAdv = new Report_I_WorkInfoServiceAdv();
	
	protected Report_P_MeasureInfoServiceAdv report_P_MeasureInfoServiceAdv = new Report_P_MeasureInfoServiceAdv();

}
