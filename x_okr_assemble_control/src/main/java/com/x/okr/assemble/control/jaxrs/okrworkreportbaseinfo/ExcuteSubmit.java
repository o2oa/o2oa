package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.GetOkrUserCacheException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.SystemConfigQueryByCodeException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.UserNoLoginException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.WorkReportAdminProcessException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.WorkReportDraftSubmitException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.WorkReportIdEmptyException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.WorkReportLeaderSubmitException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.WorkReportQueryByIdException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.WorkReportSaveException;
import com.x.okr.entity.OkrWorkAuthorizeRecord;
import com.x.okr.entity.OkrWorkReportBaseInfo;
import com.x.organization.core.express.wrap.WrapPerson;

public class ExcuteSubmit extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteSubmit.class );

	protected ActionResult<WrapOutId> execute( HttpServletRequest request,EffectivePerson effectivePerson, WrapInOkrWorkReportBaseInfo wrapIn ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		OkrWorkReportBaseInfo okrWorkReportBaseInfo = null;
		Boolean check = true;
		List<String> ids = null;
		List<String> authorize_ids = null;
		String reportor_audit_notice = null;
		String report_supervisorIdentity = null;
		OkrUserCache  okrUserCache  = null;
		OkrWorkAuthorizeRecord okrWorkAuthorizeRecord = null;

		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getName() );
		}catch(Exception e){
			check = false;
			Exception exception = new GetOkrUserCacheException( e, effectivePerson.getName() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if( check && okrUserCache == null ){
			check = false;
			Exception exception = new UserNoLoginException( effectivePerson.getName() );
			result.error( exception );
		}
		
		if( check ){
			if( wrapIn.getId() == null || wrapIn.getId().isEmpty() ){
				check = false;
				Exception exception = new WorkReportIdEmptyException();
				result.error( exception );
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
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( okrWorkReportBaseInfo == null ){
			try {
				result = new ExcuteSave().execute( request, effectivePerson, wrapIn );
			} catch (Exception e) {
				check = false;
				Exception exception = new WorkReportSaveException( e );
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
			try {
				reportor_audit_notice = okrConfigSystemService.getValueWithConfigCode( "REPORTOR_AUDIT_NOTICE" );
			} catch (Exception e) {
				check = false;
				Exception exception = new SystemConfigQueryByCodeException( e, "REPORTOR_AUDIT_NOTICE" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				report_supervisorIdentity = okrConfigSystemService.getValueWithConfigCode( "REPORT_SUPERVISOR" );
			} catch (Exception e) {
				check = false;
				Exception exception = new SystemConfigQueryByCodeException( e, "REPORT_SUPERVISOR" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			if( okrWorkReportBaseInfo == null || "草稿".equals( okrWorkReportBaseInfo.getActivityName() )){
				try {
					wrapIn.setCurrentProcessLevel( 1 );
					result = new ExcuteSubmitDraftReport().execute( request, effectivePerson, wrapIn );
					
					okrWorkReportBaseInfo = okrWorkReportQueryService.get( wrapIn.getId() );
					//发送待阅信息
					//工作负责人提交汇报后，工作的所有授权者都需要收到一条关于该工作汇报的待阅信息，知晓工作负责人已经提交工作汇报
					//向该工作所有的授权人发送待阅信息
					authorize_ids = okrWorkAuthorizeRecordService.listByWorkId( okrWorkReportBaseInfo.getWorkId() );
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
				} catch (Exception e) {
					check = false;
					Exception exception = new WorkReportDraftSubmitException( e );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}else if( okrWorkReportBaseInfo != null && "管理员督办".equals( okrWorkReportBaseInfo.getActivityName() )){
				//管理员填写督办信息
				try {
//					wrapIn.setId( okrWorkReportBaseInfo.getId() );
//					wrapIn.setTitle( okrWorkReportBaseInfo.getTitle() );
//					wrapIn.setWorkType( okrWorkReportBaseInfo.getWorkType());
					okrWorkReportBaseInfo.setIsWorkCompleted( wrapIn.getIsWorkCompleted() );
					okrWorkReportBaseInfo.setProgressPercent( wrapIn.getProgressPercent() );
					
					result = new ExcuteSubmitAdminSupervise().execute( request, effectivePerson, okrWorkReportBaseInfo, wrapIn.getAdminSuperviseInfo() );
					
					okrWorkReportBaseInfo = okrWorkReportQueryService.get( wrapIn.getId() );
					
					//发送待阅信息
					//管理员督办信息提交后，看看汇报是否已经流转完成，如果已经流转完成，那么需要向负责人和所有的授权人
					if( "已完成".equals( okrWorkReportBaseInfo.getProcessStatus() )){
						//1、向汇报人发送待阅信息
						sendWorkReportReadTask( okrWorkReportBaseInfo, okrWorkReportBaseInfo.getReporterIdentity() );
						//2、向该工作所有的授权人发送待阅信息
						authorize_ids = okrWorkAuthorizeRecordService.listByWorkId( okrWorkReportBaseInfo.getWorkId() );
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
				} catch (Exception e) {
					check = false;
					Exception exception = new WorkReportAdminProcessException( e, okrWorkReportBaseInfo.getId() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}			
			}else if( okrWorkReportBaseInfo != null){
				//领导审批
				try {
					//wrapIn.setId( okrWorkReportBaseInfo.getId() );
					//wrapIn.setTitle( okrWorkReportBaseInfo.getTitle() );
					//wrapIn.setWorkType( okrWorkReportBaseInfo.getWorkType());
					//okrWorkReportBaseInfo.setIsWorkCompleted( wrapIn.getIsWorkCompleted() );
					//okrWorkReportBaseInfo.setProgressPercent( wrapIn.getProgressPercent() );
					
					result = new ExcuteSubmitLeaderOpinion().execute( request, effectivePerson, okrWorkReportBaseInfo, wrapIn.getOpinion()  );
					
					okrWorkReportBaseInfo = okrWorkReportQueryService.get( wrapIn.getId() );
					//发送待阅信息
					//领导审批信息提交后，向负责人和所有的授权人
					//如果该汇报是经过督办员审核的，那么督办员也需要收到待阅信息
					if( "OPEN".equalsIgnoreCase( reportor_audit_notice )){
						//1、向汇报人发送待阅信息
						sendWorkReportReadTask( okrWorkReportBaseInfo, okrWorkReportBaseInfo.getReporterIdentity() );
						//2、向该工作所有的授权人发送待阅信息
						authorize_ids = okrWorkAuthorizeRecordService.listByWorkId( okrWorkReportBaseInfo.getWorkId() );
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
							authorize_ids = okrWorkAuthorizeRecordService.listByWorkId( okrWorkReportBaseInfo.getWorkId() );
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
				} catch (Exception e) {
					check = false;
					Exception exception = new WorkReportLeaderSubmitException( e, okrWorkReportBaseInfo.getId() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
			
			//记录用户操作日志记录
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

	private void sendWorkReportReadTask( OkrWorkReportBaseInfo okrWorkReportBaseInfo, String userIdentity ) throws Exception {
		String userName = null;
		String userDepartmentName = null;
		String userCompanyName = null;
		WrapPerson user = null;
		List<String> ids = okrTaskService.listIdsByTargetActivityAndObjId( "汇报确认", okrWorkReportBaseInfo.getId(), "汇报确认", userIdentity );
		if( ids == null || ids.isEmpty() ){
			user = okrUserManagerService.getUserByIdentity( userIdentity );
			if( user != null ){
				userName = user.getName();
				userDepartmentName = okrUserManagerService.getDepartmentNameByIdentity( userIdentity );
				userCompanyName = okrUserManagerService.getCompanyNameByIdentity( userIdentity );
				logger.info( "系统正在给用户发送工作汇报确认待阅信息：" + userIdentity );
				okrWorkReportFlowService.addReportConfirmReader(  okrWorkReportBaseInfo, 
						userIdentity ,
						userName, 
						userDepartmentName, 
						userCompanyName
				);
			}
		}
	}
	
}