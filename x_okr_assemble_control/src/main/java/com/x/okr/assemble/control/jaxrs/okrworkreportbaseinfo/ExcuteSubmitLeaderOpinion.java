package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.entity.OkrWorkReportBaseInfo;
import com.x.organization.core.express.wrap.WrapPerson;

public class ExcuteSubmitLeaderOpinion extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteSubmitLeaderOpinion.class );

	protected ActionResult<WrapOutId> execute( HttpServletRequest request,EffectivePerson effectivePerson, OkrWorkReportBaseInfo okrWorkReportBaseInfo, String opinion ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		List<String> ids = null;
		String reportor_audit_notice = null;
		WrapPerson report_supervisor = null;
		String report_supervisorIdentity = null; //督办员身份
		String report_supervisorName = null; //督办员身份
		String report_supervisorOrgName = null; //督办员身份
		String report_supervisorCompanyName = null; //督办员身份
		boolean check = true;
		OkrUserCache  okrUserCache  = null;
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getName() );
		}catch(Exception e){
			check = false;
			Exception exception = new GetOkrUserCacheException( e, effectivePerson.getName() );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}
		
		if( check && okrUserCache == null ){
			check = false;
			Exception exception = new UserNoLoginException( effectivePerson.getName() );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}
		
		if ( check ) {
			if( opinion == null || opinion.isEmpty() ){
				opinion = "已阅。";
			}
		}
		
		if( check ){
			try {
				reportor_audit_notice = okrConfigSystemService.getValueWithConfigCode( "REPORTOR_AUDIT_NOTICE" );
			} catch (Exception e) {
				check = false;
				Exception exception = new SystemConfigQueryByCodeException( e, "REPORTOR_AUDIT_NOTICE" );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				report_supervisorIdentity = okrConfigSystemService.getValueWithConfigCode( "REPORT_SUPERVISOR" );
			} catch (Exception e) {
				check = false;
				Exception exception = new SystemConfigQueryByCodeException( e, "REPORT_SUPERVISOR" );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				okrWorkReportFlowService.leaderProcess( okrWorkReportBaseInfo, opinion, okrUserCache.getLoginIdentityName() );
				result.setData( new WrapOutId( okrWorkReportBaseInfo.getId() ) );
				//是否每个环节都通知汇报人阅知
				if( "OPEN".equalsIgnoreCase( reportor_audit_notice )){
					//1、查询汇报人是否存在该汇报的待阅信息，如果有则不需要发送， 如果汇报都暂无该汇报的待阅信息，那么，发送一条待阅信息
					ids = okrTaskService.listIdsByTargetActivityAndObjId( "汇报确认", okrWorkReportBaseInfo.getId(), "汇报确认", okrWorkReportBaseInfo.getReporterIdentity() );
					if( ids == null || ids.isEmpty() ){
						okrWorkReportFlowService.addReportConfirmReader(  okrWorkReportBaseInfo, 
								okrWorkReportBaseInfo.getReporterIdentity() ,
								okrWorkReportBaseInfo.getReporterName(), 
								okrWorkReportBaseInfo.getReporterOrganizationName(), 
								okrWorkReportBaseInfo.getReporterCompanyName());
					}
					
					//看看流程是否过督办员审核
					if( okrWorkReportBaseInfo.getReportWorkflowType() != null && "ADMIN_AND_ALLLEADER".equalsIgnoreCase( okrWorkReportBaseInfo.getReportWorkflowType())){
						if( report_supervisorIdentity != null && !report_supervisorIdentity.isEmpty() ){
							ids = okrTaskService.listIdsByTargetActivityAndObjId( "汇报确认", okrWorkReportBaseInfo.getId(), "汇报确认", report_supervisorIdentity );
							//如果督办员没有该汇报的待办待阅，则给督办员发送待阅
							if( ids == null || ids.isEmpty() ){
								report_supervisor = okrUserManagerService.getUserByIdentity( report_supervisorIdentity );
								if( report_supervisor != null ){
									report_supervisorName = report_supervisor.getName();
									report_supervisorOrgName = okrUserManagerService.getDepartmentNameByIdentity( report_supervisorIdentity );
									report_supervisorCompanyName = okrUserManagerService.getCompanyNameByIdentity( report_supervisorIdentity );
									
									ids = okrTaskService.listIdsByTargetActivityAndObjId( "汇报确认", okrWorkReportBaseInfo.getId(), "汇报确认", report_supervisorIdentity );
									if( ids == null || ids.isEmpty() ){
										okrWorkReportFlowService.addReportConfirmReader(  okrWorkReportBaseInfo, 
												report_supervisorIdentity ,
												report_supervisorName, 
												report_supervisorOrgName, 
												report_supervisorCompanyName
										);
									}
								}
							}
						}
					}
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new LeaderOpinionSubmitException( e, okrWorkReportBaseInfo.getId() );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		return result;
	}
	
}