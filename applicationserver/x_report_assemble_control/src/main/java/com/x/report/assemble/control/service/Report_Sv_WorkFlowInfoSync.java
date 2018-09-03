package com.x.report.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.report.assemble.control.jaxrs.report.element.PermissionInfo;

/**
 * 汇报流程流转信息同步服务类
 * 
 * @author O2LEE
 *
 */
public class Report_Sv_WorkFlowInfoSync{
	
	private Logger logger = LoggerFactory.getLogger(Report_Sv_WorkFlowInfoSync.class);
	private Report_I_ServiceAdv report_I_ServiceAdv = new Report_I_ServiceAdv();
	private Report_P_PermissionServiceAdv report_P_PermissionServiceAdv = new Report_P_PermissionServiceAdv();

	public Boolean syncWithReport( String reportId, String reportStatus, String activityName, String jobId, String workflowLog, List<PermissionInfo> readerList, List<PermissionInfo> authorList ) throws Exception {
		
		//更新流程相关信息
		logger.info( "系统正在尝试更新流程信息....." );
		report_I_ServiceAdv.updateWfInfo( reportId, reportStatus, activityName, jobId, workflowLog, readerList, authorList );
		
		if( StringUtils.isNotEmpty( jobId )) {
			//更新汇报访问权限
			logger.info( "系统正在尝试更新汇报访问权限......" );
			report_P_PermissionServiceAdv.refreshReportPermission( reportId, readerList, authorList );
		}		
		return true;
	}	
}
