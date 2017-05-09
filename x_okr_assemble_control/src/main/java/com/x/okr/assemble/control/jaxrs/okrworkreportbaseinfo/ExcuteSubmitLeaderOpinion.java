package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.GetOkrUserCacheException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.LeaderOpinionSubmitException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.UserNoLoginException;
import com.x.okr.entity.OkrWorkReportBaseInfo;

public class ExcuteSubmitLeaderOpinion extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteSubmitLeaderOpinion.class );

	protected ActionResult<WrapOutId> execute( HttpServletRequest request,EffectivePerson effectivePerson, OkrWorkReportBaseInfo okrWorkReportBaseInfo, String opinion ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		Boolean check = true;
		OkrUserCache  okrUserCache  = null;
		String report_progress = "CLOSE";
		
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
			//logger.error( e, effectivePerson, request, null);
		}
		
		if ( check ) {
			if( opinion == null || opinion.isEmpty() ){
				opinion = "已阅。";
			}
		}
		
		if( check ){
			try {
				okrWorkReportFlowService.leaderProcess( okrWorkReportBaseInfo, opinion, okrUserCache.getLoginIdentityName() );
				result.setData( new WrapOutId( okrWorkReportBaseInfo.getId() ) );
			} catch (Exception e) {
				check = false;
				Exception exception = new LeaderOpinionSubmitException( e, okrWorkReportBaseInfo.getId() );
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
				okrWorkBaseInfoService.analyseWorkProgress( okrWorkReportBaseInfo.getWorkId(), report_progress, dateOperation.getNowDateTime() );
			} catch (Exception e ) {
				logger.warn( "system analyse work progres got an exceptin.", e );
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
}