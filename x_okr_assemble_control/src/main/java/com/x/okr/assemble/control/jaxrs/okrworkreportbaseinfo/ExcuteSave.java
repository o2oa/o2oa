package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;

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
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.WorkIdEmptyException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.WorkNotExistsException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.WorkQueryByIdException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.WorkReportQueryByIdException;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkReportBaseInfo;

public class ExcuteSave extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteSave.class );

	protected ActionResult<WrapOutId> execute( HttpServletRequest request,EffectivePerson effectivePerson, WrapInOkrWorkReportBaseInfo wrapIn ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		OkrWorkReportBaseInfo okrWorkReportBaseInfo = null;
		OkrWorkBaseInfo okrWorkBaseInfo = new OkrWorkBaseInfo ();
		String workAdminIdentity = null;
		boolean check = true;
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
		if( check &&  wrapIn == null ){
			check = false;
			result.error( new Exception( "保存汇报信息时未获取到工作ID，无法继续保存汇报信息!" ) );
		}
		
		if( check ){
			try {
				workAdminIdentity = okrConfigSystemService.getValueWithConfigCode( "REPORT_SUPERVISOR" );
			} catch (Exception e) {
				check = false;
				Exception exception = new SystemConfigQueryByCodeException( e, "REPORT_SUPERVISOR" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( wrapIn.getWorkId() != null && !wrapIn.getWorkId().isEmpty() ){
				try {
					okrWorkBaseInfo = okrWorkBaseInfoService.get( wrapIn.getWorkId() );
					if( okrWorkBaseInfo == null ){
						check = false;
						Exception exception = new WorkNotExistsException( wrapIn.getWorkId() );
						result.error( exception );
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new WorkQueryByIdException( e, wrapIn.getWorkId() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}else{
				check = false;
				Exception exception = new WorkIdEmptyException();
				result.error( exception );
			}
		}
		//对wrapIn里的信息进行校验		
		//查询汇报是否存在，如果存在，则不需要再新建一个了，直接更新
		if( check && wrapIn.getId() != null && !wrapIn.getId().isEmpty() ){
			try {
				okrWorkReportBaseInfo = okrWorkReportQueryService.get( wrapIn.getId() );
			} catch (Exception e) {
				check = false;
				Exception exception = new WorkReportQueryByIdException( e, wrapIn.getId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( okrWorkReportBaseInfo == null || "草稿".equals( okrWorkReportBaseInfo.getActivityName() )){
				result = new ExcuteSaveDraftReport().execute( request, effectivePerson, workAdminIdentity, wrapIn );
			}else if( "管理员督办".equals( okrWorkReportBaseInfo.getActivityName() )){
				//管理员填写督办信息
				wrapIn.setId( okrWorkReportBaseInfo.getId() );
				wrapIn.setTitle( okrWorkReportBaseInfo.getTitle() );
				result = new ExcuteSaveAdminSupervise().execute( request, effectivePerson, okrWorkReportBaseInfo.getId(), wrapIn.getAdminSuperviseInfo() );
			}else{
				//领导阅知
				wrapIn.setId( okrWorkReportBaseInfo.getId() );
				wrapIn.setTitle( okrWorkReportBaseInfo.getTitle() );
				result = new ExcuteSaveLeaderOpinion().execute( request, effectivePerson, wrapIn, okrUserCache.getLoginIdentityName() );
			}
		}
		if( check ){
			if ( result.getType().name().equals( "success" ) ) {
				String reportTitle = okrWorkBaseInfo.getTitle();
				if( okrWorkReportBaseInfo != null && okrWorkReportBaseInfo.getTitle() != null ){
					reportTitle = okrWorkReportBaseInfo.getTitle();
				}
				try {
					okrWorkDynamicsService.reportDynamic(
							okrWorkBaseInfo.getCenterId(), 
							okrWorkBaseInfo.getCenterTitle(), 
							okrWorkBaseInfo.getId(), 
							okrWorkBaseInfo.getTitle(), 
							reportTitle, 
							wrapIn.getId(), 
							"保存工作汇报", 
							effectivePerson.getName(),
							okrUserCache.getLoginUserName(),
							okrUserCache.getLoginIdentityName(), "保存工作汇报：" + wrapIn.getTitle(),
							"工作汇报保存成功！");
				} catch (Exception e) {
					logger.warn( "system save reportDynamic got an exception." );
					logger.error( e );
				}
			}
		}
		return result;
	}
	
}