package com.x.report.assemble.control.jaxrs.report;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.report.assemble.control.jaxrs.report.exception.ExceptionQueryWithReportId;
import com.x.report.assemble.control.jaxrs.report.exception.ExceptionReportIdEmpty;
import com.x.report.assemble.control.jaxrs.report.exception.ExceptionReportInfoProcess;
import com.x.report.assemble.control.jaxrs.report.exception.ExceptionReportNotExists;
import com.x.report.core.entity.Report_I_Base;

/**
 * 根据ID获取指定的汇报完整信息，包括当月计划， 完成情况 ，下月等内容，以及汇报的审批过程
 * @author O2LEE
 *
 */
public class ActionModifyReportStatus extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger( ActionModifyReportStatus.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, EffectivePerson effectivePerson, String reportId, JsonElement jsonElement ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		Report_I_Base report_base = null;
		Wi wi = null;
		Boolean check = true;
		
		if( check ){
			if( StringUtils.isEmpty( reportId ) ){
				check = false;
				Exception exception = new ExceptionReportIdEmpty();
				result.error( exception );
			}
		}
		
		if( check ){
			try {
				wi = this.convertToWrapIn( jsonElement, Wi.class );
			} catch (Exception e ) {
				check = false;
				Exception exception = new ExceptionReportInfoProcess( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				report_base = report_I_ServiceAdv.get( reportId );
				if( report_base == null ) {
					check = false;
					Exception exception = new ExceptionReportNotExists( reportId );
					result.error( exception );
				}
			} catch ( Exception e ) {
				check = false;
				Exception exception = new ExceptionQueryWithReportId( e, reportId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				report_I_ServiceAdv.modityReportStatus( reportId, wi.getReportStatus() );
			}catch( Exception e) {
				Exception exception = new ExceptionReportInfoProcess( e, "汇报信息状态修改时发生异常，ID:" + reportId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		result.setData( new WrapOutId( reportId ) );
		return result;
	}
	
	public static class WiData extends GsonPropertyObject {
		@FieldDescribe("data")
		private String data;

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}		
	}
	
	
	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("汇报状态")
		private String reportStatus;

		public String getReportStatus() {
			return reportStatus;
		}

		public void setReportStatus(String reportStatus) {
			this.reportStatus = reportStatus;
		}
		
	}

}