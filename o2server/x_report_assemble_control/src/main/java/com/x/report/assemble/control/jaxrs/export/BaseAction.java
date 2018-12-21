package com.x.report.assemble.control.jaxrs.export;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.report.assemble.control.dataadapter.strategy.CompanyStrategyMeasure;
import com.x.report.assemble.control.dataadapter.strategy.CompanyStrategyWorks;
import com.x.report.assemble.control.service.Report_C_WorkPlanNextServiceAdv;
import com.x.report.assemble.control.service.Report_C_WorkPlanServiceAdv;
import com.x.report.assemble.control.service.Report_C_WorkProgServiceAdv;
import com.x.report.assemble.control.service.Report_I_Ext_ContentServiceAdv;
import com.x.report.assemble.control.service.Report_I_ServiceAdv;
import com.x.report.assemble.control.service.Report_I_WorkInfoServiceAdv;
import com.x.report.assemble.control.service.Report_P_MeasureInfoServiceAdv;
import com.x.report.assemble.control.service.UserManagerService;
import com.x.report.common.date.DateOperation;
import com.x.report.core.entity.Report_S_Setting;

import net.sf.ehcache.Ehcache;

public class BaseAction extends StandardJaxrsAction{
	
	protected Ehcache cache = ApplicationCache.instance().getCache( Report_S_Setting.class );
	
	protected UserManagerService userManagerService = new UserManagerService();	
	protected Report_I_ServiceAdv report_I_ServiceAdv = new Report_I_ServiceAdv();	
	protected Report_C_WorkPlanServiceAdv report_C_WorkPlanServiceAdv = new Report_C_WorkPlanServiceAdv();	
	protected Report_I_WorkInfoServiceAdv report_I_WorkInfoServiceAdv = new Report_I_WorkInfoServiceAdv();	
	protected Report_C_WorkPlanNextServiceAdv report_C_WorkPlanNextServiceAdv = new Report_C_WorkPlanNextServiceAdv();	
	protected Report_C_WorkProgServiceAdv report_C_WorkProgServiceAdv = new Report_C_WorkProgServiceAdv();	
	protected Report_I_Ext_ContentServiceAdv report_I_Ext_ContentServiceAdv = new Report_I_Ext_ContentServiceAdv();	
	protected Report_P_MeasureInfoServiceAdv report_P_MeasureInfoServiceAdv = new Report_P_MeasureInfoServiceAdv();
	protected CompanyStrategyMeasure companyStrategyMeasure = new CompanyStrategyMeasure();
	protected CompanyStrategyWorks companyStrategyWorks = new CompanyStrategyWorks();
	
	protected DateOperation dateOperation = new DateOperation();

	public static class WrapFileInfo {
		
		private String id = null;
		
		private String title = null;
		
		private byte[] bytes = null;
		
		public String getTitle() {
			return title;
		}
		public byte[] getBytes() {
			return bytes;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public void setBytes(byte[] bytes) {
			this.bytes = bytes;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}		
	}
}
