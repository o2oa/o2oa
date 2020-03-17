package com.x.cms.assemble.control.jaxrs.appinfo;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.AppInfoConfig;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

public class ActionSaveConfig extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSaveConfig.class);

	@AuditLog(operation = "保存栏目配置支持信息")
	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String appId, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();

		AppInfoConfig appInfoConfig = null;
		String config = jsonElement.toString();
		Boolean check = true;
		
		if (check) {
			if ( StringUtils.isEmpty( appId ) ) {
				check = false;
				Exception exception = new ExceptionAppInfoIdEmpty();
				result.error(exception);
			}
		}

		if (check) {
			try {

				appInfoConfig = appInfoServiceAdv.saveConfig( appId, config, effectivePerson );

				Wo wo = new Wo();
				wo.setId( appInfoConfig.getId() );
				result.setData( wo );
				
				// 更新缓存
				ApplicationCache.notify( AppInfo.class );

			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAppInfoProcess(e, "应用栏目配置支持信息保存时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wo extends WoId {

	}
	
}