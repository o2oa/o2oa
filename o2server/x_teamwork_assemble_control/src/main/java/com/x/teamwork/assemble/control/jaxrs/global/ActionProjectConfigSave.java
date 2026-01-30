package com.x.teamwork.assemble.control.jaxrs.global;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.teamwork.core.entity.ProjectConfig;

public class ActionProjectConfigSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionProjectConfigSave.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		ProjectConfig projectConfig = null;
		Wi wi = null;
		Wo wo = new Wo();

		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e) {
			Exception exception = new PriorityPersistException(e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		
		
		try {					
			projectConfig = projectConfigPersistService.save( wi, effectivePerson );
			// 更新缓存
			CacheManager.notify( ProjectConfig.class );
			wo.setId( projectConfig.getId() );
		} catch (Exception e) {
			Exception exception = new PriorityPersistException(e, "项目配置信息保存时发生异常。");
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}			
		
		result.setData( wo );
		return result;
	}	

	public static class Wi extends ProjectConfig {
		private static final long serialVersionUID = -6314932919066148113L;
		
		public static WrapCopier<Wi, ProjectConfig> copier = WrapCopierFactory.wi( Wi.class, ProjectConfig.class, null, null );
		
	}

	public static class Wo extends WoId {
		
	}
	
}