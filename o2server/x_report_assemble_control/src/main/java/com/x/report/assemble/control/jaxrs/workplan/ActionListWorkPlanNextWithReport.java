package com.x.report.assemble.control.jaxrs.workplan;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.FetchType;
import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.report.assemble.control.jaxrs.workplan.exception.ExceptionQueryReportWithReportId;
import com.x.report.assemble.control.jaxrs.workplan.exception.ExceptionQueryWorkPlanNextWithReportId;
import com.x.report.assemble.control.jaxrs.workplan.exception.ExceptionReportNotExists;
import com.x.report.core.entity.Report_C_WorkPlanNext;
import com.x.report.core.entity.Report_C_WorkPlanNextDetail;
import com.x.report.core.entity.Report_I_Base;

/**
 * 根据汇报ID获取所有的下周期工作计划信息列表
 * @author O2LEE
 *
 */
public class ActionListWorkPlanNextWithReport extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionListWorkPlanNextWithReport.class);
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String reportId ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = null;
		List<Report_C_WorkPlanNext> planList = null;
		Report_I_Base reportBase = null;
		Boolean check = true;
		
		//查询汇报是否存在
		if( check ) {
			try {
				reportBase = report_I_ServiceAdv.get( reportId );
				if( reportBase == null ) {
					check = false;
					Exception exception = new ExceptionReportNotExists( reportId );
					result.error( exception );
				}		
			}catch( Exception e ) {
				check = false;
				Exception exception = new ExceptionQueryReportWithReportId( e, reportId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		//查询工作计划列表
		if( check ) {
			try {
				planList = report_C_WorkPlanNextServiceAdv.listWithReportId( reportId );
				if( planList != null ) {
					wos = Wo.copier.copy( planList );
				}
			}catch( Exception e ) {
				check = false;
				Exception exception = new ExceptionQueryWorkPlanNextWithReportId( e, reportId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		//组织工作计划详细内容
		if( check ) {
			if( wos != null && !wos.isEmpty() ) {
				Report_C_WorkPlanNextDetail detail = null;
				for( Wo wo : wos ) {
					//查询并且组织详细信息
					detail = report_C_WorkPlanNextServiceAdv.getDetailWithPlanId( wo.getId() );
					if( detail != null ) {
						wo.setDetailId( detail.getId() );
						wo.setWorkContent( detail.getWorkContent() );
						wo.setPlanNextContent( detail.getPlanContent() );
					}
				}
			}
		}
		
		result.setData( wos );
		return result;
	}
	
	public static class Wo extends Report_C_WorkPlanNext{
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();
		
		public static WrapCopier<Report_C_WorkPlanNext, Wo> copier = WrapCopierFactory.wo( Report_C_WorkPlanNext.class, Wo.class, null,Wo.Excludes);
		
		@FieldDescribe("详细信息ID")
		private String detailId;
		
		@FieldDescribe( "工作概述" )
		private String workContent = "";
		
		@Basic( fetch = FetchType.EAGER )
		@FieldDescribe( "计划工作内容概述" )
		private String planContent = "";

		public String getDetailId() {
			return detailId;
		}

		public String getWorkContent() {
			return workContent;
		}

		public String getPlanNextContent() {
			return planContent;
		}

		public void setDetailId(String detailId) {
			this.detailId = detailId;
		}

		public void setWorkContent(String workContent) {
			this.workContent = workContent;
		}

		public void setPlanNextContent(String planContent) {
			this.planContent = planContent;
		}
		
	}
	
	
	
}