package com.x.report.assemble.control.jaxrs.workinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.report.assemble.control.jaxrs.workinfo.exception.ExceptionParameterInvalid;
import com.x.report.assemble.control.jaxrs.workinfo.exception.ExceptionQueryReportWithReportId;
import com.x.report.assemble.control.jaxrs.workinfo.exception.ExceptionReportNotExists;
import com.x.report.core.entity.Report_I_Base;
import com.x.report.core.entity.Report_I_WorkInfo;

/**
 * 根据汇报ID以及指定的工作ID获取所有的工作计划信息列表
 * @author O2LEE
 *
 */
public class ActionListWorkInfoWithReport extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionListWorkInfoWithReport.class);
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String reportId, String workMonthFlag ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		Report_I_Base reportBase = null;
		List<Report_I_WorkInfo> workList = null;
		List<Wo> wos = null;
		List<String> ids = null;
		Boolean check = true;
		
		if( check ){
			if( reportId == null || reportId.isEmpty() ){
				check = false;
				Exception exception = new ExceptionParameterInvalid( "参数'reportId'不允许为空！" );
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

		if( check ) {
			ids = report_I_WorkInfoServiceAdv.listIdsWithReport( reportId,  workMonthFlag );
		}

		if( check ) {
			if(ListTools.isNotEmpty( ids )){
				workList = report_I_WorkInfoServiceAdv.list( ids );
				if( ListTools.isNotEmpty( workList )){
					wos = Wo.copier.copy( workList );
				}
			}
		}
		result.setData( wos );
		return result;
	}

	public static class Wo extends Report_I_WorkInfo  {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<>();

		public static WrapCopier<Report_I_WorkInfo, Wo> copier = WrapCopierFactory.wo( Report_I_WorkInfo.class, Wo.class, null, Wo.Excludes);
	}
}