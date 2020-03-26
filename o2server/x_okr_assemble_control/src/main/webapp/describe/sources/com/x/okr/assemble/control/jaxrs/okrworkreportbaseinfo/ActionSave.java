package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionGetOkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionSystemConfigQueryByCode;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionUserNoLogin;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionWorkIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionWorkNotExists;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionWorkQueryById;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionWorkReportQueryById;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkReportBaseInfo;

public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionSave.class );

	protected ActionResult<Wo> execute( HttpServletRequest request,EffectivePerson effectivePerson, WiOkrWorkReportBaseInfo wrapIn ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		OkrWorkReportBaseInfo okrWorkReportBaseInfo = null;
		OkrWorkBaseInfo okrWorkBaseInfo = new OkrWorkBaseInfo ();
		String workAdminIdentity = null;
		boolean check = true;
		OkrUserCache  okrUserCache  = null;
		
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
		if( check &&  wrapIn == null ){
			check = false;
			result.error( new Exception( "保存汇报信息时未获取到工作ID，无法继续保存汇报信息!" ) );
		}
		
		if( check ){
			try {
				workAdminIdentity = okrConfigSystemService.getValueWithConfigCode( "REPORT_SUPERVISOR" );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSystemConfigQueryByCode( e, "REPORT_SUPERVISOR" );
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
						Exception exception = new ExceptionWorkNotExists( wrapIn.getWorkId() );
						result.error( exception );
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionWorkQueryById( e, wrapIn.getWorkId() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}else{
				check = false;
				Exception exception = new ExceptionWorkIdEmpty();
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
				Exception exception = new ExceptionWorkReportQueryById( e, wrapIn.getId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( okrWorkReportBaseInfo == null || "草稿".equals( okrWorkReportBaseInfo.getActivityName() )){
				result = new ActionSaveDraftReport().execute( request, effectivePerson, workAdminIdentity, wrapIn );
			}else if( "管理员督办".equals( okrWorkReportBaseInfo.getActivityName() )){
				//管理员填写督办信息
				wrapIn.setId( okrWorkReportBaseInfo.getId() );
				wrapIn.setTitle( okrWorkReportBaseInfo.getTitle() );
				result = new ActionSaveAdminSupervise().execute( request, effectivePerson, okrWorkReportBaseInfo.getId(), wrapIn.getAdminSuperviseInfo() );
			}else{
				//领导阅知
				wrapIn.setId( okrWorkReportBaseInfo.getId() );
				wrapIn.setTitle( okrWorkReportBaseInfo.getTitle() );
				result = new ActionSaveLeaderOpinion().execute( request, effectivePerson, wrapIn, okrUserCache.getLoginIdentityName() );
			}
		}
		return result;
	}
}