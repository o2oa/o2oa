package com.x.report.assemble.control.jaxrs.workinfo;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.report.assemble.control.ExceptionWrapInConvert;
import com.x.report.assemble.control.jaxrs.workinfo.exception.ExceptionParameterInvalid;
import com.x.report.assemble.control.jaxrs.workinfo.exception.ExceptionQueryWorkInfoWithId;
import com.x.report.assemble.control.jaxrs.workinfo.exception.ExceptionSaveWorkInfo;
import com.x.report.assemble.control.jaxrs.workinfo.exception.ExceptionWorkInfoNotExists;
import com.x.report.core.entity.Report_I_WorkInfo;

public class ActionSaveWorkPlanSummary extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionSaveWorkPlanSummary.class );
	
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
			if( StringUtils.isEmpty(wi.getId()) ){
				check = false;
				Exception exception = new ExceptionParameterInvalid( "属性'id'不允许为空！" );
				result.error( exception );
			}
		}

//		if( check ){
//			if( StringUtils.isEmpty(wi.getWorkPlanSummary()) ){
//				check = false;
//				Exception exception = new ExceptionParameterInvalid( "属性'workPlanSummary'不允许为空！" );
//				result.error( exception );
//			}
//		}
		
		Report_I_WorkInfo workInfo = null;
		//查询工作信息是否存在
		if( check ) {
			try {
				workInfo = report_I_WorkInfoServiceAdv.get( wi.getId() );
				if( workInfo == null ) {
					check = false;
					Exception exception = new ExceptionWorkInfoNotExists( wi.getId() );
					result.error( exception );
				}
			}catch( Exception e ) {
				check = false;
				Exception exception = new ExceptionQueryWorkInfoWithId( e, wi.getId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		//检查保存工作信息
		if( check ){
			try {
				workInfo = report_I_WorkInfoServiceAdv.saveWorkPlanSummary( wi.getId(), wi.getWorkPlanSummary() );
				result.setData( new Wo( workInfo.getId() ) );
			} catch (Exception e) {
				Exception exception = new ExceptionSaveWorkInfo( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wi implements Serializable{
		
		private static final long serialVersionUID = -5076990764713538973L;

		@FieldDescribe( "workInfoId" )
		private String id = "";

		@FieldDescribe( "工作计划汇总信息" )
		private String workPlanSummary = "";

		public String getId() {
			return id;
		}

		public String getWorkPlanSummary() {
			return workPlanSummary;
		}

		public void setId(String id) {
			this.id = id;
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