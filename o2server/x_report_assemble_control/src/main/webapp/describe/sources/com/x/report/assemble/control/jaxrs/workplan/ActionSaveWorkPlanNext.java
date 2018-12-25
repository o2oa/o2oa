package com.x.report.assemble.control.jaxrs.workplan;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.FetchType;
import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.report.assemble.control.ExceptionWrapInConvert;
import com.x.report.assemble.control.jaxrs.workplan.exception.ExceptionParameterInvalid;
import com.x.report.assemble.control.jaxrs.workplan.exception.ExceptionQueryReportWithReportId;
import com.x.report.assemble.control.jaxrs.workplan.exception.ExceptionReportNotExists;
import com.x.report.assemble.control.jaxrs.workplan.exception.ExceptionSaveWorkPlanNext;
import com.x.report.core.entity.Report_C_WorkPlanNext;
import com.x.report.core.entity.Report_I_Base;

public class ActionSaveWorkPlanNext extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionSaveWorkPlanNext.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = null;
		Boolean check = true;
		
		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionWrapInConvert( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			if( wi.getReportId() == null || wi.getReportId().isEmpty() ){
				check = false;
				Exception exception = new ExceptionParameterInvalid( "属性'reportId'不允许为空！" );
				result.error( exception );
			}
		}
//		if( check ){
//			if( wi.getWorkTitle() == null || wi.getWorkTitle().isEmpty()){
//				check = false;
//				Exception exception = new ExceptionParameterInvalid( "属性'workTitle'不允许为空！" );
//				result.error( exception );
//			}
//		}
		if( check ){
			if( wi.getWorkContent() == null || wi.getWorkContent().isEmpty()){
				wi.setWorkContent("暂无");
			}
		}
		if( check ){
			if( wi.getPlanContent() == null || wi.getPlanContent().isEmpty()){
				wi.setPlanContent("");
			}
		}
		
		Report_I_Base reportBase = null;
		Report_C_WorkPlanNext workPlanNext = null;
		//查询汇报是否存在
		if( check ) {
			try {
				reportBase = report_I_ServiceAdv.get( wi.getReportId() );
				if( reportBase == null ) {
					check = false;
					Exception exception = new ExceptionReportNotExists( wi.getReportId() );
					result.error( exception );
				}		
			}catch( Exception e ) {
				check = false;
				Exception exception = new ExceptionQueryReportWithReportId( e, wi.getReportId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}

		if( check ){
			try {
				wi.setYear( reportBase.getYear() );
				wi.setMonth( reportBase.getMonth() );
				wi.setWeek( reportBase.getWeek() );
				wi.setFlag( reportBase.getFlag() );
				wi.setProfileId( reportBase.getProfileId() );
				
				workPlanNext = report_C_WorkPlanNextServiceAdv.save( wi );
				result.setData( new Wo( workPlanNext.getId() ) );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSaveWorkPlanNext( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wi extends Report_C_WorkPlanNext {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodify);
		
		public static WrapCopier< Wi, Report_C_WorkPlanNext > copier = WrapCopierFactory.wi( Wi.class, Report_C_WorkPlanNext.class, null, Wi.Excludes );
		
		@FieldDescribe( "工作概述" )
		private String workContent = "";
		
		@Basic( fetch = FetchType.EAGER )
		@FieldDescribe( "计划工作内容概述" )
		private String planContent = "";
		
		public String getWorkContent() {
			return workContent;
		}

		public String getPlanContent() {
			return planContent;
		}

		public void setWorkContent(String workContent) {
			this.workContent = workContent;
		}

		public void setPlanContent(String planContent) {
			this.planContent = planContent;
		}
		
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}