package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.GetOkrUserCacheException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ReportProcessLogListException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.SystemConfigQueryByCodeException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.UserNoLoginException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.WorkNotExistsException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.WorkQueryByIdException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.WorkReportIdEmptyException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.WorkReportQueryByIdException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.WorkReportWrapOutException;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkReportBaseInfo;
import com.x.okr.entity.OkrWorkReportDetailInfo;
import com.x.okr.entity.OkrWorkReportProcessLog;

public class ExcuteGet extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteGet.class );
	
	protected ActionResult<WrapOutOkrWorkReportBaseInfo> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutOkrWorkReportBaseInfo> result = new ActionResult<>();
		WrapOutOkrWorkReportBaseInfo wrap = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		OkrWorkReportBaseInfo okrWorkReportBaseInfo = null;
		OkrWorkReportDetailInfo okrWorkReportDetailInfo  = null;
		List<OkrWorkReportProcessLog> okrWorkReportProcessLogList = null;
		String workAdminIdentity = null;
		String report_progress = "CLOSE";
		List<String> ids = null;
		Boolean check = true;
		OkrUserCache  okrUserCache  = null;
		
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
		
		if( id == null || id.isEmpty() ){
			check = false;
			Exception exception = new WorkReportIdEmptyException();
			result.error( exception );
		}
		
		if( check ){
			if ( okrUserCache.getLoginUserName() == null ) {
				check = false;
				Exception exception = new UserNoLoginException( effectivePerson.getName() );
				result.error( exception );
			}
		}
		
		if( check ){
			try {
				okrWorkReportBaseInfo = okrWorkReportQueryService.get( id );
			} catch (Exception e) {
				check = false;
				Exception exception = new WorkReportQueryByIdException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			if( okrWorkReportBaseInfo != null ){
				try {
					wrap = wrapout_copier.copy( okrWorkReportBaseInfo );
				} catch (Exception e) {
					check = false;
					Exception exception = new WorkReportWrapOutException( e );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		
		try {
			okrWorkBaseInfo = okrWorkBaseInfoService.get( wrap.getWorkId() );
			if( okrWorkBaseInfo == null ){
				check = false;
				Exception exception = new WorkNotExistsException( wrap.getWorkId() );
				result.error( exception );
			}
		} catch (Exception e) {
			check = false;
			Exception exception = new WorkQueryByIdException( e, id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);	
		}
		
		// 查询汇报详细信息
		if( check ){
			if( okrWorkReportBaseInfo != null ){
				try {
					okrWorkReportDetailInfo = okrWorkReportDetailInfoService.get( id );
					if( okrWorkReportDetailInfo != null ){
						wrap.setWorkPlan( okrWorkReportDetailInfo.getWorkPlan() );
						wrap.setWorkPointAndRequirements( okrWorkReportDetailInfo.getWorkPointAndRequirements() );
						wrap.setProgressDescription( okrWorkReportDetailInfo.getProgressDescription() );
						wrap.setAdminSuperviseInfo( okrWorkReportDetailInfo.getAdminSuperviseInfo() );
					}
				} catch (Exception e) {
					logger.warn( "system get okrWorkReportDetailInfo got an exception" );
					logger.error(e);
				}
			}
		}
		//查询所有的审批日志
		if( check ){
			if( okrWorkReportBaseInfo != null ){
				try {
					ids = okrWorkReportProcessLogService.listByReportId( id );
					if( ids !=null ){
						okrWorkReportProcessLogList = okrWorkReportProcessLogService.list( ids );
						if( okrWorkReportProcessLogList != null ){
							wrap.setProcessLogs( okrWorkReportProcessLog_wrapout_copier.copy( okrWorkReportProcessLogList ) );
						}
					}
				} catch (Exception e) {
					Exception exception = new ReportProcessLogListException( e, id );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);	
				}
			}
		}
		
		if( check ){
			if( okrWorkReportBaseInfo != null ){
				try {
					workAdminIdentity = okrConfigSystemService.getValueWithConfigCode( "REPORT_SUPERVISOR" );
				} catch (Exception e) {
					check = false;
					Exception exception = new SystemConfigQueryByCodeException( e, "REPORT_SUPERVISOR" );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		
		if( check ){
			//判断当前处理人是什么身份
			if( wrap.getCreatorIdentity() != null && okrUserCache.getLoginIdentityName() .equalsIgnoreCase( wrap.getCreatorIdentity())){
				wrap.setIsCreator( true );
			}
			if( wrap.getReporterIdentity() != null && okrUserCache.getLoginIdentityName() .equalsIgnoreCase( wrap.getReporterIdentity())){
				wrap.setIsReporter(true);
			}
			//logger.debug( "wrap.getReportWorkflowType()=" + wrap.getReportWorkflowType() );
			if( "ADMIN_AND_ALLLEADER".equals( wrap.getReportWorkflowType() )){
				//从汇报审阅领导里进行比对
				if( wrap.getReadLeadersIdentity() != null && wrap.getReadLeadersIdentity().indexOf( okrUserCache.getLoginIdentityName()  ) >= 0 ){
					wrap.setIsReadLeader( true );
				}
			}else if( "DEPLOYER".equals( wrap.getReportWorkflowType() ) ){
				if( okrWorkBaseInfo != null ){
					//对比当前工作的部署者是否是当前用户
					if( okrWorkBaseInfo.getDeployerIdentity() != null && okrWorkBaseInfo.getDeployerIdentity().equalsIgnoreCase( okrUserCache.getLoginIdentityName()  ) ){
						wrap.setIsReadLeader( true );
					}
				}
			}
			
			if( workAdminIdentity != null && !workAdminIdentity.isEmpty() && okrUserCache.getLoginIdentityName() .equalsIgnoreCase( workAdminIdentity )){
				wrap.setIsWorkAdmin( true );
			}
			
			String workDetail = okrWorkDetailInfoService.getWorkDetailWithId( wrap.getWorkId() );
			if( workDetail != null && !workDetail.isEmpty() ){
				wrap.setTitle( workDetail );
			}
			if( "OPEN".equals( report_progress )){
				wrap.setNeedReportProgress( true );
			}else{
				wrap.setNeedReportProgress( false );
			}
			result.setData(wrap);
		}
		return result;
	}
	
}