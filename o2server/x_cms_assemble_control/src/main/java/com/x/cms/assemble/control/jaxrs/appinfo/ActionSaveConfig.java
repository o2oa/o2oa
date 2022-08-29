package com.x.cms.assemble.control.jaxrs.appinfo;

import com.google.gson.JsonElement;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.AppInfoConfig;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 保存栏目配置支持信息
 * @author sword
 */
public class ActionSaveConfig extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSaveConfig.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String appId, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();

		String config = jsonElement.toString();

		AppInfo appInfo = appInfoServiceAdv.get( appId );
		if( appInfo == null ){
			throw new ExceptionAppInfoNotExists( appId );
		}

		Business business = new Business(null);
		if (!business.isAppInfoManager(effectivePerson, appInfo)) {
			throw new ExceptionAccessDenied(effectivePerson);
		}

		AppInfoConfig appInfoConfig = appInfoServiceAdv.saveConfig( appId, config, effectivePerson );

		Wo wo = new Wo();
		wo.setId( appInfoConfig.getId() );
		result.setData( wo );

		// 更新缓存
		CacheManager.notify( AppInfo.class );
		return result;
	}

	public static class Wo extends WoId {

	}

}
