package com.x.okr.assemble.control.jaxrs.login;

import java.io.Serializable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.login.exception.ExceptionGetOkrUserCache;
import com.x.okr.assemble.control.jaxrs.login.exception.ExceptionUserHasNoProxyIdentity;
import com.x.okr.assemble.control.jaxrs.login.exception.ExceptionUserIdentityQuery;
import com.x.okr.assemble.control.jaxrs.login.exception.ExceptionUserProxyQuery;

public class ActionLogin extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionLogin.class );
	
	protected ActionResult<OkrUserCache> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<OkrUserCache> result = new ActionResult<>();
		List<String> ids = null;
		String identity = null;
		OkrUserCache okrUserCache = null;
		boolean hasIdentity = false;
		Wi wrapIn = null;
		Boolean check = true;

		if (check) {
			try {//获取传入的参数，主要是loginIdentity(登录身份)
				wrapIn = this.convertToWrapIn( jsonElement, Wi.class);
			} catch (Exception e) {
				wrapIn = new Wi();
			}
		}
		
		if (wrapIn != null) {
			if ( StringUtils.isNotEmpty(wrapIn.getLoginIdentity()) ) {
				try { //查询用户是否拥有传入的身份名称
					hasIdentity = okrUserManagerService.hasIdentity( effectivePerson.getDistinguishedName(), wrapIn.getLoginIdentity() );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionUserProxyQuery(e, effectivePerson.getDistinguishedName(), wrapIn.getLoginIdentity() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
				if ( hasIdentity ) {//用户拥有传入的身份， 以指定的身份登录
					try {
						okrUserCache = setUserLoginIdentity( request, effectivePerson, wrapIn.getLoginIdentity() );
						result.setData(okrUserCache);
					} catch (Exception e) {
						Exception exception = new ExceptionGetOkrUserCache(e, effectivePerson.getDistinguishedName(), wrapIn.getLoginIdentity());
						result.error(exception);
						logger.error(e, effectivePerson, request, null);
					}
				} else {// 用户自己没有传入的身份，查询用户是否有该身份的代理配置（秘书）
					try {
						ids = okrConfigSecretaryService.listIdsByLeaderIdentity(effectivePerson.getDistinguishedName(), wrapIn.getLoginIdentity());
						if (ListTools.isEmpty(ids)) {//用户没有指定身份的代理权限，无法登录系统							
							//以用户自己的主要身份进行登录
							identity = okrUserManagerService.getIdentityWithPerson( effectivePerson.getDistinguishedName() );
							if( StringUtils.isNotEmpty( identity )) {
								System.out.println( "用户使用身份：" + identity + " 登录系统......" );
								try {
									okrUserCache = setUserLoginIdentity( request, effectivePerson, identity );
									result.setData(okrUserCache);
								} catch (Exception e) {
									check = false;
									Exception exception = new ExceptionGetOkrUserCache(e, effectivePerson.getDistinguishedName(), wrapIn.getLoginIdentity());
									result.error(exception);
									logger.error(e, effectivePerson, request, null);
								}
							}else {
								check = false;
								Exception exception = new ExceptionUserHasNoProxyIdentity( effectivePerson.getDistinguishedName(), wrapIn.getLoginIdentity());
								result.error(exception);
							}							
						} else {// 顺利查询到身份代理配置信息，向cache里新增用户相关信息
							okrUserCache = setUserLoginIdentity(request, effectivePerson, wrapIn.getLoginIdentity());
							result.setData(okrUserCache);
						}
					} catch (Exception e) {
						check = false;
						Exception exception = new ExceptionUserProxyQuery(e, effectivePerson.getDistinguishedName(), wrapIn.getLoginIdentity());
						result.error(exception);
						logger.error(e, effectivePerson, request, null);
					}
				}
			} else { // 没有传入身份，即以自己的第一个身份登入系统
				if (!"xadmin".equals( effectivePerson.getDistinguishedName().toLowerCase()) ) {//查询是不是xadmin用户，如果不是xadmin，则查询自己的第一个身份
					try {
						identity = okrUserManagerService.getIdentityWithPerson( effectivePerson.getDistinguishedName() );
						if (identity != null && !identity.isEmpty()) {
							System.out.println( "用户使用身份：" + identity + " 登录系统......" );
							okrUserCache = setUserLoginIdentity( request, effectivePerson, identity );
						}
					} catch (Exception e) {
						check = false;
						Exception exception = new ExceptionUserIdentityQuery(e, effectivePerson.getDistinguishedName());
						result.error(exception);
						logger.error(e, effectivePerson, request, null);
					}
				} else {
					try {
						okrUserCache = setUserXadmin(request, effectivePerson);
					} catch (Exception e) {
						Exception exception = new ExceptionGetOkrUserCache(e, effectivePerson.getDistinguishedName(), wrapIn.getLoginIdentity());
						result.error(exception);
						logger.error(e, effectivePerson, request, null);
					}
				}
				result.setData( okrUserCache );
			}
			
			if( check &&  okrUserCache!= null ) {//判断是否拥有工作管理权限
				if( effectivePerson.isManager() ) {
					okrUserCache.setOkrManager( true );
				}
				if(okrUserManagerService.isHasPlatformRole(okrUserCache.getLoginUserName(), "OKRManager")) {
					okrUserCache.setOkrManager( true );
				}
				if(okrUserManagerService.isOkrWorkManager(okrUserCache.getLoginIdentityName())) {
					okrUserCache.setOkrManager( true );
				}
			}
		} else {
			check = false;
			result.error(new Exception("请求传入的参数'wrapIn'为空，无法继续登录应用!"));
		}
		
		return result;
	}
	
	public static class Wi extends GsonPropertyObject implements Serializable{
		private static final long serialVersionUID = -5076990764713538973L;
		
		@FieldDescribe( "登录身份名称" )
		private String loginIdentity = null;
		public String getLoginIdentity() {
			return loginIdentity;
		}
		public void setLoginIdentity(String loginIdentity) {
			this.loginIdentity = loginIdentity;
		}
	}

}