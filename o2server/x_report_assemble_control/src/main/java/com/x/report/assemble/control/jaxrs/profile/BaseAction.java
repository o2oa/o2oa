package com.x.report.assemble.control.jaxrs.profile;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.report.assemble.control.service.Report_C_WorkPlanNextServiceAdv;
import com.x.report.assemble.control.service.Report_C_WorkPlanServiceAdv;
import com.x.report.assemble.control.service.Report_C_WorkProgServiceAdv;
import com.x.report.assemble.control.service.Report_I_ServiceAdv;
import com.x.report.assemble.control.service.Report_I_WorkInfoServiceAdv;
import com.x.report.assemble.control.service.Report_P_PermissionServiceAdv;
import com.x.report.assemble.control.service.Report_P_ProfileOperationServiceAdv;
import com.x.report.assemble.control.service.Report_P_ProfileServiceAdv;
import com.x.report.assemble.control.service.UserManagerService;

public class BaseAction extends StandardJaxrsAction{
	
	protected UserManagerService userManagerService = new UserManagerService();
	
	protected Report_I_ServiceAdv report_I_ServiceAdv = new Report_I_ServiceAdv();
	
	protected Report_P_ProfileServiceAdv report_P_ProfileServiceAdv = new Report_P_ProfileServiceAdv();
	
	protected Report_C_WorkPlanServiceAdv report_C_WorkPlanServiceAdv = new Report_C_WorkPlanServiceAdv();
	
	protected Report_C_WorkPlanNextServiceAdv report_C_WorkPlanNextServiceAdv = new Report_C_WorkPlanNextServiceAdv();
	
	protected Report_C_WorkProgServiceAdv report_C_WorkProgServiceAdv = new Report_C_WorkProgServiceAdv();
	
	protected Report_P_PermissionServiceAdv report_I_PermissionServiceAdv = new Report_P_PermissionServiceAdv();

	protected Report_I_WorkInfoServiceAdv report_I_WorkInfoServiceAdv = new Report_I_WorkInfoServiceAdv();
	
	protected Report_P_PermissionServiceAdv report_P_PermissionServiceAdv = new Report_P_PermissionServiceAdv();
	
	protected Report_P_ProfileOperationServiceAdv report_P_ProfileOperationServiceAdv = new Report_P_ProfileOperationServiceAdv();
	
	protected Report_P_ProfileServiceAdv report_R_CreateServiceAdv = new Report_P_ProfileServiceAdv();

}
