package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionGetOkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionUserNoLogin;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionWorkReportAdminProcess;
import com.x.okr.assemble.control.jaxrs.queue.WrapInWorkDynamic;
import com.x.okr.entity.OkrWorkAuthorizeRecord;
import com.x.okr.entity.OkrWorkReportBaseInfo;

public class ActionSubmitAdminSupervise extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionSubmitAdminSupervise.class );

	protected ActionResult<Wo> execute( HttpServletRequest request,EffectivePerson effectivePerson, OkrWorkReportBaseInfo okrWorkReportBaseInfo, String adminSuperviseInfo ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Boolean check = true;
		OkrWorkAuthorizeRecord okrWorkAuthorizeRecord = null;
		OkrUserCache  okrUserCache  = null;
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
			//logger.error( e, effectivePerson, request, null);
		}
		
		if ( check ) {
			if( adminSuperviseInfo == null || adminSuperviseInfo.isEmpty() ){
				adminSuperviseInfo = "无";
			}
		}
		if( check ){//处理管理员提交过程
			try {
				okrWorkReportFlowService.adminProcess( okrWorkReportBaseInfo, adminSuperviseInfo, okrUserCache.getLoginIdentityName()  );
				result.setData( new Wo( okrWorkReportBaseInfo.getId() ) );
			} catch (Exception e) {
				Exception exception = new ExceptionWorkReportAdminProcess( e, okrWorkReportBaseInfo.getId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {//是否汇报工作的进展进度数字
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
			try {//分析该工作的进展情况
				okrWorkBaseInfoService.analyseWorkProgress( okrWorkReportBaseInfo.getWorkId(), okrWorkReportBaseInfo, report_progress, dateOperation.getNowDateTime() );
			} catch (Exception e ) {
				logger.warn( "system analyse work progres got an exceptin.", e );
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ) {
			//发送待阅信息
			//管理员督办信息提交后，看看汇报是否已经流转完成，如果已经流转完成，那么需要向负责人和所有的授权人
			if( okrWorkReportBaseInfo != null ) {
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
		
		if( check ) {
			//记录工作动态信息
			if( okrWorkReportBaseInfo != null ) {
				WrapInWorkDynamic.sendWithWorkReport( okrWorkReportBaseInfo, 
						effectivePerson.getDistinguishedName(), 
						okrUserCache.getLoginUserName(), 
						okrUserCache.getLoginIdentityName() , 
						"提交工作汇报", 
						"工作汇报流转[督办员]提交成功！"
				);
				
//				if(ListTools.isNotEmpty( okrWorkReportBaseInfo.getCurrentProcessorNameList())) {
//					for( String name : okrWorkReportBaseInfo.getCurrentProcessorNameList() ) {
//						SmsMessageOperator.send( name, "工作汇报'"+okrWorkReportBaseInfo.getTitle()+"'已经提交，请审批！");
//					}
//				}
			}
		}
		return result;
	}
}