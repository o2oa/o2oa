package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionGetOkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionLeaderOpinionSubmit;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionSystemConfigQueryByCode;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionUserNoLogin;
import com.x.okr.assemble.control.jaxrs.queue.WrapInWorkDynamic;
import com.x.okr.entity.OkrWorkAuthorizeRecord;
import com.x.okr.entity.OkrWorkReportBaseInfo;

public class ActionSubmitLeaderOpinion extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionSubmitLeaderOpinion.class );

	protected ActionResult<Wo> execute( HttpServletRequest request,EffectivePerson effectivePerson, OkrWorkReportBaseInfo okrWorkReportBaseInfo, String opinion ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Boolean check = true;
		OkrUserCache  okrUserCache  = null;
		OkrWorkAuthorizeRecord okrWorkAuthorizeRecord = null;
		String reportor_audit_notice = null;
		String report_supervisorIdentity = null;
		String report_progress = "CLOSE";
		
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getDistinguishedName() );
		}catch(Exception e){
			check = false;
			Exception exception = new ExceptionGetOkrUserCache( e, effectivePerson.getDistinguishedName() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if( check && okrUserCache == null ){
			check = false;
			Exception exception = new ExceptionUserNoLogin( effectivePerson.getDistinguishedName() );
			result.error( exception );
		}
		
		if( check ){
			try {
				reportor_audit_notice = okrConfigSystemService.getValueWithConfigCode( "REPORTOR_AUDIT_NOTICE" );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSystemConfigQueryByCode( e, "REPORTOR_AUDIT_NOTICE" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				report_supervisorIdentity = okrConfigSystemService.getValueWithConfigCode( "REPORT_SUPERVISOR" );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSystemConfigQueryByCode( e, "REPORT_SUPERVISOR" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if ( check ) {
			if( opinion == null || opinion.isEmpty() ){
				opinion = "已阅。";
			}
		}
		
		if( check ){
			try {
				okrWorkReportFlowService.leaderProcess( okrWorkReportBaseInfo, opinion, okrUserCache.getLoginIdentityName() );
				result.setData( new Wo( okrWorkReportBaseInfo.getId() ) );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionLeaderOpinionSubmit( e, okrWorkReportBaseInfo.getId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				//是否汇报工作的进展进度数字
				report_progress = okrConfigSystemService.getValueWithConfigCode( "REPORT_PROGRESS" );
				if( report_progress == null || report_progress.isEmpty() ){
					report_progress = "CLOSE";
				}
			} catch (Exception e) {
				report_progress = "CLOSE";
				logger.warn( "system get config got an exception." );
				logger.error(e);
			}
		}
		if( check ){
			try {
				okrWorkBaseInfoService.analyseWorkProgress( okrWorkReportBaseInfo.getWorkId(),okrWorkReportBaseInfo,  report_progress, dateOperation.getNowDateTime() );
			} catch (Exception e ) {
				logger.warn( "system analyse work progres got an exceptin.", e );
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if(check) {
			//发送待阅信息
			//领导审批信息提交后，向负责人和所有的授权人
			//如果该汇报是经过督办员审核的，那么督办员也需要收到待阅信息
			if( okrWorkReportBaseInfo != null ) {
				if( "OPEN".equalsIgnoreCase( reportor_audit_notice )){
					//1、向汇报人发送待阅信息
					sendWorkReportReadTask( okrWorkReportBaseInfo, okrWorkReportBaseInfo.getReporterIdentity() );
					//2、向该工作所有的授权人发送待阅信息
					List<String> authorize_ids = okrWorkAuthorizeRecordService.listByWorkId( okrWorkReportBaseInfo.getWorkId() );
					if( authorize_ids != null ){
						for( String authorize_id : authorize_ids ){
							//logger.info( "工作的授权信息ID:" + authorize_id );
							okrWorkAuthorizeRecord = okrWorkAuthorizeRecordService.get( authorize_id );
							if( okrWorkAuthorizeRecord != null ){
								//logger.info( "工作的授权人:" + okrWorkAuthorizeRecord.getDelegatorIdentity() );
								sendWorkReportReadTask( okrWorkReportBaseInfo, okrWorkAuthorizeRecord.getDelegatorIdentity() );
							}
						}
					}
					//3、如果该汇报是经过督办员审核的，那么督办员也需要收到待阅信息
					if( okrWorkReportBaseInfo.getReportWorkflowType() != null && "ADMIN_AND_ALLLEADER".equalsIgnoreCase( okrWorkReportBaseInfo.getReportWorkflowType())){
						if( report_supervisorIdentity != null && !report_supervisorIdentity.isEmpty() ){
							sendWorkReportReadTask( okrWorkReportBaseInfo, report_supervisorIdentity );
						}
					}
				}else{
					if( "已完成".equals( okrWorkReportBaseInfo.getProcessStatus() )){
						//1、向汇报人发送待阅信息
						sendWorkReportReadTask( okrWorkReportBaseInfo, okrWorkReportBaseInfo.getReporterIdentity() );
						//2、向该工作所有的授权人发送待阅信息
						List<String> authorize_ids = okrWorkAuthorizeRecordService.listByWorkId( okrWorkReportBaseInfo.getWorkId() );
						if( authorize_ids != null ){
							for( String authorize_id : authorize_ids ){
								//logger.info( "工作的授权信息ID:" + authorize_id );
								okrWorkAuthorizeRecord = okrWorkAuthorizeRecordService.get( authorize_id );
								if( okrWorkAuthorizeRecord != null ){
									//logger.info( "工作的授权人:" + okrWorkAuthorizeRecord.getDelegatorIdentity() );
									sendWorkReportReadTask( okrWorkReportBaseInfo, okrWorkAuthorizeRecord.getDelegatorIdentity() );
								}
							}
						}
					}
				}
			}
		}
		if(check) {
			//记录工作动态信息
			if( okrWorkReportBaseInfo != null ) {
				WrapInWorkDynamic.sendWithWorkReport( okrWorkReportBaseInfo, 
						effectivePerson.getDistinguishedName(), 
						okrUserCache.getLoginUserName(), 
						okrUserCache.getLoginIdentityName() , 
						"提交工作汇报", 
						"工作汇报流转[领导审批]提交成功！"
				);
			}
		}
		return result;
	}
}