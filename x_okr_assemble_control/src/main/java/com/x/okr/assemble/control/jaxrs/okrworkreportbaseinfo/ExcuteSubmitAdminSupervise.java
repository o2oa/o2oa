package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;

import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.entity.OkrWorkReportBaseInfo;

public class ExcuteSubmitAdminSupervise extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteSubmitAdminSupervise.class );

	protected ActionResult<WrapOutId> execute( HttpServletRequest request,EffectivePerson effectivePerson, OkrWorkReportBaseInfo okrWorkReportBaseInfo, String adminSuperviseInfo ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
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
			if( adminSuperviseInfo == null || adminSuperviseInfo.isEmpty() ){
				adminSuperviseInfo = "无";
			}
		}
		
		if( check ){
			try {
				okrWorkReportFlowService.adminProcess( okrWorkReportBaseInfo, adminSuperviseInfo, okrUserCache.getLoginIdentityName()  );
				result.setData( new WrapOutId( okrWorkReportBaseInfo.getId() ) );
			} catch (Exception e) {
				Exception exception = new WorkReportAdminProcessException( e, okrWorkReportBaseInfo.getId() );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		return result;
	}
	
}