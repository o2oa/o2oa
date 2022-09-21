package com.x.cms.assemble.control.jaxrs.categoryinfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.cms.assemble.control.Business;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.service.CmsBatchOperationPersistService;
import com.x.cms.assemble.control.service.CmsBatchOperationProcessService;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.element.ViewCategory;

/**
 * 删除分类
 * @author sword
 */
public class ActionDelete extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionDelete.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, String id, EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();

		CategoryInfo categoryInfo = categoryInfoServiceAdv.get(id);
		if (categoryInfo == null) {
			throw new ExceptionEntityNotExist(id);
		}
		AppInfo appInfo = appInfoServiceAdv.get(categoryInfo.getAppId());
		Business business = new Business(null);
		if (!business.isAppInfoManager(effectivePerson, appInfo)) {
			throw new ExceptionAccessDenied(effectivePerson);
		}

		Long count = documentServiceAdv.countByCategoryId( id,false );
		if ( count > 0 ) {
			throw new ExceptionEditNotAllowed(count);
		}
		count = documentServiceAdv.countByCategoryId( id,true );
		if ( count > 0 ) {
			new ActionEraseDocumentWithCategory().execute(request, id, effectivePerson);
		}

		categoryInfoServiceAdv.delete( id, effectivePerson );

		Wo wo = new Wo();
		wo.setId( categoryInfo.getId() );
		result.setData( wo );

		//增加删除栏目批量操作（对分类和文档）的信息
		new CmsBatchOperationPersistService().addOperation(
				CmsBatchOperationProcessService.OPT_OBJ_CATEGORY,
				CmsBatchOperationProcessService.OPT_TYPE_DELETE, id, id, "删除分类：ID=" + id );

		new LogService().log(null, effectivePerson.getDistinguishedName(), categoryInfo.getAppName() + "-" + categoryInfo.getCategoryName(), id, "", "", "", "CATEGORY", "删除");

		CacheManager.notify( AppInfo.class );
		CacheManager.notify( CategoryInfo.class );
		CacheManager.notify( ViewCategory.class );
		return result;
	}

	public static class Wo extends WoId {
	}
}
