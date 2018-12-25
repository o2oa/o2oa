package com.x.okr.assemble.control.jaxrs.login;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.ThisApplication;
import com.x.okr.assemble.control.service.OkrConfigSecretaryService;
import com.x.okr.assemble.control.service.OkrUserInfoService;
import com.x.okr.assemble.control.service.OkrUserManagerService;
import com.x.okr.entity.OkrUserInfo;

import net.sf.ehcache.Ehcache;

public class BaseAction extends StandardJaxrsAction {
	
	private static  Logger logger = LoggerFactory.getLogger( BaseAction.class );	
	protected OkrConfigSecretaryService okrConfigSecretaryService = new OkrConfigSecretaryService();	
	protected OkrUserManagerService okrUserManagerService = new OkrUserManagerService();	
	protected OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
	protected Ehcache cache = ApplicationCache.instance().getCache( OkrUserInfo.class);
	
	/**
	 * 设置用户登录的代理身份，记录到数据库中并且清空相应的缓存对象
	 * 
	 * @param name
	 * @throws Exception
	 */
	public OkrUserCache setUserLoginIdentity(HttpServletRequest request, EffectivePerson currentPerson, String identity)throws Exception {
		Gson gson = XGsonBuilder.instance();
		String customContent = null;
		Boolean isOkrSystemAdmin = false;
		OkrUserCache okrUserCache = new OkrUserCache();
		okrUserCache.setLoginIdentityName(identity);
		
		// 查询name的第一个身份所属的组织和顶层组织
		okrUserCache.setOperationUserName( currentPerson.getDistinguishedName() );
		okrUserCache.setOperationUserUnitName( okrUserManagerService.getUnitNameWithPerson(currentPerson.getDistinguishedName()));
		okrUserCache.setOperationUserTopUnitName( okrUserManagerService.getTopUnitNameWithPerson(currentPerson.getDistinguishedName()));
		
		// 查询identity所属的组织和顶层组织
		String personName = okrUserManagerService.getPersonNameByIdentity( identity );
		okrUserCache.setLoginUserName( personName );
		okrUserCache.setLoginUserUnitName( okrUserManagerService.getUnitNameByIdentity(identity) );
		okrUserCache.setLoginUserTopUnitName( okrUserManagerService.getTopUnitNameByIdentity(identity) );
		try {
			//查询用户是否OKR系统管理员
			if( StringUtils.isNotEmpty( okrUserCache.getLoginUserName() )) {
				isOkrSystemAdmin = okrUserManagerService.isHasPlatformRole( okrUserCache.getLoginUserName(), ThisApplication.OKRMANAGER );
			}else {
				isOkrSystemAdmin = okrUserManagerService.isHasPlatformRole( currentPerson.getDistinguishedName(), ThisApplication.OKRMANAGER );
			}
		} catch (Exception e) {
			logger.error(e, currentPerson, request, null);
		}
		okrUserCache.setOkrManager( isOkrSystemAdmin );

		//将数据存储到数据库里备用
		customContent = gson.toJson( okrUserCache, OkrUserCache.class );
		
		OkrUserInfo userInfo = new OkrUserInfo();
		userInfo.setUserName( currentPerson.getDistinguishedName() );
		userInfo.setCustomContent( customContent );
		okrUserInfoService.save( userInfo );
		
		String cacheKey = ThisApplication.getOkrUserInfoCacheKey( currentPerson.getDistinguishedName() );
		ApplicationCache.notify( OkrUserInfo.class, cacheKey );
		return okrUserCache;
	}

	/**
	 * 设置用户登录的代理身份
	 * 
	 * @param name
	 * @throws Exception
	 */
	public OkrUserCache setUserXadmin(HttpServletRequest request, EffectivePerson currentPerson) throws Exception {
		Gson gson = XGsonBuilder.instance();
		String customContent = null;
		OkrUserCache okrUserCache = new OkrUserCache();
		okrUserCache.setLoginIdentityName("xadmin");
		// 查询name的第一个身份所属的组织和顶层组织
		okrUserCache.setOperationUserName("xadmin");
		okrUserCache.setOperationUserUnitName("xadmin");
		okrUserCache.setOperationUserTopUnitName("xadmin");
		okrUserCache.setLoginUserName("xadmin");
		okrUserCache.setLoginUserUnitName("xadmin");
		okrUserCache.setLoginUserTopUnitName("xadmin");
		okrUserCache.setOkrManager(true);

		customContent = gson.toJson(okrUserCache, OkrUserCache.class);
		OkrUserInfo userInfo = new OkrUserInfo();
		userInfo.setUserName("xadmin");
		userInfo.setCustomContent( customContent );
		okrUserInfoService.save(userInfo);
		
		String cacheKey = ThisApplication.getOkrUserInfoCacheKey( currentPerson.getDistinguishedName() );
		ApplicationCache.notify( OkrUserInfo.class, cacheKey );
		
		return okrUserCache;
	}
}
