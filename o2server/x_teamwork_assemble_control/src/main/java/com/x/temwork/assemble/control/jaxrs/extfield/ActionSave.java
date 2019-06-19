package com.x.temwork.assemble.control.jaxrs.extfield;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.teamwork.core.entity.ProjectExtFieldRele;

public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSave.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		ProjectExtFieldRele projectExtFieldRele = null;
		Wi wi = null;
		Boolean check = true;
		String optType = "CREATE";

		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e) {
			check = false;
			Exception exception = new ProjectExtFieldRelePersistException(e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		
		if (check) {
			projectExtFieldRele = projectExtFieldReleQueryService.get( wi.getId() );
			if( projectExtFieldRele == null ) {
				optType = "CREATE";
			}else {
				optType = "UPDATE";
			}
		}
		
		if (check) {
			try {					
				projectExtFieldRele = projectExtFieldRelePersistService.save( wi, effectivePerson );
				
				// 更新缓存
				ApplicationCache.notify( ProjectExtFieldRele.class );
				
				Wo wo = new Wo();
				wo.setId( projectExtFieldRele.getId() );
				result.setData( wo );
			} catch (Exception e) {
				check = false;
				Exception exception = new ProjectExtFieldRelePersistException(e, "项目扩展属性关联信息保存时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}			
		}
		if (check) {
			try {					
				dynamicPersistService.save( projectExtFieldRele, optType, effectivePerson, jsonElement.toString() );
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}	

	public static class Wi extends ProjectExtFieldRele {
		private static final long serialVersionUID = -6314932919066148113L;		
		public static WrapCopier<Wi, ProjectExtFieldRele> copier = WrapCopierFactory.wi( Wi.class, ProjectExtFieldRele.class, null, null );
	}

	public static class Wo extends WoId {
	}
	
}