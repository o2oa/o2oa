package com.x.report.assemble.control.jaxrs.workinfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.report.assemble.control.dataadapter.strategy.CompanyStrategyMeasure;
import com.x.report.assemble.control.jaxrs.workinfo.exception.ExceptionParameterInvalid;
import com.x.report.assemble.control.jaxrs.workinfo.exception.ExceptionProfileNotExists;
import com.x.report.assemble.control.jaxrs.workinfo.exception.ExceptionQueryProfileWithId;
import com.x.report.assemble.control.jaxrs.workinfo.exception.ExceptionQueryReportWithReportId;
import com.x.report.assemble.control.jaxrs.workinfo.exception.ExceptionReportNotExists;
import com.x.report.core.entity.Report_I_Base;
import com.x.report.core.entity.Report_P_Profile;

/**
 * 根据汇报ID以及指定的举措ID获取指定的举措信息对象
 * 
 * @author O2LEE
 *
 */
public class ActionGetMeasureSnapWithMeasureId extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionGetMeasureSnapWithMeasureId.class);
	
	protected ActionResult<CompanyStrategyMeasure.WoMeasuresInfo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String reportId, String measureId ) throws Exception {
		ActionResult<CompanyStrategyMeasure.WoMeasuresInfo> result = new ActionResult<>();
		Report_I_Base reportBase = null;
		Report_P_Profile reportProfile = null;
		CompanyStrategyMeasure.WoMeasuresInfo measureInfo = null;
		
		Boolean check = true;
		
		if( check ){
			if( reportId == null || reportId.isEmpty() ){
				check = false;
				Exception exception = new ExceptionParameterInvalid( "参数'reportId'不允许为空！" );
				result.error( exception );
			}
		}
		
		if( check ){
			if( measureId == null || measureId.isEmpty() ){
				check = false;
				Exception exception = new ExceptionParameterInvalid( "参数'measureId'不允许为空！" );
				result.error( exception );
			}
		}
		
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
		
		//查询汇报概要文件是否存在
		if( check ) {
			try {
				reportProfile = report_P_ProfileServiceAdv.get( reportBase.getProfileId() );
				if( reportProfile == null ) {
					check = false;
					Exception exception = new ExceptionProfileNotExists( reportBase.getProfileId() );
					result.error( exception );
				}		
			}catch( Exception e ) {
				check = false;
				Exception exception = new ExceptionQueryProfileWithId( e, reportBase.getProfileId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
				
		//查询概要文件详细信息（与工作相关）列表
		if( check ) {
			measureInfo = workConfigServiceAdv.getMeasureInfo( reportBase.getProfileId(), measureId );
		}

		result.setData( measureInfo );
		return result;
	}
}