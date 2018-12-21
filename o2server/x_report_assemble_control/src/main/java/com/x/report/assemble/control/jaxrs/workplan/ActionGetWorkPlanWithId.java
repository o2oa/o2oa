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
import com.x.report.assemble.control.jaxrs.workplan.exception.ExceptionQueryWorkPlanWithReportId;
import com.x.report.core.entity.Report_C_WorkPlan;
import com.x.report.core.entity.Report_C_WorkPlanDetail;

/**
 * 根据ID获取指定的工作计划信息列表
 * @author O2LEE
 *
 */
public class ActionGetWorkPlanWithId extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionGetWorkPlanWithId.class);
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		Report_C_WorkPlan report_C_WorkPlan = null;
		Report_C_WorkPlanDetail detail = null;
		Boolean check = true;
		
		//查询工作计划列表
		if( check ) {
			try {
				report_C_WorkPlan = report_C_WorkPlanServiceAdv.get( id );
				if( report_C_WorkPlan != null ) {
					wo = Wo.copier.copy( report_C_WorkPlan );
				}
			}catch( Exception e ) {
				check = false;
				Exception exception = new ExceptionQueryWorkPlanWithReportId( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		//组织工作计划详细内容
		if( check ) {
			if( wo != null ) {
				//查询并且组织详细信息
				detail = report_C_WorkPlanServiceAdv.getDetailWithPlanId( wo.getId() );
				if( detail != null ) {
					wo.setDetailId( detail.getId() );
					wo.setWorkContent( detail.getWorkContent() );
					wo.setPlanContent( detail.getPlanContent() );
				}
			}
		}
		
		result.setData( wo );
		return result;
	}
	
	public static class Wo extends Report_C_WorkPlan  {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();
		
		public static WrapCopier<Report_C_WorkPlan, Wo> copier = WrapCopierFactory.wo( Report_C_WorkPlan.class, Wo.class, null,Wo.Excludes);
		
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

		public String getPlanContent() {
			return planContent;
		}

		public void setDetailId(String detailId) {
			this.detailId = detailId;
		}

		public void setWorkContent(String workContent) {
			this.workContent = workContent;
		}

		public void setPlanContent(String planContent) {
			this.planContent = planContent;
		}
		
	}
	
	
	
}