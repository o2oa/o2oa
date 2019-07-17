package com.x.teamwork.assemble.control.jaxrs.extfield;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.teamwork.core.entity.ProjectExtFieldRele;

public class ActionDelete extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionDelete.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String flag) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		ProjectExtFieldRele projectExtFieldRele = null;
		Boolean check = true;

		if ( StringUtils.isEmpty( flag ) ) {
			check = false;
			Exception exception = new ProjectExtFieldReleFlagForQueryEmptyException();
			result.error( exception );
		}

		if (check) {
			try {
				projectExtFieldRele = projectExtFieldReleQueryService.get(flag);
				if ( projectExtFieldRele == null) {
					check = false;
					Exception exception = new ProjectExtFieldReleNotExistsException(flag);
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ProjectExtFieldReleQueryException(e, "根据指定flag查询项目扩展属性关联信息对象时发生异常。flag:" + flag);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			try {
				projectExtFieldRelePersistService.delete(flag, effectivePerson );
				
				// 更新缓存
				ApplicationCache.notify( ProjectExtFieldRele.class );
				
				Wo wo = new Wo();
				wo.setId( projectExtFieldRele.getId() );
				result.setData( wo );
			} catch (Exception e) {
				check = false;
				Exception exception = new ProjectExtFieldReleQueryException(e, "根据指定flag删除项目扩展属性关联信息对象时发生异常。flag:" + flag);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {					
				dynamicPersistService.projectExtFieldReleDeleteDynamic( projectExtFieldRele, effectivePerson);
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}	
		}
		return result;
	}

	public static class Wo extends WoId {
	}
}