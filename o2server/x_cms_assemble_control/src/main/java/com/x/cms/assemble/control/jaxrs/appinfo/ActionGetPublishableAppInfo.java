package com.x.cms.assemble.control.jaxrs.appinfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

public class ActionGetPublishableAppInfo extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionGetPublishableAppInfo.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String appId ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		List<Wo> wos_out = new ArrayList<>();
		Boolean isXAdmin = false;
		Boolean check = true;
		Boolean isAnonymous = effectivePerson.isAnonymous();
		String personName = effectivePerson.getDistinguishedName();

		try {
			isXAdmin = userManagerService.isManager( effectivePerson );
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionAppInfoProcess(e, "系统在检查用户是否是平台管理员时发生异常。Name:" + personName);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), personName, appId, isXAdmin );
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);

		if (optional.isPresent()) {
			result.setData((Wo)optional.get());
		} else {
			if (check) {
				if ( isXAdmin ) { // 如果用户管理系统管理，则获取所有的栏目和分类信息
					try {
						wos = listPublishAbleAppInfoByPermission( personName, isAnonymous, null, "all", "全部", isXAdmin, 1000 );
					} catch (Exception e) {
						check = false;
						Exception exception = new ExceptionAppInfoProcess(e,
								"系统在根据用户权限查询所有可见的分类信息时发生异常。Name:" + personName);
						result.error(exception);
						logger.error(e, effectivePerson, request, null);
					}
				} else {
					try {
						wos_out = listPublishAbleAppInfoByPermission( personName, isAnonymous, null,  "all", "全部", isXAdmin, 1000 );
						for( Wo wo : wos_out ) {
							if( ListTools.isNotEmpty( wo.getWrapOutCategoryList() )) {
								wos.add( wo );
							}
						}
					} catch (Exception e) {
						check = false;
						Exception exception = new ExceptionAppInfoProcess(e,
								"系统在根据用户权限查询所有可见的分类信息时发生异常。Name:" + personName);
						result.error(exception);
						logger.error(e, effectivePerson, request, null);
					}
				}
			}
			if (check) {
				if(ListTools.isNotEmpty( wos)) {
					for( Wo wo : wos ) {
						if( wo.getId().equalsIgnoreCase( appId )) {
							CacheManager.put(cacheCategory, cacheKey, wo);
							result.setData( wo );
							break;
						}
					}
				}
			}
		}
		return result;
	}

}