package com.x.bbs.assemble.control.jaxrs.login;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.bbs.assemble.control.service.BBSUserInfoService;
import com.x.bbs.assemble.control.service.bean.RoleAndPermission;

/**
 * 手机用户访问论坛信息，首页所有的信息整合在一起 匿名用户可以访问
 * 
 * @param request
 * @return
 */
public class ActionLogout extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionLogout.class);
	
	public ActionResult<RoleAndPermission> execute( @Context HttpServletRequest request, EffectivePerson effectivePerson ) {
		ActionResult<RoleAndPermission> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		try {
			new BBSUserInfoService().logout(currentPerson.getDistinguishedName());
		} catch (Exception e) {
			logger.warn("system logout got an exception");
			logger.error(e);
		}
		return result;
	}

}
