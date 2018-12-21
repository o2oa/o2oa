package com.x.report.assemble.control.jaxrs.workprog;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.FetchType;
import javax.servlet.http.HttpServletRequest;

import org.apache.openjpa.lib.util.StringUtil;

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
import com.x.report.assemble.control.jaxrs.workplan.exception.ExceptionParameterInvalid;
import com.x.report.assemble.control.jaxrs.workplan.exception.ExceptionQueryReportWithReportId;
import com.x.report.assemble.control.jaxrs.workplan.exception.ExceptionReportNotExists;
import com.x.report.assemble.control.jaxrs.workprog.exception.ExceptionSaveWorkProg;
import com.x.report.core.entity.Report_C_WorkProg;
import com.x.report.core.entity.Report_I_Base;
import com.x.report.core.entity.Report_I_WorkInfo;
import com.x.report.core.entity.Report_I_WorkInfoDetail;

public class ActionSaveWorkProg extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionSaveWorkProg.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = null;
		Report_I_WorkInfo workInfo = null;
		Report_I_WorkInfoDetail  workDetail = null;
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
		
		if( check ){
			if( wi.getWorkTitle() == null || wi.getWorkTitle().isEmpty() ){
				check = false;
				Exception exception = new ExceptionParameterInvalid( "属性'workTitle'不允许为空！" );
				result.error( exception );
			}
		}
		
		if( check ){
			if( wi.getProgressContent() == null || wi.getProgressContent().isEmpty()){
				wi.setProgressContent( "暂无内容" );
			}
		}
		
		Report_I_Base reportBase = null;
		Report_C_WorkProg workProg = null;
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
		
		//判断一下，当前这个工作信息是否已经存在，如果不存在，则被认为是自定义的新工作
		//如果没有传入keyWorkId，那么就视为新增一个工作
		if( check ){
			if( StringUtil.isNotEmpty(wi.getWorkInfoId()) ){
				workInfo = report_I_WorkInfoServiceAdv.get(wi.getWorkInfoId());
			}
			//没有对应的工作信息，新增一条自定义工作
			if( workInfo == null || StringUtil.isEmpty(wi.getWorkInfoId()) ) {
				if( wi.getWorkTag() == null || wi.getWorkTag().isEmpty() ){
					check = false;
					Exception exception = new ExceptionParameterInvalid( "属性'workTag'不允许为空！" );
					result.error( exception );
				}
				if( wi.getWorkDescribe() == null || wi.getWorkDescribe().isEmpty() ){
					wi.setWorkDescribe("暂无内容");
				}
				workInfo = new Report_I_WorkInfo();
				workInfo.setId( Report_I_WorkInfo.createId() );
				workInfo.setProfileId( reportBase.getProfileId() );
				workInfo.setWorkType( ThisApplication.WORKTYPE_PERSON );
				workInfo.setWorkTag(wi.getWorkTag());
				workInfo.setWorkTitle( wi.getWorkTitle() );
				workInfo.setKeyWorkId( workInfo.getId() );//自定义工作，keyWorkId和Id设置为一样的
				workInfo.setReportId( reportBase.getId() );
				workInfo.setWorkCreator( effectivePerson.getDistinguishedName() );
				workInfo.setWorkReportMonth( reportBase.getMonth() );
				workInfo.setWorkReportYear( reportBase.getYear() );			
				workInfo.setWorkUnit( reportBase.getTargetUnit() );
				workInfo.setWorkYear( reportBase.getYear() );
			}
			
			workDetail = report_I_WorkInfoServiceAdv.getDetailWithWorkInfoId( reportBase.getId(), workInfo.getId() );
			if ( workDetail == null ) {
				workDetail = new Report_I_WorkInfoDetail();
				workDetail.setId( workInfo.getId() );
				workDetail.setDescribe( wi.getWorkDescribe() );
				workDetail.setKeyWorkId( workInfo.getId() );
				workDetail.setReportId( workInfo.getReportId() );	
			}
			
			if( ThisApplication.WORKTYPE_PERSON.equals( workInfo.getWorkType() )) {
				workInfo = report_I_WorkInfoServiceAdv.save( workInfo, workDetail );
				//保存完成后，完成情况就记录在该工作ID下
				wi.setWorkInfoId( workInfo.getId() );
				wi.setKeyWorkId( workInfo.getId() );
			}
		}
		
		if( check ){
			try {
				wi.setYear( reportBase.getYear() );
				wi.setMonth( reportBase.getMonth() );
				wi.setWeek( reportBase.getWeek() );
				wi.setFlag( reportBase.getFlag() );
				wi.setProfileId( reportBase.getProfileId() );
				
				workProg = report_C_WorkProgServiceAdv.save( wi );
				result.setData( new Wo( workProg.getId() ) );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSaveWorkProg( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wi extends Report_C_WorkProg {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodify);
		
		public static WrapCopier< Wi, Report_C_WorkProg > copier = WrapCopierFactory.wi( Wi.class, Report_C_WorkProg.class, null, Wi.Excludes );
		
		@FieldDescribe( "工作概述，新建自定义工作时使用" )
		private String workDescribe = "";
		
		@Basic( fetch = FetchType.EAGER )
		@FieldDescribe( "工作完成情况内容概述" )
		private String progressContent = "";
		
		@FieldDescribe( "工作标签" )
		private String workTag = ThisApplication.WORKTYPE_PERSON;

		public String getProgressContent() {
			return progressContent;
		}

		public void setProgressContent(String progressContent) {
			this.progressContent = progressContent;
		}

		public String getWorkDescribe() {
			return workDescribe;
		}

		public String getWorkTag() {
			return workTag;
		}

		public void setWorkDescribe(String workDescribe) {
			this.workDescribe = workDescribe;
		}

		public void setWorkTag(String workTag) {
			this.workTag = workTag;
		}		
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}