package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.entity.OkrWorkReportBaseInfo;

public class ExcuteSubmit extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteSubmit.class );

	protected ActionResult<WrapOutId> execute( HttpServletRequest request,EffectivePerson effectivePerson, WrapInOkrWorkReportBaseInfo wrapIn ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		OkrWorkReportBaseInfo okrWorkReportBaseInfo = null;
		Boolean check = true;
		List<String> ids = null;
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
		
		if( check ){
			if( wrapIn.getId() == null || wrapIn.getId().isEmpty() ){
				check = false;
				Exception exception = new WorkReportIdEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		
		//查询汇报是否存在，如果存在，则不需要再新建一个了，直接更新
		if( check ){
			try {
				okrWorkReportBaseInfo = okrWorkReportQueryService.get( wrapIn.getId() );
			} catch (Exception e) {
				check = false;
				Exception exception = new WorkReportQueryByIdException( e, wrapIn.getId() );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		
		if( okrWorkReportBaseInfo == null ){
			try {
				result = new ExcuteSave().execute( request, effectivePerson, wrapIn );
			} catch (Exception e) {
				check = false;
				Exception exception = new WorkReportSaveException( e );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
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
				wrapIn.setCurrentProcessLevel( 1 );
				try {
					result = new ExcuteSubmitDraftReport().execute( request, effectivePerson, wrapIn );
				} catch (Exception e) {
					check = false;
					Exception exception = new WorkReportDraftSubmitException( e );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}else if( okrWorkReportBaseInfo != null && "管理员督办".equals( okrWorkReportBaseInfo.getActivityName() )){
				//管理员填写督办信息
				wrapIn.setWorkType( okrWorkReportBaseInfo.getWorkType());
				wrapIn.setId( okrWorkReportBaseInfo.getId() );
				wrapIn.setTitle( okrWorkReportBaseInfo.getTitle() );
				result = new ExcuteSubmitAdminSupervise().execute( request, effectivePerson, okrWorkReportBaseInfo, wrapIn.getAdminSuperviseInfo() );
			}else if( okrWorkReportBaseInfo != null){
				//领导阅知
				wrapIn.setId( okrWorkReportBaseInfo.getId() );
				wrapIn.setTitle( okrWorkReportBaseInfo.getTitle() );
				wrapIn.setWorkType( okrWorkReportBaseInfo.getWorkType());
				result = new ExcuteSubmitLeaderOpinion().execute( request, effectivePerson, okrWorkReportBaseInfo, wrapIn.getOpinion()  );
			}
			
			okrWorkReportBaseInfo = okrWorkReportQueryService.get( wrapIn.getId() );
			if( okrWorkReportBaseInfo != null && result.getType().name().equals("success")){
				try {
					okrWorkDynamicsService.reportDynamic(
							okrWorkReportBaseInfo.getCenterId(), 
							okrWorkReportBaseInfo.getCenterTitle(), 
							okrWorkReportBaseInfo.getWorkId(), 
							okrWorkReportBaseInfo.getWorkTitle(), 
							okrWorkReportBaseInfo.getTitle(), 
							wrapIn.getId(), 
							"提交工作汇报", 
							effectivePerson.getName(), 
							okrUserCache.getLoginUserName(), 
							okrUserCache.getLoginIdentityName(), 
							"提交工作汇报：" + wrapIn.getTitle(), 
							"工作汇报提交成功！"
					);
				} catch (Exception e) {
					logger.warn( "system save reportDynamic got an exception." );
					logger.error(e);
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