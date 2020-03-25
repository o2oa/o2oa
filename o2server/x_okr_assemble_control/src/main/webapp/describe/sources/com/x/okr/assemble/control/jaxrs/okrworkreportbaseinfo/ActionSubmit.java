package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionGetOkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionUserNoLogin;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionWorkReportAdminProcess;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionWorkReportDraftSubmit;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionWorkReportIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionWorkReportLeaderSubmit;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionWorkReportQueryById;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionWorkReportSave;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionWrapInConvert;
import com.x.okr.entity.OkrWorkReportBaseInfo;

public class ActionSubmit extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionSubmit.class );

	protected ActionResult<Wo> execute( HttpServletRequest request,EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		OkrWorkReportBaseInfo okrWorkReportBaseInfo = null;
		Boolean check = true;
		List<String> ids = null;
		OkrUserCache  okrUserCache  = null;
		WiOkrWorkReportBaseInfo wrapIn = null;

		if( check ){
			try {
				wrapIn = this.convertToWrapIn( jsonElement, WiOkrWorkReportBaseInfo.class);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionWrapInConvert(e, jsonElement);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
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
			if( wrapIn.getId() == null || wrapIn.getId().isEmpty() ){
				check = false;
				Exception exception = new ExceptionWorkReportIdEmpty();
				result.error( exception );
			}
		}
		
		//查询汇报是否存在，如果存在，则不需要再新建一个了，直接更新
		if( check ){
			try {
				okrWorkReportBaseInfo = okrWorkReportQueryService.get( wrapIn.getId() );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionWorkReportQueryById( e, wrapIn.getId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( okrWorkReportBaseInfo == null ){
			try {
				logger.info( "report not exists yet, save a new report......" );
				result = new ActionSave().execute( request, effectivePerson, wrapIn );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionWorkReportSave( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			//判断当前用户身份在指定的工作汇报中是否存在待办信息，是否需要进行处理
			ids = okrTaskService.listIdsByTargetActivityAndObjId( "工作汇报", wrapIn.getId(), null, okrUserCache.getLoginIdentityName() );
			if( ids == null || ids.isEmpty() ){
				check = false;
				logger.warn( "user has no task, user need not process this report." );
			}
		}
		
		if( check ){
			if( okrWorkReportBaseInfo == null || "草稿".equals( okrWorkReportBaseInfo.getActivityName() )){
				try {
					wrapIn.setCurrentProcessLevel( 1 );
					result = new ActionSubmitDraftReport().execute( request, effectivePerson, wrapIn );					
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionWorkReportDraftSubmit( e );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}else if( okrWorkReportBaseInfo != null && "管理员督办".equals( okrWorkReportBaseInfo.getActivityName() )){
				//管理员填写督办信息
				try {
					okrWorkReportBaseInfo.setIsWorkCompleted( wrapIn.getIsWorkCompleted() );
					okrWorkReportBaseInfo.setProgressPercent( wrapIn.getProgressPercent() );
					result = new ActionSubmitAdminSupervise().execute( request, effectivePerson, okrWorkReportBaseInfo, wrapIn.getAdminSuperviseInfo() );				
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionWorkReportAdminProcess( e, okrWorkReportBaseInfo.getId() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}			
			}else if( okrWorkReportBaseInfo != null){
				//领导审批
				try {
					result = new ActionSubmitLeaderOpinion().execute( request, effectivePerson, okrWorkReportBaseInfo, wrapIn.getOpinion()  );				
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionWorkReportLeaderSubmit( e, okrWorkReportBaseInfo.getId() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		
		if( check ){
			List<String> workTypeList = new ArrayList<String>();
			if( okrWorkReportBaseInfo != null ){
				workTypeList.add( okrWorkReportBaseInfo.getWorkType() );
			}else{
				workTypeList = okrConfigWorkTypeService.listAllTypeName();
			}
			try {
				okrWorkReportTaskCollectService.checkReportCollectTask( okrUserCache.getLoginIdentityName(), workTypeList );
			} catch (Exception e) {
				logger.warn( "check report collect got an exception ." );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
}