package com.x.cms.assemble.control.jaxrs.categoryinfo;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.jaxrs.appinfo.BaseAction;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;

/**
 * 修改分类权限
 * @author sword
 */
public class ActionEditPermission extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionEditPermission.class);

	protected ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement ) throws Exception {
		logger.debug(effectivePerson.getDistinguishedName());
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			CategoryInfo categoryInfo = emc.find(id, CategoryInfo.class);
			if(categoryInfo == null){
				throw new ExceptionEntityNotExist(id, CategoryInfo.class);
			}
			AppInfo appInfo = emc.find(categoryInfo.getAppId(), AppInfo.class);
			if(appInfo == null){
				throw new ExceptionEntityNotExist(id, AppInfo.class);
			}
			if (!effectivePerson.isSecurityManager() && !business.isAppInfoManager(effectivePerson, appInfo)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			emc.beginTransaction(CategoryInfo.class);
			Wi.copier.copy(wi, categoryInfo);
			emc.check(categoryInfo, CheckPersistType.all);
			emc.commit();
			new LogService().log(null, effectivePerson.getDistinguishedName(), categoryInfo.getAppName() + "-" + categoryInfo.getCategoryName(), appInfo.getId(), categoryInfo.getId(), "", "", "CATEGORY", "修改权限");
		}
		CacheManager.notify(AppInfo.class);

		return result;
	}

	public static class Wi extends CategoryInfo {

		private static final long serialVersionUID = 5130476909053067565L;
		public static WrapCopier<Wi, CategoryInfo> copier = WrapCopierFactory.wi( Wi.class, CategoryInfo.class,
				ListTools.toList(CategoryInfo.viewablePersonList_FIELDNAME, CategoryInfo.viewableUnitList_FIELDNAME,
						CategoryInfo.viewableGroupList_FIELDNAME, CategoryInfo.publishablePersonList_FIELDNAME,
						CategoryInfo.publishableUnitList_FIELDNAME, CategoryInfo.publishableGroupList_FIELDNAME,
						CategoryInfo.manageablePersonList_FIELDNAME, CategoryInfo.manageableUnitList_FIELDNAME,
						CategoryInfo.manageableGroupList_FIELDNAME),
				null );
	}

	public static class Wo extends WoId {

	}

}
