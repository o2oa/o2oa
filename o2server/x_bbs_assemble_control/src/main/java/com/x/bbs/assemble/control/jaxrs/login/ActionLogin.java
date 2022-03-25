package com.x.bbs.assemble.control.jaxrs.login;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.bbs.assemble.control.ThisApplication;
import com.x.bbs.assemble.control.jaxrs.login.exception.ExceptionInsufficientPermissions;
import com.x.bbs.assemble.control.jaxrs.login.exception.ExceptionUserLogin;
import com.x.bbs.assemble.control.service.bean.RoleAndPermission;

/**
 * 手机用户访问论坛信息，首页所有的信息整合在一起 匿名用户可以访问
 *
 * @return
 */
public class ActionLogin extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionLogin.class);

	public ActionResult<RoleAndPermission> execute(@Context HttpServletRequest request,
			EffectivePerson effectivePerson) {

		ActionResult<RoleAndPermission> result = new ActionResult<>();
		Boolean isBBSManager = false;
		String hostIp = request.getRemoteAddr();
		String hostName = request.getRemoteAddr();
		Boolean check = true;

		if (check) {
			if ("anonymous".equalsIgnoreCase(effectivePerson.getTokenType().name())) {
				if(StringUtils.equalsAnyIgnoreCase( ThisApplication.CONFIG_BBS_ANONYMOUS_PERMISSION, "YES")){
					try {
						operationRecordService.loginOperation("anonymous", hostIp, hostName);
						result.setData(new RoleAndPermission());
					} catch (Exception e) {
						Exception exception = new ExceptionUserLogin(e, "anonymous");
						result.error(exception);
						logger.error(e, effectivePerson, request, null);
					}
				}else{
					Exception exception = new ExceptionUserLogin("系统不允许匿名访问社区资源。");
					result.error(exception);
				}
			} else {
				RoleAndPermission roleAndPermission = null;
				try {
					operationRecordService.loginOperation(effectivePerson.getDistinguishedName(), hostIp, hostName);
					roleAndPermission = userPermissionService
							.getUserRoleAndPermissionForLogin(effectivePerson.getDistinguishedName());
				} catch (Exception e) {
					Exception exception = new ExceptionUserLogin(e, effectivePerson.getDistinguishedName());
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
				try {
					isBBSManager = userManagerService.isHasPlatformRole(effectivePerson.getDistinguishedName(),
							ThisApplication.BBSMANAGER);
				} catch (Exception e) {
					Exception exception = new ExceptionInsufficientPermissions(effectivePerson.getDistinguishedName(),
							ThisApplication.BBSMANAGER);
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
				if (roleAndPermission != null) {
					roleAndPermission.setIsBBSManager(isBBSManager);
				}
				result.setData(roleAndPermission);
			}
		}
		return result;
	}

//	public static class Wi extends GsonPropertyObject implements Serializable {
//
//		private static final long serialVersionUID = -5076990764713538973L;
//
//	}
}
