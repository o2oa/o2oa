package com.x.cms.assemble.control.jaxrs.appinfo;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.core.entity.AppInfo;

public class ActionUpdateReviewForce extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionUpdateReviewForce.class );
	
	protected ActionResult<WoId> execute( HttpServletRequest request, EffectivePerson effectivePerson, String flag ) throws Exception {
		ActionResult<WoId> result = new ActionResult<>();
		AppInfo appInfo = null;
		Boolean check = true;
		
		if( StringUtils.isEmpty(flag) ){
			check = false;
			Exception exception = new ExceptionAppInfoIdEmpty();
			result.error( exception );
		}
		
		if( check ){
			try {
				appInfo = appInfoServiceAdv.getWithFlag( flag );
				if( appInfo == null ){
					check = false;
					Exception exception = new ExceptionAppInfoNotExists( flag );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAppInfoProcess( e, "根据指定flag查询应用栏目信息对象时发生异常。flag:" + flag );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				permissionOperateService.refreshReviewWithAppId( appInfo.getId() );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAppInfoProcess( e, "根据指定强制更新信息存根Review信息时发生异常。ID:" + appInfo.getId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				WoId wo = new WoId();
				wo.setId( appInfo.getId() );
				result.setData( wo );
			} catch (Exception e) {
				Exception exception = new ExceptionAppInfoProcess( e, "将查询出来的应用栏目信息对象转换为可输出的数据信息时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return result;
	}
}