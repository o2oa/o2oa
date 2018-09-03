package com.x.report.assemble.control.jaxrs.extcontent;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.report.assemble.control.service.Report_I_Ext_ContentServiceAdv;
import com.x.report.assemble.control.service.Report_I_ServiceAdv;

public class BaseAction extends StandardJaxrsAction{
	
	protected Report_I_Ext_ContentServiceAdv report_I_Ext_ContentServiceAdv = new Report_I_Ext_ContentServiceAdv();
	
	protected Report_I_ServiceAdv report_I_ServiceAdv = new Report_I_ServiceAdv();
	
}
