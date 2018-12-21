package com.x.report.assemble.control.jaxrs.workinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

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
import com.x.report.assemble.control.ThisApplication;
import com.x.report.assemble.control.jaxrs.workinfo.exception.ExceptionParameterInvalid;
import com.x.report.assemble.control.jaxrs.workinfo.exception.ExceptionQueryReportWithReportId;
import com.x.report.assemble.control.jaxrs.workinfo.exception.ExceptionReportNotExists;
import com.x.report.assemble.control.jaxrs.workinfo.exception.ExceptionSaveWorkInfo;
import com.x.report.core.entity.Report_I_Base;
import com.x.report.core.entity.Report_I_WorkInfo;

public class ActionSaveWorkInfo extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionSaveWorkInfo.class );
	
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

//		if( check ){
//			if( StringUtils.isEmpty(wi.getWorkTitle()) ){
//				check = false;
//				Exception exception = new ExceptionParameterInvalid( "属性'workTitle'不允许为空！" );
//				result.error( exception );
//			}
//		}

//		if( check ){
//			if( StringUtils.isEmpty(wi.getWorkTag()) ){
//				check = false;
//				Exception exception = new ExceptionParameterInvalid( "属性'workTag'不允许为空！" );
//				result.error( exception );
//			}
//		}

		if( check ){
			if( StringUtils.isEmpty(wi.getReportId())){
				check = false;
				Exception exception = new ExceptionParameterInvalid( "属性'reportId'不允许为空！" );
				result.error( exception );
			}
		}

//		if( check ){
//			if(StringUtils.isEmpty( wi.getDescribe() )){
//				check = false;
//				Exception exception = new ExceptionParameterInvalid( "属性'describe'不允许为空！" );
//				result.error( exception );
//			}
//		}
		
		Report_I_Base reportBase = null;
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
		
		//检查保存工作信息
		if( check ){
			try {
				wi.setWorkCreator( effectivePerson.getName() );
				wi.setWorkReportMonth( reportBase.getMonth() );
				wi.setWorkReportYear( reportBase.getYear() );
				wi.setWorkUnit( reportBase.getTargetUnit() );
				wi.setWorkYear( reportBase.getYear() );
				wi.setProfileId( reportBase.getProfileId() );
				
				if( StringUtils.isEmpty( wi.getKeyWorkId() ) || wi.getId().equalsIgnoreCase( wi.getKeyWorkId() ) ){
					wi.setWorkType(ThisApplication.WORKTYPE_PERSON );
					wi.setKeyWorkId(  wi.getId() );
				}else{
					wi.setWorkType(ThisApplication.WORKTYPE_DEPT );
				}
				
				Report_I_WorkInfo workInfo = report_I_WorkInfoServiceAdv.save( wi );
				result.setData( new Wo( workInfo.getId() ) );
			} catch (Exception e) {
				Exception exception = new ExceptionSaveWorkInfo( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wi extends Report_I_WorkInfo {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodify);
		
		public static WrapCopier< Wi, Report_I_WorkInfo > copier = WrapCopierFactory.wi( Wi.class, Report_I_WorkInfo.class, null, Wi.Excludes );

		@FieldDescribe( "工作说明内容" )
		private String describe = "";

		@FieldDescribe( "工作完成情况总结" )
		private String workProgSummary = "";

		@FieldDescribe( "后续工作计划汇总" )
		private String workPlanSummary = "";
		
		public String getDescribe() {
			return describe;
		}

		public void setDescribe(String describe) {
			this.describe = describe;
		}

		public String getWorkProgSummary() {
			return workProgSummary;
		}

		public String getWorkPlanSummary() {
			return workPlanSummary;
		}

		public void setWorkProgSummary(String workProgSummary) {
			this.workProgSummary = workProgSummary;
		}

		public void setWorkPlanSummary(String workPlanSummary) {
			this.workPlanSummary = workPlanSummary;
		}
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}

}