package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionGetOkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionUserNoLogin;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionWorkReportDispatchOver;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionWorkReportIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionWorkReportNotExists;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionWorkReportQueryById;
import com.x.okr.assemble.control.jaxrs.queue.WrapInWorkDynamic;
import com.x.okr.entity.OkrWorkReportBaseInfo;

/**
 * 将汇报信息调度到结束
 * 1、汇报信息的信息状态修改为“结束”，详细信息里状态修改为“结束”
 * 2、汇报信息的当前处理环节“结束”
 * 3、汇报信息待办信息
 * 4、汇报信息待办汇总信息
 * 5、汇报信息处理记录里添加系统处理记录
 * 6、PERSONLINK记录里的处理状态修改为“结束”
 * @author liyi_
 *
 */
public class ActionDispatchToOver extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionDispatchToOver.class );

	protected ActionResult<Wo> execute( HttpServletRequest request,EffectivePerson effectivePerson, String reportId ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Boolean check = true;
		OkrWorkReportBaseInfo okrWorkReportBaseInfo = null;
		OkrUserCache  okrUserCache  = null;
		
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getDistinguishedName() );
			} catch ( Exception e ) {
				check = false;
				Exception exception = new ExceptionGetOkrUserCache( e, effectivePerson.getDistinguishedName()  );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			Exception exception = new ExceptionUserNoLogin( effectivePerson.getDistinguishedName()  );
			result.error( exception );
		}

		if (check) {
			if (reportId == null || reportId.isEmpty()) {
				check = false;
				Exception exception = new ExceptionWorkReportIdEmpty();
				result.error(exception);
			}
		}
		
		if (check) {
			try {
				okrWorkReportBaseInfo = okrWorkReportQueryService.get( reportId );
				if( okrWorkReportBaseInfo == null ) {
					check = false;
					Exception exception = new ExceptionWorkReportNotExists( reportId );
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionWorkReportQueryById(e, reportId );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			try{
				okrWorkReportFlowService.dispatchToOver( reportId );
				result.setData(new Wo( reportId ));
			}catch(Exception e){
				Exception exception = new ExceptionWorkReportDispatchOver( e, reportId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if ( okrWorkReportBaseInfo != null) {
			WrapInWorkDynamic.sendWithWorkReport( okrWorkReportBaseInfo, 
					effectivePerson.getDistinguishedName(), 
					okrUserCache.getLoginUserName(), 
					okrUserCache.getLoginIdentityName() , 
					"调度工作汇报信息", 
					"工作汇报信息调度成功！"
			);
		}
		return result;
	}
}