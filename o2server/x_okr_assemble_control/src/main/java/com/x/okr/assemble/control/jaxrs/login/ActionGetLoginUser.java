package com.x.okr.assemble.control.jaxrs.login;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;

public class ActionGetLoginUser extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionGetLoginUser.class );
	
	protected ActionResult<OkrUserCache> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<OkrUserCache> result = new ActionResult<>();
		OkrUserCache okrUserCache = null;
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName(effectivePerson.getDistinguishedName());
			result.setData( okrUserCache );
		} catch (Exception e) {
			logger.warn( "获取登入用户信息时发生异常。" );
			logger.error( e, effectivePerson, request, null);
		}
		return result;
	}
}